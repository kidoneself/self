package com.yimao.cloud.out.controller.webapi;

import com.google.common.collect.Maps;
import com.yimao.cloud.base.baideApi.constant.Constant;
import com.yimao.cloud.base.baideApi.utils.GetDataUtil;
import com.yimao.cloud.base.baideApi.utils.StringUtil;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.properties.YunSignProperties;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignApi;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignCaller;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignConfig;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignResult;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.service.WorkOrderApi;
import com.yimao.cloud.out.utils.ResultBean;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Liu long jie
 * @Date 2019/10/14
 */
@Controller
@Slf4j
public class YunSignController {

    @Resource
    private YunSignApi yunSignApi;

    @Resource
    private UserFeign userFeign;

    @Resource
    private OrderFeign orderFeign;

    @Resource
    private WorkOrderApi workOrderApi;

    @Resource
    private YunSignProperties yunSignProperties;

    @Resource
    private DomainProperties domainProperties;

//    @Resource
//    private RedServiceApi redServiceApi;
//    @Resource
//    private WaterDeviceWorkorderDispatchService waterDeviceWorkorderDispatchService;

    public YunSignController() {
    }

    private String redirectTo(String url) {
        StringBuffer rto = new StringBuffer("redirect:");
        rto.append(url);
        return rto.toString();
    }

    /**
     * 云签前端回调（页面、跳转售后系统）
     */
    @RequestMapping({"/api/yunsign/contract/frontCall"})
    public String frontCall(Map map, String userId, String orderId, String status) {
        log.info("云签前端回调  userId：" + userId + " orderId: " + orderId + " status: " + status);
        log.info("userId==" + userId, new Object[0]);
        log.info("orderId==" + orderId, new Object[0]);
        log.info("status==" + status, new Object[0]);

        if (orderId.startsWith("YMC")) {
            YunSignResult result = yunSignApi.queryContract(orderId);
            if (result.isSuccess()) {
                map.put("userId", userId);
                map.put("orderId", orderId);
                map.put("status", status);
                log.info("前端回调成功");
                return "plugins/yunsign/baideContract";
            } else {
                return null;
            }
        } else {
            return this.redirectTo("api/yunsign/contract/viewContract/signSuccess");
        }
    }

    /**
     * 云签回调，经销商合同
     */
    @PostMapping({"/api/distributor/quickOnline/backCall"})
//    @ApiOperation(value = "云签回调，修改合同签署状态 ")
//    @ResponseBody
    public Map<String, Object> yunSignBackCall(String info) {
        log.info("云签后台回调：" + info, new Object[0]);
        userFeign.backCall(info);
        log.info("回调结束");
        return null;
    }

