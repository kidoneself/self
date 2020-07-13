package com.yimao.cloud.pojo.query.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Liu Yi
 * @date 2019/8/19
 */

@Getter
@Setter
@ApiModel(description = "合同签署查询条件")
public class DistributorProtocolQueryDTO {

    @ApiModelProperty(value = "订单号")
    private Integer orderId;

    @ApiModelProperty(value = "订单类型/0-注册、1-升级、2-续费")
    private Integer orderType;

    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;

    @ApiModelProperty(value = "经销商角色id")
    private Integer roleId;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String region;

    @ApiModelProperty(value = "合同创建状态")
    private Integer state;

    @ApiModelProperty(value = "翼猫签署状态")
    private Integer ymSignState;

    @ApiModelProperty(value = "服务站签署状态")
    private Integer stationSignState;

    @ApiModelProperty(value = "用户签署状态")
    private Integer userSignState;

    @ApiModelProperty(value = "服务站复核状态")
    private Integer stationRenewState;
}
