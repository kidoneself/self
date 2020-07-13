package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description  审核记录详情
 * @author: yu chunlei
 * @create: 2019-08-28 10:40:15
 **/
@Data
public class ExamineRecordDTO implements Serializable {
    @ApiModelProperty(value = "审核记录id")
    private Long id;

    @ApiModelProperty(value = "售后单号")
    private Long salesId;

    @ApiModelProperty(value = "工单号")
    private String refer;

    @ApiModelProperty(value = "订单号")
    private Long orderId;

    @ApiModelProperty(value = "主订单号")
    private Long mainOrderId;

    @ApiModelProperty(value = "售后申请端：1-终端app；2-微信公众号；3-经销商APP；4-小程序；5-翼猫业务系统；")
    private Integer salesTerminal;

    @ApiModelProperty(value = "申请数量")
    private Integer num;

    @ApiModelProperty(value = "申请时间")
    private Date createTime;

    @ApiModelProperty(value = "申请原因")
    private String refundReason;

    //审核信息
    @ApiModelProperty(value = "审核人")
    private String creator;

    @ApiModelProperty(value = "审核处理时间")
    private Date handleTime;

    @ApiModelProperty(value = "审核状态:0、审核不通过，1、审核通过")
    private Boolean operationStatus;

    @ApiModelProperty(value = "审核不通过原因")
    private String auditReason;

    @ApiModelProperty(value = "详细描述")
    private String detailReason;


}
