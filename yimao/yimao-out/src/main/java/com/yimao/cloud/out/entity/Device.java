// package com.yimao.cloud.out.entity;
//
// import com.yimao.cloud.pojo.dto.out.DeviceDTO;
// import lombok.Getter;
// import lombok.Setter;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.DBRef;
//
// import java.util.Date;
//
// /**
//  * 描述：mongo库中设备表
//  *
//  * @Author Zhang Bo
//  * @Date 2019/2/18
//  */
// @Getter
// @Setter
// public class Device {
//
//     @Id
//     private String id;
//     private String sncode;//SN码
//     private String simcard;//sim卡
//     private Boolean actived;//SIM卡是否激活
//     private String longitude;//经度
//     private String latitude;//纬度
//     private String province;//省份
//     private String city;//城市
//     private String region;//地区
//     private String address;//地址
//     private String cost;//计费套餐Id
//     private String costName;//计费套餐
//     private Integer lasttotaltime;//上一次累计使用时长
//     private Integer lasttotalflow;//上一次累计使用流量
//     private Integer currenttime;//当前使用总时长
//     private Integer currentflow;//当前使用总流量
//     private Double submoney;//设备初始金额
//     private Double money;//设备余额
//     private String place;//设备摆放地
//     private Date activeTime;//激活时间
//     private Date renewTime;//续费充值时间
//     private Date changecostTime;//设备变更套餐时间
//     private Date ppChangeTime;//PP滤芯更换时间
//     private Date udfChangeTime;//udf滤芯更换时间
//     private Date ctoChangeTime;//cto滤芯更换时间
//     private Date t33ChangeTime;//t33滤芯更换时间
//     private Date lastonlineTime;//最后在线时间
//     private String logisticsCoding;//物流编码
//     private Boolean online;//是否在线
//     private Boolean isChange;//是否变更套餐
//     private String changeCost;//更换后套餐
//     private Boolean bind;//sn码是否绑定水机
//     private Boolean isUnbundling;//解绑标记
//     private String version;//版本
//     private String deviceType;//产品类型
//     private String deviceScope;//产品范围
//     private String deviceModel;//产品型号
//     private String simcardAccountId;// sim卡运营商账号
//     private Date sncodeTime;//输入sncode的时间
//     private String serverModifyAddress;// 后台修改的地址
//     private String serverModifyAddressUser;//后台修改人
//     private Date serverModifyAddressTime;//后台修改的时间
//     private Integer renewStatus;//
//     private String renewStatusZHText;//
//     private Date deviceArrearageDate;//设备欠费时间,续费之后.此字段需要变更为空
//     private Integer renewTimes;//
//     private Integer internetType;//
//     private String internetTypeText;//
//     private String lockState;//
//     private String ontime;//亮屏时间
//     private String offtime;//灭屏时间
//
//     @DBRef
//     private SyncTds tds;
//     @DBRef
//     private SyncUser user;
//     @DBRef
//     private SyncCustomer customer;
//     @DBRef
//     private SyncDistributor distributor;
//
//     public Device() {
//     }
//
//     /**
//      * 将数据库实体对象信息拷贝到业务对象DeviceDTO上。
//      *
//      * @param dto 业务对象
//      */
//     public void convert(DeviceDTO dto) {
//         dto.setId(this.id);
//         dto.setSnCode(this.sncode);
//         dto.setSimCard(this.simcard);
//         dto.setActived(this.actived);
//         dto.setLongitude(this.longitude);
//         dto.setLatitude(this.latitude);
//         dto.setProvince(this.province);
//         dto.setCity(this.city);
//         dto.setRegion(this.region);
//         dto.setAddress(this.address);
//         dto.setCost(this.cost);
//         dto.setCostName(this.costName);
//         dto.setLastTotalTime(this.lasttotaltime);
//         dto.setLastTotalFlow(this.lasttotalflow);
//         dto.setCurrentTime(this.currenttime);
//         dto.setCurrentFlow(this.currentflow);
//         dto.setSubMoney(this.submoney);
//         dto.setMoney(this.money);
//         dto.setPlace(this.place);
//         dto.setActiveTime(this.activeTime);
//         dto.setRenewTime(this.renewTime);
//         dto.setChangeCostTime(this.changecostTime);
//         dto.setPpChangeTime(this.ppChangeTime);
//         dto.setUdfChangeTime(this.udfChangeTime);
//         dto.setCtoChangeTime(this.ctoChangeTime);
//         dto.setT33ChangeTime(this.t33ChangeTime);
//         dto.setLastOnlineTime(this.lastonlineTime);
//         dto.setLogisticsCoding(this.logisticsCoding);
//         dto.setOnline(this.online);
//         dto.setIsChange(this.isChange);
//         dto.setChangeCost(this.changeCost);
//         dto.setBind(this.bind);
//         dto.setIsUnbundling(this.isUnbundling);
//         dto.setVersion(this.version);
//         dto.setDeviceType(this.deviceType);
//         dto.setDeviceScope(this.deviceScope);
//         dto.setDeviceModel(this.deviceModel);
//         dto.setSimCardAccountId(this.simcardAccountId);
//         dto.setSnCodeTime(this.sncodeTime);
//         dto.setServerModifyAddress(this.serverModifyAddress);
//         dto.setServerModifyAddressUser(this.serverModifyAddressUser);
//         dto.setServerModifyAddressTime(this.serverModifyAddressTime);
//         dto.setRenewStatus(this.renewStatus);
//         dto.setRenewStatusZHText(this.renewStatusZHText);
//         dto.setDeviceArrearageDate(this.deviceArrearageDate);
//         dto.setRenewTimes(this.renewTimes);
//         dto.setInternetType(this.internetType);
//         dto.setInternetTypeText(this.internetTypeText);
//         dto.setLockState(this.lockState);
//         dto.setOntime(this.getOntime());
//         dto.setOfftime(this.getOfftime());
//
//         if (this.tds != null) {
//             dto.setOldTdsId(this.tds.getId());
//             dto.setK(this.tds.getK());
//             dto.setT(this.tds.getT());
//         }
//
//         dto.setOldUserId(this.user != null ? this.getUser().getId() : null);
//         dto.setOldUserName(this.user != null ? this.getUser().getName() : null);
//         dto.setOldUserPhone(this.user != null ? this.getUser().getPhone() : null);
//         dto.setOldCustomerId(this.customer != null ? this.getCustomer().getId() : null);
//         dto.setOldDistributorId(this.distributor != null ? this.distributor.getId() : null);
//         dto.setOldSimAccountId(this.simcardAccountId);
//     }
// }
