package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019-08-05
 */
@Getter
@Setter
public class YMAPPPushDTO {


    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "内容")
    private String content;
    @ApiModelProperty(value = "推送用户id")
    private Integer account;
    @ApiModelProperty(value = "推送时间")
    private Date pushTime;
    @ApiModelProperty(value = "推送状态 0-失败，1-成功")
    private Boolean status;
    @ApiModelProperty(value = "0-未读，1-已读")
    private Boolean hasRead;
    @ApiModelProperty(value = "0-删除，1-未删除")
    private Boolean deleted;

}
