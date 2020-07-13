package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/***
 * 功能描述:消息记录
 *
 * @auther: liu yi
 * @date: 2019/5/5 15:22
 */
@Getter
@Setter
@ApiModel(description = "消息记录")
public class MessagePushDTO implements Serializable {
    private static final long serialVersionUID = 1356226789L;
    @ApiModelProperty(value = "主键ID")
    private Integer id;
    @ApiModelProperty(value = "设备sn码")
    private String sncode;
    @ApiModelProperty(value = "设备Id")
    private String deviceId;
    @ApiModelProperty(value = "工单id")
    private String workorderId;
    @ApiModelProperty(value = "工单类型:1-安装工单 2-维修工单 3-维护工单")
    private Integer workorderType;
    @ApiModelProperty(value = "推送类型:10-系统推送 1-阈值提醒,2-续费推送,3-报警推送,4-创建账号,5-新工单分配,6-工单完成,7-审核通过,8-审核不通过,9-经销商续配额", required = true)
    private Integer pushType;
    @ApiModelProperty(value = "频次过滤类型：1-余额不足类型推送 2-制水故障类型推送 3-TDS异常故障类型推送 4-PP棉滤芯过期故障类型推送  5-CTO棉滤芯过期故障类型推送 6- UDF棉滤芯过期故障类型推送 7-T33棉滤芯过期故障类型推送")
    private Integer filterType;
    @ApiModelProperty(value = "消息标题")
    private String title;
    @ApiModelProperty(value = "消息内容", required = true)
    private String content;
    @ApiModelProperty(value = "点击通知打开方式:1-打开APP 2-打开网页", required = true)
    private Integer clickNotice;
    @ApiModelProperty(value = "链接地址")
    private String address;
    @ApiModelProperty(value = "设备范围： 0-所有设备 1-Android设备 2-IOS设备", required = true)
    private Integer devices;
    @ApiModelProperty(value = "创建时间", required = true)
    private Date createTime;
    @ApiModelProperty(value = "接收者ID")
    private Integer receiverId;
    @ApiModelProperty(value = "接收者名称")
    private String receiver;
    @ApiModelProperty(value = "推送者")
    private String sender;
    @ApiModelProperty(value = "是否删除：0-未删除；1-已删除", hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "是否已读：0-未读；1-已读", hidden = true)
    private Integer readStatus;
    @ApiModelProperty(value = "推送方式：1-推送给安装工 2-推送给经销商")
    private Integer app;
    @ApiModelProperty(value = "0-MSG_TYPE_RADIO；1-MSG_TYPE_NOTICE；", required = true)
    private Integer msgType;
}
