package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.IncomeRulePartDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 收益分配规则所含收益主体
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@Table(name = "income_rule_part")
@Getter
@Setter
public class IncomeRulePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer ruleId;             //收益规则表主表id
    private Integer subjectId;          //收益主体ID
    private String subjectCode;         //收益主体CODE
    private BigDecimal value;           //收益比例或金额
    private String creator;             //创建人
    private Date createTime;            //创建时间
    private String updater;             //更新人
    private Date updateTime;            //更新时间

    private Boolean deleted;            //是否失效

    public IncomeRulePart() {
    }

    /**
     * 用业务对象IncomeRulePartDTO初始化数据库对象IncomeRulePart。
     *
     * @param dto 业务对象
     */
    public IncomeRulePart(IncomeRulePartDTO dto) {
        this.id = dto.getId();
        this.ruleId = dto.getRuleId();
        this.subjectId = dto.getSubjectId();
        this.subjectCode = dto.getSubjectCode();
        this.value = dto.getValue();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象IncomeRulePartDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(IncomeRulePartDTO dto) {
        dto.setId(this.id);
        dto.setRuleId(this.ruleId);
        dto.setSubjectId(this.subjectId);
        dto.setSubjectCode(this.subjectCode);
        dto.setValue(this.value);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}