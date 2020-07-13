package com.yimao.cloud.station.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.yimao.cloud.station.intercept.PermissionInterceptor;

/**
 * web相关配置信息
 *
 * @author yaoweijun
 * @date 2019/12/23
 */
@Configuration
public class StationConfiguration implements WebMvcConfigurer {

    /**
     * 添加权限拦截器配置
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor())
                .order(Ordered.HIGHEST_PRECEDENCE + 1)
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/actuator/**", "/monitor/**");
    }

    /**
     * 权限拦截器具体实现逻辑
     *
     * @return
     */
    @Bean
    public PermissionInterceptor permissionInterceptor() {
        return new PermissionInterceptor();
    }

}
