package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.ProductIncomeRuleDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;

/**
 * 描述：产品-收益分配规则关联关系
 *
 * @Author Zhang Bo
 * @Date 2019/3/19
 */
@Table(name = "product__income_rule")
@Getter
@Setter
public class ProductIncomeRule {

    private Integer productId;
    private Integer incomeRuleId;

    public ProductIncomeRule() {
    }

    /**
     * 用业务对象ProductIncomeRuleDTO初始化数据库对象ProductIncomeRule。
     *
     * @param dto 业务对象
     */
    public ProductIncomeRule(ProductIncomeRuleDTO dto) {
        this.productId = dto.getProductId();
        this.incomeRuleId = dto.getIncomeRuleId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductIncomeRuleDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(ProductIncomeRuleDTO dto) {
        dto.setProductId(this.productId);
        dto.setIncomeRuleId(this.incomeRuleId);
    }
}
