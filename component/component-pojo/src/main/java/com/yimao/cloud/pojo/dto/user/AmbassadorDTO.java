package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * @description: 健康大使信息
 * @author: yu chunlei
 * @create: 2018-12-18 14:06:07
 **/
@Data
public class AmbassadorDTO implements Serializable {

    private static final long serialVersionUID = -5940786373361720619L;
    @ApiModelProperty(value = "健康大使e家号")
    private Integer ambassadorId;
    @ApiModelProperty(value = "健康大使姓名")
    private String ambassadorName;
    @ApiModelProperty(value = "健康大使昵称")
    private String ambassadorNickName;
    @ApiModelProperty(value = "健康大使身份")
    private Integer userType;
    @ApiModelProperty(value = "健康大使手机号")
    private String ambassadorMobile;
}
