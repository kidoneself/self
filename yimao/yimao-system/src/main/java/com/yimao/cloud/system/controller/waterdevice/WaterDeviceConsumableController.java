package com.yimao.cloud.system.controller.waterdevice;

import com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.WaterFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 水机设备耗材
 *
 * @author Liu Yi
 * @date 2017/12/15.
 */
@RestController
@Api(tags = "WaterDeviceConsumableController")
public class WaterDeviceConsumableController {

    @Resource
    private WaterFeign waterFeign;

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
            @ApiImplicitParam(name = "type", value = "耗材类型：1-滤芯 2-滤网", defaultValue = "1", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public PageVO<WaterDeviceConsumableDTO> page(@PathVariable Integer pageNum,
                                                 @PathVariable Integer pageSize,
                                                 @RequestParam(required = false, defaultValue = "1") Integer type,
                                                 @RequestParam(required = false) String model) {
        return waterFeign.getConsumablePageList(pageNum, pageSize, type, model);
    }

}
