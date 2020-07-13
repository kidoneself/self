// package com.yimao.cloud.out.controller;
//
// import com.yimao.cloud.base.utils.CollectionUtil;
// import com.yimao.cloud.out.entity.Customer;
// import com.yimao.cloud.pojo.dto.out.CustomerDTO;
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
//  * 描述：同步安装工数据
//  *
//  * @Author Zhang Bo
//  * @Date 2019/5/16
//  */
// @RestController
// @Api(tags = "SyncEngineerController")
// public class SyncEngineerController {
//
//     @Resource
//     private MongoTemplate mongoTemplate;
//
//     @GetMapping(value = "/sync/engineer/min/time")
//     public Date temp() {
//         Query query = new Query();
//         Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "createTime"));
//         query.with(sort);
//         query.limit(1);
//         List<Customer> list = mongoTemplate.find(query, Customer.class, "customer");
//         if (CollectionUtil.isEmpty(list)) {
//             return null;
//         }
//         return list.get(0).getCreateTime();
//     }
//
//     @GetMapping(value = "/sync/engineer/total/count")
//     public long tempDeviceCount(@RequestParam(required = false) Date startTime) {
//         Query query = new Query();
//         if (startTime != null) {
//             query.addCriteria(Criteria.where("createTime").gte(startTime));
//         }
//         long total = mongoTemplate.count(query, Customer.class, "customer");
//         System.out.println("total=" + total);
//         return total;
//     }
//
//     @GetMapping(value = "/sync/engineer/{pageSize}")
//     public List<CustomerDTO> tempDevices(@PathVariable Integer pageSize, @RequestParam(required = false) Date startTime, @RequestParam(required = false) Date endTime) {
//         Query query = new Query();
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
//         List<Customer> customer = mongoTemplate.find(query, Customer.class, "customer");
//         return CollectionUtil.batchConvert(customer, Customer.class, CustomerDTO.class);
//     }
//
// }
