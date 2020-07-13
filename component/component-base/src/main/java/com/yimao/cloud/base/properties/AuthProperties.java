package com.yimao.cloud.base.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhang Bo
 * @date 2019/5/21
 */
@Configuration
@Getter
@Setter
public class AuthProperties {

    @Value("${auth.jwt.secret}")
    public String secret;
    //客户端过期时间
    @Value("${auth.jwt.expire1}")
    public int expire1;
    //管理后台过期时间
    @Value("${auth.jwt.expire2}")
    public int expire2;
    //安装工APP过期时间
    @Value("${auth.jwt.expire3}")
    public int expire3;
    // private String pubKey;
    // private String priKey;

}
