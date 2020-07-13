package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "统计查询条件")
public class StatisticsQuery extends BaseQuery {

    @ApiModelProperty(value = "开始时间")
	private Date startTime;

    @ApiModelProperty(value = "结束时间")
	private Date endTime;
    
    @ApiModelProperty(value = "时间类型0-全部 1-七天内 2-三十天内 ") 
	private Integer dateType;
    
    @ApiModelProperty(value = "查询服务站以及区域集合") 
    private List<StatisticsStationQuery> stationList;

    //经销商id集合（用户统计查询用到）
    private List<Integer> distributorIds;
    //一级分类名称（商品统计时用到）
    private String categoryName;
    
}
