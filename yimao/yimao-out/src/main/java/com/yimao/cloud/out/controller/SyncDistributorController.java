// package com.yimao.cloud.out.controller;
//
// import com.yimao.cloud.base.utils.CollectionUtil;
// import com.yimao.cloud.out.entity.Distributor;
// import com.yimao.cloud.pojo.dto.out.DistributorDTO;
// import io.swagger.annotations.Api;
// import org.bson.Document;
// import org.springframework.data.domain.Sort;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.query.BasicQuery;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
//
// import javax.annotation.Resource;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// /**
//  * 描述：同步经销商信息。
//  *
//  * @Author Zhang Bo
//  * @Date 2019/5/16
//  */
// @RestController
// @Api(tags = "SyncDistributorController")
// public class SyncDistributorController {
//
//     @Resource
//     private MongoTemplate mongoTemplate;
//
//     @GetMapping(value = "/sync/distributor/min/time")
//     public Date temp() {
//         Query query = new BasicQuery(new Document(), new Document(this.getFields()));
//         Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "createTime"));
//         query.with(sort);
//         query.limit(1);
//         List<Distributor> list = mongoTemplate.find(query, Distributor.class, "distributor");
//         if (CollectionUtil.isEmpty(list)) {
//             return null;
//         }
//         return list.get(0).getCreateTime();
//     }
//
//     @GetMapping(value = "/sync/distributor/total/count")
//     public long tempDeviceCount(@RequestParam(required = false) Date startTime) {
//         Query query = new BasicQuery(new Document(), new Document(this.getFields()));
//         if (startTime != null) {
//             query.addCriteria(Criteria.where("createTime").gte(startTime));
//         }
//         long total = mongoTemplate.count(query, Distributor.class, "distributor");
//         System.out.println("total=" + total);
//         return total;
//     }
//
//     @GetMapping(value = "/sync/distributor/{pageSize}")
//     public List<DistributorDTO> tempDevices(@PathVariable Integer pageSize, @RequestParam(required = false) Date startTime, @RequestParam(required = false) Date endTime) {
//         Query query = new BasicQuery(new Document(), new Document(this.getFields()));
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
//         return mongoTemplate.find(query, DistributorDTO.class, "distributor");
//     }
//
//     public Map<String, Object> getFields() {
//         Map<String, Object> fields = new HashMap<>();
//         fields.put("name", 1);
//         fields.put("password", 1);
//         fields.put("realName", 1);
//         fields.put("sex", 1);
//         fields.put("phone", 1);
//         fields.put("province", 1);
//         fields.put("city", 1);
//         fields.put("region", 1);
//         fields.put("address", 1);
//         fields.put("workId", 1);
//         fields.put("mail", 1);
//         fields.put("payMoney", 1);
//         fields.put("type", 1);
//         fields.put("special", 1);
//         fields.put("stationMaster", 1);
//         fields.put("referee", 1);
//         fields.put("isSponsors", 1);
//         fields.put("level", 1);
//         fields.put("roleLevel", 1);
//         fields.put("company", 1);
//         fields.put("pid", 1);
//         fields.put("count", 1);
//         fields.put("forbidden", 1);
//         fields.put("forbiddenOrder", 1);
//         fields.put("fuhuishun", 1);
//         fields.put("createTime", 1);
//         fields.put("updateTime", 1);
//         fields.put("disTime", 1);
//         fields.put("appType", 1);
//         fields.put("version", 1);
//         return fields;
//     }
//
// }
