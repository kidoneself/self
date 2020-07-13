package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：翼猫APP客户端获取动态url
 *
 * @Author Zhang Bo
 * @Date 2019/11/12
 */
@ApiModel(description = "翼猫APP客户端获取动态url")
@Getter
@Setter
public class AppUrlDTO {

    @ApiModelProperty(position = 1, value = "接口访问域名")
    private String apiUrl;
    @ApiModelProperty(position = 2, value = "图片域名")
    private String imgUrl;
    @ApiModelProperty(position = 3, value = "微信分享域名")
    private String shareUrl;
    @ApiModelProperty(position = 4, value = "环境：test-测试；pro-生产")
    private String env;

}
