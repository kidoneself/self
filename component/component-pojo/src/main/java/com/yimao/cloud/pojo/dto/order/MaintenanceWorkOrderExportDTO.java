package com.yimao.cloud.pojo.dto.order;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/***
 * 功能描述:维护工单实体类
 *
 * @auther: liu yi
 * @date: 2019/4/1 9:58
 */
@Getter
@Setter
@ApiModel(description = "维护工单")
public class MaintenanceWorkOrderExportDTO {
    @ApiModelProperty(value = "ID")
    private String id;
    @ApiModelProperty(value = "所需耗材名称")
    private String materielDetailName;
    @ApiModelProperty(value = "详细地址，用于搜索")
    private String addressDetail;
    @ApiModelProperty(value = " 完成类型")
    private String completeType;
    @ApiModelProperty(value = "用户名")
    private String consumerName;
    @ApiModelProperty(value = "用户电话")
    private String consumerPhone;
    @ApiModelProperty(value = "批次码")
    private String deviceBatchCode;
    @ApiModelProperty(value = "产品范围")
    private String kindName;
    @ApiModelProperty(value = "设备型号Id")
    private String deviceModel;
    @ApiModelProperty(value = "设备型号")//产品类型
    private String deviceModelName;
    @ApiModelProperty(value = "设备SN码")
    private String deviceSncode;
    @ApiModelProperty(value = "设备ICCID")
    private String deviceSimcard;
    @ApiModelProperty(value = "工单备注")
    private String workOrderRemark;
    @ApiModelProperty(value = "完成状态：Y-完成 ，N-未完成")
    private String workOrderCompleteStatus;
    @ApiModelProperty(value = "完成时间")
    private String workOrderCompleteTime;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "审核方式：1-自动审核 2-人工审核")
    private String auditType;
    @ApiModelProperty(value = "来源：1-自动生成 2-总部添加，默认自动生成")
    private String source;
}
