package com.yimao.cloud.pojo.vo.out;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/***
 * 功能描述:工程师客户工单实体类
 *
 * @auther: liu yi
 * @date: 2019/7/3 17:58
 */
@Getter
@Setter
@ApiModel(description = "工程师客户工单实体类")
public class EngineerCustomerWorkOrderVO implements Serializable {
    private static final long serialVersionUID = -4829011919890027993L;
    @ApiModelProperty(position = 1, value = "工单id")
    private String id;
    @ApiModelProperty(position = 2, value = "批次码")
    private String deviceBatchCode;
    @ApiModelProperty(position = 3, value = "型号")
    private String deviceModelName;
    @ApiModelProperty(position = 4, value = "计费类型名称")
    private String chargingName;
    @ApiModelProperty(position = 5, value = "客户名称")
    private String consumerName;
    @ApiModelProperty(position = 6, value = "客户手机号")
    private String consumerPhone;
    @ApiModelProperty(position = 7, value = "设备id")
    private Integer deviceId;
    @ApiModelProperty(position = 8, value = "sn")
    private String deviceSncode;
    @ApiModelProperty(position = 9, value = "SIM")
    private String deviceSimcard;
    @ApiModelProperty(position = 10, value = "安装工手机号")
    private String engineerPhone;
    @ApiModelProperty(position = 11, value = "安装工程师")
    private String engineerName;
    @ApiModelProperty(position = 12, value = "安装时间")
    private Long batchTime;
    @ApiModelProperty(position = 13, value = "省")
    private String addrProvice;
    @ApiModelProperty(position = 14, value = "市")
    private String addrCity;
    @ApiModelProperty(position = 15, value = "区")
    private String addrRegion;
    @ApiModelProperty(position = 16, value = "地址")
    private String address;
}
