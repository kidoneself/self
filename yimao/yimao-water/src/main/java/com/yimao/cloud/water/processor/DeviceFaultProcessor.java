package com.yimao.cloud.water.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.DeviceFaultState;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFaultDTO;
import com.yimao.cloud.water.service.WaterDeviceFaultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 描述：设备故障监听队列
 *
 * @Author Zhang Bo
 * @Date 2019/11/12
 */
@Component
@Slf4j
public class DeviceFaultProcessor {

    @Resource
    private WaterDeviceFaultService waterDeviceFaultService;

    /**
     * 设备故障新增或解除
     */
    @RabbitListener(queues = RabbitConstant.DEVICE_FAULT)
    @RabbitHandler
    public void processor(WaterDeviceFaultDTO faultDTO) {
        try {
            Integer deviceId = faultDTO.getDeviceId();
            String sn = faultDTO.getSn();
            Integer type = faultDTO.getType();
            String filterType = faultDTO.getFilterType();
            String fault = faultDTO.getFault();
            if (faultDTO.getState() == DeviceFaultState.RESOLVE.value) {
                //解除故障
                waterDeviceFaultService.resolve(deviceId, sn, type, filterType);
            } else {
                //新增或更新故障
                waterDeviceFaultService.saveOrUpdate(deviceId, sn, type, filterType, fault);
            }
        } catch (Exception e) {
            log.error("设备故障队列处理发生错误，" + e.getMessage(), e);
        }
    }

}
