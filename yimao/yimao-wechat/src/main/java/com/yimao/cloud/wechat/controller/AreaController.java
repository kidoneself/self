package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.wechat.feign.SystemFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 获取省市区
 *
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/4/11
 */
@RestController
@Slf4j
@Api(tags = {"AreaController"})
public class AreaController {

    @Resource
    private SystemFeign systemFeign;

    @GetMapping("/area")
    @ApiOperation(value = "获取全部省市区列表", notes = "获取全部省市区列表")
    public ResponseEntity areaList() {
        return ResponseEntity.ok(systemFeign.areaList());
    }
}
