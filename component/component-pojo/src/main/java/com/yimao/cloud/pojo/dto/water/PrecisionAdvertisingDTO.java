package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * 描述：广告精准投放。
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:45
 * @Version 1.0
 */
@Data
@ApiModel(description = "广告精准投放")
public class PrecisionAdvertisingDTO implements Serializable {

    private static final long serialVersionUID = 3155699536302220L;

    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "水机SN码")
    private String snCode;
    @ApiModelProperty(position = 2, value = "广告配置ID")
    private Integer advertisingId;

}