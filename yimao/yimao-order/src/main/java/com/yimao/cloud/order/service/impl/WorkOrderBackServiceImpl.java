package com.yimao.cloud.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ServiceEngineerChangeWorkOrderTypeEnum;
import com.yimao.cloud.base.enums.WorkOrderBackStatusEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.feign.WaterFeign;
import com.yimao.cloud.order.mapper.ServiceEngineerChangeRecordMapper;
import com.yimao.cloud.order.mapper.WorkOrderBackMapper;
import com.yimao.cloud.order.po.ServiceEngineerChangeRecord;
import com.yimao.cloud.order.po.WorkOrderBack;
import com.yimao.cloud.order.service.OrderSubService;
import com.yimao.cloud.order.service.WorkOrderBackService;
import com.yimao.cloud.order.service.WorkOrderService;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderBackDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.system.StationBackStockRecordDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.EngineerServiceAreaDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.query.order.WorkOrderBackQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Liu Yi
 * @description 退机工单服务接口实现类
 * @date 2020/6/22 15:58
 */
@Service
@Slf4j
public class WorkOrderBackServiceImpl implements WorkOrderBackService {
    @Resource
    private WorkOrderBackMapper workOrderBackMapper;
    @Resource
    private UserCache userCache;
    @Resource
    private UserFeign userFeign;
    @Resource
    private WorkOrderService workOrderService;
    @Resource
    private WaterFeign waterFeign;
    @Resource
    private OrderSubService orderSubService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ServiceEngineerChangeRecordMapper serviceEngineerChangeRecordMapper;

    /**
     * 创建退机工单
     *
     * @param dto 工单对象
     */
    @Override
    public void createWorkOrderBack(WorkOrderBackDTO dto) {
        WorkOrderBack query = new WorkOrderBack();
        query.setSn(dto.getSn());
        int count = workOrderBackMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("该SN退机工单已存在！");
        }
        if (StringUtil.isBlank(dto.getSn())) {
            throw new BadRequestException("sn不能为空！");
        }
        if (dto.getEngineerId() == null) {
            throw new BadRequestException("请选择服务安装工！");
        }
        WaterDeviceDTO waterDevice = waterFeign.getBySnCode(dto.getSn());
        dto.setCode(UUIDUtil.getWorkOrderBackNoToStr());
        dto.setIccid(waterDevice.getIccid());
        dto.setLogisticsCode(waterDevice.getLogisticsCode());
        dto.setProvince(waterDevice.getProvince());
        dto.setCity(waterDevice.getCity());
        dto.setRegion(waterDevice.getRegion());
        dto.setAddress(waterDevice.getAddress());
        dto.setCostId(waterDevice.getCostId());
        dto.setCostName(waterDevice.getCostName());
        dto.setUserId(waterDevice.getDeviceUserId());
        dto.setUserName(waterDevice.getDeviceUserName());
        dto.setUserPhone(waterDevice.getDeviceUserPhone());
        dto.setDistributorId(waterDevice.getDistributorId());
        dto.setDistributorName(waterDevice.getDistributorName());
        dto.setDistributorAccount(waterDevice.getDistributorAccount());

        EngineerDTO engineer = userFeign.getEngineerById(dto.getEngineerId());
        if (Objects.isNull(engineer)) {
            throw new YimaoException("所选安装工不存在！");
        }
        dto.setEngineerId(engineer.getId());
        dto.setEngineerName(engineer.getUserName());
        dto.setEngineerPhone(engineer.getPhone());
        WorkOrderDTO workorder = workOrderService.getWorkOrderById(waterDevice.getWorkOrderId());
        OrderSubDTO orderSub = orderSubService.findOrderById(workorder.getSubOrderId());
        dto.setStationId(workorder.getStationId());
        dto.setStationName(workorder.getStationName());
        dto.setWorkOrderId(workorder.getId());
        dto.setProductId(workorder.getProductId());
        dto.setProductName(workorder.getProductName());
        dto.setProductCategoryName(waterDevice.getDeviceModel());
        dto.setProductFirstCategoryId(orderSub.getProductOneCategoryId());
        dto.setProductFirstCategoryName(orderSub.getProductOneCategoryName());
        dto.setProductTwoCategoryId(orderSub.getProductTwoCategoryId());
        dto.setProductTwoCategoryName(orderSub.getProductTwoCategoryName());
        dto.setProductCategoryId(orderSub.getProductCategoryId());
        dto.setProductCategoryName(orderSub.getProductCategoryName());

        dto.setInstallDate(waterDevice.getSnEntryTime());
        dto.setSource(1);
        dto.setCreateTime(new Date());
        dto.setStatus(WorkOrderBackStatusEnum.ASSIGNED.value);
        dto.setCountdownTime(dto.getServiceDate());
        dto.setLongitude(waterDevice.getLongitude());
        dto.setLatitude(waterDevice.getLatitude());

