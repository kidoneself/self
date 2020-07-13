package com.yimao.cloud.system.controller.waterdevice;

import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceFaultVO;
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
 * 描述：水机故障记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/26
 */
@RestController
@Api(tags = "WaterDeviceFaultController")
public class WaterDeviceFaultController {

    @Resource
    private WaterFeign waterFeign;

    /**
     * 水机物联-设备管理-设备故障（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param sn       SN码
     */
    @GetMapping(value = "/faultrecord/{pageNum}/{pageSize}")
    @ApiOperation(value = "水机物联-设备管理-设备故障")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "sn", value = "SN码", dataType = "String", paramType = "query"),
    })
    public PageVO<WaterDeviceFaultVO> page(@PathVariable Integer pageNum,
                                           @PathVariable Integer pageSize,
                                           @RequestParam(required = false) String sn) {
        return waterFeign.pageDeviceFault(pageNum, pageSize, sn);
    }

}
