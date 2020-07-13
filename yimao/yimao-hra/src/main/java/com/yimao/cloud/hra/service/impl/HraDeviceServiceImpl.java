package com.yimao.cloud.hra.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.enums.HraDeviceTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.CopyUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.hra.feign.SystemFeign;
import com.yimao.cloud.hra.mapper.HraDeviceMapper;
import com.yimao.cloud.hra.po.HraDevice;
import com.yimao.cloud.hra.service.HraDeviceService;
import com.yimao.cloud.pojo.dto.hra.HraDeviceDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceExportDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Zhang Bo
 * @date 2017/12/20.
 */
@Service
public class HraDeviceServiceImpl implements HraDeviceService {

    @Resource
    private HraDeviceMapper hraDeviceMapper;

    @Resource
    private SystemFeign systemFeign;

    @Override
    public PageVO<HraDeviceDTO> queryStationOnline(Integer pageNum, Integer pageSize, HraDeviceQuery query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<HraDevice> hraDevicePage = hraDeviceMapper.findHraDevice(query);
        return new PageVO<>(pageNum, hraDevicePage, HraDevice.class, HraDeviceDTO.class);
    }

    @Override
    public List<HraDeviceDTO> getHraDeviceByStationId(Integer stationId) {
        Example example = new Example(HraDevice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("stationId", stationId);
        List<HraDevice> hraDevices = hraDeviceMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(hraDevices)) {
            return CopyUtil.copyList(hraDevices, HraDevice.class, HraDeviceDTO.class);
        }
        //throw new YimaoException("查询HRA设备列表为空!");
        return null;
    }

    @Override
    public void deleteDevice(Integer id) {
        int i = hraDeviceMapper.deleteByPrimaryKey(id);
        if (i < 1) {
            throw new YimaoException("删除设备失败");
        }
    }

    @Override
    public HraDevice findHraDeviceById(Integer id) {
        HraDevice hraDevice = hraDeviceMapper.selectByPrimaryKey(id);
        if (Objects.isNull(hraDevice)) {
            throw new YimaoException("未查询到设备");
        }
        return hraDevice;
    }

    @Override
    public void updateDevice(HraDevice hraDevice) {
        if (Objects.isNull(hraDevice)) {
            throw new YimaoException("该设备不存在");
        }
        HraDevice device = this.getDevice(hraDevice.getDeviceId());
        //如果修改不是同一个
        if (device != null && !Objects.equals(hraDevice.getId(), device.getId())) {
            throw new BadRequestException("体检设备编号不能重复");
        }
        //1可用，2锁定
        if (Objects.equals(HraDeviceTypeEnum.HRA_I.value, hraDevice.getDeviceStatus())) {
            hraDevice.setDeviceStatus(HraDeviceTypeEnum.HRA_II.value);
        } else if (Objects.equals(HraDeviceTypeEnum.HRA_II.value, hraDevice.getDeviceStatus())) {
            hraDevice.setDeviceStatus(HraDeviceTypeEnum.HRA_I.value);
        } else {
            hraDevice.setDeviceStatus(HraDeviceTypeEnum.HRA_II.value);
        }
        hraDevice.setUpdateTime(new Date());
        int num = hraDeviceMapper.updateByPrimaryKeySelective(hraDevice);
        if (num < 1) {
            throw new YimaoException("修改设备失败");
        }

    }

    @Override
    public void batchDelete(Integer[] ids) {
        final List<Integer> list = Arrays.asList(ids);
        int count = hraDeviceMapper.batchDelete(list);
        if (count < 1) {
            throw new YimaoException("批量删除失败");
        }
    }

    /**
     * 查询HRA设备信息
     *
     * @param deviceId HRA设备ID
     */
    @Override
    public HraDevice getHraDeviceByDeviceId(String deviceId) {
        Example example = new Example(HraDevice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deviceId", deviceId);
        return hraDeviceMapper.selectOneByExample(example);
    }

    @Override
    public Boolean saveDeviceOnline(HraDeviceDTO dto) {
        if (dto != null) {
            this.checkDeviceInfo(dto);
            //校验设备ID是否存在
            if (this.getDevice(dto.getDeviceId()) != null) {
                throw new BadRequestException("体检设备编号不能重复");
            }
            HraDevice hraDevice = new HraDevice(dto);
            hraDevice.setCreateTime(new Date());
            hraDevice.setUpdateTime(new Date());
            return hraDeviceMapper.insertSelective(hraDevice) > 0;
        }
        return false;
    }

    @Override
    public List<HraDeviceExportDTO> exportDevice(HraDeviceQuery hraDeviceQuery) {
        List<HraDeviceExportDTO> resultList = hraDeviceMapper.exportDevice(hraDeviceQuery);
        for (HraDeviceExportDTO hraDeviceExportDTO : resultList) {
            String stationCompanyName = systemFeign.getStationCompanyNameById(hraDeviceExportDTO.getStationId());
            hraDeviceExportDTO.setStationCompanyName(stationCompanyName);
        }
        return resultList;
    }

    @Override
    public boolean getDeviceStatus(Integer stationId) {
        List<Integer> deviceStatus = hraDeviceMapper.getDeviceStatus(stationId);
        if (CollectionUtil.isNotEmpty(deviceStatus) && deviceStatus.contains(1)) {
            return true;
        }
        return false;
    }

    @Override
    public List<Integer> getHraStationIds() {
        List<Integer> ids = hraDeviceMapper.getHraStationIds();
        return ids;
    }


    /**
     * 校验设备信息
     *
     * @param dto 设置信息
     */
    private void checkDeviceInfo(HraDeviceDTO dto) {
        if (StringUtil.isEmpty(dto.getProvince()) || StringUtil.isEmpty(dto.getCity()) || StringUtil.isEmpty(dto.getRegion())) {
            throw new BadRequestException("设备的地区不能为空");
        }
        if (Objects.isNull(dto.getDeviceId())) {
            throw new BadRequestException("体检设备编号不能为空");
        }
        if (Objects.isNull(dto.getStationId()) || StringUtil.isEmpty(dto.getStationName())) {
            throw new BadRequestException("服务站不能为空");
        }
    }

    /**
     * 校验设备编号是否存在
     *
     * @param deviceId 设备编号
     * @return
     */
    private HraDevice getDevice(String deviceId) {
        Example example = new Example(HraDevice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deviceId", deviceId.trim());
        List<HraDevice> deviceList = hraDeviceMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(deviceList)) {
            if (deviceList.size() > 1) {
                throw new BadRequestException("体检设备编号不能重复");
            } else {
                return deviceList.get(0);
            }
        }
        return null;
    }
}
