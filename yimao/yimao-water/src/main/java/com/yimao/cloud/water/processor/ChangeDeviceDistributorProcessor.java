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
 * 描述：经销商转让修改设备上的经销商信息
 *
 * @Author Zhang Bo
 * @Date 2019/11/12
 */
@Component
@Slf4j
public class ChangeDeviceDistributorProcessor {

    @Resource
    private WaterDeviceMapper waterDeviceMapper;

    /**
     * 经销商转让修改设备上的经销商信息
     */
    @RabbitListener(queues = RabbitConstant.CHANGE_DEVICE_DISTRIBUTOR)
    @RabbitHandler
    public void processor(Map<String, Object> map) {
        try {
            Integer oldDistributorId = (Integer) map.get("oldDistributorId");
            Integer newDistributorId = (Integer) map.get("newDistributorId");
            String newDistributorName = (String) map.get("newDistributorName");
            String newDistributorPhone = (String) map.get("newDistributorPhone");
            waterDeviceMapper.updateForTransferDistributor(oldDistributorId, newDistributorId, newDistributorName, newDistributorPhone);
        } catch (Exception e) {
            log.error("经销商转让修改设备上的经销商信息发生错误，" + e.getMessage(), e);
        }
    }

}
