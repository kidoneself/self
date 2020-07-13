package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 对账查询信息
 * @author: yu chunlei
 * @create: 2019-05-10 10:01:24
 **/
@Getter
@Setter
@ApiModel(description = "对账查询信息")
public class ReconQueryDTO implements Serializable {

    private static final long serialVersionUID = 1047037857925571843L;
    @ApiModelProperty(value = "主订单号")
    private Long mainOrderId;
    @ApiModelProperty(value = "订单号")
    private Long id;
    @ApiModelProperty(value = "云平台工单号")
    private String refer;
    @ApiModelProperty(value = "订单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；")
    private Integer terminal;
    @ApiModelProperty(value = "订单状态")
    private Integer status;
    @ApiModelProperty(value = "用户等级：0-体验版经销商；1-微创版经销商；2-个人版经销商；3-分享用户；4-普通用户；5-企业版经销商（主）；6-企业版经销商（子）；7-分销用户；")
    private Integer userType;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "商品类型（大类）:1-实物商品；2-电子卡券；3-租赁商品；对应product表的type字段")
    private Integer productType;
    @ApiModelProperty(value = "产品型号")
    private String productModel;
    @ApiModelProperty(value = "计费方式名称(型号范围)")
    private String costName;           //计费方式名称
    @ApiModelProperty(value = "产品公司名称")
    private String productCompanyName;
    @ApiModelProperty(value = "产品数量")
    private Integer count;
    @ApiModelProperty(value = "订单金额")
    private BigDecimal fee;
    @ApiModelProperty(value = "订单支付单号，第三方支付流水号")
    private String tradeNo;
    @ApiModelProperty(value = "支付时间",example = "2018-12-28 11:00:00")
    private Date payTime;
    @ApiModelProperty(value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;

}
