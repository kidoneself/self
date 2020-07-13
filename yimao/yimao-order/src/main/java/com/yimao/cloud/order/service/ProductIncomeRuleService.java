package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;

import java.util.Set;

public interface ProductIncomeRuleService {

    void saveProductIncomeRule(Integer productId, Set<Integer> incomeRuleIds);

    /**
     * 根据产品ID查询收益分配规则ID
     *
     * @param productId 产品ID
     */
    Set<Integer> listIncomeRuleIdByProductId(Integer productId);

    /**
     * app 调用获取商品的收益规则
     *
     * @param id
     * @return
     */
    IncomeRuleDTO getIncomeValue(Integer id);

}
