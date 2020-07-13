package com.yimao.cloud.out.controller.api;

import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderStepEnum;
import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.out.enums.ApiStatusCode;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.feign.SystemFeign;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.feign.WaterFeign;
import com.yimao.cloud.out.job.WaterDeviceWorkOrderCompleteDateColorJob;
import com.yimao.cloud.out.utils.ApiResult;
import com.yimao.cloud.out.utils.InterfaceUtil;
import com.yimao.cloud.out.utils.ObjectUtil;
import com.yimao.cloud.out.utils.StockUtil;
import com.yimao.cloud.out.utils.WorkOrderStepUtil;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceWorkOrderMaterielDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.out.MaintenanceWorkOrderVO;
import com.yimao.cloud.pojo.vo.out.WaterDeviceWorkOrderMaterielVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author liu yi
 * @description 工程师webApi/净水体系/工单服务/维护工单
 * @date 2019/4/1
 */

@RestController
@Slf4j
@Api("MaintenanceWorkOrderControler")
@RequestMapping({"/webapi/engineer/waterdevice/workorder/maintenance"})
public class MaintenanceWorkOrderControler {
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserCache userCache;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    WaterFeign waterFeign;

    @GetMapping(value = {"/page"})
    @ApiOperation("读取工程师个人维护工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "维护工单状态", required = true, defaultValue = "1", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "搜索条件", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Map page(@RequestParam(value = "state", defaultValue = "1") Integer state,
                    @RequestParam(value = "page") Integer page,
                    @RequestParam(value = "pageSize") Integer pageSize,
                    @RequestParam(value = "search") String search,
                    HttpServletRequest request) {
        MaintenanceWorkOrderVO vo;
        try {
            Integer operationUserId = userCache.getCurrentEngineerId();
            if (ObjectUtil.isNull(operationUserId)) {
                return ApiResult.error(request, ApiStatusCode.WEBAPI_TOKEN_ERROR);
            }
            PageVO<MaintenanceWorkOrderDTO> pageList = this.orderFeign.getWorkOrderMaintenancePage("", String.valueOf(operationUserId),
                    state, search, page, pageSize);
            List<MaintenanceWorkOrderVO> list = new ArrayList<>();
            if (pageList == null || pageList.getResult() == null) {
                Map<String, Object> resultMap = ApiResult.result(request, list);
                resultMap.put("count", 0);

                return resultMap;
            }
            for (MaintenanceWorkOrderDTO dto : pageList.getResult()) {
                vo = new MaintenanceWorkOrderVO();
                dto.convert(vo);
                boolean isBespeak = StatusEnum.isYes(dto.getBespeakStatus());
                Date startDate = isBespeak ? dto.getBespeakTimeSeconds() : dto.getCreateTime();
                vo.setConfigMap(WaterDeviceWorkOrderCompleteDateColorJob.getDateInfo(WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE.getType(), startDate, isBespeak, ""));
                if (WorkOrderStepEnum.MAINTENANCE_WORK_ORDER_STEP_SCANBATCHCODE.getStep() == dto.getCurrentStep().intValue()) {
                    Map<String, Object> mustUploadImgResult = BaideApiUtil.mustUploadImg(dto.getWorkCode());
                    if ("00000000".equals(mustUploadImgResult.get("code").toString())) {
                        JSONObject jsonObject = JSONObject.fromObject(mustUploadImgResult.get("data").toString());
                        vo.setMustUploadImg("true".equals(jsonObject.getString("result")));
                    }
                }
                if (dto.getDistributorChildId() != null) {
                    vo.setDistributorName(dto.getDistributorChildName());
                    vo.setDistributorPhone(dto.getDistributorChildPhone());
                }
                list.add(vo);
            }
            Map<String, Object> resultMap = ApiResult.result(request, list);
            resultMap.put("count", pageList.getTotal());

            return resultMap;
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @GetMapping(value = {"/detail"})
    @ApiOperation("维护工单详情")
    @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "String", paramType = "query")
    public Map<String, Object> detail(HttpServletRequest request, @RequestParam("orderId") String workCode) {
        MaintenanceWorkOrderDTO dto;
        try {
            Integer engineerId = userCache.getCurrentEngineerId();
            dto = this.orderFeign.getWorkOrderMaintenanceByWorkCode(workCode);
            if (dto == null || !engineerId.equals(dto.getEngineerId())) {
                return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
            }
            MaintenanceWorkOrderVO vo = new MaintenanceWorkOrderVO();
            dto.convert(vo);

            boolean isBespeak = StatusEnum.isYes(dto.getBespeakStatus());
            Date startDate = isBespeak ? dto.getBespeakTime() : dto.getCreateTime();
            vo.setConfigMap(WaterDeviceWorkOrderCompleteDateColorJob.getDateInfo(WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE.getType(), startDate, isBespeak, ""));
            if (StatusEnum.isYes(dto.getChangeFilterStatus())) {
                List<WaterDeviceWorkOrderMaterielVO> materielsList = new ArrayList<>();
                WaterDeviceWorkOrderMaterielVO materielsVO;
                List<WaterDeviceWorkOrderMaterielDTO> materielList = this.waterFeign.getWaterDeviceWorkOrderMaterielListByWorkCode(workCode, WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType());
                if (materielList != null && materielList.size() > 0) {
                    for (WaterDeviceWorkOrderMaterielDTO materiel : materielList) {
                        materielsVO = new WaterDeviceWorkOrderMaterielVO();
                        materiel.convert(materielsVO);
                        materielsList.add(materielsVO);
                    }
                    if (materielsList != null) {
                        vo.setMateriels(materielsList);
                    }
                }
            }
            if (dto.getDistributorChildId() != null) {
                vo.setDistributorName(dto.getDistributorChildName());
                vo.setDistributorPhone(dto.getDistributorChildPhone());
            }
            if (dto.getCurrentStep() < WorkOrderStepEnum.MAINTENANCE_WORK_ORDER_STEP_SCANBATCHCODE.getStep()) {
                vo.setMaterielDetailName("");
            }
            if (WorkOrderStepEnum.MAINTENANCE_WORK_ORDER_STEP_SCANBATCHCODE.getStep() == dto.getCurrentStep().intValue()) {
                Map mustUploadImgResult = BaideApiUtil.mustUploadImg(dto.getWorkCode());
                if (InterfaceUtil.checkBaideExecuteResult(mustUploadImgResult)) {
                    net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(mustUploadImgResult.get("data").toString());
                    vo.setMustUploadImg("true".equals(jsonObject.getString("result")));
                }
            }
            return ApiResult.result(request, vo);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @PostMapping(value = {"/appointment"})
    @ApiOperation("工程师预约上门服务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "维护工单id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "planServiceDate", value = "预约时间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "address", value = "地址", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "备注", dataType = "String", paramType = "query")
    })
    public Map appointment(@RequestParam(name = "orderId") String workCode,
                           @RequestParam(name = "planServiceDate") String planServiceDate,
                           @RequestParam(name = "address", required = false) String address,
                           @RequestParam(name = "remark", required = false) String remark,
                           HttpServletRequest request) {
        Integer engineerId = userCache.getCurrentEngineerId();
        log.info("维护工单:工程师预约上门服务.", new Object[]{"workCode:" + workCode + " , planServerDate:" + planServiceDate + " , address:" + address});
        if (StringUtil.isBlank(workCode)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }

        try {
            Map baideResultMap = BaideApiUtil.appointment(workCode, planServiceDate, address, remark);
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, (String) baideResultMap.get("msg"));
            }

            String nextStepStr = baideResultMap.get("data").toString();
            JSONObject jsonObject = JSONObject.fromObject(nextStepStr);
            Integer nextStep = Integer.parseInt(jsonObject.get("nextStep").toString());
            MaintenanceWorkOrderDTO dto = orderFeign.getWorkOrderMaintenanceByWorkCode(workCode);
            if (dto == null) {
                return ApiResult.error(request, ApiStatusCode.SER_NO_DATA);
            }
            WorkOrderStepUtil.setMaintenanceOrderStep(dto, WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE, nextStep);
            dto.setBespeakStatus(StatusEnum.YES.value());
            dto.setBespeakTime(DateUtil.transferStringToDate(planServiceDate, "yyyy-MM-dd HH:mm:ss"));
            dto.setCountdownTime(DateUtil.transferStringToDate(planServiceDate, "yyyy-MM-dd HH:mm:ss"));
            dto.setBespeakTimeSeconds(DateUtil.transferStringToDate(planServiceDate, "yyyy-MM-dd HH:mm:ss"));
            dto.setBespeakAddress(address);
            dto.setAddressDetail(dto.getAddrProvince() + dto.getAddrCity() + dto.getAddrRegion() + address);
            dto.setAppointRemark(remark);

            if (engineerId != null && engineerId != 0) {
                dto.setUpdateUser(engineerId.toString());
            }

            dto.setUpdateTime(new Date());
            orderFeign.updateWorkOrderMaintenance(dto);
            return ApiResult.result(request, nextStepStr);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }


    @PostMapping(value = {"/appointmentChange"})
    @ApiOperation("工程师修改预约时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "维护工单id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "planServiceDate", value = "预约时间", dataType = "String", paramType = "query")
    })
    public Map appointmentChange(@RequestParam(name = "orderId") String workCode,
                                 @RequestParam(name = "planServiceDate") String planServiceDate, HttpServletRequest request) {
        Integer engineerId = userCache.getCurrentEngineerId();
        log.info("维护工单:工程师预约上门服务.", new Object[]{"workCode:" + workCode + " , planServerDate:" + planServiceDate});
        if (StringUtil.isBlank(workCode)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }
        try {
            Map baideResultMap = BaideApiUtil.changePlanDate(workCode, planServiceDate);
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, (String) baideResultMap.get("msg"));
            }
            MaintenanceWorkOrderDTO dto = orderFeign.getWorkOrderMaintenanceByWorkCode(workCode);
            if (dto == null) {
                return ApiResult.error(request, ApiStatusCode.SER_NO_DATA);
            }
            dto.setBespeakTimeSeconds(DateUtil.transferStringToDate(planServiceDate, "yyyy-MM-dd HH:mm:ss"));
            dto.setCountdownTime(DateUtil.transferStringToDate(planServiceDate, "yyyy-MM-dd HH:mm:ss"));
            dto.setBespeakStatusSeconds(StatusEnum.YES.value());
            if (engineerId != null && engineerId != 0) {
                dto.setUpdateUser(engineerId.toString());
            }
            dto.setUpdateTime(new Date());

            orderFeign.updateWorkOrderMaintenance(dto);
            return ApiResult.success(request);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @PostMapping(value = {"/startServer"})
    @ApiOperation("工程师开始服务")
    @ApiImplicitParam(name = "orderId", value = "维护工单id", required = true, dataType = "String", paramType = "query")
    public Map startServer(@RequestParam(name = "orderId") String workCode, HttpServletRequest request) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (StringUtil.isBlank(workCode)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }
        try {
            Map baideResultMap = BaideApiUtil.maintenanceWorkOrderStartServer(workCode);
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, (String) baideResultMap.get("msg"));
            }
            MaintenanceWorkOrderDTO dto = orderFeign.getWorkOrderMaintenanceByWorkCode(workCode);
            if (dto == null) {
                return ApiResult.error(request, ApiStatusCode.SER_NO_DATA);
            }

            String nextStepStr = baideResultMap.get("data").toString();
            JSONObject jsonObject = JSONObject.fromObject(nextStepStr);
            Integer nextStep = Integer.parseInt(jsonObject.get("nextStep").toString());

            WorkOrderStepUtil.setMaintenanceOrderStep(dto, WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE, nextStep);
            dto.setStartServerStatus(StatusEnum.YES.value());
            dto.setStartServerTime(new Date());

            if (engineerId != null && engineerId != 0) {
                dto.setUpdateUser(engineerId.toString());
            }
            dto.setUpdateTime(new Date());
            orderFeign.updateWorkOrderMaintenance(dto);

            return ApiResult.result(request, nextStepStr);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @PostMapping(value = {"/scanBatchCode"})
    @ApiOperation("工程师扫描二维码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "维护工单id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "batchCodes", value = "批次码", dataType = "String", paramType = "query")
    })
    public Map scanBatchCode(@RequestParam(name = "orderId") String workCode,
                             @RequestParam(name = "batchCodes") String batchCodes, HttpServletRequest request) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null || engineerId == 0) {
            return ApiResult.error(request, ApiStatusCode.TOKEN_ERROR);
        }
        EngineerDTO engineer = userFeign.getEngineerById(engineerId);
        if (StringUtil.isBlank(batchCodes) || StringUtil.isBlank(workCode)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }
        try {
            Map baideResultMap = BaideApiUtil.maintenanceWorkOrderScanBatchCode(workCode, batchCodes);
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, (String) baideResultMap.get("msg"));
            }

            List<WaterDeviceWorkOrderMaterielDTO> filterEntityList = this.getMaterielInfoList(workCode, batchCodes, engineer);

            MaintenanceWorkOrderDTO dto = orderFeign.getWorkOrderMaintenanceByWorkCode(workCode);
            if (dto == null) {
                return ApiResult.error(request, ApiStatusCode.SER_NO_DATA);
            }
            dto.setChangeFilterStatus(StatusEnum.YES.value());
            dto.setChangeFilterTime(new Date());
            dto.setFilterCount(filterEntityList.size());

            String nextStepStr = baideResultMap.get("data").toString();
            JSONObject jsonObject = JSONObject.fromObject(nextStepStr);
            Integer nextStep = Integer.parseInt(jsonObject.get("nextStep").toString());

            WorkOrderStepUtil.setMaintenanceOrderStep(dto, WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE, nextStep);
            dto.setUpdateUser(engineerId.toString());
            dto.setUpdateTime(new Date());

            orderFeign.updateWorkOrderMaintenance(dto);
            waterFeign.batchCreateWaterDeviceWorkOrderMateriel(filterEntityList);

            return ApiResult.result(request, baideResultMap.get("data"));
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @PostMapping(value = {"/uploadMaterielImg"})
    @ApiOperation("工程师上传滤芯销毁图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "维护工单id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "file1", value = "文件1", dataType = "String", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "file2", value = "文件2", dataType = "String", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "file3", value = "文件3", dataType = "String", paramType = "query", allowMultiple = true)
    })
    public Map uploadMaterielImg(@RequestParam(name = "orderId") String workCode,
                                 @RequestParam(name = "file1") MultipartFile file1,
                                 @RequestParam(required = false, name = "file2") MultipartFile file2,
                                 @RequestParam(required = false, name = "file3") MultipartFile file3,
                                 HttpServletRequest request) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null || engineerId == 0) {
            return ApiResult.error(request, ApiStatusCode.TOKEN_ERROR);
        }
        try {
            MaintenanceWorkOrderDTO dto = orderFeign.getWorkOrderMaintenanceByWorkCode(workCode);
            if (ObjectUtil.isNull(dto)) {
                return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
            }

            dto.setDestroyFilterImgStatus(StatusEnum.YES.value());
            dto.setDestroyFilterImgTime(new Date());
            dto.setDestroyFilterImg(this.upload(file1, file2, file3, null, "滤芯销毁图片"));
            dto.setCurrentStep(WorkOrderStepEnum.MAINTENANCE_WORK_ORDER_STEP_UPLOAD_IMG.getStep());
            dto.setNextStep(WorkOrderStepEnum.MAINTENANCE_WORK_ORDER_STEP_FINISH_WORK.getStep());
            dto.setUpdateUser(engineerId.toString());
            dto.setUpdateTime(new Date());

            orderFeign.updateWorkOrderMaintenance(dto);

            Map<String, Object> baideResultMap = BaideApiUtil.maintenanceWorkOrderUploadMaterielImg(workCode, file1, file2, file3);
            log.info("维护工单上传滤芯销毁图片百得返回结果:", new Object[]{baideResultMap});
            if ("00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.result(request, baideResultMap.get("data"));
            } else {
                return ApiResult.error(request, baideResultMap.get("msg").toString());
            }
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
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
            e.printStackTrace();
        }
        return path;
    }

    @PostMapping(value = {"/finishMaintenanceWorkOrder"})
    @ApiOperation("工程师完成维护工单")
    @ApiImplicitParam(name = "orderId", value = "维护工单id", required = true, dataType = "String", paramType = "query")
    public Map finishRepiarWorkOrder(@RequestParam(name = "orderId") String workCode, HttpServletRequest request) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null || engineerId == 0) {
            return ApiResult.error(request, ApiStatusCode.TOKEN_ERROR);
        }
        if (StringUtil.isBlank(workCode)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }

        MaintenanceWorkOrderDTO dto = orderFeign.getWorkOrderMaintenanceByWorkCode(workCode);

        //请求设备变更记录-未生效的(原微服务调用云平台)
        List<WaterDeviceFilterChangeRecordDTO> list = waterFeign.getFilterChangeRecordBySnCode(dto.getDeviceSncode(), dto.getCreateTime(), 1);//查安装工提交的
        StringBuilder sbTemp = new StringBuilder("请在设备上进行:");
        if (list.isEmpty()) {
            sbTemp.append(dto.getMaterielDetailName() + " 更换操作");
            return ApiResult.error(request, sbTemp.toString());
        }

        String[] filterNameArr = dto.getMaterielDetailName().split(",");
        //对比原提交的看记录里面是否都有
        Set<String> filterNameSetTemp = new HashSet();
        for (String filterName : filterNameArr) {
            boolean flag = false;
            for (WaterDeviceFilterChangeRecordDTO changeRecord : list) {
                if (changeRecord.getFilterName().equals(filterName)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                filterNameSetTemp.add(filterName);
            }
        }
        if (!filterNameSetTemp.isEmpty()) {
            for (String filterName : filterNameSetTemp) {
                sbTemp.append(filterName + ",");
            }
            sbTemp.append(" 更换操作!");
            return ApiResult.error(request, sbTemp.toString());
        }
        try {
            Map baideResultMap = BaideApiUtil.maintenanceWorkOrderFinish(workCode);
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, (String) baideResultMap.get("msg"));
            }

            WorkOrderStepUtil.setMaintenanceOrderStep(dto, WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE, WorkOrderStepEnum.WORK_ORDER_STEP_END.getStep());
            dto.setWorkOrderCompleteStatus(StatusEnum.YES.value());
            dto.setWorkOrderCompleteTime(new Date());
            dto.setCompleteType(0);
            dto.setCompleteTypeText("正常完成");
            dto.setUpdateUser(engineerId.toString());
            dto.setUpdateTime(new Date());

            orderFeign.updateWorkOrderMaintenance(dto);
            return ApiResult.success(request);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    private List<WaterDeviceWorkOrderMaterielDTO> getMaterielInfoList(String workcode, String batchCodes, EngineerDTO operationUser) {
        String[] batchCodeArr = batchCodes.split(",");
        List<WaterDeviceWorkOrderMaterielDTO> resultList = new ArrayList();
        WaterDeviceWorkOrderMaterielDTO dto;
        for (int i = 0; i < batchCodeArr.length; i++) {
            dto = StockUtil.getWaterDeviceFilter(workcode, batchCodeArr[i], WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE);
            if (!ObjectUtil.isNull(dto)) {
                if (!ObjectUtil.isNull(operationUser)) {
                    dto.setCreateUser(String.valueOf(operationUser.getId()));
                    dto.setUpdateUser(String.valueOf(operationUser.getId()));
                }
                dto.setCreateTime(new Date());
                dto.setUpdateTime(new Date());
                dto.setScanCodeTime(new Date());
                dto.setWorkOrderIndex("2");
                resultList.add(dto);
            }
        }
        return resultList;
    }
}

