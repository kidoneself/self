package com.yimao.cloud.openapi.controller;

import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.openapi.feign.SystemFeign;
import com.yimao.cloud.pojo.dto.system.AppUrlDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/11/12
 */
@RestController
@Slf4j
@Api(tags = "IOSUrlController")
public class IOSUrlController {

    private static final String IOS_URL = "IOS_URL_";

    @Resource
    private SystemFeign systemFeign;
    @Resource
    private RedisCache redisCache;

    /**
     * 翼猫APP（IOS版本）获取域名列表
     */
    @GetMapping(value = "/ios/getUrl")
    @ApiOperation(value = "翼猫APP（IOS版本）获取域名列表")
    public Object getUrl(@RequestParam Integer version) {
        AppUrlDTO appUrl = redisCache.get(IOS_URL + version, AppUrlDTO.class);
        if (appUrl == null) {
            appUrl = systemFeign.getIOSUrl(version);
            redisCache.set(IOS_URL + version, appUrl);
        }
        return appUrl;
    }

}
