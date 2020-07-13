package com.yimao.cloud.pojo.dto.system;

import java.util.Date;
import java.util.List;

import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "库存物资DTO类")
public class GoodsMaterialsDTO {
	@ApiModelProperty(value = "id")
	private Integer id;
	@ApiModelProperty(value = "物资名称")
    private String name;
	@ApiModelProperty(value = "物资类型")
    private Integer type;
	@ApiModelProperty(value = "物资分类id" ,notes="等于二级类目")
    private Integer goodsCategoryId;
	@ApiModelProperty(value = "物资分类一级id")
    private Integer firstCategoryId;
	@ApiModelProperty(value = "物资分类二级类目名称")
	private String secondLevelCategoryName;
	@ApiModelProperty(value = "物资分类一级类目名称")
	private String firstLevelCategoryName;
	@ApiModelProperty(value = "适配型号")
	private String adaptationModel;
	@ApiModelProperty(value = "创建人")
    private String creator;
	@ApiModelProperty(value = "创建时间")
    private Date createTime;
	@ApiModelProperty(value = "更新人")
    private String updater;
	@ApiModelProperty(value = "更新时间")
    private Date updateTime;
	@ApiModelProperty(value = "商品剩余可用库存")
	private Integer stockCount;	
	@ApiModelProperty(value = "物资分类排序")
	private Integer categorySorts;
	@ApiModelProperty(value = "耗材使用数")
	private Integer materialCount;
	private List<ProductCategoryDTO> adaptationModelList;//库存耗材新增型号接收参数
	
	private List<MaterialsAdaptationModelDTO> materialsAdaptationList;//库存耗材列表展示型号集合
}
