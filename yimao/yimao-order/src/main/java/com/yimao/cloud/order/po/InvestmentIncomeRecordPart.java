package com.yimao.cloud.order.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 招商收益结算对象记录
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@Data
@Table(name = "investment_income_record_part")
public class InvestmentIncomeRecordPart{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;
    private Integer incomeRecordId;//招商收益记录主表id
    private String subjectCode;//收益主体类型code
    private Integer subjectId;//收益主体id
    private String subjectName;//收益主体名称
    private String subjectPhone;//收益主体手机号
    private String subjectIdCard;//收益主体身份证号
    private BigDecimal subjectRatio;//收益主体比例
    private BigDecimal subjectMoney;//收益主体金额
    private String subjectProvince;//收益主体省
    private String subjectCity;//收益主体市
    private String subjectRegion;//收益主体区县
    private Integer settlementSubjectId;//结算主体id
    private String settlementSubjectName;//结算主体名称
    private String settlementSubjectCode; //结算主体类型code

    //private Date settlementTime;//结算时间
    private Integer distributorType;//经销商类型：1-代理商,2-经销商, 3-经销商+代理商
    //private String idCard;//身份证
    private Integer userId;//用户e家号
    private String realName;//经销商姓名
    private String userName;//经销商帐号
    private Integer hasWithdraw;//是否提现：0-否 1-是
    private String partnerTradeNo;//提现单号
    private Integer auditStatus;//申请提现审核状态： 1-通过 2-不通过 3-待审核 4-申请提现但未短信校验通过
    private Date createTime;//创建时间
    private String creator;//创建人
    private Date updateTime;//更新时间
    private String updater;//更新人
}