package com.yimao.cloud.pojo.dto.system;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("门店库存扣减类")
@Data
public class StationMaterialStockDTO {
	  //---------------维修传参-------------------
	  @ApiModelProperty("服务站门店id")
	  private Integer stationId;
	  @ApiModelProperty("产品型号id")
	  private Integer productCategoryId;
	  @ApiModelProperty("扣减耗材集合")
	  private List<GoodsMaterialsDTO> materials;
	  
	  //---------------维护传参---------------
	  @ApiModelProperty("安装工id")
	  private Integer engineerId;
	  @ApiModelProperty("滤芯名称")
	  private List<String> filterNames;
}
