package com.yimao.cloud.base.handler;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.AuthException;
import com.yimao.cloud.base.properties.AuthProperties;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.RSAUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @author Zhang Bo
 * @date 2019/9/29
 */
@Component
@Slf4j
public class JWTHandler {

    /**
     * 如果有效期小于5分钟，则需要创建新的token，老的token在有效期内仍然可以使用
     */
    private static final long ADVANCE_APP_EXPIRE_TIME = 3600L;
    private static final long ADVANCE_SYS_EXPIRE_TIME = 300L;

    @Resource
    private AuthProperties authProperties;
    @Resource
    private RedisCache redisCache;

    /**
     * 获取token的缓存key
     */
    private String getKey(JWTInfo jwt) {
        return AuthConstants.JWT_TOKEN_PREFIX + "_" + jwt.getType() + "_" + jwt.getId();
    }

    /**
     * 获取token的缓存key
     */
    private String getTokenKey(String token) {
        return AuthConstants.JWT_TOKEN_PREFIX + ":" + token;
    }
    
    /**
     * 服务站员工缓存
     */
    public String getStationKey(JWTInfo jwt) {
    	 return AuthConstants.JWT_STATIONTOKEN_PREFIX + "_" + jwt.getStationCompanyId() + "_" + jwt.getId();
    }

    /**
     * 获取过期类型：1-expire1；2-expire2；3-expire3
     */
    private int getExpireType(JWTInfo jwt) {
        if (Objects.equals(jwt.getType(), SystemType.JXSAPP.value) || Objects.equals(jwt.getType(), SystemType.WECHAT.value ) || Objects.equals(jwt.getType(), SystemType.H5.value )) {
            return 1;
        }
        if (Objects.equals(jwt.getType(), SystemType.SYSTEM.value) || Objects.equals(jwt.getType(), SystemType.WATER.value) || Objects.equals(jwt.getType(), SystemType.STATION.value)) {
            return 2;
        }
        if (Objects.equals(jwt.getType(), SystemType.ENGINEER.value)) {
            return 3;
        }
        return 0;
    }

    /**
     * 获取过期时间（单位：秒）
     */
    private int getExpire(JWTInfo jwt) {
        //过期类型：1-expire1；2-expire2；3-expire3
        int type = getExpireType(jwt);
        if (type == 1) {
            return authProperties.getExpire1();
        } else if (type == 2) {
            return authProperties.getExpire2();
        } else if (type == 3) {
            return authProperties.getExpire3();
        }
        return -1;
    }

    /**
     * 获取可用的token
     * 如该用户当前token可用，即返回
     * 当前token不可用，则返回一个新token
     */
    public String getGoodToken(JWTInfo jwt) {
        String token = redisCache.get(getKey(jwt));
        //校验当前token能否使用，不能使用则生成新token
        boolean flag = this.checkToken(token, jwt);
        if (flag) {
            String jwtStr = redisCache.get(getTokenKey(token));
            if (StringUtil.isNotEmpty(jwtStr) && !"001".equals(jwtStr)) {
                // 更新缓存的系统时间
                redisCache.set(getTokenKey(token), System.currentTimeMillis(), redisCache.getExpire(getTokenKey(token)));
            }
            return token;
        } else {
            String newToken = this.createJWTToken(jwt);
            //老token设置过期时间5秒，主要应对那些新的token还没返回到客户端且正在调用的接口
            // redisCache.set(getTokenKey(token), "000", 2);
            return newToken;
        }
    }

