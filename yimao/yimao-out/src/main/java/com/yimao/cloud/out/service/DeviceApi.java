// package com.yimao.cloud.out.service;
//
// import com.yimao.cloud.out.entity.ServiceDevice;
// import com.yimao.cloud.pojo.dto.out.ServiceDeviceDTO;
// import lombok.extern.slf4j.Slf4j;
// import org.bson.Document;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.query.BasicQuery;
// import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.stereotype.Service;
//
// import javax.annotation.Resource;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// /**
//  * 描述：查询云平台mongo数据库设备相关信息。
//  *
//  * @Author Zhang Bo
//  * @Date 2019/2/18 10:28
//  * @Version 1.0
//  */
// @Service
// @Slf4j
// public class DeviceApi {
//
//     @Resource
//     private MongoTemplate mongoTemplate;
//
//     public Map<String, Object> getFieldsList() {
//         Map<String, Object> fields = new HashMap<>();
//         fields.put("sncode", 1);
//         fields.put("simcard", 1);
//         fields.put("province", 1);
//         fields.put("city", 1);
//         fields.put("region", 1);
//         fields.put("place", 1);
//         fields.put("lastonlineTime", 1);
//         fields.put("deviceType", 1);
//         fields.put("deviceScope", 1);
//         fields.put("deviceModel", 1);
//         fields.put("internetType", 1);
//         fields.put("longitude", 1);
//         fields.put("latitude", 1);
//         fields.put("online", 1);
//         fields.put("activeTime", 1);
//         return fields;
//     }
//
//     /**
//      *查询所有的服务站设备
//      */
//     public List<ServiceDeviceDTO> serviceDevicesList(Map<String, Object> fields) {
//         Document fieldDocument = null;
//         if (fields != null && fields.size() > 0) {
//             fieldDocument = new Document(fields);
//         }
//         Query query= new BasicQuery(new Document(), fieldDocument);
//         List<ServiceDevice> data = mongoTemplate.find(query, ServiceDevice.class, "device");
//         List<ServiceDeviceDTO> list =new ArrayList<>();
//         for (ServiceDevice s : data) {
//             ServiceDeviceDTO deviceDTO=new ServiceDeviceDTO();
//             s.convert(deviceDTO);
//             list.add(deviceDTO);
//         }
//
//         return list;
//     }
//
// }
