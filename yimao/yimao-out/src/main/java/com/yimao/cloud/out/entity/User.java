// package com.yimao.cloud.out.entity;
//
// import com.alibaba.fastjson.annotation.JSONField;
// import com.fasterxml.jackson.annotation.JsonFormat;
// import com.yimao.cloud.pojo.dto.out.WaterUserDTO;
// import lombok.Getter;
// import lombok.Setter;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.DBRef;
//
// import java.io.Serializable;
// import java.util.Date;
//
// /**
//  * @author Zhang Bo
//  * @date 2017/12/17.
//  */
// @Getter
// @Setter
// public class User implements Serializable {
//
//     private static final long serialVersionUID = -7829342579594146366L;
//
//     @Id
//     private String id;//
//     private String name;//姓名
//     private String sex;//性别
//     private String phone;//电话
//     private String province;//省
//     private String city;//市
//     private String region;//区
//     private String address;//地址
//     private String job;//部门职位
//     private Integer age;//年龄
//     private String degree;//文化程度
//     @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//     private Date createTime;//创建时间
//     @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//     private Date updateTime;//更新时间
//     private Integer count;//家庭人口
//     private Integer order;//
//     @DBRef
//     private Distributor distributor;//经销商
//     private String buildingTag;//建筑标签
//     private String company;//公司
//     private String hobby;//爱好
//     private Integer childAge;//子女年龄
//     private Boolean haveChild;//是否有子女
//     private Boolean haveOld;//是否有老人
//     private String childSex;//子女性别
//     private Boolean marry;//子女是否结婚
//     private Boolean studyAbroad;//是否考虑留学
//     private String image;//头像
//     private String industry;//公司行业
//     private String legal;//公司法人
//     private String dimensions;//
//     private Integer isUserSelfHelpAdd;//是否是用户自主下单:1-否，2-是
//     private String childDistributorId;//企业版子账户ID
//
//
//     public User() {
//     }
//
//     /**
//      * 用业务对象UserDTO初始化数据库对象User。
//      * plugin author ylfjm.
//      *
//      * @param dto 业务对象
//      */
//     public User(WaterUserDTO dto) {
//         this.id = dto.getId();
//         this.name = dto.getName();
//         this.sex = dto.getSex();
//         this.phone = dto.getPhone();
//         this.province = dto.getProvince();
//         this.city = dto.getCity();
//         this.region = dto.getRegion();
//         this.address = dto.getAddress();
//         this.job = dto.getJob();
//         this.age = dto.getAge();
//         this.degree = dto.getDegree();
//         this.createTime = dto.getCreateTime();
//         this.updateTime = dto.getUpdateTime();
//         this.count = dto.getCount();
//         this.order = dto.getOrder();
//         this.buildingTag = dto.getBuildingTag();
//         this.company = dto.getCompany();
//         this.hobby = dto.getHobby();
//         this.childAge = dto.getChildAge();
//         this.haveChild = dto.getHaveChild();
//         this.haveOld = dto.getHaveOld();
//         this.childSex = dto.getChildSex();
//         this.marry = dto.getMarry();
//         this.studyAbroad = dto.getStudyAbroad();
//         this.image = dto.getImage();
//         this.industry = dto.getIndustry();
//         this.legal = dto.getLegal();
//         this.dimensions = dto.getDimensions();
//         this.isUserSelfHelpAdd = dto.getIsUserSelfHelpAdd();
//         this.childDistributorId = dto.getChildDistributorId();
//     }
//
//     /**
//      * 将数据库实体对象信息拷贝到业务对象UserDTO上。
//      * plugin author ylfjm.
//      *
//      * @param dto 业务对象
//      */
//     public void convert(WaterUserDTO dto) {
//         dto.setId(this.id);
//         dto.setName(this.name);
//         dto.setSex(this.sex);
//         dto.setPhone(this.phone);
//         dto.setProvince(this.province);
//         dto.setCity(this.city);
//         dto.setRegion(this.region);
//         dto.setAddress(this.address);
//         dto.setJob(this.job);
//         dto.setAge(this.age);
//         dto.setDegree(this.degree);
//         dto.setCreateTime(this.createTime);
//         dto.setUpdateTime(this.updateTime);
//         dto.setCount(this.count);
//         dto.setOrder(this.order);
//         dto.setDistributorId(this.distributor != null ? this.distributor.getId() : null);
//         dto.setBuildingTag(this.buildingTag);
//         dto.setCompany(this.company);
//         dto.setHobby(this.hobby);
//         dto.setChildAge(this.childAge);
//         dto.setHaveChild(this.haveChild);
//         dto.setHaveOld(this.haveOld);
//         dto.setChildSex(this.childSex);
//         dto.setMarry(this.marry);
//         dto.setStudyAbroad(this.studyAbroad);
//         dto.setImage(this.image);
//         dto.setIndustry(this.industry);
//         dto.setLegal(this.legal);
//         dto.setDimensions(this.dimensions);
//         dto.setIsUserSelfHelpAdd(this.isUserSelfHelpAdd);
//         dto.setChildDistributorId(this.childDistributorId);
//     }
// }
