package com.yimao.cloud.pojo.dto.hra;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: F卡分配信息
 * @author: yu chunlei
 * @create: 2019-02-27 18:09:37
 **/
@Data
public class HraFCardDTO implements Serializable {


    private static final long serialVersionUID = -2364015863771756841L;
    @ApiModelProperty(position = 1,value = "服务站门店ID")
    private Integer stationStoreId;
    @ApiModelProperty(position = 2,value = "商品ID")
    private Integer productId;
    @ApiModelProperty(position = 3,value = "卡数量")
    private Integer cardCount;
    @ApiModelProperty(position = 4,value = "是否只有此服务站可用,1-可用 0-不可用")
    private Integer selfStation;
    @ApiModelProperty(position = 5,value = "是否禁用：1-禁用 0-非禁用")
    private Integer disabled;
    @ApiModelProperty(position = 6,value = "短信通知手机号")
    private String phone;

}
