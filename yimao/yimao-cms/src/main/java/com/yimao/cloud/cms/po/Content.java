package com.yimao.cloud.cms.po;

import com.yimao.cloud.pojo.dto.cms.ContentDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 内容表，用于存放比如文章
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@Table(name = "t_content")
public class Content {
    //主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //标题
    private String title;
    //内容
    private String text;
    //摘要
    private String summary;
    //连接到(常用于谋文章只是一个连接)
    private String linkTo;
    //缩略图（多个用逗号分隔）
    private String thumbnail;
    //模型
    private String module;
    //样式：big-大图模式，single-单图模式，three-三图模式
    private String style;
    //用户ID
    private Integer userId;
    //作者
    private String author;
    //排序编号
    private Integer orderNumber;
    //是否删除：1已发布 2未发布，3已删除 ,
    private Integer status;
    //支持人数
    private Integer voteUp;
    //反对人数
    private Integer voteDown;
    //评分分数
    private Integer rate;
    //评分次数
    private Integer rateCount;
    //评论状态
    private Integer commentStatus;
    //评论总数
    private Integer commentCount;
    //最后评论时间
    private Date commentTime;
    //访问量
    private Integer viewCount;
    //创建日期
    private Date createTime;
    //最后更新日期
    private Date updateTime;
    //标签
    private String tag;
    //SEO关键字
    private String metaKeywords;
    //SEO描述信息
    private String metaDescription;
    //备注信息
    private String remarks;
    //是否置顶
    private Integer top;
    // 类型 1 总部业务内容，2 服务站内容
    private Integer type;
    //服务站ID
    private Integer serviceStationId;
    //资讯类型 1、普通文本内容，2、公告 带红头文件附件
    private Integer contentMode;
    //附件URL 用于公告pdf文件
    private String annexUrl;
    //点赞量
    private Integer likeCount;
    //分享量
    private Integer shareCount;
    //排序
    private Integer sorts;
    //位置：1资讯 2公号 3协议
    private Integer location;

    private String imgUrl;
    //最近一次发布日期
    private Date releaseTime;


    public Content() {
    }

    /**
     * 用业务对象ContentDTO初始化数据库对象Content。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Content(ContentDTO dto) {
        this.id = dto.getId();
        this.title = dto.getTitle();
        this.text = dto.getText();
        this.summary = dto.getSummary();
        this.linkTo = dto.getLinkTo();
        this.thumbnail = dto.getThumbnail();
        this.module = dto.getModule();
        this.style = dto.getStyle();
        this.userId = dto.getUserId();
        this.author = dto.getAuthor();
        this.orderNumber = dto.getOrderNumber();
        this.status = dto.getStatus();
        this.voteUp = dto.getVoteUp();
        this.voteDown = dto.getVoteDown();
        this.rate = dto.getRate();
        this.rateCount = dto.getRateCount();
        this.commentStatus = dto.getCommentStatus();
        this.commentCount = dto.getCommentCount();
        this.commentTime = dto.getCommentTime();
        this.viewCount = dto.getViewCount();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.tag = dto.getTag();
        this.metaKeywords = dto.getMetaKeywords();
        this.metaDescription = dto.getMetaDescription();
        this.remarks = dto.getRemarks();
        this.top = dto.getTop();
        this.type = dto.getType();
        this.serviceStationId = dto.getServiceStationId();
        this.contentMode = dto.getContentMode();
        this.annexUrl = dto.getAnnexUrl();
        this.likeCount = dto.getLikeCount();
        this.shareCount = dto.getShareCount();
        this.sorts = dto.getSorts();
        this.location = dto.getLocation();
        this.imgUrl = dto.getImgUrl();
        this.releaseTime = dto.getReleaseTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ContentDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(ContentDTO dto) {
        dto.setId(this.id);
        dto.setTitle(this.title);
        dto.setText(this.text);
        dto.setSummary(this.summary);
        dto.setLinkTo(this.linkTo);
        dto.setThumbnail(this.thumbnail);
        dto.setModule(this.module);
        dto.setStyle(this.style);
        dto.setUserId(this.userId);
        dto.setAuthor(this.author);
        dto.setOrderNumber(this.orderNumber);
        dto.setStatus(this.status);
        dto.setVoteUp(this.voteUp);
        dto.setVoteDown(this.voteDown);
        dto.setRate(this.rate);
        dto.setRateCount(this.rateCount);
        dto.setCommentStatus(this.commentStatus);
        dto.setCommentCount(this.commentCount);
        dto.setCommentTime(this.commentTime);
        dto.setViewCount(this.viewCount);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setTag(this.tag);
        dto.setMetaKeywords(this.metaKeywords);
        dto.setMetaDescription(this.metaDescription);
        dto.setRemarks(this.remarks);
        dto.setTop(this.top);
        dto.setType(this.type);
        dto.setServiceStationId(this.serviceStationId);
        dto.setContentMode(this.contentMode);
        dto.setAnnexUrl(this.annexUrl);
        dto.setLikeCount(this.likeCount);
        dto.setShareCount(this.shareCount);
        dto.setSorts(this.sorts);
        dto.setLocation(this.location);
        dto.setImgUrl(this.imgUrl);
        dto.setReleaseTime(this.releaseTime);
    }
}
