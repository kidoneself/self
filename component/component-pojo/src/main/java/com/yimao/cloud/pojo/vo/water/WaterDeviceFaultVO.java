package com.yimao.cloud.pojo.vo.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：水机设备故障记录
 *
 * @Author Zhang Bo
 * @Date 2019/2/23 15:19
 * @Version 1.0
 */
@ApiModel(description = "水机设备故障记录VO")
@Getter
@Setter
public class WaterDeviceFaultVO implements Serializable {

    private static final long serialVersionUID = 1221091391378838621L;

    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "设备ID")
    private Integer deviceId;
    @ApiModelProperty(position = 2, value = "SN码")
    private String sn;
    @ApiModelProperty(position = 3, value = "故障类型：1-余额不足；2-制水故障；3-TDS异常；4-滤芯报警；5-阀值提醒；6-续费超期；")
    private String typeText;
    @ApiModelProperty(position = 4, value = "故障状态：1-故障；2-故障已解除；")
    private String stateText;
    @ApiModelProperty(position = 5, value = "滤芯类型：PP、CTO、UDF、T33")
    private String filterType;
    @ApiModelProperty(position = 6, value = "故障描述")
    private String fault;
    @ApiModelProperty(position = 7, value = "同类故障发生的次数")
    private Integer amount;
    @ApiModelProperty(position = 8, value = "同类故障发生的最短时间间隔（单位：分钟）")
    private Integer minTimeInterval;
    @ApiModelProperty(position = 9, value = "同类故障发生的最长时间间隔（单位：分钟）")
    private Integer maxTimeInterval;
    @ApiModelProperty(position = 10, value = "创建时间（同类故障第一次发生时间）")
    private String createTime;
    @ApiModelProperty(position = 11, value = "更新时间（同类故障最近一次发生时间）")
    private String updateTime;

}
