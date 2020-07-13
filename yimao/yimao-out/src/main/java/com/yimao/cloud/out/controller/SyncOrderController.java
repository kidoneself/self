// package com.yimao.cloud.out.controller;
//
// import com.yimao.cloud.base.utils.CollectionUtil;
// import com.yimao.cloud.out.entity.Order;
// import com.yimao.cloud.pojo.dto.out.MongoOrderDTO;
// import io.swagger.annotations.Api;
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
//  * 描述：同步经销商信息。
//  *
//  * @Author Zhang Bo
//  * @Date 2019/5/16
//  */
// @RestController
// @Api(tags = "SyncOrderController")
// public class SyncOrderController {
//
//     @Resource
//     private MongoTemplate mongoTemplate;
//
//     @GetMapping(value = "/sync/order/min/time")
//     public Date temp() {
//         Query query = new Query();
//         Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "createTime"));
//         query.with(sort);
//         query.limit(1);
//         Order one = mongoTemplate.findOne(query, Order.class);
//         if (one == null) {
//             return null;
//         }
//         return one.getCreateTime();
//     }
//
//     @GetMapping(value = "/sync/order/total/count")
//     public long tempDeviceCount(@RequestParam(required = false) Date startTime) {
//         Query query = new Query();
//         if (startTime != null) {
//             query.addCriteria(Criteria.where("createTime").gte(startTime));
//         }
//         long total = mongoTemplate.count(query, Order.class);
//         System.out.println("total=" + total);
//         return total;
//     }
//
//     @GetMapping(value = "/sync/order/{pageSize}")
//     public List<MongoOrderDTO> tempDevices(@PathVariable Integer pageSize, @RequestParam(required = false) Date startTime, @RequestParam(required = false) Date endTime) {
//         Query query = new Query();
//         //设备编码
//         if (startTime != null) {
//             if (endTime != null) {
//                 query.addCriteria(Criteria.where("createTime").gte(startTime).lte(endTime));
//             } else {
//                 query.addCriteria(Criteria.where("createTime").gte(startTime));
//             }
//         } else {
//             if (endTime != null) {
//                 query.addCriteria(Criteria.where("createTime").lte(endTime));
//             }
//         }
//         Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "createTime"));
//         query.with(sort);
//         query.limit(pageSize);
//         List<Order> list = mongoTemplate.find(query, Order.class);
//         return CollectionUtil.batchConvert(list, Order.class, MongoOrderDTO.class);
//     }
//
// }
