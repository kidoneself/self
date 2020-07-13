package com.yimao.cloud.order.mapper;

import com.yimao.cloud.order.po.ProductIncomeRule;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

public interface ProductIncomeRuleMapper extends Mapper<ProductIncomeRule> {

    /**
     * 批量插入
     *
     * @param list 批量数据
     */
    void batchInsert(@Param("list") List<ProductIncomeRule> list);

    /**
     * 根据产品ID查询收益分配规则ID
     *
     * @param productId 产品ID
     */
    Set<Integer> listIncomeRuleIdByProductId(@Param("productId") Integer productId);

    IncomeRuleDTO getIncomeValue(@Param("id") Integer id);
}
