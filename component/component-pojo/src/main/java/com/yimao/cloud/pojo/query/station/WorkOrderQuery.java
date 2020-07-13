package com.yimao.cloud.pojo.query.station;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
@ApiModel(description = "工单列表查询条件")
public class WorkOrderQuery extends BaseQuery implements Serializable{

	private static final long serialVersionUID = 1218249044850367076L;
	
	@ApiModelProperty(value = "工单号")
	private String id;
	
	@ApiModelProperty(value = "工单来源")
	private Integer terminal;
	
	@ApiModelProperty(value = "工单状态：-2、审核未通过；-1、审核中；0-已支付；1-未受理；2-已受理；3-处理中；4-已完成；5-待付款；6-客服拒绝；7-分配客服")
	private Integer status;
	
	@ApiModelProperty(value = "非正常完成工单退单状态退单状态：0-待退单；1-退单中；2-退单成功")
	private Integer chargeBackStatus;
	
	@ApiModelProperty(value = "支付类型")
	private Integer payTerminal;
	
	@ApiModelProperty(value = "收货人姓名")
	private String addresseeName;
	
	@ApiModelProperty(value = "下单用户名")
	private String userName;
	
	@ApiModelProperty(value = "下单用户联系方式")
	private String userPhone;
	
	@ApiModelProperty(value = "经销商账号")
	private String distributorAccount;
	
	@ApiModelProperty(value = "经销商姓名")
	private String distributorName;
	
	@ApiModelProperty(value = "下单开始时间")
	private Date startTime;
	
	@ApiModelProperty(value = "下单结束时间")
	private Date endTime;
	
	private String delStatus;
	
	private Integer payStatus;
	
	private Boolean pay;  
	/**
	 * 是否退单
	 */
	private String isBackWorkOrder;  
	/**
	 * 拒单状态: N、未拒单；Y、已拒单
	 */
	private String notRefuse;
	/**
	 * 是否未分配：true、未分配；false、已分配
	 */
	private Boolean isNotAllot;
	
	@ApiModelProperty(value = "安装工id集合-站务系统查询")
	private List<Integer> engineerIds;
	
	@ApiModelProperty(value = "安装工程师筛选条件")
	private Integer engineerId;

}
