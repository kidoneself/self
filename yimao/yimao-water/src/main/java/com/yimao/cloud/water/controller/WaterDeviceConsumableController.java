package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.WaterDeviceConsumable;
import com.yimao.cloud.water.service.WaterDeviceConsumableService;
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

/**
 * 水机设备耗材。
 *
 * @author Zhang Bo
 * @date 2017/12/15.
 */
@RestController
@Api(tags = "WaterDeviceConsumableController")
public class WaterDeviceConsumableController {

    @Resource
    private WaterDeviceConsumableService waterDeviceConsumableService;

    /**
     * 创建水机耗材
     *
     * @param dto 水机耗材
     */
    @PostMapping(value = "/consumable")
    @ApiOperation(value = "创建水机耗材")
    @ApiImplicitParam(name = "dto", value = "水机耗材", required = true, dataType = "WaterDeviceConsumableDTO", paramType = "body")
    public void save(@RequestBody WaterDeviceConsumableDTO dto) {
        WaterDeviceConsumable consumable = new WaterDeviceConsumable(dto);
        waterDeviceConsumableService.save(consumable);
    }

    /**
     * 根据老耗材id查询耗材
     *
     * @param id 百得耗材id
     */
    @GetMapping(value = "/consumable/{oldId}/oldId")
    @ApiOperation(value = "根据老耗材id查询耗材")
    @ApiImplicitParam(name = "oldId", value = "老耗材id", required = true, dataType = "String", paramType = "path")
    public WaterDeviceConsumableDTO getConsumableByOldId(@PathVariable(value = "oldId") String oldId) {
        WaterDeviceConsumableDTO dto=waterDeviceConsumableService.getConsumableByOldId(oldId);
        return dto;
    }

    /**
     * 删除水机耗材
     *
     * @param id 水机耗材ID
     */
    @DeleteMapping(value = "/consumable/{id}")
    @ApiOperation(value = "删除水机耗材")
    @ApiImplicitParam(name = "id", value = "水机耗材ID", required = true, dataType = "Long", paramType = "path")
    public void delete(@PathVariable Integer id) {
        waterDeviceConsumableService.delete(id);
    }

    /**
     * 修改水机耗材
     *
     * @param dto 水机耗材
     */
    @PutMapping(value = "/consumable")
    @ApiOperation(value = "修改水机耗材")
    @ApiImplicitParam(name = "dto", value = "水机耗材", required = true, dataType = "WaterDeviceConsumableDTO", paramType = "body")
    public void update(@RequestBody WaterDeviceConsumableDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            WaterDeviceConsumable consumable = new WaterDeviceConsumable(dto);
            waterDeviceConsumableService.update(consumable);
        }
    }

    /**
     * 查询水机耗材（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    @GetMapping(value = "/consumable/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询水机耗材（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "model", value = "水机设备型号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "耗材类型：1-滤芯 2-滤网",dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<WaterDeviceConsumableDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                 @RequestParam(required = false) Integer type,
                                                 @RequestParam(required = false) String model) {
        return waterDeviceConsumableService.page(pageNum, pageSize, type, model);
    }

}
