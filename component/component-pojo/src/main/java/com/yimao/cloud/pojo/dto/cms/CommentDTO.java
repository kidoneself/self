package com.yimao.cloud.pojo.dto.cms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * 评论
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@ApiModel(description = "评论")
public class CommentDTO implements Serializable {

    private static final long serialVersionUID = -1678035696311829970L;
    //ID
    @ApiModelProperty(value ="ID")
    private Long id;
    //评论资源类型1 视频评论 2 文章评论
    @ApiModelProperty(value ="评论资源类型1 视频评论 2 文章评论")
    private Long type;
    //资讯id
    @ApiModelProperty(value ="资讯id")
    private Long refId;
    //用户id
    @ApiModelProperty(value ="用户id")
    private Long userId;
    //用户名称
    @ApiModelProperty(value ="用户名称")
    private String nickName;
    //评论内容
    @ApiModelProperty(value ="评论内容")
    private String commentText;
    //评论时间
    @ApiModelProperty(value ="评论时间")
    private Date commentTime;
    //是否为最热
    @ApiModelProperty(value ="是否为最热")
    private Object hot;
    //是否隐藏 1 不隐藏 2 隐藏
    @ApiModelProperty(value ="是否隐藏 1 不隐藏 2 隐藏")
    private Object hidden;
    //支持人数(点赞数量)
    @ApiModelProperty(value ="支持人数(点赞数量)")
    private Long supportCount;

}