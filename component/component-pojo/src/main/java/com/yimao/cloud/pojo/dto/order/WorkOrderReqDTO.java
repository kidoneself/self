package com.yimao.cloud.pojo.dto.order;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WorkOrderReqDTO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private String workOrderId;// 工单号
	private Integer engineerId;// 安装工id
	private String engineerName;//安装工名称
	private Integer status;// 工单状态
	private Date createTime;// 下单时间
	@ApiModelProperty(value = "省")
	private String province;           //省
	@ApiModelProperty(value = "市")
	private String city;               //市
	@ApiModelProperty(value = "区县")
	private String region;             //区县
	private String address;// 安装地址
	private String addressDetail;      //地址详情
	private Integer waterUserId;// 指的是水机用户/收货人
	private String phone;// 指的是水机用户/收货人的联系方式
	private String productModel;// 产品型号
	private String costId;// 计费方式
	private Integer productId;// 产品id

	// 改约
	private Integer appointType;// 改约类型：1-时间 2-地址
	private String appointRemark;// 改约原因
	private String appointDate;// 改约时间
	private String appointTimeLimit;// 时间点范围

	// 水源
	private String ismunicipal;// 原水水源是否为市政自来水-1是/否0
	private String tds;// tds值
	private String hydraulic; // 原水水压值
	private String otherSource; // 其他原水水源

	// 扫码
	private String sn;// sn码
	private String logisticsCode;// 批次码
	private String simCard;// sim卡
	private String delStatus;//删除状态
	
	private String search;//安装工app搜索字段
	
	//退单
	private Integer chargeBcakType;//退单类型 1.用户退单,2.经销商退单,3.其他
	private String chargeBcakReason;//退单原因
	
	//签约
	private String signClient;//签约来源:1.app,2.WeChat
	private Integer signType;//签约类型:1.纸质签约,2.电子签约,3.其他
	private String protocol; //协议编号
    private String confirmation;//确认单号
    private String signContractCredential;//纸质合同凭证
    private Integer userId;
    private Integer year;
    private String name;
    private String idCard;
    private String mail;
	private String latitude;//纬度
	private String longitude;//经度
	@ApiModelProperty(value = "排序类型:1-升序 2-降序")
	private Integer sortType;
	@ApiModelProperty(value = "排序规则：1-创建时间 2-距离 3-完成时间 4-退单时间")
	private Integer sortBy;
	
}
