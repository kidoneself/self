package com.yimao.cloud.framework.aop.parser;

import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;
import org.springframework.util.Assert;
import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

/**
 * 基础解析类
 * 单表编辑时可以直接使用id来查询
 * 如果为多表复杂逻辑，请自行编写具体实现类
 *
 * @author Zhang Bo
 */
public class DefaultContentParse implements ContentParser {

    @Override
    public Object getResult(Map<String, Object> feildValues, EnableOperationLog enableOperateLog) {
        Assert.isTrue(feildValues.containsKey("id"), "未解析到id值，请检查前台传递参数是否正确");
        Object result = feildValues.get("id");

        Mapper mapper;
        Class<?> cls = enableOperateLog.daoClass();
        mapper = (Mapper) SpringContextHolder.getBean(cls);

        Object object = null;
        if (result instanceof String) {
            Long id = Long.parseLong((String) result);
            object = mapper.selectByPrimaryKey(id);
        } else if (result instanceof Long) {
            Long id = (Long) result;
            object = mapper.selectByPrimaryKey(id);
        } else if (result instanceof Integer) {
            Integer id = (Integer) result;
            object = mapper.selectByPrimaryKey(id);
        }
        return object;
    }

}
