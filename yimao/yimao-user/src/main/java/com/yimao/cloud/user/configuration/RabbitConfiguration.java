package com.yimao.cloud.user.configuration;

import com.yimao.cloud.base.constant.RabbitConstant;
import org.springframework.amqp.core.DirectExchange;
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

    /**
     * 创建完全匹配模式的交换机
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("directExchange");
    }

    // /**
    //  * 创建模糊匹配模式的交换机
    //  */
    // @Bean
    // public TopicExchange topicExchange() {
    //     return new TopicExchange("topicExchange");
    // }

    // /**
    //  * 将队列与交换器绑定，并设置路由键
    //  */
    // @Bean
    // public Binding adminLogBinding() {
    //     return BindingBuilder.bind(adminLogQueue()).to(directExchange()).with(RabbitConstant.ADMIN_LOG);
    // }

    /**
     * 创建管理员登录日志队列
     */
    @Bean
    public Queue adminLogQueue() {
        return new Queue(RabbitConstant.ADMIN_LOG, true);
    }

    /**
     * 经销商订单生成
     */
    @Bean
    public Queue distributorOrderQueue() {
        return new Queue(RabbitConstant.USER_DISTRIBUTOR_ORDER_QUEUE, true);
    }

    // /**
    //  * 根据用户ID更新用户缓存的队列
    //  */
    // @Bean
    // public Queue userUpdateIdQueue() {
    //     return new Queue(RabbitConstant.USER_UPDATE_ID, true);
    // }

    // /**
    //  * 根据经销商ID更新经销商缓存的队列
    //  */
    // @Bean
    // public Queue distributorUpdateIdQueue() {
    //     return new Queue(RabbitConstant.DISTRIBUTOR_UPDATE_ID, true);
    // }

    // /**
    //  * 根据经销商信息更新经销商缓存的队列
    //  */
    // @Bean
    // public Queue distributorUpdateQueue() {
    //     return new Queue(RabbitConstant.DISTRIBUTOR_UPDATE, true);
    // }

    // /**
    //  * 根据管理员ID更新管理员缓存的队列
    //  */
    // @Bean
    // public Queue adminUpdateIdQueue() {
    //     return new Queue(RabbitConstant.ADMIN_UPDATE_ID, true);
    // }

    // /**
    //  * 根据管理员信息更新管理员缓存的队列
    //  */
    // @Bean
    // public Queue adminUpdateQueue() {
    //     return new Queue(RabbitConstant.ADMIN_UPDATE, true);
    // }

    @Bean
    public Queue syncDistributorQueue() {
        return new Queue(RabbitConstant.SYNC_DISTRIBUTOR, true);
    }

    @Bean
    public Queue syncEngineerQueue() {
        return new Queue(RabbitConstant.SYNC_ENGINEER, true);
    }

    /**
     * 用户身份变化记录
     */
    @Bean
    public Queue userChangeQueue() {
        return new Queue(RabbitConstant.USER_CHANGE_QUEUE, true);
    }

    /**
     * 用户身份变化记录
     */
    @Bean
    public Queue userEngineerChangeRecordQueue() {
        return new Queue(RabbitConstant.ENGINEER_CHANGE_RECORD, true);
    }

    /**
     * 安装工程师正在服务的工单数
     */
    @Bean
    public Queue engineerBusyCountQueue() {
        return new Queue(RabbitConstant.ENGINEER_BUSY_COUNT, true);
    }

    /**
     * 导出
     */
    @Bean
    public Queue exportActionUserQueue() {
        return new Queue(RabbitConstant.EXPORT_ACTION_USER, true);
    }

    /**
     * 导出
     */
    @Bean
    public Queue distributorOrderProtocolYimaoSignQueue() {
        return new Queue(RabbitConstant.DISTRIBUTOR_ORDER_PROTOCOL_YIMAO_SIGN, true);
    }

    /**
     * 同步更新安装工表中服务站名称
     */
    @Bean
    public Queue engineerUpdateStationName() {
        return new Queue(RabbitConstant.ENGINEER_UPDATE_STATION_NAME, true);
    }
}
