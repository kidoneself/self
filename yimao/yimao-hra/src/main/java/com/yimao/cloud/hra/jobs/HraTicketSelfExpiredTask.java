package com.yimao.cloud.hra.jobs;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.hra.mapper.HraTicketMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评估卡自动过期(每天执行一次)
 *
 * @author liuhao@yimaokeji.com
 * @date 2019/5/13
 */
@Slf4j
@Component
@EnableScheduling
public class HraTicketSelfExpiredTask {

    @Resource
    private HraTicketMapper hraTicketMapper;
    @Resource
    private AmqpTemplate rabbitTemplate;

    /**
     * 评估卡自动过期(每天执行一次) (0 0 3 * * ?)
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void hraTicketPastJob() {
        try {
            log.info("定时任务HraTicketSelfExpiredJob执行修改有效期方法");
            List<Integer> tickets = hraTicketMapper.findHasExpired();
            if (CollectionUtil.isNotEmpty(tickets)) {
                for (Integer tick : tickets) {
                    hraTicketMapper.updateHraTicketExpired(tick);
                }
            }
        } catch (Exception e) {
            log.error("评估卡自动过期错误");
            log.error(e.getMessage(), e);
        }
    }
}
