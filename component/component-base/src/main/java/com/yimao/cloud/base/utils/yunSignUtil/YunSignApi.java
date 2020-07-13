package com.yimao.cloud.base.utils.yunSignUtil;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class YunSignApi {

    @Resource
    private YunSignContract yunSignContract;
    @Resource
    private YunSignRegister yunSignRegister;

    public YunSignApi() {
    }

    public YunSignResult checkidentityNumber(String identityNumber, String userName) {
        String query = "checkidentityNumber_" + identityNumber + "_" + userName;
        YunSignResult result = YunSignQueryCache.getCache(query);
        if (null == result) {
            result = yunSignRegister.checkidentityNumber(identityNumber, userName);
            YunSignQueryCache.cacheQuery(query, result);
        }

        return result;
    }

    public YunSignResult registerPersonalUser(String identityNumber, String userName, String mobile, String email, String userId, boolean isAdmin) {
        YunSignResult queryResult = checkUserExisted(userId);
        return queryResult.isSuccess() ? queryResult : yunSignRegister.registerPersonalUser(identityNumber, userName, mobile, email, userId, isAdmin);
    }

    public YunSignResult registerCommpanyUser(String licenseNo, String companyName, String identityNumber, String userName, String mobile, String email, String userId, boolean isAdmin) {
        YunSignResult queryResult = checkUserExisted(userId);
        return queryResult.isSuccess() ? queryResult : yunSignRegister.registerCompanyUser(licenseNo, companyName, identityNumber, userName, mobile, email, userId, isAdmin);
    }

    public YunSignResult checkUserExisted(String userId) {
        return yunSignRegister.checkUserExisted(userId);
    }

    public YunSignResult createContract(String userId, String orderId, String customsId, String title, String templateId, String data) {
        return yunSignContract.createContract(userId, orderId, customsId, title, templateId, data);
    }

    public YunSignResult autoSignContract(String userId, String orderId) {
        YunSignResult result = yunSignContract.autoSignContract(userId, orderId);
        return result.getCode().equals("4201") ? YunSignResult.success(result.getDesc(), result.getResultData()) : result;
    }

    public YunSignResult queryContract(String orderId) {
        return yunSignContract.queryContract(orderId);
    }

    public YunSignResult addUserSignInfo(String[] userId, String orderId, String[] postion) {
        return yunSignContract.addUserSignInfo(userId, orderId, postion);
    }

    // public static void main(String[] args) {
    //     String licenseNo = "91310114MA1GTR2M8M";
    //     String companyName = "实业发展（上海）有限公司";
    //     String identityNumber = "341226199201152962";
    //     String userName = "包秀明";
    //     String mobile = "13661858188";
    //     String email = "46546fds1@163.com";
    //     String userId = "57184fcd22d91e6cb8a83e215";
    //     boolean isAdmin = true;
    //     YunSignResult yunSignResult = yunSignRegister.registerCompanyUser(licenseNo, companyName, identityNumber, userName, mobile, email, userId, isAdmin);
    //     System.out.println(yunSignResult.getResultData());
    // }
}
