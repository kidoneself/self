package com.yimao.cloud.pojo.query.station;

import java.util.Date;
import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "服务站系统消息查询类")
@Data
public class StationMessageQuery extends BaseQuery{
	 
	 @ApiModelProperty(value = "消息分类:1-阈值提醒,2-续费推送,3-报警推送,4-创建账号,5-新工单分配,6-工单完成")
	 private Integer pushType;
	 @ApiModelProperty(value = "消息状态0-未读；1-已读")
	 private Integer readStatus;
	 @ApiModelProperty(value = "内容")
	 private String content;
	 @ApiModelProperty(value = "开始时间")
	 private Date startTime;
	 @ApiModelProperty(value = "结束时间")
	 private Date endTime;
	 @ApiModelProperty(value = "排序 0-时间倒序 1-时间正序")
	 private Integer sortType;

	 private Set<Integer> preReceiverIds;
	 
	 private Set<Integer> afterReceiverIds;
	 
	 //仅售前设置，用来过滤消息类型 
	 private Integer type;
	 
	 private String sort;
}
