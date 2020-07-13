package com.yimao.cloud.hra.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.enums.IdentityTypeEnum;
import com.yimao.cloud.base.properties.WechatProperties;
import com.yimao.cloud.base.utils.AesCbcUtil;
import com.yimao.cloud.base.utils.HttpsUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.hra.feign.UserFeign;
import com.yimao.cloud.hra.service.HealthyLoginService;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.wechat.WxUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Zhang Bo
 * @date 2018/10/31.
 */
@Service
@Slf4j
public class HealthyLoginServiceImpl implements HealthyLoginService {

    @Resource
    private WechatProperties wechatProperties;
    @Resource
    private UserFeign userFeign;

    @Override
    public UserDTO getUserInfoByCode(String code, String encryptedData, String iv, Integer sharerId) {
        //向微信服务器发送请求
        //请求参数
        String params = "appid=" + wechatProperties.getHealth().getAppid() + "&secret=" + wechatProperties.getHealth().getSecret()
                + "&js_code=" + code + "&grant_type=" + WechatConstant.GRANT_TYPE;
        log.debug("*****请求数据：" + params + "******");
        log.debug("*****请求数据：sharerId=" + sharerId + "******");
        String s = HttpsUtil.sendGet(WechatConstant.WX_MALL_URL, params);
        log.debug("*****微信服务器响应数据：" + s + "******");
        /**
         * 返回参数       说明
         * openid       用户唯一标识
         * session_key  会话密钥
         * unionid      用户在开放平台的唯一标识符
         */
        //解析相应内容（转换成json对象）
        JSONObject jsonObject = JSONObject.parseObject(s);
        //用户的唯一标识（openid）
        String openid = (String) jsonObject.get("openid");
        log.debug("#######用户的唯一标识openid = " + openid + "########");
        //用户在开放平台的唯一标识符unionid
        String unionid = (String) jsonObject.get("unionid");
        log.debug("#######用户在开放平台的唯一标识符unionid = " + unionid + "########");

        String session_key = (String) jsonObject.get("session_key");
        log.debug("#######微信服务器返回的session_key= " + session_key + "########");
        log.debug("============encryptedData=" + encryptedData + ",session_key=" + session_key + ",iv=" + iv);
        //对encryptedData加密数据进行AES解密
        String result = null;
        try {
            result = AesCbcUtil.decrypt(encryptedData, session_key, iv, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("AES解密异常");
        }
        UserDTO userDTO;
        if (StringUtil.isNotEmpty(result)) {
            log.debug("#########encryptedData解密成功!##########");
            JSONObject userInfoJSON = JSONObject.parseObject(result);
            log.debug("********解密成功后:userInfoJSON=" + userInfoJSON.toString() + "***************");
            WxUserInfo wxUserInfo = new WxUserInfo();
            wxUserInfo.setOpenid((String) userInfoJSON.get("openId"));
            wxUserInfo.setUnionid((String) userInfoJSON.get("unionId"));
            wxUserInfo.setCity((String) userInfoJSON.get("city"));
            wxUserInfo.setProvince((String) userInfoJSON.get("province"));
            String sex = (String) userInfoJSON.get("sex");
            String nickName = (String) userInfoJSON.get("nickName");
            String avatarUrl = (String) userInfoJSON.get("avatarUrl");
            if (sex != null) {
                wxUserInfo.setSex(Integer.parseInt((String) userInfoJSON.get("sex")));
            }

            if (nickName != null) {
                wxUserInfo.setNickname((String) userInfoJSON.get("nickName"));
            }

            if (avatarUrl != null) {
                wxUserInfo.setHeadimgurl((String) userInfoJSON.get("avatarUrl"));
            }

            userDTO = userFeign.userProcess(IdentityTypeEnum.HEALTH_MINI.value, sharerId, wxUserInfo);
            return userDTO;
        }
        log.debug("========encryptedData解密失败==========");
        return null;
    }

}
