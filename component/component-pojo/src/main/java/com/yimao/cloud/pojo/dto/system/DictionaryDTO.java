package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 字典表
 * 尽量把只有一级的放入字典表中维护
 *
 * @author liuhao@yimaokeji.com
 * @date 2017/11/16
 */
@ApiModel(description = "数据字典")
@Getter
@Setter
@ToString
public class DictionaryDTO implements Serializable {

    @ApiModelProperty(value = "字典ID")
    private Integer id;

    @ApiModelProperty(position = 2, value = "name")
    private String name;

    @ApiModelProperty(position = 3, value = "code")
    private String code;

    @ApiModelProperty(position = 4, value = "分组")
    private String groupCode;

    @ApiModelProperty(position = 5, value = "父级编号 默认为0")
    private Integer pid;

    @ApiModelProperty(position = 6, value = "排序")
    private Integer sorts;

    @ApiModelProperty(position = 7, value = "是否删除")
    private Boolean deleted;

    @ApiModelProperty(position = 8, value = "创建人")
    protected String creator;
    @ApiModelProperty(position = 9, value = "创建时间")
    protected Date createTime;
    @ApiModelProperty(position = 10, value = "更新人")
    protected String updater;
    @ApiModelProperty(position = 11, value = "更新时间")
    protected Date updateTime;

}
