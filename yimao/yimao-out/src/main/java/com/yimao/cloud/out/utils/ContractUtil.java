package com.yimao.cloud.out.utils;

import com.google.gson.Gson;
import com.yimao.cloud.base.baideApi.utils.StringUtil;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.properties.YunSignProperties;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignCaller;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignConfig;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignContract;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignData;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public final class ContractUtil {

    @Resource
    private DomainProperties domainProperties;

    @Resource
    private YunSignProperties yunSignProperties;

    public ContractUtil() {
    }

    public Map<String, Object> signContract(String userName, String productModal, String snCode, String province, String city, String region, String address, boolean isNeedComplete, String completeNotice, int chargeType, int homeCostPrice, int homeOpenAccount, int busineseCostPrice, int busineseOpenAccount, int completeOpenAccount, String mobile, String identityCard, int yearAmount, String orderId, String email) {
        Map<String, Object> ru = new HashMap();
        if (StringUtil.isNotEmpty(identityCard)) {
            YunSignResult checkResult = checkidentityNumber(identityCard, userName);
            if (!checkResult.isSuccess()) {
                ru.put("success", false);
                ru.put("msg", checkResult.getDesc());
                return ru;
            }
        }

        ru = registerCreate(userName, productModal, snCode, province, city, region, address, isNeedComplete, completeNotice, chargeType, homeCostPrice, homeOpenAccount, busineseCostPrice, busineseOpenAccount, completeOpenAccount, mobile, identityCard, yearAmount, orderId, email);
        return ru;
    }

    private YunSignResult checkidentityNumber(String identityNumber, String userName) {
        String signType = "MD5";
        String appId = yunSignProperties.getWorkorderServiceAppid();
        String appSecretKey = yunSignProperties.getWorkorderServiceKey();
        String time = System.currentTimeMillis() + "";
        String md5str = appId + "&" + identityNumber + "&" + time + "&" + userName + "&" + appSecretKey;
        String sign = com.yimao.cloud.base.utils.StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "time", "sign", "signType", "userName", "identityNumber"};
        String[] val = new String[]{appId, time, sign, signType, userName, identityNumber};

        try {
            return YunSignCaller.callYunSign(domainProperties.getYunSign() + "/identityCheck/webservice/IdentityCard?wsdl", "verifyIdentity", strs, val);
        } catch (Exception var11) {
            var11.printStackTrace();
            return YunSignResult.err(var11.getMessage());
        }
    }

    private Map<String, Object> registerCreate(String userName, String productModal, String snCode, String province, String city, String region, String address, boolean isNeedComplete, String completeNotice, int chargeType, int homeCostPrice, int homeOpenAccount, int busineseCostPrice, int busineseOpenAccount, int completeOpenAccount, String mobile, String identityCard, int yearAmount, String orderId, String email) {
        Map<String, Object> ru = new HashMap();
        //校验userId是否在云签平台已存在
        Boolean boo = checkUserExisted(mobile).isSuccess();
        if (!boo) {
            //如果不存在则注册云签
            YunSignResult regResult = registerPersonalUser(identityCard, userName, mobile, email, mobile, true);
            if (!regResult.isSuccess()) {
                ru.put("success", false);
                ru.put("msg", regResult.getDesc());
            }
        }
        Map<String, Object> createResult = create(userName, productModal, snCode, province, city, region, address, isNeedComplete, completeNotice, chargeType, homeCostPrice, homeOpenAccount, busineseCostPrice, busineseOpenAccount, completeOpenAccount, mobile, identityCard, yearAmount, orderId, mobile);
        if (createResult != null && (Boolean) createResult.get("success")) {
            ru.put("success", true);
        } else {
            ru.put("success", false);
            ru.put("msg", createResult.get("msg"));
        }


        return ru;
    }

    private YunSignResult checkUserExisted(String userId) {
        String signType = "MD5";
        String appId = yunSignProperties.getWorkorderServiceAppid();
        String appSecretKey = yunSignProperties.getWorkorderServiceKey();
        String time = System.currentTimeMillis() + "";
        String md5str = appId + "&" + time + "&" + userId + "&" + appSecretKey;
        String sign = com.yimao.cloud.base.utils.StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "time", "sign", "signType", "userId"};
        String[] val = new String[]{appId, time, sign, signType, userId};

        try {
            return YunSignCaller.callYunSign(domainProperties.getYunSign() + "/mmecserver3.0/webservice/Common?wsdl", "userQuery", strs, val);
        } catch (Exception var10) {
            return YunSignResult.err(var10.getMessage());
        }
    }

    private YunSignResult registerPersonalUser(String identityNumber, String userName, String mobile, String email, String userId, boolean ifAdmin) {
        String signType = "MD5";
        String appId = yunSignProperties.getWorkorderServiceAppid();
        String appSecretKey = yunSignProperties.getWorkorderServiceKey();
        String time = System.currentTimeMillis() + "";
        String type = "1";
        String isAdmin = ifAdmin ? "1" : "0";
        Map<String, String> map = new HashMap();
        Gson gson = new Gson();
        map.put("type", type);
        map.put("isAdmin", isAdmin);
        map.put("userId", userId);
        map.put("userName", userName);
        if (!com.yimao.cloud.base.utils.StringUtil.isEmpty(identityNumber)) {
            map.put("identityCard", identityNumber);
        }

        map.put("mobile", mobile);
        map.put("email", email);
        List list = new ArrayList();
        list.add(map);
        String info = gson.toJson(list);
        String md5str = appId + "&" + info + "&" + time + "&" + appSecretKey;
        String sign = com.yimao.cloud.base.utils.StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "time", "sign", "signType", "info"};
        String[] val = new String[]{appId, time, sign, signType, info};

        try {
            YunSignResult result = YunSignCaller.callYunSign(domainProperties.getYunSign() + "/mmecserver3.0/webservice/Common?wsdl", "register", strs, val);
            return result;
        } catch (Exception var22) {
            return YunSignResult.err(var22.getMessage());
        }
    }

    private String getChinese(int yearAmount) {
        String chinese = "";
        if (yearAmount == 1) {
            chinese = "一";
        } else if (yearAmount == 2) {
            chinese = "二";
        } else if (yearAmount == 3) {
            chinese = "三";
        }

        return chinese;
    }

    public Map<String, Object> create(String userName, String productModal, String snCode, String province, String city, String region, String address, boolean isNeedComplete, String completeNotice, int chargeType, int homeCostPrice, int homeOpenAccount, int busineseCostPrice, int busineseOpenAccount, int completeOpenAccount, String mobile, String identityCard, int yearAmount, String orderId, String userId) {
        log.debug("userName={1}; productModal={2}; snCode={3}; province={4};  city={5};  region={6};  address={7};  isNeedComplete={8}; completeNotice={9}; chargeType={10};\n         homeCostPrice={11}; homeOpenAccount={12}; busineseCostPrice={13}; busineseOpenAccount={14}; completeOpenAccount={15};\n         mobile={16};  identityCard={17}; yearAmount={18};  orderId={19};  userId{20}", new Object[]{userName, productModal, snCode, province, city, region, address, isNeedComplete, completeNotice, chargeType, homeCostPrice, homeOpenAccount, busineseCostPrice, busineseOpenAccount, completeOpenAccount, mobile, identityCard, yearAmount, orderId, userId});
        HashMap map = new HashMap();

        try {
            String costMoney = "0.38";
            String brand = "翼猫";
            String costType = "5.1.2";
            if (isNeedComplete) {
                costType = "5.1.1";
            }

            String openAccount = BigDecimal.valueOf((long) completeOpenAccount).toString();

            String costChoose = "";
            if (chargeType == 2) {
                costChoose = "（2）";
            } else if (chargeType == 1) {
                costChoose = "（1）";
            }

            completeNotice = "十";
            String secondPartyAddress = province.trim() + city.trim() + region.trim() + address.trim();
            province = province.substring(0, province.length() - 1);
            city = city.substring(0, city.length() - 1);
            region = region.substring(0, region.length() - 1);
            String homeMoney = "1000";
            String businessMoney = "1500";

            String cycle = "12";
            String accountBank = "上海银行南翔支行";
            String accountName = "翼猫科技发展(上海)有限公司";
            String account = "31570703001374787";
            String homePrice = "1180";
            String businessPrice = "1680";

            String firstServiceCycle = "14";
            String peroidOfValidity = getChinese(yearAmount);
            Calendar calendar = Calendar.getInstance();
            String year = String.valueOf(calendar.get(1));
            String month = String.valueOf(calendar.get(2) + 1);
            String day = String.valueOf(calendar.get(5));
            calendar.add(1, yearAmount);
            String validityYear = String.valueOf(calendar.get(1));
            String validityMonth = String.valueOf(calendar.get(2) + 1);
            String validityDay = String.valueOf(calendar.get(5));
            String stopPaymentDays = "10";
            String homeCompensate = "5800";
            String firstParty = "翼猫科技发展(上海)有限公司";
            String representative = "卞尔静";
            String firstPartyAddress = "上海市嘉定区南翔镇银翔路799号20层";
            String firstPartyPhone = "18616250115";
            String indemnity1 = "5800";
            String indemnity2 = "402";
            String indemnity3 = "1820";
            String indemnity4 = "473";
            String indemnity5 = "765";
            String indemnity6 = "680";
            String indemnity7 = "650";
            String indemnity8 = "720";
            String data = YunSignData.contractData(userName, brand, productModal, snCode, province, city, region, address, costType, openAccount, completeNotice, homePrice, businessPrice, costChoose, costMoney, firstServiceCycle, homeMoney, businessMoney, cycle, accountBank, accountName, account, peroidOfValidity, year, month, day, validityYear, validityMonth, validityDay, stopPaymentDays, homeCompensate, firstParty, userName, representative, identityCard, firstPartyAddress, secondPartyAddress, firstPartyPhone, mobile, indemnity1, indemnity2, indemnity3, indemnity4, indemnity5, indemnity6, indemnity7, indemnity8);
            log.info("data:"+data);
            String customsId = userId + "," + YunSignConfig.YUNSIGN_SERVICE_YIMAO_ACCOUNT;
            YunSignResult createResult = createContract(userId, orderId, customsId, userName + "翼猫净水机使用合同", yunSignProperties.getServiceTempleid(), data);
            if (createResult.isSuccess()) {
                addUserSignInfo(new String[]{userId, YunSignConfig.YUNSIGN_SERVICE_YIMAO_ACCOUNT}, orderId, new String[]{"2", "1"});
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("msg", createResult.getDesc());
            }
        } catch (Exception var61) {
            map.put("success", false);
            map.put("msg", "合同创建失败");
        }

        return map;
    }

    public YunSignResult addUserSignInfo(String[] userId, String orderId, String[] postion) {
        String signType = "MD5";
        String positionChar = "@";
        String appId = yunSignProperties.getWorkorderServiceAppid();
        String appSecretKey = yunSignProperties.getWorkorderServiceKey();
        String time = System.currentTimeMillis() + "";
        String signInfo = "[";

        for (int i = 0; i < userId.length; ++i) {
            if (i > 0) {
                signInfo = signInfo + " ,";
            }

            signInfo = signInfo + "{\"userId\":\"" + userId[i] + "\",\"position\":\"" + postion[i] + "\",\"signUiType\":\"1\" } ";
        }

        signInfo = signInfo + "]";
        String md5str = appId + "&" + orderId + "&" + positionChar + "&" + signInfo + "&" + time + "&" + appSecretKey;
        String sign = com.yimao.cloud.base.utils.StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "sign", "signType", "orderId", "time", "positionChar", "signInfo"};
        String[] val = new String[]{appId, sign, signType, orderId, time, positionChar, signInfo};

        try {
            return YunSignCaller.callYunSign(domainProperties.getYunSign() + "/mmecserver3.0/webservice/Addition?wsdl", "addSignInfo", strs, val);
        } catch (Exception var14) {
            return YunSignResult.err(var14.getMessage());
        }
    }

    private YunSignResult createContract(String userId, String orderId, String customsId, String title, String templateId, String data) {
        String signType = "MD5";
        String appId = yunSignProperties.getWorkorderServiceAppid();
        String appSecretKey = yunSignProperties.getWorkorderServiceKey();
        String time = System.currentTimeMillis() + "";
        String offerTime = YunSignContract.offTime();
        String md5str = appId + "&" + customsId + "&" + data + "&" + offerTime + "&" + orderId + "&" + templateId + "&" + time + "&" + title + "&" + userId + "&" + appSecretKey;
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "time", "sign", "signType", "userId", "customsId", "templateId", "orderId", "title", "offerTime", "data"};
        String[] val = new String[]{appId, time, sign, signType, userId, customsId, templateId, orderId, title, offerTime, data};
        try {
            YunSignResult result = YunSignCaller.callYunSign(domainProperties.getYunSign() + YunSignConfig.URI, "createContract", strs, val);
            if (result.isSuccess()) {
                return result;
            }
            return queryContract(orderId);
        } catch (Exception e) {
            return YunSignResult.err(e.getMessage());
        }
    }

    private YunSignResult queryContract(String orderId) {
        String signType = "MD5";
        String appId = yunSignProperties.getWorkorderServiceAppid();
        String appSecretKey = yunSignProperties.getWorkorderServiceKey();
        String time = System.currentTimeMillis() + "";
        String md5str = appId + "&" + orderId + "&" + time + "&" + appSecretKey;
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        //对应参数的节点
        String[] strs = new String[]{"appId", "sign", "signType", "orderId", "time"};
        //参数值
        String[] val = new String[]{appId, sign, signType, orderId, time};
        try {
            return YunSignCaller.callYunSign(domainProperties.getYunSign() + YunSignConfig.URI, "queryContract", strs, val);
        } catch (Exception e) {
            return YunSignResult.err(e.getMessage());
        }
    }
}
