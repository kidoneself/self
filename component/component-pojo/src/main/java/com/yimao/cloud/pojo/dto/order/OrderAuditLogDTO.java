package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 审核记录实体类
 * @author: yu chunlei
 * @create: 2019-08-23 17:19:00
 **/
@Data
public class OrderAuditLogDTO implements Serializable {

    @ApiModelProperty(value = "审核ID")
    private Long id;
    @ApiModelProperty(value = "售后单号")
    private Long salesId;
    @ApiModelProperty(value = "订单号")
    private Long orderId;
    @ApiModelProperty(value = "审核类型  1.取消订单退款（未收货），2.申请退货退款（已收货），3：提现")
    private Integer type;
    @ApiModelProperty(value = "子审核类型  1-业务部门审核，2-400客服审核（租赁商品），3-400客服提交物流，4-物资审核")
    private Integer subType;
    @ApiModelProperty(value = "操作状态:0、审核不通过，1、审核通过")
    private Boolean operationStatus;
    @ApiModelProperty(value = "操作状态名")
    private String operation;
    @ApiModelProperty(value = "菜单名")
    private String menuName;
    @ApiModelProperty(value = "操作IP")
    private String ip;
    @ApiModelProperty(value = "操作人")
    private String creator;
    @ApiModelProperty(value = "操作时间")
    private Date createTime;
    @ApiModelProperty(value = "审核不通过原因")
    private String auditReason;
    @ApiModelProperty(value = "详情描述")
    private String detailReason;


    @ApiModelProperty(value = "主订单号")
    private Long mainOrderId;
    @ApiModelProperty(value = "售后申请时间")
    private Date afterSaleCreateTime;
    @ApiModelProperty(value = "申请原因")
    private String refundReason;
    @ApiModelProperty(value = "工单号")
    private String refer;
    @ApiModelProperty(value = "取消原因")
    private String cancelReason;


}
