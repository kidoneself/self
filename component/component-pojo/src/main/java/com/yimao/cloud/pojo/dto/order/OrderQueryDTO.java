// package com.yimao.cloud.pojo.dto.order;
//
// import io.swagger.annotations.ApiModel;
// import io.swagger.annotations.ApiModelProperty;
// import lombok.Getter;
// import lombok.Setter;
//
// @ApiModel(description = "微信订单查询实体类")
// @Getter
// @Setter
// public class OrderQueryDTO {
//
//     @ApiModelProperty(position = 1, value = "商户（翼猫）的支付单号（主订单号）")
//     private Long mainOrderId;
//
//     @ApiModelProperty(position = 4, value = "支付类型 JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付", hidden = true)
//     private String trade_type;
//     @ApiModelProperty(position = 5, value = "是否签名：false-否；true-是", hidden = true)
//     private Boolean signFlag = false;
//
// }
