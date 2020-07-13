// package com.yimao.cloud.pojo.dto.order;
//
// import io.swagger.annotations.ApiModel;
// import io.swagger.annotations.ApiModelProperty;
// import lombok.Getter;
// import lombok.Setter;
//
// import java.io.Serializable;
//
// /**
//  * 统一下单请求对象
//  *
//  * @author Zhang Bo
//  * @date 2017/12/3.
//  */
// @ApiModel(description = "微信统一下单实体类")
// @Getter
// @Setter
// public class UnifiedOrderDTO implements Serializable {
//
//     private static final long serialVersionUID = -7540476841448566358L;
//
//     @ApiModelProperty(position = 1, value = "微信支付分配的公众账号ID（企业号corpid即为此appId）", hidden = true)
//     private String appid;
//     @ApiModelProperty(position = 2, value = "微信支付分配的商户号", hidden = true)
//     private String mch_id;
//     @ApiModelProperty(position = 3, value = "商品描述", hidden = true)
//     private String body;
//     @ApiModelProperty(position = 4, value = "附加数据(说明)", hidden = true)
//     private String attach;
//     @ApiModelProperty(position = 5, value = "商户（翼猫）的支付单号（主订单号）")
//     private String out_trade_no;
//     @ApiModelProperty(position = 6, value = "订单总金额")
//     private Double total_fee;
//     @ApiModelProperty(position = 7, value = "终端IP（用户）", hidden = true)
//     private String spbill_create_ip;
//     /**
//      * JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付，统一下单接口trade_type的传参可参考这里
//      * MICROPAY--刷卡支付，刷卡支付有单独的支付接口，不调用统一下单接口
//      */
//     @ApiModelProperty(position = 8, value = "支付类型 JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付", hidden = true)
//     private String trade_type;
//     @ApiModelProperty(position = 9, value = "查询用户openid所需的授权码", hidden = true)
//     private String code;
//     @ApiModelProperty(position = 10, value = "openId")
//     private String openId;
//     @ApiModelProperty(position = 11, value = "该字段常用于线下活动时的场景信息上报，支持上报实际门店信息，商户也可以按需求自己上报相关信息。该字段为JSON对象数据。", hidden = true)
//     private String scene_info;
//
//     @ApiModelProperty(position = 12, value = "自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传WEB", hidden = true)
//     private String device_info;
//     @ApiModelProperty(position = 13, value = "是否有主订单：false-没有；true-有", hidden = true)
//     private Boolean mainOrderFlag = true;
//     @ApiModelProperty(position = 14, value = "异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。", hidden = true)
//     private String notify_url;
//     @ApiModelProperty(position = 15, value = "是否签名：false-否；true-是", hidden = true)
//     private Boolean signFlag = false;
//     @ApiModelProperty(position = 16, value = "签名密钥", hidden = true)
//     private String signKey;
//     @ApiModelProperty(position = 17, value = "1-商品订单 2-经销商订单 3-续费订单", hidden = true)
//     private Integer orderType;
//
// }
