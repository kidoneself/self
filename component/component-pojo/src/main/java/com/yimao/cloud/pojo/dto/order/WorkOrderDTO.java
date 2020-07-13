package com.yimao.cloud.pojo.dto.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：水机安装工单
 *
 * @Author Zhang Bo
 * @Date 2019/2/26 15:19
 * @Version 1.0
 */
@Getter
@Setter
@ApiModel(description = "工单对象")
public class WorkOrderDTO implements Serializable {
	private static final long serialVersionUID = 4727835418645950791L;

	@ApiModelProperty(value = "工单号")
	private String id;                 //工单号（区分带字母的维修、维护和新老工单）
	@ApiModelProperty(value = "主订单号")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long mainOrderId;          //主订单号
	@ApiModelProperty(value = "子订单号")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long subOrderId;           //子订单号
	@ApiModelProperty(value = "工单状态：1-未受理；2-已受理；3-处理中；4-已完成；")
	private Integer status;            //工单状态：1-未受理；2-已受理；3-处理中；4-已完成；
	@ApiModelProperty(value = "水机用户ID")
	private Integer userId;            //水机用户ID
	@ApiModelProperty(value = "用户姓名")
	private String userName;           //用户姓名
	@ApiModelProperty(value = "用户手机号")
	private String userPhone;          //用户手机号
	@ApiModelProperty(value = "用户评分")
	private Integer userScore;         //用户评分
	@ApiModelProperty(value = "用户评价")
	private String userContent;         //用户评价
	@ApiModelProperty(value = "产品ID")
	private Integer productId;         //产品ID
	@ApiModelProperty(value = "产品名称")
	private String productName;         //产品名称
	@ApiModelProperty(value = "设备ID")
	private Integer deviceId;          //设备ID
	@ApiModelProperty(value = "设备SN码")
	private String sn;                 //设备SN码
	@ApiModelProperty(value = "设备型号")
	private String deviceModel;        //设备型号
	@ApiModelProperty(value = "金额（租赁费+安装费）")
	private BigDecimal fee;            //金额（租赁费+安装费）
	@ApiModelProperty(value = "开户费")
	private BigDecimal openAccountFee; //开户费
	@ApiModelProperty(value = "经销商ID")
	private Integer distributorId;     //经销商ID
	@ApiModelProperty(value = "经销商类型")
	private Integer distributorType;     //经销商类型
	@ApiModelProperty(value = "经销商类型名称")
	private String distributorTypeName;     //经销商类型名称
	@ApiModelProperty(value = "经销商姓名")
	private String distributorName;    //经销商姓名
	@ApiModelProperty(value = "经销商手机号")
	private String distributorPhone;   //经销商手机号
	@ApiModelProperty(value = "经销商省")
	private String distributorProvince;   //经销商省
	@ApiModelProperty(value = "经销商市")
	private String distributorCity;   //经销商市
	@ApiModelProperty(value = "经销商区")
	private String distributorRegion;   //经销商区
	private String distributorIdCard;                 //经销商身份证
	private String distributorAccount;                //经销商账号
	@ApiModelProperty(value = "安装工ID")
	private Integer engineerId;        //安装工ID
	@ApiModelProperty(value = "安装工姓名")
	private String engineerName;       //安装工姓名
	@ApiModelProperty(value = "安装工手机号")
	private String engineerPhone;      //安装工手机号
	@ApiModelProperty(value = "省")
	private String province;           //省
	@ApiModelProperty(value = "市")
	private String city;               //市
	@ApiModelProperty(value = "区县")
	private String region;             //区县
	private String address;            //地址
	private String addressDetail;      //地址详情
	private Integer dispatchType;      //派单方式：1-用户派单；2-系统派单
	private Boolean dispatch;          //是否需要手动分配安装工：0-否；1-是
	private Date dispatchTime;         //派单时间
	private String remark;             //备注
	private Integer costId;            //计费方式ID
	private Integer costType;          //计费方式：1-流量计费；2-包年计费；
	private String costName;           //计费方式名称
	private Date createTime;           //工单创建时间
	private Date serviceTime;          //用户下单时选择的安装时间
	private Integer step;              //工单安装到了第几步骤
	private Date acceptTime;           //安装工接单时间
	private Date pickTime;             //提货时间
	private Date operationTime;        //安装工实际操作安装时间
	private Date completeTime;         //安装完成时间

