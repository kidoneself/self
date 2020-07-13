package com.yimao.cloud.pojo.dto.water;

import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author Chen Hui Yang
 * @date 2019/4/22
 */
@Data
@ApiModel(description = "服务站设备")
public class ServiceStationWaterDeviceDTO implements Serializable {

    private static final long serialVersionUID = 3155699536302220L;

    @ApiModelProperty(position = 1, value = "id")
    private Integer id;
    @ApiModelProperty(position = 2, value = "sn")
    private String snCode;
    @ApiModelProperty(position = 3, value = "省")
    private String province;
    @ApiModelProperty(position = 4, value = "市")
    private String city;
    @ApiModelProperty(position = 5, value = "区")
    private String region;
    @ApiModelProperty(position = 6, value = "具体地址")
    private String address;
    @ApiModelProperty(position = 7, value = "水机型号")
    private String deviceModel;
    @ApiModelProperty(position = 8, value = "sim卡号")
    private String simcard;
    @ApiModelProperty(position = 9, value = "设备类型")
    private String deviceType;
    @ApiModelProperty(position = 10, value = "纬度")
    private String latitude;
    @ApiModelProperty(position = 11, value = "经度")
    private String longitude;
    @ApiModelProperty(position = 12, value = "是否在线,0-不在线；1-在线")
    private Boolean online;
    @ApiModelProperty(position = 13, value = "水机激活时间")
    private Date activeTime;
    @ApiModelProperty(position = 14, value = "最后在线时间")
    private Date lastOnlineTime;
    @ApiModelProperty(position = 15, value = "网络连接类型：1-WIFI；3-3G，null-全部")
    private Integer connectionType;
    @ApiModelProperty(position = 16, value = "设备摆放地")
    private String place;
    @ApiModelProperty(position = 20, value = "剩余广告位")
    private Set<String> adslotStock;

    /**
     * 封装数据 DeviceDTO
     * @param dto
     */
    public void convert(WaterDeviceVO vo) {
        vo.setSnCode(this.snCode);
        vo.setProvince(this.province);
        vo.setCity(this.city);
        vo.setRegion(this.region);
        vo.setAddress(this.address);
        vo.setSimActivatingTime(this.activeTime);
        vo.setDeviceType(this.deviceType);
        vo.setDeviceModel(this.deviceModel);
        vo.setLastOnlineTime(this.lastOnlineTime);
        vo.setConnType(this.connectionType);
        vo.setPlace(this.place);
    }
}
