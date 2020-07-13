package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.water.FilterSettingDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO;
import com.yimao.cloud.pojo.query.water.FilterSettingQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.mapper.FilterSettingMapper;
import com.yimao.cloud.water.po.FilterSetting;
import com.yimao.cloud.water.service.FilterSettingService;
import com.yimao.cloud.water.service.WaterDeviceConsumableService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 描述：滤芯参数配置
 *
 * @Author Zhang Bo
 * @Date 2019/7/16
 */
@Service
public class FilterSettingServiceImpl implements FilterSettingService {

    @Resource
    private UserCache userCache;
    @Resource
    private FilterSettingMapper filterSettingMapper;
    @Resource
    private WaterDeviceConsumableService waterDeviceConsumableService;

    @Override
    public void save(FilterSetting setting) {
        this.check(setting);
        // FilterSetting record = getByPCR(setting.getProvince(), setting.getCity(), setting.getRegion(), setting.getDeviceModel());
        FilterSetting record = filterSettingMapper.selectByPCR(setting.getProvince(), setting.getCity(), setting.getRegion(), setting.getDeviceModel());
        if (record != null) {
            throw new BadRequestException("该地区该型号配置已存在。");
        }
        setting.setCreator(userCache.getCurrentAdminRealName());
        setting.setCreateTime(new Date());
        setting.setId(null);
        filterSettingMapper.insert(setting);
    }

    @Override
    public void update(FilterSetting setting) {
        this.check(setting);
        // FilterSetting record = getByPCR(setting.getProvince(), setting.getCity(), setting.getRegion(), setting.getDeviceModel());
        FilterSetting record = filterSettingMapper.selectByPCR(setting.getProvince(), setting.getCity(), setting.getRegion(), setting.getDeviceModel());
        if (record != null && !Objects.equals(record.getId(), setting.getId())) {
            throw new BadRequestException("该地区该型号配置已存在。");
        }
        setting.setUpdater(userCache.getCurrentAdminRealName());
        setting.setUpdateTime(new Date());
        filterSettingMapper.updateByPrimaryKeySelective(setting);
    }

    @Override
    public PageVO<FilterSettingDTO> page(Integer pageNum, Integer pageSize, FilterSettingQuery query) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<FilterSettingDTO> page = filterSettingMapper.selectPage(query);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public FilterSettingDTO getById(Integer id) {
        FilterSetting setting = filterSettingMapper.selectByPrimaryKey(id);
        if (setting == null) {
            return null;
        }
        List<WaterDeviceConsumableDTO> consumables = waterDeviceConsumableService.listByDeviceModelForFilterSetting(setting.getDeviceModel());
        FilterSettingDTO dto = new FilterSettingDTO();
        setting.convert(dto);
        dto.setConsumables(consumables);
        return dto;
    }

    @Override
    public FilterSetting getByPCR(String province, String city, String region, String deviceModel) {
        FilterSetting filterSetting = filterSettingMapper.selectByPCR(province, city, region, deviceModel);
        if (filterSetting == null) {
            filterSetting = filterSettingMapper.selectByPCR(province, city, null, deviceModel);
        }
        if (filterSetting == null) {
            return filterSettingMapper.selectByPCR(province, null, null, deviceModel);
        }
        return filterSetting;
    }

    /**
     * 参数校验
     *
     * @param setting setting
     */
    private void check(FilterSetting setting) {
        if (StringUtil.isEmpty(setting.getProvince())) {
            throw new BadRequestException("省不能为空。");
        }
        // if (StringUtil.isEmpty(setting.getCity())) {
        //     throw new BadRequestException("市不能为空。");
        // }
        // if (StringUtil.isEmpty(setting.getRegion())) {
        //     throw new BadRequestException("区不能为空。");
        // }
        if (StringUtil.isEmpty(setting.getDeviceModel())) {
            throw new BadRequestException("设备型号不能为空。");
        }
        if (setting.getK() == null) {
            throw new BadRequestException("TDS，k值不能为空。");
        }
        if (setting.getT() == null) {
            throw new BadRequestException("TDS，t值不能为空。");
        }
    }
}
