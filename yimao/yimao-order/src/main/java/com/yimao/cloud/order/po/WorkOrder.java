package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：水机安装工单
 *
 * @Author Zhang Bo
 * @Date 2019/2/26 15:19
 * @Version 1.0
 */
@Table(name = "workorder")
@Getter
@Setter
public class WorkOrder {

    @Id
    private String id;                 //工单号（区分带字母的维修、维护和新老工单）
    private Long mainOrderId;          //主订单号
    private Long subOrderId;           //子订单号
    private Integer status;            //工单状态：1-未受理；2-已受理；3-处理中；4-已完成；
    private Integer userId;            //水机用户ID
    private String userName;           //用户姓名
    private String userPhone;          //用户手机号
    private Integer userScore;         //用户评分
    private String userContent;        //用户评价
    private Integer productId;         //产品ID
    private String productName;        //产品名称
    private Integer deviceId;          //设备ID
    private String sn;                 //设备SN码
    private String deviceModel;        //设备型号
    private BigDecimal fee;            //金额（租赁费+安装费）
    private BigDecimal openAccountFee; //开户费
    private Integer distributorId;     //经销商ID
    private Integer distributorType;     //经销商类型
    private String distributorTypeName;     //经销商类型名称
    private String distributorIdCard;     //经销商身份证号码
    private String distributorName;    //经销商姓名
    private String distributorAccount;                //经销商账号
    private String distributorPhone;   //经销商手机号
    private String distributorProvince;   //经销商省
    private String distributorCity;   //经销商市
    private String distributorRegion;   //经销商区

    private Integer distributorRoleId;                //经销商角色ID
    private String distributorRoleName;               //经销商角色名称
    private Integer distributorRoleLevel;             //经销商等级：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子）

    private Integer subDistributorId;                 //子账号经销商ID
    private String subDistributorName;                //子经销商姓名
    private String subDistributorPhone;               //子经销商手机号
    private String subDistributorAccount;             //子经销商账号

    private Integer engineerId;        //安装工ID
    private String engineerName;       //安装工姓名
    private String engineerPhone;      //安装工手机号

    private String province;           //省
    private String city;               //市
    private String region;             //区县
    private String address;            //地址
    private String addressDetail;      //地址详情
    private String addrLatitude;//纬度
    private String addrLongitude;//经度

    private Integer dispatchType;      //派单方式：1-用户派单；2-系统派单
    private Boolean dispatch;          //是否需要手动分配安装工：0-否；1-是
    private Date dispatchTime;         //派单时间
    private String remark;             //备注
    private Integer costId;            //计费方式ID
    private Integer costType;          //计费方式：1-流量计费；2-包年计费；
    private String costName;           //计费方式名称
    private Integer step;              //工单安装到了第几步骤
    private Integer nextStep;          //下一步骤

    private Date createTime;           //工单创建时间
    private Date serviceTime;          //用户下单时选择的安装时间
    private Date acceptTime;           //安装工接单时间
    private Date pickTime;             //提货时间
    private Date operationTime;        //安装工实际操作安装时间
    private Date completeTime;         //安装完成时间
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
    private Integer chargebackStatus;      //退单状态：0-待退单；1-退单中；2-退单成功；
    private Integer chargebackType;        //1-经销商退单；2-客服退单
    private Integer chargebackNum;         //退单号（可能是中止原因对应的code）
    private String chargebackReason;       //退单原因
    private String chargebackRemark;       //退单备注
    private String chargebackSncode;       //退单设备SN码
    private Date chargebackTime;           //退单时间

    private Boolean refuse;                //拒绝工单：0-未拒绝；1-已拒绝
    private String refuseReason;           //拒单原因
    private String refuseRemark;           //拒单备注
    private Date refuseTime;               //拒单时间
    private String notRefuse;              //不能拒单状态: N、可以拒单；Y、不可以拒单

    private String protocol;               //协议编号
    private String confirmation;           //确认单号
    private String signContractCredential;//纸质合同凭证

    private Boolean discontinue;           //是否中止
    private Integer discontinueNum;        //中止号（可能是中止原因对应的code）
    private String discontinueReason;      //中止原因
    private String discontinueRemark;      //中止备注

