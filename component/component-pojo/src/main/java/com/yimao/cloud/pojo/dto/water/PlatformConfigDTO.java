package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：下发给客户端的广告平台信息
 *
 * @Author Zhang Bo
 * @Date 2019/2/20 17:34
 * @Version 1.0
 */
@Data
@ApiModel(description = "下发给客户端的广告平台信息")
public class PlatformConfigDTO implements Serializable {

    private static final long serialVersionUID = -3928510746684693316L;

    @ApiModelProperty(value = "平台id")
    private Integer id;
    @ApiModelProperty(position = 1, value = "平台名称")
    private String name;
    @ApiModelProperty(position = 2, value = "媒体ID，标识资源方，平台生成")
    private String appId;
    @ApiModelProperty(position = 3, value = "API版本，按照当前接入所参照的API文档版本赋值，影响所有后续逻辑，填写错误会导致拒绝请求。")
    private String apiVersion;
    @ApiModelProperty(position = 4, value = "第三方广告请求地址")
    private String url;

}