	// private Boolean cancel;            //工单是否取消：0-否；1-是
	private Date cancelTime;           //工单取消时间
	private String cancelReason;       //工单取消原因

	private Integer terminal;              //工单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；

	//满足云平台数据
	private Integer count;                  //数量
	private String tradeNo;                 //交易流水
	private Boolean needCompletePay;        //是否需要完款
	private Boolean completePay;            //是否完款
	private BigDecimal completePayFee;      //完款支付金额
	private Date completePayTime;           //完款时间
	private Integer completePayType;        //完款支付方式：1-微信；2-支付宝；3-POS机；4-转账；
	private String completeOutTradeNo;      //完款商户订单号
	private String completeTradeNo;         //完款交易订单号


	private Boolean chargeback;            //是否退单
	private Integer chargebackStatus;      //退单状态：(0,3)-待退单；1-退单中；2-退单成功；
	private Integer chargebackType;        //1-经销商退单；2-客服退单
	private Integer chargebackNum;         //退单号
	private String chargebackReason;       //退单原因
	private String chargebackRemark;       //退单备注
	private String chargebackSncode;       //退单设备SN码
	private Date chargebackTime;           //退单时间

	private Boolean refuse;                //拒绝工单：0-未拒绝；1-已拒绝
	private String refuseReason;           //拒单原因
	private Date refuseTime;               //拒单时间
	private String notRefuse;              //不能拒单状态: N、可以拒单；Y、不可以拒单

	private String protocol;               //协议编号
	private String confirmation;           //确认单号
	private String signContractCredential;//纸质合同凭证

	private Boolean discontinue;           //是否中止
	private Integer discontinueNum;        //中止号
	private String discontinueReason;      //中止原因
	private String discontinueRemark;      //中止备注

	private Boolean pay;                   //是否支付：0-否；1-是
	private Integer payType;               //支付方式：1-微信；2-支付宝；3-POS机；4-转账；
	private Date payTime;                  //支付时间
	private Integer payStatus;             //支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败
	private Integer payTerminal;           //支付端 1:经销商支付  2:用户支付
	private String payCredential;          //线下支付凭证
	private Date payCredentialSubmitTime;  //支付凭证提交时间
	private String notPassReason;          //其他支付审核不同过原因

	private String userAppraiseStatus;                //用户评价状态: N、未评价；Y、已评价
	private String userAppraiseContent;               //用户评价内容
	private Date userAppraiseTime;                    //用户评价时间

	private String distributorAppraiseStatus;         //经销商评价状态: N、未评价；Y、已评价
	private String distributorAppraiseContent;        //经销商评价内容
	private Date distributorAppraiseTime;             //经销商评价时间


	private Date countdownTime;                   //倒计时时间
	private Date appointTime;                     //安装工预约时间
	private String appointTimeLimit;            //时间段7-8
	private String appointStatus;                 //安装工是否预约：N、否；Y、是
	private String appointAddress;                //安装工预约地址
	private String appointRemark;                 //安装工预约备注

	private Integer nextStep;                      //下一步骤

	private String signStatus;              //用户是否签署合同：N、否；Y、是
	private Date signTime;                  //合同签约时间
	private String signOrderId;             //合同签约单号
	private String signUserName;//签约合同用户姓名
	private String signUserPhone;//签约合同用户手机号
	private String signUserIdCard;//签约合同用户身份证
	private String signUserEmail;//签约合同用户邮箱
	private String signUserAddress;//签约合同用户地址
	private String signClient;              //合同签约客户端
	private Integer contractValidity;       //合同有效性

