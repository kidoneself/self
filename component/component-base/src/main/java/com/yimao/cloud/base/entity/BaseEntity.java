// package com.yimao.cloud.base.entity;
//
// import javax.persistence.Id;
// import java.io.Serializable;
// import java.util.Date;
//
// /**
//  * 数据库共通基类
//  *
//  * @author Zhang Bo
//  * @Date 2017/10/21.
//  */
// public class BaseEntity implements Serializable {
//
//     private static final long serialVersionUID = 8404553267314205044L;
//
//     @Id
//     private Integer id;
//     protected String creator;
//     protected Date createTime;
//     protected String updater;
//     protected Date updateTime;
//
//     public Long getId() {
//         return this.id;
//     }
//
//     public void setId(Long id) {
//         this.id = id;
//     }
//
//     public Date getCreateTime() {
//         return this.createTime;
//     }
//
//     public void setCreateTime(Date createTime) {
//         this.createTime = createTime;
//     }
//
//     public String getCreator() {
//         return this.creator;
//     }
//
//     public void setCreator(String creator) {
//         this.creator = creator;
//         this.updater = creator;
//     }
//
//     public Date getUpdateTime() {
//         return this.updateTime;
//     }
//
//     public void setUpdateTime(Date updateTime) {
//         this.updateTime = updateTime;
//     }
//
//     public String getUpdater() {
//         return this.updater;
//     }
//
//     public void setUpdater(String updater) {
//         this.updater = updater;
//     }
//
// }