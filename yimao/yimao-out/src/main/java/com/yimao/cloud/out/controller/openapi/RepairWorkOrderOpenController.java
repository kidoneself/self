package com.yimao.cloud.out.controller.openapi;


import com.yimao.cloud.base.baideApi.utils.StringUtil;
import com.yimao.cloud.base.enums.AppTypeEnum;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderStepEnum;
import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.yun.YunOldIdUtil;
import com.yimao.cloud.out.enums.ApiStatusCode;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.feign.WaterFeign;
import com.yimao.cloud.out.utils.ApiResult;
import com.yimao.cloud.out.utils.ObjectUtil;
import com.yimao.cloud.out.utils.WorkOrderStepUtil;
import com.yimao.cloud.out.vo.DistributorVO;
import com.yimao.cloud.pojo.dto.order.RepairWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFailurePhenomenonDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.yimao.cloud.base.enums.AppTypeEnum.getTypeById;

/***
 * 功能描述: 维修工单同步（售后平台调用）
 *
 * @auther: liu yi
 * @date: 2019/3/27 17:06
 */
@RestController
@Api("RepairWorkOrderOpenController")
@Slf4j
@RequestMapping({"/openapi/waterdevice/workorder/repair"})
public class RepairWorkOrderOpenController {
    @Resource
    private OrderFeign orderFeign;
    @Resource
    WaterFeign waterFeign;
    @Resource
    private UserFeign userFeign;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PostMapping(value = {"/create"})
    @ApiOperation("百得推送创建维修单")
    public Map create(@RequestParam(name = "workcode") String workCode,
                      @RequestParam(name = "address", defaultValue = "", required = false) String address,
                      @RequestParam(name = "sncode") String sncode,
                      @RequestParam(name = "engineerId", defaultValue = "", required = false) String oldEngineerId,
                      @RequestParam(name = "failurePhenomenonInfo") String failurePhenomenonInfo,
                      @RequestParam(name = "faultDescribe") String faultDescribe,
                      @RequestParam(name = "planServerDate") String planServerDate,
                      @RequestParam(name = "repairAdvice") String repairAdvice,
                      @RequestParam(name = "installWorkOrderId") String installWorkOrderId,
                      @RequestParam(name = "internalModel") String productModelName,
                      @RequestParam(name = "payMoney", defaultValue = "0", required = false) double payMoney,
                      @RequestParam(name = "remarks", defaultValue = "", required = false) String remarks, HttpServletRequest request) {
        if (StringUtil.isEmpty(sncode)) {
            return ApiResult.error(request, "sncode不得为空");
        }
        if (StringUtil.isEmpty(oldEngineerId)) {
            return ApiResult.error(request, "工程师id不得为空");
        }
        if (StringUtil.isEmpty(installWorkOrderId)) {
            return ApiResult.error(request, "安装工单id不得为空");
        }
        List<String> failureIds = new ArrayList();
        try {
            //获取安装工单
            WorkOrderDTO workOrder = this.orderFeign.getWorkOrderById(installWorkOrderId);
            if (workOrder == null) {
                return ApiResult.error(request, "安装工单信息异常,未找到安装工单");
            }
            Integer deviceId = workOrder.getDeviceId();
            if (null == deviceId) {
                return ApiResult.error(request, "设备信息异常");
            }
            WaterDeviceDTO waterDevice = waterFeign.getWaterDeviceById(deviceId);
            if (ObjectUtil.isNull(waterDevice)) {
                return ApiResult.error(request, "设备信息异常,服务器暂无该用户的水机数据");
            }
          /*  ProductDTO product = productFeign.getProductById(workOrder.getProductId());
            if(product==null){
                throw new YimaoException("产品不存在！");
            }
            ProductCostDTO productCost = productFeign.productCostGetById(waterDevice.getCostId());
            if(productCost==null){
                throw new YimaoException("产品计费方式不存在！");
            }*/
            EngineerDTO engineerDTO = userFeign.getEngineerByUserName(null,oldEngineerId);
            if (ObjectUtil.isNull(engineerDTO)) {
                return ApiResult.error(request, "工程师Id异常,未找到与之关联的工程师");
            }
            if (StringUtil.isEmpty(address)) {
                address = workOrder.getAddress();
            }

            //获取经销商id
            DistributorVO distributorVO = findDistributorVO(workOrder.getDistributorId());

            JSONArray jsonArray = JSONArray.fromObject(failurePhenomenonInfo);
            List<WaterDeviceFailurePhenomenonDTO> list = (List) JSONArray.toCollection(jsonArray, WaterDeviceFailurePhenomenonDTO.class);
            String fuleTypeIds = this.setFailureIds(list, failureIds);
            RepairWorkOrderDTO dto = createRepairWorOrder(workCode, address, waterDevice, sncode, productModelName, engineerDTO, remarks, fuleTypeIds, faultDescribe, planServerDate, repairAdvice, distributorVO, WorkOrderStepEnum.REPAIR_WORK_ORDER_STEP_STARTSERVER.getStep(), AppTypeEnum.OPENAPI.getId(), workOrder);
            //创建本地维修工单信息
            orderFeign.createWorkOrderRepair(dto);

            //创建水机故障类型及解决方案
            waterFeign.createWaterDeviceFailurePhenomenon(workCode,list);
            return ApiResult.result(request, copyInfo(distributorVO));
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    public static JSONObject copyInfo(DistributorVO distributorVO){
        JSONObject dealerInfo = new JSONObject();
        if(!ObjectUtil.isNull(distributorVO)) {
            dealerInfo.put("dealerId", distributorVO.getDistributorId());
            dealerInfo.put("dealerName", distributorVO.getDistributorName());
            dealerInfo.put("dealerPhone", distributorVO.getDistributorPhone());
            dealerInfo.put("dealerIdCard", distributorVO.getDistributorIdCard());
            dealerInfo.put("dealerAccount", distributorVO.getDistributorAccount());
            dealerInfo.put("dealerRole", distributorVO.getDistributorRoleName());

            dealerInfo.put("childDealerId", distributorVO.getChildDistributorId());
            dealerInfo.put("childDealerName", distributorVO.getChildDistributorName());
            dealerInfo.put("childDealerAccount", distributorVO.getChildDistributorAccount());
            dealerInfo.put("childIdCard", distributorVO.getChildDistributorIdCard());
            dealerInfo.put("childPhone", distributorVO.getChildDistributorPhone());
        }
        return dealerInfo;
    }

    /**
     * @description   distributorId
     * @author Liu Yi
     * @date 2019/10/14 17:56
     * @param
     * @return com.yimao.cloud.out.vo.DistributorVO
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
            vo.setChildDistributorName(distributor.getUserName());
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
                vo.setDistributorName(distributor.getUserName());
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
            vo.setDistributorName(distributor.getUserName());
            vo.setDistributorPhone(distributor.getPhone());
            vo.setDistributorIdCard(distributor.getIdCard());
        }
        return vo;
    }

    /***
     * 功能描述: 设置工单属性
     *
     * @param: [workCode, address, waterDeviceEntity, sncode, productModelName, engineer, remark, fuleTypeIds,
     *          faultDescribe, planServerDate, repairAdvice, mainDistributor, distributorDTO, nextStep, createSource]
     * @auther: liu yi
     * @date: 2019/4/11 12:00
     * @return: com.yimao.cloud.pojo.dto.order.WaterDeviceWorkOrderRepairDTO
     */
    public RepairWorkOrderDTO createRepairWorOrder(String workCode, String address, WaterDeviceDTO waterDevice,
                                                   String sncode, String productModelName, EngineerDTO engineer, String remark,
                                                   String fuleTypeIds, String faultDescribe, String planServerDate,
                                                   String repairAdvice, DistributorVO distributorVO, Integer nextStep, String createSource, WorkOrderDTO workOrder) {
        RepairWorkOrderDTO dto = new RepairWorkOrderDTO();
        //获取客户信息(水机用户)
        WaterDeviceUserDTO waterDeviceUser = userFeign.getWaterDeviceUserById(waterDevice.getDeviceUserId());
        //从售后系统获取的code
        dto.setWorkCode(workCode);
        dto.setWorkOrderId(workOrder.getId());
        //dto.setId(workCode);
        dto.setAddrProvince(engineer.getProvince());
        dto.setAddrCity(engineer.getCity());
        dto.setAddrRegion(engineer.getRegion());
        dto.setAddress(address);
        dto.setBespeakAddress(address);
        dto.setRepairAdvice(repairAdvice);
        dto.setFailurePhenomenonIds(fuleTypeIds);

        //经销商信息
        if (null != distributorVO) {
            dto.setDistributorId(distributorVO.getDistributorId());
            dto.setDistributorName(distributorVO.getDistributorName());
            dto.setDistributorPhone(distributorVO.getDistributorPhone());
            dto.setDistributorIdCard(distributorVO.getDistributorIdCard());
            dto.setDistributorRoleId(distributorVO.getDistributorRoleId());
            dto.setDistributorRoleName(distributorVO.getDistributorRoleName());

            dto.setDistributorChildId(distributorVO.getChildDistributorId());
            dto.setDistributorChildName(distributorVO.getChildDistributorName());
            dto.setDistributorChildPhone(distributorVO.getChildDistributorPhone());
        }

        //dto.setProductTypeId(workOrder.getProductTypeId());
        //dto.setOldProductTypeId(workOrder.getOldProductTypeId());
        dto.setProductId(workOrder.getProductId());
        dto.setProductName(YunOldIdUtil.getProductName(workOrder.getDeviceModel()));
        dto.setDeviceId(workOrder.getDeviceId());
        dto.setDeviceModelName(workOrder.getDeviceModel());
        dto.setDeviceSncode(sncode);
        dto.setDeviceBatchCode(waterDevice.getLogisticsCode());
        dto.setDeviceSimcard(waterDevice.getIccid());
        dto.setWorkOrderRemark(remark);
        //安装工信息
        if (null != engineer) {
            dto.setEngineerId(engineer.getId());

            dto.setEngineerName(engineer.getUserName());
            dto.setEngineerPhone(engineer.getPhone());
        }
        dto.setStationId(engineer.getStationId());
        dto.setStationName(engineer.getStationName());
        dto.setConsumerId(waterDeviceUser.getId());
        dto.setConsumerName(waterDeviceUser.getRealName());
        dto.setConsumerPhone(waterDeviceUser.getPhone());
        dto.setConsumerIdCard(waterDeviceUser.getIdCard());

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
        dto.setCostName(workOrder.getCostName());
        dto.setFaultDescise(faultDescribe);
        dto.setWorkOrderSource(createSource);
        dto.setWorkOrderSourceText(getTypeById(createSource).getType());
        dto.setCreateTime(new Date());
        dto.setUpdateTime(new Date());
        dto.setCreateUser(String.valueOf(waterDevice.getEngineerId()));
        dto.setUpdateUser(String.valueOf(waterDevice.getEngineerId()));
        return dto;
    }

    private String setFailureIds(List<WaterDeviceFailurePhenomenonDTO> list, List<String> failureIds) {
        StringBuilder sb = new StringBuilder("");
        int count = 0;
        for (WaterDeviceFailurePhenomenonDTO dto : list) {
            sb.append(dto.getFaultTypeId());
            failureIds.add(dto.getFaultTypeId());
            if (count > 0) {
                sb.append(",");
                ++count;
            }
        }
        return sb.toString();
    }

    @PostMapping(value = {"/applyExchange"})
    @ApiOperation("审核换机")
    public Map applyExChange(@RequestParam(name = "workcode") String workCode,
                             @RequestParam(name = "applyResult") Boolean applyResult,
                             String refuseApplyReason, HttpServletRequest request) {
        RepairWorkOrderDTO repair = this.orderFeign.getWorkOrderRepairByWorkCode(workCode);
        if (ObjectUtil.isNull(repair)) {
            return ApiResult.error(request, ApiStatusCode.SER_NO_DATA);
        }

        repair.setApplyChangeDeviceResultStatus(StatusEnum.YES.value());
        if (applyResult) {
            repair.setApplyChangeDeviceResult(StatusEnum.YES.value());
        } else {
            if (StringUtil.isEmpty(refuseApplyReason)) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_APPLY_REFUSED_RWASON);
            }
            repair.setApplyChangeDeviceResult(StatusEnum.NO.value());
            repair.setRefuseApplyReason(refuseApplyReason);
            repair.setNextStep(WorkOrderStepEnum.REPAIR_WORK_ORDER_STEP_FACTFAULT_DESC.getStep());
            repair.setCurrentStep(WorkOrderStepEnum.REPAIR_WORK_ORDER_STEP_STARTSERVER.getStep());
        }

        try {
            this.orderFeign.updateWaterDeviceWorkOrderRepair(repair);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
        return ApiResult.success(request);
    }

    @PostMapping(value = {"/edit"})
    @ApiOperation("总部编辑维修工单")
    public Map editWorkOrder(@RequestParam(name = "workcode") String workCode,
                             @RequestParam(name = "failurePhenomenonInfo") String failurePhenomenonInfo,
                             @RequestParam(name = "repairAdvice") String repairAdvice,
                             @RequestParam(name = "faultDescribe") String faultDescribe,
                             HttpServletRequest request) {
        if (StringUtil.isEmpty(workCode) || StringUtil.isEmpty(failurePhenomenonInfo)) {
            return ApiResult.error(request, ApiStatusCode.PARAM_NOT_FOUND);
        }

        RepairWorkOrderDTO dto = this.orderFeign.getWorkOrderRepairByWorkCode(workCode);

        if (ObjectUtil.isNull(dto)) {
            return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
        }
        if (dto.getNextStep() > WorkOrderStepEnum.REPAIR_WORK_ORDER_STEP_STARTSERVER.getStep()) {
            return ApiResult.error(request, ApiStatusCode.WORKORDER_IS_STARTSERVER);
        }

        dto.setFaultDescise(faultDescribe);
        dto.setRepairAdvice(repairAdvice);

        try {
            List failures = com.alibaba.fastjson.JSONArray.parseArray(failurePhenomenonInfo, WaterDeviceFailurePhenomenonDTO.class);
            this.orderFeign.updateWaterDeviceWorkOrderRepair(dto);
            this.waterFeign.deleteWaterDeviceFailurePhenomenonByWorkCode(workCode, WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType());
            this.waterFeign.createWaterDeviceFailurePhenomenon(workCode,failures);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        } catch (Exception e1) {
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
        return ApiResult.success(request);
    }
}
