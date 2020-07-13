package com.yimao.cloud.order.mapper;


import com.yimao.cloud.order.po.IncomeRulePart;
import com.yimao.cloud.pojo.dto.order.IncomeRulePartDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 收益规则详情
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
public interface IncomeRulePartMapper extends Mapper<IncomeRulePart> {

    /**
     * 批量插入
     *
     * @param list
     */
    void batchInsert(@Param("list") List<IncomeRulePart> list);

    List<IncomeRulePartDTO> getIncomeRulePartByIncomeRuleId(@Param("ruleId") Integer ruleId);
}
