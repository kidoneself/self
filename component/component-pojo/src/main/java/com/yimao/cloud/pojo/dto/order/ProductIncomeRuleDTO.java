package com.yimao.cloud.pojo.dto.order;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：产品-收益分配规则关联关系
 *
 * @Author Zhang Bo
 * @Date 2019/3/19
 */
@Getter
@Setter
public class ProductIncomeRuleDTO {

    private Integer productId;
    private Integer incomeRuleId;

}
