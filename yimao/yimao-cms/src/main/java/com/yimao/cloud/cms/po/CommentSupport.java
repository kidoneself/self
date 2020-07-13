package com.yimao.cloud.cms.po;

import java.util.Date;
import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * 评论点赞
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@Table(name = "t_comment_support")
public class CommentSupport {
    //ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //评论id
    private Long commentId;
    //用户id
    private Long userId;
    //点赞时间
    private Date supportTime;
    //是否取消
    private Object cancel;

}