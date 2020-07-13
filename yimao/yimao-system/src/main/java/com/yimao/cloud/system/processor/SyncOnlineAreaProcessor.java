package com.yimao.cloud.system.processor;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.AreaSyncState;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.system.mapper.OnlineAreaMapper;
import com.yimao.cloud.system.po.OnlineArea;

import lombok.extern.slf4j.Slf4j;

/***
 * 地区上线同步完工单更新上线状态和同步状态
 * @author zhangbaobao
 *
 */
@Component
@Slf4j
public class SyncOnlineAreaProcessor {
		
	 @Resource
	 private OnlineAreaMapper onlineAreaMapper;
	 @RabbitListener(queues = RabbitConstant.AREA_ONLINE_STATUS_SYNC)
	 @RabbitHandler
	 public void processor(OnlineAreaDTO dto) {
		 try {
			log.info("==========更新上线地区状态========req"+JSONObject.toJSONString(dto));
			 OnlineArea area=new OnlineArea();
			 area.setId(dto.getId());
			 area.setStatus(dto.getStatus());
			 area.setSyncState(dto.getSyncState());
			 area.setSyncStateText(AreaSyncState.getAreaSyncStateName(dto.getSyncState()));
			 area.setUpdateTime(new Date());
			 onlineAreaMapper.updateByPrimaryKeySelective(area);
		} catch (Exception e) {
			log.error("=========更新地区状态异常========"+e.getMessage());
		}
	 }
}
