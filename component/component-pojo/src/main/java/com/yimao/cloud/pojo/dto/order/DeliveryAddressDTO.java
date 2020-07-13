package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/1/28
 */
@Data
public class DeliveryAddressDTO implements Serializable {


    @ApiModelProperty(value = "主键")
    private Integer id;
    @ApiModelProperty(value = "联系人")
    private String contact;
    @ApiModelProperty(value = "联系电话")
    private String mobile;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区")
    private String region;
    @ApiModelProperty(value = "联系地址")
    private String street;
    @ApiModelProperty(value = "公司名称")
    private String company;
    @ApiModelProperty(value = "是否默认发货地址")
    private Integer hasDelivery;
    @ApiModelProperty(value = "是否默认退货地址")
    private Integer hasRefund;
    @ApiModelProperty(value = "创建人")
    private String creator;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "创建时间")
    private String updater;
    @ApiModelProperty(value = "更新人")
    private Date updateTime;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "操作人ID")
    private Integer operatorId;
}
