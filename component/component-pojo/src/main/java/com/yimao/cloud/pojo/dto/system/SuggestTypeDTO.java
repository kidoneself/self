package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 * 反馈类型表
 *
 */
@Getter
@Setter
@ApiModel(description = "反馈类型表")
public class SuggestTypeDTO {

    @ApiModelProperty(value = "主键id")
    private Integer id;
    @ApiModelProperty(value = "分类名称")
    private String name;
    @ApiModelProperty(value = "排序")
    private Integer sort;
    @ApiModelProperty(value = "展示端(1-净水设备 2-健康e家公众号 3-翼猫APP 4-健康自测小程序 5-站务系统 10-管理后台 ) ps：目前只针对站务系统")
    private Integer terminal;
    @ApiModelProperty(value = "创建人")
    private String creator;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "修改人")
    private String updater;
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    //展示端名称
    private String terminalStr;
    List<String> suggestTypeName;

}
