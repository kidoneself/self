package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/***
 * 功能描述:积分详情
 *
 * @auther: liu yi
 * @date: 2019/6/3 10:50
 */
@Getter
@Setter
public class IntegralDetailDTO  implements Serializable {
    private static final long serialVersionUID = -1735821538235863185L;
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "积分规则id")
    private Integer integralRuleId;
    @ApiModelProperty(value = "规则名称")
    private String integralRuleName;
    @ApiModelProperty(value = "SN码")
    private String sn;
    @ApiModelProperty(value = "型号")
    private String model;
    @ApiModelProperty(value = "省份")
    private String province;
    @ApiModelProperty(value = "城市")
    private String city;
    @ApiModelProperty(value = "地区")
    private String region;
    @ApiModelProperty(value = "地址")
    private String address;
   /* @ApiModelProperty(value = "是否有效（被清0的无效） 0-否，1-是")
    private Integer isClear;*/
    @ApiModelProperty(value = "积分类型：1-点击广告 2-单日在线时长")
    private Integer type;
    @ApiModelProperty(value = "积分数量")
    private Integer num;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "积分总数量-后台统计用")
    private Integer totalNum;
}