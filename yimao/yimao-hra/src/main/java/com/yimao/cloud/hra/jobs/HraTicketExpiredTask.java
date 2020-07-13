package com.yimao.cloud.hra.jobs;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.hra.feign.UserFeign;
import com.yimao.cloud.hra.po.HraTicket;
import com.yimao.cloud.hra.po.HraTicketLifeCycle;
import com.yimao.cloud.hra.service.HraTicketService;
import com.yimao.cloud.hra.service.TicketLifeCycleService;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 赠送中但已过期的体检劵
 *
 * @author liuhao@yimaokeji.com
 * @date 2019/5/13
 */
@Slf4j
@Component
@EnableScheduling
public class HraTicketExpiredTask {

    @Resource
    private HraTicketService hraTicketService;
    @Resource
    private TicketLifeCycleService ticketLifeCycleService;
    @Resource
    private UserFeign userFeign;
    @Resource
    private AmqpTemplate rabbitTemplate;

    /**
     * 赠送中的卡到期提醒 (0 0 2 * * ?)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void hraTicketExpiredJob() {
        try {
            //查询处于赠送状态但已过期的体检劵
            List<HraTicket> hraTickets = hraTicketService.listExpiredHraTicket();
            if (CollectionUtil.isEmpty(hraTickets)) {
                return;
            }
            // 获取用户评估卡所有者的id集合
            Set<Integer> userIds = new HashSet<>();
            for (HraTicket hraTicket : hraTickets) {
                userIds.add(hraTicket.getUserId());
            }
            // 查询评估卡所有者的信息 包含id 和 openid 信息
            List<UserDTO> userInfos = userFeign.userByIds(userIds);
            if (CollectionUtil.isEmpty(userInfos)) {
                return;
            }
            // 将userId 与 卡号 一一对应
            Map<Integer, List<String>> map = new HashMap<>(8);
            for (UserDTO userInfo : userInfos) {
                Integer id = userInfo.getId();
                List<String> strings = map.get(id);
                if (CollectionUtil.isNotEmpty(strings)) {
                    continue;
                }
                for (HraTicket hraTicket : hraTickets) {
                    Integer userId = hraTicket.getUserId();
                    idMapper(id, userId, hraTicket.getTicketNo(), map);
                    List<HraTicketLifeCycle> ticketLifeCycles = ticketLifeCycleService.listTicketLifeCycle(hraTicket.getCardId(), userId, hraTicket.getHandselTime(), 0);
                    if (CollectionUtil.isNotEmpty(ticketLifeCycles)) {
                        HraTicketLifeCycle ticketLifeCycle = ticketLifeCycles.get(0);
                        ticketLifeCycle.setExpiredFlag(3);
                        ticketLifeCycleService.updateHandselRecordToTimeOut(ticketLifeCycle);
                    }
                    // 将赠送状态修改为未赠送状态 且 标记为过期
                    hraTicketService.updateHraTicketExpired(hraTicket.getId());
                }
            }

            for (UserDTO userInfo : userInfos) {
                Map<String, Object> msgMap = new HashMap<>(4);
                msgMap.put("touser", userInfo.getOpenid());
                msgMap.put("type", 21);
                msgMap.put("ticketNos", map.get(userInfo.getId()));
                rabbitTemplate.convertAndSend(RabbitConstant.WX_TEMPLATE_MESSAGE_QUEUE, msgMap);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 用户id 和 所持有的体检卡的一对多的映射关系
     */
    private void idMapper(Integer id, Integer userId, String ticketNo, Map<Integer, List<String>> map) {
        if (Objects.equals(id, userId)) {
            List<String> ticketNos = map.get(id);
            if (CollectionUtil.isEmpty(ticketNos)) {
                ticketNos = new LinkedList<>();
                ticketNos.add(ticketNo);
                map.put(id, ticketNos);
            } else {
                ticketNos.add(ticketNo);
            }
        }
    }
}
