package com.yimao.cloud.water.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.water.mapper.WaterDeviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 描述：客户信息修改时同步修改设备上的客户信息
 *
 * @Author Zhang Bo
 * @Date 2019/11/22
 */
@Component
@Slf4j
public class ChangeDeviceUserInfoProcessor {

    @Resource
    private WaterDeviceMapper waterDeviceMapper;

    /**
     * 客户信息修改时同步修改设备上的客户信息
     */
    @RabbitListener(queues = RabbitConstant.CHANGE_DEVICE_USER)
    @RabbitHandler
    public void processor(Map<String, Object> map) {
        try {
            Integer deviceUserId = (Integer) map.get("deviceUserId");
            String deviceUserName = (String) map.get("deviceUserName");
            String deviceUserPhone = (String) map.get("deviceUserPhone");
            waterDeviceMapper.updateDeviceUserInfo(deviceUserId, deviceUserName, deviceUserPhone);
        } catch (Exception e) {
            log.error("客户信息修改时同步修改设备上的客户信息发生错误，" + e.getMessage(), e);
        }
    }

}
