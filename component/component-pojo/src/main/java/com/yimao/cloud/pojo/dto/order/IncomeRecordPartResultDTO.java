package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 收益记录（返回结果明细）
 *
 * @author hhf
 * @date 2019/2/26
 */
@Data
@ApiModel(description = "收益记录（返回结果明细）")
public class IncomeRecordPartResultDTO {
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "收益主体身份")
    private String incomeSubjectCode;
    @ApiModelProperty(value = "结算主体")
    private String settlementSubjectName;
    @ApiModelProperty(value = "收益主体id",hidden = true)
    private Integer subjectId;
    @ApiModelProperty(value = "收益主体")
    private String incomeSubjectName;
    @ApiModelProperty(value = "手机号")
    private String subjectPhone;
    @ApiModelProperty(value = "收益分配比例")
    private String subjectRatio;
    @ApiModelProperty(value = "收益分配金额")
    private String subjectMoney;
    @ApiModelProperty(value = "服务站省")
    private String province;
    @ApiModelProperty(value = "服务站市")
    private String city;
    @ApiModelProperty(value = "服务站区")
    private String region;
    @ApiModelProperty(value = "是否提现：0-否 1-是")
    private Integer hasWithdraw;
    @ApiModelProperty(value = "提现单号")
    private String partnerTradeNo;
    @ApiModelProperty(value = "申请提现审核状态： 1-通过 2-不通过 3-待审核 4-申请提现但未短信校验通过")
    private Integer auditStatus;
    @ApiModelProperty(value = "姓名")
    private String incomeName;
    @ApiModelProperty(value = "e家号")
    private Integer userId;
    @ApiModelProperty(value = "经销商/代理商账号")
    private String account;
    @ApiModelProperty(value = "服务站公司名称")
    private String companyName;



}
