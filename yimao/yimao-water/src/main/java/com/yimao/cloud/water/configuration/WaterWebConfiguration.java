package com.yimao.cloud.water.configuration;

import com.yimao.cloud.water.interceptor.WebApiInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 描述：web相关配置信息
 *
 * @Author Zhang Bo
 * @Date 2019/10/24
 */
@Configuration
public class WaterWebConfiguration {

    /**
     * WebMvc相关配置
     *
     * @return outWebMvcConfigurer
     */
    @Bean
    @Scope(value = "prototype")
    public WebMvcConfigurer waterWebMvcConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * 添加权限拦截器配置
             * @param registry
             */
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(webApiInterceptor()).addPathPatterns("/**").order(Ordered.HIGHEST_PRECEDENCE);
            }
        };
    }

    @Bean
    public WebApiInterceptor webApiInterceptor() {
        return new WebApiInterceptor();
    }

}
