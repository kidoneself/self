package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.google.common.base.Objects;

/**
 * @author Liu Yi
 * @description 经销商订单导出
 * @date 2019/8/26 14:17
 */

@Data
@ApiModel(description = "经销商订单导出")
public class DistributorOrderExportDTO implements Serializable {

    private static final long serialVersionUID = 2430339047489701753L;
    @ApiModelProperty(value = "订单Id")
    private Long id;
    @ApiModelProperty(value = "订单类型/0-注册、1-升级、2-续费")
    private Integer orderType;
    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(value = "经销商姓名")
    private String name;
    @ApiModelProperty(value = "经销商类型/50体验版、350微创版、650个人版、950企业版")
    private Integer roleLevel;
    @ApiModelProperty(value = "升级经销商类型/50体验版、350微创版、650个人版、950企业版")
    private Integer destRoleLevel;
    @ApiModelProperty(value = "性别：1-男，2-女")
    private Integer sex;
    @ApiModelProperty(value = "身份证号")
    private String idCard;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "推荐人姓名")
    private String recommendName;
    @ApiModelProperty(value = "经销商推荐人身份证号")
    private String recommendIdCard;
    @ApiModelProperty(value = "支付方式/0-支付宝、1-微信、2-pos机、4-转账")
    private Integer payType;
    @ApiModelProperty(value = "支付状态/0-未支付、1-已支付、3-待审核")
    private Integer payState;
    @ApiModelProperty(value = "支付时间")
    private String payTime;
    @ApiModelProperty(value = "价格")
    private BigDecimal price;
    @ApiModelProperty(value = "订单状态/0-待审核、1-已完成、2-待付款")
    private Integer orderState;
    @ApiModelProperty(value = "用户签署状态/0-未签署 、1-已经签署")
    private Integer userSignState;
    @ApiModelProperty(value = "服务站合同签署状态/0-未签署 、1-已经签署")
    private Integer stationSignState;
    @ApiModelProperty(value = "翼猫合同签署状态/0-未签署、1-已经签署")
    private Integer ymSignState;
    @ApiModelProperty(value = "财务审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核")
    private Integer financialState;
    @ApiModelProperty(value = "财务审核人")
    private String financialName;
    @ApiModelProperty(value = "财务审核时间")
    private String financialTime;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "支付流水号/线上付款的流水号")
    private String tradeNo;
    @ApiModelProperty(value = "订单来源/0-H5页面、1-经销商app、2-翼猫业务系统")
    private Integer orderSouce;
    @ApiModelProperty(value = "企业审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核")
    private Integer enterpriseState;
    @ApiModelProperty(value = "企业审核人")
    private String enterpriseUser;
    @ApiModelProperty(value = "企业审核时间")
    private String enterpriseTime;
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;
    @ApiModelProperty(value = "完成时间")
    private String completionTime;
    @ApiModelProperty(value = "地区")
    private String area;
    @ApiModelProperty(value = "服务站公司名称")
    private String stationCompanyName;
    @ApiModelProperty(value = "财务审核次数")
    private Integer financialCount;
    @ApiModelProperty(value = "是否创建合同")
    private String isCreateProtocol;

    @ApiModelProperty(value = "推荐人区域")
    private String recommendArea;

    public Integer getFinancialCount() {
        if (Objects.equal(null, financialCount)) {
            return 1;
        }
        return financialCount;
    }

    public String getOrderType() {
        String result = "";
        if (Objects.equal(null, orderType)) {
            return result;
        }
        switch (orderType) {
            case 0:
                result = "注册订单";
                break;
            case 1:
                result = "升级订单";
                break;
            case 2:
                result = "续费订单";
                break;
            default:
                break;
        }
        return result;

    }

    public String getRoleLevel() {
        String result = "";
        if (Objects.equal(null, roleLevel)) {
            return result;
        }
        switch (roleLevel) {
            case -50:
                result = "折机版经销商";
                break;
            case 50:
                result = "体验版经销商";
                break;
            case 350:
                result = "微创版经销商";
                break;
            case 650:
                result = "个人版经销商";
                break;
            case 950:
                result = "企业版经销商";
                break;
            default:
                break;
        }
        return result;

    }


    public String getDestRoleLevel() {
        String result = "";
        if (Objects.equal(null, destRoleLevel)) {
            return result;
        }
        switch (destRoleLevel) {
            case 50:
                result = "体验版经销商";
                break;
            case 350:
                result = "微创版经销商";
                break;
            case 650:
                result = "个人版经销商";
                break;
            case 950:
                result = "企业版经销商";
                break;
            default:
                break;
        }
        return result;

    }

    public String getSex() {
        String result = "";
        if (Objects.equal(null, sex)) {
            return result;
        }
        switch (sex) {
            case 1:
                result = "男";
                break;
            case 2:
                result = "女";
                break;

            default:
                break;
        }
        return result;

    }

    public String getPayType() {
        String result = "";
        if (Objects.equal(null, payType)) {
            return result;
        }
        switch (payType) {
            case 1:
                result = "微信";
                break;
            case 2:
                result = "支付宝";
                break;
            case 3:
                result = "pos机";
                break;
            case 4:
                result = "转账";
                break;
            default:
                break;
        }
        return result;

    }

    public String getPayState() {
        String result = "";
        if (Objects.equal(null, payState)) {
            return result;
        }
        switch (payState) {
            case 1:
                result = "未支付";
                break;
            case 2:
                result = "已支付";
                break;
            case 3:
                result = "支付失败";
                break;
            case 4:
                result = "待审核";
                break;
            case 6:
                result = "无需支付";
                break;
            default:
                break;
        }
        return result;

    }

    public String getOrderState() {
        String result = "";
        if (Objects.equal(null, orderState)) {
            return result;
        }
        switch (orderState) {
            case 0:
                result = "待审核";
                break;
            case 1:
                result = "已完成";
                break;
            case 2:
                result = "待付款";
                break;
            case 3:
                result = "已关闭";
                break;
            default:
                break;
        }
        return result;

    }

    public String getUserSignState() {
        String result = "";
        if (Objects.equal(null, userSignState)) {
            return result;
        }
        switch (userSignState) {
            case 0:
                result = "未签署";
                break;
            case 1:
                result = "已经签署";
                break;

            default:
                break;
        }
        return result;

    }

    public String getStationSignState() {
        String result = "";
        if (Objects.equal(null, stationSignState)) {
            return result;
        }
        switch (stationSignState) {
            case 0:
                result = "未签署";
                break;
            case 1:
                result = "已经签署";
                break;

            default:
                break;
        }
        return result;

    }

    public String getYmSignState() {
        String result = "";
        if (Objects.equal(null, ymSignState)) {
            return result;
        }
        switch (ymSignState) {
            case 0:
                result = "未签署";
                break;
            case 1:
                result = "已经签署";
                break;

            default:
                break;
        }
        return result;

    }

    public String getFinancialState() {
        String result = "";
        if (Objects.equal(null, financialState)) {
            return result;
        }

        switch (financialState) {
            case 0:
                result = "未审核";
                break;
            case 1:
                result = "审核通过";
                break;
            case 2:
                result = "审核不通过";
                break;
            case 3:
                result = "无需审核";
                break;
            default:
                break;
        }
        return result;

    }

    public String getOrderSouce() {
        String result = "";
        if (Objects.equal(null, orderSouce)) {
            return result;
        }
        switch (orderSouce) {
            case 0:
                result = "H5页面";
                break;
            case 1:
                result = "经销商app";
                break;
            case 2:
                result = "翼猫业务系统";
                break;
            default:
                break;
        }
        return result;

    }

    public String getEnterpriseState() {
        String result = "";
        if (Objects.equal(null, enterpriseState)) {
            return result;
        }

        switch (enterpriseState) {
            case 0:
                result = "未审核";
                break;
            case 1:
                result = "审核通过";
                break;
            case 2:
                result = "审核不通过";
                break;
            case 3:
                result = "无需审核";
                break;
            default:
                break;
        }
        return result;

    }


}
