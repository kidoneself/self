// package com.yimao.cloud.out.configuration;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.mongodb.MongoDbFactory;
// import org.springframework.data.mongodb.core.convert.DbRefResolver;
// import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
// import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
// import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
// import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
// import org.springframework.web.client.RestTemplate;
//
// /**
//  * OutMongoConfig
//  * basePackages设置Repository的扫描目录
//  * mongoTemplateRef:mongo模板类引用,类似于JDBCTemplate
//  * repositoryBaseClass:设置默认的Repository实现类为BaseRepositoryImpl,代替SimpleMongoRepository
//  *
//  * @author Zhang Bo
//  * @date 2017/10/23.
//  */
// @Configuration
// public class OutMongoConfiguration {
//
//     /**
//      * 使用MongoTemplate插入数据时不插入_class
//      *
//      * @param factory
//      * @param context
//      * @return
//      */
//     @Bean
//     public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context) {
//         DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
//         MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
//         // Don't save _class to mongo
//         // 使用MongoTemplate插入数据时不插入_class
//         mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
//         return mappingConverter;
//     }
//
//     @Bean
//     public RestTemplate restTemplate() {
//         return new RestTemplate();
//     }
// }
