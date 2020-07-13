package com.yimao.cloud.openapi.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：水机设备型号
 *
 * @Author Zhang Bo
 * @Date 2019/7/17
 */
@RestController
@Api(tags = "WaterDeviceModelController")
public class WaterDeviceModelController {

    /**
     * 获取水机设备型号
     */
    @GetMapping(value = "/waterdevicemodel")
    @ApiOperation(value = "获取水机设备型号")
    public List<String> upload() {
        List<String> list = new ArrayList<>();
        list.add("1601T");
        list.add("1602T");
        list.add("1603T");
        list.add("1601L");
        return list;
    }

}
