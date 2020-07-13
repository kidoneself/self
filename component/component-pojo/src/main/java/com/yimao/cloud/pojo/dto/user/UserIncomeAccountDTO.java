package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 收益账户(默认经销商)
 *
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/4/12
 */
@Setter
@Getter
@ToString
@ApiModel(description = "收益账户")
public class UserIncomeAccountDTO implements Serializable {

    @ApiModelProperty(position = 1, value = "收益账户ID")
    private Integer id;
    @ApiModelProperty(position = 2, value = "用户Id")
    private Integer userId;
    @ApiModelProperty(position = 3, value = "经销商id")
    private Integer distributorId;
    @ApiModelProperty(position = 4, value = "经销商账户")
    private String account;
    @ApiModelProperty(position = 5, value = "创建人")
    private String creator;
    @ApiModelProperty(position = 6, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 7, value = "修改人")
    private String updater;
    @ApiModelProperty(position = 8, value = "修改时间")
    private Date updateTime;
}
