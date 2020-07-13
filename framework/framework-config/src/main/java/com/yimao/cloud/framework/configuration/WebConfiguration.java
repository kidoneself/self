package com.yimao.cloud.framework.configuration;

import com.yimao.cloud.framework.interceptor.FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web相关配置信息
 *
 * @author Zhang Bo
 * @date 2018/6/30.
 */
@Configuration
public class WebConfiguration {

    /**
     * Spring boot 2.0 Spring Security配置
     */
    @EnableWebSecurity
    protected class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().permitAll()
                    .and().httpBasic()
                    .and().csrf().disable();
        }
    }

    /**
     * WebMvc相关配置
     *
     * @return WebMvcConfigurer
     */
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * 允许跨域
             * @param registry
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedHeaders("*").allowedMethods("*").allowCredentials(true);
            }

            /**
             * 添加权限拦截器配置
             * @param registry
             */
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(feignRequestInterceptor())
                        .order(Ordered.HIGHEST_PRECEDENCE)
                        .addPathPatterns("/**")
                        .excludePathPatterns("/error", "/actuator/**", "/monitor/**");
            }
        };
    }

    @Bean
    public FeignRequestInterceptor feignRequestInterceptor() {
        return new FeignRequestInterceptor();
    }

}
