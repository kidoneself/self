package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.IpUtil;
import com.yimao.cloud.wechat.feign.HraFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Lizhqiang
 * @date 2019/4/12
 */
@Slf4j
@RestController
@Api(tags = {"ActivityExchangeController"})
public class ActivityExchangeController {

    @Resource
    private HraFeign hraFeign;


    @PostMapping(value = "/exchange/ticket")
    @ApiOperation(value = "兑换")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exchangeCode", value = "兑换码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "exchangeFrom", value = "兑换来源", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "channel", value = "兑换渠道", dataType = "String", paramType = "query"),
    })
    public Object exChangeTicketByCode(@RequestParam String exchangeCode,
                                       @RequestParam(defaultValue = "1") Integer exchangeFrom,
                                       @RequestParam String channel) {

        String msg = hraFeign.exChangeTicketByCode(exchangeCode, exchangeFrom, channel);
        if (msg.contains("M")) {
            return ResponseEntity.ok(msg);
        }
        throw new NotFoundException(msg);
    }
}