    /**
     * 安装工签署云签合同回调
     */
    @PostMapping({"/api/yunsign/contract/backCall"})
//    @ApiOperation(value = "云签回调，工单 ")
//    @ResponseBody
    public Map<String, Object> backCall(String info) {
        log.info("云签后台回调：{}", info);
        JSONObject json = JSONObject.fromObject(URLDecoder.decode(info));

        String orderId = json.getString("orderId");
        String userId = json.getString("userId");
        String signer = json.getString("signer");
        String status = json.getString("status");
        String updateTime = json.getString("updateTime");
        String syncOrderId = json.getString("syncOrderId");

        Map<String, Object> data = Maps.newHashMap();
        String appSecretKey = yunSignProperties.getWorkorderServiceKey();
        String md5 = orderId + "&" + signer + "&" + status + "&" + updateTime + "&" + userId + "&" + syncOrderId + "&" + appSecretKey;
        String callbackCheck = json.getString("callbackCheck");
        String check = StringUtil.encodeMD5(md5).toLowerCase();
        log.info("call back check = " + callbackCheck + "   ==  " + check);

        if (YunSignConfig.YUNSIGN_SERVICE_YIMAO_ACCOUNT.equals(userId)) {
            return null;
        } else {

            if (Objects.equals(check, callbackCheck)) {
                log.info("云签回调合法！  orderId===" + orderId);
                String name = "";
                String code = "";
                WorkOrderDTO workOrder = orderFeign.getWorkOrderBySignOrderId(orderId);
                if (workOrder != null) {
                    name = workOrder.getSignUserName();
                    code = workOrder.getId();
                }
                if (orderId.startsWith("YMC")) {
                    //微信签署合同
                    YunSignResult result = queryContract(orderId);
                    if (result.isSuccess()) {
                        ResultBean<WorkOrderDTO> wechat = workOrderApi.signSuccess(code, orderId, "wechat");
                        if (wechat.isSuccess()) {
                            autoSignContract(YunSignConfig.YUNSIGN_SERVICE_YIMAO_ACCOUNT, orderId);
                        }
                        log.info("name :" + name + " code:" + code + " orderId:" + orderId);
                        this.sign(code, name, orderId);
                        workOrderApi.completeWorkOrderAfterSign(code);
                        data.put(syncOrderId, "1");
                    }
                } else {
                    //APP签署合同
                    ResultBean<WorkOrderDTO> app = workOrderApi.signSuccess(code, orderId, "app");
                    if (app.isSuccess()) {
                        autoSignContract(YunSignConfig.YUNSIGN_SERVICE_YIMAO_ACCOUNT, orderId);
                    }
                    this.sign(code, name, orderId);
                    workOrderApi.completeWorkOrderAfterSign(code);
                    data.put(syncOrderId, "1");
                }
            }
            Map<String, Object> ru = Maps.newHashMap();
            ru.put("data", data);
            return ru;
        }
    }

    private YunSignResult autoSignContract(String userId, String orderId) {
        String signType = "MD5";
        String appId = yunSignProperties.getWorkorderServiceAppid();
        String appSecretKey = yunSignProperties.getWorkorderServiceKey();
        String time = System.currentTimeMillis() + "";
        String sealId = yunSignProperties.getWorkorderServiceSealid();
        String certType = "";
        String md5str = appId + "&" + certType + "&" + orderId + "&" + sealId + "&" + time + "&" + userId + "&" + appSecretKey;
        String sign = com.yimao.cloud.base.utils.StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "time", "sign", "signType", "userId", "orderId", "sealId", "certType"};
        String[] val = new String[]{appId, time, sign, signType, userId, orderId, sealId, certType};

