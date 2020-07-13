// package com.yimao.cloud.out.entity;
//
// import com.alibaba.fastjson.annotation.JSONField;
// import com.fasterxml.jackson.annotation.JsonFormat;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.DBRef;
//
// import java.io.Serializable;
// import java.util.Date;
//
// /**
//  * @author Zhang Bo
//  * @date 2018/1/31.
//  */
// public class Workorder implements Serializable {
//
//     private static final long serialVersionUID = -1413625260815982135L;
//
//     @Id
//     private String id;
//     private String workorderId;//工单号
//     private String province;//省份
//     private String city;//城市
//     private String region;//地区
//     private String address;//地址
//     private Integer count;//数量
//     @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//     private Date time;//安装时间
//     private String remark;//备注
//     private Integer state;//状态 (1 未受理,2 已受理,3 处理中,4 已完成)
//     private Boolean allot;//需要手动分配
//     private Integer allotType;//分配类型( 1 指定客服分配 2 系统分配 )
//     private Integer refuse;//拒绝工单  0代表未拒绝工单   1代表拒绝工单
//     private String reason;//拒绝理由
//     private String excuse;//审核未通过理由
//     private Integer score;//评价分数
//     @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//     private Date createTime;//创建时间
//     private Double costmoney;//计费金额
//     private Double openaccount;//开户费
//     private String cost;//计费套餐Id
//     private Integer payType;//支付类型  1:微信  2:支付宝 3:其他
//     private Boolean isPaid;//是否支付
//     private Integer paystate;//支付状态(0 未支付,1 审核中,2 未通过,3已支付)
//     @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//     private Date payTime;//支付时间
//     private Boolean isRenew;//是否续费
// //    @DBRef
// //    private Waterdevice waterdevice;//水机
//     @DBRef
//     private Customer customer;//客服人员（安装工）
//     //private String device;//对应设备
//     @DBRef
//     private Distributor distributor;//经销商/代理商
//     @DBRef
//     private User user;//客户
//     private Integer isDelete;//标记是否删除
//     private String trade_no;//交易单号
//     private String content;//评价
//     @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//     private Date completeTime;//完成时间
//     private Boolean isChargeback;//true-被退回，false-没被退回
//     private Integer chargebackType;//(1 经销商退单 2 客服退单)
//     private Date chargebackTime;//客服点击退单时间
//     private Integer completeType;//工单完成类型(1:正常 2:非正常)
//     private Integer payterminal;//支付端(1 经销商支付  2 用户支付)
//     private String deviceScope;//产品范围
//     private String deviceModel;//产品型号
//     private String deviceType;//产品类型
//     private Integer step;//步骤
//     private String mail;//邮箱
//     private Integer roleLevel;//经销商等级
//     private String roleName;//经销商等级名称
//     private String accountMonth;//结算月份
//     private Integer type;//1-经销商APP下单，2-二维码下单
//     private Integer scanCodeType;//1-直播二维码，3-个人二维码，4-软文二维码
//
//     public String getId() {
//         return id;
//     }
//
//     public void setId(String id) {
//         this.id = id;
//     }
//
//     public String getWorkorderId() {
//         return workorderId;
//     }
//
//     public void setWorkorderId(String workorderId) {
//         this.workorderId = workorderId;
//     }
//
//     public String getProvince() {
//         return province;
//     }
//
//     public void setProvince(String province) {
//         this.province = province;
//     }
//
//     public String getCity() {
//         return city;
//     }
//
//     public void setCity(String city) {
//         this.city = city;
//     }
//
//     public String getRegion() {
//         return region;
//     }
//
//     public void setRegion(String region) {
//         this.region = region;
//     }
//
//     public String getAddress() {
//         return address;
//     }
//
//     public void setAddress(String address) {
//         this.address = address;
//     }
//
//     public Integer getCount() {
//         return count;
//     }
//
//     public void setCount(Integer count) {
//         this.count = count;
//     }
//
//     public Date getTime() {
//         return time;
//     }
//
//     public void setTime(Date time) {
//         this.time = time;
//     }
//
//     public String getRemark() {
//         return remark;
//     }
//
//     public void setRemark(String remark) {
//         this.remark = remark;
//     }
//
//     public Integer getState() {
//         return state;
//     }
//
//     public void setState(Integer state) {
//         this.state = state;
//     }
//
//     public Boolean getAllot() {
//         return allot;
//     }
//
//     public void setAllot(Boolean allot) {
//         this.allot = allot;
//     }
//
//     public Integer getRefuse() {
//         return refuse;
//     }
//
//     public void setRefuse(Integer refuse) {
//         this.refuse = refuse;
//     }
//
//     public String getReason() {
//         return reason;
//     }
//
//     public void setReason(String reason) {
//         this.reason = reason;
//     }
//
//     public String getExcuse() {
//         return excuse;
//     }
//
//     public void setExcuse(String excuse) {
//         this.excuse = excuse;
//     }
//
//     public Integer getScore() {
//         return score;
//     }
//
//     public void setScore(Integer score) {
//         this.score = score;
//     }
//
//     public Date getCreateTime() {
//         return createTime;
//     }
//
//     public void setCreateTime(Date createTime) {
//         this.createTime = createTime;
//     }
//
//     public Double getCostmoney() {
//         return costmoney;
//     }
//
//     public void setCostmoney(Double costmoney) {
//         this.costmoney = costmoney;
//     }
//
//     public Double getOpenaccount() {
//         return openaccount;
//     }
//
//     public void setOpenaccount(Double openaccount) {
//         this.openaccount = openaccount;
//     }
//
//     public String getCost() {
//         return cost;
//     }
//
//     public void setCost(String cost) {
//         this.cost = cost;
//     }
//
//     public Integer getPayType() {
//         return payType;
//     }
//
//     public void setPayType(Integer payType) {
//         this.payType = payType;
//     }
//
//     public Boolean getPaid() {
//         return isPaid;
//     }
//
//     public void setPaid(Boolean paid) {
//         isPaid = paid;
//     }
//
//     public Boolean getRenew() {
//         return isRenew;
//     }
//
//     public void setRenew(Boolean renew) {
//         isRenew = renew;
//     }
//
//     public Distributor getDistributor() {
//         return distributor;
//     }
//
//     public void setDistributor(Distributor distributor) {
//         this.distributor = distributor;
//     }
//
//     public User getUser() {
//         return user;
//     }
//
//     public void setUser(User user) {
//         this.user = user;
//     }
//
//     public Integer getIsDelete() {
//         return isDelete;
//     }
//
//     public void setIsDelete(Integer isDelete) {
//         this.isDelete = isDelete;
//     }
//
//     public Date getPayTime() {
//         return payTime;
//     }
//
//     public void setPayTime(Date payTime) {
//         this.payTime = payTime;
//     }
//
//     public String getTrade_no() {
//         return trade_no;
//     }
//
//     public void setTrade_no(String trade_no) {
//         this.trade_no = trade_no;
//     }
//
//     public String getContent() {
//         return content;
//     }
//
//     public void setContent(String content) {
//         this.content = content;
//     }
//
//     public Date getCompleteTime() {
//         return completeTime;
//     }
//
//     public void setCompleteTime(Date completeTime) {
//         this.completeTime = completeTime;
//     }
//
//     public Boolean getChargeback() {
//         return isChargeback;
//     }
//
//     public void setChargeback(Boolean chargeback) {
//         isChargeback = chargeback;
//     }
//
//     public Integer getPaystate() {
//         return paystate;
//     }
//
//     public void setPaystate(Integer paystate) {
//         this.paystate = paystate;
//     }
//
//     public Integer getCompleteType() {
//         return completeType;
//     }
//
//     public void setCompleteType(Integer completeType) {
//         this.completeType = completeType;
//     }
//
//     public Integer getPayterminal() {
//         return payterminal;
//     }
//
//     public void setPayterminal(Integer payterminal) {
//         this.payterminal = payterminal;
//     }
//
//     public String getDeviceScope() {
//         return deviceScope;
//     }
//
//     public void setDeviceScope(String deviceScope) {
//         this.deviceScope = deviceScope;
//     }
//
//     public String getDeviceModel() {
//         return deviceModel;
//     }
//
//     public void setDeviceModel(String deviceModel) {
//         this.deviceModel = deviceModel;
//     }
//
//     public String getDeviceType() {
//         return deviceType;
//     }
//
//     public void setDeviceType(String deviceType) {
//         this.deviceType = deviceType;
//     }
//
//     public Integer getStep() {
//         return step;
//     }
//
//     public void setStep(Integer step) {
//         this.step = step;
//     }
//
//     public String getMail() {
//         return mail;
//     }
//
//     public void setMail(String mail) {
//         this.mail = mail;
//     }
//
//     public Integer getRoleLevel() {
//         return roleLevel;
//     }
//
//     public void setRoleLevel(Integer roleLevel) {
//         this.roleLevel = roleLevel;
//     }
//
//     public String getRoleName() {
//         return roleName;
//     }
//
//     public void setRoleName(String roleName) {
//         this.roleName = roleName;
//     }
//
//     public String getAccountMonth() {
//         return accountMonth;
//     }
//
//     public void setAccountMonth(String accountMonth) {
//         this.accountMonth = accountMonth;
//     }
//
//     public Integer getType() {
//         return type;
//     }
//
//     public void setType(Integer type) {
//         this.type = type;
//     }
//
//     public Integer getScanCodeType() {
//         return scanCodeType;
//     }
//
//     public void setScanCodeType(Integer scanCodeType) {
//         this.scanCodeType = scanCodeType;
//     }
//
//     public Customer getCustomer() {
//         return customer;
//     }
//
//     public void setCustomer(Customer customer) {
//         this.customer = customer;
//     }
//
// //    public Waterdevice getWaterdevice() {
// //        return waterdevice;
// //    }
// //
// //    public void setWaterdevice(Waterdevice waterdevice) {
// //        this.waterdevice = waterdevice;
// //    }
//
//     public Integer getAllotType() {
//         return allotType;
//     }
//
//     public void setAllotType(Integer allotType) {
//         this.allotType = allotType;
//     }
//
//     public Integer getChargebackType() {
//         return chargebackType;
//     }
//
//     public void setChargebackType(Integer chargebackType) {
//         this.chargebackType = chargebackType;
//     }
//
//     public Date getChargebackTime() {
//         return chargebackTime;
//     }
//
//     public void setChargebackTime(Date chargebackTime) {
//         this.chargebackTime = chargebackTime;
//     }
// }
