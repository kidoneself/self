package com.yimao.cloud.cms.po;

import com.yimao.cloud.pojo.dto.cms.CommentDTO;
import java.util.Date;
import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * 评论
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@Table(name = "t_comment")
public class Comment {
    //ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //评论资源类型1 视频评论 2 文章评论
    private Long type;
    //资讯id
    private Long refId;
    //用户id
    private Long userId;
    //用户名称
    private String nickName;
    //评论内容
    private String commentText;
    //评论时间
    private Date commentTime;
    //是否为最热
    private Object hot;
    //是否隐藏 1 不隐藏 2 隐藏
    private Object hidden;
    //支持人数(点赞数量)
    private Long supportCount;


    public Comment() {
    }

    /**
     * 用业务对象CommentDTO初始化数据库对象Comment。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Comment(CommentDTO dto) {
        this.id = dto.getId();
        this.type = dto.getType();
        this.refId = dto.getRefId();
        this.userId = dto.getUserId();
        this.nickName = dto.getNickName();
        this.commentText = dto.getCommentText();
        this.commentTime = dto.getCommentTime();
        this.hot = dto.getHot();
        this.hidden = dto.getHidden();
        this.supportCount = dto.getSupportCount();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象CommentDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(CommentDTO dto) {
        dto.setId(this.id);
        dto.setType(this.type);
        dto.setRefId(this.refId);
        dto.setUserId(this.userId);
        dto.setNickName(this.nickName);
        dto.setCommentText(this.commentText);
        dto.setCommentTime(this.commentTime);
        dto.setHot(this.hot);
        dto.setHidden(this.hidden);
        dto.setSupportCount(this.supportCount);
    }
}