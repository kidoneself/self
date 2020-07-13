package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.IncomeSubjectDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 收益主体
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@Table(name = "income_subject")
@Getter
@Setter
public class IncomeSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String incomeSubjectName;       //收益主体名称
    private String incomeSubjectCode;       //收益主体类型code
    private String settlementSubjectName;   //结算主体名称
    private String settlementSubjectCode;   //结算主体类型code

    private String creator;                 //创建人
    private Date createTime;                //创建时间
    private String updater;                 //更新人
    private Date updateTime;                //更新时间

    public IncomeSubject() {
    }

    /**
     * 用业务对象IncomeSubjectDTO初始化数据库对象IncomeSubject。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public IncomeSubject(IncomeSubjectDTO dto) {
        this.id = dto.getId();
        this.incomeSubjectName = dto.getIncomeSubjectName();
        this.incomeSubjectCode = dto.getIncomeSubjectCode();
        this.settlementSubjectCode = dto.getSettlementSubjectCode();
        this.settlementSubjectName = dto.getSettlementSubjectName();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象IncomeSubjectDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(IncomeSubjectDTO dto) {
        dto.setId(this.id);
        dto.setIncomeSubjectName(this.incomeSubjectName);
        dto.setIncomeSubjectCode(this.incomeSubjectCode);
        dto.setSettlementSubjectCode(this.settlementSubjectCode);
        dto.setSettlementSubjectName(this.settlementSubjectName);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}