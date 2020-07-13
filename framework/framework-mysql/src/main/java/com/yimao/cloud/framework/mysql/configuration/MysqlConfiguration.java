package com.yimao.cloud.framework.mysql.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 描述：mapper扫描配置类
 *
 * @Author Zhang Bo
 * @Date 2019/4/15
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.datasource", name = "type")
@MapperScan("com.yimao.cloud.**.mapper")
public class MysqlConfiguration {
}