    /**
     * 检查当前token是否还能继续使用
     * true：可以  false：不建议
     */
    private boolean checkToken(String token, JWTInfo jwt) {
        try {
            String jwtStr = redisCache.get(getTokenKey(token));
            if (StringUtil.isEmpty(jwtStr) || "001".equals(jwtStr)) {
                return false;
            }
            long expireTime = getExpireDateFromToken(token).getTime();
            long diff = expireTime - System.currentTimeMillis();
            //过期类型：1-expire1；2-expire2；3-expire3
            int type = getExpireType(jwt);
            long millis = Long.parseLong(jwtStr);
            long curmillis = System.currentTimeMillis();
            if (type == 1) {
                if (diff < ADVANCE_APP_EXPIRE_TIME * 1000) {
                    //如果有效期小于1小时，则更新token
                    return false;
                }
                // 3小时更新一次token
                if (curmillis > millis + 3 * 60 * 60 * 1000) {
                    //更新值
                    return false;
                }
            } else if (type == 2) {
                if (diff < ADVANCE_SYS_EXPIRE_TIME * 1000) {
                    //如果有效期小于5分钟，则更新token
                    return false;
                }
                // 3小时更新一次token
                if (curmillis > millis + 3 * 60 * 60 * 1000) {
                    //更新值
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // /**
    //  * 生成新token时，初始化token
    //  */
    // private void initNewToken(JWTInfo jwt, String newToken) {
    //     String key = getKey(jwt);
    //     String token = redisCache.get(key);
    //     if (StringUtil.isNotEmpty(token)) {
    //         //老token设置过期时间3秒，主要应对那些新的token还没返回到客户端且正在调用的接口
    //         redisCache.set(token, jwt, 3);
    //     }
    //     //新token初始化
    //     redisCache.set(newToken, jwt);
    // }

    /**
     * 创建新的jwt token
     *
     * @param jwt JWT的主体信息
     */
    public String createJWTToken(JWTInfo jwt) {
        //过期时间（单位：秒）
        int expire = getExpire(jwt);
        //jwt的签发时间
        Date now = new Date();
        //JWT过期时间，设置在凌晨，使用概率比较低的时间点，避免用户正在使用token过期，数据丢失
        Date expireDate = DateUtil.JWTTokenExpirationTime(now, expire);
        JwtBuilder builder = Jwts.builder()
                //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                // .setClaims(claims)
                //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(UUIDUtil.longuuid32())
                .setIssuedAt(now)
                //sub(Subject)：代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userId，roldid之类的，作为用户的唯一标志。
                .setSubject(JSONObject.toJSONString(jwt))
                .signWith(SignatureAlgorithm.RS256, RSAUtil.loadPrivateKey(redisCache.get(AuthConstants.AUTH_JWT_PRIKEY)))
                .setExpiration(expireDate);
        String token = builder.compact();

        //前一次登录尚未失效，又进行了登录，老的token处理逻辑，为了提示“您已在其它设备登录”
        dealWithOldToken(jwt);

        // 将token设置到缓存里
        redisCache.set(getKey(jwt), token, expire);
        // 将当前系统时间放入缓存中，后续通过时间判断是否需要更新token
        redisCache.set(getTokenKey(token), System.currentTimeMillis(), expire);
        return token;
    }

    /**
     * 登录，老的token处理逻辑，为了提示“您已在其它设备登录”
     */
    private void dealWithOldToken(JWTInfo jwt) {
        String oldToken = redisCache.get(getKey(jwt));
        if (StringUtil.isNotEmpty(oldToken)) {
            // try {
            // long expireTime = getExpireDateFromToken(oldToken).getTime();
            // long diff = expireTime - System.currentTimeMillis();
            //过期类型：1-expire1；2-expire2；3-expire3
            // int type = getExpireType(jwt);
            // int remainingExpire = redisCache.getExpire(getKey(jwt));
            // if (type == 1 && diff > ADVANCE_APP_EXPIRE_TIME * 1000) {
            //     redisCache.set(getTokenKey(oldToken), "001", 2);
            // } else if (type == 2 && diff > ADVANCE_SYS_EXPIRE_TIME * 1000) {
            redisCache.set(getTokenKey(oldToken), "001", 5);
            // }
            // } catch (ExpiredJwtException ex) {
            //     redisCache.set(getTokenKey(oldToken), "001", 2);
            // }
        }
    }

    /**
     * 获取jwt token中的用户信息
     */
    public JWTInfo getJWTInfoFromToken(String token) {
        try {
            String subject = getClaimFromToken(token).getSubject();
            JWTInfo jwt = JSONObject.parseObject(subject, JWTInfo.class);

            String jwtStr = redisCache.get(getTokenKey(token));
            if (StringUtil.isEmpty(jwtStr)) {
                log.info("登录失效，请重新登录。");
                throw new AuthException("登录失效，请重新登录。");
            }
            return jwt;
        } catch (ExpiredJwtException ex) {
            log.info("登录超时，请重新登录。");
            throw new AuthException("登录超时，请重新登录。");
        } catch (IllegalArgumentException ex) {
            throw new AuthException("请先登录。");
        }
    }

    /**
     * 获取jwt token中的过期时间
     */
    private Date getExpireDateFromToken(String token) {
        return getClaimFromToken(token).getExpiration();
    }

    /**
     * 获取jwt的payload部分
     */
    private Claims getClaimFromToken(String token) {
        return Jwts.parser().setSigningKey(RSAUtil.loadPublicKey(redisCache.get(AuthConstants.AUTH_JWT_PUBKEY))).parseClaimsJws(token).getBody();
    }

}
