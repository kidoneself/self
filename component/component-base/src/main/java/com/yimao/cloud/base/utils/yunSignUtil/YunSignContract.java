package com.yimao.cloud.base.utils.yunSignUtil;

import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.properties.YunSignProperties;
import com.yimao.cloud.base.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Component
@Slf4j
public class YunSignContract {
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYMMDD");

    @Resource
    private DomainProperties domainProperties;

    @Resource
    private YunSignProperties yunSignProperties;

    public YunSignContract() {
    }

    public static String offTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(1, 10);
        return simpleDateFormat.format(calendar.getTime());
    }

    public YunSignResult createContract(String userId, String orderId, String customsId, String title, String templateId, String data) {
        String signType = "MD5";
        String appId = yunSignProperties.getServiceAppid();
        String appSecretKey = yunSignProperties.getServiceKey();
        String time = System.currentTimeMillis() + "";
        String offerTime = offTime();
        String md5str = appId + "&" + customsId + "&" + data + "&" + offerTime + "&" + orderId + "&" + templateId + "&" + time + "&" + title + "&" + userId + "&" + appSecretKey;
        md5str = StringUtil.removeTransferChar(md5str);
        log.debug("创建合同签名前参数：" + md5str);
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        log.debug("创建合同签名后值：" + sign);
        String[] strs = new String[]{"appId", "time", "sign", "signType", "userId", "customsId", "templateId", "orderId", "title", "offerTime", "data"};
        String[] val = new String[]{appId, time, sign, signType, userId, customsId, templateId, orderId, title, offerTime, data};

        try {
            YunSignResult result = YunSignCaller.callYunSign(domainProperties.getYunSign() + YunSignConfig.URI, "createContract", strs, val);
            return result.isSuccess() ? result : YunSignResult.err(result.getDesc());
        } catch (Exception var16) {
            return YunSignResult.err(var16.getMessage());
        }
    }

    public YunSignResult autoSignContract(String userId, String orderId) {
        String signType = "MD5";
        String appId = yunSignProperties.getServiceAppid();
        String appSecretKey = yunSignProperties.getServiceKey();
        String time = System.currentTimeMillis() + "";
        String sealId = yunSignProperties.getServiceSealid();
        String certType = "";
        String md5str = appId + "&" + certType + "&" + orderId + "&" + sealId + "&" + time + "&" + userId + "&" + appSecretKey;
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "time", "sign", "signType", "userId", "orderId", "sealId", "certType"};
        String[] val = new String[]{appId, time, sign, signType, userId, orderId, sealId, certType};

        try {
            return YunSignCaller.callYunSign(domainProperties.getYunSign() + "/mmecserver3.0/webservice/Common?wsdl", "sign", strs, val);
        } catch (Exception var13) {
            return YunSignResult.err(var13.getMessage());
        }
    }

    public YunSignResult queryContract(String orderId) {
        String signType = "MD5";
        String appId = yunSignProperties.getServiceAppid();
        String appSecretKey = yunSignProperties.getServiceKey();
        String time = System.currentTimeMillis() + "";
        String md5str = appId + "&" + orderId + "&" + time + "&" + appSecretKey;
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "sign", "signType", "orderId", "time"};
        String[] val = new String[]{appId, sign, signType, orderId, time};

        try {
            return YunSignCaller.callYunSign(domainProperties.getYunSign() + "/mmecserver3.0/webservice/Common?wsdl", "queryContract", strs, val);
        } catch (Exception var10) {
            return YunSignResult.err(var10.getMessage());
        }
    }

    public YunSignResult addUserSignInfo(String[] userId, String orderId, String[] postion) {
        String signType = "MD5";
        String positionChar = "@";
        String appId = yunSignProperties.getServiceAppid();
        String appSecretKey = yunSignProperties.getServiceKey();
        String time = System.currentTimeMillis() + "";
        String signInfo = "[";

        for(int i = 0; i < userId.length; ++i) {
            if (i > 0) {
                signInfo = signInfo + " ,";
            }

            signInfo = signInfo + "{\"userId\":\"" + userId[i] + "\",\"position\":\"" + postion[i] + "\",\"signUiType\":\"1\" } ";
        }

        signInfo = signInfo + "]";
        String md5str = appId + "&" + orderId + "&" + positionChar + "&" + signInfo + "&" + time + "&" + appSecretKey;
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "sign", "signType", "orderId", "time", "positionChar", "signInfo"};
        String[] val = new String[]{appId, sign, signType, orderId, time, positionChar, signInfo};

        try {
            return YunSignCaller.callYunSign(domainProperties.getYunSign() + "/mmecserver3.0/webservice/Addition?wsdl", "addSignInfo", strs, val);
        } catch (Exception var14) {
            return YunSignResult.err(var14.getMessage());
        }
    }
}
