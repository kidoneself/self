package com.yimao.cloud.pojo.vo.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：订单对账前端展示实体类。
 *
 * @Author Zhang Bo
 * @Date 2019/5/29
 */
@Getter
@Setter
public class OrderBillVO {

    @ApiModelProperty(value = "主订单号")
    private Long mainOrderId;

    @ApiModelProperty(value = "子订单号")
    private Long id;

    @ApiModelProperty(value = "订单来源")
    private String terminal;

    @ApiModelProperty(value = "订单状态")
    private String status;

    @ApiModelProperty(value = "产品公司")
    private String productCompanyName;

    @ApiModelProperty(value = "产品类目")
    private String cascadeBackstageCategoryName;

    @ApiModelProperty(value = "产品数量")
    private Integer count;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal fee;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "第三方支付流水号")
    private String tradeNo;

    @ApiModelProperty(value = "下单用户id")
    private Integer userId;

    @ApiModelProperty(value = "工单号")
    private String refer;

    @ApiModelProperty(value = "下单时间")
    private Date createTime;

    @ApiModelProperty(value = "产品型号")
    private String productCategoryName;

    @ApiModelProperty(value = "用户身份")
    private String userTypeName;

    @ApiModelProperty(value = "销售主体省市区")
    private String salesSubjectPCR;

    @ApiModelProperty(value = "销售主体公司名称")
    private String salesSubjectCompanyName;

    @ApiModelProperty(value = "收款说明")
    private String feeDescription;

}
