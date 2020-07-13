package com.yimao.cloud.pojo.dto.hra;

import com.yimao.cloud.pojo.dto.system.StationDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 体检设备DTO
 * @author: yu chunlei
 * @create: 2019-01-12 17:28:03
 **/
@ApiModel(description = "hra设备导出")
@Getter
@Setter
public class HraDeviceExportDTO implements Serializable {

    private Integer id;
    @ApiModelProperty(position = 1, value = "体检设备唯一编号/设备ID")
    private String deviceId;
    @ApiModelProperty(position = 2, value = "体检设备类型：1-I型，2-II型(设备型号)")
    private String deviceType;
    @ApiModelProperty(position = 3, value = "设备状态：1可用，2锁定(上线状态)")
    private String deviceStatus;
    @ApiModelProperty(position = 4, value = "设备描述")
    private String deviceDesc;
    @ApiModelProperty(position = 5, value = "服务站名称，来自hra客户端体检软件提交数据")
    private String stationName;
    @ApiModelProperty(position = 6, value = "服务站地址，来自hra客户端体检软件提交数据")
    private String stationAddress;
    @ApiModelProperty(position = 7, value = "服务站联系电话，来自hra客户端体检软件提交数据")
    private String stationTel;
    @ApiModelProperty(position = 8, value = "备注")
    private String remark;
    @ApiModelProperty(position = 9, value = "产品类型")
    private Integer productType;
    @ApiModelProperty(position = 10, value = "服务站ID")
    private Integer stationId;
    @ApiModelProperty(position = 11, value = "区县级公司ID")
    private Integer stationCompanyId;
    @ApiModelProperty(position = 12, value = "区县级公司名称")
    private String stationCompanyName;
    @ApiModelProperty(position = 13, value = "省")
    private String province;
    @ApiModelProperty(position = 14, value = "市")
    private String city;
    @ApiModelProperty(position = 15, value = "区县")
    private String region;

    @ApiModelProperty(position = 18, value = "stationDTO")
    private StationDTO stationDTO;

    @ApiModelProperty(position = 100, value = "创建人")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间/上线时间")
    private String createTime;
    @ApiModelProperty(position = 102, value = "更新人")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private String updateTime;


}