        try {
            return YunSignCaller.callYunSign(domainProperties.getYunSign() + "/mmecserver3.0/webservice/Common?wsdl", "sign", strs, val);
        } catch (Exception var13) {
            return YunSignResult.err(var13.getMessage());
        }
    }

    private YunSignResult queryContract(String orderId) {
        String signType = "MD5";
        String appId = yunSignProperties.getWorkorderServiceAppid();
        String appSecretKey = yunSignProperties.getWorkorderServiceKey();
        String time = System.currentTimeMillis() + "";
        String md5str = appId + "&" + orderId + "&" + time + "&" + appSecretKey;
        String sign = com.yimao.cloud.base.utils.StringUtil.encodeMD5(md5str).toLowerCase();
        String[] strs = new String[]{"appId", "sign", "signType", "orderId", "time"};
        String[] val = new String[]{appId, sign, signType, orderId, time};

        try {
            return YunSignCaller.callYunSign(domainProperties.getYunSign() + "/mmecserver3.0/webservice/Common?wsdl", "queryContract", strs, val);
        } catch (Exception var10) {
            return YunSignResult.err(var10.getMessage());
        }
    }

    /**
     * 通知百得合同签署情况
     */
    private void sign(String code, String name, String orderId) {
        String url = Constant.CONTRACT_WORKORDER;
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("signUser", name);
        params.put("isSign", "2");
        params.put("contractId", orderId);

        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(code);
        if (workOrder == null) {
            throw new NotFoundException(code + "该工单信息未找到！");
        }
        try {
            String result = GetDataUtil.post(params, url);
            Map<String, Object> map = GetDataUtil.getData(result);
            log.info("通知百得合同签署情况，返回结果为：{}", map);
            if (map != null && map.size() > 0 && "00000000".equals(map.get("code"))) {
                JSONObject obj = JSONObject.fromObject(map.get("data"));
                String nextStep = obj.getString("nextStep");
                workOrder.setNextStep(Integer.parseInt(nextStep));
                orderFeign.updateWorkOrder(workOrder);
            }
            log.info("sign == " + map, new Object[0]);
        } catch (Exception var10) {
            var10.printStackTrace();
            throw new YimaoException(code + "该工单签约异常！");
        }

    }

    /**
     * 测试用 ， 展示工单合同
     * @param map
     * @param userId
     * @param codeId
     * @return
     */
    @GetMapping({"/api/yunsign/showContract"})
    @ResponseBody
    @ApiOperation(value = "合同展示页")
    public String showContract(Map map, @RequestParam String userId, @RequestParam String codeId) {
        String appId = yunSignProperties.getWorkorderServiceAppid();
        String appSecretKey = yunSignProperties.getWorkorderServiceKey();
        String signType = "MD5";
        String time = System.currentTimeMillis() + "";
        String md5str = appId + "&" + codeId + "&" + time + "&" + userId + "&" +
                appSecretKey;
        log.info(md5str);
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        log.info(sign);
        String url = domainProperties.getYunSign() + "/mmecserver3.0/showContract.do?time=" + time + "&sign=" + sign
                + "&signType=" + signType + "&userId=" + userId + "&orderId=" + codeId + "&appId=" + appId;
        return url;
    }

    @GetMapping({"/api/yunsign/viewContract"})
    @ApiOperation(value = "合同签约预览页")
    public String viewContract(Map map, @RequestParam String userId, @RequestParam String codeId) {
        log.info("开始进入合同签约预览页");
        WorkOrderDTO workOrder = orderFeign.getWorkOrderBySignOrderId(codeId);
        if (workOrder != null && StringUtil.isNotEmpty(workOrder.getSignUserPhone())) {
            userId = workOrder.getSignUserPhone();
        }

        String appId = yunSignProperties.getWorkorderServiceAppid();
        String appSecretKey = yunSignProperties.getWorkorderServiceKey();
        String signType = "MD5";
        String isForceSeal = "Y";
        String isHandWrite = "Y";
        String isSeal = "N";
        String isSignFirst = "N";
        String validType = "VALID";
        String time = System.currentTimeMillis() + "";
        String md5str = appId + "&" + codeId + "&" + time + "&" + userId + "&" + appSecretKey;
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        String domain = domainProperties.getYunSign();
        map.put("domain", domain);
        map.put("appId", appId);
        map.put("isForceSeal", isForceSeal);
        map.put("isHandWrite", isHandWrite);
        map.put("isSeal", isSeal);
        map.put("isSignFirst", isSignFirst);
        map.put("codeId", codeId);
        map.put("time", time);
        map.put("validType", validType);
        map.put("userId", userId);
        map.put("signType", signType);
        map.put("sign", sign);
        String url = "https://www.yunsign.com/mmecserver3.0/sign.do?time=" + time + "&sign=" + sign
                + "&signType=" + signType + "&userId=" + userId + "&isHandWrite=" + isHandWrite + "&isForceSeal=" + isForceSeal
                + "&isSignFirst=" + isSignFirst + "&isSeal=" + isSeal + "&validType=" + validType + "&appId=" + appId + "&orderId=" + codeId;
        log.info(url);
        return "plugins/yunsign/contract";
    }

    @GetMapping({"/api/yunsign/contract/viewContract/signSuccess"})
    @ApiOperation(value = "合同签约成功页")
    public String viewContract() {
        return "plugins/yunsign/signSuccess";
    }
}
