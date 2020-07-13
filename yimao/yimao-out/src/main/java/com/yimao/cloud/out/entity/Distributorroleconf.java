// package com.yimao.cloud.out.entity;
//
// import com.alibaba.fastjson.annotation.JSONField;
// import org.springframework.data.annotation.Id;
//
// import java.io.Serializable;
// import java.util.Date;
//
// /**
//  * @author Zhang Bo
//  * @date 2017/12/18.
//  */
// public class Distributorroleconf implements Serializable {
//
//     private static final long serialVersionUID = 4487867677669209124L;
//
//     @Id
//     private String id;
//     private String name;
//     private String introduce;
//     private String alertMessage;
//     private Boolean hasValid;
//     private Integer validDay;
//     private Double price;
//     private Boolean hasShow;
//     private Integer level;
//     @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//     private Date createTime;
//
//     public String getId() {
//         return id;
//     }
//
//     public void setId(String id) {
//         this.id = id;
//     }
//
//     public String getName() {
//         return name;
//     }
//
//     public void setName(String name) {
//         this.name = name;
//     }
//
//     public String getIntroduce() {
//         return introduce;
//     }
//
//     public void setIntroduce(String introduce) {
//         this.introduce = introduce;
//     }
//
//     public String getAlertMessage() {
//         return alertMessage;
//     }
//
//     public void setAlertMessage(String alertMessage) {
//         this.alertMessage = alertMessage;
//     }
//
//     public Boolean getHasValid() {
//         return hasValid;
//     }
//
//     public void setHasValid(Boolean hasValid) {
//         this.hasValid = hasValid;
//     }
//
//     public Integer getValidDay() {
//         return validDay;
//     }
//
//     public void setValidDay(Integer validDay) {
//         this.validDay = validDay;
//     }
//
//     public Double getPrice() {
//         return price;
//     }
//
//     public void setPrice(Double price) {
//         this.price = price;
//     }
//
//     public Boolean getHasShow() {
//         return hasShow;
//     }
//
//     public void setHasShow(Boolean hasShow) {
//         this.hasShow = hasShow;
//     }
//
//     public Integer getLevel() {
//         return level;
//     }
//
//     public void setLevel(Integer level) {
//         this.level = level;
//     }
//
//     public Date getCreateTime() {
//         return createTime;
//     }
//
//     public void setCreateTime(Date createTime) {
//         this.createTime = createTime;
//     }
// }