	private String waterStatus;                    //原水水源信息输入状态：N、输入失败；Y、输入成功
	private String waterStatusText;                //原水源状态文本
	private String municipal;                    // 原水水源是否为市政自来水-1是/否0
	private String waterSourceDescription;         // 原水水源描述
	private String tds;                            // 原水TDS值
	private String hydraulic;                      // 原水水压值
	private String otherSource;                    //其他原水水源

	private String simCard;                        //sim卡
	private Date simCardTime;                      //sim卡输入日期
	private String logisticsCode;                  //物流编码(批次码)
	private String materielCode;                   //物料码
	private String supplierName;                   //生产商

	private String waterImages;                    //水质图片
	private Date uploadImgTime;                    //上传水质图片日期


	private Integer subDistributorId;                 //子账号经销商ID
	private String subDistributorName;                //子经销商姓名
	private String subDistributorPhone;               //子经销商手机号
	private String subDistributorAccount;             //子经销商账号


	private Boolean invoice;                //是否开票：0-不开票；1-开票
	private Integer invoiceType;            //开票类型：1-普通；2-增值税；
	private Integer invoiceHeaderType;      //开票抬头类型：1-公司；2-个人；
	private String invoiceTitle;            //开票抬头
	private String invoiceTaxNum;           //税号
	private String invoiceBank;             //开户行
	private String invoiceBankNum;          //开户号
	private String invoiceAddress;          //开票地址
	private String invoicePhone;            //开票手机号
	private String invoiceEmail;             //开票邮箱
	private Date invoiceTime;               //开票时间

	private Integer completeType;          //完成类型: 1、正常完成；2、非正常完成
	private Integer type;                             //工单类型：  1、经销商添加；2、用户自助下单

	private Boolean userConfirm;       //用户信息是否确认过了：0-否；1-是

	private Integer oldEnId;        //安装工转让，更新工单信息需要将工单上的安装工信息更新。

	//------------以上为整理过的需要保留的字段----下方的为待确认是否需要保留的字段---------------


	private Integer stationId;                        //服务站id
	private String stationName;                       //服务站名称

	private Date firstUpgradeTime;                    //经销商第一次升级时间

	private Integer distributorRoleId;                //经销商角色ID
	private String distributorRoleName;               //经销商角色名称
	private Integer distributorRoleLevel;             //经销商等级：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子）

	// private String recommendName;                 //经销商推荐人姓名
	// private String recommendProvince;                 //经销商推荐人所在省
	// private String recommendCity;                     //经销商推荐人所在市
	// private String recommendRegion;                   //经销商推荐人所在区
	private String userIdCard;                        //用户身份证号
	private String voteStatus;                        //评价状态：N、未评价；Y、已评价
	private Integer voteCount;                        //总评价数量
	private Integer goodVoteCount;                    //好评数量
	// private String productName;             //产品名称
	// private Integer productTypeId;          //产品类型id
	// private String productTypeName;         //产品类型名称
	// private Integer productRangeId;         //产品范围id
	// private String productRangeName;        //产品范围名称
	// private Integer productModelId;         //产品型号id
	// private String productModelName;        //产品型号名称

	private BigDecimal modelPrice;          //计费模板价格
	private Date billTime;                  //开票时间
	private String billEmail;               //开票邮箱
	private String companyName;             //公司名称
	private String billAddress;             //开票地址
	private String billPhone;               //开票电话
	private String dutyNo;                  //税号
	private Integer invoiceHead;            //发票抬头：1、公司；2、个人
	private String bankAccount;             //开户号
	private String bankName;                //开户行
	private Date confirmTime;               //发票确认时间

	private String contractId;              //合同编号

	private String confirmUserInfoStatus;          //个人信息确认状态
	private Boolean isUserOrder;                   //是否用户已支付

