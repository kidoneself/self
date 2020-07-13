package com.yimao.cloud.pojo.dto.hra;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 体检劵生命周期
 *
 * @author liuhao@yimaokeji.com
 * @date 2019-04-10
 */
@Getter
@Setter
@ToString
@ApiModel(description = "体检劵生命周期")
public class HraTicketLifeCycleDTO implements Serializable {

    @ApiModelProperty(position = 1, value = "用户e家号")
    private Integer id;
    @ApiModelProperty(position = 2, value = "用户昵称")
    private String nickName;
    @ApiModelProperty(position = 3, value = "用户头像")
    private String headImg;
    @ApiModelProperty(position = 4, value = "时间")
    private Date time;
    @ApiModelProperty(position = 5, value = "状态 1-领取 2-支付  3-使用")
    private Integer status;
    @ApiModelProperty(position = 6, value = "赠送时间")
    private Date handselTime;
    @ApiModelProperty(position = 7, value = "过期标记 0-未过期 1-在规定时间内领取 2-已过期 3-赠送过程中体检卡劵过期")
    private Integer expiredFlag;

    @ApiModelProperty(position = 11, value = "接收者id")
    private Integer destUserId;


}
