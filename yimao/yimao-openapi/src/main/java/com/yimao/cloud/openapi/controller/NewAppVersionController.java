package com.yimao.cloud.openapi.controller;

import com.yimao.cloud.openapi.feign.SystemFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：APP版本更新
 *
 * @Author Zhang Bo
 * @Date 2019/11/8
 */
@RestController
@Slf4j
@Api(tags = "NewAppVersionController")
public class NewAppVersionController {

    @Resource
    private SystemFeign systemFeign;

    /**
     * APP版本更新查询
     *
     * @param version    当前版本
     * @param systemType 手机系统类型：0-安卓；1-IOS；
     * @param appType    终端：0-翼猫服务APP；1-翼猫APP；
     */
    @GetMapping(value = "/getNewAppVersion")
    @ApiOperation(value = "APP版本更新查询")
    public Object pageQueryIncomeRule(@RequestParam String version, @RequestParam Integer systemType, @RequestParam Integer appType) {
        return systemFeign.getNewAppVersion(version, systemType, appType);
    }
}
