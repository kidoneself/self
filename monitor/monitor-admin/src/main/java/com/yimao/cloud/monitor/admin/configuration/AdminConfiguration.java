// package com.yimao.cloud.monitor.admin.configuration;
//
// import de.codecentric.boot.admin.server.config.AdminServerProperties;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
// import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//
// /**
//  * @author Zhang Bo
//  * @date 2018/6/30.
//  */
// @Configuration
// public class AdminConfiguration {
//
//     /**
//      * Spring boot 2.0 Spring Security配置
//      */
//     @EnableWebSecurity
//     public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//         private final String adminContextPath;
//
//         public WebSecurityConfig(AdminServerProperties adminServerProperties) {
//             this.adminContextPath = adminServerProperties.getContextPath();
//         }
//
//         @Override
//         protected void configure(HttpSecurity http) throws Exception {
//             SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
//             successHandler.setTargetUrlParameter("redirectTo");
//
//             http.authorizeRequests()
//                     .antMatchers(adminContextPath + "/assets/**").permitAll()
//                     .antMatchers(adminContextPath + "/notifications").permitAll()
//                     .antMatchers(adminContextPath + "/login").permitAll()
//                     .anyRequest().authenticated()
//                     .and().formLogin().loginPage(adminContextPath + "/login").successHandler(successHandler)
//                     .and().logout().logoutUrl(adminContextPath + "/logout")
//                     .and().httpBasic()
//                     .and().csrf().disable();
//         }
//     }
//
// }
