package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.enums.IntegralRuleStatus;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.water.IntegralConfigurationDTO;
import com.yimao.cloud.pojo.dto.water.IntegralRuleDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.IntegralRule;
import com.yimao.cloud.water.service.IntegralConfigurationService;
import com.yimao.cloud.water.service.IntegralRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/***
 * 功能描述:积分规则
 *
 * @auther: liu yi
 * @date: 2019/6/3 12:02
 */
@RestController
@Api(tags = "IntegralRuleController")
public class IntegralRuleController {

    @Resource
    private IntegralRuleService integralRuleService;
    @Resource
    private IntegralConfigurationService integralConfigurationService;
    /**
     * 创建积分规则
     *
     * @param dto 水机设备信息
     */
    @PostMapping(value = "/integralRule")
    @ApiOperation(value = "创建积分规则信息")
    @ApiImplicitParam(name = "dto", value = "积分规则信息", required = true, dataType = "IntegralRuleDTO", paramType = "body")
    public Object save(@RequestBody IntegralRuleDTO dto) {
        integralRuleService.save(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 上架积分规则
     */
    @PatchMapping(value = "/integralRule")
    @ApiOperation(value = "上下架积分规则")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ruleId", value = "规则id",required = true,dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态 1-未上架；2-上架；3-下架",required = true, dataType = "Long", paramType = "query")
    })
    public Object update(@RequestParam(value = "ruleId") Integer ruleId,
                         @RequestParam(value = "status") Integer status) {
        if (ruleId == null) {
            throw new BadRequestException("规则ID不能为空！");
        }
        if (status == null) {
            throw new BadRequestException("修改状态不能为空！");
        }

        IntegralRuleDTO dto = new IntegralRuleDTO();
        dto.setId(ruleId);
        dto.setStatus(status);
        integralRuleService.update(dto);

        return ResponseEntity.noContent().build();
    }

    /**
     * 根据id获取积分规则详情
     *
     * @param id 设备ID
     */
    @GetMapping(value = "/integralRule/{id}")
    @ApiOperation(value = "根据规则ID获取积分规则信息")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "path")
    public Object getById(@PathVariable Integer id) {
        IntegralRuleDTO dto = integralRuleService.getById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * 根据条件分页获取信息
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param name
     * @param status
     */
    @GetMapping(value = "/integralRule/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询积分规则列表（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "规则名称",dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态 1-未上架；2-上架；3-下架", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页码", dataType = "Long", paramType = "path")
    })
    public Object page(@RequestParam(value = "name",required = false) String name,
                             @RequestParam(value = "status",required = false) Integer status,
                             @PathVariable(value = "pageNum") Integer pageNum,
                             @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<IntegralRuleDTO> list = integralRuleService.page(pageNum, pageSize, name, status);
        Date currentTime = new Date();
        List<IntegralConfigurationDTO> configurationList;
        if (list != null && list.getTotal() > 0) {
            for (IntegralRuleDTO rule : list.getResult()) {
                if (IntegralRuleStatus.NOTONSHELF.value == rule.getStatus()) {
                    rule.setStatusText("未生效");
                } else if (IntegralRuleStatus.ONSHELF.value == rule.getStatus()) {
                    if (currentTime.compareTo(rule.getActivityEndTime()) > 0) {
                        rule.setStatusText("已结束");
                    } else {
                        rule.setStatusText("生效中");
                    }
                } else if (IntegralRuleStatus.OFFSHELF.value == rule.getStatus()) {
                    rule.setStatusText("已结束");
                } else if (IntegralRuleStatus.DELETED.value == rule.getStatus()) {
                    rule.setStatusText("已删除");
                } else {
                    rule.setStatusText("未知");
                }
                configurationList=integralConfigurationService.getByRuleId(rule.getId(),null);
                rule.setIntegralConfigurationList(configurationList);
            }
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 获取当前生效的积分规则
     *
     */
    @GetMapping(value = "/integralRule/effectiveIntegralRule")
    @ApiOperation(value = "获取当前生效的积分规则")
    public Object getEffectiveIntegralRule() {
        IntegralRuleDTO dto = integralRuleService.getEffectiveIntegralRule();
        return ResponseEntity.ok(dto);
    }


    /**
     * 根据水机设备ID删除积分规则
     *
     * @param id 规则ID
     */
    @DeleteMapping(value = "/integralRule/{id}")
    @ApiOperation(value = "根据ID删除积分规则")
    @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "Long")
    public void delete(@PathVariable(value = "id") Integer id) {
        IntegralRule rule = new IntegralRule();
        rule.setId(id);
        integralRuleService.deleteIntegralRule(rule);
    }

}
