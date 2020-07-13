package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

/**
 * @description: 经销商配置
 * @author: yu chunlei
 * @create: 2019-08-06 10:52:39
 **/
@RestController
@Slf4j
@Api(tags = "DistributorRoleController")
public class DistributorRoleController {

    @Resource
    private UserFeign userFeign;

    /**
     * @Author ycl
     * @Description 查询经销商角色配置信息（所有）
     * @Date 10:56 2019/8/6
     * @Param
     **/
    @GetMapping(value = "/distributor/roles")
    @ApiOperation(value = "查询经销商角色配置信息（所有）", notes = "查询经销商角色配置信息（所有）")
    public ResponseEntity list() {
        return ResponseEntity.ok(userFeign.list());
    }


}
