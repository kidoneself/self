package com.yimao.cloud.pojo.vo.station;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.UserCompanyApplyDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Liu Long Jie
 * @date 2020-1-16
 */


@Getter
@Setter
@ApiModel(description = "经销商订单VO(站务系统)")
public class DistributorOrderVO implements Serializable {

    private static final long serialVersionUID = 2430339047478416754L;

    @ApiModelProperty(value = "订单Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @ApiModelProperty(value = "订单来源/0-H5页面、1-经销商app、2-翼猫业务系统")
    private Integer orderSouce;
    @ApiModelProperty(value = "订单类型/0-注册、1-升级、2-续费")
    private Integer orderType;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "经销商类型名称")
    private String roleName;
    @ApiModelProperty(value = "升级经销商类型名称")
    private String destRoleName;
    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区")
    private String region;
    @ApiModelProperty(value = "经销商账号（推荐人）")
    private String referee;
    @ApiModelProperty(value = "价格")
    private BigDecimal price;
    @ApiModelProperty(value = "支付状态:1-未支付，2-已完成(支付成功)， 3-支付失败 4-待审核")
    private Integer payState;
    @ApiModelProperty(value = "支付方式/支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;
    @ApiModelProperty(value = "支付时间")
    private Date payTime;
    @ApiModelProperty(value = "订单状态/0-待审核、1-已完成、2-待付款")
    private Integer orderState;
    @ApiModelProperty(value = "财务审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核")
    private Integer financialState;
    @ApiModelProperty(value = "企业审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核")
    private Integer enterpriseState;
    @ApiModelProperty(value = "完成时间")
    private Date completionTime;
    @ApiModelProperty(value = "升级剩余有效期")
    private Integer periodValidity;
}
