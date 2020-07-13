package com.yimao.cloud.framework.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置
 *
 * @author Zhang Bo
 * @date 2018/7/9.
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.redis", name = "host")
public class RedisConfiguration {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private int database;

    @Value("${spring.redis.host2}")
    private String host2;
    @Value("${spring.redis.port2}")
    private int port2;
    @Value("${spring.redis.password2}")
    private String password2;
    @Value("${spring.redis.database2}")
    private int database2;

    @Bean(name = "valueOperations")
    public ValueOperations<String, String> valueOperations(StringRedisTemplate stringRedisTemplate) {
        return stringRedisTemplate.opsForValue();
    }
    
    /**
     * 对hash类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
	@Bean
	public HashOperations<String,String,Object> hashOperations(RedisTemplate<String,Object> redisTemplate){
		return redisTemplate.opsForHash();
	}


    /**
     * 普通的redisTemplate，通常请使用这个类
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory(host, port, password, database));
        redisTemplate.setKeySerializer(new StringRedisSerializer());//设置key的序列化器
        redisTemplate.setValueSerializer(new RedisConverter());//设置值的序列化器
        //hash结构序列化
        FastJsonRedisSerializer redisSerializer = new FastJsonRedisSerializer(Object.class);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(redisSerializer);
        return redisTemplate;
    }

    /**
     * 专门为健康自测小程序开放的redisTemplate，因为小程序的access_token正式环境和测试环境是一样的
     */
    @Bean(name = "redisTemplateMini")
    @ConditionalOnProperty(prefix = "spring.redis", name = "host2")
    public RedisTemplate<String, String> redisTemplateMini() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory(host2, port2, password2, database2));
        redisTemplate.setKeySerializer(new StringRedisSerializer());//设置key的序列化器
        //健康自测小程序专用RedisTemplate，只保存string类型缓存，所以设置字符串序列化器
        redisTemplate.setValueSerializer(new StringRedisSerializer());//设置值的序列化器
        return redisTemplate;
    }

    /**
     * 创建Jedis连接工厂
     *
     * @param host     地址
     * @param port     端口
     * @param password 密码
     * @param database 数据库
     */
    private JedisConnectionFactory jedisConnectionFactory(String host, int port, String password, int database) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(RedisPassword.of(password));
        config.setDatabase(database);
        return new JedisConnectionFactory(config);
    }

}
