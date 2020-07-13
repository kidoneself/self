package com.yimao.cloud.pojo.dto.system;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "库存物资分类")
@Data
public class GoodsCategoryDTO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 3773724231169462796L;
	
	@ApiModelProperty(value = "id")
	private Integer id;
	@ApiModelProperty(value = "分类名称")
	private String name;
	@ApiModelProperty(value = "分类等级")
	private Integer level;
	@ApiModelProperty(value = "上级id")
	private Integer pid;
	@ApiModelProperty(value = "排序")
	private Integer sorts;
	@ApiModelProperty(value = "更新时间")
    private Date updateTime;	
	@ApiModelProperty(value = "创建时间")
    private Date createTime;
	
	private List<GoodsCategoryDTO> secondLevelCategoryList;
	
	List<GoodsMaterialsDTO> materialsList;
	
	List<StationStoreHouseDTO> stationGoodsList;
}
