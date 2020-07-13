package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.wechat.feign.SystemFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 区域客户经理
 * created by liuhao@yimaokeji.com
 * 2018052018/5/18
 */
@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
@Api(tags = {"CustomerAreaManagerController"})
public class CustomerAreaManagerController {

    @Resource
    private SystemFeign customerAreaManagerFeign;

    @GetMapping(value = "/customer/area/manager")
    @ApiOperation(value = "根据地区查询区域经理", notes = "根据地区查询区域经理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query")
    })
    public Object list(@RequestParam("province") String province,
                                       @RequestParam(value = "city", required = false) String city,
                                       @RequestParam(value = "region", required = false) String region) {
        return ResponseEntity.ok(customerAreaManagerFeign.customerAreaManagerByArea(province, city, region));
    }
}
