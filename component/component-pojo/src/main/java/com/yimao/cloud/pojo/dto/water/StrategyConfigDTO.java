package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * 描述：广告投放策略配置
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:45
 * @Version 1.0
 */
@Data
@ApiModel(description = "广告投放策略配置")
public class StrategyConfigDTO implements Serializable {

    private static final long serialVersionUID = 1573836755485035727L;

    @ApiModelProperty(value = "配置ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "广告平台：0-翼猫；1-百度；2-京东；3-科大讯飞")
    private Integer platform;//广告平台：0-翼猫；1-百度；2-京东；3-科大讯飞
    @ApiModelProperty(position = 2, value = "广告位ID")
    private Integer ownAdslotId;//广告位ID
    @ApiModelProperty(position = 3, value = "第三方平台广告位ID，当为自有广告时，值为物料ID")
    private String adslotId;//第三方平台广告位ID，当为自有广告时，值为物料ID
    @ApiModelProperty(position = 4, value = "广告时长（单位：秒）")
    private Integer duration;//广告时长（单位：秒）
    @ApiModelProperty(position = 5, value = "省市区过滤：0-不限；1-多选")
    private Integer areaFilter;//省市区过滤：0-不限；1-多选
    @ApiModelProperty(position = 6, value = "水机型号：多选以逗号分隔")
    private String models;//水机型号：多选以逗号分隔
    @ApiModelProperty(position = 7, value = "网络连接类型：0-不限；1-WIFI；3-3G")
    private Integer connectionType;//网络连接类型：0-不限；1-WIFI；3-3G；
    @ApiModelProperty(position = 8, value = "配置生效开始时间")
    private Date effectiveBeginTime;//配置生效开始时间
    @ApiModelProperty(position = 9, value = "配置生效结束时间")
    private Date effectiveEndTime;//配置生效结束时间
    @ApiModelProperty(position = 10, value = "禁用状态：0-启用；1-禁用")
    private Boolean forbidden;//禁用状态：0-启用；1-禁用
    @ApiModelProperty(position = 11, value = "删除状态：0-未删除；1-删除")
    private Boolean deleted;//删除状态：0-未删除；1-删除

    @ApiModelProperty(position = 20, value = "省市区多选集合，存放结构：数组")
    private Set<Integer> areaIds;

    @ApiModelProperty(position = 100, value = "创建者")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新者")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;

}