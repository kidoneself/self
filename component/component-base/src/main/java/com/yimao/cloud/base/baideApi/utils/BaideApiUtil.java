package com.yimao.cloud.base.baideApi.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yimao.cloud.base.baideApi.constant.Constant;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.HaidaBase64;
import com.yimao.cloud.pojo.vo.out.StockPutVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BaideApiUtil {
    private static final String DISTRIBUTOR_ALLOT = "1";
    private static final String CONTRACT_SIGNTYPE_APP = "1";

    //同步售后安装工信息类型
    public static final String INSERT = "1";
    public static final String DELETE = "2";
    public static final String UPDATE = "3";

    public static final String BAIDE_SUCCECC_CODE = "00000000";   
    public static boolean success(Map map) {
        return map != null && map.size() > 0 && map.containsKey("code") && BAIDE_SUCCECC_CODE.equals(map.get("code").toString());
    }

    public BaideApiUtil() {
    }

    public static Map<String, Object> add(String customerId, String customerName, String customerPhone, int rewardAmount, String latestTime, String province, String city, String region, String address, String orderId, String workId, String dealerId, String dealerName, String dealerPhone, String isassign, String engineerId, String productModel, String productModelName, String chargingType, String chargingTypeName, int payterminal, double accountFee, double payFee, int count, String remark, String isExperience, String dealerIdCard, String dealerAccount, String dealerRole, String childDealerId, String childDealerName, String childDealerAccount, String scanCodeType, boolean isOldOrder, boolean isUserOrder, String isPay, Date payDate, String paymentMeans, String tradeNo) throws Exception {
        if (isOldOrder) {
            return null;
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("customerId", customerId);
            params.put("customerName", customerName);
            params.put("customerPhone", customerPhone);
            if (rewardAmount != 0) {
                params.put("rewardAmount", MoneyUtil.getDoubleMoney(rewardAmount));
                params.put("latestTime", latestTime);
            }

            params.put("provinceName", province);
            params.put("cityName", city);
            params.put("areaName", region);
            params.put("address", address);
            params.put("orderId", orderId);
            params.put("placeDate", DateUtil.getDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            params.put("dealerId", dealerId);
            params.put("dealerName", dealerName);
            params.put("dealerPhone", dealerPhone);
            // if (!StringUtil.isEmpty(isassign)) {
            //     params.put("isassign", "0");
            //     if ("1".equals(isassign)) {
            //         params.put("engineerId", engineerId);
            //         params.put("isassign", "1");
            //     }
            // } else {
            //     params.put("isassign", "0");
            // }
            params.put("isassign", "1");
            params.put("engineerId", engineerId);

            params.put("productModel", productModel);
            params.put("productModelName", productModelName);
            params.put("chargingType", chargingType);
            params.put("chargingTypeName", chargingTypeName);
            params.put("payStyle", payterminal);
            params.put("payStatus", isPay);//同步售后支付状态
            if (accountFee == 0.0D) {
                params.put("accountFee", 0);
            } else {
                params.put("accountFee", accountFee);
            }

            params.put("workId", workId);
            if (payFee == 0.0D) {
                params.put("payFee", 0);
            } else {
                params.put("payFee", payFee);
            }

            params.put("goodsNum", count);
            params.put("remark", remark);
            params.put("isExperience", isExperience);
            params.put("dealerIdCard", dealerIdCard);
            params.put("dealerAccount", dealerAccount);
            params.put("dealerRole", dealerRole);
            params.put("childDealerId", childDealerId);
            params.put("childDealerName", childDealerName);
            params.put("childDealerAccount", childDealerAccount);
            params.put("scanCodeType", scanCodeType);
            params.put("isOldOrder", isOldOrder);
            params.put("isUserOrder", isUserOrder);
            
            //设置支付类型和支付时间
            if(null!=payDate){
            	params.put("payDate", DateUtil.dateToString(payDate));
            }
            if(!StringUtil.isEmpty(paymentMeans)){
            	params.put("paymentMeans", paymentMeans);
            }
            log.info("传参=" + JSON.toJSONString(params));
            String result = GetDataUtil.post(params, Constant.ADD_WORKORDER);
            Map<String, Object> data = GetDataUtil.getData(result);
            log.info("创建工单同步百得结果：{}---" + data, orderId);
            return data;
        }
    }

    public static Map<String, Object> sync(String workorderInfo) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("workorderInfo", workorderInfo);
        String result = GetDataUtil.post(params, Constant.SYNC_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("同步工单数据到售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> delete(String code, String deleteReason, String deleteRemark) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("deleteReason", deleteReason);
        params.put("deleteRemark", deleteRemark);
        String result = GetDataUtil.post(params, Constant.DELETE_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> syncEngineerAddOrUpdate(String type, String id, String province, String city, String region, String siteId, String siteName, String idCard, String sex, String forbidden, String loginName, String realName, String phone) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("id", id);
        params.put("province", province);
        params.put("city", city);
        params.put("region", region);
        params.put("siteId", siteId);
        params.put("siteName", siteName);
        params.put("idCard", idCard);
        params.put("sex", sex);
        params.put("forbidden", forbidden);
        params.put("loginName", loginName);
        params.put("realName", realName);
        params.put("phone", phone);
        String result = GetDataUtil.post(params, Constant.SYNC_ENGINEER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> syncEngineerDelete(String type, String id) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("id", id);
        String result = GetDataUtil.post(params, Constant.SYNC_ENGINEER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> syncServiceSiteAddOrUpdate(String type, String id, String name, String legalName, String legalMobile, String legalCardNo, String province, String city, String region, String address) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("id", id);
        params.put("name", name);
        params.put("legalName", legalName);
        params.put("legalMobile", legalMobile);
        params.put("legalCardNo", legalCardNo);
        params.put("province", province);
        params.put("city", city);
        params.put("area", region);
        params.put("address", address);
        String result = GetDataUtil.post(params, Constant.SYNC_SERVICESITE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> syncServiceSiteDelete(String type, String id) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("id", id);
        String result = GetDataUtil.post(params, Constant.SYNC_SERVICESITE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> syncProductAddOrUpdate(String type, String id, String productTypeId, String productScopeId, String name, int productFlag) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("id", id);
        params.put("productTypeId", productTypeId);
        params.put("productScopeId", productScopeId);
        params.put("name", name);
        params.put("productFlag", productFlag);
        String result = GetDataUtil.post(params, Constant.SYNC_PRODUCT);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> syncProductDelete(String type, String id) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("id", id);
        String result = GetDataUtil.post(params, Constant.SYNC_PRODUCT);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> addMaintenance(String batchCode, String sncode, String simcard, String province, String city, String region, String address, String productModel, String productModelName, String chargeType, String chargeTypeName, String customerId, String customerName, String customerPhone, String distributorId, String distributorName, String distributorPhone, String engineerId, String remark, String oldInstallId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("batchCode", batchCode);
        params.put("snCode", sncode);
        params.put("ICCID", simcard);
        params.put("isassign", "1");
        params.put("productModel", productModel);
        params.put("productModelName", productModelName);
        params.put("provinceName", province);
        params.put("cityName", city);
        params.put("areaName", region);
        params.put("address", address);
        params.put("customerId", customerId);
        params.put("customerName", customerName);
        params.put("customerPhone", customerPhone);
        params.put("dealerId", distributorId);
        params.put("dealerName", distributorName);
        params.put("dealerPhone", distributorPhone);
        params.put("chargingType", chargeType);
        params.put("chargingTypeName", chargeTypeName);
        params.put("engineerId", engineerId);
        params.put("remark", remark);
        params.put("oldInstallId", oldInstallId);
        String result = GetDataUtil.post(params, Constant.MAINTENANCE_WORKORDER_ADD);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> list(String engineerId, String orderType, String state, String completeType, int page, int pageSize) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("engineerId", engineerId);
        if (!StringUtil.isEmpty(orderType)) {
            params.put("orderType", orderType);
        }

        params.put("status", state);
        if (!StringUtil.isEmpty(completeType)) {
            params.put("back", completeType);
        }

        params.put("page", page);
        params.put("pageSize", pageSize);
        String result = GetDataUtil.post(params, Constant.LIST_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> handingList(String engineerId, String orderType, String detailStatus, int page, int pageSize) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("engineerId", engineerId);
        if (!StringUtil.isEmpty(orderType)) {
            params.put("orderType", orderType);
        }

        params.put("detailStatus", detailStatus);
        params.put("page", page);
        params.put("pageSize", pageSize);
        String result = GetDataUtil.post(params, Constant.HANDING_LIST_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> detail(String code) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        String result = GetDataUtil.post(map, Constant.DETAIL_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> confirmIdCard(String code) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        String result = GetDataUtil.post(map, Constant.CONFIRM_IDCARD_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> accept(String code) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        String result = GetDataUtil.post(params, Constant.ACCEPT_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> assign(String code, String isassign, String engineerId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("isassign", isassign);
        if (!StringUtil.isEmpty(engineerId)) {
            params.put("engineerId", engineerId);
        }

        String result = GetDataUtil.post(params, Constant.ASSIGN_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> editWorkOrder(String code, String customerPhone, String province, String city, String region, String address, String isassign, String engineerId, String productModel, String chargeType, String chargeTypeName, String payterminal) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        if (!StringUtil.isEmpty(customerPhone)) {
            params.put("customerPhone", customerPhone);
        }

        if (!StringUtil.isEmpty(province)) {
            params.put("provinceName", province);
        }

        if (!StringUtil.isEmpty(city)) {
            params.put("cityName", city);
        }

        if (!StringUtil.isEmpty(region)) {
            params.put("areaName", region);
        }

        if (!StringUtil.isEmpty(address)) {
            params.put("address", address);
        }

        if (!StringUtil.isEmpty(isassign)) {
            params.put("isassign", isassign);
        }

        if (!StringUtil.isEmpty(engineerId)) {
            params.put("engineerId", engineerId);
        }

        if (!StringUtil.isEmpty(productModel)) {
            params.put("productModel", productModel);
        }

        if (!StringUtil.isEmpty(chargeType)) {
            params.put("chargingType", chargeType);
        }

        if (!StringUtil.isEmpty(chargeTypeName)) {
            params.put("chargingTypeName", chargeTypeName);
        }

        if (!StringUtil.isEmpty(payterminal)) {
            params.put("payStyle", payterminal);
        }

        String result = GetDataUtil.post(params, Constant.EDIT_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> reject(String code, String reason) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("refuseReason", reason);
        String result = GetDataUtil.post(params, Constant.REJECT_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> appointment(String code, String planServiceDate, String address, String remark) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("planServiceDate", planServiceDate);
        params.put("address", address);
        params.put("engineerRemark", remark);
        String result = GetDataUtil.post(params, Constant.APPOINTMENT_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> startWorkOrder(String code, String ismunicipal, String tds, String hydraulic, String otherSource) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("ismunicipal", ismunicipal);
        params.put("tds", tds);
        params.put("hydraulic", hydraulic);
        if (!StringUtil.isEmpty(otherSource)) {
            params.put("otherSource", otherSource);
        }

        String result = GetDataUtil.post(params, Constant.START_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> changeTypeAndModel(String code, String productModel, String costId, String costName, double payFee, double accountFee) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("productModel", productModel);
        params.put("chargingType", costId);
        params.put("chargingTypeName", costName);
        params.put("payFee", String.valueOf(payFee));
        params.put("accountFee", String.valueOf(accountFee));
        log.info("调用售后系统接口同步数据：{}", JSON.toJSONString(params));
        String result = GetDataUtil.post(params, Constant.CHANGETYPE_AND_MODEL_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> supplierName(String batchCode) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("batchCode", batchCode);
        String result = GetDataUtil.post(params, Constant.SUPPLIER_PRODUCT);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> batchCode(String code, String batchCode) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("batchCode", batchCode);
        String result = GetDataUtil.post(params, Constant.BATCH_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> sncode(String code, String sncode) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("sncode", sncode);
        String result = GetDataUtil.post(params, Constant.SNCODE_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> simCard(String code, String simcard) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("iccid", simcard);
        String result = GetDataUtil.post(params, Constant.SIMCARD_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> payment(String code, String payType, MultipartFile file1, MultipartFile file2, MultipartFile file3) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("paymentMeans", payType);
        if (file1 != null) {
            params.put("ticketPhoto", pictureData(file1, file2, file3));
        }

        String result = GetDataUtil.post(params, Constant.PAYMEMENT_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> payment(String code, String payType, String imgDatas) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("paymentMeans", payType);
        params.put("ticketPhoto", imgDatas);
        String result = GetDataUtil.post(params, Constant.PAYMEMENT_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> updatepayment(String code, String auditState, String auditReason) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("auditState", auditState);
        if (!StringUtil.isEmpty(auditReason)) {
            params.put("auditReason", auditReason);
        }

        String result = GetDataUtil.post(params, Constant.UPDATE_PAYMENT_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> saveSignTypeApp(String code, String contractType, String confirmation, String signUserName, String signUserPhone, String signUserCert, String signUserEmail, String signUserAddress, String contractYears) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("contractType", contractType);
        params.put("confirmationNumber", confirmation);
        params.put("signUserName", signUserName);
        params.put("signUserPhone", signUserPhone);
        params.put("signUserCert", signUserCert);
        params.put("signUserEmail", signUserEmail);
        params.put("signUserAddress", signUserAddress);
        params.put("contractYears", contractYears);
        String result = GetDataUtil.post(params, Constant.CONTRACT_SIGN_TYPE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> saveSignTypeWechat(String code, String contractType, String confirmation) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("contractType", contractType);
        params.put("confirmationNumber", confirmation);
        String result = GetDataUtil.post(params, Constant.CONTRACT_SIGN_TYPE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> confirm(String code) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        String result = GetDataUtil.post(params, Constant.CONFIRM_USERINFO_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    private static String pictureData(MultipartFile file1, MultipartFile file2, MultipartFile file3) throws Exception {
        InputStream in = file1.getInputStream();
        byte[] data = new byte[in.available()];
        in.read(data);
        in.close();
        byte[] data2 = null;
        byte[] data3 = null;
        InputStream in3;
        if (null != file2) {
            in3 = file2.getInputStream();
            data2 = new byte[in3.available()];
            in3.read(data2);
            in3.close();
        }

        if (null != file3) {
            in3 = file3.getInputStream();
            data3 = new byte[in3.available()];
            in3.read(data3);
            in3.close();
        }

        String photoData = HaidaBase64.encode(data);
        if (null != data2) {
            photoData = photoData + "," + HaidaBase64.encode(data2);
        }

        if (null != data3) {
            photoData = photoData + "," + HaidaBase64.encode(data3);
        }

        return photoData;
    }

    public static Map<String, Object> uploadPicture(String code, MultipartFile file1, MultipartFile file2, MultipartFile file3) throws Exception {
        String photoData = pictureData(file1, file2, file3);
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("photoData", photoData);
        String result = GetDataUtil.post(params, Constant.PHOTO_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> changePlanDate(String code, String planDate) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("planServiceDate", planDate);
        String result = GetDataUtil.post(params, Constant.CHANGEDATE_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> editInformation(String code, String batchCode, int type, String sncode, String simcard, String ismunicipal, String tds, String hydrulic, String otherSource) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("batchCode", batchCode);
        params.put("type", type);
        params.put("sncode", sncode);
        params.put("simcard", simcard);
        params.put("ismunicipal", ismunicipal);
        params.put("tds", tds);
        params.put("hydraulic", hydrulic);
        params.put("otherSource", otherSource);
        String result = GetDataUtil.post(params, Constant.EDIT_INFORMATION);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> backOrder(String code, String reason, String remark) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("cancellationReason", reason);
        params.put("cancellationRemark", remark);
        String result = GetDataUtil.post(params, Constant.BACK_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> validateBackOrder(String code, String auditResult, String auditRemark) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("auditResult", auditResult);
        params.put("auditRemark", auditRemark);
        String result = GetDataUtil.post(params, Constant.AGREE_BACK_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> invoice(String orderId, String invoiceNum) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        params.put("fpqqlsh", invoiceNum);
        String result = GetDataUtil.post(params, Constant.INVOICE_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> changeInvoiceStatus(String workCode) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", workCode);
        String result = GetDataUtil.post(params, Constant.INVOICE_CHANGE_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> saveInvoice(String code, String invoiceType, String invoiceTitle, String invoiceCompany, String invoiceBank, String invoiceBankNum, String invoiceAddress, String invoicePhone, String invoiceTax, String invoiceLicence, String invoiceTime, boolean haveInvoice) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("invoiceType", Integer.valueOf(invoiceType));
        params.put("invoiceTitle", Integer.valueOf(invoiceTitle));
        params.put("invoiceCompany", invoiceCompany);
        params.put("invoiceBank", invoiceBank);
        params.put("invoiceBankNum", invoiceBankNum);
        params.put("invoiceAddress", invoiceAddress);
        params.put("invoicePhone", invoicePhone);
        params.put("invoiceTax", invoiceTax);
        params.put("invoiceLicence", invoiceLicence);
        params.put("invoiceTime", invoiceTime);
        params.put("haveInvoice", haveInvoice);
        String result = GetDataUtil.post(params, Constant.SAVE_INVOICE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> validateBatchCode(String sncode, String batchCode, String typeCode) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("snCode", sncode);
        params.put("batchCode", batchCode);
        params.put("typeCode", typeCode);
        String result = GetDataUtil.post(params, Constant.VALIDATE_BATCHCODE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> changeSncodeTime(String code, String sncodeTime, String iccid) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("sncodeTime", sncodeTime);
        params.put("iccid", iccid);
        String result = GetDataUtil.post(params, Constant.CHANGE_SNCODE_TIME);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> finishWorkOrderInstall(String code) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        String result = GetDataUtil.post(params, Constant.COMPLETE_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> count(String engineerId, String orderType) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("engineerId", engineerId);
        if (!StringUtil.isEmpty(orderType)) {
            params.put("orderType", orderType);
        }

        String result = GetDataUtil.post(params, Constant.COUNT_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> verifyOpenRepairAuthorize() throws Exception {
        Map<String, Object> params = new HashMap<>();
        String result = GetDataUtil.post(params, Constant.CREATE_REPAIR_AUTHORIZE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> ranking(String engineerId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("engineerId", engineerId);
        String result = GetDataUtil.post(params, Constant.ENGINEER_RANKING);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> stock(String engineerId, String materielId, String materielCode) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("engineerId", engineerId);
        params.put("materielId", materielId);
        params.put("materielCode", materielCode);
        String result = GetDataUtil.post(params, Constant.ENGINEER_STOCK);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> engineerStockMateriel(String engineerId, Integer materielType) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("engineerId", engineerId);
        params.put("materielType", materielType);
        String result = GetDataUtil.post(params, Constant.ENGINEER_STOCK_MATERIEL);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> serviceSiteProductStock(String serviceId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("serviceId", serviceId);
        String result = GetDataUtil.post(params, Constant.SERVICESITE_PRODUCT_STOCK);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> serviceSiteMaterielStock(String serviceId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("serviceId", serviceId);
        String result = GetDataUtil.post(params, Constant.SERVICESITE_MATERIEL_STOCK);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> serviceSiteEngineerProductStock(String serviceId, String engineerId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("serviceId", serviceId);
        params.put("engineerId", engineerId);
        String result = GetDataUtil.post(params, Constant.SERVICESITE_ENGINERR_PRODUCT_STOCK);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> serviceEngineerConsumableForstation(String serviceId, String engineerId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("serviceId", serviceId);
        params.put("engineerId", engineerId);
        String result = GetDataUtil.post(params, Constant.SERVICESITE_ENGINEER_CONSUMABLE_FOR_STATION);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> engineerApplyList(String engineerId, String applyStatus, String directStatus) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("engineerId", engineerId);
        params.put("applyStatus", applyStatus);
        params.put("type", directStatus);
        String result = GetDataUtil.post(params, Constant.ENGINEER_APPLY_LIST);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> engineerApplyOperate(String serialNo, Integer status) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("serialNo", serialNo);
        params.put("status", status);
        String result = GetDataUtil.post(params, Constant.ENGINEER_APPLY_OPERATE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> materielApply(String serviceStationId, String engineerId, String materielApplyList) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("serviceStationId", serviceStationId);
        params.put("engineerId", engineerId);
        params.put("materielApplyList", materielApplyList);
        String result = GetDataUtil.post(params, Constant.MATERIEL_APPLY);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> materielAllot(String allotFromId, String allotToId, String allotList) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("allotFromId", allotFromId);
        params.put("allotToId", allotToId);
        params.put("allotList", allotList);
        String result = GetDataUtil.post(params, Constant.ENGINEER_MATERIEL_ALLOT);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> engineerPutStock(Integer type, String engineerId, String InputFromId, List<StockPutVo> epsList, String allotId, String materielType) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("engineerId", engineerId);
        params.put("inputFromId", InputFromId);
        params.put("epsList", epsList);
        params.put("allotId", allotId);
        params.put("materielType", materielType);
        String result = GetDataUtil.post(params, Constant.ENGINEER_PUT_STOCK);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> materielStatus(String batchCodes) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("batchCodes", batchCodes);
        String result = GetDataUtil.post(params, Constant.MATERIEL_STATUS);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> materielStockList(String materielId, String stockOwnFlag, String stockOwnId, String status, String isFrozen, String page) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("materielId", materielId);
        params.put("stockOwnFlag", stockOwnFlag);
        params.put("stockOwnId", stockOwnId);
        params.put("status", status);
        params.put("isFrozen", isFrozen);
        params.put("page", page);
        String result = GetDataUtil.post(params, Constant.MATERIEL_STOCK_LIST);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> getEnigneerStockHistoryInfo(String engineerId, String materielType) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("engineerId", engineerId);
        params.put("materielType", materielType);
        String result = GetDataUtil.post(params, Constant.ENGINEER_STOCK_HISTORY_INFO);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> engineerMaterielStatusChange(String engineerId, String batchCode, String status) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("engineerId", engineerId);
        params.put("batchCode", batchCode);
        params.put("status", status);
        String result = GetDataUtil.post(params, Constant.ENGINEER_MATERIEL_STATUS_CHANGE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> getMaterielInfoBySnCode(String snCode) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("snCode", snCode);
        String result = GetDataUtil.post(params, Constant.MATERIEL_INFO_BY_SNCODE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> maintenanceWorkOrderAdd(String materielTypes, String snCode, String batchCode, String ICCID, String productModel, String productModelName, String provinceName, String cityName, String areaName, String customerId, String customerName, String customerPhone, String address, String dealerId, String dealerName, String dealerPhone, String dealerIdCard, String dealerAccount, String dealerRole, String childDealerId, String childDealerName, String childDealerAccount, String childIdCard, String childPhone, String chargingType, String chargingTypeName, String remark, String oldInstallId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("materielTypes", materielTypes);
        params.put("snCode", snCode);
        params.put("batchCode", batchCode);
        params.put("ICCID", ICCID);
        params.put("productModel", productModel);
        params.put("productModelName", productModelName);
        params.put("provinceName", provinceName);
        params.put("cityName", cityName);
        params.put("areaName", areaName);
        params.put("customerId", customerId);
        params.put("customerName", customerName);
        params.put("customerPhone", customerPhone);
        params.put("address", address);
        params.put("dealerId", dealerId);
        params.put("dealerName", dealerName);
        params.put("dealerPhone", dealerPhone);
        params.put("dealerIdCard", dealerIdCard);
        params.put("dealerAccount", dealerAccount);
        params.put("dealerRole", dealerRole);
        params.put("childDealerId", childDealerId);
        params.put("childDealerName", childDealerName);
        params.put("childDealerAccount", childDealerAccount);
        params.put("childIdCard", childIdCard);
        params.put("childPhone", childPhone);
        params.put("chargingType", chargingType);
        params.put("chargingTypeName", chargingTypeName);
        params.put("remark", remark);
        params.put("oldInstallId", oldInstallId);
        String result = GetDataUtil.post(params, Constant.MAINTENANCE_WORKORDER_ADD);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> maintenanceWorkOrderStartServer(String code) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        String result = GetDataUtil.post(params, Constant.START_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> maintenanceWorkOrderScanBatchCode(String orderId, String batchCode) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", orderId);
        params.put("batchCodes", batchCode);
        String result = GetDataUtil.post(params, Constant.MAINTENANCE_WORKORDER_SCANBATCHCODE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> mustUploadImg(String orderId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", orderId);
        String result = GetDataUtil.post(params, Constant.MAINTENANCE_WORKORDER_MUSTUPLOADIMG);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> maintenanceWorkOrderUploadMaterielImg(String orderId, MultipartFile file1, MultipartFile file2, MultipartFile file3) throws Exception {
        InputStream in1 =null;
        InputStream in2=null;
        InputStream in3=null;
        byte[] data = null;
        byte[] data2 = null;
        byte[] data3 = null;
        String photoData =null;
        try {
            if (null != file1) {
                in1 = file1.getInputStream();
                data = new byte[in1.available()];
                in1.read(data);
            }
            if (null != file2) {
                in2 = file2.getInputStream();
                data2 = new byte[in2.available()];
                in2.read(data2);
            }
            if (null != file3) {
                in3 = file3.getInputStream();
                data3 = new byte[in3.available()];
                in3.read(data3);
            }

            if (null != data) {
                photoData = HaidaBase64.encode(data);
            }
            if (null != data2) {
                photoData = photoData + ";," + HaidaBase64.encode(data2);
            }
            if (null != data3) {
                photoData = photoData + ";," + HaidaBase64.encode(data3);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            if(in1 != null){
                in1.close();
            }
            if(in2 != null){
                in2.close();
            }
            if(in3 != null){
                in3.close();
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("code", orderId);
        params.put("photoData", photoData);
        String result = GetDataUtil.post(params, Constant.MAINTENANCE_WORKORDER_UPLOAD_MATERIEL_IMG);
        Map<String, Object> data4 = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data4);
        return data4;
    }

    public static Map<String, Object> maintenanceWorkOrderFinish(String maintenanceOrderId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", maintenanceOrderId);
        String result = GetDataUtil.post(params, Constant.COMPLETE_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> updateDealerAndChildDealer(String code, String dealerId, String dealerName, String dealerPhone, String dealerIdCard, String dealerAccount, String dealerRole, String childDealerId, String childDealerName, String childDealerAccount, String childIdCard, String childPhone) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("dealerId", dealerId);
        params.put("dealerName", dealerName);
        params.put("dealerPhone", dealerPhone);
        params.put("dealerIdCard", dealerIdCard);
        params.put("dealerAccount", dealerAccount);
        params.put("dealerRole", dealerRole);
        params.put("childDealerId", childDealerId);
        params.put("childDealerName", childDealerName);
        params.put("childDealerAccount", childDealerAccount);
        params.put("childIdCard", childIdCard);
        params.put("childPhone", childPhone);
        String result = GetDataUtil.post(params, Constant.REPAIR_WORKORDER_UPDATE_DEALERINFO);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> repairWorkOrderAdd(String customerId, String customerName, String customerPhone,
                                                         String provinceName, String cityName, String areaName, String address, String sncode,
                                                         String batchCode, String iccid, String dealerId, String dealerName, String dealerPhone,
                                                         String dealerIdCard, String dealerAccount, String dealerRole, String childDealerId,
                                                         String childDealerName, String childDealerAccount, String childIdCard, String childPhone,
                                                         String chargingType, String productModel, String productModelName, String faultDescribe,
                                                         String remarks, String createBy, String bespeakTime, List faultInfo) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("customerId", customerId);
        params.put("customerName", customerName);
        params.put("customerPhone", customerPhone);
        params.put("provinceName", provinceName);
        params.put("cityName", cityName);
        params.put("areaName", areaName);
        params.put("address", address);
        params.put("batchCode", batchCode);
        params.put("sncode", sncode);
        params.put("iccid", iccid);
        params.put("dealerId", dealerId);
        params.put("dealerName", dealerName);
        params.put("dealerPhone", dealerPhone);
        params.put("dealerIdCard", dealerIdCard);
        params.put("dealerAccount", dealerAccount);
        params.put("dealerRole", dealerRole);
        params.put("childDealerId", childDealerId);
        params.put("childDealerName", childDealerName);
        params.put("childDealerAccount", childDealerAccount);
        params.put("childIdCard", childIdCard);
        params.put("childPhone", childPhone);
        params.put("chargingType", chargingType);
        params.put("productModel", productModel);
        params.put("productModelName", productModelName);
        params.put("faultDescribe", faultDescribe);
        params.put("remarks", remarks);
        params.put("createBy", createBy);
        params.put("bespeakTime", bespeakTime);
        params.put("faultInfo", JSONArray.toJSONString(faultInfo));
        String result = GetDataUtil.post(params, Constant.REPAIR_WORKORDER_ADD);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> repairWorkOrderStartServer(String code) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        String result = GetDataUtil.post(params, Constant.START_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map repairWorkOrderFactFaultDescribe(String repairOrderId, List<Map<String, Object>> malfunction, JSONArray materielInfo) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", repairOrderId);
        params.put("malfunction", JSONArray.toJSONString(malfunction));
        params.put("result", JSONArray.toJSONString(materielInfo));
        String result = GetDataUtil.post(params, Constant.REPAIR_WORKORDER_FACTFAULTDESCRIBE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map repairWorkOrderSaveMaterielInfo(String workCode, List materielInfo) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("orderCode", workCode);
        params.put("result", materielInfo);
        String result = GetDataUtil.post(params, Constant.REPAIR_WORKORDER_SAVE_MATERIELINFO);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map repairWorkOrderApplyDevice(String workCode) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", workCode);
        String result = GetDataUtil.post(params, Constant.REPAIR_WORKORDER_APPLY_EXCHANGE_DEVICE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> repairWorkOrderFinish(String workCode) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", workCode);
        String result = GetDataUtil.post(params, Constant.COMPLETE_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> repairWorkOrderFactFaultDescribeList() throws Exception {
        Map<String, Object> params = new HashMap<>();
        String result = GetDataUtil.post(params, Constant.REPAIR_WORKORDER_FACTFAULTDESCRIBE_LIST);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> repairWorkOrderFaultThirdTree() throws Exception {
        Map<String, Object> params = new HashMap<>();
        String result = GetDataUtil.post(params, Constant.REPAIR_WORKORDER_QUERY_FAULTPHENOMENON);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> repairWorkOrderFaultSecondTree() throws Exception {
        Map<String, Object> params = new HashMap<>();
        String result = GetDataUtil.post(params, Constant.REPAIR_WORKORDER_QUERY_FAULTTYPE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> repairWorkOrderMaterielDataList() throws Exception {
        Map<String, Object> params = new HashMap<>();
        String result = GetDataUtil.post(params, Constant.REPAIR_WORKORDER_MATERIEL_DATA_LIST);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> getMaterielTreeByType() throws Exception {
        String result = GetDataUtil.post((Map) null, Constant.REPAIR_WORKORDER_MATERIEL_DATA_TREE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> repairWorkOrderQueryByRepairWorkOrderId(String workCode) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("repairWorkOrderId", workCode);
        String result = GetDataUtil.post(params, Constant.REPAIR_WORKORDER_QUERY_BY_REPAIRID);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> repairWorkOrderPutNewDeviceSnAndBcAndIc(String workcode, String sncode, String batchCode, String iccid) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("code", workcode);
        params.put("batchCode", batchCode);
        params.put("sncode", sncode);
        params.put("iccid", iccid);
        String result = GetDataUtil.post(params, Constant.REPAIR_WORKORDER_PUT_NEWDEVICE_SN_BA_IC);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> repairWorkOrderInsertSystemTip(String sncode, String serviceSiteId, String serviceSiteName, String faultType, String faultInfo, String engineerId, String engineerName, String consumerId, String consumerName, String consumerPhone) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("sncode", sncode);
        params.put("serviceSiteId", serviceSiteId);
        params.put("serviceSiteName", serviceSiteName);
        params.put("faultType", faultType);
        params.put("faultInfo", faultInfo);
        params.put("engineerId", engineerId);
        params.put("engineerName", engineerName);
        params.put("consumerId", consumerId);
        params.put("consumerName", consumerName);
        params.put("consumerPhone", consumerPhone);
        String result = GetDataUtil.post(params, Constant.REPAIR_WORKORDER_INSERT_SYSTEM_TIP);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> repairWorkOrderInsertSystemTip(Map<String, Object> params) throws Exception {
        String result = GetDataUtil.post(params, Constant.REPAIR_WORKORDER_INSERT_SYSTEM_TIP);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> workOrderDecisionRule() throws Exception {
        Map<String, Object> params = new HashMap<>();
        String result = GetDataUtil.post(params, Constant.WORKORDER_DECISION_RULE_INFO);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> syncComplete(Map<String, Object> params) throws Exception {
        String result = GetDataUtil.post(params, Constant.SYNC_COMPLETE_WORKORDER);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> engineerDimission(String quondamEngineerId, String assignedEngineerId, String operationUserName) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("quondamEngineerId", quondamEngineerId);
        params.put("assignedEngineerId", assignedEngineerId);
        params.put("operationUserName", operationUserName);
        String result = GetDataUtil.post(params, Constant.ENGINEER_DIMISSION);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> renewOrderSync(Map<String, Object> paramMap) throws Exception {
    	 String result = GetDataUtil.post(paramMap, Constant.RENEW_WORKORDER_ADD);
         Map<String, Object> data = GetDataUtil.getData(result);
         log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }

    public static Map<String, Object> renewOrderApplyInvoice(Map<String, Object> paramMap) throws Exception {
        String result = GetDataUtil.post(paramMap, Constant.RENEW_WORKORDER_APPLY_INVOICE);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
        return data;
    }
    
    public static String pictureEncodeData(MultipartFile [] files) throws Exception {
        if(null==files||files.length==0){
        	return "";
        }
        String photoData="";
        for(MultipartFile file:files){
	    	InputStream in = file.getInputStream();
	        byte[] data = new byte[in.available()];
	        in.read(data);
	        in.close();
	        photoData = photoData + ";," + HaidaBase64.encode(data);//百得以;,分割
	       
        }
        return photoData;
    }
    
    public static Map<String, Object> renewOrderUpdateMoney(Map<String, Object> paramMap) throws Exception {
   	 String result = GetDataUtil.post(paramMap, Constant.RENEW_WORKORDER_UPDATE_MONEY);
        Map<String, Object> data = GetDataUtil.getData(result);
        log.info("调用售后系统接口返回结果：{}", data);
       return data;
   }
}
