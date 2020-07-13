package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/***
 * 功能描述:积分配置
 *
 * @auther: liu yi
 * @date: 2019/6/3 10:50
 */
@Getter
@Setter
public class IntegralConfigurationDTO {
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "积分类型：1-点击广告 2-单日在线时长")
    private Integer type;
    @ApiModelProperty(value = "最少在线时间 （单位小时）")
    private Integer leastOnLineTime;
    @ApiModelProperty(value = "积分数量")
    private Integer num;
    @ApiModelProperty(value = "规则id")
    private Integer integralRuleId;

}