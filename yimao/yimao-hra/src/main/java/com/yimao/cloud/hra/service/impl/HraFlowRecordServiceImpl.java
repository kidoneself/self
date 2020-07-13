package com.yimao.cloud.hra.service.impl;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.hra.feign.UserFeign;
import com.yimao.cloud.hra.mapper.HraFlowRecordMapper;
import com.yimao.cloud.hra.mapper.HraTicketMapper;
import com.yimao.cloud.hra.po.HraFlowRecord;
import com.yimao.cloud.hra.service.HraFlowRecordService;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2018/5/14.
 */
@Service
public class HraFlowRecordServiceImpl implements HraFlowRecordService {

    private static final Logger logger = LoggerFactory.getLogger(HraFlowRecordService.class);

    @Resource
    private HraFlowRecordMapper hraFlowRecordMapper;
    @Resource
    private HraTicketMapper hraTicketMapper;
    @Resource
    private UserFeign userFeign;

    @Resource
    private AmqpTemplate rabbitTemplate;

    /**
     * 保存体检记录步骤
     *
     * @param map
     */
    @Override
    public void record(Map<String, Object> map) {
        String deviceId = null;
        String ticketNo = null;
        Long customerId = null;
        Long stationId = null;
        int step = 0;
        if (map.get("deviceId") != null) {
            deviceId = String.valueOf(map.get("deviceId"));
        }
        if (map.get("ticketNo") != null) {
            ticketNo = String.valueOf(map.get("ticketNo"));
        }
        if (map.get("stationId") != null) {
            stationId = Long.parseLong(String.valueOf(map.get("stationId")));
        }
        if (map.get("customerId") != null) {
            customerId = Long.parseLong(String.valueOf(map.get("customerId")));
        }
        if (map.get("step") != null) {
            step = Integer.parseInt(String.valueOf(map.get("step")));
        }
        HraFlowRecord record;
        if (step == 3) {
            record = new HraFlowRecord();
        } else {
            Example example = new Example(HraFlowRecord.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("ticketNo", ticketNo);
            List<HraFlowRecord> recordList = hraFlowRecordMapper.selectByExample(example);
            if (CollectionUtil.isNotEmpty(recordList)) {
                record = recordList.get(0);
            } else {
                record = new HraFlowRecord();
            }
        }
        record.setDeviceId(deviceId);
        record.setTicketNo(ticketNo);
        record.setCustomerId(customerId);
        record.setStationId(stationId);
        switch (step) {
            case 1:
                record.setStepOneTime(new Date());
                break;
            case 2:
                record.setStepTwoTime(new Date());
                break;
            case 3:
                record.setStepThreeTime(new Date());
                break;
            case 4:
                record.setStepFourTime(new Date());
                break;
            case 5:
                record.setStepFiveTime(new Date());
                break;
            case 6:
                record.setStepSixTime(new Date());
                break;
            case 7:
                record.setStepSevenTime(new Date());
                break;
        }
        if (step == 3 || record.getId() == null) {
            record.setHasSendMessage(false);
            hraFlowRecordMapper.insert(record);
        } else {
            hraFlowRecordMapper.updateByPrimaryKey(record);

            //消息推送
            if (record.getStepSevenTime() != null && (record.getHasSendMessage() == null || !record.getHasSendMessage())) {
                //根据体检卡，设备id获取体检报告是否存在
                try {
                    HraTicketDTO hraTicket = hraTicketMapper.findTicketByTicketNo(ticketNo);
                    if (hraTicket != null && hraTicket.getUserId() != null) {
                        String openid = userFeign.getOpenidByUserId(hraTicket.getUserId());
                        if (StringUtil.isNotEmpty(openid)) {
                            logger.info("开始进行体检报告的推送：设备号：" + record.getDeviceId() + ",体检卡号：" + record.getTicketNo());
                            Map<String, Object> msgMap = new HashMap<>();
                            msgMap.put("touser", openid);
                            msgMap.put("type", 66);
                            rabbitTemplate.convertAndSend(RabbitConstant.WX_TEMPLATE_MESSAGE_QUEUE, msgMap);
                            //设置为已发送
                            record.setHasSendMessage(true);
                            hraFlowRecordMapper.updateByPrimaryKey(record);
                        }
                    }
                } catch (AmqpException e) {
                    logger.error("体检报告上传后的消息推送失败" + e.getMessage());
                }
            }
        }
    }
}
