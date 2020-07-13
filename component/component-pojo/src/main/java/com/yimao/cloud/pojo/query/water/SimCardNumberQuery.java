package com.yimao.cloud.pojo.query.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 描述：SIM号码段配置查询条件
 *
 * @Author Zhang Bo
 * @Date 2019/7/10
 */
@ApiModel(description = "SIM号码段配置查询条件")
@Getter
@Setter
public class SimCardNumberQuery implements Serializable {

    private static final long serialVersionUID = 6647645171932497187L;

    @ApiModelProperty(position = 1, value = "安装工账号")
    private Integer simCardAccountId;

    @ApiModelProperty(position = 2, value = "ICCID号码段")
    private String cardNumber;

    @ApiModelProperty(position = 3, value = "最小号码")
    private String minNumber;

    @ApiModelProperty(position = 4, value = "最大号码")
    private String maxNumber;

    @ApiModelProperty(position = 5, value = "中间数字")
    private String middleNumber;

    @ApiModelProperty(position = 6, value = "SIM卡号")
    private String iccid;

    @ApiModelProperty(position = 7, value = "iccid前10位（后端用，前端忽略该字段）")
    private String prefixNum;

    @ApiModelProperty(position = 8, value = "iccid后9位（后端用，前端忽略该字段）")
    private String suffixNum;

}
