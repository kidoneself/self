package com.yimao.cloud.gateway.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：网关权限控制属性
 *
 * @Author Zhang Bo
 * @Date 2019/1/18 16:11
 * @Version 1.0
 */
@Configuration
@ConfigurationProperties("yimao.auth")
@Getter
@Setter
public class FilterProperties {

    public List<String> ignoreUrls = new ArrayList<>();

    public List<String> openapiUrls = new ArrayList<>();

    public List<String> openapiKeys = new ArrayList<>();

}
