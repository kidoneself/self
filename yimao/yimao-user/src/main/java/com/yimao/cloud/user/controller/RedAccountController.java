package com.yimao.cloud.user.controller;

import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.user.service.RedAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Liu Yi
 * @description 红包账户信息
 * @date 2019/9/9
 */
@Slf4j
@RestController
@Api(tags = "RedAccountController")
public class RedAccountController {
    @Resource
    private RedAccountService redAccountService;

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.RedAccountDTO
     * @description 根据账户id和账户类型获取账户红包账户信息
     * @author Liu Yi
     * @date 2019/9/9 16:19
     */
    @GetMapping(value = "/redAccount/accountId")
    @ApiOperation(value = "根据账户id和账户类型获取账户红包账户信息")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accountId", value = "账户id", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "accountType", value = "账户类型：1-安装工 2-经销商(各种身份)", required = true, dataType = "Long", paramType = "query")
    })
    public RedAccountDTO getRedAccountByAccountId(@RequestParam("accountId") Integer accountId,
                                                  @RequestParam("accountType") Integer accountType) {
        return redAccountService.getRedAccountByAccountId(accountId, accountType);
    }
}
