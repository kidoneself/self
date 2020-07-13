package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/***
 * 销售额统计请求对象
 * @author zhangbaobao
 *
 */
@Data
@ApiModel(description = "销售额统计（经销商app）")
public class SalesStatsQueryDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "类型:1 个人,2 公司",required=true)
	private Integer type;
	@ApiModelProperty(value = "完成日期",required=false)
	private String completeTime;
	@ApiModelProperty(value = "distributorId",required=false)
	private Integer distributorId;
	@ApiModelProperty(value = "日期类型:时间类型1 :日,2 :月,3 :年",required=true)
	private Integer timeType;
	@ApiModelProperty(value = "当前用户发展的所有经销商id集合",required=false)
	private List<Integer> ids;
	@ApiModelProperty(value = "服务站公司id",required=true)
	private Integer stationCompanyId;
	//产品id集合（经销商app报表统计查询用到）
	private List<Integer> productIds;
	@ApiModelProperty(value = "日期集合",required=false)
	private List<String> dates;
	@ApiModelProperty(value = "经销商所属服务区域id集合",required=false)
	private List<Integer> areaIds;
	private Integer day;//多少天,7/30
}
