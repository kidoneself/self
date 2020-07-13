package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
/***
 * 功能描述:设备概况
 *
 * @param:
 * @auther: liu yi
 * @date: 2019/7/30 11:48
 * @return:
 */
@Getter
@Setter
public class WaterDeviceOverviewDTO implements Serializable {
    private static final long serialVersionUID = 1892833751010245968L;
    @ApiModelProperty(value = "设备总数")
    private Integer deviceTotalCount;
    @ApiModelProperty(value = "昨日新增设备总数")
    private Integer yesterdayDeviceTotalCount;

    @ApiModelProperty(value = "首年销售总额")
    private BigDecimal saleTotal;
    @ApiModelProperty(value = "昨日新增首年销售总额")
    private BigDecimal yesterdaySaleTotal;

    @ApiModelProperty(value = "续费总次数")
    private Integer renewTotalCount;
    @ApiModelProperty(value = "昨日新增续费总次数")
    private Integer yesterdayRenewTotalCount;

    @ApiModelProperty(value = "续费总额")
    private BigDecimal renewTotal;
    @ApiModelProperty(value = "昨日新增续费总额")
    private BigDecimal yesterdayRenewTotal;

    //设备在线率
    @ApiModelProperty(value = "在线总数")
    private Integer onlineTotal;
    @ApiModelProperty(value = "离线总数")
    private Integer offlineTotal;

    @ApiModelProperty(value = "型号占比")
    private Map modelCount;

    //计费类型总数
    @ApiModelProperty(value = "计费类型总数")
    private Map costTotal;

    //设备续费
    @ApiModelProperty(value = "新安装续费总数")
    private Integer newInstalRenewTotal;
    @ApiModelProperty(value = "应续费总数")
    private Integer shouldRenewTotal;
    @ApiModelProperty(value = "已续费总数")
    private Integer hasRenewTotal;

    //地区总数排行
    @ApiModelProperty(value = "地区安装续费总数排行")
    private Map instalProvinceTotal;
    @ApiModelProperty(value = "地区续费总数排行")
    private Map renewProvinceTotal;

    @ApiModelProperty(value = "所有设备续费状态统计")
    private Map allWaterDeviceRenewTotal;
}
