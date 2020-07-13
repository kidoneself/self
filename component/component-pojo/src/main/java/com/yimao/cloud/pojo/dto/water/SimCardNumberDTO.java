package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：SIM号码段配置
 *
 * @Author Zhang Bo
 * @Date 2019/4/25
 */
@ApiModel(description = "SIM号码段配置DTO")
@Getter
@Setter
public class SimCardNumberDTO {

    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "所属运营商")
    private Integer simCardAccountId;
    @ApiModelProperty(position = 2, value = "ICCID号码段")
    private String cardNumber;
    @ApiModelProperty(position = 3, value = "最小号码")
    private String minNumber;
    @ApiModelProperty(position = 4, value = "最大号码")
    private String maxNumber;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

}
