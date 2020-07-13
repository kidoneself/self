package com.yimao.cloud.product.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 订单
 *
 * @auther: Zhang Bo
 * @date: 2019/3/19
 */
@FeignClient(name = Constant.MICROSERVICE_ORDER)
public interface OrderFeign {

    /**
     * 查询收益分配规则（单个）
     *
     * @param id 收益分配规则ID
     */
    @RequestMapping(value = "/income/rule/{id}", method = RequestMethod.GET)
    IncomeRuleDTO getIncomeRuleById(@PathVariable(value = "id") Integer id);

    /**
     * 根据ID集合获取收益分配规则列表
     *
     * @param ids ID集合
     */
    @RequestMapping(value = "/income/rule", method = RequestMethod.GET)
    List<IncomeRuleDTO> listIncomeRuleByProductId(@RequestParam("productId") Integer productId);

    /**
     * 保存产品-收益分配关联关系
     *
     * @param productId     产品ID
     * @param incomeRuleIds 收益规则ID集合
     */
    @RequestMapping(value = "/productincomerule", method = RequestMethod.POST)
    void saveProductIncomeRule(@RequestParam("productId") Integer productId, @RequestParam("incomeRuleIds") Set<Integer> incomeRuleIds);

    /**
     * 根据产品ID获取收益分配规则ID集合
     *
     * @param productId 产品ID
     */
    @RequestMapping(value = "/income/rule/ids", method = RequestMethod.GET)
    Set<Integer> listIncomeRuleIdByProductId(@RequestParam("productId") Integer productId);

    @RequestMapping(value = "/income/rule/value", method = RequestMethod.GET)
    IncomeRuleDTO getIncomeRule(@RequestParam("id") Integer id);


    @GetMapping(value = "/order/sub/{id}")
    OrderSubDTO findOrderInfoById(@PathVariable("id") Long id);

    @GetMapping(value = "/order/sub/cancelWhenOffshelf")
    void cancelOrderWhenOffshelf(@RequestParam("productId") Integer productId);
}
