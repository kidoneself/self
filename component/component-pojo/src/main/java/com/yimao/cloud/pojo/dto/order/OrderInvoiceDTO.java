package com.yimao.cloud.pojo.dto.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票
 *
 * @author hhf
 * @date 2019/1/16 
 */
@Data
@ApiModel(description = "发票")
public class OrderInvoiceDTO implements Serializable {

    private static final long serialVersionUID = -513525131321145715L;

    private Integer id;

    @ApiModelProperty(value = "主订单")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long mainOrderId;              

    @ApiModelProperty(value = "子订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;                  

    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    @ApiModelProperty(value = "老用户ID")
    private String oldUserId;

    @ApiModelProperty(value = "产品ID")
    private Integer productId;             

    @ApiModelProperty(value = "产品名称")
    private String productName;            

    @ApiModelProperty(value = "开票状态：0-未申请  2-已申请")
    private Integer applyStatus;           

    @ApiModelProperty(value = "申请开票时间")
    private Date applyTime;                

    @ApiModelProperty(value = "发票类型1、普票；2、增票")//售后是0-普票；1-专票 需要做转换
    private Integer invoiceType;           

    @ApiModelProperty(value = "发票抬头:1、公司发票；2、个人发票")
    private Integer invoiceHead;           

    @ApiModelProperty(value = "公司名称")
    private String companyName;            

    @ApiModelProperty(value = "开户号")
    private String bankAccount;            

    @ApiModelProperty(value = "开户行")
    private String bankName;               

    @ApiModelProperty(value = "税号")
    private String dutyNo;                 

    @ApiModelProperty(value = "公司地址")
    private String companyAddress;         

    @ApiModelProperty(value = "公司电话")
    private String companyPhone;           

    @ApiModelProperty(value = "申请人名称")
    private String applyUser;              

    @ApiModelProperty(value = "申请用户手机号")
    private String applyPhone;             

    @ApiModelProperty(value = "申请用户邮箱")
    private String applyEmail;             

    @ApiModelProperty(value = "申请人地址")
    private String applyAddress;

    @ApiModelProperty(value = "付费方式")
    private String costName;

    @ApiModelProperty(value = "数量")
    private Integer num;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "下单时间")
    private Date orderTime;

    @ApiModelProperty(value = "订单完成时间")
    private Date orderCompleteTime;

    @ApiModelProperty(value = "订单状态")
    private Integer orderStatus;

    @ApiModelProperty(value = "订单类型：普通订单  续费订单")
    private String orderType;

    @ApiModelProperty(value = "订单类型：1.普通订单  2.续费订单")
    private Integer type;

    @ApiModelProperty(value = "续费单号")
    private String renewId;

    @ApiModelProperty(value = "确认时间")
    private Date confirmTime;
    @ApiModelProperty(value = "来源：1-安装工 app;2-健康e家公众号")
    private Integer sourceType;

    @ApiModelProperty(value = "产品类目")
    private String productCategoryName;
    @ApiModelProperty(value = "区域")
    private String area;

}
