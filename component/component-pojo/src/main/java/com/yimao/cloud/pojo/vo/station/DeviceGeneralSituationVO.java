package com.yimao.cloud.pojo.vo.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 站务系统-设备-设备概况返回类
 *
 * @author Liu Long Jie
 */
@Data
@ApiModel(description = "设备概况返回类")
public class DeviceGeneralSituationVO implements Serializable {

    private static final long serialVersionUID = 1892833753457865968L;

    @ApiModelProperty(value = "昨日新增设备")
    private Integer yesterdayInstallNum;

    @ApiModelProperty(value = "昨日续费设备")
    private Integer yesterdayRenewNum;

    @ApiModelProperty(value = "所有设备")
    private Integer totalDeviceNum;

    @ApiModelProperty(value = "续费次数")
    private Integer renewNum;

    //设备在线率
    @ApiModelProperty(value = "在线总数")
    private Integer onlineTotal;
    @ApiModelProperty(value = "离线总数")
    private Integer offlineTotal;

    @ApiModelProperty(value = "型号占比")
    private Map modelCount;

    //计费类型占比
    @ApiModelProperty(value = "计费类型占比")
    private Map costTotal;

    @ApiModelProperty(value = "所有设备续费状态统计")
    private Map allWaterDeviceRenewTotal;

    public DeviceGeneralSituationVO() {
    }

    public DeviceGeneralSituationVO(Integer yesterdayInstallNum, Integer yesterdayRenewNum, Integer totalDeviceNum, Integer renewNum) {
        this.yesterdayInstallNum = yesterdayInstallNum;
        this.yesterdayRenewNum = yesterdayRenewNum;
        this.totalDeviceNum = totalDeviceNum;
        this.renewNum = renewNum;
    }
}
