package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业审核导出信息
 *
 * @author liulongjie
 * @date 2019/8/26
 */
@Data
public class CompanyAuditExportDTO{

    @ApiModelProperty(value = "订单号")
    private Long orderId;

    @ApiModelProperty(value = "地区")
    private String address;

    @ApiModelProperty(value = "订单类型")
    private String orderTypeStr;

    @ApiModelProperty(value = "经销商账户")
    private String distributorAccount;

    @ApiModelProperty(value = "经销商姓名")
    private String realName;

    @ApiModelProperty(value = "经销商类型")
    private String roleName;

    @ApiModelProperty(value = "升级经销商类型")
    private String destRoleName;

    @ApiModelProperty(value = "性别：1-男；2-女；")
    private String sexStr;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "推荐人姓名")
    private String recommendName;

    @ApiModelProperty(value = "服务站公司名称")
    private String stationCompanyName;

    @ApiModelProperty(value = "支付方式/0-支付宝、1-微信、2-pos机、4-转账")
    private String payTypeStr;

    @ApiModelProperty(value = "支付状态")
    private String payStateStr;

    @ApiModelProperty(value = "支付时间")
    private String payTimeStr;

    @ApiModelProperty(value = "支付的金额")
    private BigDecimal payMoney;

    @ApiModelProperty(value = "订单状态/0-待审核、1-已完成、2-待付款")
    private String orderStateStr;

    @ApiModelProperty(value = "企业类型")
    private String companyTypeStr;

    @ApiModelProperty(value = "企业行业")
    private String industry;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "联系电话")
    private String companyPhone;

    @ApiModelProperty(value = "公司邮箱")
    private String companyEmail;

    @ApiModelProperty("公司地址")
    private String companyAddress;

    @ApiModelProperty("统一信用代码")
    private String creditCode;

    @ApiModelProperty("税务信息")
    private String taxInformation;

    @ApiModelProperty("法人代表")
    private String corporateRepresentative;

    @ApiModelProperty("开户账号")
    private String bankAccount;

    @ApiModelProperty("开户银行")
    private String bank;

    @ApiModelProperty("营业执照照片")
    private String businessLicense;

    @ApiModelProperty(value = "企业审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核")
    private String enterpriseStateStr;
}
