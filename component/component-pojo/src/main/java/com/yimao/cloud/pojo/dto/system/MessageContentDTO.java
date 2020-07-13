package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/27
 */
@Setter
@Getter
@ApiModel(description = "消息记录")
public class MessageContentDTO implements Serializable {

    @ApiModelProperty(value = "内容ID")
    private Long id;
    @ApiModelProperty(value = "消息内容")
    private String content;
}
