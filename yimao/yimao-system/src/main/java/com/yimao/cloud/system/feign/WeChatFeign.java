package com.yimao.cloud.system.feign;

import com.yimao.cloud.base.constant.Constant;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author liuhao@yimaokeji.com
 * @date 2018-11-12
 */
@FeignClient(name = Constant.MICROSERVICE_WECHAT)
public interface WeChatFeign {

}
