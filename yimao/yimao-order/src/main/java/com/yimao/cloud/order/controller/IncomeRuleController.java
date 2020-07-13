package com.yimao.cloud.order.controller;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.order.mapper.IncomeRuleMapper;
import com.yimao.cloud.order.po.IncomeRule;
import com.yimao.cloud.order.service.IncomeRuleService;
import com.yimao.cloud.order.service.ProductIncomeRuleService;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRulePartDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Set;

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
    private IncomeRuleService incomRuleService;
    @Resource
    private ProductIncomeRuleService productIncomeRuleService;
    @Resource
    private IncomeRuleMapper incomeRuleMapper;

    /**
     * 创建收益分配规则
     *
     * @param dto 收益分配规则
     */
    @PostMapping(value = "/income/rule")
    @ApiOperation(value = "创建收益分配规则")
    @ApiImplicitParam(name = "dto", value = "收益分配规则", required = true, dataType = "IncomeRuleDTO", paramType = "body")
    public void save(@RequestBody IncomeRuleDTO dto) {
        IncomeRule incomeRule = new IncomeRule(dto);
        List<IncomeRulePartDTO> incomeRuleParts = dto.getIncomeRuleParts();
        incomRuleService.saveIncomeRule(incomeRule, incomeRuleParts);
    }

    /**
     * 查询收益分配规则（单个）
     *
     * @param id 收益分配规则ID
     */
    @GetMapping(value = "/income/rule/{id}")
    @ApiOperation(value = "查询收益分配规则（单个）")
    @ApiImplicitParam(name = "id", value = "收益分配规则ID", required = true, dataType = "Long", paramType = "path")
    public IncomeRuleDTO getById(@PathVariable Integer id) {
        PageVO<IncomeRuleDTO> pageVO = incomRuleService.listIncomeRule(0, 1000, id, null, null, null, null, null);
        List<IncomeRuleDTO> result = pageVO.getResult();
        if (CollectionUtil.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }

    /**
     * 查询收益分配规则（条件）
     *
     * @param name       收益分配规则名称
     * @param incomeType 收益类型：1-产品收益；2-续费收益；3-服务收益；4-招商收益；
     * @param allotType  分配类型：1-按比例分配；2-按金额分配；
     */
    @GetMapping(value = "/income/rule")
    @ApiOperation(value = "查询收益分配规则")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "收益分配规则名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型：1-产品收益；2-续费收益；3-服务收益；4-招商收益；", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "allotType", value = "分配类型：1-按比例分配；2-按金额分配；", dataType = "Long", paramType = "query")
    })
    public List<IncomeRuleDTO> listIncomeRule(@RequestParam(required = false) Integer productId,
                                              @RequestParam(required = false) String name,
                                              @RequestParam(required = false) Integer incomeType,
                                              @RequestParam(required = false) Integer allotType) {
        if (productId != null) {
            return incomRuleService.listIncomeRuleByProductId(productId);
        } else {
            PageVO<IncomeRuleDTO> pageVO = incomRuleService.listIncomeRule(0, 10000, null, name, incomeType, allotType, null, null);
            return pageVO.getResult();
        }
    }

    /**
     * 查询收益分配规则（条件、分页）
     *
     * @param pageNum    页码
     * @param pageSize   分页大小
     * @param name       收益分配规则名称
     * @param incomeType 收益类型：1-产品收益；2-续费收益；3-服务收益；4-招商收益；
     * @param allotType  分配类型：1-按比例分配；2-按金额分配；
     * @param startTime  开始时间
     * @param endTime    结束时间
     */
    @GetMapping(value = "/income/rule/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询规则模版信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "name", value = "收益分配规则名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "Date", format = "yyyy-MM-dd HH:mm:ss", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "Date", format = "yyyy-MM-dd HH:mm:ss", paramType = "query")
    })
    public PageVO<IncomeRuleDTO> pageIncomeRule(@PathVariable Integer pageNum,
                                                @PathVariable Integer pageSize,
                                                @RequestParam(required = false) String name,
                                                @RequestParam(required = false) Integer incomeType,
                                                @RequestParam(required = false) Integer allotType,
                                                @RequestParam(required = false) Date startTime,
                                                @RequestParam(required = false) Date endTime) {
        return incomRuleService.listIncomeRule(pageNum, pageSize, null, name, incomeType, allotType, startTime, endTime);
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
        IncomeRule incomeRule = incomeRuleMapper.selectByPrimaryKey(id);
        if (incomeRule == null) {
            throw new NotFoundException("未找到收益分配规则");
        }
        incomRuleService.delete(incomeRule);
    }

    /**
     * 编辑收益分配规则
     *
     * @param dto 分配规则
     * @return void
     * @author hhf
     * @date 2019/4/23
     */
    @PutMapping(value = "/income/rule")
    @ApiOperation(value = "编辑收益分配规则")
    @ApiImplicitParam(name = "dto", value = "分配规则", required = true, dataType = "IncomeRuleDTO", paramType = "body")
    public void update(@RequestBody IncomeRuleDTO dto) {
        IncomeRule incomeRule = new IncomeRule(dto);
        List<IncomeRulePartDTO> incomeRuleParts = dto.getIncomeRuleParts();
        incomRuleService.update(incomeRule, incomeRuleParts);
    }

    /**
     * 根据产品ID查询收益分配规则ID
     *
     * @param productId 产品ID
     */
    @GetMapping(value = "/income/rule/ids")
    public Set<Integer> listIncomeRuleIdByProductId(@RequestParam("productId") Integer productId) {
        return productIncomeRuleService.listIncomeRuleIdByProductId(productId);
    }


    /**
     *
     * app 调用获取商品的收益规则
     * @Author lizhiqiang
     * @Date  2019-07-25
     * @Param [id]
     * @return java.lang.Object
     */
    @GetMapping(value = "/income/rule/value")
    public Object getIncomeValue(@RequestParam("id") Integer id){
        IncomeRuleDTO incomeRuleDTO =  productIncomeRuleService.getIncomeValue(id);
        return incomeRuleDTO;
    }
}
