package com.yimao.cloud.framework.aop;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.context.BaseContextHandler;
import com.yimao.cloud.base.utils.IpUtil;
import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;
import com.yimao.cloud.framework.aop.dto.OperationLogDTO;
import com.yimao.cloud.framework.aop.parser.ContentParser;
import com.yimao.cloud.framework.aop.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class EnableOperationLogAspect {

    private static final String FIELD_VALUES = "fieldValues";
    private static final String OLD_OBJECT = "oldObject";
    private static final String OPERATE_OBJ = "operateLogDTO";

    @Resource
    private AmqpTemplate rabbitTemplate;
    @Resource
    private UserCache userCache;

    @Before("@annotation(enableOperateLog)")
    public void doBefore(JoinPoint joinPoint, EnableOperationLog enableOperateLog) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes == null ? null : attributes.getRequest();

        //获取切面方法实体参数对象
        int index = enableOperateLog.index();
        Object object = joinPoint.getArgs()[index];

        OperationLogDTO operateLogDTO = new OperationLogDTO();
        operateLogDTO.setOperator(userCache.getCurrentAdminRealName());
        operateLogDTO.setOperationIp(IpUtil.getIp(request));
        operateLogDTO.setOperationDate(new Date());
        String handelName = enableOperateLog.name();
        if ("".equals(handelName)) {
            operateLogDTO.setOperationPage(request == null ? null : request.getRequestURL().toString());
        } else {
            operateLogDTO.setOperationPage(handelName);
        }
        operateLogDTO.setOperationType(enableOperateLog.type().name);
        operateLogDTO.setDescription("");

        Map<String, Object> fieldValues = new HashMap<>();
        String[] feilds = enableOperateLog.fields();
        for (String field : feilds) {
            Object result = ReflectionUtils.getFieldValue(object, field);
            fieldValues.put(field, result);
        }
        operateLogDTO.setOperationObject(JSON.toJSONString(fieldValues));

        OperationType opeType = enableOperateLog.type();
        if (Objects.equals(OperationType.UPDATE, opeType)) {
            BaseContextHandler.set(FIELD_VALUES, fieldValues);
            try {
                ContentParser contentParser = (ContentParser) enableOperateLog.parseClass().newInstance();
                Object oldObject = contentParser.getResult(fieldValues, enableOperateLog);
                BaseContextHandler.set(OLD_OBJECT, oldObject);
                BaseContextHandler.set(OPERATE_OBJ, operateLogDTO);
            } catch (Exception e) {
                log.error("service加载失败:", e);
            }
        } else if (Objects.equals(OperationType.SAVE, opeType) || Objects.equals(OperationType.DELETE, opeType)) {
            BaseContextHandler.set(OPERATE_OBJ, operateLogDTO);
        }
    }

    @SuppressWarnings("unchecked")
    @AfterReturning(pointcut = "@annotation(enableOperateLog)", returning = "object")
    public void doAfterReturing(Object object, EnableOperationLog enableOperateLog) {
        OperationLogDTO operateLogDTO = (OperationLogDTO) BaseContextHandler.get(OPERATE_OBJ);
        OperationType opeType = enableOperateLog.type();
        //队列名
        String queue = enableOperateLog.queue();
        if (Objects.equals(OperationType.UPDATE, opeType) || Objects.equals(OperationType.DELETE, opeType)) {
            ContentParser contentParser;
            Object newObject = null;

            Object oldObject = BaseContextHandler.get(OLD_OBJECT);
            Map<String, Object> fieldValues = (Map<String, Object>) BaseContextHandler.get(FIELD_VALUES);
            try {
                contentParser = (ContentParser) enableOperateLog.parseClass().newInstance();
                newObject = contentParser.getResult(fieldValues, enableOperateLog);
            } catch (Exception e) {
                log.error("service加载失败:", e);
            }
            try {
                List<Map<String, Object>> changelist = ReflectionUtils.compareTwoClass(oldObject, newObject);
                if (changelist != null && changelist.size() > 0) {
                    int size = changelist.size();
                    StringBuilder str = new StringBuilder();
                    for (int i = 0; i < size; i++) {
                        Map<String, Object> map = changelist.get(i);
                        if (i == size - 1) {
                            str.append("【" + map.get("name") + "】从【" + map.get("old") + "】改为了【" + map.get("new") + "】;");
                        } else {
                            str.append("【" + map.get("name") + "】从【" + map.get("old") + "】改为了【" + map.get("new") + "】;\n");
                        }
                    }
                    operateLogDTO.setDescription(str.toString());
                    rabbitTemplate.convertAndSend(queue, operateLogDTO);
                }
            } catch (Exception e) {
                log.error("比较异常", e);
            }
        } else if (Objects.equals(OperationType.SAVE, opeType) || Objects.equals(OperationType.DELETE, opeType)) {
            // BaseContextHandler.set(OPERATE_OBJ, operateLogDTO);
            rabbitTemplate.convertAndSend(queue, operateLogDTO);
        }
    }

}
