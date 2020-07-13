package com.yimao.cloud.pojo.dto.order;

import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@EqualsAndHashCode
@Getter
@Setter
@ApiModel(description = "购物车DTO对象")
public class ShopCartDTO extends BaseOrder implements Serializable {

    private static final long serialVersionUID = 3046967051302735153L;

    @ApiModelProperty(position = 1, value = "购物车ID")
    private Integer id;
    @ApiModelProperty(position = 2, value = "用户ID")
    private Integer userId;
//    @ApiModelProperty(position = 3, required = true, value = "购物车类型：1-普通购物车；2-经销购物车；3-站长购物车；4-特批水机购物车；5-特供产品购物车；")
//    private Integer type;
    @ApiModelProperty(position = 6,value = "产品前台一级类目ID")
    private Integer productCategoryId;
    @ApiModelProperty(position = 6, value = "产品前台一级类目名称")
    private String productCategoryName;

    @ApiModelProperty(position = 7, value = "产品图片")
    private String productImage;
    @ApiModelProperty(position = 8, value = "产品计费方式列表")
    private List<ProductCostDTO> costList;

    @ApiModelProperty(position = 20, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 21, value = "创建时间")
    private Date updateTime;

    @ApiModelProperty(position = 22, value = "货运方式：1-包邮  2-货到付款 null-包邮")
    private Integer transportType;
    @ApiModelProperty(position = 30, value = "商品是否有效，false-正常 true-失效")
    private Boolean invalid;
    @ApiModelProperty(position = 31, value = "商品失效理由")
    private String invalidReason;
}
