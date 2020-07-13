package com.yimao.cloud.pojo.vo.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：产品状态统计VO对象
 *
 * @Author Zhang Bo
 * @Date 2019/3/18
 */
@ApiModel(description = "产品状态统计VO对象")
@Getter
@Setter
public class ProductStatusStatisticVO {

    @ApiModelProperty(position = 1, value = "全部（不包括已删除的）")
    private Integer allStatus;
    @ApiModelProperty(position = 2, value = "已上架")
    private Integer upStatus;
    @ApiModelProperty(position = 3, value = "已下架")
    private Integer downStatus;

}
