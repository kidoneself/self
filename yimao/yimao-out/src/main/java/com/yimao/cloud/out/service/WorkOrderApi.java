package com.yimao.cloud.out.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.DistributorRoleLevel;
import com.yimao.cloud.base.enums.FinancialStateEnum;
import com.yimao.cloud.base.enums.MessageMechanismEnum;
import com.yimao.cloud.base.enums.MessageModelTypeEnum;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.enums.MessagePushObjectEnum;
import com.yimao.cloud.base.enums.PayStatus;
import com.yimao.cloud.base.enums.PayTerminal;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WaterDeviceRenewStatus;
import com.yimao.cloud.base.enums.WorkOrderChargebackEnum;
import com.yimao.cloud.base.enums.WorkOrderCompleteEnum;
import com.yimao.cloud.base.enums.WorkOrderInstallStep;
import com.yimao.cloud.base.enums.WorkOrderStatusEnum;
import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.exception.YimaoRemoteException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.properties.YunSignProperties;
import com.yimao.cloud.base.utils.AmountUtils;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.yun.YunOldIdUtil;
import com.yimao.cloud.out.enums.ApiStatusCode;
import com.yimao.cloud.out.enums.OpenApiStatusCode;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.feign.ProductFeign;
import com.yimao.cloud.out.feign.SystemFeign;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.feign.WaterFeign;
import com.yimao.cloud.out.job.WaterDeviceWorkOrderCompleteDateColorJob;
import com.yimao.cloud.out.utils.ApiResult;
import com.yimao.cloud.out.utils.ContractUtil;
import com.yimao.cloud.out.utils.InterfaceUtil;
import com.yimao.cloud.out.utils.ObjectUtil;
import com.yimao.cloud.out.utils.OpenApiResult;
import com.yimao.cloud.out.utils.ResultBean;
import com.yimao.cloud.out.utils.ValidateUtils;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderOperationDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderQueryDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.AppMessageDTO;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * @author zhilin.he
 * @description 工单相关信息调用api
 * @date 2019/3/19 10:36
 **/
@Service
@Slf4j
public class WorkOrderApi {

    //处理中状态查询
    private static final String STATE_WAIT_PAY = "000";//待支付
    private static final String STATE_PAIED = "001";//已支付
    private static final String STATE_APPOINT_PAY = "002";//约定支付
    private static final String STATE_WAIT_APPOINTMENT = "005";//待预约
    private static final String STATE_APPOINTED = "006";//已预约
    private static final String STATE_VERIFYING = "007";//审核中
    private static final String STATE_VERIFY_NOT_PASS = "008";//未通过
    private static final String STATE_NOT_CONTRACT = "010";//未签署
    private static final String STATE_CONTRACTED = "012";//已签约

    @Resource
    private UserCache userCache;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private WaterFeign waterFeign;

    @Resource
    private DomainProperties domainProperties;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ContractUtil contractUtil;

    @Resource
    private YunSignProperties yunSignProperties;


