package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.enums.BaideConsumableTypeIdEnum;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderStateEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderOperateLogDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordExportDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.out.MaintenanceWorkOrderVO;
import com.yimao.cloud.water.enums.DeviceTypeEnum;
import com.yimao.cloud.water.feign.OrderFeign;
import com.yimao.cloud.water.mapper.WaterDeviceFilterChangeRecordMapper;
import com.yimao.cloud.water.po.WaterDevice;
import com.yimao.cloud.water.po.WaterDeviceFilterChangeRecord;
import com.yimao.cloud.water.service.WaterDeviceFilterChangeRecordService;
import com.yimao.cloud.water.service.WaterDeviceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 描述：水机滤芯更换记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/26
 */
@Service
public class WaterDeviceFilterChangeRecordServiceImpl implements WaterDeviceFilterChangeRecordService {
    @Resource
    private WaterDeviceFilterChangeRecordMapper waterDeviceFilterChangeRecordMapper;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private WaterDeviceService waterDeviceService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void save(WaterDeviceFilterChangeRecordDTO dto) {
        if (dto == null) {
            throw new BadRequestException("参数不能为空！");
        }

        waterDeviceFilterChangeRecordMapper.insertSelective(new WaterDeviceFilterChangeRecord(dto));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void createByClient(String filterNames, Integer deviceGroup, Integer source, String maintenanceWorkOrderId) {
        if (source == null) {
            throw new BadRequestException("请选择来源！");
        }
        if (StringUtil.isBlank(filterNames)) {
            throw new BadRequestException("请选择需要提交的滤芯！");
        }
        if (deviceGroup == null) {
            throw new BadRequestException("请选择设备组！");
        }
        /*if (deviceGroup == 2) {
            throw new BadRequestException("本版本目前只支持用户设备组自助更换滤芯！");
        }*/
        if (StringUtil.isBlank(maintenanceWorkOrderId)) {
            throw new BadRequestException("维护工单id不能为空！");
        }
        MaintenanceWorkOrderDTO order = orderFeign.getWorkOrderMaintenanceById(maintenanceWorkOrderId);
        if (order == null) {
            throw new BadRequestException("还没有该设备维修工单，请联系客服！");
        }
        List<WaterDeviceFilterChangeRecord> recordList = getWaterDeviceFilterChangeRecordByOrderId(maintenanceWorkOrderId);
        if (recordList != null && recordList.size() > 0) {
            throw new BadRequestException("该维护工单已经提交，请耐心等待客服处理！");
        }

        //根据sn查询设备信息
        if (!Objects.equals(deviceGroup, DeviceTypeEnum.USER.value)) {
            throw new BadRequestException("设备组选择有误！");
        }
        // 查询用户组设备数据
        WaterDevice waterDevice = waterDeviceService.getBySnCode(order.getDeviceSncode());
        if (Objects.isNull(waterDevice)) {
            throw new BadRequestException("该水机设备不存在！");
        }
        waterDevice.setId(waterDevice.getId());
        if (filterNames.contains(BaideConsumableTypeIdEnum.FILTER_CTO.name)) {
            waterDevice.setLastCtoChangeTime(new Date());
        }
        if (filterNames.contains(BaideConsumableTypeIdEnum.FILTER_UDF.name)) {
            waterDevice.setLastUdfChangeTime(new Date());
        }
        if (filterNames.contains(BaideConsumableTypeIdEnum.FILTER_T33.name)) {
            waterDevice.setLastT33ChangeTime(new Date());
        }
        if (filterNames.contains(BaideConsumableTypeIdEnum.FILTER_PP.name)) {
            waterDevice.setLastPpChangeTime(new Date());
        }
        waterDeviceService.update(waterDevice);

        MaintenanceWorkOrderDTO orderDto = new MaintenanceWorkOrderDTO();
        orderDto.setId(maintenanceWorkOrderId);
        orderDto.setWorkCode(order.getWorkCode());
        orderDto.setAuditType(1);//自动审核
        orderDto.setState(WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.state);
        orderDto.setStateText(WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.stateText);
        orderDto.setWorkOrderCompleteStatus(StatusEnum.YES.value());//滤芯维护工单自动从待审核变成已完成
        orderDto.setWorkOrderCompleteTime(new Date());
        orderDto.setUpdateTime(new Date());
        orderFeign.updateWorkOrderMaintenance(orderDto);

        //更新日志
        MaintenanceWorkOrderOperateLogDTO operateLog = new MaintenanceWorkOrderOperateLogDTO();
        operateLog.setMaintenanceWorkOrderId(maintenanceWorkOrderId);
        operateLog.setOperateDescription("用户提交滤芯");
        orderFeign.createMaintenanceWorkOrderOperateLog(operateLog);

        WaterDeviceFilterChangeRecordDTO recordDto = new WaterDeviceFilterChangeRecordDTO();
        recordDto.setEffective(1);//此条滤芯更换记录的“是否有效”设为“是”
        recordDto.setActivatingTime(waterDevice.getCreateTime());
        recordDto.setSn(order.getDeviceSncode());
        recordDto.setFilterName(filterNames);
        recordDto.setProvince(order.getAddrProvince());
        recordDto.setCity(order.getAddrCity());
        recordDto.setRegion(order.getAddrRegion());
        recordDto.setAddress(order.getAddressDetail());
        recordDto.setMaintenanceWorkOrderId(maintenanceWorkOrderId);
        recordDto.setSource(source);
        recordDto.setCreateTime(new Date());//创建时间即是更换时间

        waterDeviceFilterChangeRecordMapper.insertSelective(new WaterDeviceFilterChangeRecord(recordDto));
    }

    public List<WaterDeviceFilterChangeRecord> getWaterDeviceFilterChangeRecordByOrderId(String maintenanceOrderId) {
        if (StringUtil.isBlank(maintenanceOrderId)) {
            throw new BadRequestException("维护工单id不能为空！");
        }

        Example example = new Example(WaterDeviceFilterChangeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("maintenanceWorkOrderId", maintenanceOrderId);

        List<WaterDeviceFilterChangeRecord> list = waterDeviceFilterChangeRecordMapper.selectByExample(example);

        return list;
    }

    @Override
    public List<WaterDeviceFilterChangeRecordDTO> getWaterDeviceFilterChangeRecordBySN(String deviceSncode, Date createTime, Integer source) {
        if (StringUtil.isBlank(deviceSncode)) {
            throw new BadRequestException("参数SN不能为空！");
        }
        if (createTime == null) {
            throw new BadRequestException("创建日期不能为空！");
        }

        Example example = new Example(WaterDeviceFilterChangeRecord.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sn", deviceSncode);
       //安装工提交滤芯在工单生成之后（所以要大于工单创建时间）
        criteria.andGreaterThan("createTime", createTime);
        //criteria.andEqualTo("effective", 1);

        if (source != null) {//来源：1-安装工提交 2-客户提交 3-自动生成
            criteria.andEqualTo("source", source);
        }

        List<WaterDeviceFilterChangeRecord> list = waterDeviceFilterChangeRecordMapper.selectByExample(example);
        WaterDeviceFilterChangeRecordDTO dto;
        List<WaterDeviceFilterChangeRecordDTO> dtoList = new ArrayList<>();
        for (WaterDeviceFilterChangeRecord record : list) {
            dto = new WaterDeviceFilterChangeRecordDTO();
            record.convert(dto);
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public void forbiddenWaterDeviceFilterChangeRecord(Integer[] ids, Integer effective) {
        if (ids == null || ids.length == 0) {
            throw new BadRequestException("请选择客户端提交的维护记录！");
        }
        WaterDeviceFilterChangeRecord mwor = new WaterDeviceFilterChangeRecord();
        mwor.setEffective(effective);
        List<Integer> idList = Arrays.asList(ids);
        if (idList.size() == 1) {
            mwor.setId(idList.get(0));
            //mwor.setCreateTime(new Date());
            waterDeviceFilterChangeRecordMapper.updateByPrimaryKeySelective(mwor);
        } else {
            Example example = new Example(WaterDeviceFilterChangeRecord.class);
            example.createCriteria().andIn("id", idList);
            waterDeviceFilterChangeRecordMapper.updateByExampleSelective(mwor, example);
        }
    }

    @Override
    public void batchSave(List<WaterDeviceFilterChangeRecordDTO> list) {
        waterDeviceFilterChangeRecordMapper.batchInsert(list);
    }

    @Override
    public WaterDeviceFilterChangeRecordDTO getFilterChangeRecordById(Integer id) {
        if (id == null) {
            throw new BadRequestException("参数id不能为空！");
        }

        WaterDeviceFilterChangeRecordDTO record = waterDeviceFilterChangeRecordMapper.getFilterChangeRecordById(id);
        return record;
    }

    @Override
    public List<WaterDeviceFilterChangeRecordDTO> getFilterChangeRecordListByIds(Integer[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BadRequestException("维护记录id不能为空！");
        }
        Example example = new Example(WaterDeviceFilterChangeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        List<WaterDeviceFilterChangeRecord> list = waterDeviceFilterChangeRecordMapper.selectByExample(example);

        List<WaterDeviceFilterChangeRecordDTO> dtoList = new ArrayList<>();
        WaterDeviceFilterChangeRecordDTO dto;
        for (WaterDeviceFilterChangeRecord record : list) {
            dto = new WaterDeviceFilterChangeRecordDTO();
            record.convert(dto);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public PageVO<WaterDeviceFilterChangeRecordDTO> pageList(WaterDeviceFilterChangeRecordQueryDTO queryDTO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDeviceFilterChangeRecordDTO> page = waterDeviceFilterChangeRecordMapper.findPage(queryDTO);
        return new PageVO(pageNum, page);

    }

}
