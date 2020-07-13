package com.yimao.cloud.system.configuration;

import com.yimao.cloud.base.constant.RabbitConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 队列创建
 *
 * @author Zhang Bo
 * @date 2018/8/16
 */
@Configuration
public class RabbitConfiguration {

    @Bean
    public Queue operationLogQueue() {
        return new Queue(RabbitConstant.SYSTEM_OPERATION_LOG, true);
    }

    @Bean
    public Queue systemMessagePushQueue() {
        return new Queue(RabbitConstant.SYSTEM_MESSAGE_PUSH, true);
    }

    @Bean
    public Queue engineerAppMessagePushQueue() {
        return new Queue(RabbitConstant.ENGINEER_APP_MESSAGE_PUSH, true);
    }

    @Bean
    public Queue smsAppMessagePushQueue() {
        return new Queue(RabbitConstant.SMS_MESSAGE_PUSH, true);
    }

    @Bean
    public Queue yimaoAppMessagePushQueue() {
        return new Queue(RabbitConstant.YIMAO_APP_MESSAGE_PUSH, true);
    }
    
    @Bean
    public Queue stationMessageQueue() {
        return new Queue(RabbitConstant.STATION_MESSAGE_PUSH, true);
    }

    @Bean
    public Queue syncStationQueue() {
        return new Queue(RabbitConstant.SYNC_STATION, true);
    }
    

    /**
     * 请求接口路径日志
     */
    @Bean
    public Queue requestPathRecordQueue() {
        return new Queue(RabbitConstant.REQUEST_PATH_RECORD, true);
    }

    /**
     * 更新某个地区的库存数量
     */
    @Bean
    public Queue updateStoreHouseCountQueue() {
        return new Queue(RabbitConstant.INCREASE_OR_DECREASE_STOCK, true);
    }

    /***
     * 更新地区状态
     */
    @Bean
    public Queue updateAreaOnlineQueue() {
        return new Queue(RabbitConstant.AREA_ONLINE_STATUS_SYNC, true);
    }

    /**
     * 导出
     */
    @Bean
    public Queue exportActionSystemQueue() {
        return new Queue(RabbitConstant.EXPORT_ACTION_SYSTEM, true);
    }

    /**
     * 导出记录
     */
    @Bean
    public Queue exportRecordQueue() {
        return new Queue(RabbitConstant.EXPORT_RECORD, true);
    }

    /**
     * 转让操作记录
     */
    @Bean
    public Queue transferOperationLogQueue() {
        return new Queue(RabbitConstant.TRANSFER_OPERATION_LOG, true);
    }

    /**
     * 转让操作记录
     */
    @Bean
    public Queue storeHouseOperationLog() {
        return new Queue(RabbitConstant.STORE_HOUSE_OPERATION_LOG, true);
    }
    
    /**
     *安装工退机完成生成退机库存记录
     * @return
     */
    @Bean
    public Queue stationBackStockRecordQueue() {
        return new Queue(RabbitConstant.STATION_BACK_STOCK_RECORD, true);
    }
}