    /**
     * 原微服务--校验工单数据(售后找不到安装工数据请求微服务)
     *
     * @param oldInstallId
     * @param customerId
     * @param request
     * @return
     */
    @PostMapping(value = "/oldSystem/checkWorkCode")
    @ApiOperation(value = "校验工单数据")
    public Map checkWorkCode(@RequestParam(name = "oldInstallId") String oldInstallId
            , @RequestParam(name = "customerId") String customerId
            , HttpServletRequest request) {

        if (StringUtil.isEmpty(oldInstallId))
            return ApiResult.error(request, "工单号不得为空");
        if (StringUtil.isEmpty(customerId))
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);

        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(oldInstallId);
        if (ObjectUtil.isNull(workOrder)) {
            return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
        }
        if (workOrder.getStatus() != 4) {
            return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_COMPLETE);
        }

        Integer deviceId = workOrder.getDeviceId();
        if (deviceId == null) {
            return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
        }
        WaterDeviceDTO waterDevice = waterFeign.getWaterDeviceById(deviceId);
        if (ObjectUtil.isNull(waterDevice)) {
            return ApiResult.error(request, ApiStatusCode.DEVICE_NOT_FOUND);
        }

        Integer consumerId = waterDevice.getDeviceUserId();
        if (!customerId.equals(consumerId)) {
            return ApiResult.error(request, "用户匹配失败");
        }
        return ApiResult.result(request, waterDevice.getEngineerId());
    }

    /**
     * 原微服务--安装工程师工单列表
     *
     * @param orderType    工单类型：0-安装 1-维修 2-维护 3-退机 4-换机 5-移机-拆机 6-移机-装机
     * @param state        1-未受理 2-处理中 3-已完成
     * @param completeType 用于查询已完成下的正常完成和非正常完成 0-正常 1-非正常
     * @param search       搜索内容
     * @param page         查询页数
     * @param pageSize     每页条数
     */
    public Map<String, Object> list(HttpServletRequest request, String orderType, int state, String completeType, String search, int page, int pageSize) {
        Map<String, Object> ru = new HashMap<>();
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderQueryDTO query = new WorkOrderQueryDTO();
        query.setEngineerId(engineerId);
        query.setSearch(search);
        //安装工app状态 1.待接单(对应未受理状态),2.处理中(对应已受理和处理中),3.已完成(对应已完成)
        if (state == WorkOrderStatusEnum.ASSIGNED.value) {
            //待接单
            query.setStatus(WorkOrderStatusEnum.ASSIGNED.value);
        } else if (state == WorkOrderStatusEnum.INSTALLING.value || StringUtil.isNotEmpty(completeType)) {
            //获取已完成的工单列表
            query.setStatus(WorkOrderStatusEnum.COMPLETED.value);
            //是否正常完成
            if (StringUtil.isNotEmpty(completeType)) {
                if (completeType.equals("0")) {
                    query.setCompleteType(WorkOrderCompleteEnum.NORMAL_COMPLETE.getState());//正常完成
                } else if (completeType.equals("1")) {
                    query.setCompleteType(WorkOrderCompleteEnum.AUTO_COMPLETE.getState());//非正常完成
                }

            }

        } else if (state == WorkOrderStatusEnum.ACCEPTED.value) {
            //处理中状态的工单包含状态为2,3
            query.setState(2);
        } else {
            query.setStatus(state);
        }

        // query.setNotRefuse("N");
        PageVO<WorkOrderDTO> pageVO = orderFeign.getWorkOrderList(query, page, pageSize);
        List<WorkOrderDTO> workOrderList = pageVO.getResult();
        List<Map<String, Object>> listMap = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(workOrderList)) {
            for (WorkOrderDTO dto : workOrderList) {
                listMap.add(this.getData(dto));
            }

        }
        //安装工app根据返回做相应的提示
        ru = ApiResult.result(request, listMap);
        ru.put("count", pageVO.getTotal());
        return ru;
    }

    /**
     * 原微服务--安装工程师处理中详细工单列表
     */
    public Map<String, Object> handingList(HttpServletRequest request, String orderType, String detailStatus, int page, int pageSize, String search) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderQueryDTO query = new WorkOrderQueryDTO();
        query.setEngineerId(engineerId);
        query.setSearch(search);
        if (STATE_WAIT_PAY.equalsIgnoreCase(detailStatus)) {
            query.setPay(false);
        } else if (STATE_PAIED.equalsIgnoreCase(detailStatus)) {
            query.setPay(true);
            query.setPayStatus(PayStatus.PAY.value);
        } else if (STATE_WAIT_APPOINTMENT.equalsIgnoreCase(detailStatus)) {
            query.setAppointStatus(StatusEnum.NO.value());
        } else if (STATE_APPOINTED.equalsIgnoreCase(detailStatus)) {
            query.setAppointStatus(StatusEnum.YES.value());
        } else if (STATE_VERIFYING.equalsIgnoreCase(detailStatus)) {
            query.setPay(false);
            query.setPayStatus(PayStatus.WAITING_AUDIT.value);
        } else if (STATE_VERIFY_NOT_PASS.equalsIgnoreCase(detailStatus)) {
            query.setPayStatus(PayStatus.FAIL.value);
        } else if (STATE_NOT_CONTRACT.equalsIgnoreCase(detailStatus)) {
            query.setSignStatus(StatusEnum.NO.value());
        } else if (STATE_CONTRACTED.equalsIgnoreCase(detailStatus)) {
            query.setSignStatus(StatusEnum.YES.value());
        }
        query.setStatus(WorkOrderStatusEnum.INSTALLING.value);
        Map<String, Object> ru = new HashMap<>();
        PageVO<WorkOrderDTO> pageVO = orderFeign.getWorkOrderList(query, page, pageSize);
        List<WorkOrderDTO> workOrderList = pageVO.getResult();
        if (CollectionUtil.isNotEmpty(workOrderList)) {
            List<Map<String, Object>> listMap = new ArrayList<>();
            for (WorkOrderDTO dto : workOrderList) {
                listMap.add(this.getData(dto));
            }
            ru = ApiResult.result(request, listMap);
            ru.put("count", pageVO.getTotal());
        }
        return ru;
    }

    /**
     * 原微服务--查询工单详情
     */
    public Map<String, Object> detail(HttpServletRequest request, String code) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        Map<String, Object> ru = new HashMap<>();
        if (entity != null && entity.getEngineerId().equals(engineerId)) {
            Map<String, Object> map = this.getData(entity);
            map.put("idRequired", this.confirmIdCard(code));
            ru = ApiResult.result(request, map);
        } else {
            ru = ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
        return ru;
    }


    /**
     * 原微服务--工程师接单
     */
    public Map<String, Object> accept(HttpServletRequest request, String code) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        if (entity != null && Objects.equals(entity.getEngineerId(), engineerId)) {
            Map<String, Object> map;
            try {
                map = BaideApiUtil.accept(code);
            } catch (Exception var9) {
                return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
            }
            if ("00000000".equals(map.get("code").toString())) {
                // //接单修改
                // Date now = new Date();
                // // entity.setAcceptStatus(StatusEnum.YES.value());
                // entity.setAcceptTime(now);
                // entity.setOperationTime(now);
                // entity.setStatus(WorkOrderStatusEnum.ACCEPTED.value);
                // entity.setStep(WorkOrderInstallStep.START.value);
                JSONObject obj = JSONObject.fromObject(map.get("data"));
                String nextStep = obj.getString("nextStep");
                // entity.setNextStep(Integer.parseInt(nextStep));
                // WorkOrderDTO result = orderFeign.updateWorkOrder(entity);
                try {
                    orderFeign.acceptWorkOrder(code, engineerId, Integer.parseInt(nextStep));
                    return ApiResult.result(request, nextStep);
                } catch (Exception e) {
                    return ApiResult.error(request, code, "安装工程师接单失败");
                }
            } else {
                return ApiResult.error(request, map.get("msg").toString());
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }

    /**
     * 原微服务--工程师拒单
     */
    public Map<String, Object> reject(HttpServletRequest request, String code, String reason) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(code);
        if (workOrder != null && Objects.equals(workOrder.getEngineerId(), engineerId)) {
            if (StatusEnum.isYes(workOrder.getNotRefuse())) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_REFUSE);
            }
            Map<String, Object> map;
            try {
                map = BaideApiUtil.reject(code, reason);
            } catch (Exception e) {
                log.error("=========安装工拒单异常(engineerId=" + engineerId + "),errMsg=" + e.getMessage());
                return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
            }
            if ("00000000".equals(map.get("code").toString())) {
                Date now = new Date();
                JSONObject obj = JSONObject.fromObject(map.get("data"));
                String nextStep = obj.getString("nextStep");
                String oldEngineerId = obj.getString("engineerId");
                WorkOrderDTO result;
                workOrder.setRefuseReason(reason);
                workOrder.setRefuseTime(now);
                workOrder.setRefuse(true);
                // entity.setNotRefuse(StatusEnum.YES.value());
                workOrder.setStep(WorkOrderInstallStep.START.value);
                if (StringUtil.isEmpty(oldEngineerId)) {
                    workOrder.setEngineerId(null);
                    workOrder.setEngineerPhone(null);
                    workOrder.setEngineerName(null);
                    workOrder.setEngineerIdCard(null);
                    workOrder.setStationId(null);
                    workOrder.setStationName(null);
                    workOrder.setOldEngineerId(null);
                    workOrder.setOldStationId(null);
                    //需要后台人员手动分配安装工
                    workOrder.setDispatch(true);
                    result = orderFeign.updateWorkOrder(workOrder);
                } else {
                    workOrder.setOperationTime(now);
                    EngineerDTO engineer = userFeign.getEngineerByOldId(oldEngineerId);
                    workOrder.setEngineerId(engineer.getId());
                    workOrder.setEngineerName(engineer.getRealName());
                    workOrder.setEngineerPhone(engineer.getPhone());
                    workOrder.setEngineerIdCard(engineer.getIdCard());
                    workOrder.setOldEngineerId(engineer.getOldId());
                    workOrder.setStationId(engineer.getStationId());
                    workOrder.setOldStationId(engineer.getOldSiteId());
                    workOrder.setStationName(engineer.getStationName());
                    workOrder.setStatus(WorkOrderStatusEnum.ASSIGNED.value);
                    result = orderFeign.updateWorkOrder(workOrder);
                }
                if (result != null) {
                    return ApiResult.result(request, nextStep);
                } else {
                    return ApiResult.error(request, code, "安装工程师拒单失败");
                }
            } else {
                return ApiResult.error(request, map.get("msg").toString());
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }

    /**
     * 原微服务--工程师预约
     */
    public Map<String, Object> appointment(HttpServletRequest request, String code, String planServiceDate, String address, String remark) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        if (entity != null && Objects.equals(entity.getEngineerId(), engineerId)) {
            Map<String, Object> map;
            try {
                map = BaideApiUtil.appointment(code, planServiceDate, address, remark);
            } catch (Exception var11) {
                return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
            }
            if ("00000000".equals(map.get("code").toString())) {
                JSONObject obj = JSONObject.fromObject(map.get("data"));
                String nextStep = obj.getString("nextStep");
                entity.setNextStep(Integer.parseInt(nextStep));
                entity.setAppointTime(DateUtil.stringToDate(planServiceDate));
                entity.setCountdownTime(DateUtil.stringToDate(planServiceDate));
                if (StringUtil.isNotEmpty(address)) {
                    entity.setAppointAddress(address);
                    entity.setAddressDetail(entity.getProvince() + entity.getCity() + entity.getRegion() + address);
                }
                if (StringUtil.isNotEmpty(remark)) {
                    entity.setAppointRemark(remark);
                }
                entity.setAppointStatus(StatusEnum.YES.value());
                entity.setOperationTime(new Date());
                entity.setStatus(WorkOrderStatusEnum.INSTALLING.value);
                entity.setStep(WorkOrderInstallStep.PICK.value);
                WorkOrderDTO result = orderFeign.updateWorkOrder(entity);
                if (result != null) {
                    return ApiResult.result(request, nextStep);
                } else {
                    return ApiResult.error(request, code, "安装工程师预约失败");
                }
            } else {
                return ApiResult.error(request, map.get("msg").toString());
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }

    }

    /**
     * 原微服务--工程师开始服务
     */
    public Map<String, Object> startService(HttpServletRequest request, String code, String ismunicipal, String tds, String hydraulic, String otherSource) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        Map<String, Object> ru;
        if (entity != null && Objects.equals(entity.getEngineerId(), engineerId)) {
            Map<String, Object> map;
            String hydraulicMatches = "^([0-9][0-9]*)$|(([1-9][0-9]*)\\.([0-9]))|[0]\\.([0-9])|(([1-9][0-9]*)\\.([0-9]{2}))|[0]\\.([0-9]{2})";
            String tdsMatches = "^([0-9][0-9]*)$";
            if (!tds.matches(tdsMatches)) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_TDS_ERROR);
            }
            if (!hydraulic.matches(hydraulicMatches)) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_HYDRAULIC_ERROR);
            }
            try {
                map = BaideApiUtil.startWorkOrder(code, ismunicipal, tds, hydraulic, otherSource);
            } catch (Exception var14) {
                return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
            }
            if ("00000000".equals(map.get("code").toString())) {
                JSONObject obj = JSONObject.fromObject(map.get("data"));
                String nextStep = obj.getString("nextStep");
                entity.setNextStep(Integer.parseInt(nextStep));
                entity.setMunicipal(ismunicipal);
                entity.setTds(tds);
                entity.setHydraulic(hydraulic);
                entity.setOtherSource(otherSource);
                entity.setWaterStatus(StatusEnum.YES.value());
                entity.setWaterStatusText("水源信息输入成功");
                entity.setOperationTime(new Date());
                entity.setStatus(WorkOrderStatusEnum.INSTALLING.value);
                entity.setStep(WorkOrderInstallStep.PICK.value);
                WorkOrderDTO result = orderFeign.updateWorkOrder(entity);
                if (result != null) {
                    ru = ApiResult.result(request, nextStep);
                } else {
                    ru = ApiResult.error(request, code, "安装工程师开始服务失败");
                }
            } else {
                ru = ApiResult.error(request, map.get("msg").toString());
            }
        } else {
            ru = ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
        return ru;
    }

    /**
     * 原微服务--退单
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map<String, Object> backOrder(HttpServletRequest request, String code, String reason, String remark) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        if (entity != null && Objects.equals(entity.getEngineerId(), engineerId)) {
            if (!StringUtil.isEmpty(remark)) {
                Map<String, Object> map;
                try {
                    map = BaideApiUtil.backOrder(code, reason, remark);
                } catch (Exception var10) {
                    return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
                }
                if ("00000000".equals(map.get("code").toString())) {
                    Date now = new Date();
                    // entity.setCancelTime(new Date());
                    // entity.setCancelReason(reason);
                    // entity.setCancelRemark(remark);
                    // entity.setCancelPerson("客服退单");
                    // entity.setIsBackWorkOrder(StatusEnum.YES.value());
                    // entity.setBackOrderStatus(WorkOrderChargebackEnum.BACK_WAIT.getState());
                    // entity.setWorkorderBackStatusText(WorkOrderChargebackEnum.BACK_WAIT.getStateText());

                    String sncode = "";
                    if (entity.getDeviceId() != null) {
                        WaterDeviceDTO device = waterFeign.getWaterDeviceById(entity.getDeviceId());
                        if (device != null) {
                            sncode = device.getSn();
                            if (StringUtil.isNotEmpty(device.getIccid())) {
                                waterFeign.deactivatedSimCard(device.getId());
                            }
                            waterFeign.deleteWaterDevice(device.getId());
                        }
                    }

                    entity.setChargebackSncode(sncode);
                    entity.setOperationTime(now);
                    entity.setStatus(WorkOrderStatusEnum.COMPLETED.value);
                    entity.setCompleteType(WorkOrderCompleteEnum.AUTO_COMPLETE.getState());//异常完成
                    entity.setStep(-1);
                    entity.setChargeback(true);
                    entity.setChargebackStatus(Integer.parseInt(WorkOrderChargebackEnum.BACK_WAIT_YUN.getState()));
                    entity.setChargebackType(2);//1-经销商退单；2-客服退单
                    entity.setChargebackReason(reason);
                    entity.setChargebackRemark(remark);
                    entity.setChargebackSncode(entity.getSn());
                    entity.setChargebackTime(now);
                    WorkOrderDTO result = orderFeign.updateWorkOrder(entity);
                    //使用异步队列保存工单操作记录
                    WorkOrderOperationDTO operation = new WorkOrderOperationDTO();
                    operation.setAdmin(entity.getEngineerName());
                    operation.setWorkOrderId(entity.getId());
                    operation.setOperation("客服退单");
                    operation.setRemark("当前sncode:" + entity.getSn() + ",simcard:" + entity.getSimCard() + ",batchCode:" + entity.getLogisticsCode());
                    operation.setCreateTime(now);
                    rabbitTemplate.convertAndSend(RabbitConstant.WORK_ORDER_OPERATION, operation);
                    if (result != null) {
                        //修改订单状态为：待经销商审核
                        List<Long> ids = new ArrayList<>();
                        ids.add(result.getSubOrderId());
                        com.alibaba.fastjson.JSONObject json = orderFeign.updateOrderStatusBatch(reason, remark, ids);
                        int failureSize = json.get("failureArray") == null ? 0 : ((JSONArray) json.get("failureArray")).size();
                        int successSize = json.get("successArray") == null ? 0 : ((JSONArray) json.get("successArray")).size();
                        if (failureSize > 0 && successSize < 0) {
                            return ApiResult.error(request, code, "安装工程师退单失败,修改订单状态异常。");
                        }
                        for (Long subOrderId : ids) {
                            //根据订单号查询订单
                            OrderSubDTO sbuOrder = orderFeign.getOrder(subOrderId);
                            //发送通知
                            Map<String, String> messageMap = new HashMap<>();
                            messageMap.put("#code#", sbuOrder.getMainOrderId().toString());
                            DistributorDTO distributorDTO = userFeign.getDistributorBasicInfoByIdForMsgPushInfo(sbuOrder.getDistributorId());
                            if (Objects.isNull(distributorDTO)) {
                                log.error("安装工退单通知推送失败，子订单未能找到经销商，子订单id=" + subOrderId);
                                continue;
                            }
                            //为自己下单的订单
                            if (sbuOrder.getDistributorId() != null && sbuOrder.getUserId().equals(distributorDTO.getUserId())) {
                                //消息推送
	                            pushMsgToDistributorUser(MessageModelTypeEnum.VERIFY_NOTICE.value, MessageMechanismEnum.DISTRIBUTION_SELF_ORDER_AUDIT.value, MessageModelTypeEnum.VERIFY_NOTICE.name, distributorDTO.getUserId(), distributorDTO.getUserName(), distributorDTO.getAppType(), messageMap);
                            } else {//为客户下单的订单
                                //消息推送
	                            pushMsgToDistributorUser(MessageModelTypeEnum.VERIFY_NOTICE.value, MessageMechanismEnum.DISTRIBUTION_LATTER_ORDER_AUDIT.value, MessageModelTypeEnum.VERIFY_NOTICE.name, distributorDTO.getUserId(), distributorDTO.getUserName(), distributorDTO.getAppType(), messageMap);
                            }
                        }
                        return ApiResult.success(request);
                    } else {
                        return ApiResult.error(request, code, "安装工程师退单失败");
                    }
                } else {
                    return ApiResult.error(request, map.get("msg").toString());
                }
            } else {
                return ApiResult.error(request, ApiStatusCode.BACKORDER_REMARK);
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }

    public void pushMsgToDistributorUser(Integer pushType, Integer mechanism, String title, Integer receiverId, String receiverName, Integer devices, Map<String, String> distributorMessage) {
        //构建APP消息推送实体
        AppMessageDTO appMessage = new AppMessageDTO();
        appMessage.setMsgType(Constant.MSG_TYPE_NOTICE);
        appMessage.setPushType(pushType);
        appMessage.setCreateTime(new Date());
        appMessage.setReceiverId(receiverId);
        appMessage.setReceiver(receiverName);
        appMessage.setTitle(title);
        appMessage.setApp(MessagePushModeEnum.YIMAO_APP_NOTICE.value);//1-推送给安装工；2-推送消息给经销商
        appMessage.setMechanism(mechanism);
        appMessage.setDevices(devices);
        appMessage.setContentMap(distributorMessage);
        //2-推送消息给经销商
        appMessage.setPushObject(MessagePushObjectEnum.DISTRIBUTOR.value);
        rabbitTemplate.convertAndSend(RabbitConstant.YIMAO_APP_MESSAGE_PUSH, appMessage);
    }

    /**
     * 原微服务--安装工程师扫描批次码
     */
    public Map<String, Object> batchCode(HttpServletRequest request, String code, String batchCode) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        if (entity != null && Objects.equals(entity.getEngineerId(), engineerId)) {
            Map<String, Object> map;
            try {
                map = BaideApiUtil.batchCode(code, batchCode);
            } catch (Exception var12) {
                return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
            }
            if ("00000000".equals(map.get("code").toString())) {
                JSONObject json = JSONObject.fromObject(map.get("data"));
                String nextStep = json.getString("nextStep");
                String materielCode = null;
                try {
                    Map<String, Object> materielMap = BaideApiUtil.supplierName(batchCode);
                    if ("00000000".equals(materielMap.get("code").toString())) {
                        JSONObject jsonObject = JSONObject.fromObject(materielMap.get("data"));
                        materielCode = jsonObject.getString("productName");
                    } else {
                        return ApiResult.error(request, map.get("msg").toString());
                    }
                } catch (Exception e) {
                    log.error("新流程安装工程师扫描批次码出错，向百得获取物料编码出错，msg={}", e.getMessage());
                }
                entity.setMaterielCode(materielCode);
                entity.setLogisticsCode(batchCode);
                // entity.setBatchCodeStatus(StatusEnum.YES.value());
                // entity.setWorkorderBatchCodeStatusText("批次码输入成功");
                // entity.setBatchCodeTime(new Date());
                entity.setOperationTime(new Date());
                entity.setStatus(WorkOrderStatusEnum.INSTALLING.value);
                entity.setStep(WorkOrderInstallStep.PROTOCOL.value);
                entity.setNextStep(Integer.parseInt(nextStep));
                WorkOrderDTO result = orderFeign.updateWorkOrder(entity);
                if (result != null) {
                    return ApiResult.result(request, nextStep);
                } else {
                    return ApiResult.error(request, code, "新流程安装工程师扫描批次码失败");
                }
            } else {
                return ApiResult.error(request, map.get("msg").toString());
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }

    /**
     * 原微服务--安装工程师扫描SN码
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Map<String, Object> sncode(HttpServletRequest request, String code, String sncode) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(code);
        if (workOrder != null && Objects.equals(workOrder.getEngineerId(), engineerId)) {
            Map<String, Object> map;
            try {
                map = BaideApiUtil.sncode(code, sncode);
            } catch (Exception e) {
                log.error("新流程安装工程师扫描SN码出错，向百得同步SN码出错，msg={}", e.getMessage());
                return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
            }
            WaterDeviceDTO device = waterFeign.getWaterDeviceBySnCode(sncode);
            if (device == null) {
                if ("00000000".equals(map.get("code").toString())) {
                    JSONObject jsonObject = JSONObject.fromObject(map.get("data"));
                    String nextStep = jsonObject.getString("nextStep");
                    workOrder.setNextStep(Integer.parseInt(nextStep));
                    // if (workOrder.getPay()) {
                    //     workOrder.setNextStep(Integer.parseInt(nextStep) + 1);
                    // }
                    workOrder.setSn(sncode);
                    workOrder.setOperationTime(new Date());
                    workOrder.setStatus(WorkOrderStatusEnum.INSTALLING.value);
                    workOrder.setStep(WorkOrderInstallStep.SNCODE.value);
                    //先创建水机设备
                    device = this.createWaterDevice(workOrder);
                    workOrder.setDeviceId(device.getId());
                    workOrder.setSnCodeTime(device.getSnEntryTime());
                    WorkOrderDTO result = orderFeign.updateWorkOrder(workOrder);
                    if (result != null) {
                        return ApiResult.result(request, nextStep);
                    } else {
                        return ApiResult.error(request, code, "安装工程师扫描SN码失败");
                    }
                } else {
                    return ApiResult.error(request, map.get("msg").toString());
                }
            } else {
                return ApiResult.error(request, ApiStatusCode.SNCODE_EXSISTS_ERROR.getTextZh());
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }

    public WaterDeviceDTO createWaterDevice(WorkOrderDTO workOrder) {
        Date now = new Date();
        WaterDeviceDTO device = new WaterDeviceDTO();
        device.setSn(workOrder.getSn());
        device.setSnEntryTime(now);
        device.setLogisticsCode(workOrder.getLogisticsCode());
        device.setCostId(workOrder.getCostId());
        device.setOldCostId(workOrder.getOldCostId());
        device.setCostType(workOrder.getCostType());
        device.setCostName(workOrder.getCostName());
        device.setCostChanged(false);
        device.setDistributorId(workOrder.getDistributorId());
        device.setOldDistributorId(workOrder.getOldDistributorId());
        device.setDistributorName(workOrder.getDistributorName());
        device.setDistributorPhone(workOrder.getDistributorPhone());
        device.setDistributorAccount(workOrder.getDistributorAccount());
        device.setEngineerId(workOrder.getEngineerId());
        device.setEngineerName(workOrder.getEngineerName());
        device.setEngineerPhone(workOrder.getEngineerPhone());
        device.setDeviceUserId(workOrder.getUserId());
        device.setDeviceUserName(workOrder.getUserName());
        device.setDeviceUserPhone(workOrder.getUserPhone());
        try {
            WaterDeviceUserDTO deviceUser = userFeign.getWaterDeviceUserById(workOrder.getUserId());
            if (deviceUser != null) {
                device.setOldCustomerId(deviceUser.getOldId());
            }
        } catch (Exception e) {
            log.error("初始化水机设备信息时，获取水机设备用户oldId失败" + e.getMessage(), e);
        }
        device.setProvince(workOrder.getProvince());
        device.setCity(workOrder.getCity());
        device.setRegion(workOrder.getRegion());
        device.setAddress(workOrder.getAddress());
        device.setDeviceModel(workOrder.getDeviceModel());
        device.setDeviceScope(YunOldIdUtil.getProductScope(workOrder.getDeviceModel()));
        device.setDeviceType(YunOldIdUtil.getProductTypeName());
        device.setInitMoney(workOrder.getFee());
        device.setMoney(workOrder.getFee());
        device.setCreateTime(now);
        device.setWorkOrderId(workOrder.getId());
        // device.setBind(false);
        device.setCurrentTotalTime(0);
        device.setCurrentTotalFlow(0);
        device.setLastTotalTime(0);
        device.setLastTotalFlow(0);
        device.setUseTime(0);
        device.setUseFlow(0);
        device.setRenewStatus(WaterDeviceRenewStatus.NONEED.value);
        device.setRenewStatusText(WaterDeviceRenewStatus.NONEED.name);
        return waterFeign.createWaterDevice(device);
    }


    /**
     * 原微服务--安装工程师扫描SIM卡
     */
    public Map<String, Object> simcard(HttpServletRequest request, String code, String simcard) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        if (entity != null && Objects.equals(entity.getEngineerId(), engineerId)) {
            WaterDeviceDTO device = waterFeign.getWaterDeviceByIccid(simcard);
            if (device == null) {
                Map<String, Object> map;
                try {
                    map = BaideApiUtil.simCard(code, simcard);
                } catch (Exception var9) {
                    return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
                }
                if ("00000000".equals(map.get("code").toString())) {
                    Date now = new Date();
                    JSONObject jsonObject = JSONObject.fromObject(map.get("data"));
                    String nextStep = jsonObject.getString("nextStep");
                    entity.setNextStep(Integer.parseInt(nextStep));
                    entity.setSimCard(simcard);
                    // entity.setSimCardStatus(StatusEnum.YES.value());
                    entity.setSimCardTime(now);
                    // entity.setSnCodeStatus(StatusEnum.YES.value());
                    // entity.setWorkorderSimcardStatusText("simcard输入成功");
                    entity.setOperationTime(now);
                    entity.setStatus(WorkOrderStatusEnum.INSTALLING.value);
                    entity.setStep(WorkOrderInstallStep.SIMCARD.value);
                    WorkOrderDTO result = orderFeign.updateWorkOrder(entity);
                    if (result != null) {
                        device = waterFeign.getWaterDeviceById(entity.getDeviceId());
                        if (device != null) {
                            waterFeign.activatingSimCard(device.getId(), simcard);
                        } else {
                            return ApiResult.error(request, code, "安装工程师扫描SIM卡失败");
                        }
                        return ApiResult.result(request, nextStep);
                    } else {
                        return ApiResult.error(request, code, "安装工程师扫描SIM卡失败");
                    }
                } else {
                    return ApiResult.error(request, map.get("msg").toString());
                }
            } else {
                return ApiResult.error(request, ApiStatusCode.SIMCARD_EXSISTS_ERROR.getTextZh());
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }


    /**
     * 原微服务--上传水质图片
     */
    public Map<String, Object> uploadImg(HttpServletRequest request, String code, MultipartFile file1, MultipartFile file2, MultipartFile file3) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        if (entity != null && Objects.equals(entity.getEngineerId(), engineerId)) {
            Map<String, Object> map;
            try {
                map = BaideApiUtil.uploadPicture(code, file1, file2, file3);
            } catch (Exception var14) {
                return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
            }
            if ("00000000".equals(map.get("code").toString())) {
                Date now = new Date();
                JSONObject jsonObject = JSONObject.fromObject(map.get("data"));
                String nextStep = jsonObject.getString("nextStep");
                entity.setNextStep(Integer.parseInt(nextStep));
                entity.setWaterImages(this.upload(file1, file2, file3, null, null));
                entity.setUploadImgTime(new Date());
                // entity.setUploadImgStatus(StatusEnum.YES.value());
                // entity.setWorkorderUploadImgStatusText("上传水质图片成功");
                entity.setOperationTime(now);
                entity.setStatus(WorkOrderStatusEnum.INSTALLING.value);
                WorkOrderDTO result = orderFeign.updateWorkOrder(entity);
                if (result != null) {
                    return ApiResult.result(request, nextStep);
                } else {
                    return ApiResult.error(request, code, "安装工程师上传水质图片失败");
                }
            } else {
                return ApiResult.error(request, map.get("msg").toString());
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }

    /**
     * 原微服务--上传其他支付图片
     */
    public Map<String, Object> uploadPayImg(HttpServletRequest request, String code, String paymentId, int otherPayType, MultipartFile file1, MultipartFile file2, MultipartFile file3) {
        log.info("=======workOrderOtherPayReq=========paymentId:" + paymentId + ",code=" + code + ",otherPayType=" + otherPayType);
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        if (entity != null && Objects.equals(entity.getEngineerId(), engineerId)) {
            if (PayType.find(otherPayType) == null) {
                return ApiResult.error(request, "004", "未知的其他支付类型");
            }
            String payType = "2";
            Map<String, Object> map;
            try {
                map = BaideApiUtil.payment(code, payType, file1, file2, file3);
                log.info("==========BaidePayPaymentResp=" + JSON.toJSONString(map));
            } catch (Exception e) {
                log.error("原微服务--上传其他支付图片发生异常，meg={}", e.getMessage());
                return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
            }
            if ("00000000".equals(map.get("code").toString())) {
                // JSONObject obj = JSONObject.fromObject(map.get("data"));
                // String nextStep = obj.getString("nextStep");
                String payCredential = null;
                try {
                    payCredential = this.upload(file1, file2, file3, null, null);
                    if (StringUtil.isEmpty(payCredential)) {
                        return ApiResult.error(request, "004", "安装工程师上传其他支付图片失败");
                    }
                } catch (Exception e) {
                    return ApiResult.error(request, "004", "安装工程师上传其他支付图片失败");
                }
                entity.setPayCredential(payCredential);
                entity.setPayCredentialSubmitTime(new Date());
                log.info("===========workOrderOtherPayImages=" + entity.getPayCredential());
                entity.setPayStatus(PayStatus.WAITING_AUDIT.value);
                entity.setPayType(otherPayType);
                entity.setStep(WorkOrderInstallStep.PAID.value);
                entity.setNextStep(8);//TODO 售后系统没有返回该字段，所以写死了
                WorkOrderDTO result = orderFeign.updateWorkOrder(entity);

                //@ 更新订单状态
                orderFeign.otherPaySubmitCredential(entity.getSubOrderId(), entity.getId(), entity.getPayType(), entity.getPayCredential());
                if (result != null) {
                    // ru = ApiResult.result(request, 6);//
                    return ApiResult.result(request, result.getNextStep());
                } else {
                    return ApiResult.error(request, "004", "安装工程师上传其他支付图片失败");
                }
            } else {
                return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }

    public String upload(MultipartFile file1, MultipartFile file2, MultipartFile file3, String folder, String remark) {
        String path = "";
        try {
            if (file1 != null) {
                String path1 = systemFeign.upload(file1, folder, remark);
                path += StringUtil.isNotBlank(path1) ? path1 : "";
            }
            if (file2 != null) {
                String path2 = systemFeign.upload(file2, folder, remark);
                path += StringUtil.isNotBlank(path2) ? "," + path2 : "";
            }
            if (file3 != null) {
                String path3 = systemFeign.upload(file3, folder, remark);
                path += StringUtil.isNotBlank(path3) ? "," + path3 : "";
            }
        } catch (Exception e) {
            log.error("==========上传失败=========" + e.getMessage());
        }
        return path;
    }

    /**
     * 原微服务--安装工程师修改预约时间
     */
    public Map<String, Object> planServiceDate(HttpServletRequest request, String code, String planDate) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        if (entity != null && Objects.equals(entity.getEngineerId(), engineerId)) {
            Map<String, Object> map;
            try {
                map = BaideApiUtil.changePlanDate(code, planDate);
            } catch (Exception var10) {
                return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
            }
            JSONObject obj = JSONObject.fromObject(map);
            if ("00000000".equals(obj.getString("code"))) {
                entity.setAppointTime(DateUtil.transferStringToDate(planDate));
                entity.setCountdownTime(DateUtil.transferStringToDate(planDate));
                WorkOrderDTO result = orderFeign.updateWorkOrder(entity);
                if (result != null) {
                    return ApiResult.success(request);
                } else {
                    return ApiResult.error(request, code, "安装工程师修改预约时间失败");
                }
            } else {
                return ApiResult.error(request, map.get("msg").toString());
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }

    /**
     * 原微服务--更换设备、编辑信息
     */
    public Map<String, Object> editInformation(HttpServletRequest request, String code, String batchCode, Integer type, String sncode, String simcard, String ismunicipal, String tds, String hydraulic, String otherSource) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(code);
        if (workOrder != null && Objects.equals(workOrder.getEngineerId(), engineerId)) {
            String oldSncode = workOrder.getSn();
            String oldSimcard = workOrder.getSimCard();
            String oldBatchCode = workOrder.getLogisticsCode();
            Map<String, Object> map;
            Map<String, Object> deviceMap = new HashMap<>();
            try {
                map = BaideApiUtil.editInformation(code, batchCode, type, sncode, simcard, ismunicipal, tds, hydraulic, otherSource);
            } catch (Exception var21) {
                return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
            }
            if ("00000000".equals(map.get("code").toString())) {
                try {
                    deviceMap = BaideApiUtil.supplierName(batchCode);
                } catch (Exception e) {
                    log.info("请求百得获取设备相关信息失败了,batchcode: " + batchCode);
                }

                String supplierName = InterfaceUtil.analysisMapData(deviceMap, "supplierName");
                String materielCode = InterfaceUtil.analysisMapData(deviceMap, "productName");
                Integer deviceId = workOrder.getDeviceId();
                WaterDeviceDTO device = null;
                if (deviceId != null) {
                    device = waterFeign.getWaterDeviceById(deviceId);
                }

                boolean changeDevice = false;

                if (StringUtil.isNotEmpty(batchCode) && !Objects.equals(batchCode, oldBatchCode)) {
                    changeDevice = true;
                    workOrder.setLogisticsCode(batchCode);
                    if (device != null) {
                        device.setLogisticsCode(batchCode);
                    }
                }
                //是否需要更新sn码到工单和设备上
                if (StringUtil.isNotEmpty(sncode) && !Objects.equals(sncode, oldSncode)) {
                    changeDevice = true;
                    boolean flag = waterFeign.checkSnExists(deviceId, sncode);
                    if (flag) {
                        return ApiResult.error(request, code, ApiStatusCode.SNCODE_EXSISTS_ERROR.getTextZh());
                    } else {
                        workOrder.setSn(sncode);
                        workOrder.setMaterielCode(materielCode);
                        workOrder.setSupplierName(supplierName);
                        if (device != null) {
                            device.setSn(sncode);
                        }
                    }
                }
                //是否需要更新iccid到工单和设备上
                if (StringUtil.isNotEmpty(simcard) && !Objects.equals(simcard, oldSimcard)) {
                    changeDevice = true;
                    boolean flag = waterFeign.checkIccidExists(deviceId, simcard);
                    if (flag) {
                        return ApiResult.error(request, code, ApiStatusCode.SIMCARD_EXSISTS_ERROR.getTextZh());
                    } else {
                        workOrder.setSimCard(simcard);
                        if (device != null) {
                            device.setIccid(simcard);
                        }
                    }
                }

                if (StringUtil.isNotEmpty(ismunicipal)) {
                    workOrder.setMunicipal(ismunicipal);
                }
                if (StringUtil.isNotEmpty(tds)) {
                    workOrder.setTds(tds);
                }
                if (StringUtil.isNotEmpty(hydraulic)) {
                    workOrder.setHydraulic(hydraulic);
                }
                if (StringUtil.isNotEmpty(otherSource)) {
                    workOrder.setOtherSource(otherSource);
                }
                if (device != null) {
                    waterFeign.updateDevice(device);
                }
                WorkOrderDTO result = orderFeign.updateWorkOrder(workOrder);
                if (changeDevice) {
                    //使用异步队列保存工单操作记录
                    WorkOrderOperationDTO operation = new WorkOrderOperationDTO();
                    operation.setAdmin(workOrder.getEngineerName());
                    operation.setWorkOrderId(workOrder.getId());
                    operation.setOperation("更换设备");
                    if (device != null) {
                        operation.setSnCode(device.getSn());
                        operation.setSimCard(device.getIccid());
                        operation.setBatchCode(device.getLogisticsCode());
                    }
                    operation.setCreateTime(new Date());
                    rabbitTemplate.convertAndSend(RabbitConstant.WORK_ORDER_OPERATION, operation);
                }
                if (result != null) {
                    return ApiResult.success(request);
                } else {
                    return ApiResult.error(request, code, "更换设备、编辑信息失败");
                }
            } else {
                return ApiResult.error(request, map.get("msg").toString());
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }


    /**
     * 原微服务--安装工程师修改计费方式、设备型号
     *
     * @param code         工单号
     * @param productModel 产品id
     * @param costId       计费方式id
     * @param costName     计费方式名称
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map<String, Object> changeTypeAndModel(HttpServletRequest request, String code, String productModel, String costId, String costName) throws Exception {
        log.info("更改设备型号计费方式参数=" + " code=" + code + ",productModel=" + productModel + ",costId=" + costId + ",costName=" + costName);
        Integer engineerId = userCache.getCurrentEngineerId();
        //查询选择产品
        ProductDTO product = productFeign.getById(Integer.parseInt(productModel));
        if (product == null) {
            return ApiResult.error(request, "035", "产品数据错误");
        }
        //查询当前工单
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(code);
        if (workOrder != null && Objects.equals(workOrder.getEngineerId(), engineerId)) {
            //获取变更改的产品型号计费方式
            ProductCostDTO newCost = productFeign.productCostGetById(Integer.parseInt(costId));
            if (newCost == null) {
                return ApiResult.error(request, "035", "计费方式数据错误");
            }

            //同步售后(这里的productModel非方法参数中的productModel，传参productModel=productId，这里是型号id)
            Map<String, Object> map = BaideApiUtil.changeTypeAndModel(code, product.getOldId(), newCost.getOldId(), costName, newCost.getRentalFee().doubleValue(), newCost.getInstallationFee().doubleValue());
            log.info("baide同步更改产品型号及计费方式结果=" + JSON.toJSONString(map));
            //解析同步结果
            if (!"00000000".equals(map.get("code").toString())) {
                return ApiResult.error(request, map.get("msg").toString());
            }
            try {
                List<ProductCostDTO> costList = productFeign.productCostList(product.getId());
                boolean check = costList.stream().anyMatch(costDTO -> Objects.equals(costDTO.getId(), newCost.getId()));
                if (!check) {
                    return ApiResult.error(request, "不能更换为该计费方式，如有疑问可咨询客服！");
                }
                orderFeign.changeWorkOrderProductAndCostByEngineer(code, product.getId(), newCost.getId());
                return ApiResult.success(request);
            } catch (Exception e) {
                if (e instanceof YimaoRemoteException) {
                    return ApiResult.error(request, "035", e.getMessage());
                } else {
                    return ApiResult.error(request, "035", "更换机型遇到错误");
                }
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }

    /**
     * 原微服务--获取用户信息
     */
    public Map<String, Object> getUserInfo(HttpServletRequest request, String customerId) {
        try {
            //获取水机用户
            WaterDeviceUserDTO entity = userFeign.getWaterDeviceUserById(Integer.parseInt(customerId));
            Map<String, Object> dataMap = new HashMap<>();
            if (entity != null) {
                dataMap.put("degeree", entity.getDegree());
                dataMap.put("address", entity.getAddress());
                dataMap.put("childSex", "1");
                dataMap.put("mail", entity.getEmail());
                dataMap.put("name", entity.getRealName());
                dataMap.put("mobile", entity.getPhone());
                dataMap.put("count", null);
                dataMap.put("idCard", entity.getIdCard());
                dataMap.put("marry", "");
                dataMap.put("childAge", null);
                dataMap.put("haveOld", "");
                dataMap.put("haveChild", "");
                dataMap.put("studyAbroad", "");
                dataMap.put("hobby", "");
            }
            return ApiResult.result(request, dataMap);
        } catch (Exception var6) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    /**
     * 原微服务--修改用户信息
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Map<String, Object> setUserInfo(HttpServletRequest request, String customerId, String code, String mobile, String address, String mail, Integer count, String hobby, Integer childAge, String degeree, boolean haveChild, boolean haveOld, String childSex, boolean marry, boolean studyAbroad) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        Map<String, Object> ru;
        if (entity != null && Objects.equals(entity.getEngineerId(), engineerId)) {
            //获取水机用户
            WaterDeviceUserDTO userDTO = userFeign.getWaterDeviceUserById(Integer.parseInt(customerId));
            if (userDTO == null) {
                throw new BadRequestException(customerId + "该用户信息未找到");
            }
            if (StringUtil.isNotEmpty(mobile)) {
                userDTO.setPhone(mobile);
            }
            if (StringUtil.isNotEmpty(address)) {
                userDTO.setAddress(address);
            }
            userDTO.setEmail(mail);
            userDTO.setDegree(degeree);
            userDTO.setId(Integer.parseInt(customerId));
            log.info("水机用户更新=" + JSON.toJSONString(userDTO));
            userFeign.updateDeviceUserInfo(userDTO);
            //个人信息确认
            // entity.setUserStatusTime(new Date());
            // entity.setConfirmUserInfoStatus(StatusEnum.YES.value());
            // entity.setWorkorderConfirmInformationStatusText("个人信息已确认");
            entity.setOperationTime(new Date());
            try {
                Map<String, Object> map = BaideApiUtil.confirm(code);
                ru = this.getMapData(request, entity, map);
            } catch (Exception var26) {
                return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
            }

            int step = WorkOrderInstallStep.MSG.value;
            if (StatusEnum.isYes(entity.getSignStatus()) && entity.getPay()) {
                step = WorkOrderInstallStep.BILL.value;
                Map<String, Object> map = new HashMap<>();
                try {
                    map = BaideApiUtil.finishWorkOrderInstall(code);
                } catch (Exception var25) {
                    var25.printStackTrace();
                }
                if ("00000000".equals(map.get("code").toString())) {
                    orderFeign.completeWorkOrder(code);
                }
            } else {
                entity.setStatus(WorkOrderStatusEnum.INSTALLING.value);
                entity.setStep(WorkOrderInstallStep.MSG.value);
                orderFeign.updateWorkOrder(entity);
            }
        } else {
            ru = ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
        return ru;
    }

    private Map<String, Object> getMapData(HttpServletRequest request, WorkOrderDTO dto, Map<String, Object> map) {
        Map<String, Object> ru = new HashMap<>();
        if ("00000000".equals(map.get("code").toString())) {
            JSONObject obj = JSONObject.fromObject(map.get("data"));
            String nextStep = obj.getString("nextStep");
            dto.setNextStep(Integer.parseInt(nextStep));
            orderFeign.updateWorkOrder(dto);
            return ApiResult.result(request, nextStep);
        } else {
            return ApiResult.error(request, map.get("msg").toString());
        }
    }

    /**
     * 原微服务--获取未签署完成的合同
     */
    public Map<String, Object> getContract(HttpServletRequest request, String code) {
        log.info("获取未签署完成的合同");
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        if (entity != null && Objects.equals(entity.getEngineerId(), engineerId)) {
            String yunsignOrderId = entity.getSignOrderId();
            Map<String, Object> data = new HashMap<>();
            if (StringUtil.isNotEmpty(yunsignOrderId) && !StatusEnum.isYes(entity.getSignStatus())) {
                // String path = request.getContextPath();
                // String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
                // String signUrl = basePath + "/api/yunsign/viewContract?userId=" + entity.getSignUserPhone() + "&codeId=" + yunsignOrderId;
                String signUrl = viewContract(entity.getSignUserPhone(), yunsignOrderId);
                data.put("success", true);
                data.put("url", signUrl);
                return ApiResult.result(request, data);
            } else {
                data.put("success", false);
                return ApiResult.result(request, data);
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }

    /**
     * 原微服务--用户签署合同
     */
    public Map<String, Object> contract(HttpServletRequest request, String code, String confirmation, String userId, int year, String name, String phone, String idCard, String mail, String address) {
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        if (entity != null && Objects.equals(entity.getEngineerId(), engineerId)) {
            if (!StatusEnum.isYes(entity.getSignStatus())) {
                Map<String, Object> map;
                try {
                    map = BaideApiUtil.saveSignTypeApp(code, "1", confirmation, name, phone, idCard, mail, address, String.valueOf(year));
                } catch (Exception var27) {
                    return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
                }
                if ("00000000".equals(map.get("code").toString())) {
                    log.info("合法，开始执行操作");
                    String orderId = "YMAPP" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + (new Random().nextInt(9999) + 10000) + code;
                    ProductCostDTO cost = productFeign.productCostGetById(entity.getCostId());
                    String completeNotice = "";
                    int completeOpenAccount = entity.getOpenAccountFee().intValue();
                    int homeCostPrice = 0;
                    int homeOpenAccount = 0;
                    int busineseCostPrice = 0;
                    int busineseOpenAccount = 0;
                    //翼猫V2.0不需要完款，所以完款状态默认'N'，
                    Integer productChargeTypeId = entity.getCostType();
                    if ("家用".equals(YunOldIdUtil.getProductScope(entity.getDeviceModel()))) {
                        homeCostPrice = cost.getRentalFee().intValue();
                        homeOpenAccount = cost.getInstallationFee().intValue();
                    }

                    if ("商用".equals(YunOldIdUtil.getProductScope(entity.getDeviceModel()))) {
                        busineseCostPrice = cost.getRentalFee().intValue();
                        busineseOpenAccount = cost.getInstallationFee().intValue();
                    }

                    boolean isExperience = entity.getDistributorType() != null && DistributorRoleLevel.D_50.value == entity.getDistributorType();

                    Map<String, Object> contractMap = contractUtil.signContract(name, entity.getDeviceModel(), entity.getSn(), entity.getProvince(), entity.getCity(), entity.getRegion(), entity.getAddress(), isExperience, completeNotice, productChargeTypeId, homeCostPrice, homeOpenAccount, busineseCostPrice, busineseOpenAccount, completeOpenAccount, phone, idCard, year, orderId, mail);
                    if (contractMap != null && contractMap.containsKey("success") && (Boolean) contractMap.get("success")) {
                        Date now = new Date();
                        entity.setContractValidity(year);
                        entity.setConfirmation("");
                        //entity.setSignStatus(StatusEnum.YES.value());
                        entity.setSignTime(now);
                        entity.setSignOrderId(orderId);
                        entity.setSignUserName(name);
                        entity.setSignUserPhone(phone);
                        entity.setSignUserEmail(mail);
                        entity.setSignUserAddress(address);
                        if (StringUtil.isNotEmpty(idCard)) {
                            entity.setSignUserIdCard(idCard);
                            entity.setUserIdCard(idCard);
                        }
                        entity.setSignClient("app");
                        entity.setOperationTime(now);
                        WorkOrderDTO result = orderFeign.updateWorkOrder(entity);
                        if (result != null) {
                            log.info("操作结束");
                            return ApiResult.result(request, viewContract(phone, orderId));
                            // return ApiResult.result(request, domainProperties.getApi() + "/api/yunsign/viewContract?userId=" + phone + "&codeId=" + orderId);
                        } else {
                            return ApiResult.error(request, code, "用户签署合同失败");
                        }
                    } else {
                        return ApiResult.error(request, contractMap.get("msg").toString());
                    }
                } else {
                    return ApiResult.error(request, map.get("msg").toString());
                }
            } else {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_SIGN_SUCCESS);
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
    }

    /**
     * 根据手机号和云签单号获取云签合同访问地址
     *
     * @param userId 用户签约手机号
     * @param codeId 签约单号
     */
    private String viewContract(String userId, String codeId) {
        // WorkOrderDTO workOrder = orderFeign.getWorkOrderBySignOrderId(codeId);
        // if (workOrder != null && com.yimao.cloud.base.baideApi.utils.StringUtil.isNotEmpty(workOrder.getSignUserPhone())) {
        //     userId = workOrder.getSignUserPhone();
        // }

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
        String sign = com.yimao.cloud.base.baideApi.utils.StringUtil.encodeMD5(md5str).toLowerCase();
        String url = domainProperties.getYunSign() + "/mmecserver3.0/sign.do?time=" + time + "&sign=" + sign
                + "&signType=" + signType + "&userId=" + userId + "&isHandWrite=" + isHandWrite + "&isForceSeal=" + isForceSeal
                + "&isSignFirst=" + isSignFirst + "&isSeal=" + isSeal + "&validType=" + validType + "&appId=" + appId + "&orderId=" + codeId;
        log.info("云签合同访问地址为：{}", url);
        return url;
    }

    /**
     * 原微服务--用户公众号签署合同
     */
    public Map<String, Object> wechatContract(HttpServletRequest request, String code, String confirmation) {
        Map<String, Object> ru = new HashMap<>();
        Integer engineerId = userCache.getCurrentEngineerId();
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        if (entity != null && Objects.equals(entity.getEngineerId(), engineerId)) {
            Map<String, Object> map;
            try {
                map = BaideApiUtil.saveSignTypeWechat(code, "0", confirmation);
            } catch (Exception var9) {
                return ApiResult.error(request, ApiStatusCode.BAIDE_ERROR);
            }
            if ("00000000".equals(map.get("code").toString())) {
                entity.setConfirmation("");
                entity.setSignClient("wechat");
                WorkOrderDTO result = orderFeign.updateWorkOrder(entity);
                if (result != null) {
                    ru = ApiResult.success(request);
                }
            } else {
                ru = ApiResult.error(request, map.get("msg").toString());
            }
        } else {
            ru = ApiResult.error(request, ApiStatusCode.DATA_ERROR);
        }
        return ru;
    }

    public ResultBean<WorkOrderDTO> signSuccess(String code, String orderId, String signClient) {
        WorkOrderDTO entity = orderFeign.getWorkOrderById(code);
        if (entity != null && StatusEnum.isNo(entity.getSignStatus())) {
            Date now = new Date();
            entity.setSignTime(now);
            entity.setSignOrderId(orderId);
            entity.setSignClient(signClient);
            entity.setSignStatus(StatusEnum.YES.value());
            // entity.setWorkorderSignContractStatusText("用户签署合同成功");
            entity.setOperationTime(now);
            try {
                entity = orderFeign.updateWorkOrder(entity);
                if (entity != null) {
                    return ResultBean.success(entity);
                }
            } catch (Exception var6) {
                return ResultBean.fastFailServerException();
            }
        }

        return ResultBean.fastFail(ApiStatusCode.SER_EXCEPTION.getTextZh());
    }

    /**
     * 原微服务--订单查询
     */
    public Map<String, Object> getOrderByOrderId(HttpServletRequest request, String workOrderId, String province, String city, String region, String consumerName, String consumerPhone, String dealerAccount, String dealerName, String dealerPhone, String serviceSiteId, Integer page, Integer pageSize) {
        log.info("原微服务--订单查询 -start");
        Integer distributorId = null;
        if (StringUtil.isNotEmpty(dealerAccount)) {
            DistributorDTO distributor = userFeign.getDistributorByUserName(dealerAccount);
            if (distributor == null) {
                return OpenApiResult.error(request, OpenApiStatusCode.DISTRIBUTOR_ACCOUNT_EXISTS);
            }
            distributorId = distributor.getId();
        }
        WorkOrderQueryDTO query = new WorkOrderQueryDTO();
        query.setId(workOrderId);
        query.setDistributorId(distributorId);
        if (StringUtil.isNotEmpty(province) || StringUtil.isNotEmpty(city) || StringUtil.isNotEmpty(region)) {
            List<AreaDTO> areaList = systemFeign.areaList();
            if (StringUtil.isNotEmpty(province)) {
                province = this.getAreaNameById(province, areaList);
            }
            if (StringUtil.isNotEmpty(city)) {
                city = this.getAreaNameById(city, areaList);
            }
            if (StringUtil.isNotEmpty(region)) {
                region = this.getAreaNameById(region, areaList);
            }
        }
        query.setProvince(province);
        query.setCity(city);
        query.setRegion(region);
        query.setUserName(consumerName);
        query.setUserPhone(consumerPhone);
        query.setDistributorName(dealerName);
        query.setDistributorPhone(dealerPhone);
        if (StringUtil.isNotEmpty(serviceSiteId)) {
            StationDTO station = systemFeign.getStationByOldId(serviceSiteId);
            if (station == null) {
                return OpenApiResult.error(request, OpenApiStatusCode.PARAM_ERROR);
            }
            query.setStationId(station.getId());
        }
        PageVO<WorkOrderDTO> workOrderPage = orderFeign.getWorkOrderList(query, page, pageSize);
        List<WorkOrderDTO> workorderList = workOrderPage.getResult();
        if (CollectionUtil.isEmpty(workorderList)) {
            return OpenApiResult.error(request, OpenApiStatusCode.DATA_NOT_FOUND);
        }
        return OpenApiResult.result(request, this.getDataList(workorderList));
    }

    private String getAreaNameById(String province, List<AreaDTO> areaList) {
        for (AreaDTO area : areaList) {
            if (area.getId() == Integer.parseInt(province)) {
                return area.getName();
            }
        }
        return null;
    }

    /**
     * 原微服务--订单查询
     */
    public Map<String, Object> getOrderList(HttpServletRequest request, String serviceSiteId, Integer page, Integer pageSize) {
        log.info("原微服务--订单查询 -start");
        WorkOrderQueryDTO query = new WorkOrderQueryDTO();
        if (StringUtil.isNotEmpty(serviceSiteId)) {
            StationDTO station = systemFeign.getStationByOldId(serviceSiteId);
            if (station == null) {
                return ApiResult.error(request, ApiStatusCode.SERVICE_SITE_DEVICE_NOT_FOUND);
            }
            query.setStationId(station.getId());
        }
        PageVO<WorkOrderDTO> workOrderPage = orderFeign.getWorkOrderList(query, page, pageSize);
        List<WorkOrderDTO> workorderList = workOrderPage.getResult();
        if (CollectionUtil.isEmpty(workorderList)) {
            return ApiResult.error(request, ApiStatusCode.SER_NO_DATA);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("data", this.getDataList(workorderList));
        map.put("count", workOrderPage.getTotal());
        return ApiResult.result(request, map);
    }

    /**
     * 原微服务--获取支付凭证图片
     *
     * @param code
     * @return
     */
    public Map<String, Object> getImagesUrl(HttpServletRequest request, String code) {
        Map<String, Object> ru = new HashMap<>();
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(code);
        if (workOrder != null) {
            String images = workOrder.getPayCredential();
            if (images != null) {
                ru = OpenApiResult.result(request, images);
            } else {
                ru = OpenApiResult.error(request, "未找到图片信息");
            }
        } else {
            ru = OpenApiResult.error(request, "未找到工单信息");
        }
        return ru;
    }

    /**
     * @description 原微服务--总部修改预约时间
     */
    public Map<String, Object> updateAppointTime(HttpServletRequest request, String code, String time) {
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(code);
        if (workOrder != null) {
            try {
                //预约时间
                workOrder.setAppointTime(DateUtil.transferStringToDate(time));
                Date currentTime = new Date();
                //倒计时间
//                Date countdownTime = new Date(currentTime.getTime() - workOrder.getAppointTime().getTime());
//                workOrder.setCountdownTime(countdownTime);
                //原微服务代码是预约时间
                workOrder.setCountdownTime(DateUtil.transferStringToDate(time));
                WorkOrderDTO dto = orderFeign.updateWorkOrder(workOrder);
                if (dto != null) {
                    return ApiResult.success(request);
                } else {
                    return ApiResult.error(request, ResultBean.fastFailFail().getStatusText());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ApiResult.error(request, ResultBean.fastFailServerException().getStatusText());
            }
        } else {
            return OpenApiResult.error(request, OpenApiStatusCode.DATA_NOT_FOUND);
        }
    }

    /**
     * 原微服务--查看工单经销商评价信息
     *
     * @param code
     * @return
     */
    public Map<String, Object> appraisal(HttpServletRequest request, String code) {
        WorkOrderDTO workorder = orderFeign.getWorkOrderById(code);
        if (workorder != null) {
            Map<String, Object> ru = new HashMap();
            if ((StatusEnum.isYes(workorder.getDistributorAppraiseStatus()))) {
                ru.put("content", workorder.getDistributorAppraiseContent());
                ru.put("appraiseTime", workorder.getDistributorAppraiseTime() == null ? "" : DateUtil.transferDateToString(workorder.getDistributorAppraiseTime(), "yyyy-MM-dd HH:mm:ss"));
                return OpenApiResult.result(request, ru);
            } else {
                return OpenApiResult.error(request, "经销商未评价");
            }
        } else {
            return OpenApiResult.error(request, OpenApiStatusCode.DATA_NOT_FOUND);
        }
    }

    /**
     * @description 原微服务--工单评分
     */
    public Map<String, Object> rating(HttpServletRequest request, String orderId, Integer score, String opinion, String levelId, String level, boolean automatic) {
        WorkOrderDTO workOrder = orderFeign.updateWorkOrderAppraise(orderId, level);
        if (workOrder != null) {
            if (automatic) {
                WorkOrderDTO disDto = orderFeign.updateDistributorApprise(orderId, 2);
                if (disDto != null) {
                    //SyncYimaoWorkOrder.syncWorkOrder(9, 4, disDto);
                }
            } else {
                //SyncYimaoWorkOrder.syncWorkOrder(-1, 4, workOrder);
            }

            boolean goodVote = false;
            if (level.equals("好")) {
                goodVote = true;
            }

            ResultBean<WorkOrderDTO> resultBean = this.voteWorkOrder(orderId, goodVote);
            if (resultBean.isSuccess()) {
                Long subOrderId = workOrder.getSubOrderId();
                String engineerId = workOrder.getEngineerId().toString();
                String province = workOrder.getProvince();
                String city = workOrder.getCity();
                String region = workOrder.getRegion();
                String businessId = workOrder.getId().toString();
                String goodsSystemId = "waterdevice";
                //评价统计从订单转到工单上
                WorkOrderDTO orderEntity = resultBean.getResultData();
                if (orderEntity.getGoodVoteCount() > 1) {
                    //TODO 红包相关
//                        CommonAddressInfoEntity address = this.commonAddressApi.getAddressInfo(province, city, region);
//                        ResultBean<RedEntity> masterBean = this.masterRedApi.applyMasterRed(address, engineerId, goodsSystemId, businessId, order_Id);
//                        BuguLogger.info("engineerId==" + engineerId + "businessId==" + businessId + "orderId===" + order_Id + "user master red state == " + masterBean.getStatusText(), new Object[0]);
//                        if (masterBean.isSuccess()) {
//                            this.workOrderInstallApi.updateMasterRed(businessId, ((RedEntity)masterBean.getResultData()).getId());
//                        }
                }
            }

            return OpenApiResult.success(request);
        } else {
            return OpenApiResult.error(request, OpenApiStatusCode.RATING_ERROR);
        }
    }

    // /**
    //  * @description 原微服务--经销商7日未退单
    //  */
    // public Map automatic(HttpServletRequest request, String code) {
    //     WorkOrderDTO workorder = orderFeign.getWorkOrderById(code);
    //     if (workorder != null) {
    //         String backResult = "";
    //         String completeTime = "";
    //         WorkOrderDTO result = null;
    //         //自动退单
    //         workorder.setDistributorVerifyBackOrderTime(new Date());
    //         workorder.setDistributorVerifyBackOrderType(WorkOrderDistributorVerifyEnum.BACKORDER_AUTO.getVerifyType());
    //         if (workorder.getPay() != null && workorder.getPay()) {
    //             backResult = "complete";
    //             //取消订单
    //             List<Long> ids = new ArrayList<>();
    //             ids.add(workorder.getSubOrderId());
    //             orderFeign.updateOrderStatusBatch("安装单退单", null, ids);
    //             workorder.setCompleteTime(new Date());
    //             workorder.setStatus(WorkOrderStatusEnum.COMPLETED.value);
    //             workorder.setCompleteType(WorkOrderCompleteEnum.AUTO_COMPLETE.getState());//异常完成
    //             workorder.setStatusText("已完成");
    //             workorder.setChargeback(true);
    //             workorder.setChargebackStatus(2);
    //             workorder.setChargebackReason("经销商7日退单");
    //             workorder.setChargebackRemark("经销商7日退单");
    //             workorder.setChargebackSncode(workorder.getSn());
    //             workorder.setChargebackTime(new Date());
    //             result = orderFeign.updateWorkOrder(workorder);
    //             if (result != null) {
    //                 completeTime = DateUtil.dateToString(workorder.getCompleteTime());
    //             }
    //             if (workorder.getDeviceId() != null) {
    //                 WaterDeviceDTO device = new WaterDeviceDTO();
    //                 device.setId(workorder.getDeviceId());
    //                 device.setOnline(false);
    //                 device.setLastOnlineTime(new Date());
    //                 //移除设备
    //                 waterFeign.updateDevice(device);
    //             }
    //         } else {
    //             backResult = "agree";
    //             workorder.setChargebackStatus(1);//退单中
    //             workorder.setChargebackReason("自动退单");
    //             workorder.setChargebackRemark("自动退单");
    //             workorder.setChargebackTime(new Date());
    //             result = orderFeign.updateWorkOrder(workorder);
    //         }
    //
    //         if (result != null) {
    //             return ApiResult.success(request);
    //         } else {
    //             return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
    //         }
    //     } else {
    //         return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
    //     }
    // }

    /**
     * @description 原微服务--7日自动完成
     */
    public Map completeAutomatic(HttpServletRequest request, String workOrderId) {
        Date now = new Date();
        WorkOrderDTO update = new WorkOrderDTO();
        update.setId(workOrderId);
        update.setOperationTime(now);
        update.setCompleteType(WorkOrderCompleteEnum.AUTO_COMPLETE.getState());
        update.setSignClient("wechat");
        update.setDistributorAppraiseStatus(StatusEnum.YES.value());
        update.setDistributorAppraiseContent("一般");
        update.setDistributorAppraiseTime(now);
        orderFeign.updateWorkOrderPart(update);
        return ApiResult.success(request);
    }

    /**
     * @description 原微服务--总部退单审核结果
     */
    public Map backNotice(HttpServletRequest request, String code) {
        WorkOrderDTO workorder = orderFeign.getWorkOrderById(code);
        if (workorder != null) {
            //取消订单
            List<Long> ids = new ArrayList<>();
            ids.add(workorder.getSubOrderId());
            orderFeign.updateOrderStatusBatch("总部审核退单通过", null, ids);

            workorder.setChargeback(true);
            workorder.setChargebackStatus(2);
            workorder.setChargebackTime(new Date());
            WorkOrderDTO result = orderFeign.updateWorkOrder(workorder);
            if (result.getDeviceId() != null) {
                WaterDeviceDTO device = new WaterDeviceDTO();
                device.setId(result.getDeviceId());
                device.setOnline(false);
                device.setLastOnlineTime(new Date());
                waterFeign.updateDevice(device);
            }
            return ApiResult.success(request);
        } else {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    /**
     * @description 原微服务--修改安装确认单号
     */
    public Map putConfirmationNamber(String code, String confirmationNumber, String sureTime, HttpServletRequest request) {
        Date formatSureTime = DateUtil.stringToDate(sureTime);
        if (ObjectUtil.isNull(formatSureTime)) {
            return ApiResult.error(request, "时间格式错误");
        } else if (!StringUtil.isEmpty(code) && !StringUtil.isEmpty(confirmationNumber) && !ObjectUtil.isNull(sureTime)) {
            WorkOrderDTO dto = orderFeign.getWorkOrderById(code);
            if (ObjectUtil.isNull(dto)) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
            } else {
                dto.setConfirmation(confirmationNumber);
                dto.setSignClient("wechat");
                WorkOrderDTO result = orderFeign.updateWorkOrder(dto);
                if (result != null) {
//                    SyncYimaoWorkOrder.updateContractionNum(code, confirmationNumber);
                    return ApiResult.success(request);
                } else {
                    return ApiResult.error(request, code, "修改安装确认单号失败");
                }
            }
        } else {
            return ApiResult.error(request, ApiStatusCode.SER_PARAM_ERROR);
        }
    }

    /**
     * 原微服务返回给安装工APP的工单列表字段
     */
    public Map<String, Object> getData(WorkOrderDTO workorder) {
        Map<String, Object> map = new HashMap<>();
        //TODO 红包相关
        map.put("redAssembly", false);

        map.put("userAppraise", StatusEnum.isYes(workorder.getUserAppraiseStatus()));
        map.put("distributorAppraise", StatusEnum.isYes(workorder.getDistributorAppraiseStatus()));
        if ("wechat".equals(workorder.getSignClient())) {
            map.put("wechatSign", true);
        } else {
            map.put("wechatSign", false);
        }

        if (StringUtil.isNotEmpty(workorder.getAppointAddress())) {
            map.put("address", workorder.getAppointAddress());
        } else {
            map.put("address", workorder.getAddress());
        }

        map.put("province", workorder.getProvince());
        map.put("city", workorder.getCity());
        map.put("region", workorder.getRegion());
        map.put("code", workorder.getId());
        map.put("orderId", workorder.getSubOrderId());
        map.put("payFee", AmountUtils.convertTo2NumPoint(workorder.getFee()));
        map.put("workorderId", workorder.getId());
        ProductCostDTO charge = productFeign.productCostGetById(workorder.getCostId());
        map.put("chargeTypeId", workorder.getCostId().toString());
        map.put("chargeTypeName", workorder.getCostName());
        if (charge != null) {
            map.put("chargeTypePrice", AmountUtils.convertTo2NumPoint(charge.getTotalFee()));
        }
        if (workorder.getStatus() == 1) {
            map.put("state", 1);
        } else if (workorder.getStatus() == 2 || workorder.getStatus() == 3) {
            map.put("state", 2);
        } else if (workorder.getStatus() == 4) {
            map.put("state", 3);
        }
        map.put("productName", workorder.getDeviceModel());
        //TODO 为了应对多个产品获取不到对应的计费方式，所以这里去新系统的产品ID传给客户端
        // map.put("productId", YunOldIdUtil.getProductId(workorder.getDeviceModel()));
        map.put("productId", workorder.getProductId());
        // map.put("productScope", workorder.getProductRangeName());
        // map.put("productScopeId", workorder.getProductRangeId());
        map.put("productScope", YunOldIdUtil.getProductScope(workorder.getDeviceModel()));
        map.put("productScopeId", YunOldIdUtil.getProductScopeId(workorder.getDeviceModel()) + "_" + workorder.getProductId());
        map.put("remark", workorder.getRemark());
        map.put("appointRemark", workorder.getAppointRemark());
        if (workorder.getNextStep() != null) {
            map.put("step", workorder.getNextStep() - 1);
        } else {
            map.put("step", 0);
        }
        // map.put("step", workorder.getStep());

        //倒计时计算时间根据创建时间计算,如果存在预约,则从预约时间开始计算
        Date startTime = null;
        boolean isBespeak = StatusEnum.isYes(workorder.getAppointStatus());
        if (isBespeak) {
            startTime = workorder.getAppointTime();
        } else {
            startTime = workorder.getCreateTime();
        }

        Map<String, Object> configMap = WaterDeviceWorkOrderCompleteDateColorJob.getDateInfo(WorkOrderTypeEnum.ORDER_TYPE_INSTALL.getType(), startTime, isBespeak, "");
        map.put("surplusTime", configMap.get("dateStr"));
        map.put("color", configMap.get("color"));
        map.put("count", 1);
        map.put("orderType", "0");

        if (workorder.getPayType() != null && (workorder.getPayType() == PayType.POS.value || workorder.getPayType() == PayType.TRANSFER.value)) {
            map.put("otherPay", true);
            if (workorder.getPayStatus() == PayStatus.FAIL.value) {
                map.put("pass", String.valueOf(FinancialStateEnum.UN_PASS_AUDIT.value));
            } else if (workorder.getPayStatus() == PayStatus.PAY.value) {
                map.put("pass", String.valueOf(FinancialStateEnum.PASS_AUDIT.value));
            } else {
                map.put("pass", String.valueOf(FinancialStateEnum.UN_AUDIT.value));
            }
        }

        map.put("customerId", workorder.getUserId());
        map.put("customerName", workorder.getUserName());
        map.put("customerPhone", workorder.getUserPhone());
        if (workorder.getSubDistributorId() != null) {
            map.put("distributorName", workorder.getSubDistributorName());
            map.put("distributorPhone", workorder.getSubDistributorPhone());
        } else {
            map.put("distributorName", workorder.getDistributorName());
            map.put("distributorPhone", workorder.getDistributorPhone());
        }

        map.put("distributorId", workorder.getDistributorId());
        map.put("engineerName", workorder.getEngineerName());
        map.put("engineerPhone", workorder.getEngineerPhone());
        map.put("createTime", DateUtil.getDateToString(workorder.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        if (null != workorder.getAppointTime()) {
            map.put("planTime", DateUtil.getDateToString(workorder.getAppointTime(), "yyyy-MM-dd HH:mm:ss"));
        }

        map.put("sncode", workorder.getSn());
        map.put("iccid", workorder.getSimCard());
        map.put("batchCode", workorder.getLogisticsCode());
        if (StringUtil.isNotEmpty(workorder.getSn())) {
            map.put("materielCode", workorder.getMaterielCode());
        }

        map.put("ismunicipal", workorder.getMunicipal());
        map.put("tds", workorder.getTds());
        map.put("hydraulic", workorder.getHydraulic());
        map.put("otherSource", workorder.getOtherSource());
        map.put("backReason", workorder.getChargebackReason());
        map.put("backRemark", workorder.getChargebackRemark());
        map.put("isBackOrder", workorder.getChargeback());
        if (workorder.getChargebackStatus() != null && (Objects.equals(WorkOrderChargebackEnum.BACKING.getState(), workorder.getChargebackStatus().toString())
                || Objects.equals(WorkOrderChargebackEnum.BACK_WAIT.getState(), workorder.getChargebackStatus().toString())
                || Objects.equals(WorkOrderChargebackEnum.BACK_WAIT_YUN.getState(), workorder.getChargebackStatus().toString()))) {
            map.put("waitback", true);
        }

        map.put("isPaid", workorder.getPay());
        if (null != workorder.getCompleteTime()) {
            map.put("completeTime", DateUtil.getDateToString(workorder.getCompleteTime(), "yyyy-MM-dd HH:mm:ss"));
        }

        if (workorder.getPayTerminal() == PayTerminal.USER.value) {
            map.put("payterminal", 0);
        } else if (workorder.getPayTerminal() == PayTerminal.DEALER.value) {
            map.put("payterminal", PayTerminal.DEALER.value);
        }
        map.put("appraise", workorder.getDistributorAppraiseContent());
        if (StringUtil.isNotEmpty(workorder.getConfirmation())) {
            map.put("confirmation", workorder.getConfirmation());
        }

        map.put("productType", "1");
        map.put("productModel", workorder.getDeviceModel());
        return map;
    }

    public boolean confirmIdCard(String code) {
        try {
            Map<String, Object> idCardMap = BaideApiUtil.confirmIdCard(code);
            if ("00000000".equals(idCardMap.get("code").toString())) {
                JSONObject obj = JSONObject.fromObject(idCardMap.get("data"));
                if (!this.checkJsonNull(obj.get("flag"))) {
                    if (1 == obj.getInt("flag")) {
                        return true;
                    }
                    if (0 == obj.getInt("flag")) {
                        return false;
                    }
                }
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }
        return false;
    }

    private boolean checkJsonNull(Object o) {
        if (o instanceof JSONNull) {
            return true;
        } else {
            return o == null;
        }
    }

    private String getImages(Map<String, String> map1, Map<String, String> map2, Map<String, String> map3) {
        Map<String, String> all = new HashMap();
        all.put("standard1", map1.get("standard"));
        all.put("standard2", map2.get("standard"));
        all.put("standard3", map3.get("standard"));
        all.put("small1", map1.get("small"));
        all.put("small2", map2.get("small"));
        all.put("small3", map3.get("small"));
        return JSONObject.fromObject(all).toString();
    }

    private Map<String, String> getFileId(String img) {
        Map<String, String> map = new HashMap<>();
        try {
            map.put("standard", img);
            map.put("small", img);
        } catch (Exception e) {
            throw new YimaoException("图片上传失败");
        }
        return map;
    }

//    private Map<String, String> getFileId(HttpServletRequest request, String workorderId, MultipartFile multiFile) {
//        String path = request.getRealPath("");
//        File file = new File(path + "/" + multiFile.getOriginalFilename());
//        Map image = null;
//
//        try {
//            multiFile.transferTo(file);
//            FileInputStream fis = new FileInputStream(file);
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            byte[] b = new byte[1024];
//
//            int n;
//            while ((n = fis.read(b)) != -1) {
//                bos.write(b, 0, n);
//            }
//
//            fis.close();
//            bos.close();
////            image = this.fileUploadApi.saveImage(Moudles.BUSINESS_WATER_DEVICE.getId(), Moudles.BUSINESS_WATER_DEVICE.getMoudleName(), workorderId, file.getName(), multiFile.getContentType(), bos.toByteArray(), true);
//            file.delete();
//        } catch (Exception var11) {
//            var11.printStackTrace();
//        }
//
//        return image;
//    }


    public ResultBean<WorkOrderDTO> voteWorkOrder(String code, boolean goodVote) {
        WorkOrderDTO workorder = orderFeign.getWorkOrderById(code);
        if (workorder == null) {
            return ResultBean.fastFailFail();
        }
        //评价统计从订单转到工单上
        int goodVoteCount = workorder.getGoodVoteCount();
        int voteCount = workorder.getVoteCount() + 1;
        if (goodVote) {
            ++goodVoteCount;
        }
        if (voteCount < goodVoteCount) {
            log.error("好评数不能大于总评价数！参数错误！");
            throw new BadRequestException("好评数不能大于总评价数！参数错误!");
        } else if (voteCount < 1) {
            log.error("未产生评价数据，不予处理");
            throw new BadRequestException("未产生评价数据，不予处理!");
        }
        workorder.setGoodVoteCount(goodVoteCount);
        workorder.setVoteCount(voteCount);
        workorder.setVoteStatus(StatusEnum.YES.value());
        WorkOrderDTO bean = orderFeign.updateWorkOrder(workorder);
        if (bean == null) {
            return ResultBean.fastFailFail();
        }
        return ResultBean.success(workorder);
    }

    public List<WorkOrderDTO> getBelongDate(List<WorkOrderDTO> workorders, List<Integer> ids) {
        List<WorkOrderDTO> result = new ArrayList<>();
        if (!ObjectUtil.isNull(ids) && ids.size() > 0) {
            workorders.forEach((workorder) -> {
                Integer distributoId = workorder.getDistributorId();
                if (ids.contains(distributoId)) {
                    result.add(workorder);
                }
            });
            return result;
        } else {
            return result;
        }
    }

    private List<Map<String, Object>> getDataList(List<WorkOrderDTO> installEntitys) {
        List<Map<String, Object>> results = new ArrayList();
        installEntitys.forEach((installEntity) -> {
            results.add(getDatas(installEntity));
        });
        return results;
    }

    private Map<String, Object> getDatas(WorkOrderDTO installEntity) {
        Map<String, Object> paramMap = new HashMap();
//        OrderSubDTO order = orderFeign.getOrder(installEntity.getSubOrderId());
//        paramMap.put("orderId", installEntity.getOldSubOrderId());
        paramMap.put("orderId", Objects.isNull(installEntity.getOldSubOrderId()) ? installEntity.getId() : installEntity.getOldSubOrderId());
        Integer isassign = 0;
        if (installEntity.getDispatchType() != null && (installEntity.getDispatchType() == 1 || installEntity.getDispatchType() == 2)) {
            isassign = 1;
        }
        paramMap.put("isassign", isassign);
        paramMap.put("productRangeName", installEntity.getDeviceModel());
        paramMap.put("chargingTypeName", installEntity.getCostName());
        paramMap.put("goodsNum", installEntity.getCount());

//        if (!ObjectUtil.isNull(order)) {
//            paramMap.put("goodsNum", order.getCount());
//        }

        String payType = getPayTypeZNText(installEntity.getPayType());
        paramMap.put("payType", payType);
        paramMap.put("payStyle", installEntity.getPayTerminal());
        paramMap.put("dealerId", installEntity.getOldDistributorId());
        paramMap.put("dealerName", installEntity.getDistributorName());
        paramMap.put("dealerPhone", installEntity.getDistributorPhone());
        paramMap.put("dealerProvince", installEntity.getDistributorProvince());
        paramMap.put("dealerCity", installEntity.getDistributorCity());
        paramMap.put("dealerArea", installEntity.getDistributorRegion());
        paramMap.put("dealerAccount", installEntity.getDistributorAccount());

//        Integer userId = userCache.getUserId();
//        if (userId == null) {
//        if (Objects.nonNull(installEntity.getDistributorId())) {
//            DistributorDTO userAccountEntity = userFeign.getDistributorById(installEntity.getDistributorId());
//            if (!ObjectUtil.isNull(userAccountEntity)) {
//                paramMap.put("dealerProvince", userAccountEntity.getProvince());
//                paramMap.put("dealerCity", userAccountEntity.getCity());
//                paramMap.put("dealerArea", userAccountEntity.getRegion());
//                paramMap.put("dealerAccount", userAccountEntity.getUserName());
////            }
//            }
//        }

        paramMap.put("engineerId", installEntity.getOldEngineerId());
        paramMap.put("engineerName", installEntity.getEngineerName());
        paramMap.put("engineerPhone", installEntity.getEngineerPhone());
        paramMap.put("consumerId", installEntity.getOldUserId());
        paramMap.put("consumerName", installEntity.getUserName());
        paramMap.put("consumerPhone", installEntity.getUserPhone());
        paramMap.put("consumerProvince", installEntity.getProvince());
        paramMap.put("consumerCity", installEntity.getCity());
        paramMap.put("consumerArea", installEntity.getRegion());
        return paramMap;
    }

    private String getPayTypeZNText(Integer data) {
        if (Objects.isNull(data)) {
            return "未支付";
        }
        String payType = "";
        if (PayType.ALIPAY.value == data) {
            payType = "支付宝支付";
        } else if (PayType.WECHAT.value == data) {
            payType = "微信支付";
        } else if (PayType.POS.value == data || PayType.TRANSFER.value == data) {
            payType = "其他支付";
        }
        return payType;
    }

    /**
     * 签署合同成功后设置工单为已完成状态
     *
     * @param code 工单号
     */
    public void completeWorkOrderAfterSign(String code) {
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(code);
        if (workOrder == null) {
            log.error("签署合同成功后设置工单（{}）为已完成状态-失败，未查询到工单信息", code);
            return;
        }
        if (!workOrder.getPay()) {
            log.error("签署合同成功后设置工单（{}）为已完成状态-失败，工单未支付", code);
            return;
        }
        if (workOrder.getPayStatus() == null || workOrder.getPayStatus() != 3) {  //支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败
            log.error("签署合同成功后设置工单（{}）为已完成状态-失败，工单未支付成功", code);
            return;
        }
        //通知百得完成工单
        Map<String, Object> result;
        try {
            result = BaideApiUtil.finishWorkOrderInstall(code);
            log.info("通知百得完成工单 工单号={}，result={}", code, result);
        } catch (Exception e) {
            log.error("签署合同成功后设置工单（{}）为已完成状态-失败，调百得接口异常", code);
            return;
        }
        if (result == null) {
            log.error("签署合同成功后设置工单（{}）为已完成状态-失败，调百得接口返回空", code);
            return;
        }
        if ("00000000".equals(result.get("code"))) {
            orderFeign.completeWorkOrder(code);
            log.info("签署合同成功后设置工单（{}）为已完成状态-成功", code);
        } else {
            log.error("签署合同成功后设置工单（{}）为已完成状态-失败，调百得接口返回{}", code, result);
        }
    }
}
