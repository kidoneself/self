package com.yimao.cloud.order.controller;

import com.yimao.cloud.order.service.ProductIncomeRuleService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 描述：产品-收益规则关联
 *
 * @Author Zhang Bo
 * @Date 2019/3/21
 */
@RestController
@Api(tags = "ProductIncomeRuleController")
public class ProductIncomeRuleController {

    @Resource
    private ProductIncomeRuleService productIncomeRuleService;

    /**
     * 根据产品ID查询收益分配规则ID
     *
     * @param productId 产品ID
     */
    @PostMapping(value = "/productincomerule")
    public void listIncomeRuleIdByProductId(@RequestParam Integer productId, @RequestParam Set<Integer> incomeRuleIds) {
        productIncomeRuleService.saveProductIncomeRule(productId, incomeRuleIds);
    }

}
