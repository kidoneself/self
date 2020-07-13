package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/***
 * 功能描述:消息记录
 *
 * @auther: liu yi
 * @date: 2019/5/5 15:22
 */
@Getter
@Setter
@ApiModel(description = "消息记录导出")
public class MessagePushExportDTO implements Serializable {
    private static final long serialVersionUID = 1356226789L;

    @ApiModelProperty(value = "消息标题")
    private String title;
    @ApiModelProperty(value = "消息内容")
    private String content;
    @ApiModelProperty( value = "设备类型名称")
    private String deviceTypeStr;
    @ApiModelProperty(value = "创建时间")
    private String createTimeStr;
}
