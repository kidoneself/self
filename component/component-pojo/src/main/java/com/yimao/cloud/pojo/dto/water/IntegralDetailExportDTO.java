package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
public class IntegralDetailExportDTO  implements Serializable {
    private static final long serialVersionUID = -1735821538235863185L;

    @ApiModelProperty(value = "规则名称")
    private String integralRuleName;
    @ApiModelProperty(value = "类型")
    private String type;
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
    @ApiModelProperty(value = "积分数量")
    private Integer num;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
}