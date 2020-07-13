package com.yimao.cloud.user.runner;

import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.properties.AuthProperties;
import com.yimao.cloud.base.utils.RSAUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

@Configuration
public class AuthRunner implements CommandLineRunner {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ValueOperations<String, String> valueOperations;
    @Resource
    private AuthProperties authProperties;

    /**
     * 初始化鉴权公钥私钥
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        Boolean hasPriKey = redisTemplate.hasKey(AuthConstants.AUTH_JWT_PRIKEY);
        Boolean hasPubKey = redisTemplate.hasKey(AuthConstants.AUTH_JWT_PUBKEY);
        if (Objects.equals(hasPriKey, true) && Objects.equals(hasPubKey, true)) {
            // authProperties.setPriKey(valueOperations.get(AuthConstants.AUTH_JWT_PRIKEY));
            // authProperties.setPubKey(valueOperations.get(AuthConstants.AUTH_JWT_PUBKEY));
        } else {
            Map<String, String> keyMap = RSAUtil.generateKey(authProperties.getSecret());
            // authProperties.setPriKey(keyMap.get(AuthConstants.PRI));
            // authProperties.setPubKey(keyMap.get(AuthConstants.PUB));
            valueOperations.set(AuthConstants.AUTH_JWT_PRIKEY, keyMap.get(AuthConstants.PRI));
            valueOperations.set(AuthConstants.AUTH_JWT_PUBKEY, keyMap.get(AuthConstants.PUB));
        }
    }

}
