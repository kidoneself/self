// package com.yimao.cloud.pojo.dto.order;
//
// import io.swagger.annotations.ApiModel;
// import io.swagger.annotations.ApiModelProperty;
// import lombok.Getter;
// import lombok.Setter;
//
// /**
//  * 描述：支付宝支付下单实体类
//  *
//  * @Author Zhang Bo
//  * @Date 2019/9/6
//  */
// @ApiModel(description = "支付宝支付下单实体类")
// @Getter
// @Setter
// public class AlipayOrderParamsDTO {
//
//     @ApiModelProperty(position = 1, value = "商户网站唯一订单号")
//     private String outTradeNo;
//     @ApiModelProperty(position = 2, value = "订单总金额，单位为元，精确到小数点后两位")
//     private Double totalAmount;
//     @ApiModelProperty(position = 3, value = "商品名称。")
//     private String body;
//     @ApiModelProperty(position = 4, value = "商品的标题/交易标题/订单标题/订单关键字等。", hidden = true)
//     private String subject;
//     @ApiModelProperty(position = 5, value = "支付宝服务器主动通知商户服务器里指定的页面http/https路径。", hidden = true)
//     private String notifyUrl;
//     @ApiModelProperty(position = 6, value = "1-商品订单 2-经销商订单", hidden = true)
//     private Integer orderType;
//
// }
