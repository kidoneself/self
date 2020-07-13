package com.yimao.cloud.order.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.enums.MessagePushObjectEnum;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.order.service.InstallerApiService;
import com.yimao.cloud.pojo.dto.system.AppMessageDTO;

import lombok.extern.slf4j.Slf4j;

/***
 * 安装工app消息推送
 * 
 * @author zhangbaobao
 *
 */
@Service
@Slf4j
public class InstallerApiServiceImpl implements InstallerApiService {

	@Resource
	private RabbitTemplate rabbitTemplate;

	@Override
	public void pushMsgToApp(List<AppMessageDTO> appMessageList) {
		if (!CollectionUtil.isEmpty(appMessageList)) {
			for (AppMessageDTO appMessage : appMessageList) {
				// 构建APP消息推送实体
				if (MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value == appMessage.getApp()) {
					// 1-推送给安装工；
					appMessage.setPushObject(MessagePushObjectEnum.ENGINEER.value);
					rabbitTemplate.convertAndSend(RabbitConstant.ENGINEER_APP_MESSAGE_PUSH, appMessage);
				} else {
					// 2-推送消息给经销商
					appMessage.setPushObject(MessagePushObjectEnum.DISTRIBUTOR.value);
					rabbitTemplate.convertAndSend(RabbitConstant.YIMAO_APP_MESSAGE_PUSH, appMessage);
				}
				log.info("==========(workOrderId=" + appMessage.getWorkorderId()
						+ ")InstallerApiService.pushMsgToApp.over====" + JSONObject.toJSONString(appMessage));
			}
		}
	}

}
