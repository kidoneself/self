package com.yimao.cloud.out.processor;


import com.google.common.collect.Lists;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.yun.YunOldIdUtil;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.feign.ProductFeign;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.job.WaterDeviceWorkOrderCompleteDateColorJob;
import com.yimao.cloud.out.utils.InterfaceUtil;
import com.yimao.cloud.out.utils.ObjectUtil;
import com.yimao.cloud.out.vo.DistributorVO;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;


/***
 * 功能描述:同步水机设备故障记录
 *
 * @auther: liu yi
 * @date: 2019/5/6 14:06
 */
@Component
@Slf4j
public class SyncMaintenanceWorkOrderProcessor {
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private ProductFeign productFeign;

    /***
     * 功能描述:维护工单推送售后并记录
     *
     * @param:
     * @auther: liu yi
     * @date: 2019/5/6 16:58
     * @return: void
     */
    @RabbitListener(queues = RabbitConstant.SYNC_MAINTENANCE_WORK_ORDER)
    @RabbitHandler
    public void processor(Map<String, Object> map) {
        if (null == map || map.isEmpty()) {
            log.error("创建维护工单失败：推送参数不能为空");
            return;
        }

        StringBuilder materielTypeNamesSb = new StringBuilder();
        StringBuilder materielTypeIdsSb = new StringBuilder();
        Map baideResultMap;
        try {
            WaterDeviceDTO waterDevice = (WaterDeviceDTO) map.get("waterDevice");
            WorkOrderDTO workOrder = (WorkOrderDTO) map.get("workOrder");
            String filterName = (String) map.get("filterName");
            if (waterDevice == null || workOrder == null || filterName == null) {
                log.error("创建维护工单失败：传入参数不能为空");
                return;
            }

            List<String> filterList = Lists.newArrayList(filterName.split(","));
            //获取到对应的售后系统的耗材id和名称
            Map<String, String> consumableTypeMap = YunOldIdUtil.getBaideFilterMap(filterList);
            if (consumableTypeMap == null || consumableTypeMap.size() <= 0) {
                log.error("没有对应的百得耗材信息,工单id:" + workOrder.getId());
            }

            //sn码查询没有提交完成的维护工单
            List<MaintenanceWorkOrderDTO> list = this.orderFeign.getWorkOrderMaintenanceBySnCode(waterDevice.getSn(), null, StatusEnum.NO.value(), MaintenanceWorkOrderSourceEnum.AUTO_CREATE.value);
            Set<String> unFinshedMaterielSet = new HashSet();
            for (MaintenanceWorkOrderDTO dto : list) {
                String[] ids = dto.getMaterielDetailIds().split(",");
                for (String id : ids) {
                    unFinshedMaterielSet.add(id);
                }
            }
            //过滤，只对未提交的滤芯类型创建维护工单
            consumableTypeMap.forEach((key, value) -> {
                boolean flag = false;
                for (String id : unFinshedMaterielSet) {
                    if (id.equals(key)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    materielTypeIdsSb.append(key + ",");
                    materielTypeNamesSb.append(value + ",");
                }
            });
            if (materielTypeIdsSb.toString().isEmpty()) {
                log.error("该设备还有未完成维护工单！");
                return;
            }
            String materielTypeIds = materielTypeIdsSb.substring(0, materielTypeIdsSb.length() - 1);
            String materielTypeNames = materielTypeNamesSb.substring(0, materielTypeNamesSb.length() - 1);

            String address = workOrder.getModifyAddress();
            if (StringUtil.isEmpty(address)) {
                address = workOrder.getAddress();
            }
            if (waterDevice.getCostId() == null) {
                log.error("创建维护工单失败：设备没有计费方式,工单id:" + workOrder.getId());
                return;
            }
            DistributorVO distributorVO = findDistributorVO(waterDevice.getDistributorId());
            if (Objects.isNull(distributorVO)) {
                log.error("创建维护工单失败：设备关联经销商不存在,工单id:" + workOrder.getId());
                return;
            }
            ProductCostDTO productCost = productFeign.productCostGetById(waterDevice.getCostId());
            if (Objects.isNull(productCost)) {
                log.error("创建维护工单失败：计费方式不存在,工单id:" + workOrder.getId());
                return;
            }
            WaterDeviceUserDTO deviceUser=userFeign.getWaterDeviceUserById(waterDevice.getDeviceUserId());
            if (Objects.isNull(deviceUser)) {
                log.error("设备关联水机用户不存在,设备id："+waterDevice.getId());
                return;
            }

            String costId = productCost.getOldId();
            String costName = productCost.getName();
            baideResultMap = BaideApiUtil.maintenanceWorkOrderAdd(materielTypeIds, waterDevice.getSn(), waterDevice.getLogisticsCode(), waterDevice.getIccid(), YunOldIdUtil.getProductId(workOrder.getDeviceModel()), workOrder.getDeviceModel(),
                    workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion(), deviceUser.getOldId(), waterDevice.getDeviceUserName(), waterDevice.getDeviceUserPhone(),
                    address, distributorVO.getOldDistributorId(), distributorVO.getDistributorName(), distributorVO.getDistributorPhone(), distributorVO.getDistributorIdCard(), distributorVO.getDistributorAccount(), distributorVO.getDistributorRoleName(),
                    distributorVO.getOldChildDistributorId(), distributorVO.getChildDistributorName(), distributorVO.getChildDistributorAccount(), distributorVO.getChildDistributorIdCard(), distributorVO.getChildDistributorPhone(), costId,
                    costName, "滤芯更换", workOrder.getId());

            log.info("百得创建维护工单结果:", new Object[]{baideResultMap.toString()});
            if (!InterfaceUtil.checkBaideExecuteResult(baideResultMap)) {
                log.error("售后维护工单创建失败：工单id:" + workOrder.getId() + "，错误信息：" + baideResultMap.get("msg").toString());
                return;
            }

            JSONObject jsonObject = JSONObject.fromObject(baideResultMap.get("data"));
            Map<String, Object> configMap = WaterDeviceWorkOrderCompleteDateColorJob.getDateInfo(WorkOrderTypeEnum.ORDER_TYPE_INSTALL.getType(), new Date(), false, "");
            Long countdownTime = (Long) configMap.get("lastCompleteTime");
            String workCode = jsonObject.get("code").toString();
            MaintenanceWorkOrderDTO dto = new MaintenanceWorkOrderDTO();
            dto.setId(workCode); //保障后台能够正常查询
            dto.setWorkCode(workCode);
            dto.setWorkOrderRemark(workOrder.getRemark());
            // dto.setCountdownTime(new Date());
            dto.setCountdownTime(new Date(countdownTime));
            dto.setMaintenanceRemark("滤芯更换");
            dto.setMaterielDetailName(materielTypeNames);//耗材类型名称
            dto.setMaterielDetailIds(materielTypeIds);//耗材类型
            dto.setAddrProvince(waterDevice.getProvince());
            dto.setAddrCity(waterDevice.getCity());
            dto.setAddrRegion(waterDevice.getRegion());
            dto.setAddress(address);
            //dto.setAddressDetail(dto.getAddrProvince() + dto.getAddrCity() + dto.getAddrRegion() + address);
            dto.setBespeakAddress(address);
            //设置经销商信息
            dto.setDistributorId(workOrder.getDistributorId());
            dto.setDistributorName(workOrder.getDistributorName());
            dto.setDistributorIdCard(workOrder.getDistributorIdCard());
            dto.setDistributorPhone(workOrder.getDistributorPhone());
            dto.setDistributorRoleId(workOrder.getDistributorRoleId());
            dto.setDistributorRoleName(workOrder.getDistributorRoleName());
            dto.setDistributorChildId(workOrder.getSubDistributorId());
            dto.setDistributorChildName(workOrder.getSubDistributorName());
            dto.setDistributorChildPhone(workOrder.getSubDistributorPhone());
            dto.setCreateUser(String.valueOf(workOrder.getEngineerId()));
            dto.setUpdateUser(String.valueOf(workOrder.getEngineerId()));
            dto.setUpdateTime(new Date());
            dto.setCreateTime(new Date());
            dto.setProductId(workOrder.getProductId());     //产品Id
            dto.setKindName(waterDevice.getDeviceScope());  // 产品适用范围
            dto.setDeviceId(waterDevice.getId());
            dto.setDeviceModelName(workOrder.getDeviceModel());//  产品名称 1601L-00
            dto.setDeviceSncode(waterDevice.getSn());
            dto.setDeviceBatchCode(waterDevice.getLogisticsCode());
            dto.setDeviceSimcard(waterDevice.getIccid());
            //设置工程师和安装工程师、服务站门店等信息
            dto.setEngineerId(workOrder.getEngineerId());
            dto.setEngineerName(workOrder.getEngineerName());
            dto.setEngineerIdCard(workOrder.getEngineerIdCard());
            dto.setEngineerPhone(workOrder.getEngineerPhone());
            dto.setStationId(workOrder.getStationId());
            dto.setStationName(workOrder.getStationName());
            dto.setConsumerId(workOrder.getUserId());// 所属客户Id
            dto.setConsumerName(waterDevice.getDeviceUserName());
            dto.setConsumerPhone(waterDevice.getDeviceUserPhone());
            /* dto.setConsumerIdCard(consumerUser.getIdCard());*/
            //设置工单步骤 此为测试阶段
            //WorkOrderStepUtil.setMaintenanceOrderStep(dto, WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE, WorkOrderStepEnum.MAINTENANCE_WORK_ORDER_STEP_APPOINTMENT.getStep());
            //设置受理状态
            dto.setState(WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.state);
            dto.setStateText(WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.stateText);
            //设置下一步骤和当前步骤,并强制接单
            dto.setCurrentStep(WorkOrderStepEnum.MAINTENANCE_WORK_ORDER_STEP_ACCEPT.getStep());
            dto.setNextStep(WorkOrderStepEnum.MAINTENANCE_WORK_ORDER_STEP_APPOINTMENT.getStep());

            dto.setAcceptStatus(StatusEnum.YES.value());
            dto.setAcceptTime(new Date());
            dto.setCostName(costName);//计费方式名称
            dto.setSource(1);
            dto.setChangeFilterStatus(StatusEnum.NO.value());
            dto.setDestroyFilterImgStatus(StatusEnum.NO.value());
            dto.setFilterCount(materielTypeNames.split(",").length);
            dto.setWorkOrderId(workOrder.getId());

            orderFeign.createWorkOrderMaintenance(dto);
        } catch (YimaoException e) {
            log.error("水机设备调用创建维护工单失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("水机设备调用创建维护工单失败：" + e.getMessage());
        }
    }

    /**
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

}
