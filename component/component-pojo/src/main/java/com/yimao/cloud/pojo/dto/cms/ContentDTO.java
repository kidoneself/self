package com.yimao.cloud.pojo.dto.cms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 内容表，用于存放比如文章
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@ApiModel(description = "内容信息")
public class ContentDTO implements Serializable {
    private static final long serialVersionUID = -3303963252886621128L;

    private Integer id;
    //标题
    @ApiModelProperty(value = "标题")
    private String title;
    //内容
    @ApiModelProperty(value = "内容")
    private String text;
    //摘要
    @ApiModelProperty(value = "摘要")
    private String summary;
    //连接到(常用于谋文章只是一个连接)
    @ApiModelProperty(value = "连接到(常用于谋文章只是一个连接)")
    private String linkTo;
    //缩略图（多个用逗号分隔）
    @ApiModelProperty(value = "缩略图（多个用逗号分隔）")
    private String thumbnail;
    //模型
    @ApiModelProperty(value = "模型")
    private String module;
    //样式：big-大图模式，single-单图模式，three-三图模式
    @ApiModelProperty(value = "样式：big-大图模式，single-单图模式，three-三图模式")
    private String style;
    //用户ID
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    //作者
    @ApiModelProperty(value = "作者")
    private String author;
    //分类ID
    @ApiModelProperty(value = "分类ID")
    private Integer[] categoryIds;
    //排序编号
    @ApiModelProperty(value = "排序编号")
    private Integer orderNumber;
    //是否删除：是否删除：是否删除：1已发布 2未发布 4删除 7 未审核 8审核未通过
    @ApiModelProperty(value = "是否删除：1已发布 2未发布 4删除 7 未审核 8审核未通过")
    private Integer status;
    //支持人数
    @ApiModelProperty(value = "支持人数")
    private Integer voteUp;
    //反对人数
    @ApiModelProperty(value = "反对人数")
    private Integer voteDown;
    //评分分数
    @ApiModelProperty(value = "评分分数")
    private Integer rate;
    //评分次数
    @ApiModelProperty(value = "评分次数")
    private Integer rateCount;
    //评论状态
    @ApiModelProperty(value = "评论状态")
    private Integer commentStatus;
    //评论总数
    @ApiModelProperty(value = "评论总数")
    private Integer commentCount;
    //最后评论时间
    @ApiModelProperty(value = "最后评论时间")
    private Date commentTime;
    //访问量
    @ApiModelProperty(value = "访问量")
    private Integer viewCount;
    //创建日期
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    //最后更新日期
    @ApiModelProperty(value = "最后更新日期")
    private Date updateTime;
    @ApiModelProperty(value = "最近一次发布时间")
    private Date releaseTime;
    //标签
    @ApiModelProperty(value = "标签")
    private String tag;
    //SEO关键字
    @ApiModelProperty(value = "SEO关键字")
    private String metaKeywords;
    //SEO描述信息
    @ApiModelProperty(value = "SEO描述信息")
    private String metaDescription;
    //备注信息
    @ApiModelProperty(value = "备注信息")
    private String remarks;
    //是否置顶
    @ApiModelProperty(value = "是否置顶")
    private Integer top;
    // 类型 1 总部业务内容，2 服务站内容
    @ApiModelProperty(value = "类型 1 总部业务内容，2 服务站内容")
    private Integer type;
    @ApiModelProperty(value = "父分类ID")
    private Integer parentCategoryId;
    @ApiModelProperty(value = "端 1 经销商app 2 微信公众号  3 小程序 4 站务系统")
    private Integer platform;
    @ApiModelProperty(value = "服务站ID")
    private Integer serviceStationId;
    //资讯类型 1、普通文本内容，2、公告 带红头文件附件
    @ApiModelProperty(value = "资讯类型 1、普通文本内容，2、公告 带红头文件附件")
    private Integer contentMode;
    //附件URL 用于公告pdf文件
    @ApiModelProperty(value = "附件URL 用于公告pdf文件")
    private String annexUrl;
    @ApiModelProperty(value = "分类ID用于查询")
    private Integer categoryId;
    //点赞量
    @ApiModelProperty(value = "点赞量")
    private Integer likeCount;
    //分享量
    @ApiModelProperty(value = "分享量")
    private Integer shareCount;
    //排序
    @ApiModelProperty(value = "排序")
    private Integer sorts;
    //展示端 数组
    @ApiModelProperty(value = "展示端ids ")
    private Integer[] platforms;
    @ApiModelProperty(value = "分类关系")
    private List<CategoryRelationDTO> categoryRelationList;

    @ApiModelProperty(value = "分类集合查询用")
    private List<Integer> ids;

    @ApiModelProperty(value = "位置：1资讯 2公号 3协议")
    private Integer location;

    @ApiModelProperty(value = "已读未读")
    private Boolean hasRead;

    @ApiModelProperty(value = "图")
    private String imgUrl;

    @ApiModelProperty(value = "发布时间（始）")
    private Date startReleaseTime;

    @ApiModelProperty(value = "发布时间（终）")
    private Date endReleaseTime;

    @ApiModelProperty(value = "站务系统排序规则 1-最新逆序 2-推荐")
    private Integer sort;
    
    @ApiModelProperty(value = "分类名")
    private String categoryName;


}
