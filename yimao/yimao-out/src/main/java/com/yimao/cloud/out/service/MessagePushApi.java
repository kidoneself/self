// package com.yimao.cloud.out.service;
//
// import com.yimao.cloud.base.enums.StatusEnum;
// import com.yimao.cloud.base.utils.StringUtil;
// import com.yimao.cloud.out.entity.MessagePush;
// import com.yimao.cloud.out.enums.MessageTypeEnum;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.stereotype.Service;
//
// import javax.annotation.Resource;
// import java.util.List;
// import java.util.Objects;
//
// /**
//  * 描述：TODO
//  *
//  * @Author Zhang Bo
//  * @Date 2019/3/9
//  */
// @Service
// @Slf4j
// public class MessagePushApi {
//
//     @Resource
//     private MongoTemplate mongoTemplate;
//
//     public List<MessagePush> index(String username, Integer app, int pageNo, int pageSize) {
//         Query query = new Query();
//         query.addCriteria(Criteria.where("username").is(username));
//         query.addCriteria(Criteria.where("isDelete").ne(true));
//         query.addCriteria(Criteria.where("readStatus").ne(StatusEnum.YES.value()));
//         if (app != null) {
//             query.addCriteria(Criteria.where("app").is(app));
//         }
//         query.skip((pageNo - 1) * pageSize);
//         query.limit(pageSize);
//         return mongoTemplate.find(query, MessagePush.class, "messagepush");
//     }
//
//     public List<MessagePush> list(String username, String typeName, Integer app, int pageNo, int pageSize) {
//         Query query = new Query();
//         query.addCriteria(Criteria.where("isDelete").ne(true));
//
//         this.orSystemPushMessage(query, username, typeName, app);
//
//         query.skip((pageNo - 1) * pageSize);
//         query.limit(pageSize);
//         return mongoTemplate.find(query, MessagePush.class, "messagepush");
//     }
//
//     public int unReadNum(String username, String content, String typeName, Integer app) {
//         Query query = new Query();
//         query.addCriteria(Criteria.where("isDelete").ne(true));
//         query.addCriteria(Criteria.where("readStatus").is(StatusEnum.NO.value()));
//         query.addCriteria(Criteria.where("content").regex(".*" + content + ".*"));
//
//         this.orSystemPushMessage(query, username, typeName, app);
//
//
//         return (int) mongoTemplate.count(query, MessagePush.class, "messagepush");
//     }
//
//     private void orSystemPushMessage(Query query, String username, String typeName, Integer app) {
//         if (StringUtil.isNotEmpty(typeName) && Objects.equals(typeName, MessageTypeEnum.ALL.getTypeZNText())) {
//             Criteria criteria1 = new Criteria();
//             criteria1.and("username").is(username);
//             if (app != null) {
//                 criteria1.and("app").is(app);
//             }
//             Criteria criteria2 = new Criteria();
//             criteria1.and("username").exists(false);
//             criteria1.and("title").regex(".*" + MessageTypeEnum.SYSTEM_PUSH.getTypeZNText() + ".*");
//
//             query.addCriteria(new Criteria().orOperator(criteria1, criteria2));
//         } else if (StringUtil.isNotEmpty(typeName) && !Objects.equals(typeName, MessageTypeEnum.ALL.getTypeZNText())) {
//             if (!Objects.equals(typeName, MessageTypeEnum.SYSTEM_PUSH.getTypeZNText())) {
//                 query.addCriteria(Criteria.where("username").is(username));
//                 if (app != null) {
//                     query.addCriteria(Criteria.where("app").is(app));
//                 }
//             } else {
//                 query.addCriteria(Criteria.where("username").exists(false));
//             }
//             query.addCriteria(Criteria.where("title").regex(".*" + typeName + ".*"));
//         } else {
//             query.addCriteria(Criteria.where("username").is(username));
//             if (app != null) {
//                 query.addCriteria(Criteria.where("app").is(app));
//             }
//         }
//     }
// }
