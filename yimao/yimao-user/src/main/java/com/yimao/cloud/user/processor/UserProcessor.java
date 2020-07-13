package com.yimao.cloud.user.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.UserType;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.user.UserChangeRecordDTO;
import com.yimao.cloud.user.mapper.UserMapper;
import com.yimao.cloud.user.po.User;
import com.yimao.cloud.user.service.UserChangeService;
import com.yimao.cloud.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * 用户信息更新队列
 *
 * @author Zhang Bo
 * @date 2019/01/21
 */
@Component
@Slf4j
public class UserProcessor {

    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private UserChangeService userChangeService;

    // /**
    //  * 更新用户缓存信息
    //  *
    //  * @param userId 用户ID
    //  */
    // @RabbitListener(queues = RabbitConstant.USER_UPDATE_ID)
    // @RabbitHandler
    // public void processor01(Integer userId) {
    //     try {
    //         if (Objects.nonNull(userId)) {
    //             UserDTO userDTO = userService.getFullUserDTOById(userId);
    //             if (Objects.nonNull(userDTO)) {
    //                 redisCache.set(Constant.USER_CACHE + userId, userDTO);
    //             }
    //         }
    //     } catch (Exception e) {
    //         log.error("更新用户缓存信息发生错误，" + e.getMessage(), e);
    //     }
    // }

    /**
     * 用户信息变更记录
     *
     * @param userDTO
     */
    @RabbitListener(queues = RabbitConstant.USER_CHANGE_QUEUE)
    @RabbitHandler
    public void userChangeRecord(UserChangeRecordDTO userDTO) {
        try {
            if (Objects.nonNull(userDTO)) {
                userChangeService.save(userDTO.getOrigUserId(), userDTO.getType(), userDTO.getOrigUserType(), userDTO.getOrigPhone(), new Date(), userDTO.getRemark(), null);
            }
        } catch (Exception e) {
            log.error("用户信息变更记录发生错误，" + e.getMessage(), e);
        }
    }

    /**
     * 升级会员用户逻辑
     *
     * @param userId 用户ID
     */
    @RabbitListener(queues = RabbitConstant.USER_UPGRADEVIP)
    @RabbitHandler
    public void upgradeUserToVip(Integer userId) {
        try {
            User user = userMapper.selectForUpgradeVip(userId);
            if (user.getUserType() == UserType.USER_3.value) {
                userService.upgradeToSaleUser(user);
            }
        } catch (Exception e) {
            log.error("升级会员用户发生错误，" + e.getMessage(), e);
        }
    }
}
