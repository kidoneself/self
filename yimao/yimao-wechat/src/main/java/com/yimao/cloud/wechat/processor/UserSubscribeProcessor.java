package com.yimao.cloud.wechat.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.wechat.feign.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 关注公众号队列
 */
@Component
@Slf4j
public class UserSubscribeProcessor {

    @Resource
    private UserFeign userFeign;

    @RabbitListener(queues = RabbitConstant.USER_SUBSCRIBE_QUEUE)
    @RabbitHandler
    public void userSubscribe(UserDTO user) {
        try {
            log.info("=================执行关注公众号===============================");
            user.setSubscribe(true);
            userFeign.update(user);
            userFeign.saveSubscribe(user.getId(), 7, user.getUserType(), user.getMobile(), new Date(), "关注公众号");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @RabbitListener(queues = RabbitConstant.USER_UNSUBSCRIBE_QUEUE)
    @RabbitHandler
    public void userUnSubscribe(UserDTO user) {
        try {
            log.info("=================执行取关公众号===============================");
            user.setSubscribe(false);
            userFeign.update(user);
            userFeign.saveSubscribe(user.getId(), 9, user.getUserType(), user.getMobile(), new Date(), "取关公众号");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
