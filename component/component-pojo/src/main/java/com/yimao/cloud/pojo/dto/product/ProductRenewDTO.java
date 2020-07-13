package com.yimao.cloud.pojo.dto.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品续费
 *
 * @author Liu Yi
 * @date 2018/11/29.
 */
@Data
@ApiModel(description = "商品续费")
public class ProductRenewDTO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "续费商品ID")
    private Integer id;

    @ApiModelProperty(value = "续费商品名称")
    private String name;

    @ApiModelProperty(value = "后台类目名称")
    private String categoryName;

    @ApiModelProperty(value = "产品分类id")
    private Integer categoryId;

    @ApiModelProperty(value = "计费方式模版id")
    private Integer costId;

    @ApiModelProperty(value = "删除标识")
    private Boolean deleted;

   /* @ApiModelProperty(value = "计费类型：1-流量计费；2-包年计费；3-其它")
    private String code;*/

    @ApiModelProperty("计费类型：1-流量计费；2-包年计费；3-其它")
    private Integer type;

    @ApiModelProperty(value = "流量单价（针对流量计费类型）")
    private BigDecimal price;

    @ApiModelProperty(value = "流量（针对流量计费类型）")
    private BigDecimal flowRate;

    @ApiModelProperty(value = "模版展示价格")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "租凭费")
    private BigDecimal rentalFee;

    @ApiModelProperty(value = "安装费")
    private BigDecimal installationFee;

    @ApiModelProperty(value = "有效期类型：0-没有有效期；3-年；2-月；1-天（针对包年计费类型）")
    private Integer timeLimitType;

    @ApiModelProperty(value = "有效期长度（针对包年计费类型）")
    private Integer timeLimitNum;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updater;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;
    @ApiModelProperty("分类ids ，隔开")
    private String categoryIds;
    @ApiModelProperty("价格模板ids ")
    private String costIds;
    @ApiModelProperty("排序")
    private Integer sorts;
    @ApiModelProperty("上级分类id")
    private Integer parentCategoryId;


}
