// package com.yimao.cloud.out.controller;
//
// import com.yimao.cloud.base.utils.CollectionUtil;
// import com.yimao.cloud.out.entity.User;
// import com.yimao.cloud.pojo.dto.out.WaterUserDTO;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
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
//  * 查询云平台mongo数据库设备用户相关信息。
//  *
//  * @author Zhang Bo
//  * @date 2017/12/15.
//  */
// @RestController
// @Api(tags = "SyncDeviceUserController")
// public class SyncDeviceUserController {
//
//     @Resource
//     private MongoTemplate mongoTemplate;
//
//     @GetMapping(value = "/sync/device/user/min/time")
//     public Date getMinTime() {
//         Query query = new Query();
//         Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "createTime"));
//         query.with(sort);
//         query.limit(1);
//         List<User> list = mongoTemplate.find(query, User.class, "user");
//         if (CollectionUtil.isEmpty(list)) {
//             return null;
//         }
//         return list.get(0).getCreateTime();
//     }
//
//     @GetMapping(value = "/sync/device/user/total/count")
//     public long getTotalCount(@RequestParam(required = false) Date startTime) {
//         Query query = new Query();
//         if (startTime != null) {
//             query.addCriteria(Criteria.where("createTime").gte(startTime));
//         }
//         long total = mongoTemplate.count(query, User.class, "user");
//         System.out.println("total=" + total);
//         return total;
//     }
//
//     @GetMapping(value = "/sync/device/user/{pageSize}")
//     @ApiOperation(value = "同步设备数据")
//     public List<WaterUserDTO> getPageData(@PathVariable Integer pageSize, @RequestParam(required = false) Date startTime, @RequestParam(required = false) Date endTime) {
//         // Query query = new Query();
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
//         List<User> list = mongoTemplate.find(query, User.class, "user");
//         return CollectionUtil.batchConvert(list, User.class, WaterUserDTO.class);
//     }
//
//     public Map<String, Object> getFields() {
//         Map<String, Object> fields = new HashMap<>();
//         fields.put("name", 1);
//         fields.put("phone", 1);
//         fields.put("province", 1);
//         fields.put("city", 1);
//         fields.put("region", 1);
//         fields.put("address", 1);
//         fields.put("job", 1);
//         fields.put("sex", 1);
//         fields.put("age", 1);
//         fields.put("degree", 1);
//         fields.put("createTime", 1);
//         fields.put("updateTime", 1);
//         return fields;
//     }
//
// }
