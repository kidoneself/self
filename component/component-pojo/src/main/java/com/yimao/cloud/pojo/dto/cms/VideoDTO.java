package com.yimao.cloud.pojo.dto.cms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/21
 */
@Data
@ApiModel(description = "视频DTO")
public class VideoDTO implements Serializable {

    @ApiModelProperty(value = "视频id", position = 1)
    private Integer liveId;
    @ApiModelProperty(value = "频道（视频）名称", position = 2)
    private String name;
    @ApiModelProperty(value = "状态：1-直播中，2-回放，3-视频，4-预告", position = 3)
    private Integer liveStatus;
    @ApiModelProperty(value = "是否为推荐视频（0-否，1-是）", position = 4)
    private Boolean recommend;
    @ApiModelProperty(value = "一级视频分类ID", position = 5)
    private Integer liveTypeId;
    @ApiModelProperty(value = "二级视频分类ID", position = 6)
    private Integer liveTypeSubId;
    @ApiModelProperty(value = "观看次数（虚拟）", position = 7)
    private Integer watchTimes;
    @ApiModelProperty(value = "观看次数（真实）", position = 8)
    private Integer realWatchTimes;
    @ApiModelProperty(value = "移动端观看地址", position = 9)
    private String mobileUrl;
    @ApiModelProperty(value = "频道图标地址", position = 10)
    private String coverImage;
    @ApiModelProperty(value = "删除标识（0-未删除，1-已删除）", position = 11)
    private Boolean deleteFlag;
    @ApiModelProperty(value = "创建时间", position = 12)
    private Date createTime;
    @ApiModelProperty(value = "更新时间", position = 13)
    private Date updateTime;
    @ApiModelProperty(value = "最近一次发布时间", position = 22)
    private Date releaseTime;
    @ApiModelProperty(value = "排序", position = 14)
    private Integer sorts;
    @ApiModelProperty(value = "点赞数", position = 15)
    private Integer likeNum;
    @ApiModelProperty(value = "分享数", position = 16)
    private Integer shareNum;
    @ApiModelProperty(value = "分享标题", position = 17)
    private String shareContent;
    @ApiModelProperty(value = "详情图", position = 18)
    private String detailImg;

    @ApiModelProperty(value = "是否点赞", position = 19)
    private Boolean hasLikes;
    @ApiModelProperty(value = "视频类型", position = 20)
    private VideoTypeDTO videoType;

    @ApiModelProperty(value = "是否发布 0-未 1-已", position = 21)
    private Integer status;
}
