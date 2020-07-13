package com.yimao.cloud.pojo.dto.cms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * 内容和 视频分类
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@ApiModel(description = "内容和视频的分类信息")
@Getter
@Setter
public class CmsCategoryDTO implements Serializable {

    private static final long serialVersionUID = -40040759026674072L;

    @ApiModelProperty(position = 1, value ="ID")
    private Integer id;
    @ApiModelProperty(position = 2, value ="分类名称")
    private String name;
    @ApiModelProperty(position = 3, value ="父id")
    private Integer parentId;
    @ApiModelProperty(position = 4, value ="分类等级")
    private Integer level;
    @ApiModelProperty(position = 5, value ="类别类型：2 总部文章分类，3 服务站视频分类，4 服务站文章分类")
    private Integer type;
    @ApiModelProperty(position = 6, value ="状态 1 有效 2 无效")
    private Integer status;
    @ApiModelProperty(position = 7, value = "端 1 经销商app 2 微信公众号 ")
    private Integer platform;
    @ApiModelProperty(position = 8, value = "前端展示的位置：1.资讯 2.公告 3.协议")
    private Integer location;
    @ApiModelProperty(position = 9, value ="排序")
    private Integer sorts;
    @ApiModelProperty(position = 10, value ="创建时间")
    private Date createTime;
    @ApiModelProperty(position = 11, value ="修改时间")
    private Date updateTime;

    @ApiModelProperty(position = 20, value = "保存表示1 是批量保存多1级  2 单个保存一个2级分类")
    private Integer saveFlag;
    @ApiModelProperty(position = 21, value = "展示端多个,隔开 保存多个一级分类的时候使用")
    private Integer[] platformStr;
}
