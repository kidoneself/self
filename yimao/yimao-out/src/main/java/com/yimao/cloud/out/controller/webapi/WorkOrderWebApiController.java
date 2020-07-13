package com.yimao.cloud.out.controller.webapi;

import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.PayTerminal;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.out.enums.ApiStatusCode;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.feign.SystemFeign;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.service.WorkOrderApi;
import com.yimao.cloud.out.utils.ApiResult;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/3/13
 */
@RestController
@Slf4j
@Api(tags = "WorkOrderWebApiController")
public class WorkOrderWebApiController {

    @Resource
    private WorkOrderApi workOrderApi;
    @Resource
    private UserCache userCache;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserFeign userFeign;


    /**
     * 安装工app调用原微服务--安装工程师工单列表
     */
    @GetMapping(value = "/webapi/engineer/waterdevice/workorder/install/list")
    public Map<String, Object> list(HttpServletRequest request,
                                    @RequestParam(required = false) String orderType,
                                    @RequestParam int state,
                                    @RequestParam(required = false) String completeType,
                                    String search,
                                    @RequestParam int page,
                                    @RequestParam int pageSize) {
        return workOrderApi.list(request, orderType, state, completeType, search, page, pageSize);
    }

    /**
     * 安装工app调用原微服务--安装工程师处理中详细工单列表
     */
    @GetMapping(value = "/webapi/engineer/waterdevice/workorder/install/handing/list")
    public Map<String, Object> handingList(HttpServletRequest request,
                                           @RequestParam(required = false) String orderType,
                                           @RequestParam String detailStatus,
                                           @RequestParam int page,
                                           @RequestParam int pageSize,
                                           String search) {
        return workOrderApi.handingList(request, orderType, detailStatus, page, pageSize, search);
    }

