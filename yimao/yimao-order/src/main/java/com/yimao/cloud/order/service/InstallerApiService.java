package com.yimao.cloud.order.service;

import java.util.List;

import com.yimao.cloud.pojo.dto.system.AppMessageDTO;

/***
 * 安装工app消息推送
 * @author zhangbaobao
 *
 */
public interface InstallerApiService {
	
	/***
	 * 消息推送
	 * @param pushType
	 * @param mechanism
	 * @param pushMode
	 * @param title
	 * @param name
	 */
	void pushMsgToApp(List<AppMessageDTO> appMessageList);

}
