package com.yimao.cloud.pojo.dto.cms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 *  评论点赞
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@ApiModel(description = "评论点赞")
public class CommentSupportDTO implements Serializable {
    private static final long serialVersionUID = -4755874464973800380L;
    //ID
    @ApiModelProperty(value ="ID")
    private Long id;
    //评论id
    @ApiModelProperty(value ="评论id")
    private Long commentId;
    //用户id
    @ApiModelProperty(value ="用户id")
    private Long userId;
    //点赞时间
    @ApiModelProperty(value ="点赞时间")
    private Date supportTime;
    //是否取消
    @ApiModelProperty(value ="是否取消")
    private Object cancel;

}