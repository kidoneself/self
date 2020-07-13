// package com.yimao.cloud.out.controller;
//
// import com.yimao.cloud.base.utils.CollectionUtil;
// import com.yimao.cloud.out.entity.Device;
// import com.yimao.cloud.pojo.dto.out.DeviceDTO;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
// import org.springframework.data.domain.Sort;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
//
// import javax.annotation.Resource;
// import java.util.Date;
// import java.util.List;
//
// /**
//  * 查询云平台mongo数据库设备相关信息。
//  *
//  * @author Zhang Bo
//  * @date 2017/12/15.
//  */
// @RestController
// @Api(tags = "SyncDeviceController")
// public class SyncDeviceController {
//
//     @Resource
//     private MongoTemplate mongoTemplate;
//
//     @GetMapping(value = "/sync/device/min/time")
//     public Date getMinTime() {
//         Query query = new Query();
//         Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "activeTime"));
//         query.with(sort);
//         query.limit(1);
//         List<Device> list = mongoTemplate.find(query, Device.class, "device");
//         if (CollectionUtil.isEmpty(list)) {
//             return null;
//         }
//         return list.get(0).getActiveTime();
//     }
//
//     @GetMapping(value = "/sync/device/total/count")
//     public long getTotalCount(@RequestParam(required = false) Date startTime) {
//         Query query = new Query();
//         if (startTime != null) {
//             query.addCriteria(Criteria.where("activeTime").gte(startTime));
//         }
//         long total = mongoTemplate.count(query, Device.class, "device");
//         System.out.println("total=" + total);
//         return total;
//     }
//
//     @GetMapping(value = "/sync/device/total/count2")
//     public long getTotalCount2(@RequestParam(required = false) Date startTime, @RequestParam(required = false) Date endTime) {
//         Query query = new Query();
//         if (startTime != null) {
//             if (endTime != null) {
//                 query.addCriteria(Criteria.where("activeTime").gte(startTime).lte(endTime));
//             } else {
//                 query.addCriteria(Criteria.where("activeTime").gte(startTime));
//             }
//         }
//         long total = mongoTemplate.count(query, Device.class, "device");
//         System.out.println("total2=" + total);
//         return total;
//     }
//
//     @GetMapping(value = "/sync/device/{pageSize}")
//     @ApiOperation(value = "同步设备数据")
//     public List<DeviceDTO> getPageData(@PathVariable Integer pageSize, @RequestParam(required = false) Date startTime, @RequestParam(required = false) Date endTime) {
//         Query query = new Query();
//         //设备编码
//         if (startTime != null) {
//             if (endTime != null) {
//                 query.addCriteria(Criteria.where("activeTime").gte(startTime).lte(endTime));
//             } else {
//                 query.addCriteria(Criteria.where("activeTime").gte(startTime));
//             }
//         } else {
//             if (endTime != null) {
//                 query.addCriteria(Criteria.where("activeTime").lte(endTime));
//             }
//         }
//         Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "activeTime"));
//         query.with(sort);
//         query.limit(pageSize);
//         List<Device> devices = mongoTemplate.find(query, Device.class, "device");
//         return CollectionUtil.batchConvert(devices, Device.class, DeviceDTO.class);
//     }
//
// }
