package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 描述：效果统计
 */
@Data
@ApiModel(description = "效果统计")
public class EffectStatisticsForAppDTO implements Serializable {

    private static final long serialVersionUID = 1573836755485035727L;

    @ApiModelProperty(position = 1, value = "id")
    private Integer id;
    @ApiModelProperty(position = 2, value = "广告位ID")
    private String adslotId;
    @ApiModelProperty(position = 3, value = "物料名称")
    private String materielName;
    @ApiModelProperty(position = 4, value = "广告主")
    private String advertisers;
    @ApiModelProperty(position = 5, value = "屏幕位置：1-大屏；2-小屏")
    private Integer screenLocation;
    @ApiModelProperty(position = 6, value = "广告平台：0-翼猫；1-百度；2-京东；3-科大讯飞")
    private Integer platform;
    @ApiModelProperty(position = 7, value = "物料ID")
    private Integer materielId;
    @ApiModelProperty(position = 8, value = "投放配置ID")
    private Integer advertisingId;
    @ApiModelProperty(position = 9, value = "设备编码")
    private String snCode;
    @ApiModelProperty(position = 10, value = "投放开始时间")
    private Date beginTime;
    @ApiModelProperty(position = 11, value = "投放结束时间")
    private Date endTime;
    @ApiModelProperty(position = 12, value = "设备组：1-用户组，2-服务站组")
    private Integer deviceGroup;
    @ApiModelProperty(position = 13, value = "播放数据集合")
    private List<EffectStatisticsDayAppDTO> dayAppDTOList;

    @ApiModelProperty(position = 110, value = "创建时间")
    private Date createTime;


}