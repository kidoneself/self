package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 服务区域转让服务区域信息封装(用作工单、设备、安装工等数据的转让)
 *
 * @author Liu Long Jie
 * @date 2020-5-22 10:08:56
 */
@Data
public class TransferAreaInfoDTO {

    @ApiModelProperty(value = "转让区域id", required = true)
    private Integer areaId;
    @ApiModelProperty(value = "转让区域省", required = true)
    private String province;
    @ApiModelProperty(value = "转让区域市", required = true)
    private String city;
    @ApiModelProperty(value = "转让区域区", required = true)
    private String region;

    @ApiModelProperty(value = "承包方服务站门店id", required = true)
    private Integer stationId;
    @ApiModelProperty(value = "承包方安装工", required = false)
    private Integer engineerId;
}