    private Boolean pay;                   //是否支付：0-否；1-是
    private Integer payType;               //支付方式：1-微信；2-支付宝；3-POS机；4-转账；
    private Date payTime;                  //支付时间
    private Integer payStatus;             //支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败
    private Integer payTerminal;           //支付端 1-立即支付；2-货到付款；
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
    private String appointTimeLimit;                     //安装工预约时间段,例如上午8点-9点
    private String appointStatus;                 //安装工是否预约：N、否；Y、是
    private String appointAddress;                //安装工预约地址
    private String appointRemark;                 //安装工预约备注


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

    private Boolean invoice;                //是否开票：0-不开票；1-开票
    private Integer invoiceType;            //开票类型：1-普通；2-增值税；
    private Integer invoiceHeaderType;      //开票抬头类型：1-公司；2-个人；
    private String invoiceTitle;            //开票抬头
    private String invoiceTaxNum;           //税号
    private String invoiceBank;             //开户行
    private String invoiceBankNum;          //开户号
    private String invoiceAddress;          //开票地址
    private String invoicePhone;            //开票手机号
    private String invoiceEmail;            //开票邮箱
    private Date invoiceTime;               //开票时间

    private Integer completeType;          //完成类型: 1、正常完成；2、非正常完成
    private Integer type;                             //工单类型：  1、经销商添加；2、用户自助下单

    private Boolean userConfirm;       //用户信息是否确认过了：0-否；1-是

    //------------以上为整理过的需要保留的字段----下方的为待确认是否需要保留的字段---------------


    private Integer stationId;                        //服务站id
    private String stationName;                       //服务站名称

    private Date firstUpgradeTime;                    //经销商第一次升级时间


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
    private String workOrderIntallType;            //工单安装类型文本：  1、经销商添加；2、用户自助下单
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
    // private String addrCountry;    //国家
    // private String addrCountryId;  //国家id
    // private String addrLongitude;  //经度
    // private String addrLatitude;   //纬度
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

    public WorkOrder() {
    }

