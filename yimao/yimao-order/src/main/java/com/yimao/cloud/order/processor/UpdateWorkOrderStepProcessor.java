package com.yimao.cloud.order.processor;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.WorkOrderInstallStep;
import com.yimao.cloud.order.mapper.WorkOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 老流程补库存后，设置工单step
 */
@Component
@Slf4j
public class UpdateWorkOrderStepProcessor {

    @Resource
    private WorkOrderMapper workOrderMapper;

    @RabbitListener(queues = RabbitConstant.RESET_WORK_ORDER_STEP)
    @RabbitHandler
    public void process(Map<String, Object> map) {
        try {
            log.info("老流程补库存后，设置工单step，补库存的产品，{}", JSONObject.toJSONString(map));
            String province = (String) map.get("province");
            String city = (String) map.get("city");
            String region = (String) map.get("region");
            Integer productId = (Integer) map.get("productId");
            workOrderMapper.updateOutStockStepToStartStep(province, city, region, productId, WorkOrderInstallStep.OUTSTOCK.value, WorkOrderInstallStep.START.value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
