// package com.yimao.cloud.out.entity;
//
// import com.yimao.cloud.pojo.dto.out.DeviceDTO;
// import com.yimao.cloud.pojo.dto.out.ServiceDeviceDTO;
// import lombok.Data;
// import org.springframework.data.annotation.Id;
//
// import java.io.Serializable;
// import java.util.Date;
//
// /**
//  * 描述：TODO
//  *
//  * @Author Zhang Bo
//  * @Date 2019/2/18 11:04
//  * @Version 1.0
//  */
// @Data
// public class ServiceDevice  {
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
//     private String place;//设备摆放地
//     private Date activeTime;//激活时间
//     private String online;//是否在线
//     private String deviceType;//产品类型
//     private String deviceScope;//产品范围
//     private String deviceModel;//产品型号
//     private Integer internetType;//
//     private Date lastonlineTime;
//     private String ontime;//亮屏时间
//     private String offtime;//灭屏时间
//
//
//     public ServiceDevice() {
//     }
//
//     /**
//      * 用业务对象ServiceDeviceDTO初始化数据库对象ServiceDevice。
//      * plugin author ylfjm.
//      *
//      * @param dto 业务对象
//      */
//     public ServiceDevice(ServiceDeviceDTO dto) {
//         this.id = dto.getId();
//         this.sncode = dto.getSncode();
//         this.simcard = dto.getSimcard();
//         this.actived = dto.getActived();
//         this.longitude = dto.getLongitude();
//         this.latitude = dto.getLatitude();
//         this.province = dto.getProvince();
//         this.city = dto.getCity();
//         this.region = dto.getRegion();
//         this.place = dto.getPlace();
//         this.activeTime = dto.getActiveTime();
//         this.online = dto.getOnline();
//         this.deviceType = dto.getDeviceType();
//         this.deviceScope = dto.getDeviceScope();
//         this.deviceModel = dto.getDeviceModel();
//         this.internetType = dto.getInternetType();
//         this.lastonlineTime = dto.getLastonlineTime();
//         this.ontime = dto.getOntime();
//         this.offtime = dto.getOfftime();
//     }
//
//     /**
//      * 将数据库实体对象信息拷贝到业务对象ServiceDeviceDTO上。
//      * plugin author ylfjm.
//      *
//      * @param dto 业务对象
//      */
//     public void convert(ServiceDeviceDTO dto) {
//         dto.setId(this.id);
//         dto.setSncode(this.sncode);
//         dto.setSimcard(this.simcard);
//         dto.setActived(this.actived);
//         dto.setLongitude(this.longitude);
//         dto.setLatitude(this.latitude);
//         dto.setProvince(this.province);
//         dto.setCity(this.city);
//         dto.setRegion(this.region);
//         dto.setPlace(this.place);
//         dto.setActiveTime(this.activeTime);
//         dto.setOnline(this.online);
//         dto.setDeviceType(this.deviceType);
//         dto.setDeviceScope(this.deviceScope);
//         dto.setDeviceModel(this.deviceModel);
//         dto.setInternetType(this.internetType);
//         dto.setLastonlineTime(this.lastonlineTime);
//         dto.setOntime(this.ontime);
//         dto.setOfftime(this.offtime);
//     }
// }
