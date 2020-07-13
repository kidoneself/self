package com.yimao.cloud.order.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.locatorUtil;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.WaterFeign;
import com.yimao.cloud.order.mapper.MaintenanceWorkOrderMapper;
import com.yimao.cloud.order.po.MaintenanceWorkOrder;
import com.yimao.cloud.order.service.MaintenanceWorkOrderOperateLogService;
import com.yimao.cloud.order.service.MaintenanceWorkOrderService;
import com.yimao.cloud.order.utils.IdGenerator;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.dto.system.StationMaterialStockDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import com.yimao.cloud.pojo.query.station.StationMaintenanceOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/***
 * 功能描述:维护工单
 *
 * @auther: liu yi
 * @date: 2019/4/1 11:10
 */
@Service
@Slf4j
public class MaintenanceWorkOrderServiceImpl implements MaintenanceWorkOrderService {
    @Resource
    private MaintenanceWorkOrderMapper maintenanceWorkOrderMapper;
    @Resource
    private UserCache userCache;
    @Resource
    private WaterFeign waterFeign;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private MaintenanceWorkOrderOperateLogService maintenanceWorkOrderOperateLogService;

    @Override
    public MaintenanceWorkOrderDTO getWorkOrderMaintenanceById(String id) {
        if (StringUtil.isBlank(id)) {
            throw new BadRequestException("id不能为空!");
        }
        MaintenanceWorkOrder order = maintenanceWorkOrderMapper.selectByPrimaryKey(id);
        if (order == null) {
            return null;
        }
        MaintenanceWorkOrderDTO dto = new MaintenanceWorkOrderDTO();
        order.convert(dto);
        //设置设备当前余额
        if (Objects.nonNull(order.getDeviceSncode())) {
            WaterDeviceDTO waterDeviceDTO = waterFeign.getBySnCode(order.getDeviceSncode());
            if (Objects.nonNull(waterDeviceDTO)) {
                dto.setMoney(waterDeviceDTO.getMoney());
                dto.setDeviceInstallTime(waterDeviceDTO.getCreateTime());
                dto.setDeviceCompleteTime(waterDeviceDTO.getSnEntryTime());
            }
        }
        return dto;
    }

