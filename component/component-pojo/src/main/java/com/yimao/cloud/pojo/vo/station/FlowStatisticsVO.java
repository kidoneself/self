package com.yimao.cloud.pojo.vo.station;

import com.yimao.cloud.pojo.dto.station.FlowStatisticsDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "站务系统-统计-流水统计VO")
public class FlowStatisticsVO {
	@ApiModelProperty(value = "流水统计表格数据（全部，产品，hra，招商）")
	List<FlowStatisticsDTO> flowStatistics;
	@ApiModelProperty(value = "产品统计图")
	List<FlowStatisticsDTO> productPicRes;
	@ApiModelProperty("hra统计图")
	List<FlowStatisticsDTO> hraPicRes;
	@ApiModelProperty("招商统计图")
	List<FlowStatisticsDTO> distributorOrderPicRes;
	@ApiModelProperty("全部-产品总销量和hra总销量统计图")
	List<FlowStatisticsDTO> totalProductAndHraPicRes;
	@ApiModelProperty("全部-招商总销量统计图")
	List<FlowStatisticsDTO> totalDistributorOrderPicRes;
}
