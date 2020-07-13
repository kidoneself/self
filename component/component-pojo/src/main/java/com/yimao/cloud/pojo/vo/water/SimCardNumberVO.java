package com.yimao.cloud.pojo.vo.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：SIM号码段配置VO
 *
 * @Author Zhang Bo
 * @Date 2019/4/25
 */
@ApiModel(description = "SIM号码段配置VO")
@Getter
@Setter
public class SimCardNumberVO {

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(position = 1, value = "所属运营商ID")
    private String simCardAccountId;

    @ApiModelProperty(position = 2, value = "所属运营商名称")
    private String simCardCompanyName;

    @ApiModelProperty(position = 3, value = "ICCID号码段")
    private String cardNumber;

    @ApiModelProperty(position = 4, value = "最小号码")
    private String minNumber;

    @ApiModelProperty(position = 5, value = "最大号码")
    private String maxNumber;

    @ApiModelProperty(position = 6, value = "创建时间")
    private Date createTime;

    @ApiModelProperty(position = 7, value = "修改时间")
    private Date updateTime;

}
