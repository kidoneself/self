package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/27
 */
@Setter
@Getter
@ApiModel(description = "消息记录")
public class MessageRecordDTO implements Serializable {

    @ApiModelProperty(value = "消息ID")
    private Integer id;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "接收消息的openid")
    private String openid;
    @ApiModelProperty(value = "接收消息的用户id")
    private Integer userId;
    @ApiModelProperty(value = "内容id")
    private Long contentId;
    @ApiModelProperty(value = "类型 1-手机短信 2-推送消息")
    private Integer type;
    @ApiModelProperty(value = "订单号")
    private Long orderId;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
