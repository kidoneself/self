package com.yimao.cloud.pojo.export.water;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @description: 滤芯更换导出实体类
 * @author: yu chunlei
 * @create: 2019-11-01 16:09:23
 **/
@Getter
@Setter
public class FilterReplaceExport {

    @ApiModelProperty(position = 1, value = "SN码")
    private String sn;
    @ApiModelProperty(position = 2, value = "滤芯名称")
    private String filterName;
    @ApiModelProperty(position = 3, value = "安装区域-省")
    private String province;
    @ApiModelProperty(position = 4, value = "安装区域-市")
    private String city;
    @ApiModelProperty(position = 5, value = "安装区域-区")
    private String region;
    @ApiModelProperty(position = 7, value = "设备添加时间")
    private String activatingTime;
    @ApiModelProperty(position = 8, value = "更换时间")
    private String createTime;
}
