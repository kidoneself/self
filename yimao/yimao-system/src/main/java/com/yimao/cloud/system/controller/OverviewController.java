package com.yimao.cloud.system.controller;


import com.yimao.cloud.framework.aop.annotation.ExecutionTime;
import com.yimao.cloud.pojo.dto.system.BusinessProfileDTO;
import com.yimao.cloud.system.feign.OrderFeign;
import com.yimao.cloud.system.feign.UserFeign;
import com.yimao.cloud.system.service.OverviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@Slf4j
@Api(tags = "OverviewController")
public class OverviewController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private OverviewService overviewService;

    @ExecutionTime
    @GetMapping(value = "/overview")
    @ApiOperation(value = "概况-待办事项统计", notes = "概况-待办事项统计")
    public ResponseEntity overview() {
        Map<String, Long> map = orderFeign.orderOverview();
        map.putAll(userFeign.distributorOrderOverview());
        return ResponseEntity.ok(map);
    }

    @ExecutionTime
    @GetMapping(value = "/overview/business")
    @ApiOperation(value = "概况-经营概况", notes = "概况-经营概况")
    public BusinessProfileDTO overviewBusiness() {
        return overviewService.overviewBusiness();
    }
}
