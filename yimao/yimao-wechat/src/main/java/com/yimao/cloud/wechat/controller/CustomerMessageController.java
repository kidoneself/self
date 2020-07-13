package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.pojo.dto.system.CustomerMessageDTO;
import com.yimao.cloud.wechat.feign.SystemFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * created by liuhao@yimaokeji.com
 * 2018052018/5/18
 */
@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
@Api(tags = {"CustomerMessageController"})
public class CustomerMessageController {


    @Resource
    private SystemFeign systemFeign;

    @GetMapping(value = "/customer/manager")
    @ApiOperation(value = "保存客户留言", notes = "保存客户留言")
    @ApiImplicitParam(name = "customerMessage", value = "客户留言消息", dataType = "CustomerMessage", required = true, paramType = "body")
    public Object save(@RequestBody CustomerMessageDTO dto) {
        return ResponseEntity.ok(systemFeign.saveCustomerMessage(dto));
    }

}
