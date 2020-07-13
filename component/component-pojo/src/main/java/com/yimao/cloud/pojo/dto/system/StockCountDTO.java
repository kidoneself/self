package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhilin.he
 * @description
 * @date 2019/5/5 10:19
 **/

@Getter
@Setter
@ApiModel(description = "云平台仓库数量")
public class StockCountDTO {

    @ApiModelProperty(value = "产品id")
    private Integer id;
    @ApiModelProperty(value = "产品名称")
    private String name;
    @ApiModelProperty(value = "库存数量")
    private Integer count;

    public StockCountDTO() {
        this.id = 0;
        this.name = "0";
        this.count = 0;
    }

    public StockCountDTO(Integer id, String name, Integer count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

}
