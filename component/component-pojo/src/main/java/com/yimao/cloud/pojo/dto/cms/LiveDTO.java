package com.yimao.cloud.pojo.dto.cms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 *  视频
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@ApiModel(description = "视频信息")
public class LiveDTO implements Serializable {
    private static final long serialVersionUID = -8248490759685642896L;
    //视频ID
    @ApiModelProperty(value ="视频ID")
    private Integer id;
    //名称
    @ApiModelProperty(value ="名称/标题")
    private String title;
    //状态：1-直播中，2-回放，3-视频，4-预告
    @ApiModelProperty(value ="状态：1-直播中，2-回放，3-视频，4-预告")
    private Integer liveStatus;
    //是否为推荐视频（0-否，1-是）
    @ApiModelProperty(value ="是否为推荐视频（0-否，1-是）")
    private Integer recommend;
    //分类ID
    @ApiModelProperty(value ="分类ID用于查询")
    private Integer categoryId;
    //观看人数.
    @ApiModelProperty(value ="观看人数.")
    private Integer watchCount;
    //真实观看人数
    @ApiModelProperty(value ="真实观看人数.")
    private Integer realWatchCount;
    //分享人数
    @ApiModelProperty(value ="分享人数.")
    private Integer shareCount;
    //真实分享人数
    @ApiModelProperty(value ="真实分享人数.")
    private Integer realShareCount;
    //PC端观看地址
    @ApiModelProperty(value ="PC端观看地址.")
    private String pcUrl;
    //PC端视频LOGO地址
    @ApiModelProperty(value ="PC端视频LOGO地址.")
    private String pcLogo;
    //移动端观看地址
    @ApiModelProperty(value ="移动端观看地址.")
    private String mobileUrl;
    //移动端视频LOGO地址
    @ApiModelProperty(value ="移动端视频LOGO地址.")
    private String mobileLogo;
    //频道图标地址
    @ApiModelProperty(value ="频道图标地址.")
    private String coverImage;
    //直播窗口背景地址
    @ApiModelProperty(value ="直播窗口背景地址.")
    private String liveImage;
    //排序
    @ApiModelProperty(value ="排序.")
    private Integer sorts;
    //是否删除：是否删除：1已发布 2未发布 4删除 7 未审核 8审核未通过
    @ApiModelProperty(value ="是否删除：1已发布 2未发布 4删除 7 未审核 8审核未通过")
    private Integer status;
    //创建时间
    @ApiModelProperty(value ="创建时间")
    private Date createTime;
    //修改时间
    @ApiModelProperty(value ="修改时间")
    private Date updateTime;
    // 类型 1 总部业务内容，2 服务站内容
    @ApiModelProperty(value ="类型 1 总部业务内容，2 服务站内容")
    private Integer type;
    @ApiModelProperty(value = "父分类ID")
    private Integer parentCategoryId;
    @ApiModelProperty(value = "端 1 经销商app 2 微信公众号  3 小程序")
    private Integer platform;
    @ApiModelProperty(value = "服务站ID")
    private  Integer serviceStationId;
    @ApiModelProperty(value = "新增保存 数组")
    private Integer [] categoryIds;
    @ApiModelProperty(value = "显示分类的关联信息")
    private List<CategoryRelationDTO> categoryRelationList;
}