    /**
     * 用业务对象WorkOrderDTO初始化数据库对象WorkOrder。
     *
     * @param dto 业务对象
     */
    public WorkOrder(WorkOrderDTO dto) {
        this.id = dto.getId();
        this.mainOrderId = dto.getMainOrderId();
        this.subOrderId = dto.getSubOrderId();
        this.status = dto.getStatus();
        this.userId = dto.getUserId();
        this.userName = dto.getUserName();
        this.userPhone = dto.getUserPhone();
        this.userScore = dto.getUserScore();
        this.userContent = dto.getUserContent();
        this.productId = dto.getProductId();
        this.deviceId = dto.getDeviceId();
        this.sn = dto.getSn();
        this.deviceModel = dto.getDeviceModel();
        this.fee = dto.getFee();
        this.openAccountFee = dto.getOpenAccountFee();
        this.distributorId = dto.getDistributorId();
        this.distributorType = dto.getDistributorType();
        this.distributorTypeName = dto.getDistributorTypeName();
        this.distributorIdCard = dto.getDistributorIdCard();
        this.distributorName = dto.getDistributorName();
        this.distributorAccount = dto.getDistributorAccount();
        this.distributorPhone = dto.getDistributorPhone();
        this.distributorProvince = dto.getDistributorProvince();
        this.distributorCity = dto.getDistributorCity();
        this.distributorRegion = dto.getDistributorRegion();
        this.engineerId = dto.getEngineerId();
        this.engineerName = dto.getEngineerName();
        this.engineerPhone = dto.getEngineerPhone();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.address = dto.getAddress();
        this.addressDetail = dto.getAddressDetail();
        this.dispatchType = dto.getDispatchType();
        this.dispatch = dto.getDispatch();
        this.dispatchTime = dto.getDispatchTime();
        this.remark = dto.getRemark();
        this.costId = dto.getCostId();
        this.costType = dto.getCostType();
        this.createTime = dto.getCreateTime();
        this.serviceTime = dto.getServiceTime();
        this.step = dto.getStep();
        this.nextStep = dto.getNextStep();
        this.acceptTime = dto.getAcceptTime();
        this.pickTime = dto.getPickTime();
        this.operationTime = dto.getOperationTime();
        this.completeTime = dto.getCompleteTime();
        this.cancelTime = dto.getCancelTime();
        this.cancelReason = dto.getCancelReason();
        this.terminal = dto.getTerminal();
        this.count = dto.getCount();
        this.tradeNo = dto.getTradeNo();
        this.needCompletePay = dto.getNeedCompletePay();
        this.completePay = dto.getCompletePay();
        this.completePayTime = dto.getCompletePayTime();
        this.completeOutTradeNo = dto.getCompleteOutTradeNo();
        this.completeTradeNo = dto.getCompleteTradeNo();
        this.chargeback = dto.getChargeback();
        this.chargebackStatus = dto.getChargebackStatus();
        this.chargebackType = dto.getChargebackType();
        this.chargebackNum = dto.getChargebackNum();
        this.chargebackReason = dto.getChargebackReason();
        this.chargebackRemark = dto.getChargebackRemark();
        this.chargebackSncode = dto.getChargebackSncode();
        this.chargebackTime = dto.getChargebackTime();
        this.refuse = dto.getRefuse();
        this.refuseReason = dto.getRefuseReason();
        this.refuseTime = dto.getRefuseTime();
        this.notRefuse = dto.getNotRefuse();
        this.protocol = dto.getProtocol();
        this.confirmation = dto.getConfirmation();
        this.discontinue = dto.getDiscontinue();
        this.discontinueNum = dto.getDiscontinueNum();
        this.discontinueReason = dto.getDiscontinueReason();
        this.discontinueRemark = dto.getDiscontinueRemark();
        this.pay = dto.getPay();
        this.payType = dto.getPayType();
        this.payTime = dto.getPayTime();
        this.payStatus = dto.getPayStatus();
        this.payTerminal = dto.getPayTerminal();
        this.payCredential = dto.getPayCredential();
        this.payCredentialSubmitTime = dto.getPayCredentialSubmitTime();
        this.notPassReason = dto.getNotPassReason();
        this.userAppraiseStatus = dto.getUserAppraiseStatus();
        this.userAppraiseContent = dto.getUserAppraiseContent();
        this.userAppraiseTime = dto.getUserAppraiseTime();
        this.distributorAppraiseStatus = dto.getDistributorAppraiseStatus();
        this.distributorAppraiseContent = dto.getDistributorAppraiseContent();
        this.distributorAppraiseTime = dto.getDistributorAppraiseTime();
        this.countdownTime = dto.getCountdownTime();
        this.appointTime = dto.getAppointTime();
        this.appointStatus = dto.getAppointStatus();
        this.appointAddress = dto.getAppointAddress();
        this.appointRemark = dto.getAppointRemark();
        this.signStatus = dto.getSignStatus();
        this.signTime = dto.getSignTime();
        this.signOrderId = dto.getSignOrderId();
        this.signUserName = dto.getSignUserName();
        this.signUserPhone = dto.getSignUserPhone();
        this.signUserIdCard = dto.getSignUserIdCard();
        this.signUserEmail = dto.getSignUserEmail();
        this.signUserAddress = dto.getSignUserAddress();
        this.signClient = dto.getSignClient();
        this.contractValidity = dto.getContractValidity();
        this.waterStatus = dto.getWaterStatus();
        this.waterStatusText = dto.getWaterStatusText();
        this.municipal = dto.getMunicipal();
        this.waterSourceDescription = dto.getWaterSourceDescription();
        this.tds = dto.getTds();
        this.hydraulic = dto.getHydraulic();
        this.otherSource = dto.getOtherSource();
        this.simCard = dto.getSimCard();
        this.simCardTime = dto.getSimCardTime();
        this.logisticsCode = dto.getLogisticsCode();
        this.materielCode = dto.getMaterielCode();
        this.supplierName = dto.getSupplierName();
        this.waterImages = dto.getWaterImages();
        this.uploadImgTime = dto.getUploadImgTime();
        this.subDistributorId = dto.getSubDistributorId();
        this.subDistributorName = dto.getSubDistributorName();
        this.subDistributorPhone = dto.getSubDistributorPhone();
        this.subDistributorAccount = dto.getSubDistributorAccount();
        this.invoice = dto.getInvoice();
        this.invoiceType = dto.getInvoiceType();
        this.invoiceHeaderType = dto.getInvoiceHeaderType();
        this.invoiceTitle = dto.getInvoiceTitle();
        this.invoiceTaxNum = dto.getInvoiceTaxNum();
        this.invoiceBank = dto.getInvoiceBank();
        this.invoiceBankNum = dto.getInvoiceBankNum();
        this.invoiceAddress = dto.getInvoiceAddress();
        this.invoicePhone = dto.getInvoicePhone();
        this.invoiceEmail = dto.getInvoiceEmail();
        this.invoiceTime = dto.getInvoiceTime();
        this.completeType = dto.getCompleteType();
        this.type = dto.getType();
        this.userConfirm = dto.getUserConfirm();
        this.stationId = dto.getStationId();
        this.stationName = dto.getStationName();
        this.firstUpgradeTime = dto.getFirstUpgradeTime();
        this.distributorRoleId = dto.getDistributorRoleId();
        this.distributorRoleName = dto.getDistributorRoleName();
        this.distributorRoleLevel = dto.getDistributorRoleLevel();
        this.userIdCard = dto.getUserIdCard();
        this.voteStatus = dto.getVoteStatus();
        this.voteCount = dto.getVoteCount();
        this.goodVoteCount = dto.getGoodVoteCount();
        this.productName = dto.getProductName();
        // this.productTypeId = dto.getProductTypeId();
        // this.productTypeName = dto.getProductTypeName();
        // this.productRangeId = dto.getProductRangeId();
        // this.productRangeName = dto.getProductRangeName();
        // this.productModelId = dto.getProductModelId();
        // this.productModelName = dto.getProductModelName();
        this.costName = dto.getCostName();
        this.modelPrice = dto.getModelPrice();
        this.billTime = dto.getBillTime();
        this.billEmail = dto.getBillEmail();
        this.companyName = dto.getCompanyName();
        this.billAddress = dto.getBillAddress();
        this.billPhone = dto.getBillPhone();
        this.dutyNo = dto.getDutyNo();
        this.invoiceHead = dto.getInvoiceHead();
        this.contractId = dto.getContractId();
        this.confirmUserInfoStatus = dto.getConfirmUserInfoStatus();
        this.isUserOrder = dto.getIsUserOrder();
        this.scanCodeType = dto.getScanCodeType();
        this.snCodeStatus = dto.getSnCodeStatus();
        this.simCardStatus = dto.getSimCardStatus();
        this.batchCodeStatus = dto.getBatchCodeStatus();
        this.snCodeTime = dto.getSnCodeTime();
        this.batchCodeTime = dto.getBatchCodeTime();
        this.activeTime = dto.getActiveTime();
        this.changeTime = dto.getChangeTime();
        this.payCompleteOrderNo = dto.getPayCompleteOrderNo();
        this.payCompleteTime = dto.getPayCompleteTime();
        this.payCompleteMoney = dto.getPayCompleteMoney();
        this.payCompletePayType = dto.getPayCompletePayType();
        this.payCompleteTradeNo = dto.getPayCompleteTradeNo();
        this.syncBaideTime = dto.getSyncBaideTime();
        this.acceptStatus = dto.getAcceptStatus();
        this.engineerIdCard = dto.getEngineerIdCard();
        // this.backOrderStatus = dto.getBackOrderStatus();
        // this.isBackWorkOrder = dto.getIsBackWorkOrder();
        // this.cancelPerson = dto.getCancelPerson();
        // this.cancelRemark = dto.getCancelRemark();
        // this.backRefundStatus = dto.getBackRefundStatus();
        // this.backRefundResidueTime = dto.getBackRefundResidueTime();
        // this.backOrderType = dto.getBackOrderType();
        // this.chargeBackMoneyMethod = dto.getChargeBackMoneyMethod();
        // this.chargeBackMoneyTime = dto.getChargeBackMoneyTime();
        // this.refundMoney = dto.getRefundMoney();
        // this.chargeBackTradeOrder = dto.getChargeBackTradeOrder();
        this.updateTime = dto.getUpdateTime();
        // this.createUser = dto.getCreateUser();
        this.updateUser = dto.getUpdateUser();
        this.delStatus = dto.getDelStatus();
        this.deleteTime = dto.getDeleteTime();
        // this.idStatus = dto.getIdStatus();
        this.statusText = dto.getStatusText();
        // this.redAssemblyId = dto.getRedAssemblyId();
        // this.redAssemblyMoney = dto.getRedAssemblyMoney();
        // this.bindRedAssemblyTime = dto.getBindRedAssemblyTime();
        // this.masterRedId = dto.getMasterRedId();
        // this.padSnCodeTime = dto.getPadSnCodeTime();
        // this.serviceEngineerId = dto.getServiceEngineerId();
        // this.serviceEngineerName = dto.getServiceEngineerName();
        // this.serviceEngineerPhone = dto.getServiceEngineerPhone();
        // this.serviceEngineerIdCard = dto.getServiceEngineerIdCard();
        // this.serviceMoney = dto.getServiceMoney();
        this.dispatchTypeText = dto.getDispatchTypeText();
        this.payTerminalText = dto.getPayTerminalText();
        this.deleteRemark = dto.getDeleteRemark();
        this.deleteReason = dto.getDeleteReason();
        this.otherPayType = dto.getOtherPayType();
        // this.accountingTime = dto.getAccountingTime();
        // this.userStatusTime = dto.getUserStatusTime();
        // this.distributorVerifyBackOrderType = dto.getDistributorVerifyBackOrderType();
        // this.distributorVerifyBackOrderTypeText = dto.getDistributorVerifyBackOrderTypeText();
        // this.distributorVerifyBackOrderReason = dto.getDistributorVerifyBackOrderReason();
        // this.distributorVerifyBackOrderTime = dto.getDistributorVerifyBackOrderTime();
        // this.headquartersVerifyBackOrderTime = dto.getHeadquartersVerifyBackOrderTime();
        this.workOrderIntallType = dto.getWorkOrderIntallType();
        this.oldCompleteOutTradeNo = dto.getOldCompleteOutTradeNo();
        this.accountMonth = dto.getAccountMonth();
        this.modifyAddress = dto.getModifyAddress();
        // this.renewStatus = dto.getRenewStatus();
        // this.renewStatusText = dto.getRenewStatusText();
        // this.renewTimes = dto.getRenewTimes();
        this.syncBaideStatus = dto.getSyncBaideStatus();
        // this.workorderAcceptStatusText = dto.getWorkorderAcceptStatusText();
        // this.redAssemblyStatus = dto.getRedAssemblyStatus();
        // this.accountingStatus = dto.getAccountingStatus();
        // this.accountingStatusText = dto.getAccountingStatusText();
        // this.workorderBatchCodeStatusText = dto.getWorkorderBatchCodeStatusText();
        // this.workorderSncodeStatusText = dto.getWorkorderSncodeStatusText();
        // this.workorderSimcardStatusText = dto.getWorkorderSimcardStatusText();
        // this.workorderPayStatusText = dto.getWorkorderPayStatusText();
        // this.workorderConfirmInformationStatusText = dto.getWorkorderConfirmInformationStatusText();
        // this.workorderSignContractStatusText = dto.getWorkorderSignContractStatusText();
        // this.workorderBackStatusText = dto.getWorkorderBackStatusText();
        this.syncBaideFailureText = dto.getSyncBaideFailureText();
        this.oldSubOrderId = dto.getOldSubOrderId();
        this.oldUserId = dto.getOldUserId();
        this.oldProductId = dto.getOldProductId();
        this.oldDeviceId = dto.getOldDeviceId();
        this.oldDistributorId = dto.getOldDistributorId();
        this.oldEngineerId = dto.getOldEngineerId();
        this.oldServiceEngineerId = dto.getOldServiceEngineerId();
        this.oldCostId = dto.getOldCostId();
        this.oldStationId = dto.getOldStationId();
        this.oldSubDistributorId = dto.getOldSubDistributorId();
        this.oldDistributorRoleId = dto.getOldDistributorRoleId();
        this.oldProductTypeId = dto.getOldProductTypeId();
        this.oldProductRangeId = dto.getOldProductRangeId();
        this.oldProductModelId = dto.getOldProductModelId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WorkOrderDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(WorkOrderDTO dto) {
        dto.setId(this.id);
        dto.setMainOrderId(this.mainOrderId);
        dto.setSubOrderId(this.subOrderId);
        dto.setStatus(this.status);
        dto.setUserId(this.userId);
        dto.setUserName(this.userName);
        dto.setUserPhone(this.userPhone);
        dto.setUserScore(this.userScore);
        dto.setUserContent(this.userContent);
        dto.setProductId(this.productId);
        dto.setDeviceId(this.deviceId);
        dto.setSn(this.sn);
        dto.setDeviceModel(this.deviceModel);
        dto.setFee(this.fee);
        dto.setOpenAccountFee(this.openAccountFee);
        dto.setDistributorId(this.distributorId);
        dto.setDistributorType(this.distributorType);
        dto.setDistributorTypeName(this.distributorTypeName);
        dto.setDistributorIdCard(this.distributorIdCard);
        dto.setDistributorName(this.distributorName);
        dto.setDistributorAccount(this.distributorAccount);
        dto.setDistributorPhone(this.distributorPhone);
        dto.setDistributorProvince(this.distributorProvince);
        dto.setDistributorCity(this.distributorCity);
        dto.setDistributorRegion(this.distributorRegion);
        dto.setEngineerId(this.engineerId);
        dto.setEngineerName(this.engineerName);
        dto.setEngineerPhone(this.engineerPhone);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAddress(this.address);
        dto.setAddressDetail(this.addressDetail);
        dto.setDispatchType(this.dispatchType);
        dto.setDispatch(this.dispatch);
        dto.setDispatchTime(this.dispatchTime);
        dto.setRemark(this.remark);
        dto.setCostId(this.costId);
        dto.setCostType(this.costType);
        dto.setCreateTime(this.createTime);
        dto.setServiceTime(this.serviceTime);
        dto.setStep(this.step);
        dto.setNextStep(this.nextStep);
        dto.setAcceptTime(this.acceptTime);
        dto.setPickTime(this.pickTime);
        dto.setOperationTime(this.operationTime);
        dto.setCompleteTime(this.completeTime);
        dto.setCancelTime(this.cancelTime);
        dto.setCancelReason(this.cancelReason);
        dto.setTerminal(this.terminal);
        dto.setCount(this.count);
        dto.setTradeNo(this.tradeNo);
        dto.setNeedCompletePay(this.needCompletePay);
        dto.setCompletePay(this.completePay);
        dto.setCompletePayTime(this.completePayTime);
        dto.setCompleteOutTradeNo(this.completeOutTradeNo);
        dto.setCompleteTradeNo(this.completeTradeNo);
        dto.setChargeback(this.chargeback);
        dto.setChargebackStatus(this.chargebackStatus);
        dto.setChargebackType(this.chargebackType);
        dto.setChargebackNum(this.chargebackNum);
        dto.setChargebackReason(this.chargebackReason);
        dto.setChargebackRemark(this.chargebackRemark);
        dto.setChargebackSncode(this.chargebackSncode);
        dto.setChargebackTime(this.chargebackTime);
        dto.setRefuse(this.refuse);
        dto.setRefuseReason(this.refuseReason);
        dto.setRefuseTime(this.refuseTime);
        dto.setNotRefuse(this.notRefuse);
        dto.setProtocol(this.protocol);
        dto.setConfirmation(this.confirmation);
        dto.setDiscontinue(this.discontinue);
        dto.setDiscontinueNum(this.discontinueNum);
        dto.setDiscontinueReason(this.discontinueReason);
        dto.setDiscontinueRemark(this.discontinueRemark);
        dto.setPay(this.pay);
        dto.setPayType(this.payType);
        dto.setPayTime(this.payTime);
        dto.setPayStatus(this.payStatus);
        dto.setPayTerminal(this.payTerminal);
        dto.setPayCredential(this.payCredential);
        dto.setPayCredentialSubmitTime(this.payCredentialSubmitTime);
        dto.setNotPassReason(this.notPassReason);
        dto.setUserAppraiseStatus(this.userAppraiseStatus);
        dto.setUserAppraiseContent(this.userAppraiseContent);
        dto.setUserAppraiseTime(this.userAppraiseTime);
        dto.setDistributorAppraiseStatus(this.distributorAppraiseStatus);
        dto.setDistributorAppraiseContent(this.distributorAppraiseContent);
        dto.setDistributorAppraiseTime(this.distributorAppraiseTime);
        dto.setCountdownTime(this.countdownTime);
        dto.setAppointTime(this.appointTime);
        dto.setAppointStatus(this.appointStatus);
        dto.setAppointAddress(this.appointAddress);
        dto.setAppointRemark(this.appointRemark);
        dto.setSignStatus(this.signStatus);
        dto.setSignTime(this.signTime);
        dto.setSignOrderId(this.signOrderId);
        dto.setSignUserName(this.signUserName);
        dto.setSignUserPhone(this.signUserPhone);
        dto.setSignUserIdCard(this.signUserIdCard);
        dto.setSignUserEmail(this.signUserEmail);
        dto.setSignUserAddress(this.signUserAddress);
        dto.setSignClient(this.signClient);
        dto.setContractValidity(this.contractValidity);
        dto.setWaterStatus(this.waterStatus);
        dto.setWaterStatusText(this.waterStatusText);
        dto.setMunicipal(this.municipal);
        dto.setWaterSourceDescription(this.waterSourceDescription);
        dto.setTds(this.tds);
        dto.setHydraulic(this.hydraulic);
        dto.setOtherSource(this.otherSource);
        dto.setSimCard(this.simCard);
        dto.setSimCardTime(this.simCardTime);
        dto.setLogisticsCode(this.logisticsCode);
        dto.setMaterielCode(this.materielCode);
        dto.setSupplierName(this.supplierName);
        dto.setWaterImages(this.waterImages);
        dto.setUploadImgTime(this.uploadImgTime);
        dto.setSubDistributorId(this.subDistributorId);
        dto.setSubDistributorName(this.subDistributorName);
        dto.setSubDistributorPhone(this.subDistributorPhone);
        dto.setSubDistributorAccount(this.subDistributorAccount);
        dto.setInvoice(this.invoice);
        dto.setInvoiceType(this.invoiceType);
        dto.setInvoiceHeaderType(this.invoiceHeaderType);
        dto.setInvoiceTitle(this.invoiceTitle);
        dto.setInvoiceTaxNum(this.invoiceTaxNum);
        dto.setInvoiceBank(this.invoiceBank);
        dto.setInvoiceBankNum(this.invoiceBankNum);
        dto.setInvoiceAddress(this.invoiceAddress);
        dto.setInvoicePhone(this.invoicePhone);
        dto.setInvoiceEmail(this.invoiceEmail);
        dto.setInvoiceTime(this.invoiceTime);
        dto.setCompleteType(this.completeType);
        dto.setType(this.type);
        dto.setUserConfirm(this.userConfirm);
        dto.setStationId(this.stationId);
        dto.setStationName(this.stationName);
        dto.setFirstUpgradeTime(this.firstUpgradeTime);
        dto.setDistributorRoleId(this.distributorRoleId);
        dto.setDistributorRoleName(this.distributorRoleName);
        dto.setDistributorRoleLevel(this.distributorRoleLevel);
        dto.setUserIdCard(this.userIdCard);
        dto.setVoteStatus(this.voteStatus);
        dto.setVoteCount(this.voteCount);
        dto.setGoodVoteCount(this.goodVoteCount);
        dto.setProductName(this.productName);
        // dto.setProductTypeId(this.productTypeId);
        // dto.setProductTypeName(this.productTypeName);
        // dto.setProductRangeId(this.productRangeId);
        // dto.setProductRangeName(this.productRangeName);
        // dto.setProductModelId(this.productModelId);
        // dto.setProductModelName(this.productModelName);
        dto.setCostName(this.costName);
        dto.setModelPrice(this.modelPrice);
        dto.setBillTime(this.billTime);
        dto.setBillEmail(this.billEmail);
        dto.setCompanyName(this.companyName);
        dto.setBillAddress(this.billAddress);
        dto.setBillPhone(this.billPhone);
        dto.setDutyNo(this.dutyNo);
        dto.setInvoiceHead(this.invoiceHead);
        dto.setContractId(this.contractId);
        dto.setConfirmUserInfoStatus(this.confirmUserInfoStatus);
        dto.setIsUserOrder(this.isUserOrder);
        dto.setScanCodeType(this.scanCodeType);
        dto.setSnCodeStatus(this.snCodeStatus);
        dto.setSimCardStatus(this.simCardStatus);
        dto.setBatchCodeStatus(this.batchCodeStatus);
        dto.setSnCodeTime(this.snCodeTime);
        dto.setBatchCodeTime(this.batchCodeTime);
        dto.setActiveTime(this.activeTime);
        dto.setChangeTime(this.changeTime);
        dto.setPayCompleteOrderNo(this.payCompleteOrderNo);
        dto.setPayCompleteTime(this.payCompleteTime);
        dto.setPayCompleteMoney(this.payCompleteMoney);
        dto.setPayCompletePayType(this.payCompletePayType);
        dto.setPayCompleteTradeNo(this.payCompleteTradeNo);
        dto.setSyncBaideTime(this.syncBaideTime);
        dto.setAcceptStatus(this.acceptStatus);
        dto.setEngineerIdCard(this.engineerIdCard);
        // dto.setBackOrderStatus(this.backOrderStatus);
        // dto.setIsBackWorkOrder(this.isBackWorkOrder);
        // dto.setCancelPerson(this.cancelPerson);
        // dto.setCancelRemark(this.cancelRemark);
        // dto.setBackRefundStatus(this.backRefundStatus);
        // dto.setBackRefundResidueTime(this.backRefundResidueTime);
        // dto.setBackOrderType(this.backOrderType);
        // dto.setChargeBackMoneyMethod(this.chargeBackMoneyMethod);
        // dto.setChargeBackMoneyTime(this.chargeBackMoneyTime);
        // dto.setRefundMoney(this.refundMoney);
        // dto.setChargeBackTradeOrder(this.chargeBackTradeOrder);
        dto.setUpdateTime(this.updateTime);
        // dto.setCreateUser(this.createUser);
        dto.setUpdateUser(this.updateUser);
        dto.setDelStatus(this.delStatus);
        dto.setDeleteTime(this.deleteTime);
        // dto.setIdStatus(this.idStatus);
        dto.setStatusText(this.statusText);
        // dto.setRedAssemblyId(this.redAssemblyId);
        // dto.setRedAssemblyMoney(this.redAssemblyMoney);
        // dto.setBindRedAssemblyTime(this.bindRedAssemblyTime);
        // dto.setMasterRedId(this.masterRedId);
        // dto.setPadSnCodeTime(this.padSnCodeTime);
        // dto.setServiceEngineerId(this.serviceEngineerId);
        // dto.setServiceEngineerName(this.serviceEngineerName);
        // dto.setServiceEngineerPhone(this.serviceEngineerPhone);
        // dto.setServiceEngineerIdCard(this.serviceEngineerIdCard);
        // dto.setServiceMoney(this.serviceMoney);
        dto.setDispatchTypeText(this.dispatchTypeText);
        dto.setPayTerminalText(this.payTerminalText);
        dto.setDeleteRemark(this.deleteRemark);
        dto.setDeleteReason(this.deleteReason);
        dto.setOtherPayType(this.otherPayType);
        // dto.setAccountingTime(this.accountingTime);
        // dto.setUserStatusTime(this.userStatusTime);
        // dto.setDistributorVerifyBackOrderType(this.distributorVerifyBackOrderType);
        // dto.setDistributorVerifyBackOrderTypeText(this.distributorVerifyBackOrderTypeText);
        // dto.setDistributorVerifyBackOrderReason(this.distributorVerifyBackOrderReason);
        // dto.setDistributorVerifyBackOrderTime(this.distributorVerifyBackOrderTime);
        // dto.setHeadquartersVerifyBackOrderTime(this.headquartersVerifyBackOrderTime);
        dto.setWorkOrderIntallType(this.workOrderIntallType);
        dto.setOldCompleteOutTradeNo(this.oldCompleteOutTradeNo);
        dto.setAccountMonth(this.accountMonth);
        dto.setModifyAddress(this.modifyAddress);
        // dto.setRenewStatus(this.renewStatus);
        // dto.setRenewStatusText(this.renewStatusText);
        // dto.setRenewTimes(this.renewTimes);
        dto.setSyncBaideStatus(this.syncBaideStatus);
        // dto.setWorkorderAcceptStatusText(this.workorderAcceptStatusText);
        // dto.setRedAssemblyStatus(this.redAssemblyStatus);
        // dto.setAccountingStatus(this.accountingStatus);
        // dto.setAccountingStatusText(this.accountingStatusText);
        // dto.setWorkorderBatchCodeStatusText(this.workorderBatchCodeStatusText);
        // dto.setWorkorderSncodeStatusText(this.workorderSncodeStatusText);
        // dto.setWorkorderSimcardStatusText(this.workorderSimcardStatusText);
        // dto.setWorkorderPayStatusText(this.workorderPayStatusText);
        // dto.setWorkorderConfirmInformationStatusText(this.workorderConfirmInformationStatusText);
        // dto.setWorkorderSignContractStatusText(this.workorderSignContractStatusText);
        // dto.setWorkorderBackStatusText(this.workorderBackStatusText);
        dto.setSyncBaideFailureText(this.syncBaideFailureText);
        dto.setOldSubOrderId(this.oldSubOrderId);
        dto.setOldUserId(this.oldUserId);
        dto.setOldProductId(this.oldProductId);
        dto.setOldDeviceId(this.oldDeviceId);
        dto.setOldDistributorId(this.oldDistributorId);
        dto.setOldEngineerId(this.oldEngineerId);
        dto.setOldServiceEngineerId(this.oldServiceEngineerId);
        dto.setOldCostId(this.oldCostId);
        dto.setOldStationId(this.oldStationId);
        dto.setOldSubDistributorId(this.oldSubDistributorId);
        dto.setOldDistributorRoleId(this.oldDistributorRoleId);
        dto.setOldProductTypeId(this.oldProductTypeId);
        dto.setOldProductRangeId(this.oldProductRangeId);
        dto.setOldProductModelId(this.oldProductModelId);
    }
}
