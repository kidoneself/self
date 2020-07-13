package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "站务系统维修工单列表查询条件")
public class StationRepairOrderQuery extends BaseQuery{
	@ApiModelProperty("维修工单号")
    private String workOrderNo;
	
	@ApiModelProperty("工单状态 1-待维修 2-维修中 3-挂单 4-已完成")
	private Integer status;
	
	@ApiModelProperty("水机用户用户名")
	private String deviceUserName;
    
    @ApiModelProperty("水机用户手机号")
	private String deviceUserPhone;
    
    @ApiModelProperty("水机所属经销商姓名")
    private String distributorName;
    
    @ApiModelProperty("创建开始时间")
   	private String startLaunchTime;
   	
    @ApiModelProperty("创建结束时间")
   	private String endLaunchTime;
    
    @ApiModelProperty("来源 0-安装工app创建 1-后台业务系统创建 2-水机故障推送创建")
    private Integer sourceType;
}