    @Override
    public MaintenanceWorkOrderDTO getWorkOrderMaintenanceByWorkCode(String workCode) {
        if (StringUtil.isBlank(workCode)) {
            throw new BadRequestException("workCode不能为空!");
        }
        Example example = new Example(MaintenanceWorkOrder.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("workCode", workCode);
        MaintenanceWorkOrder order = maintenanceWorkOrderMapper.selectOneByExample(example);
        if (order == null) {
            return null;
        }

        MaintenanceWorkOrderDTO dto = new MaintenanceWorkOrderDTO();
        order.convert(dto);

        return dto;
    }

    @Override
    public List<MaintenanceWorkOrderDTO> getWorkOrderMaintenanceBySnCode(String sncode, Integer state, String workOrderCompleteStatus, Integer source) {
        if (StringUtil.isBlank(sncode)) {
            throw new BadRequestException("SN不能为空!");
        }
        Example example = new Example(MaintenanceWorkOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deviceSncode", sncode);
        if (state != null) {
            criteria.andEqualTo("state", state);
        }
        if (StringUtil.isNotBlank(workOrderCompleteStatus)) {
            criteria.andEqualTo("workOrderCompleteStatus", workOrderCompleteStatus);
        }
        if (source != null) {
            criteria.andEqualTo("source", source);
        }
        criteria.andEqualTo("delStatus", StatusEnum.NO.value());

        List<MaintenanceWorkOrder> list = this.maintenanceWorkOrderMapper.selectByExample(example);
        MaintenanceWorkOrderDTO dto;
        List<MaintenanceWorkOrderDTO> listDto = new ArrayList<>();
        //转换
        for (MaintenanceWorkOrder waterDeviceWorkOrderMaintenance : list) {
            dto = new MaintenanceWorkOrderDTO();
            waterDeviceWorkOrderMaintenance.convert(dto);
            listDto.add(dto);
        }

        return listDto;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void createWorkOrderMaintenance(MaintenanceWorkOrderDTO dto) {
        if (dto == null) {
            throw new BadRequestException("传入参数不能为空!");
        }
        if (Objects.isNull(dto.getDeviceSncode()) || Objects.isNull(dto.getMaterielDetailName())) {
            throw new BadRequestException("SN码或需更换滤芯不能为空!");
        }

        List<String> listByStr = StringUtil.spiltListByStr(dto.getMaterielDetailName());
        //判断是否有未完成的维护工单
        Example example = new Example(MaintenanceWorkOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deviceSncode", dto.getDeviceSncode());
//        criteria.andEqualTo("deviceModelName",dto.getDeviceModelName());
        criteria.andIn("materielDetailName", listByStr);
        criteria.andEqualTo("delStatus", "N");
        criteria.andNotEqualTo("state", WorkOrderNewStatusEnum.COMPLETED.value);

        List<MaintenanceWorkOrder> maintenanceWorkOrders = maintenanceWorkOrderMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(maintenanceWorkOrders)) {
            log.error("此滤芯更换正在服务中,如无人更换请联系客服!");
//            throw new BadRequestException("有未完成的滤芯更换,如无人更换请联系客服!");
            return;
        }


        MaintenanceWorkOrder order = new MaintenanceWorkOrder(dto);
        if (StringUtil.isBlank(order.getId())) {
            String workCode = "WH" + IdGenerator.localSafeId();
            //TODO 字段冗余，调用处较多，本版本上线前不做处理
            order.setId(workCode);
            order.setWorkCode(workCode);
        }

        if (order.getSource() == null) {
            order.setSource(MaintenanceWorkOrderSourceEnum.AUTO_CREATE.value);//1-自动生成 2-总部添加
            order.setAuditType(2);//审核方式：1-自动审核 2-人工审核
        }

        order.setDelStatus(StatusEnum.NO.value());
        order.setWorkOrderCompleteStatus(StatusEnum.NO.value());
        order.setAcceptStatus(StatusEnum.YES.value());
        order.setBespeakStatus(StatusEnum.NO.value());
        order.setStartServerStatus(StatusEnum.NO.value());

        order.setWorkOrderCompleteStatus(StatusEnum.NO.value());
        order.setWorkorderUserAppraiseStatus(StatusEnum.NO.value());
        order.setWorkorderUserAppraiseStatus(StatusEnum.NO.value());
        order.setDelStatus(StatusEnum.NO.value());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());

        //设置经纬度
        if (Objects.nonNull(dto.getDeviceSncode())) {
            WaterDeviceDTO waterDeviceDTO = waterFeign.getBySnCode(dto.getDeviceSncode());
            if (Objects.nonNull(waterDeviceDTO)) {
                if (Objects.nonNull(waterDeviceDTO.getLatitude())) {
                    order.setLatitude(Double.parseDouble(waterDeviceDTO.getLatitude()));
                }
                if (Objects.nonNull(waterDeviceDTO.getLongitude())) {
                    order.setLongitude(Double.parseDouble(waterDeviceDTO.getLongitude()));
                }
            }
        }

        try {
            this.maintenanceWorkOrderMapper.insert(order);
            //更新日志
            MaintenanceWorkOrderOperateLogDTO operateLog = new MaintenanceWorkOrderOperateLogDTO();
            operateLog.setMaintenanceWorkOrderId(order.getId());
            operateLog.setOperateDescription("新建维护工单！");
            operateLog.setCreateTime(new Date());
            if (MaintenanceWorkOrderSourceEnum.SYSTEM_INCOME.value == order.getSource()) {//1-自动生成没有创建用户，设备提交
                operateLog.setCreator(userCache.getCurrentAdminRealName());
            }
            maintenanceWorkOrderOperateLogService.save(operateLog);
        } catch (Exception e) {
            log.error("维护工单创建失败" + e.getMessage());
            throw new YimaoException("维护工单创建失败:", e);
        }
    }

    @Override
    public int getWorkOrderMaintenanceCount(Integer engineerId, Integer state) {
//        Example example = new Example(MaintenanceWorkOrder.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("engineerId", engineerId);
//        criteria.andEqualTo("state", state);
//        return maintenanceWorkOrderMapper.selectCountByExample(example);
        return maintenanceWorkOrderMapper.getMaintenanceWorkOrderCount(engineerId, state);
    }

    /**
     * 检查是否存在
     *
     * @param id
     * @return
     */
    @Override
    public Boolean exists(String id) {
        return StringUtil.isBlank(id) ? false : this.maintenanceWorkOrderMapper.existsWithPrimaryKey(id);
    }

    @Override
    public List<MaintenanceWorkOrderDTO> getNotCompleteWorkOrderMaintenance(String deviceSncode, Integer engineerId, Integer source) {
        if (StringUtil.isBlank(deviceSncode)) {
            throw new BadRequestException("SN参数不得为空!");
        }

        Example example = new Example(MaintenanceWorkOrder.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deviceSncode", deviceSncode);

      /*  if (StringUtil.isNotBlank(materiels)) {
            materiels = materiels.replaceAll("棉", "");
            String[] materielArr = materiels.split(",");
            List<String> materielList = Arrays.asList(materielArr);
            criteria.andIn("materielDetailName", materielList);
        }*/

        if (engineerId != null) {
            criteria.andEqualTo("engineerId", engineerId);
        }
        if (source != null) {
            //来源：1-自动生成 2-总部添加，默认自动生成
            criteria.andEqualTo("source", source);
        }
        criteria.andEqualTo("workOrderCompleteStatus", StatusEnum.NO.value());

        List<MaintenanceWorkOrder> list = this.maintenanceWorkOrderMapper.selectByExample(example);
        MaintenanceWorkOrderDTO dto;
        List<MaintenanceWorkOrderDTO> listDto = new ArrayList<>();
        //转换
        for (MaintenanceWorkOrder waterDeviceWorkOrderMaintenance : list) {
            dto = new MaintenanceWorkOrderDTO();
            waterDeviceWorkOrderMaintenance.convert(dto);
            listDto.add(dto);
        }

        return listDto;
    }

    @Override
    public void updateWorkOrderMaintenance(MaintenanceWorkOrderDTO dto) {
        if (dto == null) {
            throw new BadRequestException("操作失败，更新对象不能为空。");
        }
        String workCode = dto.getWorkCode();
        if (StringUtil.isBlank(workCode)) {
            throw new BadRequestException("操作失败，workCode不能为空。");
        }

        MaintenanceWorkOrder update = new MaintenanceWorkOrder(dto);
        //update.setUpdateUser(userCache.getCurrentAdminRealName());
        update.setUpdateTime(new Date());

        Example example = new Example(MaintenanceWorkOrder.class);
        example.createCriteria().andEqualTo("workCode", dto.getWorkCode());
        int result = maintenanceWorkOrderMapper.updateByExampleSelective(update, example);
        if (result < 1) {
            throw new BadRequestException("维护工单更新失败！");
        }
    }

    @Override
    public void deleteMaintenanceWorkOrderById(String id) {
        if (StringUtil.isBlank(id)) {
            throw new BadRequestException("id不能为空。");
        }
        MaintenanceWorkOrder order = new MaintenanceWorkOrder();
        order.setId(id);
        order.setUpdateTime(new Date());
        order.setDelStatus(StatusEnum.YES.value());
        order.setDeleteTime(new Date());

        Integer result = maintenanceWorkOrderMapper.updateByPrimaryKeySelective(order);
        if (result < 1) {
            throw new BadRequestException("维护工单更新失败！");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void editworkOrderMaintenanceBySystem(String id, String materielDetailIds, String materielDetailNames) {
        if (StringUtil.isBlank(id)) {
            throw new BadRequestException("工单id不能为空!");
        }
        if (StringUtil.isBlank(materielDetailIds)) {
            throw new BadRequestException("请选择滤芯类型!");
        }
        if (StringUtil.isBlank(materielDetailNames)) {
            throw new BadRequestException("请选择滤芯类型!");
        }

        MaintenanceWorkOrder order = maintenanceWorkOrderMapper.selectByPrimaryKey(id);
        if (order == null) {
            throw new YimaoException("工单不存在");
        }
        String[] oldMaterielNames = order.getMaterielDetailName().split(",");
        String[] newMaterielNames = materielDetailNames.split(",");
        Set<String> oldSet = new HashSet(Arrays.asList(oldMaterielNames));
        int oldSize = oldSet.size();
        oldSet.addAll(Arrays.asList(newMaterielNames));  // a补充到b
        boolean containAll = oldSet.size() == oldSize;  // b吃了a之后还是原来的set大小
        //如果有变动则修改
        if (!containAll || newMaterielNames.length != oldMaterielNames.length) {
            String msg = "编辑维护工单，[滤芯类型]从[" + order.getMaterielDetailName() + "]改为了[" + materielDetailNames + "]";
            MaintenanceWorkOrderDTO dto = new MaintenanceWorkOrderDTO();
            dto.setId(id);
            dto.setMaterielDetailIds(materielDetailIds);
            dto.setMaterielDetailName(materielDetailNames);

            this.updateWorkOrderMaintenance(dto);
            MaintenanceWorkOrderOperateLogDTO operateLog = new MaintenanceWorkOrderOperateLogDTO();
            operateLog.setMaintenanceWorkOrderId(id);
            operateLog.setOperateDescription(msg);
            operateLog.setCreateTime(new Date());
            operateLog.setCreator(userCache.getCurrentAdminRealName());
            maintenanceWorkOrderOperateLogService.save(operateLog);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void auditMaintenanceWorkOrder(String id, Integer[] recordIds, Integer effective) {
        if (StringUtil.isBlank(id)) {
            throw new BadRequestException("id不能为空!");
        }

        if (recordIds != null) {
            //批量生效
            waterFeign.forbiddenChangeWaterDeviceFilterChangeRecord(recordIds, effective);
        } else {
            //客户可能不会提交记录信息，但是后台审核需要通过,并添加信息
            MaintenanceWorkOrder order = maintenanceWorkOrderMapper.selectByPrimaryKey(id);
            if (Objects.isNull(order)) {
                throw new YimaoException("维修工单id错误！");
            }
            WaterDeviceDTO waterDevice = waterFeign.getBySnCode(order.getDeviceSncode());
            if (Objects.isNull(waterDevice)) {
                throw new YimaoException("设备sn有误，关联设备不存在！");
            }
            WaterDeviceFilterChangeRecordDTO recordDto = new WaterDeviceFilterChangeRecordDTO();
            recordDto.setSn(order.getDeviceSncode());
            recordDto.setFilterName(order.getMaterielDetailName());
            recordDto.setProvince(order.getAddrProvince());
            recordDto.setCity(order.getAddrCity());
            recordDto.setRegion(order.getAddrRegion());
            recordDto.setAddress(order.getAddress());//详细地址
            recordDto.setMaintenanceWorkOrderId(id);
            recordDto.setCreateTime(new Date());
            recordDto.setSource(3);//自动生成
            recordDto.setEffective(1);//此条滤芯更换记录的“是否有效”设为“是”
            recordDto.setActivatingTime(waterDevice.getCreateTime());
            // 查询用户组设备数据
            waterDevice.setId(waterDevice.getId());
            if (recordDto.getFilterName().contains(BaideConsumableTypeIdEnum.FILTER_CTO.name)) {
                waterDevice.setLastCtoChangeTime(new Date());
            }
            if (recordDto.getFilterName().contains(BaideConsumableTypeIdEnum.FILTER_UDF.name)) {
                waterDevice.setLastUdfChangeTime(new Date());
            }
            if (recordDto.getFilterName().contains(BaideConsumableTypeIdEnum.FILTER_T33.name)) {
                waterDevice.setLastT33ChangeTime(new Date());
            }
            if (recordDto.getFilterName().contains(BaideConsumableTypeIdEnum.FILTER_PP.name)) {
                waterDevice.setLastPpChangeTime(new Date());
            }
            waterFeign.updateDevice(waterDevice);

            waterFeign.createWaterDeviceFilterChangeRecord(recordDto);
        }

        MaintenanceWorkOrder order = new MaintenanceWorkOrder();
        order.setId(id);
        order.setUpdateTime(new Date());
        order.setWorkOrderCompleteStatus(StatusEnum.YES.value());
        order.setWorkOrderCompleteTime(new Date());
        order.setAuditType(2);
        order.setState(WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.state);
        order.setStateText(WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.stateText);
        maintenanceWorkOrderMapper.updateByPrimaryKeySelective(order);
        //更新日志
        MaintenanceWorkOrderOperateLogDTO operateLog = new MaintenanceWorkOrderOperateLogDTO();
        operateLog.setMaintenanceWorkOrderId(id);
        operateLog.setOperateDescription("审核通过");
        operateLog.setCreateTime(new Date());
        operateLog.setCreator(userCache.getCurrentAdminRealName());
        //改变设备更新时间

        maintenanceWorkOrderOperateLogService.save(operateLog);
    }

    @Override
    public List<MaintenanceDTO> listMaintenanceWorkOrderForClient(Integer pageNum, Integer pageSize, Integer state, String distributorId, Integer engineerId, String search, Double longitude, Double latitude) {
        //获取这个安装工所有的维护工单
        List<MaintenanceDTO> maintenanceDTOList = maintenanceWorkOrderMapper.listMaintenanceWorkOrderForClient(state, engineerId, search);
        List<MaintenanceDTO> otherMaintenanceDTOList = new ArrayList<>();
        if (CollectionUtil.isEmpty(maintenanceDTOList)) {
            return null;
        }
        if (Objects.nonNull(longitude) && Objects.nonNull(latitude)) {
            ListIterator<MaintenanceDTO> listIterator = maintenanceDTOList.listIterator();
            while (listIterator.hasNext()) {
                MaintenanceDTO maintenanceDTO = listIterator.next();
                Double slng = maintenanceDTO.getLongitude();
                Double slat = maintenanceDTO.getLatitude();
                //数据库中经纬度为空
                if (slng == null || slat == null) {
                    otherMaintenanceDTOList.add(maintenanceDTO);
                    listIterator.remove();
                } else {
                    //获取距离
//                    double distance = getDistance(longitude, latitude, slng, slat);
                    double distance = locatorUtil.getDistance(longitude, latitude, slng, slat);
                    maintenanceDTO.setDistance(distance);
                }
            }
            if (CollectionUtil.isEmpty(maintenanceDTOList)) {
                return null;
            }
            //排序
            Collections.sort(maintenanceDTOList, (o1, o2) -> o1.getDistance() >= o2.getDistance() ? 1 : -1);
            maintenanceDTOList.addAll(otherMaintenanceDTOList);
        }
        //分页
        maintenanceDTOList = maintenanceDTOList.subList((pageNum - 1) * pageNum, maintenanceDTOList.size() > pageNum * pageSize ? pageNum * pageSize : maintenanceDTOList.size());
        if (CollectionUtil.isNotEmpty(maintenanceDTOList)) {
            for (MaintenanceDTO dto : maintenanceDTOList) {
                //查询需更换的滤芯记录
                dto.setFilterChangeMap(maintenanceWorkOrderMapper.maintenanceFilterChangeList(dto.getDeviceSncode(), Objects.isNull(dto.getHandselFlag()) ? null : dto.getHandselFlag(), state, 1, engineerId));
            }
        }
        return maintenanceDTOList;
    }


    @Override
    public PageVO<MaintenanceWorkOrderDTO> listMaintenanceWorkOrder(Integer pageNum, Integer pageSize, MaintenanceWorkOrderQueryDTO queryDTO) {
        Example example = new Example(MaintenanceWorkOrder.class);
        setQueryParam(example, queryDTO);
        example.orderBy("createTime").desc();

        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<MaintenanceWorkOrder> pageResult = (Page<MaintenanceWorkOrder>) this.maintenanceWorkOrderMapper.selectByExample(example);

        return new PageVO<>(pageNum, pageResult, MaintenanceWorkOrder.class, MaintenanceWorkOrderDTO.class);
    }

    @Override
    public void updateLasteFinishTime(Integer hour) {
        maintenanceWorkOrderMapper.updateLasteFinishTime(hour);
    }

    @Override
    public List<RenewDTO> getMaintenanceWorkOrderList(String completeTime, Integer engineerId, Integer timeType) {
        return maintenanceWorkOrderMapper.getMaintenanceWorkOrderList(completeTime, engineerId, timeType);
    }

    public void setQueryParam(Example example, MaintenanceWorkOrderQueryDTO queryDTO) {
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotBlank(queryDTO.getId())) {
            criteria.andLike("id", "%" + queryDTO.getId() + "%");
        }
        if (StringUtil.isNotBlank(queryDTO.getWorkOrderCompleteStatus())) {
            criteria.andEqualTo("workOrderCompleteStatus", queryDTO.getWorkOrderCompleteStatus());
        }
        if (StringUtil.isNotBlank(queryDTO.getProvince())) {
            criteria.andLike("addrProvince", "%" + queryDTO.getProvince() + "%");
        }
        if (StringUtil.isNotBlank(queryDTO.getCity())) {
            criteria.andLike("addrCity", "%" + queryDTO.getCity() + "%");
        }
        if (StringUtil.isNotBlank(queryDTO.getRegion())) {
            criteria.andLike("addrRegion", "%" + queryDTO.getRegion() + "%");
        }
        if (StringUtil.isNotBlank(queryDTO.getConsumerName())) {
            criteria.andLike("consumerName", "%" + queryDTO.getConsumerName() + "%");
        }
        if (StringUtil.isNotBlank(queryDTO.getConsumerPhone())) {
            criteria.andLike("consumerPhone", "%" + queryDTO.getConsumerPhone() + "%");
        }
        if (StringUtil.isNotBlank(queryDTO.getDeviceSncode())) {
            criteria.andLike("deviceSncode", "%" + queryDTO.getDeviceSncode() + "%");
        }
        if (queryDTO.getSource() != null) {
            criteria.andEqualTo("source", queryDTO.getSource());
        }
        if (queryDTO.getCreateStartTime() != null) {
            criteria.andGreaterThanOrEqualTo("createTime", queryDTO.getCreateStartTime());
        }
        if (queryDTO.getCreateEndTime() != null) {
            criteria.andLessThanOrEqualTo("createTime", queryDTO.getCreateEndTime());
        }
        if (queryDTO.getFinshStartTime() != null) {
            criteria.andGreaterThanOrEqualTo("workOrderCompleteTime", queryDTO.getFinshStartTime());
        }
        if (queryDTO.getFinshEndTime() != null) {
            criteria.andLessThanOrEqualTo("workOrderCompleteTime", queryDTO.getFinshEndTime());
        }

        criteria.andEqualTo("delStatus", StatusEnum.NO.value());
    }


    @Override
    public Map<String, Integer> getMaintenanceWorkOrderCount(Integer engineerId) {
        Map<String, Integer> map = new HashMap<>();
        map.put("toBeMaintainedCount", maintenanceWorkOrderMapper.getMaintenanceWorkOrderCount(engineerId, WorkOrderNewStatusEnum.WAIT_INSTALL.value));//待维护
        map.put("processingMaintainedCount", maintenanceWorkOrderMapper.getMaintenanceWorkOrderCount(engineerId, WorkOrderNewStatusEnum.PROCESSING.value));//处理中
        map.put("pendingMaintainedCount", maintenanceWorkOrderMapper.getMaintenanceWorkOrderCount(engineerId, WorkOrderNewStatusEnum.PENDING.value));//挂单
        map.put("completeMaintainedCount", maintenanceWorkOrderMapper.getMaintenanceWorkOrderCount(engineerId, WorkOrderNewStatusEnum.COMPLETED.value));//已完成
        return map;
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void hangMaintenanceWorkOrder(MaintenanceDTO maintenanceDTO) {
        if (maintenanceDTO == null || maintenanceDTO.getWorkCode() == null) {
            throw new BadRequestException("操作失败，更新对象不能为空。");
        }
        if (maintenanceDTO.getState() == null) {
            throw new BadRequestException("操作失败，状态不能为空。");
        }

        try {
            String workCodeList = maintenanceDTO.getWorkCode();
            if (StringUtil.isNotBlank(workCodeList)) {
                String[] workCodeStr = workCodeList.split(",");
                Date now = new Date();
                MaintenanceWorkOrder update = new MaintenanceWorkOrder();
                //1-待维护 2-处理中 3-挂单 4-已完成
                if (maintenanceDTO.getState() == WorkOrderNewStatusEnum.PROCESSING.value) {
                    update.setState(WorkOrderNewStatusEnum.PROCESSING.value);
                    update.setStateText(WorkOrderNewStatusEnum.PROCESSING.name);
                    update.setStartServerTime(now);
                } else if (maintenanceDTO.getState() == WorkOrderNewStatusEnum.PENDING.value) {
                    update.setAddrProvince(maintenanceDTO.getAddrProvince());
                    update.setAddrCity(maintenanceDTO.getAddrCity());
                    update.setAddrRegion(maintenanceDTO.getAddrRegion());
                    update.setAddress(maintenanceDTO.getAddress());
                    update.setState(WorkOrderNewStatusEnum.PENDING.value);
                    update.setStateText(WorkOrderNewStatusEnum.PENDING.name);
                    update.setHangStartTime(maintenanceDTO.getHangStartTime());
                    update.setHangEndTime(maintenanceDTO.getHangEndTime());
                    //挂单原因
                    update.setHangReason(maintenanceDTO.getHangReason());
                    //挂单备注
                    update.setHangRemark(maintenanceDTO.getHangRemark());
                    update.setHangTime(now);
                } else if (maintenanceDTO.getState() == WorkOrderNewStatusEnum.COMPLETED.value) {
                    update.setState(WorkOrderNewStatusEnum.COMPLETED.value);
                    update.setStateText(WorkOrderNewStatusEnum.COMPLETED.name);
                    update.setWorkOrderCompleteTime(now);
                    update.setWorkOrderCompleteStatus(StatusEnum.YES.value());

                } else {
                    log.error("操作失败，状态信息有误,状态为：" + maintenanceDTO.getState());
                    throw new BadRequestException("操作失败，状态信息有误。");
                }

                for (String workCode : workCodeStr) {
                    update.setId(workCode);
                    update.setUpdateTime(now);
                    int result = maintenanceWorkOrderMapper.updateByPrimaryKeySelective(update);
                    if (result < 1) {
                        throw new BadRequestException("维护工单更新失败！");
                    }

                    if (maintenanceDTO.getState() == WorkOrderNewStatusEnum.COMPLETED.value) {
                        //生成耗材记录
                        MaintenanceWorkOrder maintenanceWorkOrder = maintenanceWorkOrderMapper.selectByPrimaryKey(workCode);
                        if (maintenanceWorkOrder != null) {
                            StationMaterialStockDTO materialStockDTO = new StationMaterialStockDTO();
                            materialStockDTO.setEngineerId(maintenanceWorkOrder.getEngineerId());
                            if (Objects.nonNull(maintenanceWorkOrder.getMaterielDetailName())) {
                                materialStockDTO.setFilterNames(StringUtil.spiltListByStr(maintenanceWorkOrder.getMaterielDetailName()));
                            }
                            systemFeign.reduceStationMaterialStock(materialStockDTO);

                            // 更新设备上，最后滤芯更换时间
                            WaterDeviceDTO waterDevice = waterFeign.getBySnCode(maintenanceWorkOrder.getDeviceSncode());
                            if (Objects.isNull(waterDevice)) {
                                throw new YimaoException("设备sn有误，关联设备不存在！");
                            }
                            waterDevice.setId(waterDevice.getId());
                            if (maintenanceWorkOrder.getMaterielDetailName().contains(BaideConsumableTypeIdEnum.FILTER_CTO.name)) {
                                waterDevice.setLastCtoChangeTime(now);
                            }
                            if (maintenanceWorkOrder.getMaterielDetailName().contains(BaideConsumableTypeIdEnum.FILTER_UDF.name)) {
                                waterDevice.setLastUdfChangeTime(now);
                            }
                            if (maintenanceWorkOrder.getMaterielDetailName().contains(BaideConsumableTypeIdEnum.FILTER_T33.name)) {
                                waterDevice.setLastT33ChangeTime(now);
                            }
                            if (maintenanceWorkOrder.getMaterielDetailName().contains(BaideConsumableTypeIdEnum.FILTER_PP.name)) {
                                waterDevice.setLastPpChangeTime(now);
                            }
                            waterFeign.updateDevice(waterDevice);
                        }
                    }

                    //更新日志
                    MaintenanceWorkOrderOperateLogDTO operateLog = new MaintenanceWorkOrderOperateLogDTO();
                    operateLog.setMaintenanceWorkOrderId(workCode);
                    operateLog.setOperateDescription(update.getStateText());
                    operateLog.setCreateTime(new Date());
                    maintenanceWorkOrderOperateLogService.save(operateLog);
                }
            } else {
                throw new BadRequestException("操作失败，维护工单号不能为空。");
            }
        } catch (BadRequestException e) {
            e.printStackTrace();
            throw new BadRequestException("操作失败，修改维护工单号失败。");
        }
    }

    @Override
    public Page<MaintenanceDTO> maintenanceWorkOrderCompleteList(Integer engineerId, Integer sortType, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return maintenanceWorkOrderMapper.maintenanceWorkOrderCompleteList(engineerId, sortType);
    }

    @Override
    public List<MaintenanceDTO> maintenanceWorkOrderRecordDetail(Integer engineerId, String deviceSncode) {
        //1.根据sn码获取基本信息和维护标识
        List<MaintenanceDTO> maintenanceDTOList = maintenanceWorkOrderMapper.maintenanceWorkOrderRecordDetail(engineerId, deviceSncode);
        if (CollectionUtil.isNotEmpty(maintenanceDTOList)) {
            //2.获取维护工单列表
            for (MaintenanceDTO dto : maintenanceDTOList) {
                dto.setFilterChangeMap(maintenanceWorkOrderMapper.maintenanceFilterChangeRecordList(deviceSncode, engineerId, dto.getHandselFlag()));
            }
        }
        return maintenanceDTOList;
    }

    /**
     * 站务系统维护工单列表查询
     */
    public PageVO<MaintenanceWorkOrderDTO> listMaintenanceOrderToStation(Integer pageNum, Integer pageSize,
                                                                         StationMaintenanceOrderQuery query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<MaintenanceWorkOrderDTO> pageResult = maintenanceWorkOrderMapper.listMaintenanceOrderToStation(query);
        return new PageVO(pageNum, pageResult);

	}

    @Override
    public Integer getMaintenanceModelWorkOrderTotalCount(Integer engineerId) {
        return maintenanceWorkOrderMapper.getMaintenanceModelWorkOrderTotalCount(engineerId);
    }
}
