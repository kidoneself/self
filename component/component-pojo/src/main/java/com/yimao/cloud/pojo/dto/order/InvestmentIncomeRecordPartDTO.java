package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
@ApiModel(description = "招商收益结算对象记录")
public class InvestmentIncomeRecordPartDTO   implements Serializable {
    private static final long serialVersionUID = -2678352484273595560L;

    @ApiModelProperty(value = "主键ID")
    private Integer id;
    @ApiModelProperty(value = "招商收益记录主表id")
    private Integer incomeRecordId;
    @ApiModelProperty(value = "收益主体类型code")
    private String subjectCode;
    @ApiModelProperty(value = "收益主体id")
    private String subjectId;
    @ApiModelProperty(value = "收益主体名称")
    private String subjectName;
    @ApiModelProperty(value = "收益主体手机号")
    private String subjectPhone;
    @ApiModelProperty(value = "收益主体比例")
    private Integer subjectRatio;
    @ApiModelProperty(value = "收益主体金额")
    private BigDecimal subjectMoney;
    @ApiModelProperty(value = "收益主体省")
    private String subjectProvince;
    @ApiModelProperty(value = "收益主体市")
    private String subjectCity;
    @ApiModelProperty(value = "收益主体区县")
    private String subjectRegion;
    @ApiModelProperty(value = "结算主体id")
    private Integer settlementSubjectId;
    @ApiModelProperty(value = "结算主体名称")
    private String settlementSubjectName;
    @ApiModelProperty(value = "结算时间")
    private Date settlementTime;
    @ApiModelProperty(value = "经销商类型：1-代理商,2-经销商, 3-经销商+代理商")
    private Integer distributorType;
    @ApiModelProperty(value = "经销商账户")
    private String distributorAccount;
   /* @ApiModelProperty(value = "身份证")
    private String idCard;*/
    @ApiModelProperty(value = "用户e家号")
    private String userId;
    @ApiModelProperty(value = "是否提现：0-否 1-是")
    private Integer hasWithdraw;
    @ApiModelProperty(value = "提现单号")
    private String partnerTradeNo;
    @ApiModelProperty(value = "申请提现审核状态： 1-通过 2-不通过 3-待审核 4-申请提现但未短信校验通过")
    private Integer auditStatus;
    @ApiModelProperty(value = "创建时间",example = "2018-12-28 11:00:00")
    private Date createTime;
    @ApiModelProperty(value = "创建人")
    private String creator;
    @ApiModelProperty(value = "更新时间",example = "2018-12-28 11:00:00")
    private Date updateTime;
    @ApiModelProperty(value = "更新人")
    private String updater;
}