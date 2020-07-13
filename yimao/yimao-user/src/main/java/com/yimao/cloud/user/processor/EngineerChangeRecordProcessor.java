package com.yimao.cloud.user.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.aop.dto.OperationLogDTO;
import com.yimao.cloud.user.mapper.EngineerChangeRecordMapper;
import com.yimao.cloud.user.po.EngineerChangeRecord;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 安装工信息修改监听队列
 *
 * @author Zhang Bo
 * @date 2019/7/8
 */
@Component
public class EngineerChangeRecordProcessor {

    @Resource
    private EngineerChangeRecordMapper engineerChangeRecordMapper;

    @RabbitListener(queues = RabbitConstant.ENGINEER_CHANGE_RECORD)
    @RabbitHandler
    public void processor(OperationLogDTO dto) {
        if (dto != null) {
            Integer engineerId = null;
            String operationObject = dto.getOperationObject();
            if (StringUtil.isNotBlank(operationObject)) {
                JSONObject json = JSON.parseObject(operationObject);
                engineerId = json.getInteger("id");
            }
            EngineerChangeRecord record = new EngineerChangeRecord();
            record.setEngineerId(engineerId);
            record.setOperationType(dto.getOperationPage());
            record.setOperationObject(operationObject);
            record.setDescription(dto.getDescription());
            record.setOperator(dto.getOperator());
            record.setOperationDate(dto.getOperationDate());
            record.setOperationIp(dto.getOperationIp());
            engineerChangeRecordMapper.insert(record);
        }
    }

}
