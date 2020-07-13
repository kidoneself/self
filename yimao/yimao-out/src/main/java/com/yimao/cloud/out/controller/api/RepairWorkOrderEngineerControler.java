package com.yimao.cloud.out.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.MaintenanceWorkOrderSourceEnum;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderStepEnum;
import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.yun.YunOldIdUtil;
import com.yimao.cloud.out.enums.ApiStatusCode;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.feign.WaterFeign;
import com.yimao.cloud.out.job.WaterDeviceWorkOrderCompleteDateColorJob;
import com.yimao.cloud.out.utils.ApiResult;
import com.yimao.cloud.out.utils.InterfaceUtil;
import com.yimao.cloud.out.utils.ObjectUtil;
import com.yimao.cloud.out.utils.StockUtil;
import com.yimao.cloud.out.utils.WorkOrderStepUtil;
import com.yimao.cloud.out.vo.DistributorVO;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.RepairWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFailurePhenomenonDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceRepairFactFaultDescribeInfoDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceWorkOrderMaterielDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.out.EngineerCustomerWorkOrderVO;
import com.yimao.cloud.pojo.vo.out.RepairWorkOrderVO;
import com.yimao.cloud.pojo.vo.out.WaterDeviceFailurePhenomenonVO;
import com.yimao.cloud.pojo.vo.out.WaterDeviceRepairFactFaultDescribeInfoVO;
import com.yimao.cloud.pojo.vo.out.WaterDeviceWorkOrderMaterielVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.yimao.cloud.base.enums.AppTypeEnum.getTypeById;

/**
 * @author liu yi
 * @description 工程师webApi/净水体系/工单服务/维修工单
 * @date 2019/3/20
 */
@RestController
@Slf4j
@Api(tags = "RepairWorkOrderEngineerControler")
@RequestMapping({"/webapi"})
public class RepairWorkOrderEngineerControler {
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserCache userCache;
    @Resource
    WaterFeign waterFeign;
    @Resource
    private UserFeign userFeign;

