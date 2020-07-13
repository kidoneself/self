package com.yimao.cloud.pojo.dto.cms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * 代言卡和宣传卡
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@Data
@ApiModel(description = "宣传卡代言卡")
public class CardDTO implements Serializable {

    private static final long serialVersionUID = 169116624709431995L;
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value ="卡类型 1 代言卡 2 宣传卡")
    private Integer cardType;

    @ApiModelProperty(value = "卡类型的code 比如：代言卡的健康食品code")
    private String typeCode;

    @ApiModelProperty(value ="标题")
    private String title;

    @ApiModelProperty(value ="标语（分享出去的标题），多个以逗号分隔。")
    private String tag;

    @ApiModelProperty(value ="背景图url用于 代言卡")
    private String backgroundImg;

    @ApiModelProperty(value ="卡图片url  代言卡图片/宣传卡图标")
    private String cardImg;

    @ApiModelProperty(value ="分享文案")
    private String content;

    @ApiModelProperty(value ="文字颜色code")
    private String textColor;

    @ApiModelProperty(value ="按钮颜色code")
    private String button;

    @ApiModelProperty(value ="排序")
    private Integer sorts;

    @ApiModelProperty(value ="状态1已发布 2 未发布以保存，4 已删除")
    private Integer cardStatus;

    @ApiModelProperty(value = "H5链接地址")
    private String h5Url;
}
