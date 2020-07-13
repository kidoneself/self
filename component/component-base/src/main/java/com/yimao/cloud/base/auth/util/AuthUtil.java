package com.yimao.cloud.base.auth.util;

import com.yimao.cloud.base.utils.CookieUtil;
import com.yimao.cloud.base.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zhang Bo
 * @date 2018/11/5.
 */
@Slf4j
public class AuthUtil {

    // /**
    //  * 创建token
    //  *
    //  * @param jwtInfo
    //  * @param prikey
    //  * @param expire
    //  */
    // public static String createJWTToken(JWTInfo jwtInfo, String prikey, int expire) {
    //     return JWTHelper.generateJWTInfo(jwtInfo, prikey, expire);
    // }

    // /**
    //  * 根据token和公钥获取JWTInfo
    //  *
    //  * @param token
    //  * @param pubkey
    //  */
    // public static JWTInfo getJWTToken(String token, String pubkey) {
    //     try {
    //         return JWTHelper.getJWTInfoFromToken(token, pubkey);
    //     } catch (ExpiredJwtException ex) {
    //         throw new AuthException("登录超时，请重新登录");
    //     } catch (IllegalArgumentException ex) {
    //         throw new AuthException("请先登录");
    //     }
    // }

    /**
     * 从请求信息中获取用户认证token
     */
    public static String getAuthToken(ServerHttpRequest request, String tokenKey) {
        // 从请求header中获取用户权限认证信息
        String authToken = request.getHeaders().getFirst(tokenKey);
        // 从Cookie中获取用户权限认证信息
        if (StringUtil.isBlank(authToken)) {
            HttpCookie httpCookie = request.getCookies().getFirst(tokenKey);
            authToken = httpCookie == null ? null : httpCookie.getValue();
        }
        // 从请求参数中获取用户权限认证信息
        if (StringUtil.isBlank(authToken)) {
            return request.getQueryParams().getFirst(tokenKey);
        }
        return authToken;
    }

    /**
     * 从请求信息中获取用户认证token
     */
    public static String getAuthToken(HttpServletRequest request, String tokenKey) {
        // 从请求header中获取用户权限认证信息
        String authToken = request.getHeader(tokenKey);
        // 从Cookie中获取用户权限认证信息
        if (StringUtil.isBlank(authToken)) {
            authToken = CookieUtil.getCookieValue(request, tokenKey);
        }
        // 从请求参数中获取用户权限认证信息
        if (StringUtil.isBlank(authToken)) {
            return request.getParameter(tokenKey);
        }
        return authToken;
    }

}