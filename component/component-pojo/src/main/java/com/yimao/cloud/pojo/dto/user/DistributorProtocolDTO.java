package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/1/2
 */

@Data
@ApiModel(description = "合同签署")
public class DistributorProtocolDTO {

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "订单号")
    private Long orderId;

    @ApiModelProperty(value = "订单类型/0-注册、1-升级、2-续费")
    private Integer orderType;

    @ApiModelProperty(value = "经销商id")
    private Integer distributorId;

    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;

    @ApiModelProperty(value = "合同标题名称")
    private String title;

    @ApiModelProperty(value = "订单号")
    private Integer roleId;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String region;

    @ApiModelProperty(value = "服务站联系人")
    private String linkMan;

    @ApiModelProperty(value = "服务站联系电话")
    private String linkPhone;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "合同状态 0-未完成，1-已完成")
    private Integer state;

    @ApiModelProperty(value = "合同地址")
    private String contract;

    @ApiModelProperty(value = "翼猫签署状态")
    private Integer ymSignState;

    @ApiModelProperty(value = "翼猫签署时间")
    private Date ymSignTime;

    @ApiModelProperty(value = "服务站签署状态")
    private Integer stationSignState;

    @ApiModelProperty(value = "服务站签署时间")
    private Date stationSignTime;

    @ApiModelProperty(value = "用户签署状态")
    private Integer userSignState;

    @ApiModelProperty(value = "用户签署时间")
    private Date userSignTime;

    @ApiModelProperty(value = "服务站复核状态")
    private Integer stationRenewState;

    @ApiModelProperty(value = "服务站复核时间")
    private Date stationRenewTime;

}
