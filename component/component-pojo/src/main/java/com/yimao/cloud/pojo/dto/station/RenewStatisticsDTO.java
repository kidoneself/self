package com.yimao.cloud.pojo.dto.station;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "续费统计返回类")
public class RenewStatisticsDTO {
	@ApiModelProperty(value = "服务站门店名称")
	private String stationName;
	@ApiModelProperty(value = "日期")
	private String date;
	@ApiModelProperty(value = "总安装数")
	private Integer totalInstallNum;
	@ApiModelProperty(value = "应续费")
	private Integer needRenewNum;
	@ApiModelProperty(value = "新安装")
	private Integer newInstallNum;
	@ApiModelProperty(value = "已续费")
	private Integer isRenewNum;

	
	//---图表数据 ---
	@ApiModelProperty(value = "续费统计图日期")
	private String time;

	public Integer getTotalInstallNum() {
		if(Objects.isNull(totalInstallNum)) {
			return 0;
		}else {
			return totalInstallNum;
		}
	}
	
	public Integer getNeedRenewNum() {
		if(Objects.isNull(needRenewNum)) {
			return 0;
		}else {
			return needRenewNum;
		}
	}
	
	public Integer getNewInstallNum() {
		if(Objects.isNull(newInstallNum)) {
			return 0;
		}else {
			return newInstallNum;
		}
	}
	
	
	public Integer getIsRenewNum() {
		if(Objects.isNull(isRenewNum)) {
			return 0;
		}else {
			return isRenewNum;
		}
	}
	


}
