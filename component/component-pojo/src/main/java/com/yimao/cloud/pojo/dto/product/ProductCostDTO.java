package com.yimao.cloud.pojo.dto.product;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 计费模版
 *
 * @auther: Liu Yi
 * @date: 2018/12/25
 */
@Getter
@Setter
public class ProductCostDTO {

    @ApiModelProperty(value = "计费方式ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "计费方式名称")
    private String name;
    @ApiModelProperty(position = 2, value = "计费方式：1-流量计费；2-时长计费；")
    private Integer type;
    @ApiModelProperty(position = 3, value = "套餐流量或者套餐天数")
    private Integer value;
    @ApiModelProperty(position = 4, value = "单价（流量单位为升；包年单位为天）")
    private BigDecimal unitPrice;
    @ApiModelProperty(position = 5, value = "租凭费")
    private BigDecimal rentalFee;
    @ApiModelProperty(position = 6, value = "安装费（开户费）")
    private BigDecimal installationFee;
    @ApiModelProperty(position = 7, value = "商品总价格")
    private BigDecimal totalFee;
    @ApiModelProperty(position = 8, value = "产品二级类目ID（后台类目）")
    private Integer productCategoryId;
    @ApiModelProperty(position = 9, value = "阀值1")
    private Integer threshold1;
    @ApiModelProperty(position = 9, value = "阀值2")
    private Integer threshold2;
    @ApiModelProperty(position = 9, value = "阀值3")
    private Integer threshold3;
    @ApiModelProperty(position = 11, value = "删除标识：0-未删除；1-已删除；")
    private Boolean deleted;
    @ApiModelProperty(position = 12, value = "排序")
    private Integer sorts;

    @ApiModelProperty(position = 13, value = "模板类型：1-首年 2-续费")
    private Integer modelType;
    @ApiModelProperty(position = 14, value = "收益模板名id（续费才有）")
    private Integer incomeRuleId;
    @ApiModelProperty(position = 15, value = "收益模板名称（续费才有）")
    private String incomeRuleName;

    @ApiModelProperty(position = 16, value = "续费套餐对应的计费方式描述（PAD端续费展示用到）")
    private String renewRemark;

    @ApiModelProperty(position = 100, value = "创建人")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新人")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(position = 104, value = "老的计费方式id")
    private String oldId;


}