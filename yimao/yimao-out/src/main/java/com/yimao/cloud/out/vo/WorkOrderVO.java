// package com.yimao.cloud.out.vo;
//
// import com.yimao.cloud.base.utils.DateUtil;
// import com.yimao.cloud.base.utils.StringUtil;
// import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
//
// import lombok.Data;
// /*****
//  * @desc 安装工app返回列表
//  * @author zhangbaobao
//  * @date 2019/10/10
//  */
// @Data
// public class WorkOrderVO {
// 	private String id;
// 	private String workorderId;//工单号
// 	private String costId;//计费方式
// 	private String costName;//计费方式名称
// 	private double price;//金额
// 	private String deviceName;//产品型号
// 	private String deviceScope;//产品范围
// 	private String deviceScopeId;//产品范围Id
// 	private String name;//客户名称
// 	private String phone;//联系方式
// 	private String province;//省份
// 	private String city;//城市
// 	private String region;//地区
// 	private boolean isOnlineArea;//是否上线地区
// 	private String address;//地址
// 	private int count;//水机数量
// 	private String time;//安装时间
// 	private String remark;//备注
// 	private int state;//状态 (1 未受理,2 已受理,3 处理中,4 已完成)
// 	private int paystate;//支付状态(0 未支付,1 审核中,2 未通过,3已支付)
// 	private int score;//分数
// 	private String scoreText;//分数描述
// 	private String userScoreText;//用户评价描述
// 	private String content;//评价
// 	private String createTime;//创建时间
// 	private String distributor;//经销商
// 	private String disphone;//经销商电话
// 	private String customer;//客服姓名
// 	private String cusphone;//客服电话
// 	private String sncode;//sn码
// 	private String excuse;//审核不通过原因
// 	private double money;//总金额
// 	private int step;//步骤
//
// 	private String company;//公司
// 	private String legal;//公司法人
// 	private String industry;//公司行业
// 	private String dimensions;//公司规模
// 	private String image;//公司营业执照
//
// 	private String companyName;//公司名称
//
// 	private int usercount;//家庭人口数
// 	private String hobby;//兴趣
// 	private String degree;//文化程度
// 	private int childAge;//子女年龄
// 	private String childSex;//子女性别
// 	private boolean haveChild;//是否有子女
// 	private boolean haveOld;//是否有老人
// 	private boolean marry;//子女是否结婚
// 	private boolean studyAbroad;//是否考虑留学
// 	private String mail;//邮箱地址==>>2018.10.23调整为了用户邮箱
// 	private String serviceSitemail;//服务站邮箱地址
//
// 	private boolean isBilling;//是否开票
// 	private int completeType;//完成类型
// 	private int pass;//申请退单状态（1 审核中 ）
// 	private boolean discontinue;//是否中止
// 	private boolean chargeback;//是否退单
// 	private String chargebackreason;//非正常完成原因
// 	private String discontinuereason;//中止原因
// 	private String chargebackremark;//非正常完成备注
// 	private String discontinueremark;//中止备注
// 	private String operationTime;//操作时间
// 	private int allot;//是否派单
// 	private int payterminal;//支付端
//
// 	private String productType;
// 	private String productModel;
//
// 	/**
// 	 * 用户类别
// 	 */
// 	private int userType;
//
// 	/**
// 	 * 是否有红包
// 	 */
// 	private boolean redAssembly;
// 	/**
// 	 * 红包金额
// 	 */
// 	private double redAssemblyMoney;
// 	/**
// 	 * 工单code
// 	 */
// 	private String code;
// 	/**
// 	 * 子账户姓名
// 	 */
// 	private String childRealName;
// 	/**
// 	 * 子账户用户名
// 	 */
// 	private String childName;
// 	/**
// 	 * 子账号经销商id
// 	 */
// 	private String childDistributorId;
// 	/**
// 	 * 来源
// 	 */
// 	private String sourceStr;
//
// 	private String distributorId;
//
// 	/**
// 	 * 续费状态
// 	 */
// 	private int renewStatus;
// 	private String renewStatusZHText;
// 	private int renewTimes;
//
// 	/**
// 	 * 批量订单号
// 	 */
// 	private String addWorkOrderId;
//
// 	public WorkOrderVO(){
//
// 	}
//
// 	public WorkOrderVO(WorkOrderDTO workOrderDTO){
// 		this.id=workOrderDTO.getId();
// 		// this.workorderId=workOrderDTO.getAddWorkOrderId();
// 		this.costId=String.valueOf(workOrderDTO.getCostId());
// 		this.time=DateUtil.getDateToString(workOrderDTO.getServiceTime(),DateUtil.defaultDateTimeFormat);
// 		this.createTime=DateUtil.getDateToString(workOrderDTO.getCreateTime(),DateUtil.defaultDateTimeFormat);
// 		this.province=workOrderDTO.getProvince();
// 		this.city=workOrderDTO.getCity();
// 		this.region=workOrderDTO.getRegion();
// 		this.address=workOrderDTO.getAddress();
// 		this.step=workOrderDTO.getStep().intValue();
// 		this.paystate=workOrderDTO.getPayStatus().intValue();
// 		this.distributorId=String.valueOf(workOrderDTO.getDistributorId());
// 		this.name=workOrderDTO.getUserName();
// 		this.phone=workOrderDTO.getUserPhone();
// 		this.deviceName=workOrderDTO.getDeviceModel();
// 		this.deviceScope=workOrderDTO.getProductRangeName();//产品范围
// 		this.completeType=workOrderDTO.getCompleteType();
// 		this.isBilling=workOrderDTO.getInvoice();
// 		this.chargeback=(!StringUtil.isEmpty(workOrderDTO.getIsBackWorkOrder())&&workOrderDTO.getIsBackWorkOrder().equals("Y"))?true:false;
// 		this.chargebackremark=workOrderDTO.getCancelRemark();
// 		this.excuse=workOrderDTO.getNotPassReason();
// 		this.chargebackreason=workOrderDTO.getCancelRemark();
// 		this.count=workOrderDTO.getCount().intValue();
// 		this.remark=workOrderDTO.getRemark();
// 		this.score=workOrderDTO.getUserScore().intValue();
// 		this.operationTime=DateUtil.getDateToString(workOrderDTO.getOperationTime(),DateUtil.defaultDateTimeFormat);
//
// 	}
//
// }