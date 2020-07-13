package com.yimao.cloud.order.service.impl;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.feign.WaterFeign;
import com.yimao.cloud.order.mapper.*;
import com.yimao.cloud.order.service.OrderTransferService;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.system.TransferAreaInfoDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.vo.station.WorkRepairOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class OrderTransferServiceImpl implements OrderTransferService {

    @Resource
    private UserFeign userFeign;

    @Resource
    private SubOrderDetailMapper subOrderDetailMapper;

    @Resource
    private WorkOrderMapper workOrderMapper;

    @Resource
    private OrderRenewMapper orderRenewMapper;

    @Resource
    private ProductIncomeRecordPartMapper productIncomeRecordPartMapper;

    @Resource
    private WaterFeign waterFeign;

    @Resource
    private SystemFeign systemFeign;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private MoveWaterDeviceOrderMapper moveWaterDeviceOrderMapper;

    @Resource
    private WorkRepairOrderMapper workRepairOrderMapper;

    @Resource
    private WorkOrderBackMapper workOrderBackMapper;

    @Resource
    private MaintenanceWorkOrderMapper maintenanceWorkOrderMapper;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void transferData(Integer oldEngineerId, Integer newEngineerId) {
        try {
            log.info("============transferData start===========oldEngineerId=" + oldEngineerId + "newEngineerId=" + newEngineerId);
            Long beginTime = new Date().getTime();//请求开始时间
            //先获取安装工信息
            EngineerDTO oldEngineer = getEngineerInfo(oldEngineerId);

            EngineerDTO newEngineer = getEngineerInfo(newEngineerId);

            //更订单状态为未完成、产品类型为租贷产品的order_sub和sub_order_detail上的安装工信息
            SubOrderDetailDTO sod = initSubOrderDetailData(oldEngineerId, newEngineer);
            subOrderDetailMapper.updateEngineerInfo(sod);

            //更新未完成未退单的工单的安装工信息
            WorkOrderDTO wod = initWorkOrderData(oldEngineerId, newEngineer);
            int result = workOrderMapper.updateWorkorderForEngineer(wod);

            //需要更新安装工的正在进行的工单数
            if (result > 0) {
                //老的安装工数量-
                Map<String, Integer> subMap = new HashMap<>();
                subMap.put("engineerId", oldEngineer.getId());
                subMap.put("num", -result);
                rabbitTemplate.convertAndSend(RabbitConstant.ENGINEER_BUSY_COUNT, subMap);

                //新的安装工数量+
                Map<String, Integer> addMap = new HashMap<>();
                addMap.put("engineerId", newEngineer.getId());
                addMap.put("num", result);
                rabbitTemplate.convertAndSend(RabbitConstant.ENGINEER_BUSY_COUNT, addMap);
            }
            //更新线下支付并且未完成的续费单
            OrderRenewDTO ord = initOrderRenewData(oldEngineerId, newEngineer);
            orderRenewMapper.updateRenewOrderForEngineer(ord);

            //更新未完成的收益上的安装工信息
            ProductIncomeRecordPartDTO pirp = initIncomeData(oldEngineerId, newEngineer);
            productIncomeRecordPartMapper.updateIncomeForEngineer(pirp);

            //更新未完成的移机工单
            MoveWaterDeviceOrderDTO mwdo = initMoveWaterDeviceOrderData(oldEngineerId, newEngineer);
            //更换拆机安装工
            moveWaterDeviceOrderMapper.updateDismantleEngineerInfo(mwdo);
            //更换装机装机安装工
            moveWaterDeviceOrderMapper.updateInstallEngineerInfo(mwdo);

            //更新维修工单
            WorkRepairOrderVO wro = initWorkRepairOrderData(oldEngineerId, newEngineer);
            workRepairOrderMapper.updateEngineerInfo(wro);

            //更新维护工单
            MaintenanceWorkOrderDTO mwo = initMaintenanceWorkOrderData(oldEngineerId, newEngineer);
            maintenanceWorkOrderMapper.updateEngineerInfo(mwo);

            //更新退机工单
            WorkOrderBackDTO wob = initWorkOrderBackData(oldEngineerId, newEngineer);
            workOrderBackMapper.updateEngineerInfo(wob);

            //更新水机上的安装工信息
            WaterDeviceDTO wdd = initWaterDeviceData(oldEngineerId, newEngineer);
            List<WaterDeviceDTO> wdds = new ArrayList<>();
            wdds.add(wdd);
            waterFeign.updateWaterDeviceForEngineer(wdds);

            log.info("============transferData end===========耗时=" + (new Date().getTime() - beginTime) / 1000 + "秒");
        } catch (Exception e) {
            log.error("========安装工转让更新订单、水机、工单信息失败====[oldEngineerId=" + oldEngineerId + ",newEngineerId=" + newEngineerId + "],异常信息:" + e.getMessage());
            throw new YimaoException("安装工转让更新订单、水机、工单信息异常");
        }
    }

    /***
     * 组装续费单对象
     * @param oldEngineerId
     * @param newEngineer
     * @return
     */
    private OrderRenewDTO initOrderRenewData(Integer oldEngineerId, EngineerDTO newEngineer) {
        OrderRenewDTO renew = new OrderRenewDTO();
        renew.setEngineerId(newEngineer.getId());
        renew.setEngineerName(newEngineer.getRealName());
        renew.setEngineerPhone(newEngineer.getPhone());
        renew.setEngineerStationName(newEngineer.getStationName());
        if (null != oldEngineerId) {
            renew.setOldEngineerId(oldEngineerId);
        }

        return renew;
    }

    /****
     * 组装工单对象
     * @param oldEngineerId
     * @param newEngineer
     * @return
     */
    private WorkOrderDTO initWorkOrderData(Integer oldEngineerId, EngineerDTO newEngineer) {
        WorkOrderDTO workOrder = new WorkOrderDTO();
        workOrder.setEngineerId(newEngineer.getId());
        workOrder.setOldEngineerId(newEngineer.getOldId());
        workOrder.setEngineerName(newEngineer.getRealName());
        workOrder.setEngineerPhone(newEngineer.getPhone());
        workOrder.setEngineerIdCard(newEngineer.getIdCard());
        workOrder.setStationId(newEngineer.getStationId());
        workOrder.setStationName(newEngineer.getStationName());
        if (!StringUtil.isEmpty(newEngineer.getOldSiteId())) {
            workOrder.setOldStationId(newEngineer.getOldSiteId());
        }
        if (oldEngineerId != null) {
            workOrder.setOldEnId(oldEngineerId);
        }
        return workOrder;
    }

    /***
     * 组装订单数据
     * @param oldEngineerId
     * @param newEngineer
     * @return
     */
    private SubOrderDetailDTO initSubOrderDetailData(Integer oldEngineerId, EngineerDTO newEngineer) {
        SubOrderDetailDTO sod = new SubOrderDetailDTO();
        if (null != oldEngineerId) {
            sod.setOldEngineerId(oldEngineerId);
        }
        sod.setEngineerId(newEngineer.getId());
        sod.setEngineerName(newEngineer.getRealName());
        sod.setEngineerIdCard(newEngineer.getIdCard());
        sod.setEngineerPhone(newEngineer.getPhone());
        sod.setEngineerProvince(newEngineer.getProvince());
        sod.setEngineerCity(newEngineer.getCity());
        sod.setEngineerRegion(newEngineer.getRegion());
        return sod;
    }

    /***
     * 获取安装工信息
     * @param id
     * @return
     */
    private EngineerDTO getEngineerInfo(Integer id) {
        EngineerDTO engineer = userFeign.getEngineerById(id);
        if (null == engineer) {
            log.error("================安装工信息未找到========,engineerId=" + id);
            throw new NotFoundException("安装工信息未找到");
        }
        return engineer;
    }

    /****
     * 组装收益对象
     * @param oldEngineerId
     * @param newEngineer
     * @return
     */
    private ProductIncomeRecordPartDTO initIncomeData(Integer oldEngineerId, EngineerDTO newEngineer) {
        ProductIncomeRecordPartDTO recordPart = new ProductIncomeRecordPartDTO();
        recordPart.setSubjectId(newEngineer.getId());
        recordPart.setSubjectName(newEngineer.getRealName());
        recordPart.setSubjectPhone(newEngineer.getPhone());
        recordPart.setSubjectIdCard(newEngineer.getIdCard());
        recordPart.setSubjectProvince(newEngineer.getProvince());
        recordPart.setSubjectCity(newEngineer.getCity());
        recordPart.setSubjectRegion(newEngineer.getRegion());
        if (null != oldEngineerId) {
            recordPart.setOldSubjectId(oldEngineerId);
        }
        return recordPart;
    }

    /***
     * 组装水机对象
     * @param oldEngineerId
     * @param newEngineer
     * @return
     */
    private WaterDeviceDTO initWaterDeviceData(Integer oldEngineerId, EngineerDTO newEngineer) {
        WaterDeviceDTO device = new WaterDeviceDTO();
        device.setEngineerId(newEngineer.getId());
        device.setEngineerName(newEngineer.getRealName());
        device.setEngineerPhone(newEngineer.getPhone());
        if (null != oldEngineerId) {
            device.setOldEngineerId(oldEngineerId);
        }
        return device;
    }

    /****
     * 服务站公司承保转让-更新工单、订单信息
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void updateOrderDeviceForEngineerStation(List<TransferAreaInfoDTO> transferAreaInfoDTOS) {
        Long beginTime = new Date().getTime();//请求开始时间
        if (CollectionUtil.isNotEmpty(transferAreaInfoDTOS)) {
            for (TransferAreaInfoDTO trans : transferAreaInfoDTOS) {
                log.info("=============transferOrderDevice start=======privince=" + trans.getProvince() + ",city=" + trans.getCity() + ",region=" + trans.getRegion());
                if (trans.getEngineerId() == null) {
                    //只更新服务站公司信息到工单
                    StationDTO sd = systemFeign.getStationById(trans.getStationId());
                    if (sd == null) {
                        log.error("=============服务站信息不存在==========stationId=" + trans.getStationId());
                        throw new YimaoException("服务站信息不存在");
                    }

                    //更新安装工单
                    WorkOrderDTO wo = new WorkOrderDTO();
                    wo.setProvince(trans.getProvince());
                    wo.setCity(trans.getCity());
                    wo.setRegion(trans.getRegion());
                    wo.setStationId(sd.getId());
                    wo.setStationName(sd.getName());
                    workOrderMapper.updateWorkorderForEngineer(wo);

                    //更新移机工单
                    MoveWaterDeviceOrderDTO updateMoveWaterDeviceOrder = new MoveWaterDeviceOrderDTO();
                    updateMoveWaterDeviceOrder.setOrigProvince(trans.getProvince());
                    updateMoveWaterDeviceOrder.setOrigCity(trans.getCity());
                    updateMoveWaterDeviceOrder.setOrigRegion(trans.getRegion());
                    updateMoveWaterDeviceOrder.setDismantleEngineerStationId(sd.getId());
                    updateMoveWaterDeviceOrder.setDismantleEngineerStationName(sd.getName());
                    //更新该服务区域下拆机安装工服务站信息
                    moveWaterDeviceOrderMapper.updateDismantleEngineerStationInfoByPCR(updateMoveWaterDeviceOrder);
                    updateMoveWaterDeviceOrder.setDestProvince(trans.getProvince());
                    updateMoveWaterDeviceOrder.setDestCity(trans.getCity());
                    updateMoveWaterDeviceOrder.setDestRegion(trans.getRegion());
                    updateMoveWaterDeviceOrder.setInstallEngineerStationId(sd.getId());
                    updateMoveWaterDeviceOrder.setInstallEngineerStationName(sd.getName());
                    //更新该服务区域下装机安装工服务站信息
                    moveWaterDeviceOrderMapper.updateInstallEngineerStationInfoByPCR(updateMoveWaterDeviceOrder);

                    //更新维修工单
                    WorkRepairOrderVO updateWorkRepairOrder = new WorkRepairOrderVO();
                    updateWorkRepairOrder.setProvince(trans.getProvince());
                    updateWorkRepairOrder.setCity(trans.getCity());
                    updateWorkRepairOrder.setRegion(trans.getRegion());
                    updateWorkRepairOrder.setStationId(sd.getId());
                    updateWorkRepairOrder.setStationName(sd.getName());
                    workRepairOrderMapper.updateStationInfoByPCR(updateWorkRepairOrder);

                    //更新维护工单
                    MaintenanceWorkOrderDTO updateMaintenanceWorkOrder = new MaintenanceWorkOrderDTO();
                    updateMaintenanceWorkOrder.setAddrProvince(trans.getProvince());
                    updateMaintenanceWorkOrder.setAddrCity(trans.getCity());
                    updateMaintenanceWorkOrder.setAddrRegion(trans.getRegion());
                    updateMaintenanceWorkOrder.setStationId(sd.getId());
                    updateMaintenanceWorkOrder.setStationName(sd.getName());
                    maintenanceWorkOrderMapper.updateStationInfoByPCR(updateMaintenanceWorkOrder);

                    //更新退机工单
                    WorkOrderBackDTO updateWorkOrderBack = new WorkOrderBackDTO();
                    updateWorkOrderBack.setProvince(trans.getProvince());
                    updateWorkOrderBack.setCity(trans.getCity());
                    updateWorkOrderBack.setRegion(trans.getRegion());
                    updateWorkOrderBack.setStationId(sd.getId());
                    updateWorkOrderBack.setStationName(sd.getName());
                    workOrderBackMapper.updateStationInfoByPCR(updateWorkOrderBack);
                } else {
                    //更新服务站信息、安装工信息到工单和水机
                    EngineerDTO newEngineer = getEngineerInfo(trans.getEngineerId());
                    if (newEngineer == null) {
                        log.error("=============安装工信息不存在==========engineerId=" + trans.getEngineerId());
                        throw new YimaoException("安装工信息不存在");
                    }

                    //转换工单上的安装工，需要将旧的安装工的count-
                    List<Integer> engineerIds = workOrderMapper.queryWorkOrderForEngineerIdsByPcr(trans.getProvince(), trans.getCity(), trans.getRegion());
                    if (!CollectionUtil.isEmpty(engineerIds)) {
                        Map<String, Integer> subMap = null;
                        for (Integer id : engineerIds) {
                            subMap = new HashMap<>();
                            subMap.put("engineerId", id);
                            subMap.put("num", -1);
                            rabbitTemplate.convertAndSend(RabbitConstant.ENGINEER_BUSY_COUNT, subMap);
                        }
                    }

                    //更新工单上的安装工信息
                    WorkOrderDTO wod = initWorkOrderData(null, newEngineer);
                    wod.setProvince(trans.getProvince());
                    wod.setCity(trans.getCity());
                    wod.setRegion(trans.getRegion());
                    int result = workOrderMapper.updateWorkorderForEngineer(wod);
                    if (result > 0) {
                        //新的安装工数量+
                        Map<String, Integer> map = new HashMap<>();
                        map.put("engineerId", newEngineer.getId());
                        map.put("num", result);
                        rabbitTemplate.convertAndSend(RabbitConstant.ENGINEER_BUSY_COUNT, map);
                    }

                    //更新订单上的安装工信息
                    SubOrderDetailDTO sod = initSubOrderDetailData(null, newEngineer);
                    sod.setAddresseeProvince(trans.getProvince());
                    sod.setAddresseeCity(trans.getCity());
                    sod.setAddresseeRegion(trans.getRegion());
                    subOrderDetailMapper.updateEngineerInfo(sod);

                    //更新线下支付并且未完成的续费单
                    OrderRenewDTO ord = initOrderRenewData(null, newEngineer);
                    ord.setProvince(trans.getProvince());
                    ord.setCity(trans.getCity());
                    ord.setRegion(trans.getRegion());
                    orderRenewMapper.updateRenewOrderForEngineer(ord);

                    //更新未完成的收益数据
                    ProductIncomeRecordPartDTO pirp = initIncomeData(null, newEngineer);
                    pirp.setAddresseeProvince(trans.getProvince());
                    pirp.setAddresseeCity(trans.getCity());
                    pirp.setAddresseeRegion(trans.getRegion());
                    productIncomeRecordPartMapper.updateIncomeForEngineer(pirp);

                    //更新移机工单的拆机安装工信息
                    MoveWaterDeviceOrderDTO updateMoveWaterDeviceOrder = initMoveWaterDeviceOrderData(null, newEngineer);
                    updateMoveWaterDeviceOrder.setOrigProvince(trans.getProvince());
                    updateMoveWaterDeviceOrder.setOrigCity(trans.getCity());
                    updateMoveWaterDeviceOrder.setOrigRegion(trans.getRegion());
                    moveWaterDeviceOrderMapper.updateDismantleEngineerInfoByPCR(updateMoveWaterDeviceOrder);

                    //更新移机工单的装机安装工信息
                    updateMoveWaterDeviceOrder.setDestProvince(trans.getProvince());
                    updateMoveWaterDeviceOrder.setDestCity(trans.getCity());
                    updateMoveWaterDeviceOrder.setDestRegion(trans.getRegion());
                    moveWaterDeviceOrderMapper.updateInstallEngineerInfoByPCR(updateMoveWaterDeviceOrder);

                    //更新维修工单的安装工信息
                    WorkRepairOrderVO updateWorkRepairOrder = initWorkRepairOrderData(null, newEngineer);
                    updateWorkRepairOrder.setProvince(trans.getProvince());
                    updateWorkRepairOrder.setCity(trans.getCity());
                    updateWorkRepairOrder.setRegion(trans.getRegion());
                    workRepairOrderMapper.updateEngineerInfoByPCR(updateWorkRepairOrder);

                    //更新维护工单的安装工信息
                    MaintenanceWorkOrderDTO updateMaintenanceWorkOrder = initMaintenanceWorkOrderData(null, newEngineer);
                    updateMaintenanceWorkOrder.setAddrProvince(trans.getProvince());
                    updateMaintenanceWorkOrder.setAddrCity(trans.getCity());
                    updateMaintenanceWorkOrder.setAddrRegion(trans.getRegion());
                    maintenanceWorkOrderMapper.updateEngineerInfoByPCR(updateMaintenanceWorkOrder);

                    //更新退机工单的安装工信息
                    WorkOrderBackDTO updateWorkOrderBack = initWorkOrderBackData(null, newEngineer);
                    updateWorkOrderBack.setProvince(trans.getProvince());
                    updateWorkOrderBack.setCity(trans.getCity());
                    updateWorkOrderBack.setRegion(trans.getRegion());
                    workOrderBackMapper.updateEngineerInfoByPCR(updateWorkOrderBack);
                }
            }
            //防止以上操作出异常导致water服务无法回滚，设备数据的更改在后面进行
            List<WaterDeviceDTO> wdds = new ArrayList<>();
            for (TransferAreaInfoDTO trans : transferAreaInfoDTOS) {
                if (trans.getEngineerId() != null) {
                    EngineerDTO newEngineer = getEngineerInfo(trans.getEngineerId());
                    if (newEngineer == null) {
                        log.error("=============安装工信息不存在==========engineerId=" + trans.getEngineerId());
                        throw new YimaoException("安装工信息不存在");
                    }
                    //,先查询需要更新的设备,然后根据设备id更新水机上的安装工信息。
                    WaterDeviceDTO wdd = initWaterDeviceData(null, newEngineer);
                    wdd.setProvince(trans.getProvince());
                    wdd.setCity(trans.getCity());
                    wdd.setRegion(trans.getRegion());
                    wdd.setAddress(trans.getProvince() + trans.getCity() + trans.getRegion());
                    List<Integer> deviceIds = waterFeign.getWaterDeviceListByPrc(wdd);
                    if (!CollectionUtil.isEmpty(deviceIds)) {
                        wdd.setIds(deviceIds);
                        wdds.add(wdd);
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(wdds)) {
                waterFeign.updateWaterDeviceForEngineer(wdds);
            }
        }
        log.info("=============transferOrderDevice  end=======耗时=" + (new Date().getTime() - beginTime) / 1000 + "秒");
    }

    private WorkOrderBackDTO initWorkOrderBackData(Integer oldEngineerId, EngineerDTO newEngineer) {
        WorkOrderBackDTO workOrderBackDTO = new WorkOrderBackDTO();
        if (oldEngineerId != null) {
            workOrderBackDTO.setOldEngineerId(oldEngineerId);
        }
        workOrderBackDTO.setEngineerId(newEngineer.getId());
        workOrderBackDTO.setEngineerName(newEngineer.getRealName());
        workOrderBackDTO.setEngineerPhone(newEngineer.getPhone());
        workOrderBackDTO.setStationId(newEngineer.getStationId());
        workOrderBackDTO.setStationName(newEngineer.getStationName());
        return workOrderBackDTO;
    }

    private MaintenanceWorkOrderDTO initMaintenanceWorkOrderData(Integer oldEngineerId, EngineerDTO newEngineer) {
        MaintenanceWorkOrderDTO maintenanceWorkOrderDTO = new MaintenanceWorkOrderDTO();
        if (oldEngineerId != null) {
            maintenanceWorkOrderDTO.setOldEngineerId(oldEngineerId);
        }
        maintenanceWorkOrderDTO.setEngineerId(newEngineer.getId());
        maintenanceWorkOrderDTO.setEngineerName(newEngineer.getRealName());
        maintenanceWorkOrderDTO.setEngineerPhone(newEngineer.getPhone());
        maintenanceWorkOrderDTO.setEngineerIdCard(newEngineer.getIdCard());
        maintenanceWorkOrderDTO.setStationId(newEngineer.getStationId());
        maintenanceWorkOrderDTO.setStationName(newEngineer.getStationName());
        return maintenanceWorkOrderDTO;
    }

    private WorkRepairOrderVO initWorkRepairOrderData(Integer oldEngineerId, EngineerDTO newEngineer) {
        WorkRepairOrderVO workRepairOrder = new WorkRepairOrderVO();
        if (oldEngineerId != null) {
            workRepairOrder.setOldEngineerId(oldEngineerId);
        }
        workRepairOrder.setEngineerId(newEngineer.getId());
        workRepairOrder.setEngineerName(newEngineer.getRealName());
        workRepairOrder.setStationId(newEngineer.getStationId());
        workRepairOrder.setStationName(newEngineer.getStationName());
        return workRepairOrder;
    }

    private MoveWaterDeviceOrderDTO initMoveWaterDeviceOrderData(Integer oldEngineerId, EngineerDTO newEngineer) {
        MoveWaterDeviceOrderDTO moveWaterDeviceOrderDTO = new MoveWaterDeviceOrderDTO();
        if (oldEngineerId != null) {
            moveWaterDeviceOrderDTO.setOldEngineerId(oldEngineerId);
        }
        moveWaterDeviceOrderDTO.setDismantleEngineerId(newEngineer.getId());
        moveWaterDeviceOrderDTO.setDismantleEngineerName(newEngineer.getRealName());
        moveWaterDeviceOrderDTO.setDismantleEngineerPhone(newEngineer.getPhone());
        moveWaterDeviceOrderDTO.setDismantleEngineerStationId(newEngineer.getStationId());
        moveWaterDeviceOrderDTO.setDismantleEngineerStationName(newEngineer.getStationName());

        moveWaterDeviceOrderDTO.setInstallEngineerId(newEngineer.getId());
        moveWaterDeviceOrderDTO.setInstallEngineerName(newEngineer.getRealName());
        moveWaterDeviceOrderDTO.setInstallEngineerPhone(newEngineer.getPhone());
        moveWaterDeviceOrderDTO.setInstallEngineerStationId(newEngineer.getStationId());
        moveWaterDeviceOrderDTO.setInstallEngineerStationName(newEngineer.getStationName());
        return moveWaterDeviceOrderDTO;
    }

}
