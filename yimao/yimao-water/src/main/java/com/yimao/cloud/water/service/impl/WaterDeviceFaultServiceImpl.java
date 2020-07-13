package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.enums.DeviceFaultState;
import com.yimao.cloud.base.enums.DeviceFaultType;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFaultDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceFaultVO;
import com.yimao.cloud.water.mapper.WaterDeviceFaultMapper;
import com.yimao.cloud.water.po.WaterDeviceFault;
import com.yimao.cloud.water.service.WaterDeviceFaultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 描述：水机设备故障信息。
 *
 * @Author Zhang Bo
 * @Date 2019/2/25 14:43
 * @Version 1.0
 */
@Service
@Slf4j
public class WaterDeviceFaultServiceImpl implements WaterDeviceFaultService {

    @Resource
    private WaterDeviceFaultMapper waterDeviceFaultMapper;

    /**
     * 新增或更新水机设备故障
     */
    @Override
    public void saveOrUpdate(Integer deviceId, String sn, Integer type, String filterType, String fault) {
        Example example = new Example(WaterDeviceFault.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deviceId", deviceId);
        criteria.andEqualTo("sn", sn);
        criteria.andEqualTo("type", type);
        if (type == DeviceFaultType.FILTER.value && StringUtil.isNotEmpty(filterType)) {
            criteria.andEqualTo("filterType", filterType);
        }
        criteria.andEqualTo("state", DeviceFaultState.FAULT.value);
        example.orderBy("createTime").desc();
        List<WaterDeviceFault> faults = waterDeviceFaultMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(faults)) {
            WaterDeviceFault update = new WaterDeviceFault();
            WaterDeviceFault deviceFault = faults.get(0);
            update.setId(deviceFault.getId());
            update.setAmount(deviceFault.getAmount() + 1);
            Date now = new Date();
            update.setUpdateTime(now);
            Date start = deviceFault.getUpdateTime() == null ? deviceFault.getCreateTime() : deviceFault.getUpdateTime();
            int months = DateUtil.betweenMonths(start, now);
            Integer minTimeInterval = deviceFault.getMinTimeInterval();
            Integer maxTimeInterval = deviceFault.getMaxTimeInterval();
            if (minTimeInterval == null || months < minTimeInterval) {
                update.setMinTimeInterval(months);
            }
            if (maxTimeInterval == null || months > maxTimeInterval) {
                update.setMaxTimeInterval(months);
            }
            waterDeviceFaultMapper.updateByPrimaryKeySelective(update);
        } else {
            WaterDeviceFault deviceFault = new WaterDeviceFault();
            deviceFault.setDeviceId(deviceId);
            deviceFault.setSn(sn);
            deviceFault.setType(type);
            if (type == DeviceFaultType.FILTER.value && StringUtil.isNotEmpty(filterType)) {
                deviceFault.setFilterType(filterType);
            }
            deviceFault.setState(DeviceFaultState.FAULT.value);
            deviceFault.setFault(fault);
            deviceFault.setCreateTime(new Date());
            deviceFault.setAmount(1);
            waterDeviceFaultMapper.insert(deviceFault);
        }
    }

    /**
     * 解除水机设备故障
     */
    @Override
    public void resolve(Integer deviceId, String sn, Integer type, String filterType) {
        Example example = new Example(WaterDeviceFault.class);
        Example.Criteria criteria = example.createCriteria();
        if (deviceId != null) {
            criteria.andEqualTo("deviceId", deviceId);
        }
        if (StringUtil.isNotBlank(sn)) {
            criteria.andEqualTo("sn", sn);
        }
        if (type != null) {
            criteria.andEqualTo("type", type);
            if (type == DeviceFaultType.FILTER.value && StringUtil.isNotEmpty(filterType)) {
                criteria.andEqualTo("filterType", filterType);
            }
        }
        criteria.andEqualTo("state", DeviceFaultState.FAULT.value);
        WaterDeviceFault update = new WaterDeviceFault();
        //故障状态：1-故障；2-故障已解除；
        update.setState(DeviceFaultState.RESOLVE.value);
        waterDeviceFaultMapper.updateByExampleSelective(update, example);
    }

    /**
     * 根据SN码查询设备故障信息
     *
     * @param sn SN码
     */
    @Override
    public WaterDeviceFaultDTO getByDeviceIdAndSn(Integer deviceId, String sn) {
        return waterDeviceFaultMapper.selectByDeviceIdAndSn(deviceId, sn, DeviceFaultState.FAULT.value);
    }

    /**
     * 查询水机故障记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param sn       SN码
     */
    @Override
    public PageVO<WaterDeviceFaultVO> page(Integer pageNum, Integer pageSize, String sn) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDeviceFaultVO> page = waterDeviceFaultMapper.selectPage(sn);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public boolean existsWith(Integer deviceId, String sn, Integer type, String filterType) {
        return waterDeviceFaultMapper.existsWith(deviceId, sn, type, DeviceFaultState.FAULT.value, filterType);
    }

}
