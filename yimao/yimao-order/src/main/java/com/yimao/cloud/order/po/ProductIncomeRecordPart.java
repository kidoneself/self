package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.ProductIncomeRecordPartDTO;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 产品收益结算对象记录
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@Table(name = "product_income_record_part")
@Getter
@Setter
public class ProductIncomeRecordPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer recordId;               //产品收益记录主表id

    private Integer subjectId;              //收益主体id
    private String subjectCode;             //收益主体类型code
    private String subjectName;             //收益主体名称
    private String subjectPhone;            //收益主体手机号
    private String subjectIdCard;           //收益主题身份证号
    private String subjectProvince;         //收益主体省
    private String subjectCity;             //收益主体市
    private String subjectRegion;           //收益主体区县
    private BigDecimal subjectRatio;        //收益主体比例
    private BigDecimal subjectMoney;        //收益主体金额

    private Integer settlementSubjectId;    //结算主体ID
    private String settlementSubjectCode;   //结算主体CODE
    private String settlementSubjectName;   //结算主体名称

    private Date settlementTime;            //结算时间
    private Integer hasWithdraw;            //是否提现：0-否1-是 默认0
    private Long partnerTradeNo;            //提现单号
    private Integer auditStatus;            //申请提现审核状态： 1-通过 2-不通过 3-待审核 4-申请提现但未短信校验通过

    private Date createTime;                //创建时间
    private Date updateTime;                //更新时间
    public ProductIncomeRecordPart() {
    }

    /**
     * 用业务对象ProductIncomeRecordPartDTO初始化数据库对象ProductIncomeRecordPart。
     *
     * @param dto 业务对象
     */
    public ProductIncomeRecordPart(ProductIncomeRecordPartDTO dto) {
        this.id = dto.getId();
        this.recordId = dto.getRecordId();
        this.subjectId = dto.getSubjectId();
        this.subjectCode = dto.getSubjectCode();
        this.subjectName = dto.getSubjectName();
        this.subjectPhone = dto.getSubjectPhone();
        this.subjectProvince = dto.getSubjectProvince();
        this.subjectCity = dto.getSubjectCity();
        this.subjectRegion = dto.getSubjectRegion();
        this.subjectRatio = dto.getSubjectRatio();
        this.subjectMoney = dto.getSubjectMoney();
        this.settlementSubjectId = dto.getSettlementSubjectId();
        this.settlementSubjectCode = dto.getSettlementSubjectCode();
        this.settlementSubjectName = dto.getSettlementSubjectName();
        this.settlementTime = dto.getSettlementTime();
        this.hasWithdraw = dto.getHasWithdraw();
        this.partnerTradeNo = dto.getPartnerTradeNo();
        this.auditStatus = dto.getAuditStatus();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductIncomeRecordPartDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(ProductIncomeRecordPartDTO dto) {
        dto.setId(this.id);
        dto.setRecordId(this.recordId);
        dto.setSubjectId(this.subjectId);
        dto.setSubjectCode(this.subjectCode);
        dto.setSubjectName(this.subjectName);
        dto.setSubjectPhone(this.subjectPhone);
        dto.setSubjectProvince(this.subjectProvince);
        dto.setSubjectCity(this.subjectCity);
        dto.setSubjectRegion(this.subjectRegion);
        dto.setSubjectRatio(this.subjectRatio);
        dto.setSubjectMoney(this.subjectMoney);
        dto.setSettlementSubjectId(this.settlementSubjectId);
        dto.setSettlementSubjectCode(this.settlementSubjectCode);
        dto.setSettlementSubjectName(this.settlementSubjectName);
        dto.setSettlementTime(this.settlementTime);
        dto.setHasWithdraw(this.hasWithdraw);
        dto.setPartnerTradeNo(this.partnerTradeNo);
        dto.setAuditStatus(this.auditStatus);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}