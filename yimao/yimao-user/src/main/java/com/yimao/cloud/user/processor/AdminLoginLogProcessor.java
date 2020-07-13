package com.yimao.cloud.user.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.pojo.dto.user.AdminLogDTO;
import com.yimao.cloud.user.service.AdminLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Zhang Bo
 * @date 2018/8/15
 */
@Component
@Slf4j
public class AdminLoginLogProcessor {

    @Resource
    private AdminLogService adminLogService;

    @RabbitListener(queues = RabbitConstant.ADMIN_LOG)
    @RabbitHandler
    public void processor(AdminLogDTO dto) {
        try {
            adminLogService.save(dto);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
