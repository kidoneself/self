package com.yimao.cloud.user.controller;

import com.yimao.cloud.framework.aop.annotation.ExecutionTime;
import com.yimao.cloud.pojo.dto.system.BusinessProfileDTO;
import com.yimao.cloud.pojo.dto.user.UserOverviewDTO;
import com.yimao.cloud.user.service.UserOverviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 概况-用户
 *
 * @author hhf
 * @date 2019/3/27
 */
@RestController
@Slf4j
@Api(tags = "UserOverviewController")
public class UserOverviewController {

    @Resource
    private UserOverviewService userOverviewService;

    /**
     * 用户概况
     *
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/19
     */
    @ExecutionTime
    @GetMapping("/user/overview")
    @ApiOperation(value = "用户概况", notes = "用户概况")
    public ResponseEntity<UserOverviewDTO> overview() {
        UserOverviewDTO userOverviewDTO = userOverviewService.overview();
        return ResponseEntity.ok(userOverviewDTO);
    }

    /**
     * 经营概况（用户相关）
     *
     * @param
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/27
     */
    @ExecutionTime
    @GetMapping("/user/overview/business")
    @ApiOperation(value = "经营概况（用户相关）", notes = "-经营概况（用户相关）")
    public ResponseEntity<BusinessProfileDTO> overviewBusiness() {
        BusinessProfileDTO businessProfileDTO = userOverviewService.overviewBusiness();
        return ResponseEntity.ok(businessProfileDTO);
    }
}
