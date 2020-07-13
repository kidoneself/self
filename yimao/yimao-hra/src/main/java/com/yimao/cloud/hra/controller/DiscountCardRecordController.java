package com.yimao.cloud.hra.controller;

import com.yimao.cloud.hra.service.DiscountCardRecordService;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 优惠卡
 *
 * @author hhf
 * @date 2019/4/16
 */
@RestController
@Slf4j
@Api(tags = "HraCardController")
public class DiscountCardRecordController {

    @Resource
    private DiscountCardRecordService discountCardRecordService;

    @PostMapping(value = {"/discount/card/grant"})
    @ApiOperation(value = "优惠卡发放")
    @ApiImplicitParam(name = "userDTO", value = "用户信息", required = true, dataType = "UserDTO", paramType = "body")
    public void grantDiscountCard(@RequestBody UserDTO userDTO) {
        discountCardRecordService.grantDiscountCard(userDTO);
    }
}
