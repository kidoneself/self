package com.yimao.cloud.pojo.dto.station;

import com.yimao.cloud.pojo.dto.cms.ContentDTO;
import com.yimao.cloud.pojo.dto.cms.VideoDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "控制台-代办事项类")
public class StationScheduleDTO {
	
	@ApiModelProperty(value = "昨日新增设备")
	private Integer yesterdayInstallNum;
	
	@ApiModelProperty(value = "昨日续费设备")
	private Integer yesterdayRenewNum;
	
	@ApiModelProperty(value = "所有设备")
	private Integer totalDeviceNum;
	
	@ApiModelProperty(value = "续费次数")
	private Integer renewNum;
	
	@ApiModelProperty(value = "未受理工单")
	private Integer notAcceptedNum;
	
	@ApiModelProperty(value = "已受理工单")
	private Integer acceptedNum;
	
	@ApiModelProperty(value = "处理中工单")
	private Integer processingNum;
	
	@ApiModelProperty(value = "已完成工单")
	private Integer finishNum;
	
	@ApiModelProperty(value = "区县级代理数量")
	private Integer agentNum;
	
	@ApiModelProperty(value = "经销商数量")
	private Integer distributorNum;
	
	@ApiModelProperty(value = "普通用户")
	private Integer commonUserNum;
	
	@ApiModelProperty(value = "会员用户")
	private Integer vipUserNum;
	
	@ApiModelProperty(value = "今日待评估")
	private Integer todayNeedAssess;
	
	@ApiModelProperty(value = "昨日完成评估")
	private Integer yesterdayFinishAssess;
	
	@ApiModelProperty(value = "总完成评估")
	private Integer totalFinishAssess;

	@ApiModelProperty(value = "推荐视频")
	private List<VideoDTO> video;

	@ApiModelProperty(value = "推荐资讯")
	private List<ContentDTO> information;
	
}
