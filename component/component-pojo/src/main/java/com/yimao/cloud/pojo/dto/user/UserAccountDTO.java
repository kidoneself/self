package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-07-19 11:24:18
 **/
@Data
public class UserAccountDTO implements Serializable {
    @ApiModelProperty(position = 1, value = "用户e家号")
    private Integer userId;
    @ApiModelProperty(position = 2, value = "账号")
    private String userName;
    @ApiModelProperty(position = 3, value = "头像")
    private String headImg;

    @ApiModelProperty(position = 4, value = "手机号")
    private String mobile;

    @ApiModelProperty(position = 5, value = "经销商角色")
    private String roleName;

    @ApiModelProperty(position = 6, value = "经销商id")
    private Integer id;


}
