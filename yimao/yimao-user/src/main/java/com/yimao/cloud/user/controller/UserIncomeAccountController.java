package com.yimao.cloud.user.controller;

import com.yimao.cloud.pojo.dto.user.UserIncomeAccountDTO;
import com.yimao.cloud.user.service.UserIncomeAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户收益账户
 *
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/4/12
 */
@RestController
@Slf4j
@Api(tags = "UserIncomeAccountController")
public class UserIncomeAccountController {

    @Resource
    private UserIncomeAccountService userIncomeAccountService;

    /**
     * 普通用户下单的经销商信息
     *
     * @return
     */
    @GetMapping("/income/account")
    @ApiOperation(value = "普通用户下单的经销商信息", notes = "普通用户下单的经销商信息")
    public ResponseEntity<UserIncomeAccountDTO> getIncomeAccount() {
        return ResponseEntity.ok(userIncomeAccountService.getIncomeAccount());
    }
}
