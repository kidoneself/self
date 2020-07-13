package com.yimao.cloud.pojo.dto.hra;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * @description: 体检卡信息
 * @author: yu chunlei
 * @create: 2019-02-27 12:06:13
 **/
@Data
public class HraAssignDTO implements Serializable {

    private static final long serialVersionUID = -3089732211617282030L;
    @ApiModelProperty(position = 1,value = "用户ID")
    private Integer userId;
    @ApiModelProperty(position = 2,value = "商品ID")
    private Integer productId;
    @ApiModelProperty(position = 3,value = "数量")
    private Integer cardCount;
}
