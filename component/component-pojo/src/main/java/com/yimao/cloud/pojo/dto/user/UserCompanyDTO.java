package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业
 *
 * @author hhf
 * @date 2018/12/20
 */
@Data
public class UserCompanyDTO implements Serializable {

    private static final long serialVersionUID = -2140191474828223515L;

    private Integer id;

    @ApiModelProperty(position = 1,value = "企业类型")
    private Integer companyType;

    @ApiModelProperty(position = 2,value = "所属行业")
    private String industry;

    @ApiModelProperty(position = 3,value = "企业名称")
    private String companyName;

    @ApiModelProperty(position = 4,value = "信用代码")
    private String creditCode;

    @ApiModelProperty(position = 5,value = "税务信息")
    private String taxInformation;

    @ApiModelProperty(position = 6,value = "法人代表")
    private String corporateRepresentative;

    @ApiModelProperty(position = 7,value = "银行账号")
    private String bankAccount;

    @ApiModelProperty(position = 8,value = "开户银行")
    private String bank;

    @ApiModelProperty(position = 9,value = "开设端口数量")
    private Integer portNumber;

    @ApiModelProperty(position = 10,value = "联系电话")
    private String phone;

    @ApiModelProperty(position = 11,value = "公司邮箱")
    private String email;

    @ApiModelProperty(position = 12,value = "公司地址")
    private String address;

    @ApiModelProperty(position = 13,value = "营业执照")
    private String businessLicense;

    @ApiModelProperty(position = 14,value = "简介")
    private String introduction;

    @ApiModelProperty(position = 15,value = "创建人")
    private String creator;

    @ApiModelProperty(position = 16,value = "创建时间")
    private Date createTime;

    @ApiModelProperty(position = 17,value = "更新人")
    private String updater;

    @ApiModelProperty(position = 18,value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(position = 19,value = "订单号")
    private Long orderId;

    @ApiModelProperty(position = 20,value = "经销商账号")
    private String distributorAccount;

    @ApiModelProperty(position = 21,value = "经销商类型")
    private String distributorType;

    @ApiModelProperty(position = 22,value = "企业审核状态")
    private Integer enterpriseState;

    @ApiModelProperty(position = 23,value = "企业审核人")
    private String auditor;

    @ApiModelProperty(position = 24,value = "审核时间")
    private Date auditTime;

    @ApiModelProperty(position = 25,value = "审核不通过原因")
    private String cause;

    @ApiModelProperty(position = 26,value = "支付状态")
    private String payState;

    @ApiModelProperty(position = 27,value = "订单类型")
    private String orderType;

    /**
     * 场景标签
     */
    @ApiModelProperty(position = 28,value = "场景标签")
    private String sceneTag;

    /**
     * 服务人数
     */
    @ApiModelProperty(position = 29,value = "服务人数")
    private String serviceNum;
    @ApiModelProperty(position = 30,value = "备注")
    private String remark;
    /**
     * 对应mongo库的id
     */
    @ApiModelProperty(position = 31,value = "对应mongo库的id")
    private String oldId;



}
