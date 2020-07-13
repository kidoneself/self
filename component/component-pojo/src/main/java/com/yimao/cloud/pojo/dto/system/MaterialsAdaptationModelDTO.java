package com.yimao.cloud.pojo.dto.system;

import java.util.Date;
import java.util.List;

import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "库存耗材适配类型DTO类")
public class MaterialsAdaptationModelDTO {
	@ApiModelProperty(value = "水机产品型号名称")
	private String deviceModelName;
	@ApiModelProperty(value = "水机产品分类id")
    private Integer productCategoryId;
}
