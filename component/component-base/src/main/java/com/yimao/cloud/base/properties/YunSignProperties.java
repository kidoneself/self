package com.yimao.cloud.base.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author LiuLongJie
 * @date 2019/10/25
 */
@Configuration
@ConfigurationProperties("yimao.yunsign")
@Getter
@Setter
public class YunSignProperties {

    private String serviceAppid;
    private String serviceKey;
    private String workorderServiceAppid;
    private String workorderServiceKey;
    private String serviceTempleid;
    private String serviceSealid;
    private String workorderServiceSealid;
    private String templeid;

}
