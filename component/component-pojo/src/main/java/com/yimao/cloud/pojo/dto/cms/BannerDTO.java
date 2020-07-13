package com.yimao.cloud.pojo.dto.cms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Id;
import lombok.Data;

/**
 *
 * 广告
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@ApiModel(description = "产品信息")
public class BannerDTO implements Serializable {
    private static final long serialVersionUID = 222273092002057956L;

    @ApiModelProperty(value = "bannerId")
    private Integer id;
    //banner名称
    @ApiModelProperty(value = "banner名称")
    private String name;
    //位置code
    @ApiModelProperty(value = "位置code")
    private String positionCode;
    //标题
    @ApiModelProperty(value = "标题")
    private String title;
    //内容
    @ApiModelProperty(value = "内容")
    private String content;
    //banner跳转url
    @ApiModelProperty(value = "banner跳转url")
    private String url;
    //图片url
    @ApiModelProperty(value = "图片url")
    private String adImg;
    //状态1可用 2删除
    @ApiModelProperty(value = "状态1已发布 2 未发布 保存状态为 2未发布  4 已删除")
    private Integer status;
    //排序编号
    @ApiModelProperty(value = "排序编号")
    private Integer sorts;
    //创建日期
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    //创建人
    @ApiModelProperty(value = "创建人")
    private String creator;
    //更新日期
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    //更新人
    @ApiModelProperty(value = "更新人")
    private String updater;
    //1-健康e家公众号；2-小猫店小程序；3-经销商APP
    @ApiModelProperty(value = "1-健康e家公众号；2-小猫店小程序；3-经销商APP")
    private Integer terminal;

}