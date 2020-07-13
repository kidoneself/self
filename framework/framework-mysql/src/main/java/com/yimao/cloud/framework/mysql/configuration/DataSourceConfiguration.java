// package com.yimao.cloud.framework.mysql.configuration;
//
// import com.alibaba.druid.pool.DruidDataSource;
// import com.yimao.cloud.framework.mysql.properties.DBProperties;
// import com.yimao.cloud.framework.mysql.properties.MasterDBProperties;
// import com.yimao.cloud.framework.mysql.properties.Slave1Properties;
// import com.yimao.cloud.framework.mysql.properties.Slave2Properties;
// import org.apache.ibatis.session.SqlSessionFactory;
// import org.mybatis.spring.SqlSessionFactoryBean;
// import org.springframework.boot.autoconfigure.AutoConfigureBefore;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
// import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
// import org.springframework.boot.context.properties.EnableConfigurationProperties;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Primary;
// import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
// import org.springframework.jdbc.datasource.DataSourceTransactionManager;
// import tk.mybatis.spring.annotation.MapperScan;
//
// import javax.annotation.Resource;
// import javax.sql.DataSource;
// import java.sql.SQLException;
// import java.util.HashMap;
// import java.util.Map;
//
// /**
//  * 多数据源、读写分离配置
//  *
//  * @author Zhang Bo
//  * @date 2018/9/27.
//  */
// @Configuration
// @EnableConfigurationProperties({MasterDBProperties.class, Slave1Properties.class, Slave2Properties.class})
// @ConditionalOnClass(DruidDataSource.class)
// @AutoConfigureBefore(DataSourceAutoConfiguration.class)
// @MapperScan(basePackages = "com.yimao.cloud.**.mapper", sqlSessionFactoryRef = "masterSqlSessionFactory")
// public class DataSourceConfiguration {
//
//     private static final String TYPE_ALIASES_PACKAGE = "com.yimao.cloud.**.po";
//     private static final String MAPPER_LOCATIONS = "classpath:mapper/*.xml";
//
//     @Resource
//     private MasterDBProperties masterDBProperties;
//     @Resource
//     private Slave1Properties slave1Properties;
//     @Resource
//     private Slave2Properties slave2Properties;
//
//     @Bean
//     @Primary
//     public DataSourceTransactionManager masterTransactionManager() {
//         return new DataSourceTransactionManager(dataSource());
//     }
//
//     @Bean
//     @Primary
//     public SqlSessionFactory masterSqlSessionFactory(DataSource dataSource) throws Exception {
//         final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
//         sessionFactory.setDataSource(dataSource);
//         sessionFactory.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
//         sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATIONS));
//         return sessionFactory.getObject();
//     }
//
//     @Bean
//     @Primary
//     public DataSource dataSource() {
//         return buildDataSource();
//     }
//
//     private DataSource buildDataSource() {
//         Map<Object, Object> targetDataSources = new HashMap<>();
//         DataSource master = masterDataSource(masterDBProperties);
//         targetDataSources.put(ReadWriteSplitRoutingDataSource.MASTER, master);
//         if (slave1Properties.getUrl() != null) {
//             DataSource slave1 = slaveDataSource(slave1Properties);
//             targetDataSources.put(ReadWriteSplitRoutingDataSource.SLAVE1, slave1);
//         }
//         if (slave2Properties.getUrl() != null) {
//             DataSource slave2 = slaveDataSource(slave2Properties);
//             targetDataSources.put(ReadWriteSplitRoutingDataSource.SLAVE2, slave2);
//         }
//         ReadWriteSplitRoutingDataSource dataSource = new ReadWriteSplitRoutingDataSource();
//         dataSource.setTargetDataSources(targetDataSources);// 该方法是AbstractRoutingDataSource的方法
//         dataSource.setDefaultTargetDataSource(master);
//         return dataSource;
//     }
//
//     /**
//      * 主库配置（写操作）
//      *
//      * @param properties
//      * @return
//      */
//     private DataSource masterDataSource(DBProperties properties) {
//         DruidDataSource master = new DruidDataSource();
//         master.setUrl(properties.getUrl());
//         master.setUsername(properties.getUsername());
//         master.setPassword(properties.getPassword());
//         master.setDriverClassName(properties.getDriverClassName());
//         if (properties.getInitialSize() > 0) {
//             master.setInitialSize(properties.getInitialSize());
//         }
//         if (properties.getMinIdle() > 0) {
//             master.setMinIdle(properties.getMinIdle());
//         }
//         if (properties.getMaxActive() > 0) {
//             master.setMaxActive(properties.getMaxActive());
//         }
//         if (properties.getTimeBetweenEvictionRunsMillis() > 0) {
//             master.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
//         }
//         if (properties.getMinEvictableIdleTimeMillis() > 0) {
//             master.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
//         }
//         master.setValidationQuery(properties.getValidationQuery());
//         master.setTestOnBorrow(properties.isTestOnBorrow());
//         master.setTestOnReturn(properties.isTestOnReturn());
//         master.setTestWhileIdle(properties.isTestWhileIdle());
//         try {
//             master.init();
//             return master;
//         } catch (SQLException e) {
//             throw new RuntimeException(e);
//         }
//     }
//
//     /**
//      * 从库配置（读操作）
//      *
//      * @param properties
//      * @return
//      */
//     private DataSource slaveDataSource(DBProperties properties) {
//         DruidDataSource slave = new DruidDataSource();
//         slave.setUrl(properties.getUrl());
//         slave.setUsername(properties.getUsername());
//         slave.setPassword(properties.getPassword());
//         if (properties.getInitialSize() > 0) {
//             slave.setInitialSize(properties.getInitialSize());
//         }
//         if (properties.getMinIdle() > 0) {
//             slave.setMinIdle(properties.getMinIdle());
//         }
//         if (properties.getMaxActive() > 0) {
//             slave.setMaxActive(properties.getMaxActive());
//         }
//         if (properties.getTimeBetweenEvictionRunsMillis() > 0) {
//             slave.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
//         }
//         if (properties.getMinEvictableIdleTimeMillis() > 0) {
//             slave.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
//         }
//         // 启用监控统计功能
//         try {
//             slave.setFilters(properties.getFilters());
//         } catch (SQLException e) {
//             throw new RuntimeException(e);
//         }
//         slave.setTestOnBorrow(properties.isTestOnBorrow());
//         try {
//             slave.init();
//             return slave;
//         } catch (SQLException e) {
//             throw new RuntimeException(e);
//         }
//     }
//
// }