        workOrderBackMapper.insert(new WorkOrderBack(dto));
    }

    /**
     * 根据条件查询退机工单信息
     */
    @Override
    public List<Map<String, Object>> getWorkOrderBackCountByEngineerId() {
        Integer engineerId = userCache.getCurrentEngineerId();

        if (engineerId == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }

        List<Map<String, Object>> list = workOrderBackMapper.getWorkOrderBackCountByEngineerId(engineerId);
        return list;
    }

    /**
     * 根据条件查询退机工单信息
     */
    @Override
    public PageVO<WorkOrderBackDTO> getWorkOrderBackList(WorkOrderBackQueryDTO workOrderBackQueryDTO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WorkOrderBackDTO> ptPage = workOrderBackMapper.getWorkOrderBackList(workOrderBackQueryDTO);
        return new PageVO<>(pageNum, ptPage);
    }

    /**
     * 描述：根据退机工单id获取工单信息
     *
     * @param id 工单ID
     **/
    @Override
    public WorkOrderBackDTO getWorkOrderBackById(Integer id) {
        WorkOrderBackDTO dto = new WorkOrderBackDTO();
        WorkOrderBack workOrder = workOrderBackMapper.selectByPrimaryKey(id);
        if (workOrder == null) {
            throw new YimaoException("退机工单不存在！");
        }

        workOrder.convert(dto);
        //退机工单已完成获取自己的设备信息，未完成获取设备上的信息
        if (workOrder.getStatus() != 4) {
            WaterDeviceDTO waterDevice = waterFeign.getBySnCode(workOrder.getSn());
            dto.setIccid(waterDevice.getIccid());
            dto.setLogisticsCode(waterDevice.getLogisticsCode());
            dto.setProductCategoryName(waterDevice.getDeviceModel());
            dto.setCostName(waterDevice.getCostName());
            dto.setMoney(waterDevice.getMoney());
            dto.setInstallDate(waterDevice.getSnEntryTime());
        }
        return dto;
    }

    /**
     * 修改退机工单
     *
     * @param dto 工单对象
     */
    @Override
    public void updateWorkOrderBack(WorkOrderBackDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("该工单号参数不能为空！");
        }
        WorkOrderBack query = new WorkOrderBack();
        query.setId(dto.getId());
        int count = workOrderBackMapper.selectCount(query);
        if (count < 1) {
            throw new BadRequestException("该工单号不存在！");
        }
        WorkOrderBack workOrderBack = new WorkOrderBack(dto);
        workOrderBack.setUpdateTime(new Date());
        int result = workOrderBackMapper.updateByPrimaryKeySelective(workOrderBack);
        if (result < 1) {
            log.error("修改工单失败！");
        }
    }

    /**
     * 描述：完成退机工单
     **/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void finishWorkOrderBack(Integer id, String snCode) {
        if (StringUtils.isBlank(snCode)) {
            throw new BadRequestException("sn必填!");
        }
        if (null == id) {
            throw new BadRequestException("id不能为空!");
        }

        try {
            WorkOrderBack workOrder = workOrderBackMapper.selectByPrimaryKey(id);
            if (workOrder == null) {
                throw new YimaoException("退机工单不存在！");
            }
            if (!snCode.equalsIgnoreCase(workOrder.getSn())) {
                throw new YimaoException("sn不匹配!");
            }
            Date nowDate = new Date();
            WaterDeviceDTO waterDevice = waterFeign.getBySnCode(snCode.trim());
            workOrder.setUpdateTime(new Date());
            workOrder.setStatus(WorkOrderBackStatusEnum.COMPLETED.value);
            workOrder.setMoney(waterDevice.getMoney());
            workOrder.setIccid(waterDevice.getIccid());
            workOrder.setLogisticsCode(waterDevice.getLogisticsCode());
            workOrder.setCostId(waterDevice.getCostId());
            workOrder.setCostName(waterDevice.getCostName());
            workOrder.setCompleteTime(nowDate);
            workOrder.setUpdateTime(nowDate);

            //1-停用 原设备sim卡  2- 设备信息变更为已删除（这个设备后面还要继续安装） 扣费计划不管 3-设备的金额等信息记录到退机工单
            workOrderBackMapper.updateByPrimaryKeySelective(workOrder);
            waterFeign.deactivatedSimCard(waterDevice.getId());
            waterFeign.deleteWaterDevice(waterDevice.getId());

            WorkOrderDTO workorder = workOrderService.getWorkOrderById(waterDevice.getWorkOrderId());
            OrderSubDTO orderSub = orderSubService.findOrderById(workorder.getSubOrderId());
            StationBackStockRecordDTO dto = new StationBackStockRecordDTO();
            dto.getWorkorderBackCode();
            dto.setSn(snCode);
            dto.setStationId(workorder.getStationId());
            dto.setStationName(workorder.getStationName());
            dto.setProvince(workorder.getProvince());
            dto.setCity(workorder.getCity());
            dto.setRegion(workorder.getRegion());
            dto.setAddress(workorder.getAddress());
            dto.setEngineerId(workorder.getEngineerId());
            dto.setEngineerName(workorder.getEngineerName());
            dto.setProductCategoryId(orderSub.getProductCategoryId());
            dto.setProductCategoryName(orderSub.getProductCategoryName());
            dto.setProductTwoCategoryName(orderSub.getProductTwoCategoryName());
            dto.setProductFirstCategoryName(orderSub.getProductOneCategoryName());
            dto.setIsTransferStock(false);
            dto.setCompleteTime(nowDate);
            dto.setTransferUserId(userCache.getCurrentEngineerId());
            //推到不良品库
            rabbitTemplate.convertAndSend(RabbitConstant.STATION_BACK_STOCK_RECORD, dto);
        } catch (Exception e) {
            log.error("完成退机工单失败，工单id:" + id);
            throw new YimaoException("完成退机工单失败！");
        }
    }

    @Override
    public List<RenewDTO> queryWorkOrderBackList(String completeTime, Integer engineerId, Integer timeType) {
        return workOrderBackMapper.queryWorkOrderBackList(completeTime, engineerId, timeType);
    }


    @Override
    public Map<String, Integer> getWorkOrderBackCount(Integer engineerId) {
        Map<String, Integer> backMap = new HashMap<>();
        backMap.put("assignedWorkOrderCount", workOrderBackMapper.getWorkOrderBackCount(engineerId, WorkOrderBackStatusEnum.ASSIGNED.value));//待处理
        backMap.put("installingWorkOrderCount", workOrderBackMapper.getWorkOrderBackCount(engineerId, WorkOrderBackStatusEnum.INSTALLING.value));//处理中
        backMap.put("suspendWorkOrderCount", workOrderBackMapper.getWorkOrderBackCount(engineerId, WorkOrderBackStatusEnum.SUSPEND.value));//挂单
        backMap.put("comoleteWorkOrderCount", workOrderBackMapper.getWorkOrderBackCount(engineerId, WorkOrderBackStatusEnum.COMPLETED.value));//已完成
        return backMap;
    }

    @Override
    public Integer getBackModelTotalCount(Integer engineerId) {
        return workOrderBackMapper.getBackModelTotalCount(engineerId);
    }

    @Override
    public void workOrderBackChangeEngineer(Integer id, Integer engineerId, List<Integer> engineerIds, Integer source, String operator) {
        WorkOrderBack workOrder = workOrderBackMapper.selectByPrimaryKey(id);
        if (workOrder == null) {
            throw new YimaoException("退机工单不存在！");
        }
        if (workOrder.getStatus() != WorkOrderBackStatusEnum.ASSIGNED.value) {
            throw new BadRequestException("该退机工单已受理，不能进行更换安装工的操作！");
        }
        if (!engineerIds.contains(workOrder.getEngineerId())) {
            //登录管理员没有管理该退机工单的权限
            throw new BadRequestException("您没有操作该退机工单更换服务人员的权限");
        }
        EngineerDTO origEngineer = userFeign.getEngineerById(workOrder.getEngineerId());
        if (origEngineer == null) {
            throw new BadRequestException("原退机服务人员信息不存在！");
        }

        EngineerDTO destEngineer = userFeign.getEngineerById(engineerId);
        if (destEngineer == null) {
            throw new BadRequestException("替换的安装工信息不存在");
        }
        boolean isServiceArea = false;
        for (EngineerServiceAreaDTO serviceArea : destEngineer.getServiceAreaList()) {
            String province = serviceArea.getProvince();
            String city = serviceArea.getCity();
            String region = serviceArea.getRegion();
            if (workOrder.getProvince().equals(province) && workOrder.getCity().equals(city) && workOrder.getRegion().equals(region)) {
                isServiceArea = true;
                break;
            }
        }
        if (!isServiceArea) {
            throw new YimaoException("更换安装工服务区域不匹配");
        }
        if (origEngineer.getStationId().intValue() != destEngineer.getStationId()) {
            throw new YimaoException("替代的安装工与原安装工不属于同一服务站门店！");
        }
        Date now = new Date();
        WorkOrderBack update = new WorkOrderBack();
        update.setId(id);
        update.setEngineerId(engineerId);
        update.setEngineerName(destEngineer.getRealName());
        update.setEngineerPhone(destEngineer.getPhone());
        update.setUpdateTime(now);
        workOrderBackMapper.updateByPrimaryKeySelective(update);

        // 服务人员更换记录
        ServiceEngineerChangeRecord serviceEngineerChangeRecord = new ServiceEngineerChangeRecord();
        serviceEngineerChangeRecord.setWorkOrderNo(workOrder.getWorkOrderId());
        serviceEngineerChangeRecord.setWorkOrderType(ServiceEngineerChangeWorkOrderTypeEnum.BACK_WORK_ORDER.value);
        serviceEngineerChangeRecord.setSource(source);
        serviceEngineerChangeRecord.setOrigEngineerId(origEngineer.getId());
        serviceEngineerChangeRecord.setOrigEngineerName(origEngineer.getRealName());
        serviceEngineerChangeRecord.setDestEngineerId(destEngineer.getId());
        serviceEngineerChangeRecord.setDestEngineerName(destEngineer.getRealName());
        serviceEngineerChangeRecord.setOperator(operator);
        serviceEngineerChangeRecord.setTime(now);
        serviceEngineerChangeRecordMapper.insertSelective(serviceEngineerChangeRecord);
    }
}
