package com.yimao.cloud.pojo.query.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Long Long jie
 * @date 2020-3-18
 */
@ApiModel(description = "建议反馈查询条件")
@Getter
@Setter
public class SuggestQuery {

    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区")
    private String region;
    @ApiModelProperty(value = "反馈人id")
    private Integer userId;
    @ApiModelProperty(value = "反馈人")
    private String name;
    @ApiModelProperty(value = "回复人")
    private String replier;
    @ApiModelProperty(value = "展示端")
    private Integer terminal;
    @ApiModelProperty(value = "建议类型")
    private Integer suggestType;
    @ApiModelProperty(value = "回复状态")
    private Integer status;
    @ApiModelProperty(value = "反馈时间（始）", example = "2019-12-28 11:00:00")
    private Date startTime;
    @ApiModelProperty(value = "反馈时间（终）", example = "2019-12-28 11:00:00")
    private Date endTime;
    @ApiModelProperty(value = "回复时间（始）", example = "2019-12-28 11:00:00")
    private Date startReplyTime;
    @ApiModelProperty(value = "回复时间（终）", example = "2019-12-28 11:00:00")
    private Date endReplyTime;

    @ApiModelProperty(value = "时间排序 true-正序，false-逆序")
    private Boolean sort;

    @ApiModelProperty(value = "已回复列表时间排序区分 1-反馈时间 2-回复时间")
    private Integer sortType;

}
