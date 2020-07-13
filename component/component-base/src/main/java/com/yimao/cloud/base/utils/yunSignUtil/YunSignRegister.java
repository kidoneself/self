package com.yimao.cloud.base.utils.yunSignUtil;

import com.google.gson.Gson;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.properties.YunSignProperties;
import com.yimao.cloud.base.utils.StringUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class YunSignRegister {

    @Resource
    private DomainProperties domainProperties;

    @Resource
    private YunSignProperties yunSignProperties;

    public YunSignRegister() {
    }

    public YunSignResult registerCompanyUser(String licenseNo, String companyName, String identityNumber, String userName, String mobile, String email, String userId, boolean ifAdmin) {
        String signType = "MD5";
        String appId = yunSignProperties.getServiceAppid();
        String appSecretKey = yunSignProperties.getServiceKey();
        String time = System.currentTimeMillis() + "";
        String type = "2";
        String isAdmin = ifAdmin ? "1" : "0";
        Map<String, String> map = new HashMap();
        Gson gson = new Gson();
        map.put("type", type);
        map.put("isAdmin", isAdmin);
        map.put("userId", userId);
        map.put("userName", userName);
        map.put("identityCard", identityNumber);
        map.put("mobile", mobile);
        map.put("email", email);
        map.put("licenseNo", licenseNo);
        map.put("companyName", companyName);
        List list = new ArrayList();
        list.add(map);
        String info = gson.toJson(list);
        String md5str = appId + "&" + info + "&" + time + "&" + appSecretKey;
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "time", "sign", "signType", "info"};
        String[] val = new String[]{appId, time, sign, signType, info};

        try {
            return YunSignCaller.callYunSign(domainProperties.getYunSign() + "/mmecserver3.0/webservice/Common?wsdl", "register", strs, val);
        } catch (Exception var24) {
            return YunSignResult.err("");
        }
    }

    public YunSignResult registerPersonalUser(String identityNumber, String userName, String mobile, String email, String userId, Boolean ifAdmin) {
        String signType = "MD5";
        String appId = yunSignProperties.getServiceAppid();
        String appSecretKey = yunSignProperties.getServiceKey();
        String time = System.currentTimeMillis() + "";
        String type = "1";
        String isAdmin = ifAdmin ? "1" : "0";
        Map<String, String> map = new HashMap();
        Gson gson = new Gson();
        map.put("type", type);
        map.put("isAdmin", isAdmin);
        map.put("userId", userId);
        map.put("userName", userName);
        if (!StringUtil.isEmpty(identityNumber)) {
            map.put("identityCard", identityNumber);
        }

        map.put("mobile", mobile);
        map.put("email", email);
        List list = new ArrayList();
        list.add(map);
        String info = gson.toJson(list);
        String md5str = appId + "&" + info + "&" + time + "&" + appSecretKey;
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "time", "sign", "signType", "info"};
        String[] val = new String[]{appId, time, sign, signType, info};

        try {
            YunSignResult result = YunSignCaller.callYunSign(domainProperties.getYunSign() + "/mmecserver3.0/webservice/Common?wsdl", "register", strs, val);
            return result;
        } catch (Exception var22) {
            return YunSignResult.err(var22.getMessage());
        }
    }

    public YunSignResult checkUserExisted(String userId) {
        String signType = "MD5";
        String appId = yunSignProperties.getServiceAppid();
        String appSecretKey = yunSignProperties.getServiceKey();
        String time = System.currentTimeMillis() + "";
        String md5str = appId + "&" + time + "&" + userId + "&" + appSecretKey;
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "time", "sign", "signType", "userId"};
        String[] val = new String[]{appId, time, sign, signType, userId};

        try {
            return YunSignCaller.callYunSign(domainProperties.getYunSign() + "/mmecserver3.0/webservice/Common?wsdl", "userQuery", strs, val);
        } catch (Exception var10) {
            return YunSignResult.err(var10.getMessage());
        }
    }

    public YunSignResult checkidentityNumber(String identityNumber, String userName) {
        String signType = "MD5";
        String appId = yunSignProperties.getServiceAppid();
        String appSecretKey = yunSignProperties.getServiceKey();
        String time = System.currentTimeMillis() + "";
        String md5str = appId + "&" + identityNumber + "&" + time + "&" + userName + "&" + appSecretKey;
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "time", "sign", "signType", "userName", "identityNumber"};
        String[] val = new String[]{appId, time, sign, signType, userName, identityNumber};

        try {
            return YunSignCaller.callYunSign(domainProperties.getYunSign() + "/identityCheck/webservice/IdentityCard?wsdl", "verifyIdentity", strs, val);
        } catch (Exception var11) {
            var11.printStackTrace();
            return YunSignResult.err(var11.getMessage());
        }
    }
}
