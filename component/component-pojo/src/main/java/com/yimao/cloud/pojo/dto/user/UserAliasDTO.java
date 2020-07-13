package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户星级
 *
 * @author hhf
 * @date 2019/4/20
 */
@Getter
@Setter
@ApiModel(description = "用户星级")
public class UserAliasDTO {
    @ApiModelProperty(position = 1,value = "健康大使ID")
    private Integer userId;
    @ApiModelProperty(position = 2,value = "客户ID")
    private Integer clientId;
    @ApiModelProperty(position = 3,value = "客户别名")
    private String clientName;
    @ApiModelProperty(position = 4,value = "几星")
    private Integer starNum;
    @ApiModelProperty(position = 5,value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 6,value = "更新时间")
    private Date updateTime;
}
