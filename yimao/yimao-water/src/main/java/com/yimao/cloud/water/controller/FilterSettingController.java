package com.yimao.cloud.water.controller;

import com.yimao.cloud.pojo.dto.water.FilterSettingDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO;
import com.yimao.cloud.pojo.query.water.FilterSettingQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.mapper.FilterSettingMapper;
import com.yimao.cloud.water.po.FilterSetting;
import com.yimao.cloud.water.service.FilterSettingService;
import com.yimao.cloud.water.service.WaterDeviceConsumableService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 滤芯参数配置
 *
 * @author Zhang Bo
 * @date 2019/7/16
 */
@RestController
public class FilterSettingController {

    @Resource
    private FilterSettingService filterSettingService;
    @Resource
    private FilterSettingMapper filterSettingMapper;
    @Resource
    private WaterDeviceConsumableService waterDeviceConsumableService;

    /**
     * 创建滤芯参数配置
     *
     * @param dto dto
     */
    @PostMapping(value = "/filtersetting")
    public void save(@RequestBody FilterSettingDTO dto) {
        FilterSetting setting = new FilterSetting(dto);
        filterSettingService.save(setting);
    }

    /**
     * 业务系统-水机物联-滤芯参数配置-删除
     *
     * @param id 主键
     */
    @DeleteMapping(value = "/filtersetting/{id}")
    public void delete(@PathVariable Integer id) {
        filterSettingMapper.deleteByPrimaryKey(id);
    }

    /**
     * 编辑滤芯参数配置
     *
     * @param dto dto
     */
    @PutMapping(value = "/filtersetting")
    public void update(@RequestBody FilterSettingDTO dto) {
        FilterSetting setting = new FilterSetting(dto);
        filterSettingService.update(setting);
    }

    /**
     * 业务系统-水机物联-滤芯参数配置-查询列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/filtersetting/{pageNum}/{pageSize}")
    public PageVO<FilterSettingDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody FilterSettingQuery query) {
        return filterSettingService.page(pageNum, pageSize, query);
    }

    /**
     * 业务系统-水机物联-滤芯参数配置-详情
     *
     * @param id 主键
     */
    @GetMapping(value = "/filtersetting/{id}/detail")
    public FilterSettingDTO getById(@PathVariable Integer id) {
        return filterSettingService.getById(id);
    }

    /**
     * 业务系统-水机物联-滤芯参数配置-根据设备型号获取耗材列表
     */
    @GetMapping(value = "/filtersetting/consumables")
    public List<WaterDeviceConsumableDTO> listConsumableByDeviceModel(@RequestParam String deviceModel) {
        return waterDeviceConsumableService.listByDeviceModelForFilterSetting(deviceModel);
    }

}
