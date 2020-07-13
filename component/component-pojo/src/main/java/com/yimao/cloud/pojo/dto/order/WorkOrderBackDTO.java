package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description 退机工单
 * @author Liu Yi
 * @date 2020/6/22 10:26
 */
@Data
public class WorkOrderBackDTO implements Serializable {
	private static final long serialVersionUID = 4727835418645950791L;
	@ApiModelProperty(value = "主键id")
	private Integer id;                     //主键id
	@ApiModelProperty(value = "退机工单号")
	private String code;                    //退机工单号
	@ApiModelProperty(value = "工单id")
	private String workOrderId;             //工单id
	@ApiModelProperty(value = "退机状态:1-待处理，2-挂单，3-处理中，4-已完成")
	private Integer status;                 //退机状态:1-待处理，2-挂单，3-处理中，4-已完成
	//@ApiModelProperty(value = "退机子状态:1-开始，2-拆前拍照，3-拆后确认，4-完成")
	//private Integer subStatus;              //退机子状态:1-开始，2-拆前拍照，3-拆后确认，4-完成
	@ApiModelProperty(value = "安装工接单时间")
	private Date acceptTime;
	@ApiModelProperty(value = "省")
	private String province;                //省
	@ApiModelProperty(value = "市")
	private String city;                    //市
	@ApiModelProperty(value = "区")
	private String region;                  //区
	@ApiModelProperty(value = "详细地址")
	private String address;                 //详细地址
	@ApiModelProperty(value = "设备sn")
	private String sn;                      //设备sn
	@ApiModelProperty(value = "批次码")
	private String iccid;                   //批次码
	@ApiModelProperty(value = "物流编码")
	private String logisticsCode;           //物流编码
	@ApiModelProperty(value = "用户id")
	private Integer userId;                 //用户id
	@ApiModelProperty(value = "用户名")
	private String userName;                //用户名
	@ApiModelProperty(value = "用户号码")
	private String userPhone;               //用户号码
	@ApiModelProperty(value = "经销商id")
	private Integer distributorId;          //经销商id
	@ApiModelProperty(value = "经销商账号")
	private String distributorAccount;      //经销商账号
	@ApiModelProperty(value = "经销商姓名")
	private String distributorName;         //经销商姓名
	@ApiModelProperty(value = "产品id")
	private Integer productId;              //产品id
	@ApiModelProperty(value = "产品名称")
	private String productName;             //产品名称
	@ApiModelProperty(value = "产品一级类目ID（一级类目）")
	private Integer productFirstCategoryId;  //产品一级类目ID（一级类目）
	@ApiModelProperty(value = "产品一级类目名称（一级类目）")
	private String productFirstCategoryName; //产品一级类目名称（一级类目）
	@ApiModelProperty(value = "产品二级类目ID（二级类目）")
	private Integer productTwoCategoryId;  //产品二级类目ID（二级类目）
	@ApiModelProperty(value = "产品二级类目名称（二级类目）")
	private String productTwoCategoryName; //产品二级类目名称（二级类目）
	@ApiModelProperty(value = "产品类目ID（三级类目）")
	private Integer productCategoryId;  //产品类目ID（三级类目）
	@ApiModelProperty(value = "产品类目名称（三级类目）")
	private String productCategoryName; //产品类目名称（三级类目）
	@ApiModelProperty(value = "计费方式id")
	private Integer costId;                 //计费方式id
	@ApiModelProperty(value = "计费方式")
	private String costName;                //计费方式
	@ApiModelProperty(value = "设备剩余金额")
	private BigDecimal money;               //设备剩余金额
	@ApiModelProperty(value = "安装日期")
	private Date installDate;               //安装日期
	@ApiModelProperty(value = "安装工id")
	private Integer engineerId;             //安装工id
	@ApiModelProperty(value = "安装工姓名")
	private String engineerName;            //安装工姓名
	@ApiModelProperty(value = "安装工电话")
	private String engineerPhone;           //安装工电话
	/*private String engineerProvince;        //安装工省份
	private String engineerCity;            //安装工市
	private String engineerRegion;          //安装工区县*/
	@ApiModelProperty(value = "服务站门店id")
	private Integer stationId;              //服务站门店id
	@ApiModelProperty(value = "服务站门店名称")
	private String stationName;             //服务站门店名称
	//private String stationPhone;            //服务站门店联系方式
	@ApiModelProperty(value = "上传图片,多个逗号隔开")
	private String img;                     //上传图片,多个逗号隔开
	@ApiModelProperty(value = "上传图片时间")
	private Date imgTime;                   //上传图片时间
	@ApiModelProperty(value = "来源：1-总部派单")
	private Integer source;                 //来源：1-总部派单
	@ApiModelProperty(value = "完成时间")
	private Date completeTime;              //完成时间
	@ApiModelProperty(value = "服务日期")
	private Date serviceDate;               //服务日期
	@ApiModelProperty(value = "服务时间点范围")
	private String serviceTimeLimit;               //服务时间段
	// 改约
	@ApiModelProperty(value = "改约时间")
	private Date appointDate;// 改约时间
	@ApiModelProperty(value = "改约时间点范围")
	private String appointTimeLimit;// 时间点范围
	@ApiModelProperty(value = "改约原因类型：1-时间冲突  2-其它")
	private Integer appointCauseType;      //改约原因类型：1-时间冲突  2-其它
	@ApiModelProperty(value = "倒计时时间")
	private Date countdownTime;             //倒计时时间
	@ApiModelProperty(value = "备注")
	private String remark;                  //备注
	@ApiModelProperty(value = "经度")
	private String longitude;
	@ApiModelProperty(value = "纬度")
	private String latitude;
	@ApiModelProperty(value = "创建时间")
	private Date createTime;                //创建时间
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;                //更新时间
	@ApiModelProperty(value = "距离")
	private String distanceNum;

	private Integer oldEngineerId;       //更换订单详情的安装工信息用到
}
