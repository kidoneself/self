package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import java.util.Date;

/**
 * 站务系统 建议反馈表
 *
 */
@Getter
@Setter
@ApiModel(description = "建议反馈表")
public class SuggestDTO {

    @ApiModelProperty(value = "主键id")
    private Integer id;
    @ApiModelProperty(value = "建议类型id")
    private Integer suggestType;
    @ApiModelProperty(value = "反馈内容")
    private String content;
    @ApiModelProperty(value = "附件")
    private String accessory;
    @ApiModelProperty(value = "展示端(1-净水设备 2-健康e家公众号 3-翼猫APP 4-健康自测小程序 5-站务系统 10-管理后台 ) ps：目前只针对站务系统")
    private Integer terminal;
    @ApiModelProperty(value = "服务站id")
    private Integer stationId;
    @ApiModelProperty(value = "状态 (0-未回复 1-已回复)")
    private Integer status;
    @ApiModelProperty(value = "角色id")
    private Integer roleId;
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "反馈者id")
    private Integer userId;
    @ApiModelProperty(value = "反馈者姓名")
    private String name;
    @ApiModelProperty(value = "反馈时间")
    private Date time;

    private String province;                //服务站所在省
    private String city;                    //服务站所在市
    private String region;                  //服务站所在区
    private String stationName;             //服务站名称
    private String suggestTypeName;         //建议类型
    private String accessoryStatus;         //附件状态 （是否存在）
    //回复人
    private String replier;
    //回复内容
    private String replyContent;
    //回复时间
    private Date replyTime;
    //回复附件
    private String replyAccessory;

}