	private Integer scanCodeType;                  //扫描码类型：1、直播二维码；2、企业账号二维码；3、个人二维码；4、软文二维码；5、翼猫微商城；
	private String snCodeStatus;                   //SN码输入状态：N、输入失败；Y、输入成功
	private String simCardStatus;                  //sim卡输入状态：N、输入失败；Y、输入成功
	private String batchCodeStatus;                //物流编码(批次码)输入状态：N、输入失败；Y、输入成功

	private Date snCodeTime;                       //SN码输入日期
	private Date batchCodeTime;                    //物流编码(批次码)输入日期

	private Date activeTime;                       //设备激活时间

//    private String charging;                       //物料id

//    private String chargingName;                   //物料名


	private Date changeTime;                       //更换日期
	private String payCompleteOrderNo;             //完款订单号
	private Date payCompleteTime;                  //完款时间
	private BigDecimal payCompleteMoney;           //完款金额
	private Integer payCompletePayType;            //完款支付类型
	private String payCompleteTradeNo;             //完款交易单号
	private Date syncBaideTime;                    //同步百得时间
	private String acceptStatus;                   //安装工接单状态

	private String engineerIdCard;                //安装工身份证

	// private String backOrderStatus;                 //退单状态1:退单中；2：退单成功；3：待退单；N、未退单；
	// private String isBackWorkOrder;                 //是否取消订单：N、否；Y、是
	// private String cancelPerson;                    //工单取消人
	// private String cancelRemark;                    //工单取消备注
	// private Integer backRefundStatus;               //退款状态
	// private Integer backRefundResidueTime;          //退款剩余时间
	// private Integer backOrderType;                  //退单发起人类型
	// private Integer chargeBackMoneyMethod;          //退款类型
	// private Date chargeBackMoneyTime;               //退款时间
	// private BigDecimal refundMoney;                 //实际退款金额
	// private String chargeBackTradeOrder;            //退单交易单号


	/**
	 * -------------微服务--基础信息对象------BaseEntity ---start----------
	 */
	private Date updateTime;       //修改时间
	// private String createUser;     //创建用户
	private String updateUser;     //修改用户
	private String delStatus;//删除状态N:未删除,Y:已删除
	private Date deleteTime;       //删除时间
	// private String idStatus;       //id状态
	/**-------------微服务--基础信息对象------BaseEntity ---start----------*/

	/**
	 * -------------微服务所需---工单对象 ---start----------
	 */
	private String statusText;                       //工单状态文本
	// private String redAssemblyId;                    //红包配置id
	// private BigDecimal redAssemblyMoney;             //红包配置金额
	// private Date bindRedAssemblyTime;                //红包绑定时间
	// private String masterRedId;                      // 主账号红包
	// private Date padSnCodeTime;                      //水机sn码时间
	// private Integer serviceEngineerId;               //服务工程师id
	// private String serviceEngineerName;              //服务工程师姓名
	// private String serviceEngineerPhone;             //服务工程师手机号
	// private String serviceEngineerIdCard;            //服务工程师身份证
	// private BigDecimal serviceMoney;//服务金额
	private String dispatchTypeText;//派单类型文本
	private String payTerminalText;//支付端文本
	private String deleteRemark;//删除备注
	private String deleteReason;//删除原因
	private Integer otherPayType;//其他支付类型

	// private Date accountingTime;//其他支付审核时间
	// private Date userStatusTime;//用户当前状态时间
	// private Integer distributorVerifyBackOrderType;   //经销商审核退单类型
	// private String distributorVerifyBackOrderTypeText;//经销商审核退单类型文本
	// private String distributorVerifyBackOrderReason;//经销商审核退单类型原因
	// private Date distributorVerifyBackOrderTime;   //经销商审核退单单时间
	// private Date headquartersVerifyBackOrderTime; //总部审核退单时间
	private String workOrderIntallType;            //工单安装类型文本：1、经销商添加；2、用户自助下单
	private String oldCompleteOutTradeNo;//老的完成交易流水
	private String accountMonth;//账号月份
	private String modifyAddress;//修改地址
	// private Integer renewStatus;//续费状态
	// private String renewStatusText;//续费状态文本
	// private Integer renewTimes;//续费时间
	// private String addWorkOrderId;//添加工单id(老流程id)
	/**-------------微服务---工单对象 ---end----------*/

