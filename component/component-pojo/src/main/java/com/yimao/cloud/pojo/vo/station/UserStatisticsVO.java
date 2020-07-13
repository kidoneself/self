package com.yimao.cloud.pojo.vo.station;

import com.yimao.cloud.pojo.dto.station.UserStatisticsDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "站务系统-统计-流水统计VO")
public class UserStatisticsVO {
	@ApiModelProperty(value = "用户统计表格数据（全部，用户，经销商，代理商）")
	List<UserStatisticsDTO> userStatistics;
	@ApiModelProperty(value = "用户统计图")
	List<UserStatisticsDTO> userPicRes;
	@ApiModelProperty("经销商统计图")
	List<UserStatisticsDTO> distributorPicRes;
	@ApiModelProperty("代理商统计图")
	List<UserStatisticsDTO> agentPicRes;
}
