package com.yimao.cloud.pojo.vo.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 计费方式VO对象
 *
 * @auther: Zhang Bo
 * @date: 2019/3/15
 */
@Getter
@Setter
@ApiModel(description = "计费方式VO对象")
public class ProductCostVO {

    @ApiModelProperty(value = "计费方式ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "计费方式名称")
    private String name;                      //计费方式名称
    @ApiModelProperty(position = 2, value = "计费方式：1-流量计费；2-时长计费；")
    private Integer type;                     //计费方式：1-流量计费；2-时长计费；
    @ApiModelProperty(position = 3, value = "套餐流量或者套餐天数")
    private Integer value;                    //套餐流量或者套餐天数
    @ApiModelProperty(position = 4, value = "单价（流量单位为升；包年单位为天）")
    private BigDecimal unitPrice;             //单价（流量单位为升；包年单位为天）
    @ApiModelProperty(position = 5, value = "租凭费")
    private BigDecimal rentalFee;             //租凭费
    @ApiModelProperty(position = 6, value = "安装费（开户费）")
    private BigDecimal installationFee;       //安装费（开户费）
    @ApiModelProperty(position = 7, value = "商品总价格")
    private BigDecimal totalFee;              //商品总价格
    @ApiModelProperty(position = 8, value = "产品类目ID（后台类目）")
    private Integer productCategoryId;        //产品类目ID（后台类目）
    @ApiModelProperty(position = 9, value = "产品类目等级（后台类目）")
    private String productFirstCategoryName;  //产品类目名称（后台类目）
    @ApiModelProperty(position = 10, value = "产品类目等级（后台类目）")
    private String productSecondCategoryName; //产品类目名称（后台类目）
    @ApiModelProperty(position = 11, value = "删除标识：0-未删除；1-已删除；")
    private Boolean deleted;                  //删除标识：0-未删除；1-已删除；
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
    private String creator;                //创建人
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;               //创建时间
    @ApiModelProperty(position = 102, value = "更新人")
    private String updater;                //更新人
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;               //更新时间
}