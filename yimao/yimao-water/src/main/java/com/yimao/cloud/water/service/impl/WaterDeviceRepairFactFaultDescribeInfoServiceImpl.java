package com.yimao.cloud.water.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.water.WaterDeviceRepairFactFaultDescribeInfoDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.mapper.WaterDeviceRepairFactFaultDescribeInfoMapper;
import com.yimao.cloud.water.po.WaterDeviceRepairFactFaultDescribeInfo;
import com.yimao.cloud.water.service.WaterDeviceRepairFactFaultDescribeInfoService;
import com.yimao.cloud.water.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class WaterDeviceRepairFactFaultDescribeInfoServiceImpl implements WaterDeviceRepairFactFaultDescribeInfoService {
    @Resource
    private WaterDeviceRepairFactFaultDescribeInfoMapper waterDeviceRepairFactFaultDescribeInfoMapper;

    public WaterDeviceRepairFactFaultDescribeInfo getById(Integer id) {
        if(null == id){
            throw new BadRequestException("id不能为空!");
        }
        WaterDeviceRepairFactFaultDescribeInfo entity = this.waterDeviceRepairFactFaultDescribeInfoMapper.selectByPrimaryKey(id);
        return entity;
    }

    public void create(WaterDeviceRepairFactFaultDescribeInfoDTO dto) {
        dto.setDelStatus(StatusEnum.FALSE.value());
        dto.setIdStatus(StatusEnum.YES.value());
        this.waterDeviceRepairFactFaultDescribeInfoMapper.insert(new WaterDeviceRepairFactFaultDescribeInfo(dto));
    }

    public void delete(Integer id) {
        if (null ==id) {
            throw new BadRequestException("id不能为空!");
        }
        try {
            Example example = new Example(WaterDeviceRepairFactFaultDescribeInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("id", id);
            WaterDeviceRepairFactFaultDescribeInfo repairFactFaultDescribeInfo = waterDeviceRepairFactFaultDescribeInfoMapper.selectByPrimaryKey(id);
            repairFactFaultDescribeInfo.setDelStatus(StatusEnum.YES.value());
            repairFactFaultDescribeInfo.setIdStatus(StatusEnum.FALSE.value());
            repairFactFaultDescribeInfo.setDeleteTime(new Date());
            waterDeviceRepairFactFaultDescribeInfoMapper.updateByExampleSelective(repairFactFaultDescribeInfo, example);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteByWorkCode(String workCode) {
        if (StringUtil.isBlank(workCode)) {
            throw new BadRequestException("工单id不能为空!");
        }

        try {
            Example example = new Example(WaterDeviceRepairFactFaultDescribeInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("workCode", workCode);

            WaterDeviceRepairFactFaultDescribeInfo repairFactFaultDescribeInfo = new WaterDeviceRepairFactFaultDescribeInfo();
            repairFactFaultDescribeInfo.setDelStatus(StatusEnum.YES.value());
           /* repairFactFaultDescribeInfo.setIdStatus(StatusEnum.FALSE.value());*/
            repairFactFaultDescribeInfo.setDeleteTime(new Date());
            repairFactFaultDescribeInfo.setUpdateTime(new Date());
            repairFactFaultDescribeInfo.setIdStatus(StatusEnum.FALSE.value());

            waterDeviceRepairFactFaultDescribeInfoMapper.updateByExampleSelective(repairFactFaultDescribeInfo, example);
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("删除解决措施信息失败！");
        }
    }

    public boolean exists(Integer id) {
        return this.waterDeviceRepairFactFaultDescribeInfoMapper.existsWithPrimaryKey(id);
    }

    public PageVO<WaterDeviceRepairFactFaultDescribeInfoDTO> page(Integer pageNum, Integer pageSize, Integer id, String workCode) {
        Example example = new Example(WaterDeviceRepairFactFaultDescribeInfo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("id", id);
        criteria.andEqualTo("workCode", workCode);
        criteria.andEqualTo("delStatus", StatusEnum.FALSE.value());
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDeviceRepairFactFaultDescribeInfo> pageResult = (Page<WaterDeviceRepairFactFaultDescribeInfo>)this.waterDeviceRepairFactFaultDescribeInfoMapper.selectByExample(example);
        return new PageVO<>(pageNum, pageResult, WaterDeviceRepairFactFaultDescribeInfo.class, WaterDeviceRepairFactFaultDescribeInfoDTO.class);
    }

    public List<WaterDeviceRepairFactFaultDescribeInfoDTO> list(Integer id, String workCode) {
        Example example = new Example(WaterDeviceRepairFactFaultDescribeInfo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("id", id);
        criteria.andEqualTo("workCode", workCode);
        criteria.andEqualTo("delStatus", StatusEnum.FALSE.value());
        List<WaterDeviceRepairFactFaultDescribeInfo> result = this.waterDeviceRepairFactFaultDescribeInfoMapper.selectByExample(example);
        WaterDeviceRepairFactFaultDescribeInfoDTO dto;
        List<WaterDeviceRepairFactFaultDescribeInfoDTO> dtoList=new ArrayList<>();

        for(WaterDeviceRepairFactFaultDescribeInfo factFaultDescribeInfo:result){
            dto=new WaterDeviceRepairFactFaultDescribeInfoDTO();
            factFaultDescribeInfo.convert(dto);
            dtoList.add(dto);
        }
        return dtoList;
    }

    /***
     * 功能描述：批量创建
     *
     * @param: [factFaultInfos, deviceId, sncode, workcode, workOrderTypeEnum, operationUserId]
     * @auther: liu yi
     * @date: 2019/4/16 10:17
     * @return: void
     */
    /*public void create(String factFaultInfos, String deviceId, String sncode, String workcode, WorkOrderTypeEnum workOrderTypeEnum, Integer operationUserId) {
        JSONArray jsonArray;
        try {
            jsonArray = JSONArray.parseArray(factFaultInfos);
        } catch (Exception e) {
            log.info("解析解决措施信息json失败", new Object[]{e.getMessage()});
            throw new YimaoException("解析解决措施信息json失败！");
        }

        if (jsonArray ==null) {
            throw new YimaoException("参数必须有值！");
        }

        List<WaterDeviceRepairFactFaultDescribeInfo> list = JSONArray.parseArray(factFaultInfos,WaterDeviceRepairFactFaultDescribeInfo.class);
        for(WaterDeviceRepairFactFaultDescribeInfo describeInfo:list){
            describeInfo.setCreateTime(new Date());
            describeInfo.setUpdateTime(new Date());
            if (operationUserId != null) {
                describeInfo.setCreateUser(String.valueOf(operationUserId));
                describeInfo.setUpdateUser(String.valueOf(operationUserId));
            }

            //describeInfo.setDeviceId(deviceId);//TODO 缺少id
            describeInfo.setOldDeviceId(deviceId);
            describeInfo.setDeviceSncode(sncode);
            describeInfo.setWorkCode(workcode);
            describeInfo.setWorkOrderIndex(workOrderTypeEnum.getType());
            describeInfo.setWorkOrderIndexName(workOrderTypeEnum.getTypeName());
            describeInfo.setDelStatus(StatusEnum.FALSE.value());
            describeInfo.setIdStatus(StatusEnum.YES.value());

            this.waterDeviceRepairFactFaultDescribeInfoMapper.insert(describeInfo);
        }
    }*/
}
