package com.yimao.cloud.pojo.dto.order;

import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/***
 * 工单列表返回对象
 * @author zhangbaobao
 *
 */
@Data
public class WorkOrderRsDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "工单编号")
	private String id;
	@ApiModelProperty(value = "客户姓名")
	private String customerName;
	@ApiModelProperty(value = "客户手机号")
	private String customerPhone;
	@ApiModelProperty(value = "产品型号")
	private String productModel;
	@ApiModelProperty(value = "计费id")
	private String costId;
	@ApiModelProperty(value = "计费名称")
	private String costName;
	@ApiModelProperty(value = "订单金额")
	private BigDecimal amount;
	@ApiModelProperty(value = "省")
	private String province;
	@ApiModelProperty(value = "市")
	private String city;
	@ApiModelProperty(value = "区县")
	private String region;
	@ApiModelProperty(value = "服务地址,详细地址")
	private String serviceAdress;
	/*@ApiModelProperty(value = "经度")
	private String longitude;
	@ApiModelProperty(value = "纬度")
	private String latitude;*/
	@ApiModelProperty(value = "工单状态：1-未受理；2-已受理；3-处理中；4-已完成；")
	private Integer status;
	@ApiModelProperty(value = "发起时间")
	private String starTime;
	@ApiModelProperty(value = "服务时间")
	private String serviceTime;
	@ApiModelProperty(value = "预约时间")
	private String appointTime;
	@ApiModelProperty(value = "预约时间限制")
	private String appointTimeLimit;
	@ApiModelProperty(value = "完成时间")
	private String completeTime;
	@ApiModelProperty(value = "倒计时")
	private String orderCountdownTime;
	@ApiModelProperty(value = "是否支付：0-否；1-是")
	private Boolean pay;
	@ApiModelProperty(value = "支付方式：1-微信；2-支付宝；3-POS机；4-转账")
	private Integer payType;
	@ApiModelProperty(value = "支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败")
	private Integer payStatus;
	@ApiModelProperty(value = "当前步骤：0-开始 1-采集水源 2-激活 3-支付 4-签约 5-完成工单")
	private Integer step;
	@ApiModelProperty(value = "下一步骤：0-开始 1-采集水源 2-激活 3-支付 4-签约 5-完成工单")
	private Integer nextStep;
	@ApiModelProperty(value = "距离")
	private String distanceNum;
	@ApiModelProperty(value = "改约备注")
	private String appointRemark;
	@ApiModelProperty(value = "纬度")
	private String addrLatitude;
	@ApiModelProperty(value = "经度")
	private String addrLongitude;
	@ApiModelProperty(value = "地址（详细地址）")
	private String address;
	@ApiModelProperty(value = "拒单状态: N、可以拒单；Y、不可以拒单")
	private String notRefuse;
	@ApiModelProperty(value = "退单时间")
	private String chargebackTime;
	@ApiModelProperty(value = "用户是否签署合同：N、否；Y、是")
	private String signStatus; 
}
