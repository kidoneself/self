// package com.yimao.cloud.framework.mysql.configuration;
//
// import com.alibaba.druid.pool.DruidDataSource;
// import com.alibaba.druid.support.http.StatViewServlet;
// import com.alibaba.druid.support.http.WebStatFilter;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.boot.web.servlet.FilterRegistrationBean;
// import org.springframework.boot.web.servlet.ServletRegistrationBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// import java.util.Arrays;
//
// /**
//  * Druid数据库连接
//  *
//  * @author Zhang Bo
//  * @date 2018/7/8.
//  */
// @Configuration
// @ConditionalOnClass(DruidDataSource.class)
// @ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.alibaba.druid.pool.DruidDataSource")
// public class DruidConfiguration {
//
//     //配置Druid的监控
//     //1、配置一个管理后台的Servlet
//     @Bean
//     public ServletRegistrationBean<StatViewServlet> statViewServlet() {
//         ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>();
//         bean.setServlet(new StatViewServlet());
//         bean.addUrlMappings("/druid/*");
//         bean.addInitParameter("loginUsername", "yimao");
//         bean.addInitParameter("loginPassword", "123456");
//         bean.addInitParameter("resetEnable", "false");
//         return bean;
//     }
//
//     //2、配置一个web监控的filter
//     @Bean
//     public FilterRegistrationBean<WebStatFilter> webStatFilter() {
//         FilterRegistrationBean<WebStatFilter> bean = new FilterRegistrationBean<>();
//         bean.setFilter(new WebStatFilter());
//         bean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
//         bean.setUrlPatterns(Arrays.asList("/*"));
//         return bean;
//     }
//
// }
