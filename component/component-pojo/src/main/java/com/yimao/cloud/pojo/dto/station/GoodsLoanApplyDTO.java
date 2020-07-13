package com.yimao.cloud.pojo.dto.station;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "服务站门店库存借调类")
public class GoodsLoanApplyDTO {
		
	@ApiModelProperty(value = "发起申请服务站公司id")
	private Integer applyCompanyId;
	
	@ApiModelProperty(value = "发起申请服务站公司名称")
    private String applyCompanyName;
	
    @ApiModelProperty(value = "发起申请服务站门店id")
    private Integer applyStationId;
    
    @ApiModelProperty(value = "发起申请服务站门店编号")
    private String applyStationCode;
    
    @ApiModelProperty(value = "发起申请服务站门店名称")
    private String applyStationName;
    
    @ApiModelProperty(value = "被申请服务站公司id")
	private Integer loanCompanyId;
    
	@ApiModelProperty(value = "被申请服务站公司名称")
    private String loanCompanyName;
	
    @ApiModelProperty(value = "被申请服务站门店id")
    private Integer loanStationId;
    
    @ApiModelProperty(value = "被申请服务站门店编号")
    private String loanStationCode;
    
    @ApiModelProperty(value = "被申请服务站门店名称")
    private String loanStationName;
    
	@ApiModelProperty(value = "借调状态  0-待审核 1-已通过 2-拒绝")
	private Integer status;
	
	@ApiModelProperty(value = "库存一级类目名称")
	private String firstCategoryLevelName;
	
	@ApiModelProperty(value = "库存二级类目名称")
	private String secondCategoryLevelName;	
	
	@ApiModelProperty(value = "库存物资id")
    private Integer goodsMaterialsId;
	
	@ApiModelProperty(value = "库存物资名称")
    private String goodsMaterialsName;
	
	@ApiModelProperty(value = "申请数量")
    private Integer applyCount;
	
	@ApiModelProperty(value = "备注")
    private String remark;
	
	@ApiModelProperty(value = "申请人")
    private String applyerName;
	
	@ApiModelProperty(value = "申请时间")
    private Date applyTime;
	
	@ApiModelProperty(value = "审核人")
    private String auditorName;
	
	@ApiModelProperty(value = "审核时间")
    private Date auditTime;
	
	/*
	 * 列表展示
	 */
	@ApiModelProperty(value = "借调类型 0-借入 1-借出")
	private Integer applyType;
	
	@ApiModelProperty(value = "我方门店")
	private String myStationName;
	
	@ApiModelProperty(value = "对象门店")
	private String objStationName;
	
	@ApiModelProperty(value = "对象门店所属公司")
	private String objCompanyName;
	
	@ApiModelProperty(value = "对象门店站长联系方式")
	private String objStationMasterPhone;
	
	@ApiModelProperty(value = "对象门店站长姓名")
	private String objStationMasterName;
	
	@ApiModelProperty(value = "对象门店编号")
	private String objStationCode;
	
	//selectStockLoanApply sql中使用
	@ApiModelProperty(value = "服务站门店id")
	private Integer stationId;
	    
}
