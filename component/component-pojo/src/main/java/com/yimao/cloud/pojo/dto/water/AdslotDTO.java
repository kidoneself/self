package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 广告位
 *
 * @author Chen Hui Yang
 * @date 2019/2/15
 */
@Data
@ApiModel(description = "广告位")
public class AdslotDTO implements Serializable {

    private static final long serialVersionUID = 1573836755485035727L;

    @ApiModelProperty(position = 1,value = "id")
    private Integer id;
    @ApiModelProperty(position = 2, value = "广告位ID")
    private String adslotId;
    @ApiModelProperty(position = 3, value = "广告位名称")
    private String name;
    @ApiModelProperty(position = 5, value = "广告时长（单位：秒）")
    private Integer duration;
    @ApiModelProperty(position = 6, value = "禁用状态：0-启用；1-禁用")
    private Boolean forbidden;
    @ApiModelProperty(position = 7, value = "删除状态：0-未删除；1-删除")
    private Boolean deleted;
    @ApiModelProperty(position = 8, value = "屏幕位置：1-大屏；2-小屏")
    private Integer position;

    @ApiModelProperty(position = 100, value = "创建者")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新者")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;
}
