package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 产品收益结算对象记录
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@ApiModel(description = "产品收益结算对象记录")
@Getter
@Setter
public class ProductIncomeRecordPartDTO {

    @ApiModelProperty(value = "主键ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "服务收益记录主表id")
    private Integer recordId;

    @ApiModelProperty(position = 11, value = "收益主体id")
    private Integer subjectId;
    @ApiModelProperty(position = 12, value = "收益主体类型code")
    private String subjectCode;
    @ApiModelProperty(position = 13, value = "收益主体名称")
    private String subjectName;
    @ApiModelProperty(position = 14, value = "收益主体手机号")
    private String subjectPhone;
    @ApiModelProperty(position = 15, value = "收益主体省")
    private String subjectProvince;
    @ApiModelProperty(position = 16, value = "收益主体市")
    private String subjectCity;
    @ApiModelProperty(position = 17, value = "收益主体区县")
    private String subjectRegion;
    @ApiModelProperty(position = 18, value = "收益主体比例")
    private BigDecimal subjectRatio;
    @ApiModelProperty(position = 19, value = "收益主体金额")
    private BigDecimal subjectMoney;

    @ApiModelProperty(position = 21, value = "结算主体ID")
    private Integer settlementSubjectId;
    @ApiModelProperty(position = 22, value = "结算主体CODE")
    private String settlementSubjectCode;
    @ApiModelProperty(position = 23, value = "结算主体名称")
    private String settlementSubjectName;

    @ApiModelProperty(position = 31, value = "是否提现：0-否 1-是")
    private Integer hasWithdraw;
    @ApiModelProperty(position = 32, value = "提现单号")
    private Long partnerTradeNo;
    @ApiModelProperty(position = 33, value = "申请提现审核状态： 1-通过 2-不通过 3-待审核 4-申请提现但未短信校验通过")
    private Integer auditStatus;

    @ApiModelProperty(position = 40, value = "产品公司")
    private Integer productCompanyId;

    @ApiModelProperty(position = 51, value = "结算时间", example = "2018-12-28 11:00:00")
    private Date settlementTime;
    @ApiModelProperty(position = 100, value = "创建时间", example = "2018-12-28 11:00:00")
    private Date createTime;
    @ApiModelProperty(position = 101, value = "更新时间", example = "2018-12-28 11:00:00")
    private Date updateTime;
    @ApiModelProperty(position = 102, value = "收益主体身份证")
    private String subjectIdCard;
    private Integer oldSubjectId;//旧的收益主体id,安装工转让，更新收益信息使用
    private String addresseeProvince; //收货人省
    private String addresseeCity;     //收货人市
    private String addresseeRegion;   //收货人区
}