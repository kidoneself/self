package com.yimao.cloud.pojo.dto.system;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

@Data
@ApiModel(description = "门店库存物资")
public class StationStoreHouseDTO {
	@ApiModelProperty(value = "id")
    private Integer id;
	@ApiModelProperty(value = "门店id")
    private Integer stationId;
	@ApiModelProperty(value = "物资id")
    private Integer goodsId;
	@ApiModelProperty(value = "可用库存")
    private Integer stockCount;
	@ApiModelProperty(value = "占用库存")
    private Integer occupyStockCount;
	@ApiModelProperty(value = "不良品库存")
    private Integer defectiveStockCount;
	@ApiModelProperty(value = "服务站名称")
    private String stationName;
	@ApiModelProperty(value = "服务站code")
    private String stationCode;
	@ApiModelProperty(value = "服务站所在省")
    private String province;
	@ApiModelProperty(value = "服务站所在市")
    private String city;
	@ApiModelProperty(value = "服务站所在区")
    private String region;
	@ApiModelProperty(value = "服务站所属公司名称")
    private String stationCompanyName;
	@ApiModelProperty(value = "物资一级类目名")
    private String firstCategoryName;
	@ApiModelProperty(value = "物资二级类目名")
    private String twoCategoryName;
	@ApiModelProperty(value = "物资名")
    private String goodsName;
	@ApiModelProperty(value = "适配型号",notes="展示不使用，查询用")
    private String adaptationModel;
	@ApiModelProperty(value = "物资所属二级类目id")
    private Integer goodsCategoryId;
	@ApiModelProperty(value = "物资所属二级类目排序号")
    private Integer goodsCategorySorts;
	@ApiModelProperty(value = "适配型号集合",notes="展示耗材适配型号使用")
	private List<MaterialsAdaptationModelDTO> materialsAdaptationList;//库存耗材列表展示型号集合
}