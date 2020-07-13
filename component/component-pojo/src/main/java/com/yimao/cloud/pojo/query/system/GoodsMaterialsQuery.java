package com.yimao.cloud.pojo.query.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "库存物资展示查询类")
public class GoodsMaterialsQuery {
	
	@ApiModelProperty(value = "物资名称")
	private String name;
		
	@ApiModelProperty(value = "物料类型")
	private Integer type;
	
	@ApiModelProperty(value = "类目等级")
	private Integer goodsCategoryLevel;
	
	@ApiModelProperty(value = "一级类目分类名称")
	private String goodsCategoryFirstName;
	
	@ApiModelProperty(value = "二级类目分类名称")
	private String goodsCategorySecondName;
	
	@ApiModelProperty(value = "适配型号名称")
	private String deviceModelName;
	
	@ApiModelProperty(value = "适配型号id")
	private Integer productCategoryId;
	
	@ApiModelProperty(value = "修改开始时间")
	private String updateStartTime;
	
	@ApiModelProperty(value = "修改结束时间")
	private String updateEndTime;
	
	@ApiModelProperty(value = "创建开始时间")
	private String createStartTime;
	
	@ApiModelProperty(value = "创建结束时间")
	private String createEndTime;
	
	
}
