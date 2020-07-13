package com.yimao.cloud.user.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.EngineerState;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.user.mapper.EngineerMapper;
import com.yimao.cloud.user.po.Engineer;
import com.yimao.cloud.user.service.EngineerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 服务站门店名称更变同步更新安装工表中服务站名称
 *
 * @author Liu Long Jie
 * @date 2020-7-10
 */
@Component
@Slf4j
public class EngineerUpdateStationNameProcessor {

    @Resource
    private EngineerService engineerService;

    @RabbitListener(queues = RabbitConstant.ENGINEER_UPDATE_STATION_NAME)
    @RabbitHandler
    public void processor(Map<String, Object> stationInfo) {
        log.info("============================ 服务站门店名称更变同步更新安装工表中服务站名称队列开始执行。 ========================");
        try {
            Integer stationId = (Integer) stationInfo.get("stationId");
            String newStationName = (String) stationInfo.get("newStationName");
            if (stationId != null && StringUtil.isNotEmpty(newStationName)) {
                engineerService.updateStationName(stationInfo);
            } else {
                log.error("========================== 服务站门店名称更变同步更新安装工表中服务站名称队列异常终止，传参有误！ ==================================");
            }
            log.info("============================ 服务站门店名称更变同步更新安装工表中服务站名称队列执行结束。 ========================");
        } catch (Exception e) {
            log.error("============================ 服务站门店名称更变同步更新安装工表中服务站名称队列执行异常 ========================");
        }
    }

}
