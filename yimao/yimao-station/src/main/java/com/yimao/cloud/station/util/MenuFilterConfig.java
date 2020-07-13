package com.yimao.cloud.station.util;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 站务系统菜单过滤配置
 * @author yaoweijun
 *
 */
@Data
@Configuration
@ConfigurationProperties(prefix="menu-filter")
public class MenuFilterConfig {
	private List<String> list;

}
