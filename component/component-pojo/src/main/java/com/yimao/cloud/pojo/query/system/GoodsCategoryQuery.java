package com.yimao.cloud.pojo.query.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "库存物资类型展示查询类")
public class GoodsCategoryQuery {
	@ApiModelProperty(value = "id")
	private Integer id;
	
	@ApiModelProperty(value = "类目等级")
	private Integer level;
	
	@ApiModelProperty(value = "分类名称")
	private String name;
	
	@ApiModelProperty(value = "分类")
	private Integer type;
	
	@ApiModelProperty(value = "上级id")
	private Integer pid;
	
	@ApiModelProperty(value = "修改开始时间")
	private String updateStartTime;
	
	@ApiModelProperty(value = "修改结束时间")
	private String updateEndTime;
	
	@ApiModelProperty(value = "创建开始时间")
	private String createStartTime;
	
	@ApiModelProperty(value = "创建结束时间")
	private String createEndTime;

}
