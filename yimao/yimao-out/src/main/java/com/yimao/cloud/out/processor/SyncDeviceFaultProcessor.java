package com.yimao.cloud.out.processor;

import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.constant.RabbitConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;


/***
 * 功能描述:同步水机设备故障记录
 *
 * @auther: liu yi
 * @date: 2019/5/6 14:06
 */
@Component
@Slf4j
public class SyncDeviceFaultProcessor {
    /***
     * 功能描述:同步水机设备故障记录
     *
     * @param:
     * @auther: liu yi
     * @date: 2019/5/6 16:58
     * @return: void
     */
    @RabbitListener(queues = RabbitConstant.SYNC_DEVICE_FAULT)
    @RabbitHandler
    public void processor(Map<String, Object> map) {
        if (null == map || map.isEmpty()) {
            return;
        }

        try {
            BaideApiUtil.repairWorkOrderInsertSystemTip(map);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
