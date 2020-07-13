package com.yimao.cloud.order.controller;

import com.yimao.cloud.order.service.OrderOverviewService;
import com.yimao.cloud.pojo.dto.system.BusinessProfileDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 订单概况
 *
 * @author hhf
 * @date 2019/3/26
 */
@RestController
@Slf4j
@Api(tags = "OrderOverviewController")
public class OrderOverviewController {

    @Resource
    private OrderOverviewService orderOverviewService;

    /**
     * 概况-待办事项统计
     *
     * @return Map
     * @author hhf
     * @date 2019/3/22
     */
    @GetMapping(value = "/order/overview")
    @ApiOperation(value = "概况-待办事项统计", notes = "概况-待办事项统计")
    public ResponseEntity orderOverview() {
        Map<String, Long> map = orderOverviewService.orderOverview();
        return ResponseEntity.ok(map);
    }

    /**
     * 概况-经营概况
     *
     * @return Map
     * @author hhf
     * @date 2019/3/22
     */
    @GetMapping(value = "/order/overview/business")
    @ApiOperation(value = "概况-经营概况", notes = "概况-经营概况")
    public ResponseEntity orderOverviewBusiness() {
        BusinessProfileDTO dto = orderOverviewService.orderOverviewBusiness();
        return ResponseEntity.ok(dto);
    }

}
