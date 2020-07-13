package com.yimao.cloud.pojo.query.station;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "站务系统维护工单列表查询条件")
public class StationMaintenanceOrderQuery extends BaseQuery {
	@ApiModelProperty("维修工单号")
	private String id;
	@ApiModelProperty("维护工单状态：1-待维护 2-处理中 3-挂单 4-已完成")
	private Integer state;
	@ApiModelProperty(value = "设备型号名称")
    private String deviceModelName;
    @ApiModelProperty(value = "客户名")
    private String consumerName;
    @ApiModelProperty(value = "客户电话")
    private String consumerPhone;
    @ApiModelProperty(value = "所属经销商")
    private String distributorName;
    @ApiModelProperty("创建开始时间")
    private Date createStartTime;
    @ApiModelProperty("创建结束时间")
    private Date createEndTime;
	@ApiModelProperty(value = "安装工id集合-站务系统查询")
	private List<Integer> engineerIds;
}
