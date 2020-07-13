package com.yimao.cloud.pojo.query.product;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/***
 * 产品活动查询请求对象
 * @author zhangbaobao
 * @date 2020/3/12
 */
@ApiModel
@Data
public class ProductActivityQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "活动id")
    private Integer id;
    @ApiModelProperty(value = "0.未开启,1.已开启")
    private Boolean opening;
    @ApiModelProperty(value = "产品id")
    private Integer productId;
    @ApiModelProperty(value = "产品状态")
    private Integer productStatus;
    @ApiModelProperty(value = "活动类型")
    private Integer activityType;
    @ApiModelProperty(value = "来源：1-终端app；2-微信公众号；3-翼猫APP；4-小程序；")
    private Integer terminal;

}
