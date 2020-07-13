// package com.yimao.cloud.out.entity;
//
// import com.yimao.cloud.pojo.dto.out.ServicesiteDTO;
// import lombok.Data;
// import org.springframework.data.annotation.Id;
//
// import java.io.Serializable;
// import java.util.Date;
//
// /**
//  * @author zhilin.he
//  * @description 服务站
//  * @date 2019/1/22 11:47
//  **/
// @Data
// public class Servicesite implements Serializable {
//
//     private static final long serialVersionUID = 6749473063502837750L;
//
//     @Id
//     private String id;
//     private String province;//省
//     private String city;//市
//     private String region;//区
//     private String name;
//     private Date createTime;
//     private String person;//联系人
//     private String phone;//联系电话
//     private String address;//服务站详细地址
//     private Date updateTime;
//     private String mail;
//     private String legalPerson;//法定代表人姓名
//     private String companyName;//区县级公司名称
//     private String creditCode;//统一社会信用代码
//     private String masterName;//站长姓名
//     private String masterPhone;//站长电话
//     private String identityNumber;//站长身份证号
//     private String yunSignId;//云签编号
//     private Date synchronousTime;//同步时间
//     private Boolean synchronousState;//同步状态
//     private Boolean signUp;//是否云签
//     private Date yunSignTime;//云签时间
//     private String[] costRoles;//水机计费方式权限
//
//
//     public Servicesite() {
//     }
//
//     /**
//      * 用业务对象ServicesiteDTO初始化数据库对象Servicesite。
//      * plugin author ylfjm.
//      *
//      * @param dto 业务对象
//      */
//     public Servicesite(ServicesiteDTO dto) {
//         this.id = dto.getId();
//         this.province = dto.getProvince();
//         this.city = dto.getCity();
//         this.region = dto.getRegion();
//         this.name = dto.getName();
//         this.createTime = dto.getCreateTime();
//         this.person = dto.getPerson();
//         this.phone = dto.getPhone();
//         this.address = dto.getAddress();
//         this.updateTime = dto.getUpdateTime();
//         this.mail = dto.getMail();
//         this.legalPerson = dto.getLegalPerson();
//         this.companyName = dto.getCompanyName();
//         this.creditCode = dto.getCreditCode();
//         this.masterName = dto.getMasterName();
//         this.masterPhone = dto.getMasterPhone();
//         this.identityNumber = dto.getIdentityNumber();
//         this.yunSignId = dto.getYunSignId();
//         this.synchronousTime = dto.getSynchronousTime();
//         this.synchronousState = dto.getSynchronousState();
//         this.signUp = dto.getSignUp();
//         this.yunSignTime = dto.getYunSignTime();
//         this.costRoles = dto.getCostRoles();
//     }
//
//     /**
//      * 将数据库实体对象信息拷贝到业务对象ServicesiteDTO上。
//      * plugin author ylfjm.
//      *
//      * @param dto 业务对象
//      */
//     public void convert(ServicesiteDTO dto) {
//         dto.setId(this.id);
//         dto.setProvince(this.province);
//         dto.setCity(this.city);
//         dto.setRegion(this.region);
//         dto.setName(this.name);
//         dto.setCreateTime(this.createTime);
//         dto.setPerson(this.person);
//         dto.setPhone(this.phone);
//         dto.setAddress(this.address);
//         dto.setUpdateTime(this.updateTime);
//         dto.setMail(this.mail);
//         dto.setLegalPerson(this.legalPerson);
//         dto.setCompanyName(this.companyName);
//         dto.setCreditCode(this.creditCode);
//         dto.setMasterName(this.masterName);
//         dto.setMasterPhone(this.masterPhone);
//         dto.setIdentityNumber(this.identityNumber);
//         dto.setYunSignId(this.yunSignId);
//         dto.setSynchronousTime(this.synchronousTime);
//         dto.setSynchronousState(this.synchronousState);
//         dto.setSignUp(this.signUp);
//         dto.setYunSignTime(this.yunSignTime);
//         dto.setCostRoles(this.costRoles);
//     }
// }