package com.yimao.cloud.pojo.vo.station;

import java.util.List;

import com.yimao.cloud.pojo.dto.station.FlowStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.HraStatisticsDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "站务系统-统计-评估统计VO")
public class HraStatisticsVO {
	
	@ApiModelProperty(value = "评估统计表格数据（体检用户数，公众号预约数）")
	List<HraStatisticsDTO> hraStatistics;
	
	@ApiModelProperty(value = "预约图表数据")
	List<HraStatisticsDTO> reservationPicRes;

	@ApiModelProperty(value = "评估图表数据")
	List<HraStatisticsDTO> usedPicRes;
}
