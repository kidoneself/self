package com.yimao.cloud.zuul.filter;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.auth.util.AuthUtil;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.handler.JWTHandler;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.zuul.config.SwaggerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

/**
 * @author Zhang Bo
 * @date 2018/8/6.
 */
@Component
@Slf4j
public class AuthFilter extends ZuulFilter {

    private static final String PATH_APP = "/api/app";
    private static final String PATH_CAT = "/api/cat";
    private static final String PATH_CMS = "/api/cms";
    private static final String PATH_HRA = "/api/hra";
    private static final String PATH_OPEN = "/api/open";
    private static final String PATH_OUT = "/api/out";
    private static final String PATH_STATION = "/api/station";
    private static final String PATH_SYSTEM = "/api/system";
    private static final String PATH_WATER = "/api/water";
    private static final String PATH_WECHAT = "/api/wechat";
    private static final String PATH_USER = "/api/user";
    private static final String PATH_TASK = "/api/task";

    @Resource
    private JWTHandler jwtHandler;
    @Resource
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        //前置拦截器
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //最高优先级
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public boolean shouldFilter() {
        String path = RequestContext.getCurrentContext().getRequest().getRequestURI();
        log.info("==================================================");
        log.info("ZuulFilter-->AuthFilter-->path=" + path);
        log.info("==================================================");
        if (path.endsWith(SwaggerProvider.API_URI)) {
            return false;
        }
        //放开被忽略的请求（配置在配置文件中），不做权限验证
        if (CollectionUtil.isNotEmpty(filterProperties.ignoreUrls)) {
            for (String prefix : filterProperties.ignoreUrls) {
                if (path.startsWith(prefix)) {
                    return false;
                }
            }
        }
        //放开TASK服务的所有请求，不做权限验证
        if (path.startsWith(PATH_TASK)) {
            return false;
        }

        return true;
    }

    @Override
    public Object run() {
        log.info("Zuul AuthFilter start...");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //预请求（OPTIONS）处理
        if (CorsUtils.isPreFlightRequest(request)) {
            return null;
        }
        String path = RequestContext.getCurrentContext().getRequest().getRequestURI();

        boolean isOpenapi = false;
        if (CollectionUtil.isNotEmpty(filterProperties.openapiUrls)) {
            isOpenapi = filterProperties.openapiUrls.stream().anyMatch(prefix -> path.startsWith(prefix));
        }

        // 3.用户权限校验
        // openapi 外部系统请求
        if (path.startsWith(PATH_OPEN) || isOpenapi) {
            if (SpringContextHolder.getEnvironment().equalsIgnoreCase(EnvironmentEnum.LOCAL.code)) {
                //本地环境调用接口无需传递ACCESS_TOKEN
                return null;
            } else {
                String accessToken = AuthUtil.getAuthToken(request, AuthConstants.ACCESS_TOKEN);
                log.info("==================================================");
                log.info("Zuul openapi access token is [" + accessToken + "]");
                log.info("==================================================");
                if (filterProperties.openapiKeys.contains(accessToken)) {
                    // 外部请求携带了正确的apikey
                    return null;
                } else {
                    this.noAuth(ctx);
                }
            }
        } else {
            // 内部系统请求
            String authToken = this.getToken(request, path);
            log.info("==================================================");
            log.info("Zuul auth token is [" + authToken + "]");
            log.info("==================================================");
            try {
                // 通过token获取JWT对象信息
                JWTInfo info = jwtHandler.getJWTInfoFromToken(authToken);
                // 将认证token设置到请求头传递到后续服务
                // mutate.header("Content-Type", "text/html; charset=utf8");
                ctx.addZuulRequestHeader(AuthConstants.JWTINFO, URLEncoder.encode(JSONObject.toJSONString(info), "UTF-8"));
                return null;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                this.noAuth(ctx);
            } finally {
                log.info("Zuul AuthFilter end...");
            }
        }
        return null;
    }

    /**
     * 获取token
     *
     * @param request
     * @param path
     * @return
     */
    private String getToken(HttpServletRequest request, String path) {
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
     * 用户权限认证失败，返回无权操作
     *
     * @param ctx
     */
    private void noAuth(RequestContext ctx) {
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        log.info("您没有操作权限[003]。");
        ctx.setResponseBody("您没有操作权限[003]。");
        ctx.getResponse().setContentType("text/html;charset=UTF-8");
    }

}
