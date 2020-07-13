// package com.yimao.cloud.pojo.dto.order;
//
// import io.swagger.annotations.ApiModel;
// import io.swagger.annotations.ApiModelProperty;
// import lombok.Getter;
// import lombok.Setter;
//
// import java.math.BigDecimal;
//
// /**
//  * @author zhilin.he
//  * @description 返回前端消息信息
//  * @date 2019/8/13 15:50
//  **/
// @Getter
// @Setter
// @ApiModel(description = "返回前端消息信息对象")
// public class ResultDTO {
//
//     @ApiModelProperty(position = 1, value = "消息提示类型：1、静态字符串提示；2、动态数据提示；3、弹出框提示；")
//     private Integer msgType;
//     @ApiModelProperty(position = 3, value = "产品起订量")
//     private Integer count;
//     @ApiModelProperty(value = "主订单号")
//     private Long id;
//     @ApiModelProperty(value = "订单总金额")
//     private BigDecimal orderAmountFee;//订单总金额
//     @ApiModelProperty(value = "消息")
//     private String msg;
//     @ApiModelProperty(value = "子订单号")
//     private Long subOrderId;
//
// }
