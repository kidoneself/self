package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.water.service.WaterDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * 设备概况，图标展示。
 *
 * @author Zhang Bo
 * @date 2017/12/15.
 */
@RestController
@Api(tags = "WaterDeviceChartController")
public class WaterDeviceChartController {

    @Resource
    private WaterDeviceService waterDeviceService;

    /**
     * 设备概况：按在线离线统计分类
     */
    @GetMapping(value = "/waterdevice/classification/online")
    @ApiOperation(value = "设备概况：在线离线")
    public Map<String, Integer> classificationByOnline() {
        return waterDeviceService.classificationByOnline();
    }

    /**
     * 设备概况：按设备地区统计分类
     */
    @GetMapping(value = "/waterdevice/classification/area")
    @ApiOperation(value = "设备概况：按设备地区统计分类")
    public Map<String, Integer> classificationByArea() {
        return waterDeviceService.classificationByArea();
    }

    /**
     * 设备概况：按设产品型号统计分类
     */
    @GetMapping(value = "/waterdevice/classification/model")
    @ApiOperation(value = "设备概况：按设产品型号统计分类")
    public Map<String, Integer> classificationByModel() {
        return waterDeviceService.classificationByModel();
    }

    /**
     * 设备概况：按激活日期统计分类
     */
    @GetMapping(value = "/waterdevice/classification/trend")
    @ApiOperation(value = "设备概况：按激活日期统计分类")
    public Map<String, Integer> classificationByTrend(@RequestParam(required = false) Date startTime, @RequestParam(required = false) Date endTime) {
        if (startTime != null && endTime != null && startTime.after(endTime)) {
            throw new BadRequestException("日期选择错误。");
        } else {
            return waterDeviceService.classificationByTrend(startTime, endTime);
        }
    }

}
