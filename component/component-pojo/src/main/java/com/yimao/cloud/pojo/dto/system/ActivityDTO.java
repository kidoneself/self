package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动
 *
 * @author liuhao@yimaokeji.com
 *         2018042018/4/27
 */
@Data
@ApiModel(description = "活动")
public class ActivityDTO implements Serializable {
    private static final long serialVersionUID = 5129618195464165576L;

    @ApiModelProperty(position = 1, value = "活动ID")
    private Integer id;
    @ApiModelProperty(position = 2, value = "标题")
    private String title;
    @ApiModelProperty(position = 3, value = "内容")
    private String content;
    @ApiModelProperty(position = 4, value = "url")
    private String acUrl;
    @ApiModelProperty(position = 5, value = "图片")
    private String acImg;
    @ApiModelProperty(position = 6, value = "活动类型  1：普通活动  2：京东兑换活动")
    private Integer acType;
    @ApiModelProperty(position = 7, value = "活动开始时间")
    private Date beginTime;
    @ApiModelProperty(position = 8, value = "活动结束时间")
    private Date endTime;
    @ApiModelProperty(position = 9, value = "排序")
    private Integer sorts;
    @ApiModelProperty(position = 10, value = "是否删除 1：删除  0 ：未删除")
    private Boolean deleteFlag;
    @ApiModelProperty(position = 11, value = "端 1-公众号 2-小程序")
    private Integer side;
    @ApiModelProperty(position = 12, value = "布局类型： 1-大图模式 2-小图模式")
    private Integer layoutType;
    @ApiModelProperty(position = 13, value = "背景图片 如果是兑换活动-用背景图")
    private String backImg;
    @ApiModelProperty(position = 14, value = "背景颜色")
    private String backColor;

    /**
     * 渠道 只有兑换活动才在后台设置渠道，通过渠道-生成对应渠道的体检卡样式，多个以逗号分隔
     */
    @ApiModelProperty(position = 15, value = "渠道")
    private String channel;
    @ApiModelProperty(position = 16, value = "推荐产品")
    private String recommend;

    @ApiModelProperty(position = 17, value = "创建者")
    public String creator;
    @ApiModelProperty(position = 18, value = "创建时间")
    public Date createTime;
    @ApiModelProperty(position = 19, value = "更新者")
    public String updater;
    @ApiModelProperty(position = 20, value = "更新时间")
    public Date updateTime;

}
