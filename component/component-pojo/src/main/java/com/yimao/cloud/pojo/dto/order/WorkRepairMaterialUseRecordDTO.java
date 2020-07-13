package com.yimao.cloud.pojo.dto.order;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "维修耗材使用纪录")
public class WorkRepairMaterialUseRecordDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -704178114540266915L;
	
	@ApiModelProperty("耗材名称")	
	private String materialName;
	@ApiModelProperty("耗材使用数")
    private Integer materialCount;
	@ApiModelProperty("一级类目名称")
    private String firstCategoryName;
	@ApiModelProperty("二级类目名称")
    private String secondCategoryName;
}
