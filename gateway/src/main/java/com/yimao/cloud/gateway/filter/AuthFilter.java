package com.yimao.cloud.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.auth.util.AuthUtil;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.AuthException;
import com.yimao.cloud.base.handler.JWTHandler;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.gateway.swagger.config.SwaggerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * 网关权限过滤器
 *
 * @author Zhang Bo
 * @date 2018/8/6.
 */
@Configuration
@Slf4j
public class AuthFilter implements GlobalFilter {

    private static final String PATH_APP = "/api/app";
    private static final String PATH_CAT = "/api/cat";
    private static final String PATH_CMS = "/api/cms";
    private static final String PATH_HRA = "/api/hra";
    private static final String PATH_OPENAPI = "/api/open";
    private static final String PATH_ORDER = "/api/order";
    private static final String PATH_OUT = "/api/out";
    private static final String PATH_STATION = "/api/station";
    private static final String PATH_SYSTEM = "/api/system";
    private static final String PATH_WATER = "/api/water";
    private static final String PATH_PAD = "/api/pad";
    private static final String PATH_WECHAT = "/api/wechat";
    private static final String PATH_USER = "/api/user";
    private static final String PATH_TASK = "/api/task";
    private static final String ENGINEER_APP = "/api/engineer";

    @Resource
    private RedisCache redisCache;
    @Resource
    private JWTHandler jwtHandler;
    @Resource
    private FilterProperties filterProperties;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 权限过滤器，针对需要进行权限限制的接口进行权限控制
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest.Builder mutate = request.mutate();

        String path = this.getRequestPath(exchange, request);
        log.info("Gateway AuthFilter start...path=" + path);

        //记录请求路径
        // rabbitTemplate.convertAndSend(RabbitConstant.REQUEST_PATH_RECORD, path);

        ServerHttpRequest newRequest = mutate.build();
        // 1.过滤掉不需要进行用户权限验证的请求allowMaximumSizeToDivergeFromCoreSize
        // 2.释放Swagger /v2/api-docs请求
        if (path.endsWith(SwaggerProvider.API_URI)) {
            return chain.filter(exchange.mutate().request(newRequest).build());
        }
        
        //放开被忽略的请求（配置在配置文件中），不做权限验证
        if (CollectionUtil.isNotEmpty(filterProperties.ignoreUrls)) {
            for (String prefix : filterProperties.ignoreUrls) {
                if (path.startsWith(prefix)) {
                    return chain.filter(exchange.mutate().request(newRequest).build());
                }
            }
        }
        //放开TASK服务的所有请求，不做权限验证
        if (path.startsWith(PATH_TASK)) {
            return chain.filter(exchange.mutate().request(newRequest).build());
        }
        boolean isOpenapi = false;
        if (CollectionUtil.isNotEmpty(filterProperties.openapiUrls)) {
            isOpenapi = filterProperties.openapiUrls.stream().anyMatch(prefix -> path.startsWith(prefix));
        }

