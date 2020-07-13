package com.yimao.cloud.out.controller.openapi;

import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.out.enums.ApiStatusCode;
import com.yimao.cloud.out.enums.OpenApiStatusCode;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.service.WorkOrderApi;
import com.yimao.cloud.out.utils.ApiResult;
import com.yimao.cloud.out.utils.ObjectUtil;
import com.yimao.cloud.out.utils.OpenApiResult;
import com.yimao.cloud.out.utils.ResultBean;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.RepairWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/2/23 16:12
 * @Version 1.0
 */
@RestController
@Api(tags = "WorkOrderOpenApiController")
@Slf4j
public class WorkOrderOpenApiController {

    @Resource
    private OrderFeign orderFeign;

    @Resource
    private WorkOrderApi workOrderApi;


    /**
     * 售后系统调用原微服务--订单查询
     */
    @GetMapping(value = "/openapi/workorder/getOrder")
    @ApiOperation(value = "订单查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isHeadQuarter", value = "isHeadQuarter:Y、是；N、否", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "consumerName", value = "用户姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "consumerPhone", value = "用户手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "dealerAccount", value = "经销商账号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "dealerName", value = "经销商姓名", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "dealerPhone", value = "经销商手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "serviceSiteId", value = "服务站id", dataType = "String", paramType = "query")
    })
    public Map<String, Object> getOrder(HttpServletRequest request,
                                        @RequestParam(value = "orderId", required = false) String orderId,
                                        @RequestParam(value = "province", required = false) String province,
                                        @RequestParam(value = "city", required = false) String city,
                                        @RequestParam(value = "region", required = false) String region,
                                        @RequestParam(value = "consumerName", required = false) String consumerName,
                                        @RequestParam(value = "consumerPhone", required = false) String consumerPhone,
                                        @RequestParam(value = "dealerAccount", required = false) String dealerAccount,
                                        @RequestParam(value = "dealerName", required = false) String dealerName,
                                        @RequestParam(value = "dealerPhone", required = false) String dealerPhone,
                                        @RequestParam(value = "serviceSiteId", required = false) String serviceSiteId,
                                        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        log.info("售后系统--订单查询：orderId={},province={},city={},region={},consumerName={},consumerPhone={},dealerAccount={},dealerName={},dealerPhone={},serviceSiteId={},page={},pageSize={}",
                orderId, province, city, region, consumerName, consumerPhone, dealerAccount, dealerName, dealerPhone, serviceSiteId, page, pageSize);
        return workOrderApi.getOrderByOrderId(request, orderId, province, city, region, consumerName, consumerPhone, dealerAccount, dealerName, dealerPhone, serviceSiteId, page, pageSize);
    }


    /**
     * 售后系统--订单查询
     */
    @PostMapping(value = "/openapi/waterdevice/workorder/order/list")
    @ApiOperation(value = "售后系统--订单查询")
    public Map<String, Object> orderList(HttpServletRequest request,
                                         @RequestParam(value = "serviceSiteId", required = false) String serviceSiteId,
                                         @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                         @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        log.info("售后系统--订单查询：serviceSiteId={},page={},pageSize={}", serviceSiteId, page, pageSize);
        return workOrderApi.getOrderList(request, serviceSiteId, page, pageSize);
    }

    /**
     * 售后系统调用原微服务--获取支付凭证图片
     *
     * @param code
     * @return
     */
    @GetMapping(value = "/openapi/workorder/payImages/url")
    @ApiOperation(value = "获取支付凭证图片")
    @ApiImplicitParam(name = "code", value = "工单号", required = true, dataType = "String", paramType = "query")
    public Map<String, Object> getImagesUrl(HttpServletRequest request, @RequestParam String code) {
        return workOrderApi.getImagesUrl(request, code);
    }

    /**
     * 售后系统调用原微服务--总部修改预约时间
     */
    @PostMapping(value = "/openapi/workorder/update/appointTime")
    @ApiOperation(value = "总部修改预约时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "工单号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "time", value = "预约时间", required = true, dataType = "String", paramType = "query")
    })
    public Map<String, Object> updateAppointTime(HttpServletRequest request,
                                                 @RequestParam("code") String workCode,
                                                 @RequestParam("time") String time) {
        if (workCode.contains("WX")) {
            try {
                RepairWorkOrderDTO entity = this.orderFeign.getWorkOrderRepairByWorkCode(workCode);
                if (entity == null) {
                    return ApiResult.error(request, ApiStatusCode.SER_NO_DATA);
                }
                entity.setBespeakTimeSeconds(DateUtil.transferStringToDate(time, "yyyy-MM-dd HH:mm:ss"));
                entity.setBespeakStatusSeconds(StatusEnum.YES.value());
                entity.setCountdownTime(DateUtil.transferStringToDate(time, "yyyy-MM-dd HH:mm:ss"));
                entity.setUpdateTime(new Date());
                //entity.setUpdateUser(operationUserId.toString());

                orderFeign.updateWorkOrderRepair(entity);
            } catch (Exception e) {
                e.printStackTrace();
                return ApiResult.error(request, ResultBean.fastFailServerException().getStatusText());
            }
            return ApiResult.success(request);
        } else if (workCode.contains("WH")) {
            try {
                MaintenanceWorkOrderDTO dto = orderFeign.getWorkOrderMaintenanceByWorkCode(workCode);
                if (dto == null) {
                    return ApiResult.error(request, ApiStatusCode.SER_NO_DATA);
                }

                dto.setBespeakTimeSeconds(DateUtil.transferStringToDate(time, "yyyy-MM-dd HH:mm:ss"));
                dto.setCountdownTime(DateUtil.transferStringToDate(time, "yyyy-MM-dd HH:mm:ss"));
                dto.setBespeakStatusSeconds(StatusEnum.YES.value());
                dto.setUpdateTime(new Date());

                orderFeign.updateWorkOrderMaintenance(dto);
            } catch (Exception e) {
                e.printStackTrace();
                return ApiResult.error(request, ResultBean.fastFailServerException().getStatusText());
            }

            return ApiResult.success(request);
        } else {
            return workOrderApi.updateAppointTime(request, workCode, time);
        }
    }

    /**
     * 售后系统调用原微服务--查看工单经销商评价信息
     *
     * @param code
     * @return
     */
    @GetMapping(value = "/openapi/workorder/appraisal")
    @ApiOperation(value = "查看工单经销商评价信息")
    @ApiImplicitParam(name = "code", value = "工单号", required = true, dataType = "String", paramType = "query")
    public Map<String, Object> appraisal(HttpServletRequest request, @RequestParam String code) {
        return workOrderApi.appraisal(request, code);
    }

    /**
     * 售后系统调用原微服务--工单评分
     */
    @PostMapping(value = "/openapi/workorder/rating")
    @ApiOperation(value = "工单评分")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "工单号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "score", value = "成绩", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "opinion", value = "意见", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "levelId", value = "等级id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "level", value = "等级", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "automatic", value = "是否自动", dataType = "Boolean", paramType = "query")
    })
    public Map<String, Object> rating(HttpServletRequest request,
                                      @RequestParam("orderId") String workCode,
                                      @RequestParam("score") Integer score,
                                      @RequestParam("opinion") String opinion,
                                      @RequestParam("levelId") String levelId,
                                      @RequestParam("level") String level,
                                      @RequestParam(name = "automatic", required = false) boolean automatic) {
        if (workCode.contains("WX")) {
            return this.ratingPush(request, workCode, score, opinion, levelId, level, WorkOrderTypeEnum.ORDER_TYPE_REPAIR);
        } else if (workCode.contains("WH")) {
            return this.ratingPush(request, workCode, score, opinion, levelId, level, WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE);
        } else {
            return workOrderApi.rating(request, workCode, score, opinion, levelId, level, automatic);
        }
    }

    /**
     * 售后系统调用原微服务--维护维修工单评分
     */
    @PostMapping(value = {"/openapi/workorder/rating/push"})
    @ApiOperation("维护维修工单评分")
    public Map<String, Object> ratingPush(HttpServletRequest request, @RequestParam String workcode, @RequestParam Integer score, @RequestParam String opinion, @RequestParam String levelId, @RequestParam String level, WorkOrderTypeEnum workOrderTypeEnum) {
        if (StringUtil.isBlank(workcode)) {
            return ApiResult.error(request, "数据异常!参数不得为空");
        }
        if (ObjectUtil.isNull(workOrderTypeEnum)) {
            return ApiResult.error(request, "数据异常!请确认你要操作的工单类型");
        }

        RepairWorkOrderDTO dto;
        MaintenanceWorkOrderDTO maintenanceDto;
        int goodCount;
        int count;
        try {
            switch (workOrderTypeEnum) {
                case ORDER_TYPE_REPAIR:
                    dto = orderFeign.getWorkOrderRepairByWorkCode(workcode);
                    dto.setUserAppriseTime(new Date());
                    dto.setWorkorderUserAppraiseStatus(StatusEnum.YES.value());
                    dto.setWorkorderUserAppraiseStatusText(level);
                   /* goodCount = dto.getGoodVoteCount();
                    count = dto.getVoteCount();
                    if ("好".equals(level)) {
                        dto.setGoodVoteCount(++goodCount);
                    }
                    dto.setVoteCount(++count);
                    dto.setVoteStatus(StatusEnum.YES.value());*/
                    dto.setUpdateTime(new Date());
                    //dto.setHaveMasterRedStatus(StatusEnum.NO.value());
                    dto.setUpdateTime(new Date());
                    /* dto.setUpdateUser();*/
                    orderFeign.updateWorkOrderRepair(dto);
                    break;
                case ORDER_TYPE_MAINTENANCE:
                    maintenanceDto = orderFeign.getWorkOrderMaintenanceById(workcode);
                    maintenanceDto.setUserAppriseTime(new Date());
                    maintenanceDto.setWorkorderUserAppraiseStatus(StatusEnum.YES.value());
                    maintenanceDto.setWorkorderUserAppraiseStatusText(level);
                  /*  goodCount = maintenanceDto.getGoodVoteCount();
                    count = maintenanceDto.getVoteCount();
                    if ("好".equals(level)) {
                        maintenanceDto.setGoodVoteCount(++goodCount);
                    }
                    maintenanceDto.setVoteCount(++count);
                    maintenanceDto.setVoteStatus(StatusEnum.YES.value());*/
                    maintenanceDto.setUpdateTime(new Date());

                    this.orderFeign.updateWorkOrderMaintenance(maintenanceDto);
            }
            //TODO 红包暂时没有
        } catch (Exception e) {
            log.error("评论失败：" + e.getMessage());
            return OpenApiResult.error(request, OpenApiStatusCode.RATING_ERROR);
        }
        return OpenApiResult.success(request);

    }


    /**
     * 售后系统调用原微服务--拒绝工单保存安装工数据（售后：推送工单拒单后总部指派安装工信息给海大）
     */
    @PostMapping(value = "/openapi/waterdevice/workorder/install/save/engineer")
    @ApiOperation(value = "拒绝工单保存安装工数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "工单号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "engineerId", value = "安装工id", required = true, dataType = "String", paramType = "query")
    })
    public Map saveEngineer(HttpServletRequest request, @RequestParam String code, @RequestParam String engineerId) {
        WorkOrderDTO workOrder = orderFeign.updateWorkOrderEngineer(code, Integer.parseInt(engineerId));
        if (workOrder != null) {
            //不需要再同步给云平台了
            //SyncYimaoWorkOrder.syncEngineerId(code, engineerId);
            return ApiResult.success(request);
        } else {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    // /**
    //  * 售后系统调用原微服务--经销商7日未退单
    //  */
    // @PostMapping(value = "/openapi/waterdevice/workorder/install/back/automatic")
    // @ApiOperation(value = "经销商7日未退单")
    // @ApiImplicitParam(name = "code", value = "工单号", required = true, dataType = "String", paramType = "query")
    // public Map automatic(HttpServletRequest request, @RequestParam String code) {
    //     return workOrderApi.automatic(request, code);
    // }

    /**
     * 售后系统调用原微服务--7日自动完成
     */
    @PostMapping(value = "/openapi/waterdevice/workorder/install/complete/automatic")
    @ApiOperation(value = "7日自动完成")
    @ApiImplicitParam(name = "code", value = "工单号", required = true, dataType = "String", paramType = "query")
    public Map completeAutomatic(HttpServletRequest request, @RequestParam String code) {
        return workOrderApi.completeAutomatic(request, code);
    }

    /**
     * 售后系统调用原微服务--总部退单审核结果
     *
     * @param code
     * @return
     */
    @GetMapping(value = "/openapi/workorder/back/notice")
    @ApiOperation(value = "总部退单审核结果")
    @ApiImplicitParam(name = "code", value = "工单号", required = true, dataType = "String", paramType = "query")
    public Map backNotice(HttpServletRequest request, @RequestParam String code) {
        return workOrderApi.backNotice(request, code);
    }

    /**
     * 售后系统调用原微服务--修改安装确认单号
     *
     * @param code
     * @return
     */
    @GetMapping(value = "/openapi/workorder/putConfirmationNumber")
    @ApiOperation(value = "修改安装确认单号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "工单号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "confirmationNumber", value = "确认单号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sureTime", value = "确认时间", required = true, dataType = "String", paramType = "query")
    })
    public Map putConfirmationNamber(@RequestParam String code, @RequestParam String confirmationNumber, @RequestParam String sureTime, HttpServletRequest request) {
        return workOrderApi.putConfirmationNamber(code, confirmationNumber, sureTime, request);
    }

}
