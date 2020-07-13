package com.yimao.cloud.pojo.dto.station;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "评估统计返回类")
public class HraStatisticsDTO {
	
	@ApiModelProperty(value = "服务站门店id")
	private Integer stationId;
	
	@ApiModelProperty(value = "服务站门店名称")
	private String stationName;
	
	@ApiModelProperty(value = "日期")
	private String date;
	
	@ApiModelProperty(value = "服务站公众号预约数")
	private Integer reserveNum;
	
	@ApiModelProperty(value = "服务站评估数")
	private Integer usedNum;
	

	@ApiModelProperty(value = "评估图表时间轴时间")
	private String time;
	
	@ApiModelProperty(value = "预约图表数据")
	List<HraStatisticsDTO> reservationPicRes;

	@ApiModelProperty(value = "评估图表数据")
	List<HraStatisticsDTO> usedPicRes;
}
