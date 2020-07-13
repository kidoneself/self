package com.yimao.cloud.out.configuration;

import com.yimao.cloud.out.interceptor.ApiAuthInterceptor;
import com.yimao.cloud.out.interceptor.WebApiInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web相关配置信息
 *
 * @author Zhang Bo
 * @date 2018/6/30.
 */
@Configuration
public class OutWebConfiguration {

    /**
     * WebMvc相关配置
     *
     * @return outWebMvcConfigurer
     */
    @Bean
    @Scope(value = "prototype")
    public WebMvcConfigurer outWebMvcConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * 添加权限拦截器配置
             * @param registry
             */
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(webApiInterceptor()).addPathPatterns("/webapi/**").order(Ordered.HIGHEST_PRECEDENCE);
                registry.addInterceptor(webApiInterceptor()).addPathPatterns("/web/waterDevice/**").order(Ordered.HIGHEST_PRECEDENCE);
                registry.addInterceptor(apiAuthInterceptor()).addPathPatterns("/api/**").order(Ordered.HIGHEST_PRECEDENCE + 1);
            }
        };
    }

    @Bean
    public WebApiInterceptor webApiInterceptor() {
        return new WebApiInterceptor();
    }

    @Bean
    public ApiAuthInterceptor apiAuthInterceptor() {
        return new ApiAuthInterceptor();
    }

}
