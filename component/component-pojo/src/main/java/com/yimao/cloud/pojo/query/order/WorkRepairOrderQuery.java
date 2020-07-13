package com.yimao.cloud.pojo.query.order;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "维修工单列表查询条件")
public class WorkRepairOrderQuery implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 4907927344237652141L;
	
	@ApiModelProperty("维修工单号")
    private String workOrderNo;
	
	@ApiModelProperty("工单状态 1-待维修 2-维修中 3-挂单 4-已完成")
	private Integer status;
	
	@ApiModelProperty("水机所在省")
    private String province;
	
    @ApiModelProperty("水机所在市")
    private String city;
    
    @ApiModelProperty("水机所在区")
    private String region;
	
    @ApiModelProperty("水机用户用户名")
	private String deviceUserName;
    
    @ApiModelProperty("水机用户手机号")
	private String deviceUserPhone;
    
    @ApiModelProperty("水机所属经销商姓名")
    private String distributorName;
    
    @ApiModelProperty("来源 0-安装工app创建 1-后台业务系统创建 2-水机故障推送创建")
    private Integer sourceType;
    
    @ApiModelProperty("创建开始时间")
	private String startLaunchTime;
	
    @ApiModelProperty("创建结束时间")
	private String endLaunchTime;
    
	@ApiModelProperty(value = "安装工id集合-站务系统查询")
	private List<Integer> engineerIds;
	
	@ApiModelProperty(value = "安装工程师筛选条件")
	private Integer engineerId;
	
	@ApiModelProperty(value = "关键字搜索")
	private String keywords;
	
    @ApiModelProperty("经度")
    private String longitude;
    
    @ApiModelProperty("纬度")
    private String latitude;
    
    @ApiModelProperty("排序依据 1-距离 2-创建时间 3-完成时间")
    private Integer sortBy;
    
    @ApiModelProperty("排序方式1-正序 2-倒叙")
    private Integer sortType;
    
    @ApiModelProperty("排序方式字符串 asc/desc")
    private String sortTypeString;
    
    
}