    @GetMapping(value = {"/distributor/waterdevice/workorder/repair/verifyAuthorize"})
    @ApiOperation(value = "核验维修建单权限")
    public Map verifyCreateRepairWorkOrderAuthorize(HttpServletRequest request) {
        try {
            Map map = BaideApiUtil.verifyOpenRepairAuthorize();
            if (!"00000000".equals(map.get("code"))) {
                return ApiResult.error(request, map.get("msg").toString());
            }

            String data = map.get("data").toString();
            JSONObject jsonObject = JSONObject.parseObject(data);
            return ApiResult.result(request, jsonObject);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e) {
            log.info("维修建单权限请求异常, " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @PostMapping(value = {"/distributor/waterdevice/workorder/repair/create"})
    @ApiOperation("创建维修工单(安装工)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "地址", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "failurePhenomenonInfo", value = "工单故障原因", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "faultDescribe", value = "物料", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "planServerDate", value = "预约时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "createSource", value = "创建来源：1-ANDROID 2-IOS 3-OPENAPI", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "payMoney", value = "金额", dataType = "Double", required = false, paramType = "query"),
            @ApiImplicitParam(name = "remarks", value = "备注", dataType = "String", paramType = "query")
    })
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map create(@RequestParam(name = "address") String address,
                      @RequestParam(name = "deviceId") String deviceId,
                      @RequestParam(name = "failurePhenomenonInfo") String failurePhenomenonInfo,
                      @RequestParam(name = "faultDescribe") String faultDescribe,
                      @RequestParam(name = "planServerDate") String planServerDate,
                      @RequestParam(name = "createSource") String createSource,
                      @RequestParam(name = "payMoney", required = false) Double payMoney,
                      @RequestParam(name = "remarks") String remarks,
                      HttpServletRequest request) {
        List<String> failureIds = new ArrayList();
        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null) {
            return ApiResult.error(request, ApiStatusCode.SER_PARAM_ERROR);
        }

        Map baideResultMap;
        try {
            //获取设备信息
            WaterDeviceDTO waterDevice = waterFeign.getWaterDeviceById(Integer.parseInt(deviceId));
            if (ObjectUtil.isNull(waterDevice)) {
                return ApiResult.error(request, ApiStatusCode.WATER_DEVICE_NOT_EXISTS);
            }
            //根据deviceId获取安装工单
            WorkOrderDTO workOrder = orderFeign.getWorkOrderById(waterDevice.getWorkOrderId());
            if (null == workOrder) {
                return ApiResult.error(request, "设备关联的工单不存在!");
            }
            WaterDeviceUserDTO deviceUser = userFeign.getWaterDeviceUserById(waterDevice.getDeviceUserId());
            if (Objects.isNull(deviceUser)) {
                return ApiResult.error(request, "设备关联水机用户不存在,设备id：" + waterDevice.getId());
            }
            //转换故障类型集合
            List<WaterDeviceFailurePhenomenonDTO> list = this.analysisJSON(failurePhenomenonInfo);
            if (list == null || list.size() <= 0) {
                return ApiResult.error(request, "请选择故障信息再提交!");
            }

            DistributorVO distributorVO = findDistributorVO(workOrder.getDistributorId());

            if (distributorVO == null) {
                return ApiResult.error(request, "经销商不存在!");
            }
            String fuleTypeIds = this.setFailureIds(list, failureIds);
            log.info("同步售后创建维修工单开始：客户id:" + deviceUser.getOldId() + ",sn:" + waterDevice.getSn() + ",批次码：" + waterDevice.getLogisticsCode() + ",iccid：" + waterDevice.getIccid() + ",deviceId：" + deviceId);
            baideResultMap = BaideApiUtil.repairWorkOrderAdd(deviceUser.getOldId(), waterDevice.getDeviceUserName(), waterDevice.getDeviceUserPhone(), workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion(), address, waterDevice.getSn(), waterDevice.getLogisticsCode(), waterDevice.getIccid()
                    , distributorVO.getOldDistributorId(), distributorVO.getDistributorName(), distributorVO.getDistributorPhone(), distributorVO.getDistributorIdCard(), distributorVO.getDistributorAccount(), distributorVO.getDistributorRoleName(), distributorVO.getOldChildDistributorId(), distributorVO.getChildDistributorName()
                    , distributorVO.getChildDistributorAccount(), distributorVO.getChildDistributorIdCard(), distributorVO.getChildDistributorPhone()
                    , waterDevice.getOldCostId(), YunOldIdUtil.getProductId(workOrder.getDeviceModel()), workOrder.getDeviceModel(), faultDescribe, remarks, workOrder.getOldEngineerId(), planServerDate, failureIds);

            log.info("百得创建维修工单结果", new Object[]{baideResultMap.toString()});
            if (!InterfaceUtil.checkBaideExecuteResult(baideResultMap)) {
                return ApiResult.error(request, baideResultMap.get("msg").toString());
            }

            JSONObject jsonObject = JSONObject.parseObject(baideResultMap.get("data").toString());
            String workcode = jsonObject.get("code").toString();
            RepairWorkOrderDTO dto = setRepairWorOrder(workcode, address, waterDevice, workOrder, remarks, fuleTypeIds, faultDescribe, planServerDate, "", distributorVO, WorkOrderStepEnum.REPAIR_WORK_ORDER_STEP_STARTSERVER.getStep(), createSource);
            orderFeign.createWorkOrderRepair(dto); //创建本地维修工单信息
            waterFeign.createWaterDeviceFailurePhenomenon(workcode, list);  //创建水机故障类型及解决方案
            return ApiResult.result(request, workcode);
        } catch (YimaoException e) {
            log.error("创建维修工单失败，" + e.getMessage());
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e) {
            log.error("创建维修工单失败，" + e.getMessage());
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    /**
     * @param
     * @return com.yimao.cloud.out.vo.DistributorVO
     * @description distributorId
     * @author Liu Yi
     * @date 2019/10/14 17:56
     */
    public DistributorVO findDistributorVO(Integer distributorId) {
        if (distributorId == null) {
            return null;
        }

        DistributorDTO distributor = userFeign.getDistributorById(distributorId);
        if (ObjectUtil.isNull(distributor)) {
            return null;
        }

        DistributorVO vo = new DistributorVO();
        //父账户id
        Integer pid = distributor.getPid();
        if (pid != null) {
            //1-子账号
            vo.setOldChildDistributorId(distributor.getOldId());
            vo.setChildDistributorId(distributor.getId());
            vo.setChildDistributorName(distributor.getRealName());
            vo.setChildDistributorPhone(distributor.getPhone());
            vo.setChildDistributorAccount(distributor.getUserName());
            vo.setChildDistributorRoleId(distributor.getRoleId());
            vo.setChildDistributorRoleName(distributor.getRoleName());
            //子账号在微服务中没有身份证,请求云平台一次,获取经销商子账号信息
            //vo.setChildDistributorIdNo(getChildAccountIdNo(id));
            vo.setChildDistributorIdCard(distributor.getIdCard());
            //获取经销商父帐号信息 设置经销商账号信息
            distributor = userFeign.getDistributorById(pid);
            if (!ObjectUtil.isNull(distributor)) {
                vo.setDistributorAccount(distributor.getUserName());
                vo.setDistributorRoleId(distributor.getRoleId());
                vo.setDistributorRoleName(distributor.getRoleName());
                vo.setOldDistributorId(distributor.getOldId());
                vo.setDistributorId(distributor.getId());
                vo.setDistributorName(distributor.getRealName());
                vo.setDistributorPhone(distributor.getPhone());
                vo.setDistributorIdCard(distributor.getIdCard());
            }
        } else {
            //2-经销商 设置经销商账号信息
            vo.setDistributorAccount(distributor.getUserName());
            //vo.setDistributorRoleId(distributor.getRoleId());
            vo.setDistributorRoleId(distributor.getRoleId());
            vo.setDistributorRoleName(distributor.getRoleName());
            vo.setOldDistributorId(distributor.getOldId());
            vo.setDistributorId(distributor.getId());
            vo.setDistributorName(distributor.getRealName());
            vo.setDistributorPhone(distributor.getPhone());
            vo.setDistributorIdCard(distributor.getIdCard());
        }
        return vo;
    }

    @GetMapping(value = {"/engineer/waterdevice/workorder/repair/page"})
    @ApiOperation("读取工程师个人维修工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "维修工单状态：2-已受理,3-处理中,4-已完成", dataType = "Long", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderStatus", value = "订单类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "搜索条件", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Map page(@RequestParam(name = "state") Integer state,
                    @RequestParam(name = "page") Integer page,
                    @RequestParam(name = "pageSize") Integer pageSize,
                    @RequestParam(name = "orderStatus", required = false) String orderStatus,
                    @RequestParam(name = "search") String search,
                    HttpServletRequest request) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null || engineerId == 0) {
            return ApiResult.error(request, ApiStatusCode.WEBAPI_TOKEN_ERROR);
        }
        Map<String, Object> resultMap;
        PageVO<RepairWorkOrderDTO> pageResult;
        try {
            //分页获取维修工单
            pageResult = this.orderFeign.waterDeviceWorkOrderRepairPage(StatusEnum.NO.value(), "", engineerId, state, orderStatus, search, page, pageSize);
            List<RepairWorkOrderVO> voList = new ArrayList<>();
            if (pageResult == null || pageResult.getResult() == null) {
                resultMap = ApiResult.result(request, voList);
                resultMap.put("count", 0);
                return resultMap;
            }

            List<RepairWorkOrderDTO> list = pageResult.getResult();
            Date startTime;
            RepairWorkOrderVO vo;
            for (RepairWorkOrderDTO repair : list) {
                vo = new RepairWorkOrderVO();
                repair.convert(vo);
                boolean isBespeak = StatusEnum.isYes(repair.getBespeakStatus());
                if (isBespeak) {
                    startTime = repair.getBespeakTimeSeconds();
                } else {
                    startTime = repair.getCreateTime();
                }

                //获取颜色配置
                vo.setConfigMap(WaterDeviceWorkOrderCompleteDateColorJob.getDateInfo(WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType(), startTime, isBespeak, vo.getFailurePhenomenonIds()));
                //经销商信息
                if (null != repair.getDistributorChildId()) {
                    vo.setDistributorName(vo.getDistributorChildName());
                    vo.setDistributorPhone(vo.getDistributorChildPhone());
                }

                //获取工单故障原因
                List<WaterDeviceFailurePhenomenonDTO> failurePhenomenonPageResult = this.waterFeign.getWaterDeviceFailurePhenomenonListByWorkCode(vo.getWorkCode());
                List<WaterDeviceFailurePhenomenonVO> phenomenonList = new ArrayList<>();
                WaterDeviceFailurePhenomenonVO phenomenonVO;
                for (WaterDeviceFailurePhenomenonDTO dto : failurePhenomenonPageResult) {
                    phenomenonVO = new WaterDeviceFailurePhenomenonVO();
                    dto.convert(phenomenonVO);
                    phenomenonList.add(phenomenonVO);
                }
                //故障信息

                if (phenomenonList != null && phenomenonList.size() > 0) {
                    vo.setFailurePhenomenons(phenomenonList);
                }

                voList.add(vo);
            }
            resultMap = ApiResult.result(request, voList);
            resultMap.put("count", pageResult.getTotal());
        } catch (YimaoException e) {
            resultMap = ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            resultMap = ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
        return resultMap;
    }


    @GetMapping(value = {"/engineer/waterdevice/workorder/repair/detail"})
    @ApiOperation("维修工单详情")
    @ApiImplicitParam(name = "orderId", value = "维修工单workCode", dataType = "String", required = true, paramType = "query")
    public Map<String, Object> detail(HttpServletRequest request, @RequestParam(name = "orderId") String workCode) {
        Integer engineerId = userCache.getCurrentEngineerId();
        Date startTime;
        try {
            //获取维修工单
            RepairWorkOrderDTO dto = this.orderFeign.getWorkOrderRepairByWorkCode(workCode);
            //权限判断
            if (dto == null || !engineerId.equals(dto.getEngineerId())) {
                return ApiResult.error(request, ApiStatusCode.DATA_ERROR);
            }

            RepairWorkOrderVO vo = new RepairWorkOrderVO();
            dto.convert(vo);
            //是否重新预约过
            boolean isBespeak = StatusEnum.isYes(dto.getBespeakStatus());
            if (isBespeak) {
                startTime = dto.getBespeakTimeSeconds();
            } else {
                startTime = dto.getCreateTime();
            }
            //获取颜色方案
            vo.setConfigMap(WaterDeviceWorkOrderCompleteDateColorJob.getDateInfo(WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType(), startTime, isBespeak, dto.getFailurePhenomenonIds()));

            //工单消耗的耗材(滤芯)
            List<WaterDeviceWorkOrderMaterielDTO> materiels;
            List<WaterDeviceWorkOrderMaterielVO> materielsList = new ArrayList<>();
            WaterDeviceWorkOrderMaterielVO materielsVO;
            if (StatusEnum.isYes(dto.getScanBatchCodeStatus())) {
                materiels = this.waterFeign.getWaterDeviceWorkOrderMaterielListByWorkCode(workCode, WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType());
                for (WaterDeviceWorkOrderMaterielDTO materiel : materiels) {
                    materielsVO = new WaterDeviceWorkOrderMaterielVO();
                    materiel.convert(materielsVO);
                    materielsList.add(materielsVO);
                }
                if (materiels != null && materiels.size() > 0) {
                    vo.setMateriels(materielsList);
                }
            }

            List<WaterDeviceRepairFactFaultDescribeInfoDTO> describeInfos;
            //维修单水机实际故障信息
            List<WaterDeviceRepairFactFaultDescribeInfoVO> describeInfoList = new ArrayList<>();
            WaterDeviceRepairFactFaultDescribeInfoVO describeInfoVO;
            if (StatusEnum.isYes(dto.getFactFaultStatus())) {
                describeInfos = this.waterFeign.findWaterDeviceRepairFactFaultDescribeInfoList(null, workCode);
                for (WaterDeviceRepairFactFaultDescribeInfoDTO materiel : describeInfos) {
                    describeInfoVO = new WaterDeviceRepairFactFaultDescribeInfoVO();
                    materiel.convert(describeInfoVO);
                    describeInfoList.add(describeInfoVO);
                }
                if (describeInfoList != null && describeInfoList.size() > 0) {
                    vo.setFactFaults(describeInfoList);
                }
            }

            //水机故障类型及解决方案
            List<WaterDeviceFailurePhenomenonVO> failurePhenomenonList = new ArrayList<>();
            WaterDeviceFailurePhenomenonVO failurePhenomenonVO;
            List<WaterDeviceFailurePhenomenonDTO> failurePhenomenons = this.waterFeign.getWaterDeviceFailurePhenomenonListByWorkCode(workCode);
            if (failurePhenomenons != null && failurePhenomenons.size() > 0) {
                for (WaterDeviceFailurePhenomenonDTO failurePhenomenon : failurePhenomenons) {
                    failurePhenomenonVO = new WaterDeviceFailurePhenomenonVO();
                    failurePhenomenon.convert(failurePhenomenonVO);
                    failurePhenomenonList.add(failurePhenomenonVO);
                }
                if (failurePhenomenonList != null && failurePhenomenonList.size() > 0) {
                    vo.setFailurePhenomenons(failurePhenomenonList);
                }
            }

            if (dto.getDistributorChildId() != null) {
                vo.setDistributorName(dto.getDistributorChildName());
                vo.setDistributorPhone(dto.getDistributorChildPhone());
            }
            return ApiResult.result(request, vo);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    private boolean verifyString(String info) {
        return (StringUtil.isBlank(info) || (!info.contains("1=1")) && !info.contains("%") && !info.contains("'"));
    }

    @GetMapping(value = {"/engineer/waterdevice/workorder/repair/getServicedCustomerInfo"})
    @ApiOperation("根据用户名读取工程师个人所服务的用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "consumerName", value = "客户姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "consumerPhone", value = "客户电话", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "deviceSncode", value = "SN", dataType = "String", paramType = "query")
    })
    public Map getCustomerInfoPage(@RequestParam(name = "consumerName", required = false) String consumerName,
                                   @RequestParam(name = "consumerPhone", required = false) String consumerPhone,
                                   @RequestParam(name = "deviceSncode", required = false) String deviceSncode,
                                   HttpServletRequest request) {
        if (StringUtil.isBlank(consumerName) && StringUtil.isBlank(consumerPhone) && StringUtil.isBlank(deviceSncode)) {
            return ApiResult.error(request, "请求异常,请输入要查询的信息");
        }

        if (!this.verifyString(consumerName) || !this.verifyString(consumerPhone) || !this.verifyString(deviceSncode)) {
            return ApiResult.error(request, "您输入的信息为非法字符！");
        }
        Integer engineerId = userCache.getCurrentEngineerId();
        if (null == engineerId || engineerId == 0) {
            return ApiResult.error(request, ApiStatusCode.WEBAPI_TOKEN_ERROR);
        }
        try {
            List<WorkOrderDTO> workOrderList = orderFeign.getWorkOrderCompleteList(engineerId, consumerName, consumerPhone, deviceSncode);
            if (workOrderList == null || workOrderList.size() <= 0) {
                return ApiResult.error(request, "该用户" + consumerName + "在您的服务列表中没有已完成的安装工单，请确认！");
            }
            List<EngineerCustomerWorkOrderVO> list = new ArrayList<>();
            for (WorkOrderDTO order : workOrderList) {
                EngineerCustomerWorkOrderVO customerWorkOrder = new EngineerCustomerWorkOrderVO();
                customerWorkOrder.setId(order.getId());
                customerWorkOrder.setConsumerName(order.getUserName());
                customerWorkOrder.setConsumerPhone(order.getUserPhone());
                customerWorkOrder.setAddrProvice(order.getProvince());
                customerWorkOrder.setAddrCity(order.getCity());
                customerWorkOrder.setAddrRegion(order.getRegion());
                customerWorkOrder.setAddress(order.getAddress());
                customerWorkOrder.setDeviceSncode(order.getSn());
                customerWorkOrder.setChargingName(order.getCostName());//计费类型
                if (order.getCompleteTime() != null) {
                    customerWorkOrder.setBatchTime(order.getCompleteTime().getTime());
                }
                customerWorkOrder.setDeviceBatchCode(order.getLogisticsCode());
                customerWorkOrder.setDeviceId(order.getDeviceId());
                customerWorkOrder.setDeviceModelName(order.getDeviceModel());
                customerWorkOrder.setEngineerName(order.getEngineerName());
                customerWorkOrder.setEngineerPhone(order.getEngineerPhone());
                customerWorkOrder.setDeviceSimcard(order.getSimCard());
                list.add(customerWorkOrder);
            }

            return ApiResult.result(request, list);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @PostMapping(value = {"/engineer/waterdevice/workorder/repair/appointment"})
    @ApiOperation("工程师预约上门服务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "维修工单ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "planServiceDate", value = "预约日期", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "备注", dataType = "String", paramType = "query")
    })
    public Map appointment(@RequestParam(name = "orderId") String workCode,
                           @RequestParam(name = "planServiceDate") String planServiceDate, String address,
                           @RequestParam(required = false) String remark,
                           HttpServletRequest request) {
        log.info("维修工单:工程师预约上门服务.", new Object[]{"repairWorkOrderId:" + workCode + " , planServerDate:" + planServiceDate + " , address:" + address});
        if (StringUtil.isBlank(workCode)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }
        Integer engineerId = userCache.getCurrentEngineerId();
        if (null == engineerId) {
            return ApiResult.error(request, ApiStatusCode.ENGINEER_NOT_EXISTS);
        }
        try {
            RepairWorkOrderDTO order = orderFeign.getWorkOrderRepairByWorkCode(workCode);
            order.setBespeakTime(DateUtil.transferStringToDate(planServiceDate, "yyyy-MM-dd HH:mm:ss"));
            order.setBespeakTimeSeconds(DateUtil.transferStringToDate(planServiceDate, "yyyy-MM-dd HH:mm:ss"));
            order.setCountdownTime(DateUtil.transferStringToDate(planServiceDate, "yyyy-MM-dd HH:mm:ss"));
            order.setBespeakStatus(StatusEnum.YES.value());
            order.setBespeakAddress(address);
            order.setAddress(address);
            order.setAppointRemark(remark);
            WorkOrderStepUtil.setRepairWorkOrderStep(order, WorkOrderTypeEnum.ORDER_TYPE_REPAIR, WorkOrderStepEnum.REPAIR_WORK_ORDER_STEP_STARTSERVER.getStep());
            order.setUpdateTime(new Date());
            order.setUpdateUser(engineerId.toString());

            orderFeign.updateWorkOrderRepair(order);
            return ApiResult.result(request, this.setNextStep(WorkOrderStepEnum.REPAIR_WORK_ORDER_STEP_STARTSERVER.getStep()));
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    private JSONObject setNextStep(int nextStep) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nextStep", nextStep);
        return jsonObject;
    }

    @PostMapping(value = {"/engineer/waterdevice/workorder/repair/appointmentChange"})
    @ApiOperation("工程师修改预约时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "维修工单ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "planServiceDate", value = "预约日期", dataType = "String", paramType = "query")
    })
    public Map appointmentChange(@RequestParam(name = "orderId") String workCode,
                                 @RequestParam(name = "planServiceDate") String planServiceDate,
                                 HttpServletRequest request) {
        log.info("维修工单:工程师修改预约服务.", new Object[]{"workCode:" + workCode + " , planServerDate:" + planServiceDate});
        if (StringUtil.isBlank(workCode)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }

        try {
            Integer engineerId = userCache.getCurrentEngineerId();
            if (null == engineerId) {
                return ApiResult.error(request, ApiStatusCode.ENGINEER_NOT_EXISTS);
            }

            RepairWorkOrderDTO order = orderFeign.getWorkOrderRepairByWorkCode(workCode);
            if (order == null) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
            }
            Map baideResultMap = BaideApiUtil.changePlanDate(workCode, planServiceDate);
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, (String) baideResultMap.get("msg"));
            }

            order.setBespeakTimeSeconds(DateUtil.transferStringToDate(planServiceDate, "yyyy-MM-dd HH:mm:ss"));
            order.setBespeakStatusSeconds(StatusEnum.YES.value());
            order.setCountdownTime(DateUtil.transferStringToDate(planServiceDate, "yyyy-MM-dd HH:mm:ss"));
            order.setUpdateTime(new Date());
            order.setUpdateUser(engineerId.toString());

            orderFeign.updateWorkOrderRepair(order);

            return ApiResult.success(request);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @PostMapping(value = {"/engineer/waterdevice/workorder/repair/startServer"})
    @ApiOperation("工程师开始服务")
    @ApiImplicitParam(name = "orderId", value = "维修工单ID", dataType = "String", paramType = "query")
    public Map startServer(@RequestParam(name = "orderId") String workCode,
                           HttpServletRequest request) {
        if (StringUtil.isBlank(workCode)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }
        try {
            Integer engineerId = userCache.getCurrentEngineerId();
            if (null == engineerId) {
                return ApiResult.error(request, ApiStatusCode.ENGINEER_NOT_EXISTS);
            }

            RepairWorkOrderDTO order = orderFeign.getWorkOrderRepairByWorkCode(workCode);
            if (order == null) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
            }

            Map baideResultMap = BaideApiUtil.repairWorkOrderStartServer(workCode);
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, (String) baideResultMap.get("msg"));
            }

            String data = (String) baideResultMap.get("data");

            order.setStartServerStatus(StatusEnum.YES.value());
            order.setStartServerTime(new Date());
            WorkOrderStepUtil.setRepairWorkOrderStep(order, WorkOrderTypeEnum.ORDER_TYPE_REPAIR, this.getNextStep(data));
            order.setUpdateTime(new Date());
            order.setUpdateUser(engineerId.toString());

            orderFeign.updateWorkOrderRepair(order);
            return ApiResult.result(request, data);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @PostMapping(value = {"/engineer/waterdevice/workorder/repair/checkMateriel"})
    @ApiOperation("检测未完成的维修工单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "维修工单ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "materielInfo", value = "物料", dataType = "String", paramType = "query")
    })
    public Map checkMateriel(@RequestParam(name = "orderId") String workCode,
                             @RequestParam(name = "materielInfo") String materiels,
                             HttpServletRequest request) {
        if (StringUtil.isBlank(workCode) && StringUtil.isBlank(materiels)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }

        try {
            Integer engineerId = userCache.getCurrentEngineerId();
            if (engineerId == null) {
                return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
            }
            RepairWorkOrderDTO workOrder = this.orderFeign.getWorkOrderRepairByWorkCode(workCode);
            if (Objects.isNull(workOrder)) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
            }

            if (StringUtil.isNotBlank(materiels)) {
                List<MaintenanceWorkOrderDTO> list = this.orderFeign.getNotCompleteWorkOrderMaintenance(workOrder.getDeviceSncode(), engineerId, 1);
                if (CollectionUtils.isNotEmpty(list)) {
                    StringBuilder msg = new StringBuilder("您还有: ");
                    materiels = materiels.replaceAll("棉", "");
                    //1、解析出选择的耗材
                    String[] materielArr = materiels.split(",");
                    List<String> materielList = Arrays.asList(materielArr);

                    //2、获取到维护工单未完成的工单所包含的耗材
                    Set<String> unFinshSet = new HashSet();
                    for (MaintenanceWorkOrderDTO dto : list) {
                        String materielDetailName = dto.getMaterielDetailName();
                        String arr[] = materielDetailName.split(",");
                        for (String str : arr) {
                            unFinshSet.add(str);
                        }
                    }

                    //3、对比未完成的是否有本次选择的，有则提示该维护工单未完成
                    boolean flag = false;
                    for (String materie : materielList) {
                        for (String materieTemp : unFinshSet) {
                            if (materie.equals(materieTemp)) {
                                msg.append(materieTemp + ",");
                                flag = true;
                            }
                        }
                    }

                    if (flag) {
                        msg.append("对应的维护工单未完成!");
                        return ApiResult.error(request, msg.toString());
                    }
                }
            }
            return ApiResult.success(request);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PostMapping(value = {"/engineer/waterdevice/workorder/repair/putfactFaultDescribe"})
    @ApiOperation("工程师填写故障信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "维修工单ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "factFaultInfos", value = "物料", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "materielInfo", value = "materielInfo", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "isFactFault", value = "维修工单解决措施", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "isMateriel", value = "isMateriel", dataType = "String", paramType = "query")
    })
    public Map putfactFaultDescribe(@RequestParam(name = "orderId") String workCode,
                                    @RequestParam(name = "factFaultInfos") String factFaultInfos,
                                    @RequestParam(name = "materielInfo") String materielInfo,
                                    @RequestParam(name = "isFactFault") Boolean isFactFault,
                                    @RequestParam(name = "isMateriel") Boolean isMateriel,
                                    HttpServletRequest request) {
        if (StringUtil.isBlank(workCode)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }

        try {
            Integer engineerId = userCache.getCurrentEngineerId();
            if (engineerId == null) {
                return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
            }
            RepairWorkOrderDTO order = orderFeign.getWorkOrderRepairByWorkCode(workCode);
            if (order == null) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
            }

            Map baideResultMap = BaideApiUtil.repairWorkOrderFactFaultDescribe(workCode, this.setFaultInfo(factFaultInfos), JSONArray.parseArray(materielInfo));
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, (String) baideResultMap.get("msg"));
            }

            waterFeign.deleteFactFaultDescribeInfoByWorkCode(workCode);
            waterFeign.deleteWorkOrderMaterielByWorkCode(workCode, WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType());

            if (isMateriel) {
                this.saveMaterielInfo(workCode, materielInfo, engineerId);
            }

            String data = (String) baideResultMap.get("data");
            //填写故障信息 保存物料
            order.setFactFaultStatus(StatusEnum.YES.value());
            order.setFactFaultDate(new Date());

            if (isMateriel) {
                order.setScanBatchCodeStatus(StatusEnum.YES.value());
                order.setScanBatchCodeTime(new Date());
            } else {
                order.setScanBatchCodeStatus(StatusEnum.NO.value());
                //entity.setScanBatchCodeTime((Date) null);
            }
            //添加解决措施
            createFactFaultDescribe(factFaultInfos, order, workCode, WorkOrderTypeEnum.ORDER_TYPE_REPAIR, engineerId);
            WorkOrderStepUtil.setRepairWorkOrderStep(order, WorkOrderTypeEnum.ORDER_TYPE_REPAIR, this.getNextStep(data));
            order.setUpdateTime(new Date());
            order.setUpdateUser(engineerId.toString());
            //保存
            orderFeign.updateWorkOrderRepair(order);

            return ApiResult.result(request, data);
        } catch (YimaoException e) {
            waterFeign.deleteFactFaultDescribeInfoByWorkCode(workCode);
            waterFeign.deleteWorkOrderMaterielByWorkCode(workCode, WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType());
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            waterFeign.deleteFactFaultDescribeInfoByWorkCode(workCode);
            waterFeign.deleteWorkOrderMaterielByWorkCode(workCode, WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType());
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    /***
     * 功能描述:工程师更换物料明细信息
     *
     * @param: [repairWorkOrderId, materielInfo, request]
     * @auther: liu yi
     * @date: 2019/5/10 15:05
     * @return: java.util.Map
     */
    @PostMapping(value = {"/engineer/waterdevice/workorder/repair/putMaterielInfo"})
    @ApiOperation("工程师更换物料明细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "维修工单ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "materielInfo", value = "materielInfo", dataType = "String", paramType = "query")
    })
    public Map saveMaterielInfo(@RequestParam(name = "orderId") String workCode,
                                @RequestParam(name = "materielInfo") String materielInfo,
                                HttpServletRequest request) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (StringUtil.isBlank(workCode)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }
        try {
            this.saveMaterielInfo(workCode, materielInfo, engineerId);
            RepairWorkOrderDTO order = orderFeign.getWorkOrderRepairByWorkCode(workCode);
            if (order == null) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
            }
            Map baideResultMap = BaideApiUtil.repairWorkOrderSaveMaterielInfo(workCode, (List) null);
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, (String) baideResultMap.get("msg"));
            }

            String data = (String) baideResultMap.get("data");

            //保存物料
            order.setScanBatchCodeStatus(StatusEnum.YES.value());
            order.setScanBatchCodeTime(new Date());
            WorkOrderStepUtil.setRepairWorkOrderStep(order, WorkOrderTypeEnum.ORDER_TYPE_REPAIR, this.getNextStep(data));
            order.setUpdateTime(new Date());
            order.setUpdateUser(engineerId.toString());

            orderFeign.updateWorkOrderRepair(order);

            return ApiResult.result(request, data);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    private void saveMaterielInfo(String workCode, String materielInfo, Integer operationUserId) {
        try {
            net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(materielInfo);
            System.out.println(jsonArray.toString());
            List<WaterDeviceWorkOrderMaterielDTO> materirlList = this.getMaterielInfoList(workCode, materielInfo, operationUserId);
            waterFeign.batchCreateWaterDeviceWorkOrderMateriel(materirlList);
        } catch (Exception e) {
            throw new YimaoException("保存失败");
        }
    }

    private List<WaterDeviceWorkOrderMaterielDTO> getMaterielInfoList(String workcode, String materielInfoJson, Integer operationUserId) {
        List<WaterDeviceWorkOrderMaterielDTO> resultList = new ArrayList();
        WaterDeviceWorkOrderMaterielDTO dto;
        try {
            net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(materielInfoJson);
            net.sf.json.JSONObject jsonObject;
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonObject = (net.sf.json.JSONObject) jsonArray.get(i);
                String materielNum = jsonObject.get("materielNum").toString();
                if (null != materielNum && "1".equals(materielNum)) {
                    String materielBatch = jsonObject.get("materielBatchCode").toString();
                    if (!StringUtil.isBlank(materielBatch)) {
                        dto = StockUtil.getWaterDeviceFilter(workcode, materielBatch, WorkOrderTypeEnum.ORDER_TYPE_REPAIR);
                        if (Objects.nonNull(dto)) {
                            String materielType = jsonObject.getString("materielType");
                            dto.setMaterielTypeId(materielType);
                            if (operationUserId != null) {
                                dto.setCreateUser(String.valueOf(operationUserId));
                                dto.setUpdateUser(String.valueOf(operationUserId));
                            }
                            dto.setCreateTime(new Date());
                            dto.setUpdateTime(new Date());
                            dto.setScanCodeTime(new Date());
                            dto.setWorkOrderIndex("1");

                            resultList.add(dto);
                        }
                    }
                } else {
                    dto = StockUtil.getWaterDeviceElectric(workcode, jsonObject, WorkOrderTypeEnum.ORDER_TYPE_REPAIR);
                    resultList.add(dto);
                }
            }
        } catch (Exception e) {
            throw new YimaoException("保存失败");
        }

        return resultList;
    }

    @PostMapping(value = {"/engineer/waterdevice/workorder/repair/changeDevice"})
    @ApiOperation("维修工单申请更换设备")
    @ApiImplicitParam(name = "orderId", value = "维修工单ID", dataType = "String", paramType = "query")
    public Map applyChangeDevice(@RequestParam(name = "orderId") String workCode,
                                 HttpServletRequest request) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (StringUtil.isBlank(workCode)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }

        try {
            RepairWorkOrderDTO order = orderFeign.getWorkOrderRepairByWorkCode(workCode);
            if (order == null) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
            }
            Map<String, Object> baideResultMap = BaideApiUtil.repairWorkOrderApplyDevice(workCode);
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, baideResultMap.get("msg").toString());
            }
            String data = baideResultMap.get("data").toString();
            //申请更换设备
            order.setApplyChangeDeviceStatus(StatusEnum.YES.value());
            order.setApplyChangeDeviceResultStatus(StatusEnum.NO.value());
            order.setApplycount(order.getApplycount() + 1);
            WorkOrderStepUtil.setRepairWorkOrderStep(order, WorkOrderTypeEnum.ORDER_TYPE_REPAIR, this.getNextStep(data));
            order.setUpdateTime(new Date());
            order.setUpdateUser(engineerId.toString());

            orderFeign.updateWorkOrderRepair(order);
            return ApiResult.result(request, data);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PostMapping(value = {"/engineer/waterdevice/workorder/repair/exchange"})
    @ApiOperation("工程师更换设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "维修工单ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sncode", value = "SN", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "batchCode", value = "批次码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "simCard", value = "SIM", required = true, dataType = "String", paramType = "query")
    })
    public Map exChangeDevice(@RequestParam(name = "orderId") String workCode,
                              @RequestParam(name = "sncode") String sncode,
                              @RequestParam(name = "batchCode") String batchCode,
                              @RequestParam(name = "simCard") String simCard,
                              HttpServletRequest request) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (StringUtil.isBlank(workCode)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }

        try {
            EngineerDTO engineer = userFeign.getEngineerById(engineerId);
            if (null == engineer) {
                return ApiResult.error(request, ApiStatusCode.ENGINEER_NOT_EXISTS);
            }
            RepairWorkOrderDTO order = this.orderFeign.getWorkOrderRepairByWorkCode(workCode);
            if (null == order) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
            }
            Integer deviceId = order.getDeviceId();
            if (null == deviceId) {
                return ApiResult.error(request, "维修工单设备id不存在！");
            }
            boolean flag1 = this.waterFeign.checkSnExists(deviceId, sncode);
            if (flag1) {
                return ApiResult.error(request, ApiStatusCode.SNCODE_EXSISTS_ERROR);
            }
            boolean flag2 = this.waterFeign.checkIccidExists(deviceId, simCard);
            if (flag2) {
                return ApiResult.error(request, ApiStatusCode.SIMCARD_EXSISTS_ERROR);
            }

            WaterDeviceDTO waterDevice = waterFeign.getWaterDeviceById(deviceId);
            log.info("维修工单设备获取设备：" + waterDevice.getSn());
            Map baideResultMap = BaideApiUtil.repairWorkOrderPutNewDeviceSnAndBcAndIc(workCode, sncode, batchCode, simCard);
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, (String) baideResultMap.get("msg"));
            } else {
                String data = baideResultMap.get("data").toString();
                Map<String, Object> materielMap = BaideApiUtil.supplierName(batchCode);
                log.info("维修更换设备,查询batchcode:" + batchCode + "供应商返回数据: " + materielMap.toString(), new Object[0]);
                order.setChangeDeviceStatus(StatusEnum.YES.value());
                order.setChangeDeviceTime(new Date());
                order.setDeviceModelName(InterfaceUtil.analysisMapData(materielMap, "productName"));
                order.setChangeDeviceSncode(sncode);
                order.setChangeDeviceBatchCode(batchCode);
                order.setChangeDeviceSimcard(simCard);
                order.setWorkOrderCompleteStatus(StatusEnum.YES.value());
                order.setWorkOrderCompleteTime(new Date());
                WorkOrderStepUtil.setRepairWorkOrderStep(order, WorkOrderTypeEnum.ORDER_TYPE_REPAIR, WorkOrderStepEnum.WORK_ORDER_STEP_END.getStep());
                order.setUpdateTime(new Date());
                order.setUpdateUser(engineer.getOldId());

                //更换设备
                this.waterFeign.replaceWaterDevice(null, waterDevice.getSn(), sncode, waterDevice.getIccid(), simCard, waterDevice.getLogisticsCode(), batchCode);
                orderFeign.updateWorkOrderRepair(order);
                return ApiResult.result(request, data);
            }
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @PostMapping(value = {"/engineer/waterdevice/workorder/repair/finish"})
    @ApiOperation("工程师完成维修工单")
    @ApiImplicitParam(name = "orderId", value = "维修工单ID", dataType = "String", required = true, paramType = "query")
    public Map finishRepiarWorkOrder(@RequestParam(name = "orderId") String workCode,
                                     HttpServletRequest request) {
        Integer engineerId = userCache.getCurrentEngineerId();
        try {
            EngineerDTO engineer = userFeign.getEngineerById(engineerId);
            if (Objects.isNull(engineer)) {
                return ApiResult.error(request, ApiStatusCode.ENGINEER_NOT_EXISTS);
            }
            RepairWorkOrderDTO order = this.orderFeign.getWorkOrderRepairByWorkCode(workCode);
            if (Objects.isNull(order)) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
            }
            if (StringUtil.isNotBlank(order.getScanBatchCodeStatus()) && StatusEnum.isYes(order.getScanBatchCodeStatus())) {
                List<WaterDeviceWorkOrderMaterielDTO> materiels = waterFeign.getWaterDeviceWorkOrderMaterielListByWorkCode(workCode, WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType());
                if (null != materiels && materiels.size() > 0) {
                    Set<String> filterNameSet = new HashSet();
                    WaterDeviceWorkOrderMaterielDTO materiel;
                    String materielTypeName;
                    for (int i = 0; i < materiels.size(); i++) {
                        materiel = materiels.get(i);
                        log.info("耗材类型：" + materiel.getMaterielTypeName() + materiel.getMaterielIndex());
                        if ("1".equals(materiel.getMaterielIndex())) {
                            materielTypeName = materiel.getMaterielTypeName().contains("PP") ? "PP" : materiel.getMaterielTypeName();//PP棉需要转换成PP
                            filterNameSet.add(materielTypeName);
                        }
                    }

                    //请求设备变更记录(安装工没有提交则提示需要提交)
                    if (!filterNameSet.isEmpty()) {
                        List<WaterDeviceFilterChangeRecordDTO> list = waterFeign.getFilterChangeRecordBySnCode(order.getDeviceSncode(), order.getCreateTime(), 1);//查安装工提交的
                        StringBuilder sbTemp = new StringBuilder("请在设备上进行:");
                        if (list == null || list.size() < 0) {
                            StringBuffer filterNameStr = new StringBuffer();
                            for (String filterName : filterNameSet) {
                                filterNameStr.append(filterName + ",");
                            }
                            sbTemp.append(filterNameStr.toString() + " 更换操作");
                            return ApiResult.error(request, sbTemp.toString());
                        }

                        //对比原提交的看记录里面是否都有
                        Set<String> filterNameSetTemp = new HashSet();
                        for (String filterName : filterNameSet) {
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
                    }
                }
            }
            Map baideResultMap = BaideApiUtil.repairWorkOrderFinish(workCode);
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, (String) baideResultMap.get("msg"));
            }
            order.setWorkOrderCompleteStatus(StatusEnum.YES.value());
            order.setWorkOrderCompleteTime(new Date());
            WorkOrderStepUtil.setRepairWorkOrderStep(order, WorkOrderTypeEnum.ORDER_TYPE_REPAIR, WorkOrderStepEnum.WORK_ORDER_STEP_END.getStep());
            order.setUpdateTime(new Date());
            order.setUpdateUser(engineer.getOldId());
            //完成维修工单
            this.orderFeign.updateWorkOrderRepair(order);
            log.info("流程完成");
            return ApiResult.result(request, baideResultMap.get("data"));
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @GetMapping(value = {"/engineer/waterdevice/workorder/repair/getMaterielTree"})
    @ApiOperation("维修工单耗材联动菜单")
    public Map getMaterielTree(HttpServletRequest request) {
        try {
            Map<String, Object> baideResultMap = BaideApiUtil.getMaterielTreeByType();
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, baideResultMap.get("msg").toString());
            }

            if (baideResultMap.containsKey("data")) {
                String resultStr = baideResultMap.get("data").toString();
                resultStr = resultStr.substring(1, resultStr.length() - 1);
                JSONObject jsonObject = JSONObject.parseObject(resultStr);
                return ApiResult.result(request, jsonObject);
            }

            return ApiResult.error(request, "暂无数据");
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @GetMapping(value = {"/engineer/waterdevice/workorder/repair/getFaultThirdTree"})
    @ApiOperation("维修工单三级故障联动菜单")
    public Map getFaultThirdTree(HttpServletRequest request) {
        try {
            Map<String, Object> baideResultMap = BaideApiUtil.repairWorkOrderFaultThirdTree();
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, baideResultMap.get("msg").toString());
            }

            if (baideResultMap.containsKey("data")) {
                String resultStr = baideResultMap.get("data").toString();
                net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(resultStr);
                return ApiResult.result(request, jsonArray);
            }

            return ApiResult.error(request, "暂无数据");
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    @GetMapping(value = {"/engineer/waterdevice/workorder/repair/getFaultSecondTree"})
    @ApiOperation("维修工单二级故障现象联动菜单")
    public Map getFaultSecondTree(HttpServletRequest request) {
        try {
            Map<String, Object> baideResultMap = BaideApiUtil.repairWorkOrderFaultSecondTree();
            if (!"00000000".equals(baideResultMap.get("code"))) {
                return ApiResult.error(request, baideResultMap.get("msg").toString());
            }

            String baideDataStr = baideResultMap.get("data").toString();
            net.sf.json.JSONObject baideDataOneJson = net.sf.json.JSONObject.fromObject(baideDataStr);
            net.sf.json.JSONArray baideDataTwoJson = (net.sf.json.JSONArray) baideDataOneJson.get("result");
            net.sf.json.JSONObject baideDataThreeJson = net.sf.json.JSONObject.fromObject(baideDataTwoJson.get(0).toString());
            net.sf.json.JSONArray result = net.sf.json.JSONArray.fromObject(baideDataThreeJson.get("children"));
            return ApiResult.result(request, result);
        } catch (YimaoException e) {
            e.printStackTrace();
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.error(request, ApiStatusCode.SER_UNKNOW);
        }
    }

    private Integer getNextStep(String baideData) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(baideData);
            return Integer.parseInt(jsonObject.get("nextStep").toString());
        } catch (Exception e) {
            throw new YimaoException("解析步骤失败！");
        }
    }


    /***
     * 功能描述:设置工单属性
     *
     * @param: [workCode, address, waterDevice, workOrderDTO, engineer, remark, fuleTypeIds, faultDescribe, planServerDate, repairAdvice, mainDistributor, distributorDTO, nextStep, createSource, product]
     * @auther: liu yi
     * @date: 2019/5/24 12:07
     * @return: com.yimao.cloud.pojo.dto.order.RepairWorkOrderDTO
     */
    public RepairWorkOrderDTO setRepairWorOrder(String workCode, String address, WaterDeviceDTO waterDevice, WorkOrderDTO workOrder, String remark, String fuleTypeIds, String faultDescribe, String planServerDate, String repairAdvice, DistributorVO distributorVO, int nextStep, String createSource) {
        RepairWorkOrderDTO dto = new RepairWorkOrderDTO();
        //从售后系统获取的code
        dto.setWorkCode(workCode);
        //dto.setId(workCode);
        dto.setWorkOrderId(workOrder.getId());
        //地区信息
        dto.setAddrProvince(workOrder.getProvince());
        dto.setAddrCity(workOrder.getCity());
        dto.setAddrRegion(workOrder.getRegion());
        dto.setAddress(address);
        dto.setBespeakAddress(address);
        //维修建议
        dto.setRepairAdvice(repairAdvice);
        //故障现象类型id
        dto.setFailurePhenomenonIds(fuleTypeIds);
        //经销商信息
        dto.setDistributorId(distributorVO.getChildDistributorId());
        dto.setDistributorName(distributorVO.getDistributorName());
        dto.setDistributorPhone(distributorVO.getDistributorPhone());
        dto.setDistributorIdCard(distributorVO.getDistributorIdCard());
        dto.setDistributorRoleId(distributorVO.getDistributorRoleId());

        dto.setDistributorRoleName(distributorVO.getDistributorRoleName());
        dto.setDistributorChildId(distributorVO.getChildDistributorId());
        dto.setDistributorChildName(distributorVO.getChildDistributorName());
        dto.setDistributorChildPhone(distributorVO.getChildDistributorPhone());

        //产品信息
       /* dto.setProductTypeId(workOrder.getProductTypeId());//产品类型
        dto.setOldProductTypeId(workOrder.getOldProductTypeId());
        dto.setProductType(workOrder.getProductTypeName());//产品类型*/
        dto.setProductId(workOrder.getProductId());
        dto.setProductName(YunOldIdUtil.getProductName(workOrder.getDeviceModel()));

        dto.setDeviceId(workOrder.getDeviceId());
        dto.setDeviceModelName(workOrder.getDeviceModel());
        dto.setDeviceSncode(waterDevice.getSn());
        dto.setDeviceBatchCode(waterDevice.getLogisticsCode());
        dto.setDeviceSimcard(waterDevice.getIccid());
        dto.setWorkOrderRemark(remark);
        //安装工信息
        dto.setEngineerId(workOrder.getEngineerId());
        dto.setEngineerName(workOrder.getEngineerName());
        dto.setEngineerPhone(workOrder.getEngineerPhone());
        dto.setStationId(workOrder.getStationId());
        dto.setStationName(workOrder.getStationName());
        //客户信息(水机用户)
        dto.setConsumerId(workOrder.getUserId());
        dto.setConsumerName(workOrder.getUserName());
        dto.setConsumerPhone(workOrder.getUserPhone());
        dto.setConsumerIdCard(workOrder.getUserIdCard());

        WorkOrderStepUtil.setRepairWorkOrderStep(dto, WorkOrderTypeEnum.ORDER_TYPE_REPAIR, nextStep);

        dto.setAcceptStatus(StatusEnum.YES.value());//接单状态
        dto.setAcceptTime(new Date());//接单时间
        Date planServerDatTemp = DateUtil.transferStringToDate(planServerDate, "yyyy-MM-dd hh:mm:ss");
        dto.setBespeakTime(planServerDatTemp);
        dto.setBespeakTimeSeconds(planServerDatTemp);
        dto.setCountdownTime(planServerDatTemp);
        dto.setBespeakStatus(StatusEnum.YES.value());
        //计费类型
        dto.setCostId(waterDevice.getCostId());
        dto.setCostName(waterDevice.getCostName());
        dto.setFaultDescise(faultDescribe);
        dto.setWorkOrderSource(createSource);
        dto.setWorkOrderSourceText(getTypeById(createSource).getType());
        dto.setCreateUser(waterDevice.getEngineerId().toString());
        dto.setUpdateUser(waterDevice.getEngineerId().toString());

        return dto;
    }


    private String setFailureIds(List<WaterDeviceFailurePhenomenonDTO> list, List<String> failureIds) {
        StringBuilder sb = new StringBuilder("");
        for (WaterDeviceFailurePhenomenonDTO dto : list) {
            failureIds.add(dto.getFaultTypeId());
            sb.append(dto.getFaultTypeId());
            sb.append(",");
        }
        return sb.toString();
    }

    public List<WaterDeviceFailurePhenomenonDTO> analysisJSON(String failurePhenomenons) {
        if (StringUtil.isBlank(failurePhenomenons)) {
            return null;
        }

        log.info("故障提交信息: " + failurePhenomenons, new Object[0]);
        List failures;
        try {
            failures = JSONArray.parseArray(failurePhenomenons, WaterDeviceFailurePhenomenonDTO.class);
        } catch (Exception e) {
            log.info("解析故障现象信息失败", new Object[]{"failurePhenomenons:" + failurePhenomenons + "   \r\n ,reason:" + e.getMessage()});
            return null;
        }
        return failures;
    }

    /***
     * 功能描述：批量创建解决措施
     *
     * @param: [factFaultInfos, deviceId, sncode, workcode, workOrderTypeEnum, operationUserId]
     * @auther: liu yi
     * @date: 2019/4/16 10:17
     * @return: void
     */
    public void createFactFaultDescribe(String factFaultInfos, RepairWorkOrderDTO order, String workcode, WorkOrderTypeEnum workOrderTypeEnum, Integer operationUserId) {
        JSONArray jsonArray;
        try {
            jsonArray = JSONArray.parseArray(factFaultInfos);
        } catch (Exception e) {
            log.info("解析解决措施信息json失败", new Object[]{e.getMessage()});
            throw new YimaoException("解析解决措施信息json失败！");
        }

        if (jsonArray == null) {
            throw new YimaoException("参数必须有值！");
        }

        List<WaterDeviceRepairFactFaultDescribeInfoDTO> list = JSONArray.parseArray(factFaultInfos, WaterDeviceRepairFactFaultDescribeInfoDTO.class);
        for (WaterDeviceRepairFactFaultDescribeInfoDTO repairFactFault : list) {
            repairFactFault.setCreateTime(new Date());
            repairFactFault.setUpdateTime(new Date());
            if (operationUserId != null) {
                repairFactFault.setCreateUser(String.valueOf(operationUserId));
                repairFactFault.setUpdateUser(String.valueOf(operationUserId));
            }

            repairFactFault.setDeviceId(order.getDeviceId());
            repairFactFault.setDeviceSncode(order.getDeviceSncode());
            repairFactFault.setWorkCode(workcode);
            repairFactFault.setWorkOrderIndex(workOrderTypeEnum.getType());

            repairFactFault.setDelStatus(StatusEnum.FALSE.value());
            repairFactFault.setIdStatus(StatusEnum.YES.value());
            waterFeign.createWaterDeviceRepairFactFaultDescribeInfo(repairFactFault);
        }
    }

    private List<Map<String, Object>> setFaultInfo(String factFaultInfos) {
        net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(factFaultInfos);
        net.sf.json.JSONObject jsonObject;
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonObject = (net.sf.json.JSONObject) jsonArray.get(i);
            jsonObject.put("factFaultDescribe", jsonObject.getString("factFaultDescribeId"));
            jsonObject.put("factFaultReason", jsonObject.getString("factFaultReasonId"));
            jsonObject.put("solveMeasure", jsonObject.getString("solveMeasureId"));
        }

        return jsonArray;
    }
}