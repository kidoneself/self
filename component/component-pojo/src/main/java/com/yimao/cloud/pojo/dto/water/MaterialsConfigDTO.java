package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述：下发给客户端的自有广告物料信息
 *
 * @Author Zhang Bo
 * @Date 2019/2/20 17:34
 * @Version 1.0
 */
@Data
@ApiModel(description = "下发给客户端的自有广告物料信息")
public class MaterialsConfigDTO implements Serializable {

    private static final long serialVersionUID = -1735821538235863185L;

    @ApiModelProperty(position = 1, value = "id")
    private Integer id;
    @ApiModelProperty(position = 2, value = "物料名称")
    private String name;
    @ApiModelProperty(position = 3, value = "物料文件大小（单位：KB）")
    private Integer size;
    @ApiModelProperty(position = 4, value = "广告时长（单位：秒）")
    private Integer duration;
    @ApiModelProperty(position = 5, value = "屏幕位置：1-大屏；2-小屏")
    private Integer screenLocation;
    @ApiModelProperty(position = 6, value = "关联图")
    private String image;
    @ApiModelProperty(position = 7, value = "广告主名称")
    private String advertisers;
    @ApiModelProperty(position = 8, value = "物料地址")
    private String url;
    @ApiModelProperty(position = 9, value = "物料类型：1-视频；2-图片;3-链接")
    private Integer type;
    @ApiModelProperty(position = 10, value = "物料说明")
    private String description;

}
