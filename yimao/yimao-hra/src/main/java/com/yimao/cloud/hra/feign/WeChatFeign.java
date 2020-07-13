package com.yimao.cloud.hra.feign;

import com.yimao.cloud.base.constant.Constant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author liuhao@yimaokeji.com
 */
@FeignClient(name = Constant.MICROSERVICE_WECHAT)
public interface WeChatFeign {

    /**
     * 微信带参二维码
     *
     * @param userId    用户id
     * @param shareType 分享类型
     * @param shareNo   分享卡号
     * @param dateTime  时间戳
     * @return String
     */
    @RequestMapping(value = "/wx/qrcode")
    String getQRCodeWithParam(@RequestParam(value = "userId") Integer userId,
                    @RequestParam(value = "shareType", required = false) Integer shareType,
                    @RequestParam(value = "shareNo", required = false) String shareNo,
                    @RequestParam(value = "dateTime", required = false) Long dateTime);

}
