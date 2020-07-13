package com.yimao.cloud.order.service.impl;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.order.mapper.MaintenanceWorkOrderOperateLogMapper;
import com.yimao.cloud.order.po.MaintenanceWorkOrderOperateLog;
import com.yimao.cloud.order.service.MaintenanceWorkOrderOperateLogService;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderOperateLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/***
 * 功能描述:维护工单记录
 *
 * @auther: liu yi
 * @date: 2019/5/15 11:04
 */
@Service
@Slf4j
public class MaintenanceWorkOrderOperateLogServiceImpl implements MaintenanceWorkOrderOperateLogService {
    @Resource
    private MaintenanceWorkOrderOperateLogMapper WorkOrderMaintenanceOperateLogMapper;

    @Override
    public void save(MaintenanceWorkOrderOperateLogDTO dto) {
        if (dto == null) {
            throw new BadRequestException("参数不能为空！");
        }

        //String creator = userCache.getCurrentAdminRealName();
        //dto.setCreateUser(creator);
        dto.setCreateTime(new Date());
        WorkOrderMaintenanceOperateLogMapper.insert(new MaintenanceWorkOrderOperateLog(dto));
    }

    @Override
    public MaintenanceWorkOrderOperateLogDTO getMaintenanceWorkOrderOperateLogById(Integer id) {
        if (id == null) {
            throw new BadRequestException("参数id不能为空！");
        }
        MaintenanceWorkOrderOperateLogDTO dto = new MaintenanceWorkOrderOperateLogDTO();
        MaintenanceWorkOrderOperateLog record = WorkOrderMaintenanceOperateLogMapper.selectByPrimaryKey(id);
        record.convert(dto);

        return dto;
    }

    @Override
    public List<MaintenanceWorkOrderOperateLogDTO> getListByMaintenanceWorkOrderId(String maintenanceWorkOrderId) {
        if(StringUtils.isBlank(maintenanceWorkOrderId)){
            throw new BadRequestException("维修工单id不能为空！");
        }
        Example example = new Example(MaintenanceWorkOrderOperateLog.class);
        example.orderBy("createTime");
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("maintenanceWorkOrderId", maintenanceWorkOrderId);

        MaintenanceWorkOrderOperateLogDTO dto = null;
        List<MaintenanceWorkOrderOperateLog> list = WorkOrderMaintenanceOperateLogMapper.selectByExample(example);
        List<MaintenanceWorkOrderOperateLogDTO> dtoList = new ArrayList<>();
        for (MaintenanceWorkOrderOperateLog workOrderMaintenanceOperateLog : list) {
            dto = new MaintenanceWorkOrderOperateLogDTO();
            workOrderMaintenanceOperateLog.convert(dto);
            dtoList.add(dto);
        }

        return dtoList;
    }
}
