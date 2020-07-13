package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 公众号订单列表
 * @author: yu chunlei
 * @create: 2019-04-10 11:45:09
 **/
@Data
@ApiModel(description = "订单实体类")
public class WxOrderDTO implements Serializable {

    private Long id;
    @ApiModelProperty( value = "订单号")
    private Long mainOrderId;
    @ApiModelProperty( value = "数量")
    private Integer count;
    @ApiModelProperty( value = "状态")
    private Integer status;
    @ApiModelProperty( value = "金额")
    private BigDecimal fee;
    @ApiModelProperty( value = "支付方式  1:微信  2：支付宝 3：其他")
    private Integer payType;
    @ApiModelProperty( value = "是否支付：0、未支付；1、已支付")
    private Boolean pay;
    @ApiModelProperty( value = "支付时间")
    private Date payTime;
    @ApiModelProperty( value = "订单创建时间")
    private Date createTime;
    @ApiModelProperty( value = "订单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；")
    private Integer terminal;
    @ApiModelProperty( value = "产品活动类型：1 普通产品， 2 折机商品，3-180产品")
    private Integer activityType;
    @ApiModelProperty( value = "收货地址ID")
    private Integer addressId;
    @ApiModelProperty( value = "买家ID")
    private Integer userId;

    @ApiModelProperty( value = "商品ID")
    private Integer productId;
    @ApiModelProperty( value = "商品名称")
    private String productName;
    @ApiModelProperty( value = "商品图片")
    private String productImg;
    @ApiModelProperty( value = "产品型号")
    private String productModel;
    @ApiModelProperty( value = "活动方式  1：普通商品  2：活动商品")
    private Integer productActivityType;
    @ApiModelProperty( value = "详情")
    private String textDetail;
    @ApiModelProperty( value = "封面")
    private String coverImg;
    @ApiModelProperty( value = "价格")
    private BigDecimal price;
    @ApiModelProperty( value = "市场价格")
    private BigDecimal marketPrice;
    @ApiModelProperty( value = "开户费")
    private BigDecimal openAccountFee;

    @ApiModelProperty( value = "类目ID")
    private Integer categoryId;
    @ApiModelProperty( value = "类目名称")
    private String categoryName;

    @ApiModelProperty( value = "计费方式")
    private Integer costId;
    @ApiModelProperty( value = "计费方式名称")
    private String costName;

    @ApiModelProperty( value = "商品类型（大类）:1-实物商品；2-电子卡券；3-租赁商品；对应product表的type字段")
    private Integer productType;


}
