// package com.yimao.cloud.base.enums;
//
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// public enum OtherPayTypeEnum {
//     POS(1, "pos机"),
//     TRANSFER_ACCOUNTS(2, "转账");
//
//     private int index;
//     private String name;
//
//     OtherPayTypeEnum(int index, String name) {
//         this.index = index;
//         this.name = name;
//     }
//
//     public static OtherPayTypeEnum findByIndex(int index) {
//         if (index > 0 && index < 10) {
//             OtherPayTypeEnum[] otherPayTypes = OtherPayTypeEnum.values();
//             for (OtherPayTypeEnum otherPayType : otherPayTypes) {
//                 if (index == otherPayType.index) {
//                     return otherPayType;
//                 }
//             }
//         }
//         return null;
//     }
//
//     public static List<Map<String, Object>> all() {
//         List<Map<String, Object>> list = new ArrayList<>();
//         Map<String, Object> map;
//         OtherPayTypeEnum[] otherPayTypeEnums = OtherPayTypeEnum.values();
//         for (OtherPayTypeEnum otherPayType : otherPayTypeEnums) {
//             map = new HashMap<>();
//             map.put("index", otherPayType.index);
//             map.put("name", otherPayType.name);
//             list.add(map);
//         }
//         return list;
//     }
//
//     public int getIndex() {
//         return this.index;
//     }
//
//     public void setIndex(int index) {
//         this.index = index;
//     }
//
//     public String getName() {
//         return this.name;
//     }
//
//     public void setName(String name) {
//         this.name = name;
//     }
// }
