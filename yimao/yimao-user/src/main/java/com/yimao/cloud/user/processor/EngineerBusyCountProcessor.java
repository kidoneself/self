package com.yimao.cloud.user.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.EngineerState;
import com.yimao.cloud.user.mapper.EngineerMapper;
import com.yimao.cloud.user.po.Engineer;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 安装工程师正在服务的工单数
 *
 * @author Zhang Bo
 * @date 2019/11/1
 */
@Component
public class EngineerBusyCountProcessor {

    @Resource
    private EngineerMapper engineerMapper;

    @RabbitListener(queues = RabbitConstant.ENGINEER_BUSY_COUNT)
    @RabbitHandler
    public void processor(Map<String, Integer> map) {
        try {
            Integer engineerId = map.get("engineerId");
            //num为正数就是加，为负数就是减
            Integer num = map.get("num");
            if (engineerId != null && num != null) {
                Engineer engineer = engineerMapper.selectStateAndCountById(engineerId);
                Integer oldCount = engineer.getCount();
                if (oldCount == null) {
                    oldCount = 0;
                }
                int newCount = oldCount + num;
                if (newCount < 0) {
                    newCount = 0;
                }
                engineer.setCount(newCount);
                if (newCount == 0) {
                    engineer.setState(EngineerState.FREE.value);//客服人员状态：1-忙碌，0-空闲
                }
                engineerMapper.updateByPrimaryKeySelective(engineer);
            }
        } catch (Exception e) {

        }
    }

}