	/**
	 * -------------微服务---工单状态对象------WaterDeviceWorkOrderStatus ---start----------
	 */
	private String syncBaideStatus;   //同步百得状态: N、未同步；Y、已同步
	private String syncBaideFailureText;   //同步百得故障内容
	// private String workorderAcceptStatusText;//接单状态文本
	// private String redAssemblyStatus;//红包状态
	// private String accountingStatus;   //其他支付审核状态：0-审核中、1-审核通过、2-审核未通过
	// private String accountingStatusText;//其他支付审核状态文本
	// private String billStatus;        //开票状态
	// private String workorderBatchCodeStatusText;//批次码状态文本
	// private String workorderSncodeStatusText;//sn码状态文本
	// private String workorderSimcardStatusText;//sim码状态文本
	// private String workorderPayStatusText;//支付状态文本
	// private String workorderConfirmInformationStatusText;//用户确认信息状态文本
	// private String workorderSignContractStatusText;//签约合同状态文本
	// private String workorderBackStatusText;//退单状态文本
	/**-------------微服务--工单状态对象------WaterDeviceWorkOrderStatus ---end----------*/

	// /**-------------微服务--地址信息对象------BaseAddressEntity ---start----------*/
	// @ApiModelProperty(value = "国家")
	// private String addrCountry;    //国家
	// @ApiModelProperty(value = "国家id")
	// private String addrCountryId;  //国家id
	@ApiModelProperty(value = "经度")
	private String addrLongitude;  //经度
	@ApiModelProperty(value = "纬度")
	private String addrLatitude;   //纬度
	// /**-------------微服务--地址信息对象------BaseAddressEntity ---end----------*/

	/**
	 * -------------老数据映射字段---------------start----------
	 */
	private String oldSubOrderId;    //老的子订单号
	private String oldUserId;        //老的用户ID
	private String oldProductId;     //老的产品id
	private String oldDeviceId;      //老的设备ID
	private String oldDistributorId;    //老的经销商ID
	private String oldEngineerId;       //老的安装工ID
	private String oldServiceEngineerId; //老的服务工程师id
	private String oldCostId;            //老的计费方式ID
	private String oldStationId;          //老的服务站id
	private String oldSubDistributorId;   //老的子账号经销商ID
	private String oldDistributorRoleId;  //老的经销商角色ID
	private String oldProductTypeId;      //老的产品类型id
	private String oldProductRangeId;     //老的产品范围id
	private String oldProductModelId;     //老的产品型号id
	/**
	 * -------------老数据映射字段---------------end----------
	 */

	private String terminalText;          //工单来源文本

	/**
	 * ----------业务系统工单详情页展示 start-----------------
	 */
	private String receiveName;            //收货人姓名
	private String receivePhone;        //收货人联系方式
	private String receiveProvice;        //收货人省
	private String receiveCity;            //收货人市
	private String receiveRegion;        //收货人区
	private String receiveAdress;        //收货地址
	private String costTypeName;        //计费类型
	private String distributorFirstUpgradetime;//体验版经销商第一次升级时间
	private String userTypeName;//用户身份类型
	private String isTake;//提货状态
	private Integer createUserId;//下单人id
	private String createUserName;//下单人姓名
	private String createUserPhone;//下单人手机号
	private String completePayText;//是否完款文本
	private String engineerRegion;//安装工服务站
	private String settlementMonth;//结算月份=工单安装完成时间次月
	/**
	 * ----------业务系统工单详情页展示 end-----------------
	 */
}
