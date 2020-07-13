package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 第三方广告平台
 *
 * @author Chen Hui Yang
 * @date 2019/2/15
 */
@Data
@ApiModel(description = "第三方广告平台")
public class PlatformDTO implements Serializable {

    private static final long serialVersionUID = 1573836755485035727L;

    @ApiModelProperty(value = "平台id")
    private Integer id;
    @ApiModelProperty(position = 1, value = "平台名称")
    private String name;
    @ApiModelProperty(position = 2, value = "媒体ID，标识资源方，平台生成")
    private String appId;
    @ApiModelProperty(position = 3, value = "API版本，按照当前接入所参照的API文档版本赋值，影响所有后续逻辑，填写错误会导致拒绝请求。")
    private String apiVersion;
    @ApiModelProperty(position = 4, value = "第三方广告请求地址")
    private String url;
    @ApiModelProperty(position = 5, value = "广告位ID，多个以英文逗号进行拼接")
    private String adslotIds;
    @ApiModelProperty(position = 6, value = "排序")
    private Integer sort;
    @ApiModelProperty(position = 7, value = "删除状态：0-未删除；1-删除")
    private Boolean deleted;


    @ApiModelProperty(position = 100, value = "创建者")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新者")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;
}