    /**
     * 安装工app调用原微服务--查询工单详情
     */
    @GetMapping(value = "/webapi/engineer/waterdevice/workorder/install/detail/code")
    public Map<String, Object> detail(HttpServletRequest request, @RequestParam String code) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        if (entity == null) {
            return ApiResult.error(request, ApiStatusCode.WORKORDER_DELETED);
        } else if (!Objects.equals(entity.getEngineerId(), engineerId)) {
            return ApiResult.error(request, ApiStatusCode.WORKORDER_NO_AUTH);
        } else {
            Map<String, Object> map = workOrderApi.getData(entity);
            map.put("idRequired", workOrderApi.confirmIdCard(code));
            return ApiResult.result(request, map);
        }
    }

    /**
     * 安装工app调用原微服务--工程师接单
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/accept")
    public Map<String, Object> accept(HttpServletRequest request, @RequestParam String code) {
        return workOrderApi.accept(request, code);
    }

    /**
     * 安装工app调用原微服务--工程师拒单
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/refuse")
    public Map<String, Object> reject(HttpServletRequest request, @RequestParam String code, @RequestParam String reason) {
        return workOrderApi.reject(request, code, reason);
    }

    /**
     * 安装工app调用原微服务--工程师预约
     *
     * @param code            工单号
     * @param planServiceDate 预约时间
     * @param address         预约地址
     * @param remark          备注信息
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/appointment")
    public Map<String, Object> appointment(HttpServletRequest request, @RequestParam String code, @RequestParam String planServiceDate, @RequestParam(required = false) String address, @RequestParam(required = false) String remark) {
        return workOrderApi.appointment(request, code, planServiceDate, address, remark);
    }

    /**
     * 安装工app调用原微服务--工程师开始服务
     *
     * @param code        工单号
     * @param ismunicipal 原水水源是否为市政自来水-1是/否0
     * @param tds         tds值
     * @param hydraulic   原水水压值
     * @param otherSource 其他原水水源
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/startService")
    public Map<String, Object> startService(HttpServletRequest request, @RequestParam String code, @RequestParam String ismunicipal, @RequestParam String tds, @RequestParam String hydraulic, @RequestParam(required = false) String otherSource) {
        return workOrderApi.startService(request, code, ismunicipal, tds, hydraulic, otherSource);
    }

    /**
     * 安装工app调用原微服务--退单
     *
     * @param code   工单号
     * @param reason 退单原因
     * @param remark 退单备注
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/backOrder")
    public Map<String, Object> backOrder(HttpServletRequest request, @RequestParam String code, @RequestParam String reason, @RequestParam String remark) {
        return workOrderApi.backOrder(request, code, reason, remark);
    }

    /**
     * 安装工app调用原微服务--安装工程师扫描批次码
     *
     * @param code      工单号
     * @param batchCode 批次码
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/batchCode")
    public Map<String, Object> batchCode(HttpServletRequest request, @RequestParam String code, @RequestParam String batchCode) {
        return workOrderApi.batchCode(request, code, batchCode.trim());
    }

    /**
     * 安装工app调用原微服务--安装工程师扫描SN码
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/sncode")
    public Map<String, Object> sncode(HttpServletRequest request, @RequestParam String code, @RequestParam String sncode) {
        return workOrderApi.sncode(request, code, sncode.trim());
    }

    /**
     * 安装工app调用原微服务--安装工程师扫描SIM卡
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/simcard")
    public Map<String, Object> simcard(HttpServletRequest request, @RequestParam String code, @RequestParam String simcard) {
        return workOrderApi.simcard(request, code, simcard.trim());
    }

    /**
     * 安装工app调用原微服务--上传水质图片
     */
    @RequestMapping(value = "/webapi/engineer/waterdevice/workorder/install/uploadImg", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadImg(HttpServletRequest request, @RequestParam String code, @RequestParam("file1") MultipartFile file1, @RequestParam(required = false, value = "file2") MultipartFile file2, @RequestParam(required = false, value = "file3") MultipartFile file3) {
        return workOrderApi.uploadImg(request, code, file1, file2, file3);
    }

    /**
     * 安装工app调用原微服务--上传其他支付图片
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/upload/payImg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadPayImg(HttpServletRequest request,
                                            @RequestParam String code,
                                            @RequestParam String paymentId,
                                            @RequestParam(required = false, defaultValue = "0") int otherPayType,
                                            @RequestPart("file1") MultipartFile file1,
                                            @RequestPart(required = false, value = "file2") MultipartFile file2,
                                            @RequestPart(required = false, value = "file3") MultipartFile file3) {
        return workOrderApi.uploadPayImg(request, code, paymentId, otherPayType, file1, file2, file3);
    }

    /**
     * 安装工app调用原微服务--安装工程师修改预约时间
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/change/planServiceDate")
    public Map<String, Object> planServiceDate(HttpServletRequest request, @RequestParam String code, @RequestParam String planDate) {
        return workOrderApi.planServiceDate(request, code, planDate);
    }

    /**
     * 安装工app调用原微服务--更换设备、编辑信息
     *
     * @param code        工单号
     * @param batchCode   批次码
     * @param type        1：更换设备  2：编辑信息
     * @param sncode      sn码
     * @param simcard     sim卡
     * @param ismunicipal 原水水源是否为市政自来水-1是/否0
     * @param tds         tds值
     * @param hydraulic   原水水压值
     * @param otherSource 其他原水水源
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/editInformation")
    public Map<String, Object> editInformation(HttpServletRequest request,
                                               @RequestParam String code,
                                               @RequestParam String batchCode,
                                               @RequestParam Integer type,
                                               @RequestParam(required = false) String sncode,
                                               @RequestParam(required = false) String simcard,
                                               @RequestParam(required = false) String ismunicipal,
                                               @RequestParam(required = false) String tds,
                                               @RequestParam(required = false) String hydraulic,
                                               @RequestParam(required = false) String otherSource) {
        return workOrderApi.editInformation(request, code, batchCode.trim(), type, sncode, simcard, ismunicipal, tds, hydraulic, otherSource);
    }

    /**
     * 安装工app调用原微服务--安装工程师修改计费方式、设备型号
     *
     * @param code         工单号
     * @param productModel 产品型号
     * @param costId       计费id
     * @param costName     计费方式名称
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/changeTypeAndModel")
    public Map<String, Object> changeTypeAndModel(HttpServletRequest request,
                                                  @RequestParam String code,
                                                  @RequestParam String productModel,
                                                  @RequestParam String costId,
                                                  @RequestParam String costName) {
        try {
            return workOrderApi.changeTypeAndModel(request, code, productModel, costId, costName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof YimaoException) {
                return ApiResult.error(request, "035", e.getMessage());
            } else {
                return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
            }
        }
    }

    /**
     * 安装工app调用原微服务--获取用户信息
     */
    @GetMapping(value = "/webapi/engineer/waterdevice/workorder/install/getUserInfo")
    public Map<String, Object> getUserInfo(HttpServletRequest request, @RequestParam String customerId) {
        return workOrderApi.getUserInfo(request, customerId);
    }

    /**
     * 安装工app调用原微服务--修改用户信息
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/setUserInfo")
    public Map<String, Object> setUserInfo(HttpServletRequest request,
                                           @RequestParam String customerId,
                                           @RequestParam String code,
                                           @RequestParam(required = false) String mobile,
                                           @RequestParam(required = false) String address,
                                           @RequestParam(required = false) String mail,
                                           @RequestParam(required = false) Integer count,
                                           @RequestParam(required = false) String hobby,
                                           @RequestParam(required = false, defaultValue = "0") Integer childAge,
                                           @RequestParam(required = false) String degeree,
                                           @RequestParam(required = false) boolean haveChild,
                                           @RequestParam(required = false) boolean haveOld,
                                           @RequestParam(required = false, defaultValue = "") String childSex,
                                           @RequestParam(required = false) boolean marry,
                                           @RequestParam(required = false) boolean studyAbroad) {
        return workOrderApi.setUserInfo(request, customerId, code, mobile, address, mail, count, hobby, childAge, degeree, haveChild, haveOld, childSex, marry, studyAbroad);
    }

    /**
     * 安装工app调用原微服务--获取未签署完成的合同
     */
    @GetMapping(value = "/webapi/engineer/waterdevice/workorder/install/getContract")
    public Map<String, Object> getContract(HttpServletRequest request, @RequestParam String code) {
        return workOrderApi.getContract(request, code);
    }

    /**
     * 安装工app调用原微服务--用户签署合同
     *
     * @param code         工单号
     * @param confirmation 确认编号
     * @param userId       用户id
     * @param year         签署年限
     * @param name         用户名称
     * @param phone        手机号
     * @param idCard       身份证
     * @param mail         邮箱
     * @param address      地址
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/contract")
    public Map<String, Object> contract(HttpServletRequest request,
                                        @RequestParam String code,
                                        @RequestParam(required = false) String confirmation,
                                        @RequestParam String userId,
                                        @RequestParam int year,
                                        @RequestParam String name,
                                        @RequestParam String phone,
                                        @RequestParam String idCard,
                                        @RequestParam String mail,
                                        @RequestParam String address) {
        return workOrderApi.contract(request, code, confirmation, userId, year, name, phone, idCard, mail, address);
    }

    /**
     * 安装工app调用原微服务--用户公众号签署合同
     */
    @PostMapping(value = "/webapi/engineer/waterdevice/workorder/install/wechat/sign")
    public Map<String, Object> wechatContract(HttpServletRequest request, @RequestParam String code, @RequestParam(required = false) String confirmation) {
        return workOrderApi.wechatContract(request, code, confirmation);
    }

    /**
     * 功能描述:安装工调用微服务--工程师服务质量排名
     *
     * @param: [request]
     * @auther: liu yi
     * @date: 2019/5/10 10:52
     */
    @RequestMapping(value = {"/webapi/engineer/waterdevice/workorder/install/ranking"}, method = {RequestMethod.GET})
    @ApiOperation("工程师服务质量排名")
    public Map<String, Object> engineerRank(HttpServletRequest request) {
        Map<String, Integer> rankMap = new HashMap();
        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null) {
            return ApiResult.error(request, ApiStatusCode.TOKEN_ERROR);
        }
        EngineerDTO engineer = userFeign.getEngineerById(engineerId);
        if (engineer == null) {
            return ApiResult.error(request, ApiStatusCode.ENGINEER_NOT_EXISTS);
        }
        Map map;
        try {
            map = BaideApiUtil.ranking(engineer.getOldId());
            if (map == null || !"00000000".equals(map.get("code").toString())) {
                return ApiResult.error(request, map.get("msg").toString());
            }
            JSONObject data = JSONObject.fromObject(map.get("data"));
            if (null != data.get("rankCityMonth")) {
                rankMap.put("rankCityMonth", Integer.valueOf(data.getString("rankCityMonth")));
            }

            if (null != data.get("rankProvinceMonth")) {
                rankMap.put("rankProvinceMonth", Integer.valueOf(data.getString("rankProvinceMonth")));
            }

            if (null != data.get("rankCountryMonth")) {
                rankMap.put("rankCountryMonth", Integer.valueOf(data.getString("rankCountryMonth")));
            }

            if (null != data.get("rankCityYear")) {
                rankMap.put("rankCityYear", Integer.valueOf(data.getString("rankCityYear")));
            }

            if (null != data.get("rankProvinceYear")) {
                rankMap.put("rankProvinceYear", Integer.valueOf(data.getString("rankProvinceYear")));
            }

            if (null != data.get("rankCountryYear")) {
                rankMap.put("rankCountryYear", Integer.valueOf(data.getString("rankCountryYear")));
            }
        } catch (Exception e) {
            return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
        }
        return ApiResult.result(request, rankMap);
    }

    /**
     * 安装工app调用原微服务--获取支付单号
     *
     * @param code 工单号
     */
    @GetMapping(value = "/webapi/engineer/waterdevice/workorder/install/getPaymentId")
    @ApiOperation(value = "获取支付单号")
    @ApiImplicitParam(name = "code", value = "工单号码", required = true, dataType = "String", paramType = "query")
    @ResponseBody
    public Map<String, Object> getPaymentId(HttpServletRequest request, @RequestParam String code) {
        try {
            log.info("==========getPaymentId(workOrderid)==" + code);
            WorkOrderDTO workOrder = orderFeign.getWorkOrderById(code);
            if (workOrder != null) {
                boolean userPay = workOrder.getPayTerminal() == PayTerminal.USER.value;
                OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
                if (userPay && onlineArea != null && !"Y".equalsIgnoreCase(onlineArea.getSyncState())) {
                    ApiResult.error(request, "您所在地区正在升级新流程，用户支付不允许支付!");
                }
                Map<String, Object> data = new HashMap<>();
                data.put("paymentId", code);
                data.put("orderId", workOrder.getSubOrderId().toString());
                return ApiResult.result(request, data);
            } else {
                return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }

}
