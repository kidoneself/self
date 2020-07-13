package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 收益分配规则所含收益主体
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@ApiModel(description = "收益分配规则所含收益主体DTO")
@Getter
@Setter
public class IncomeRulePartDTO {

    @ApiModelProperty(value = "主键ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "收益规则ID")
    private Integer ruleId;
    @ApiModelProperty(position = 2, value = "收益主体ID")
    private Integer subjectId;
    @ApiModelProperty(position = 3, value = "收益主体CODE")
    private String subjectCode;
    @ApiModelProperty(position = 4, value = "收益比例或金额")
    private BigDecimal value;
    @ApiModelProperty(position = 5, value = "经销商姓名")
    private String realName;
    @ApiModelProperty(position = 6, value = "经销商类型：1-代理商,2-经销商, 3-经销商+代理商")
    private Integer distributorType;
    @ApiModelProperty(position = 7, value = "用户e家号")
    private Integer userId;
    @ApiModelProperty(position = 8, value = "是否提现：0-否 1-是")
    private Integer hasWithdraw;
    @ApiModelProperty(position = 9, value = "提现单号")
    private String partnerTradeNo;
    @ApiModelProperty(position = 10, value = "申请提现审核状态： 1-通过 2-不通过 3-待审核 4-申请提现但未短信校验通过")
    private Integer auditStatus;

    @ApiModelProperty(position = 100, value = "创建人")
    private String creator;

    @ApiModelProperty(position = 101, value = "创建时间", example = "2018-12-28 11:00:00")
    private Date createTime;

    @ApiModelProperty(position = 102, value = "更新人")
    private String updater;

    @ApiModelProperty(position = 103, value = "更新时间", example = "2018-12-28 11:00:00")
    private Date updateTime;

    @ApiModelProperty(position = 104, value = "是否失效")
    private Boolean deleted;

}