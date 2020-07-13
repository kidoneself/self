// package com.yimao.cloud.monitor.dashboard.configuration;
//
// import org.springframework.context.annotation.Configuration;
//
// /**
//  * @author Zhang Bo
//  * @date 2018/6/30.
//  */
// @Configuration
// public class DashboardConfiguration {
//
//     // /**
//     //  * Unable to connect to Command Metric Stream.问题解决方案
//     //  */
//     // @Bean
//     // public ServletRegistrationBean<HystrixMetricsStreamServlet> streamServlet() {
//     //     HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
//     //     ServletRegistrationBean<HystrixMetricsStreamServlet> registrationBean = new ServletRegistrationBean<>(streamServlet);
//     //     registrationBean.setLoadOnStartup(1);
//     //     registrationBean.addUrlMappings("hystrix.stream");
//     //     registrationBean.setName("HystrixMetricsStreamServlet");
//     //     return registrationBean;
//     // }
//
//     // /**
//     //  * Spring boot 2.0 Spring Security配置
//     //  */
//     // @EnableWebSecurity
//     // protected class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//     //     @Override
//     //     protected void configure(HttpSecurity http) throws Exception {
//     //         //Spring Security默认开启了所有CSRF攻击防御，禁用CSRF防御。使用basic auth认证方式
//     //         http.authorizeRequests().anyRequest().authenticated()
//     //                 .and().httpBasic()
//     //                 .and().csrf().disable();
//     //     }
//     // }
//
// }
