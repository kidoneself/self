package com.yimao.cloud.order.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderStateEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.mapper.RepairWorkOrderMapper;
import com.yimao.cloud.order.po.RepairWorkOrder;
import com.yimao.cloud.order.service.RepairWorkOrderService;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.dto.order.RepairWorkOrderDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/***
 * 功能描述:维修工单服务
 *
 * @auther: liu yi
 * @date: 2019/4/10 14:32
 */
@Service
@Slf4j
public class RepairWorkOrderServiceImpl implements RepairWorkOrderService {
    @Resource
    private RepairWorkOrderMapper workOrderRepairMapper;

    @Override
    public RepairWorkOrderDTO getRepairWorkOrderById(Integer id) {
        if (id == null) {
            throw new BadRequestException("id传参不能为空！");
        }
        RepairWorkOrder workOrderRepair = this.workOrderRepairMapper.selectByPrimaryKey(id);
        if (workOrderRepair == null) {
            return null;
        }
        RepairWorkOrderDTO dto = new RepairWorkOrderDTO();
        workOrderRepair.convert(dto);

        return dto;
    }

    @Override
    public RepairWorkOrderDTO getRepairWorkOrderByWorkCode(String workCode) {
        if (StringUtils.isBlank(workCode)) {
            throw new BadRequestException("workCode传参不能为空！");
        }

        Example example = new Example(RepairWorkOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("workCode", workCode);
        RepairWorkOrder workOrderRepair = this.workOrderRepairMapper.selectOneByExample(example);
        if (workOrderRepair == null) {
            return null;
        }
        RepairWorkOrderDTO dto = new RepairWorkOrderDTO();
        workOrderRepair.convert(dto);

        return dto;
    }

    /**
     * 检查是否存在
     *
     * @param workCode
     * @return
     */
    public Boolean exists(String workCode) {
        if (StringUtil.isBlank(workCode)) {
            throw new BadRequestException("workCode传参不能为空！");
        }
        RepairWorkOrderDTO dto = getRepairWorkOrderByWorkCode(workCode);
        return dto != null ? true : false;
    }

    @Override
    public void create(RepairWorkOrderDTO dto) {
        if (null == dto) {
            throw new BadRequestException("维修工单不能空！");
        }

        if (exists(dto.getWorkCode())) {
            throw new BadRequestException("维修工单已存在！维修工单号：" + dto.getWorkCode());
        }

        RepairWorkOrder order = new RepairWorkOrder(dto);
        order.setScanBatchCodeStatus(StatusEnum.NO.value());
        order.setApplyChangeDeviceStatus(StatusEnum.NO.value());
        order.setApplyChangeDeviceResultStatus(StatusEnum.NO.value());
        order.setChangeDeviceStatus(StatusEnum.NO.value());
        order.setFactFaultStatus(StatusEnum.NO.value());
        order.setAcceptStatus(StatusEnum.YES.value());
        order.setBespeakStatus(StatusEnum.YES.value());
        order.setStartServerStatus(StatusEnum.NO.value());
        order.setState(WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.state);
        order.setStateText(WorkOrderStateEnum.HANDLING.stateText);
        order.setWorkOrderCompleteStatus(StatusEnum.NO.value());
        order.setWorkorderUserAppraiseStatus(StatusEnum.NO.value());
        order.setDelStatus(StatusEnum.NO.value());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());

        try {
            int result = workOrderRepairMapper.insertSelective(order);
            if (result < 1) {
                throw new YimaoException("维修工单创建失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("维修工单创建失败！");
        }
    }

    @Override
    public void update(RepairWorkOrder workOrderRepair, Integer operationUserId) {
        if (workOrderRepair == null) {
            throw new BadRequestException("维修工单不能空！");
        }
        if (workOrderRepair.getId() == null) {
            throw new BadRequestException("维修工单id不能空！");
        }

        try {
            workOrderRepair.setUpdateTime(new Date());
            if (operationUserId != null) {
                workOrderRepair.setUpdateUser(operationUserId.toString());
            }

            Example example = new Example(RepairWorkOrder.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("id", workOrderRepair.getId());

            int result = this.workOrderRepairMapper.updateByExampleSelective(workOrderRepair, example);
            if (result <= 0) {
                throw new YimaoException("更新维修工单失败！");
            }
        } catch (Exception e) {
            throw new YimaoException("更新维修工单失败！");
        }
    }

    @Override
    public Integer getWorkOrderRepairEngineerCount(String engineerId, Integer state) {
        Example example = new Example(RepairWorkOrder.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("engineerId", engineerId);
        criteria.andEqualTo("state", state);
        criteria.andEqualTo("delStatus", StatusEnum.NO.value());
        return this.workOrderRepairMapper.selectCountByExample(example);
    }

    @Override
    public void userAppraise(String workCode, String appraiseContent) {
        if (StringUtil.isBlank(workCode)) {
            throw new BadRequestException("维修工单id参数不能空！");
        }

        Example example = new Example(RepairWorkOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("workCode", workCode);

        RepairWorkOrder wr = new RepairWorkOrder();

        wr.setUserAppriseTime(new Date());
        wr.setWorkorderUserAppraiseStatus(StatusEnum.YES.value());
        wr.setWorkorderUserAppraiseStatusText(appraiseContent);

        workOrderRepairMapper.updateByExampleSelective(wr, example);
    }

    @Override
    public void updateMasterRed(String workCode, String masterId) {
        if (StringUtil.isBlank(workCode)) {
            throw new BadRequestException("维修工单id参数不能空！");
        }

        Example example = new Example(RepairWorkOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("workCode", workCode);
        RepairWorkOrder waterDeviceWorkOrderRepair = workOrderRepairMapper.selectOneByExample(example);
        //waterDeviceWorkOrderRepair.setMasterRedId(masterId);
        //waterDeviceWorkOrderRepair.setHaveMasterRedStatus(StatusEnum.YES.value());

        this.update(waterDeviceWorkOrderRepair, null);
    }

    @Override
    public PageVO<RepairWorkOrderDTO> page(Integer pageNum, Integer pageSize, String isCompanyDistributor, String distributorId, Integer engineerId, Integer state, String orderStatus, String search) {
        Example example = new Example(RepairWorkOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if (state == null) {
            state = 1;
        }
        if (engineerId != null) {
            criteria.andEqualTo("engineerId", engineerId);
        }
        criteria.andEqualTo("state", state);
        criteria.andEqualTo("delStatus", StatusEnum.NO.value());
        this.setQueryParam(criteria, state, orderStatus);

        if (StringUtil.isNotBlank(search)) {
            Example.Criteria searchCriteria = example.createCriteria();
            searchCriteria.andLike("workCode", "%" + search + "%");
            searchCriteria.orLike("addrProvince", "%" + search + "%");
            searchCriteria.orLike("addrCity", "%" + search + "%");
            searchCriteria.orLike("addrRegion", "%" + search + "%");
            searchCriteria.orLike("address", "%" + search + "%");
            searchCriteria.orLike("deviceModelName", "%" + search + "%");
            searchCriteria.orLike("consumerName", "%" + search + "%");
            searchCriteria.orLike("consumerPhone", "%" + search + "%");
            searchCriteria.orLike("deviceBatchCode", "%" + search + "%");
            searchCriteria.orLike("deviceSncode", "%" + search + "%");
            searchCriteria.orLike("deviceSimcard", "%" + search + "%");
            searchCriteria.orLike("changeDeviceSncode", "%" + search + "%");
            searchCriteria.orLike("changeDeviceSimcard", "%" + search + "%");
            searchCriteria.orLike("changeDeviceBatchCode", "%" + search + "%");
            example.and(searchCriteria);
        }

        if (WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.state == state) {
            example.orderBy("workOrderCompleteTime").desc();
        } else {
            example.orderBy("countdownTime").asc();
        }
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<RepairWorkOrder> pageResult = (Page<RepairWorkOrder>) this.workOrderRepairMapper.selectByExample(example);

        return new PageVO<>(pageNum, pageResult, RepairWorkOrder.class, RepairWorkOrderDTO.class);
    }


    @Override
    public List<RenewDTO> statisticsRepairWorkOrder(String completeTime, Integer engineerId ,Integer timeType) {
        return workOrderRepairMapper.getRepairWorkOrderList(completeTime,engineerId,timeType);
    }

    private void setQueryParam(Example.Criteria criteria, Integer state, String orderStatus) {
        byte temp;
        if (WorkOrderStateEnum.WORKORDER_STATE_SERVING.state == state) {
            temp = -1;
            switch (orderStatus.hashCode()) {
                case 50:
                    if (orderStatus.equals("2")) {
                        temp = 0;
                    }
                    break;
                case 51:
                    if (orderStatus.equals("3")) {
                        temp = 1;
                    }
                    break;
                case 52:
                    if (orderStatus.equals("4")) {
                        temp = 2;
                    }
            }
            switch (temp) {
                case 0:
                    criteria.andEqualTo("applyChangeDeviceStatus", StatusEnum.YES.value());
                    criteria.andEqualTo("applyChangeDeviceResultStatus", StatusEnum.NO.value());
                    break;
                case 1:
                    criteria.andEqualTo("applyChangeDeviceStatus", StatusEnum.YES.value());
                    criteria.andEqualTo("applyChangeDeviceResultStatus", StatusEnum.YES.value());
                    criteria.andEqualTo("applyChangeDeviceResult", StatusEnum.NO.value());
                    break;
                case 2:
                    criteria.andEqualTo("applyChangeDeviceStatus", StatusEnum.YES.value());
                    criteria.andEqualTo("applyChangeDeviceResultStatus", StatusEnum.YES.value());
                    criteria.andEqualTo("applyChangeDeviceResult", StatusEnum.YES.value());
                    criteria.andEqualTo("changeDeviceStatus", StatusEnum.NO.value());
            }
        } else if (WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.state == state) {
            temp = -1;
            switch (orderStatus.hashCode()) {
                case 50:
                    if (orderStatus.equals("2")) {
                        temp = 0;
                    }
                    break;
                case 51:
                    if (orderStatus.equals("3")) {
                        temp = 1;
                    }
            }
            switch (temp) {
                case 0:
                    criteria.andEqualTo("changeDeviceStatus", StatusEnum.YES.value());
                    break;
                case 1:
                    criteria.andEqualTo("changeDeviceStatus", StatusEnum.NO.value());
            }
        }
    }
}
