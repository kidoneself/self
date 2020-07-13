package com.yimao.cloud.system.controller.income;


import com.yimao.cloud.base.enums.IncomeType;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author Liu Yi
 * @description 收益规则服务
 * @date 2018/12/17
 */
@RestController
@Slf4j
@Api(tags = "IncomeRuleController")
public class IncomeRuleController {
    @Resource
    private OrderFeign orderFeign;


    /**
     * 分页查询规则模版信息
     *
     * @param name
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return
     * @author Liu Yi
     * @date 2019/1/17
     */
    @GetMapping(value = "/income/rule/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询规则模版信息", notes = "分页查询规则模版信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "模版名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "Date", format = "yyyy-MM-dd HH:mm:ss", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "Date", format = "yyyy-MM-dd HH:mm:ss", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity pageQueryIncomeRule(@RequestParam(value = "name", required = false) String name,
                                              @RequestParam(value = "startTime", required = false) Date startTime,
                                              @RequestParam(value = "endTime", required = false) Date endTime,
                                              @PathVariable(value = "pageNum") Integer pageNum,
                                              @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<IncomeRuleDTO> page = orderFeign.pageQueryIncomeRule(name, startTime, endTime, pageNum, pageSize);

        return ResponseEntity.ok(page);
    }

    /**
     * 创建收益分配规则
     *
     * @param dto 收益分配规则
     */
    @PostMapping(value = "/income/rule")
    @ApiOperation(value = "创建收益分配规则")
    @ApiImplicitParam(name = "dto", value = "收益分配规则", required = true, dataType = "IncomeRuleDTO", paramType = "body")
    public void save(@RequestBody IncomeRuleDTO dto) {
        orderFeign.saveIncomeRule(dto);
    }

    /**
     * 修改收益分配规则
     *
     * @param dto 分配规则
     * @return void
     * @author hhf
     * @date 2019/4/23
     */
    @PutMapping(value = "/income/rule")
    @ApiOperation(value = "修改收益分配规则")
    @ApiImplicitParam(name = "dto", value = "分配规则", required = true, dataType = "IncomeRuleDTO", paramType = "body")
    public void update(@RequestBody IncomeRuleDTO dto) {
        orderFeign.update(dto);
    }

    /**
     * 删除收益分配规则
     *
     * @param id 收益分配规则ID
     */
    @DeleteMapping(value = "/income/rule/{id}")
    @ApiOperation(value = "删除收益分配规则")
    @ApiImplicitParam(name = "id", value = "收益分配规则ID", required = true, dataType = "Long", paramType = "path")
    public void delete(@PathVariable Integer id) {
        orderFeign.delete(id);
    }

    /**
     * 获取所有招商收益分配规则
     */
    @GetMapping(value = "/income/rule/investment")
    @ApiOperation(value = "获取所有招商收益分配规则")
    public List<IncomeRuleDTO> listInvestmentIncomeRule() {
        List<IncomeRuleDTO> list = orderFeign.listInvestmentIncomeRule(IncomeType.INVESTMENT_INCOME.value);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        for (IncomeRuleDTO dto : list) {
            dto.setCode(null);
            dto.setIncomeType(null);
            dto.setAllotType(null);
            dto.setCreator(null);
            dto.setCreateTime(null);
            dto.setUpdater(null);
            dto.setUpdateTime(null);
            dto.setDeleted(null);
            dto.setIncomeRuleParts(null);
        }
        return list;
    }
}
