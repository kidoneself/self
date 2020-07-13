package com.yimao.cloud.system.controller.waterdevice;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.water.FilterSettingDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO;
import com.yimao.cloud.pojo.query.water.FilterSettingQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.WaterFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "FilterSettingController")
public class FilterSettingController {

    @Resource
    private WaterFeign waterFeign;

    /**
     * 创建滤芯参数配置
     *
     * @param dto dto
     */
    @PostMapping(value = "/filtersetting")
    @ApiOperation(value = "创建滤芯参数配置")
    @ApiImplicitParam(name = "dto", value = "滤芯参数配置信息", dataType = "FilterSettingDTO", paramType = "body", required = true)
    public void save(@RequestBody FilterSettingDTO dto) {
        waterFeign.saveFilterSetting(dto);
    }

    /**
     * 业务系统-水机物联-滤芯参数配置-删除
     *
     * @param id 主键
     */
    @DeleteMapping(value = "/filtersetting/{id}")
    @ApiOperation(value = "业务系统-水机物联-滤芯参数配置-删除")
    @ApiImplicitParam(name = "id", value = "主键ID", dataType = "Long", paramType = "path", required = true)
    public void delete(@PathVariable Integer id) {
        waterFeign.deleteFilterSetting(id);
    }

    /**
     * 业务系统-水机物联-滤芯参数配置-编辑
     *
     * @param dto dto
     */
    @PutMapping(value = "/filtersetting")
    @ApiOperation(value = "编辑滤芯参数配置")
    @ApiImplicitParam(name = "dto", value = "滤芯参数配置信息", dataType = "FilterSettingDTO", paramType = "body", required = true)
    public void update(@RequestBody FilterSettingDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("操作对象ID不能为空。");
        }
        waterFeign.updateFilterSetting(dto);
    }

    /**
     * 业务系统-水机物联-滤芯参数配置-查询列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/filtersetting/{pageNum}/{pageSize}")
    @ApiOperation(value = "业务系统-水机物联-滤芯参数配置-查询列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "FilterSettingQuery", paramType = "body", required = true)
    })
    public PageVO<FilterSettingDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody FilterSettingQuery query) {
        return waterFeign.pageFilterSetting(pageNum, pageSize, query);
    }

    /**
     * 业务系统-水机物联-滤芯参数配置-详情
     *
     * @param id 主键
     */
    @GetMapping(value = "/filtersetting/{id}/detail")
    @ApiOperation(value = "业务系统-水机物联-滤芯参数配置-详情")
    @ApiImplicitParam(name = "id", value = "主键ID", dataType = "Long", paramType = "path", required = true)
    public FilterSettingDTO getDetailById(@PathVariable Integer id) {
        return waterFeign.getFilterSettingById(id);
    }

    /**
     * 业务系统-水机物联-滤芯参数配置-根据设备型号获取耗材列表
     */
    @GetMapping(value = "/filtersetting/consumables")
    @ApiOperation(value = "业务系统-水机物联-滤芯参数配置-根据设备型号获取耗材列表")
    @ApiImplicitParam(name = "deviceModel", value = "设备型号：1601T、1602T、1603T、1601L", dataType = "String", paramType = "query", required = true)
    public List<WaterDeviceConsumableDTO> listConsumableByDeviceModel(@RequestParam String deviceModel) {
        return waterFeign.listConsumableByDeviceModelForFilterSetting(deviceModel);
    }
}
