package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 租赁商品导出实体类
 * @author: yu chunlei
 * @create: 2019-08-26 09:19:17
 **/
@Data
public class RentalGoodsExportDTO implements Serializable {

    @ApiModelProperty(value = "售后单号")
    private Long salesId;

    @ApiModelProperty(value = "工单号")
    private String refer;

    @ApiModelProperty(value = "订单号")
    private Long orderId;

    @ApiModelProperty(value = "主订单号")
    private Long mainOrderId;

    @ApiModelProperty( value = "订单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；")
    private String terminal;


    @ApiModelProperty(value = "产品类目名称")
    private String productCategoryName;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "收件人")
    private String addresseeName;

    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;

    @ApiModelProperty(value = "经销商姓名")
    private String distributorName;

    @ApiModelProperty(value = "安装工姓名")
    private String engineerName;

    @ApiModelProperty(value = "售后申请端：1-终端app；2-微信公众号；3-经销商APP；4-小程序；5-翼猫业务系统；")
    private String salesTerminal;

    @ApiModelProperty(value = "申请数量")
    private Integer num;

    @ApiModelProperty(value = "申请时间",example = "2018-12-28 11:00:00")
    private String createTime;


    @ApiModelProperty(value = "售后状态")
    private String status;

    @ApiModelProperty(value = "审核方")
    private String auditee;

    @ApiModelProperty(value = "物资审核人")
    private String buyer;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "备注")
    private Date accountTime;

    @ApiModelProperty(value = "操作状态:0、审核不通过，1、审核通过")
    private String operationStatus;

    @ApiModelProperty(value = "审核处理时间")
    private String handleTime;

    @ApiModelProperty(value = "审核不通过原因")
    private String auditReason;

    @ApiModelProperty(value = "取消原因")
    private String cancelReason;



}
