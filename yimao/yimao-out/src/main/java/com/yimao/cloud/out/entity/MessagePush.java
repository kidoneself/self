// package com.yimao.cloud.out.entity;
//
// import lombok.Getter;
// import lombok.Setter;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;
//
// import java.util.Date;
//
// @Document(collection = "messagepush")
// @Getter
// @Setter
// public class MessagePush {
//     public static final int CLICK_APP = 1;
//     public static final int CLICK_URL = 2;
//     public static final int CLICK_PAGE = 3;
//     public static final int ROLE_ALL = 0;
//     public static final int ROLE_CUSTOMER = 1;
//     public static final int ROLE_DISTRIBUTOR = 2;
//     public static final int DEVICE_ALL = 0;
//     public static final int DEVICE_ANDROID = 1;
//     public static final int DEVICE_IOS = 2;
//     public static final int DEVICE_FRIDGE = 3;
//     public static final int DEVICE_CONDITION = 4;
//     public static final int PUSHTYPE_MONEY = 1;
//     public static final int PUSHTYPE_WATER = 2;
//     public static final int PUSHTYPE_TDS = 3;
//     public static final int PUSHTYPE_FILTER_PP = 4;
//     public static final int PUSHTYPE_FILTER_CTO = 5;
//     public static final int PUSHTYPE_FILTER_UDF = 6;
//     public static final int PUSHTYPE_FILTER_T33 = 7;
//     public static final String WORKORDER_TYPE_INSTALL = "install";
//
//     @Id
//     private String id;
//     private String sncode;
//     private String deviceId;
//     private String workorderId;
//     private String workorderType = "install";
//     private int pushType;
//     private String title;
//     private String content;
//     private int clicknotice;
//     private String address;
//     private int devices;
//     private Date createTime;
//     private String username;
//     private String admin;
//     private boolean isDelete;
//     private String readStatus;
//     private int app;
//
//     public MessagePush() {
//         this.readStatus = "N";
//     }
//
// }
