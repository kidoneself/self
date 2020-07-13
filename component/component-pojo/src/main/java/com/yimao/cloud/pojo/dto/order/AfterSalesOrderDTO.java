package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AfterSalesOrderDTO implements Serializable {

    private static final long serialVersionUID = 6634821802266553082L;
    //订单信息
    @ApiModelProperty(value = "售后订单")
    private Long id;
    @ApiModelProperty(value = "工单号")
    private String refer;
    @ApiModelProperty(value = "子订单")
    private Long orderId;
    @ApiModelProperty(value = "主订单")
    private Long mainOrderId;
    @ApiModelProperty(value = "产品图片")
    private String productImg;
    @ApiModelProperty(value = "产品一级类目")
    private String productOneCategoryName;
    @ApiModelProperty(value = "产品二级类目")
    private String productTwoCategoryName;
    @ApiModelProperty(value = "产品三级类目")
    private String productCategoryName;
    @ApiModelProperty(value = "产品数量")
    private Integer productNum;
    @ApiModelProperty(value = "订单来源")
    private String orderResource;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "经销商Id")
    private Integer distributorId;
    @ApiModelProperty(value = "经销商")
    private String distributorName;
    @ApiModelProperty(value = "安装工")
    private String engineerName;
    @ApiModelProperty(value = "取消原因")
    private String cancelReason;

    //收货信息
    @ApiModelProperty(value = "收货人")
    private String userName;
    @ApiModelProperty(value = "收货人联系方式")
    private String addresseePhone;
    @ApiModelProperty(value = "收件人省")
    private String addresseeProvince;
    @ApiModelProperty(value = "收件人市")
    private String addresseeCity;
    @ApiModelProperty(value = "收件人区")
    private String addresseeRegion;
    @ApiModelProperty(value = "收件人街道")
    private String addresseeStreet;

    //售后信息
    @ApiModelProperty(value = "售后发起端")
    private Integer terminal;
    @ApiModelProperty(value = "申请数量")
    private Integer num;
    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundFee;
    @ApiModelProperty(value = "实退金额")
    private BigDecimal realRefundFee;
    @ApiModelProperty(value = "手续费")
    private BigDecimal formalitiesFee;
    @ApiModelProperty(value = "申请退单时间")
    private Date createTime;
    @ApiModelProperty(value = "退款原因")
    private String refundReason;
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;
    @ApiModelProperty(value = "审核不通过原因")
    private String auditReason;
    @ApiModelProperty(value = "详细描述")
    private String detailReason;
    @ApiModelProperty(value = "状态:0-待审核(经销商)  1-待审核(总部)  2-待退货入库  3-待退款(财务)  4-售后失败 5-售后成功")
    private Integer status;


    @ApiModelProperty(value = "商品类型（大类）:1实物商品，2电子卡券，3租赁商品 对应产品:product: type")
    private Integer productType;           
    @ApiModelProperty(value = "产品id")
    private Integer productId;             
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "产品公司名称")
    private String productCompanyName;
    @ApiModelProperty(value = "用户身份")
    private Integer userType;              
    @ApiModelProperty(value = "支付类型")
    private Integer payType;               
    @ApiModelProperty(value = "支付金额")
    private BigDecimal payFee;
    @ApiModelProperty(value = "退款时间")
    private Date refundTime;                
    @ApiModelProperty(value = "退款流水号")
    private String refundTradeNo;          
    @ApiModelProperty(value = "退款到账时间")
    private Date accountTime;               
    @ApiModelProperty(value = "物流单号")
    private String logisticsNo;             
    @ApiModelProperty(value = "物流公司编号")
    private String logisticsCompanyNo;     
    @ApiModelProperty(value = "物流公司名称")
    private String logisticsCompanyName;   
    @ApiModelProperty(value = "收货确认时间")
    private Date receiveTime;
    @ApiModelProperty(value = "物资审核时间")
    private Date buyAuditTime;             
    @ApiModelProperty(value = "物资审核人")
    private String buyer;                    
    @ApiModelProperty(value = "财务审核时间")
    private Date financeTime;               
    @ApiModelProperty(value = "财务审核人")
    private String financer;                 
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "财务审核状态")
    private Integer financeAuditStatus;
    @ApiModelProperty(value = "财务审核原因")
    private String financeReason;
    @ApiModelProperty(value = "审核方:如：经销商、总部")
    private String auditee;
    @ApiModelProperty(value = "400客服审核状态或提交物流：1-审核通过；2-审核不通过（1-已提交物流，2-未提交）")
    private Integer customerServiceAuditStatus;


}
