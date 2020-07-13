package com.yimao.cloud.base.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhang Bo
 * @date 2019/5/21
 */
@Configuration
@ConfigurationProperties("yimao.domain")
@Getter
@Setter
public class DomainProperties {

    public String api;
    public String admin;
    public String ad;
    public String wechat;
    public String health;
    public String yunSign;
    public String image;

}
