package com.yimao.cloud.pojo.dto.hra;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Lizhqiang
 * @date 2019/4/22
 */
@Data
public class HraExchangeSettingDTO {

    @ApiModelProperty(position = 1, value = "服务站门店ID")
    private Integer id;
    @ApiModelProperty(position = 2, value = "端  1-终端app；2-微信公众号；3-经销商APP；4-小程序")
    private Integer terminal;
    @ApiModelProperty(position = 3, value = "渠道")
    private Integer channel;
    @ApiModelProperty(position = 4, value = " 限制类型: 1-每天，2-每周，3-每月，4-季度，5-每年")
    private Integer limitType;
    @ApiModelProperty(position = 5, value = "次数  空为不限制")
    private Integer times;

}
