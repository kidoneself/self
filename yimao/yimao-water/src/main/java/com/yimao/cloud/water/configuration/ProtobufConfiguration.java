package com.yimao.cloud.water.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 描述：Protobuf配置
 *
 * @Author Zhang Bo
 * @Date 2019/1/29 15:10
 * @Version 1.0
 */
@Configuration
public class ProtobufConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
