package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 开票查询实体
 *
 * @author hhf
 * @date 2019/1/16
 */
@Data
@ApiModel(description = "开票查询实体")
public class OrderInvoiceQueryDTO implements Serializable {

    private static final long serialVersionUID = 8644081627971893922L;

    @ApiModelProperty(value = "订单号")
    private Long orderId;

    @ApiModelProperty(value = "产品类目")
    private Integer productCategoryId;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String region;

    @ApiModelProperty(value = "开票状态：1-未申請 2-已申请")
    private Integer invoiceState;

    @ApiModelProperty(value = "计费方式")
    private Integer costId;

    @ApiModelProperty(value = "下单开始时间")
    private Date orderBeginTime;

    @ApiModelProperty(value = "下单结束时间")
    private Date orderEndTime;

    @ApiModelProperty(value = "订单完成开始时间")
    private Date orderCompleteBeginTime;

    @ApiModelProperty(value = "订单完成结束时间")
    private Date orderCompleteEndTime;

    @ApiModelProperty(value = "申请开票开始时间")
    private Date applyBeginTime;

    @ApiModelProperty(value = "申请开票结束时间")
    private Date applyEndTime;

    @ApiModelProperty(value = "产品公司ID")
    private Integer productCompanyId;

    @ApiModelProperty(value = "开票状态")
    private Integer applyStatus;
    
    private String completeTime;
    
    private Integer pageSize;

    @Override
    public String toString() {
        return "OrderInvoiceQueryDTO{" +
                "orderId=" + orderId +
                ", productCategoryId=" + productCategoryId +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", invoiceState=" + invoiceState +
                ", costId=" + costId +
                ", orderBeginTime=" + orderBeginTime +
                ", orderEndTime=" + orderEndTime +
                ", orderCompleteBeginTime=" + orderCompleteBeginTime +
                ", orderCompleteEndTime=" + orderCompleteEndTime +
                ", applyBeginTime=" + applyBeginTime +
                ", applyEndTime=" + applyEndTime +
                ", productCompanyId=" + productCompanyId +
                ", applyStatus=" + applyStatus +
                '}';
    }
}
