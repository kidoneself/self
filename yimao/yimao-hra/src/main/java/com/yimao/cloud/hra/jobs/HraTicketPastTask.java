package com.yimao.cloud.hra.jobs;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.HraType;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.hra.feign.SystemFeign;
import com.yimao.cloud.hra.feign.UserFeign;
import com.yimao.cloud.hra.mapper.HraTicketMapper;
import com.yimao.cloud.pojo.dto.hra.HraTicketPastDTO;
import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 体检卡-评估卡 过期时间剩余7天时(=7)提醒
 *
 * @author liuhao@yimaokeji.com
 * @date 2019/5/13
 */
@Slf4j
@Component
@EnableScheduling
public class HraTicketPastTask {

    @Resource
    private HraTicketMapper hraTicketMapper;
    @Resource
    private AmqpTemplate rabbitTemplate;
    @Resource
    private UserFeign userFeign;

    @Resource
    private SystemFeign systemFeign;


    /**
     * 每天20:00执行 (0 0 20 * * ?)
     * 体检卡-评估卡 过期时间剩余7天时(=7)提醒
     */
    @Scheduled(cron = "0 0 20 * * ?")
    public void hraTicketPastJob() {
        try {
            //获取M卡七天过期的所属用户
            List<HraTicketPastDTO> mList = hraTicketMapper.ticketPastUserId(HraType.M.value);
            this.pastList(mList, 1);
            //获取Y卡七天过期的所属用户
            List<HraTicketPastDTO> yList = hraTicketMapper.ticketPastUserId(HraType.Y.value);
            this.pastList(yList, 2);
            //预约的体检卡明天过期的所属用户
            List<HraTicketPastDTO> tList = hraTicketMapper.ticketReservePastUserId();
            this.pastList(tList, 3);

            if (CollectionUtil.isNotEmpty(mList)) {
                this.ticketPastMessage(mList, 1);
            }
            if (CollectionUtil.isNotEmpty(yList)) {
                this.ticketPastMessage(yList, 2);
            }
            if (CollectionUtil.isNotEmpty(tList)) {
                this.ticketPastMessage(tList, 3);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * @param hpList
     * @param type   1-M卡七天过期  2-Y卡七天过期 3-预约的明天过期的
     * @return
     */
    private List<UserDTO> pastList(List<HraTicketPastDTO> hpList, Integer type) {
        if (CollectionUtil.isNotEmpty(hpList)) {
            // 获取处于赠送过期状态的体检劵的所持有者的id集合
            Set<Integer> userIds = new HashSet<>();
            //获取预约中所有的服务站id集合
            Set<Integer> stationIds = new HashSet<>();
            for (HraTicketPastDTO dto : hpList) {
                userIds.add(dto.getUserId());
                if (Objects.equals(type, 3)) {
                    if (dto.getStationId() != null) {
                        stationIds.add(dto.getStationId());
                    }
                }
            }
            // 查询体检劵所有者的信息 (信息包含 id 和 openid )
            List<UserDTO> mUserInfo = userFeign.userByIds(userIds);
            List<StationServiceAreaDTO> stationMap = null;
            if (Objects.equals(type, 3) && CollectionUtil.isNotEmpty(stationIds)) {
                stationMap = systemFeign.getStationNameByIds(stationIds);
            }
            if (CollectionUtil.isNotEmpty(mUserInfo)) {
                for (HraTicketPastDTO dto : hpList) {
                    //设置openid
                    for (UserDTO user : mUserInfo) {
                        if (Objects.equals(dto.getUserId(), user.getId())) {
                            dto.setOpenId(user.getOpenid());
                        }
                    }
                    //设置服务站名称
                    if (Objects.equals(type, 3) && stationMap != null) {
                        if (CollectionUtil.isNotEmpty(stationMap)) {
                            for (StationServiceAreaDTO stationDto : stationMap) {
                                if (Objects.equals(dto.getStationId(), stationDto.getStationId())) {
                                    dto.setStationName(stationDto.getStationName());
                                }
                            }
                        }
                    }
                }
                return mUserInfo;
            }
        }
        return null;
    }

    private void ticketPastMessage(List<HraTicketPastDTO> hraTicketPastDto, int type) {
        for (HraTicketPastDTO htp : hraTicketPastDto) {
            Map<String, Object> msgMap = new HashMap<>(8);
            msgMap.put("touser", htp.getOpenId());
            if (type == 1) {
                msgMap.put("type", 63);
                msgMap.put("counts", htp.getCounts());
            } else if (type == 2) {
                msgMap.put("type", 64);
                msgMap.put("counts", htp.getCounts());
            } else {
                msgMap.put("type", 65);
                msgMap.put("stationName", htp.getStationName());
            }
            rabbitTemplate.convertAndSend(RabbitConstant.WX_TEMPLATE_MESSAGE_QUEUE, msgMap);
        }
    }
}
