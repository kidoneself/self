package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Chen Hui Yang
 * @date 2018/12/26
 * 订单发货单
 */
@Data
@ApiModel(description = "订单发货单")
public class OrderDeliveryDTO {

    @ApiModelProperty(value = "发货单ID")
    private Long id;
    @ApiModelProperty(value = "子订单ID")
    private Long orderId;
    @ApiModelProperty(value = "物流单号")
    private String logisticsNo;
    @ApiModelProperty(value = "物流公司")
    private String logisticsCompany;
    @ApiModelProperty(value = "物流公司编码")
    private String logisticsCompanyCode;
    @ApiModelProperty(value = "物流费用")
    private Integer logisticsFee;
    @ApiModelProperty(value = "发货件数")
    private Integer num;
    @ApiModelProperty(value = "发货数量/盒")
    private Integer boxNum;
    @ApiModelProperty(value = "重量")
    private Integer weigh;
    @ApiModelProperty(value = "发货时间")
    private Date deliveryTime;
    @ApiModelProperty(value = "寄付方式")
    private String payType;
    @ApiModelProperty(value = "发货人联系方式")
    private String deliveryPhone;
    @ApiModelProperty(value = "寄件公司")
    private String sendingCompany;
    @ApiModelProperty(value = "寄件人")
    private String sender;
    @ApiModelProperty(value = "寄件人所属省")
    private String senderProvince;
    @ApiModelProperty(value = "寄件人所属市")
    private String senderCity;
    @ApiModelProperty(value = "寄件人所属区")
    private String senderRegion;
    @ApiModelProperty(value = "寄件人所属街道")
    private String senderStreet;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "创建人")
    private String creator;


    @Override
    public boolean equals(Object o) {
        return o instanceof OrderDeliveryDTO && this.orderId.equals(((OrderDeliveryDTO) o).orderId);
    }
}
