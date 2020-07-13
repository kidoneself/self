package com.yimao.cloud.pojo.vo.station;

import java.util.List;

import com.yimao.cloud.pojo.dto.station.FlowStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.RenewStatisticsDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "统计-续费统计返回类")
public class RenewStatisticsVO {
	@ApiModelProperty(value = "续费统计表格数据")
	List<RenewStatisticsDTO> renewStatistics;
	@ApiModelProperty("续费统计图(已续费)")
	List<RenewStatisticsDTO> isRenewPicData;
	@ApiModelProperty("续费统计图（新安装）")
	List<RenewStatisticsDTO> newInstallPicData;
}
