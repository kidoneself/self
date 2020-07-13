package com.yimao.cloud.system.processor;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.MessageModelTypeEnum;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.properties.SystemProperties;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.station.StationMessageDTO;
import com.yimao.cloud.pojo.dto.system.AppMessageDTO;
import com.yimao.cloud.pojo.dto.system.MessageTemplateDTO;
import com.yimao.cloud.system.po.MessagePush;
import com.yimao.cloud.system.service.MessagePushService;
import com.yimao.cloud.system.service.MessageTemplateService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StationMessageProcessor {

    @Resource
    private MessagePushService messagePushService;
    @Resource
    private MessageTemplateService messageTemplateService;
    @Resource
    private SystemProperties systemProperties;
    
    /**
     * 功能描述:站务系统信息查询与查询消息更改已读未读状态
     */
    @RabbitListener(queues = RabbitConstant.STATION_MESSAGE_PUSH)
    @RabbitHandler
    public void processor(StationMessageDTO message) {
    	
    	
    	try {
    		if (message == null) {
	                log.error("消息推送失败");
	                return;
	            }
    		
    		if(Objects.isNull(message.getMessageType())) {
    			return;
    		}
    		
    		if(message.getMessageType() == 0) {
    			//消息推送
    			log.info("消息站务消息，推送类型={}",message.getPushType());
    			 
	            //非空校验
	            if (Objects.isNull(message.getPushType()) || Objects.isNull(message.getMechanism()) || Objects.isNull(message.getPushObject())) {
	                log.info("查询消息模板参数为空");
	                return;
	            }
	            if (null == message.getContentMap() || message.getContentMap().size() <= 0) {
	                log.error("短信推送失败，参数非法");
	                return;
	            }
	            if (message.getReceiverId() == null) {
	                log.info("消息推送失败，推送服务站区域id不能为空！");
	                return;
	            }
	            //创建安装工和创建经销商调用相同的模板
	            MessageTemplateDTO messageTemplate;
	            if(message.getPushType() == MessageModelTypeEnum.CREATE_STATION_ENGINEER_ACCOUNT.value || message.getPushType() == MessageModelTypeEnum.CREATE_STATION_DISTRIBUTOR_ACCOUNT.value) {
	            	//获取模版信息
		            messageTemplate = messageTemplateService.getMessageTemplateByParam(MessageModelTypeEnum.CREATE_ACCOUNT.value, message.getMechanism(), message.getPushObject(), MessagePushModeEnum.YIMAO_STATION.value);
	            }else {
	            	//获取模版信息
		            messageTemplate = messageTemplateService.getMessageTemplateByParam(message.getPushType(), message.getMechanism(), message.getPushObject(), MessagePushModeEnum.YIMAO_STATION.value);
	            }

	            
	            if (Objects.isNull(messageTemplate)) {
	                log.error("短信推送失败，短信模版不存在！");
	                return;
	            }
	            
	            String messageContent = getMessageContent(message.getContentMap(), messageTemplate.getTemplate());
	            MessagePush messagePush = new MessagePush(message);
	            messagePush.setApp(6);
	            messagePush.setContent(messageContent);
	            messagePush.setIsDelete(0);
	            messagePush.setReadStatus(0);
	            //保存通知发送记录
	            this.messagePushService.insert(messagePush);
    			
    		}else if(message.getMessageType() == 1) {
    			//暂时没有
    			
    			
    		}else{
    			return;
    		}
			
		} catch (Exception e) {
			
		}
    }
    
    
    private String getMessageContent(Map<String, String> map, String template) {
        for (String key : map.keySet()) {
            String value = map.get(key);
            //过滤掉null值
            if (StringUtil.isBlank(value)) {
                value = "";
            } else {
                value = map.get(key);
            }
            template = template.replace(key, value);
        }
        return template;
    }
    
    
}
