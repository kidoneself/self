package com.yimao.cloud.water.controller;

import com.yimao.cloud.water.po.FlowRule;
import com.yimao.cloud.water.service.FlowRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/***
 * 功能描述:pad流量规则
 *
 * @auther: liu yi
 * @date: 2019/6/3 12:02
 */
@RestController
@Api(tags = "FlowRuleController")
public class FlowRuleController {

    @Resource
    private FlowRuleService flowRuleService;

    /**
     * 创建流量规则
     *
     * @param rule 流量规则信息
     */
    @PostMapping(value = "/flowRule")
    @ApiOperation(value = "创建流量规则信息")
    @ApiImplicitParam(name = "rule", value = "流量规则信息", required = true, dataType = "FlowRule", paramType = "body")
    public Object save(@RequestBody FlowRule rule) {
        flowRuleService.save(rule);
        return ResponseEntity.noContent().build();
    }

    /**
     * 编辑流量规则
     */
    @PutMapping(value = "/flowRule")
    @ApiOperation(value = "编辑流量规则")
    @ApiImplicitParam(name = "rule", value = "流量规则信息", required = true, dataType = "FlowRule", paramType = "body")
    public Object update(@RequestBody FlowRule rule) {

        flowRuleService.update(rule);

        return ResponseEntity.noContent().build();
    }

    /**
     * 获取流量规则
     *
     */
    @GetMapping(value = "/flowRule")
    @ApiOperation(value = "获取流量规则")
    public Object getFlowRule() {
        FlowRule rule = flowRuleService.getFlowRule();
        return ResponseEntity.ok(rule);
    }
}
