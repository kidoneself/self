package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 收益分配规则
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@Table(name = "income_rule")
@Getter
@Setter
public class IncomeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;                //名称
    private String code;                //规则code
    private Integer incomeType;         //收益类型：1-产品收益；2-续费收益；3-服务收益；4-招商收益
    private Integer allotType;          //分配类型：1-按比例分配；2-按金额分配；
    private String creator;             //创建人
    private Date createTime;            //创建时间
    private String updater;             //更新人
    private Date updateTime;            //更新时间

    private Boolean deleted;             //是否删除


    public IncomeRule() {
    }

    /**
     * 用业务对象IncomeRuleDTO初始化数据库对象IncomeRule。
     *
     * @param dto 业务对象
     */
    public IncomeRule(IncomeRuleDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.code = dto.getCode();
        this.incomeType = dto.getIncomeType();
        this.allotType = dto.getAllotType();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.deleted = dto.getDeleted();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象IncomeRuleDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(IncomeRuleDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setCode(this.code);
        dto.setIncomeType(this.incomeType);
        dto.setAllotType(this.allotType);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setDeleted(this.deleted);
    }
}