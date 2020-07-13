// package com.yimao.cloud.base.constant;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Configuration;
//
// import javax.annotation.PostConstruct;
//
// /**
//  * @author Zhang Bo
//  * @date 2018/11/29.
//  */
// @Configuration
// public class DomainConstant {
//
//     public static String API;
//     public static String ADMIN;
//     public static String AD;
//     public static String WECHAT;
//     public static String HEALTH;
//
//     @Value("${domain.api}")
//     public void setAPI(String API) {
//         DomainConstant.API = API;
//     }
//
//     @Value("${domain.admin}")
//     public void setADMIN(String ADMIN) {
//         DomainConstant.ADMIN = ADMIN;
//     }
//
//     @Value("${domain.ad}")
//     public void setAD(String AD) {
//         DomainConstant.AD = AD;
//     }
//
//     @Value("${domain.wechat}")
//     public void setWECHAT(String WECHAT) {
//         DomainConstant.WECHAT = WECHAT;
//     }
//
//     @Value("${domain.health}")
//     public void setHEALTH(String HEALTH) {
//         DomainConstant.HEALTH = HEALTH;
//     }
//
//     @PostConstruct
//     public void init() {
//
//     }
//
// }
