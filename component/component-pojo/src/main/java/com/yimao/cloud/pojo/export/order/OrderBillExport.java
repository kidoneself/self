package com.yimao.cloud.pojo.export.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 描述：订单对账导出实体类。
 *
 * @Author Zhang Bo
 * @Date 2019/5/29
 */
@Getter
@Setter
public class OrderBillExport {

    @ApiModelProperty(value = "主订单号")
    private Long mainOrderId;

    @ApiModelProperty(value = "子订单号")
    private Long id;

    @ApiModelProperty(value = "工单号")
    private String refer;

    @ApiModelProperty(value = "订单来源")
    private String terminal;

    @ApiModelProperty(value = "下单用户id")
    private Integer userId;

    @ApiModelProperty(value = "下单用户身份")
    private String userTypeName;

    @ApiModelProperty(value = "产品类型（后台一级类目）")
    private String productFirstCategoryName;

    @ApiModelProperty(value = "产品范围（后台二级类目）")
    private String productSecondCategoryName;

    @ApiModelProperty(value = "产品型号（后台三级类目）")
    private String productThirdCategoryName;

    @ApiModelProperty(value = "产品公司")
    private String productCompanyName;

    @ApiModelProperty(value = "计费方式")
    private String costName;

    @ApiModelProperty(value = "产品数量")
    private Integer count;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal fee;

    @ApiModelProperty(value = "第三方支付流水号")
    private String tradeNo;

    @ApiModelProperty(value = "支付时间")
    private String payTime;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "下单时间")
    private String createTime;

    @ApiModelProperty(value = "订单状态")
    private String status;

    @ApiModelProperty(value = "经销商ID")
    private Integer distributorId;

    @ApiModelProperty(value = "经销商身份")
    private String distributorTypeName;

    @ApiModelProperty(value = "经销商姓名")
    private String distributorName;

    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;

    @ApiModelProperty(value = "经销商所属省")
    private String distributorProvince;

    @ApiModelProperty(value = "经销商所属市")
    private String distributorCity;

    @ApiModelProperty(value = "经销商所属区")
    private String distributorRegion;

    @ApiModelProperty(value = "是否有子账号")
    private String hasSubDistributor;

    @ApiModelProperty(value = "子账号")
    private String subDistributorAccount;

    @ApiModelProperty(value = "子账号姓名")
    private String subDistributorName;

    @ApiModelProperty(value = "推荐人姓名")
    private String recommendName;

    @ApiModelProperty(value = "推荐人账号")
    private String recommendAccount;

    @ApiModelProperty(value = "推荐人所属省")
    private String recommendProvince;

    @ApiModelProperty(value = "推荐人所属市")
    private String recommendCity;

    @ApiModelProperty(value = "推荐人所属区")
    private String recommendRegion;

    @ApiModelProperty(value = "销售主体省市区")
    private String salesSubjectPCR;

    @ApiModelProperty(value = "销售主体公司名称")
    private String salesSubjectCompanyName;

    @ApiModelProperty(value = "收款说明")
    private String feeDescription;

}
