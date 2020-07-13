package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import java.util.Date;

/**
 * 站务系统 回复用户的建议反馈
 *
 */
@Getter
@Setter
@ApiModel(description = "站务系统 回复用户的建议反馈")
public class ReplyDTO {

    @ApiModelProperty(value = "主键id")
    private Integer id;
    @ApiModelProperty(value = "意见表主键id")
    private Integer suggestId;
    @ApiModelProperty(value = "回复内容")
    private String replyContent;
    @ApiModelProperty(value = "附件")
    private String accessory;
    @ApiModelProperty(value = "回复人")
    private String replier;
    @ApiModelProperty(value = "回复时间")
    private Date time;
}
