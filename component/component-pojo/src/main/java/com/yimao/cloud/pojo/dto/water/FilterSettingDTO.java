package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 描述：滤芯参数配置
 *
 * @Author Zhang Bo
 * @Date 2019/7/16
 */
@ApiModel(description = "滤芯参数配置DTO")
@Getter
@Setter
public class FilterSettingDTO {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(position = 1, value = "省")
    private String province;

    @ApiModelProperty(position = 2, value = "市")
    private String city;

    @ApiModelProperty(position = 3, value = "区")
    private String region;

    @ApiModelProperty(position = 4, value = "设备型号：1601T、1602T、1603T、1601L")
    private String deviceModel;

    @ApiModelProperty(position = 5, value = "TDS，k值")
    private Double k;

    @ApiModelProperty(position = 6, value = "TDS，t值")
    private Double t;

    @ApiModelProperty(position = 7, value = "滤芯耗材集合，用作前端展示，添加和编辑操作提交时无需传递。")
    private List<WaterDeviceConsumableDTO> consumables;

    @ApiModelProperty(position = 100, value = "创建者")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新者")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;

}