        // 3.用户权限校验
        // openapi 外部系统请求
        if (path.startsWith(PATH_OPENAPI) || isOpenapi) {
            if (SpringContextHolder.getEnvironment().equalsIgnoreCase(EnvironmentEnum.LOCAL.code)) {
                //本地环境调用接口无需传递ACCESS_TOKEN
                return chain.filter(exchange.mutate().request(newRequest).build());
            } else {
                String accessToken = AuthUtil.getAuthToken(request, AuthConstants.ACCESS_TOKEN);
                if (!Constant.PRO_ENVIRONMENT) {
                    log.info("Gateway openapi access token is [" + accessToken + "]");
                }
                if (StringUtil.isEmpty(accessToken)) {
                    accessToken = AuthUtil.getAuthToken(request, "accessKey");
                }
                if (filterProperties.openapiKeys.contains(accessToken)) {
                    // 外部请求携带了正确的apikey
                    return chain.filter(exchange.mutate().request(newRequest).build());
                } else {
                    // 外部请求携带了错误的apikey
                    log.info("您没有操作权限【001】。");
                    return getVoidMono(exchange, "您没有操作权限【001】。");
                }
            }
        } else if (path.startsWith(PATH_ORDER)) {
            //order服务调用
            return getVoidMono(exchange, "您没有操作权限");
        } else if (path.startsWith(PATH_WATER + PATH_PAD)) {
            // String accessKey = AuthUtil.getAuthToken(request, "accessKey");
            // log.info("/api/pad   accessKey=" + accessKey);
            // if (filterProperties.openapiKeys.contains(accessKey)) {
            //     //pad调用接口
            //     return chain.filter(exchange.mutate().request(newRequest).build());
            // } else {
            //     // pad端请求携带了错误的apikey
            //     log.info("您没有操作权限【002】。");
            //     return getVoidMono(exchange, "您没有操作权限【002】。");
            // }
            //TODO POST请求获取不到accessKey
            return chain.filter(exchange.mutate().request(newRequest).build());
        } else if (path.startsWith(PATH_OUT)) {
            //out调用接口
            return chain.filter(exchange.mutate().request(newRequest).build());
        } else {
            // 内部系统请求
            String authToken = this.getToken(request, path);
            if (!Constant.PRO_ENVIRONMENT) {
                log.info("Gateway auth token is [" + authToken + "]");
            }
            try {
                // 通过token获取JWT对象信息
                // JWTInfo info = AuthUtil.getJWTToken(authToken, pubKey);
                JWTInfo jwt = jwtHandler.getJWTInfoFromToken(authToken);
                //不同登录端的权限匹配问题校验，防止使用别的系统的token交叉登录
                boolean check = this.checkSystemType(jwt.getType(), path);
                if (!check) {
                    log.info("请先登录【003】。");
                    return getVoidMono(exchange, "请先登录");
                }
                //设置相应头TOKEN信息
                this.setTokenIntoResponseHeader(response, jwt);
                // 将认证token设置到请求头传递到后续服务
                // mutate.header("Content-Type", "text/html; charset=utf8");
                mutate.header(AuthConstants.JWTINFO, URLEncoder.encode(JSONObject.toJSONString(jwt), "UTF-8"));
                newRequest = mutate.build();
                return chain.filter(exchange.mutate().request(newRequest).response(response).build());
            } catch (AuthException e) {
                log.error(e.getMessage(), e);
                return getVoidMono(exchange, e.getMessage());
            } catch (Exception e) {
                log.info("操作失败，请重新登录【002】。");
                log.error(e.getMessage(), e);
                return getVoidMono(exchange, "操作失败，请重新登录");
            } finally {
                if (!Constant.PRO_ENVIRONMENT) {
                    log.info("Gateway AuthFilter end...");
                }
            }
        }
    }

    /**
     * 将jwt token设置到响应header
     */
    private void setTokenIntoResponseHeader(ServerHttpResponse response, JWTInfo info) {
        HttpHeaders headers = response.getHeaders();
        // response header name
        String headerKey = AuthConstants.USER_TOKEN;
        if (Objects.equals(info.getType(), SystemType.JXSAPP.value)) {
            headerKey = AuthConstants.USER_TOKEN;
        }
        if (Objects.equals(info.getType(), SystemType.WECHAT.value)) {
            headerKey = AuthConstants.USER_TOKEN;
        }
        if (Objects.equals(info.getType(), SystemType.H5.value)) {
            headerKey = AuthConstants.USER_TOKEN;
        }
        if (Objects.equals(info.getType(), SystemType.SYSTEM.value)) {
            headerKey = AuthConstants.ADMIN_TOKEN;
        }
        if (Objects.equals(info.getType(), SystemType.WATER.value)) {
            headerKey = AuthConstants.WATER_ADMIN_TOKEN;
        }
        if (Objects.equals(info.getType(), SystemType.STATION.value)) {
            headerKey = AuthConstants.STATION_TOKEN;
        }
        if (Objects.equals(info.getType(), SystemType.ENGINEER.value)) {
            headerKey = AuthConstants.ENGINEER_TOKEN;
        }
        // response header value
        String goodToken = jwtHandler.getGoodToken(info);
        headers.add(headerKey, goodToken);
    }

    /**
     * 使用redis校验token有效性（在其它设备登录、修改密码）
     *
     * @param id    登录用户ID
     * @param type  登陆的系统
     * @param token 当前请求携带的token
     */
    public boolean checkJWTToken(JWTInfo info, String token) {
        String cacheToken = redisCache.get(AuthConstants.JWT_TOKEN_PREFIX + "_" + info.getType() + "_" + info.getId());
        if (StringUtil.isNotEmpty(cacheToken) && cacheToken.equalsIgnoreCase(token)) {
            //当前请求携带的token和缓存的token一直，则有效
            return true;
        }
        return false;
    }

    /**
     * 不同登录端的权限匹配问题校验，防止使用别的系统的token交叉登录
     */
    private boolean checkSystemType(Integer type, String path) {
        if (path.startsWith(PATH_APP) && !Objects.equals(type, SystemType.JXSAPP.value)) {
            return false;
        }
        if (path.startsWith(ENGINEER_APP) && !Objects.equals(type, SystemType.ENGINEER.value)) {
            return false;
        }
        if (path.startsWith(PATH_WECHAT)) {
            if (!Objects.equals(type, SystemType.WECHAT.value) && !Objects.equals(type, SystemType.H5.value)) {
                return false;
            }
        }
        if (path.startsWith(PATH_SYSTEM) && !Objects.equals(type, SystemType.SYSTEM.value)) {
            return false;
        }
        if (path.startsWith(PATH_WATER) && !Objects.equals(type, SystemType.WATER.value)) {
            return false;
        }
        if (path.startsWith(PATH_STATION) && !Objects.equals(type, SystemType.STATION.value)) {
            return false;
        }
        return true;
    }

    /**
     * 获取token
     */
    private String getToken(ServerHttpRequest request, String path) {
        if (path.startsWith(PATH_APP)) {
            return AuthUtil.getAuthToken(request, AuthConstants.USER_TOKEN);
        }
        if (path.startsWith(PATH_CAT)) {
            return AuthUtil.getAuthToken(request, AuthConstants.USER_TOKEN);
        }
        if (path.startsWith(PATH_HRA)) {
            return AuthUtil.getAuthToken(request, AuthConstants.USER_TOKEN);
        }
        if (path.startsWith(PATH_WECHAT)) {
            return AuthUtil.getAuthToken(request, AuthConstants.USER_TOKEN);
        }
        if (path.startsWith(PATH_SYSTEM)) {
            return AuthUtil.getAuthToken(request, AuthConstants.ADMIN_TOKEN);
        }
        if (path.startsWith(PATH_CMS)) {
            return AuthUtil.getAuthToken(request, AuthConstants.ADMIN_TOKEN);
        }
        if (path.startsWith(PATH_STATION)) {
            return AuthUtil.getAuthToken(request, AuthConstants.STATION_TOKEN);
        }
        if (path.startsWith(PATH_WATER)) {
            return AuthUtil.getAuthToken(request, AuthConstants.WATER_ADMIN_TOKEN);
        }
        if (path.startsWith(ENGINEER_APP)) {
            return AuthUtil.getAuthToken(request, AuthConstants.ENGINEER_TOKEN);
        }
        //本地调试用
        if (path.startsWith(PATH_OUT)) {
            return AuthUtil.getAuthToken(request, AuthConstants.ADMIN_TOKEN);
        }
        if (path.startsWith(PATH_USER)) {
            return AuthUtil.getAuthToken(request, AuthConstants.ADMIN_TOKEN);
        }
        return null;
    }

    /**
     * 获取完整的请求url
     */
    private String getRequestPath(ServerWebExchange exchange, ServerHttpRequest request) {
        // Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        // 获取请求uri
        LinkedHashSet<URI> requiredAttribute = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        Iterator<URI> iterator = requiredAttribute.iterator();
        String path = request.getPath().pathWithinApplication().value();
        while (iterator.hasNext()) {
            URI uri = iterator.next();
            return uri.getPath();
            // if (uri.getPath().startsWith(Constant.GATE_WAY_PREFIX)) {
            //     path = uri.getPath();
            // }
        }
        return path;
    }

    /**
     * 用户权限认证失败，返回无权操作
     */
    private Mono<Void> getVoidMono(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

}
