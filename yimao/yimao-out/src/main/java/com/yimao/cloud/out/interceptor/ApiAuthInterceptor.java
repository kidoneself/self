package com.yimao.cloud.out.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ApiAuth拦截器，只拦截/api/**
 *
 * @author Zhang Bo
 * @date 2019/11/11
 */
@Slf4j
public class ApiAuthInterceptor extends HandlerInterceptorAdapter {

    private static final String ACCESS_KEY = "LH6AB08F8G7324H6GBC42D3OC72GLJ25";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        log.info("out---ApiAuthInterceptor---requestURI=" + requestURI);
        //云签接口放行
        if (requestURI.startsWith("/api/yunsign")
                || requestURI.startsWith("/api/distributor/quickOnline/backCall")
                || requestURI.startsWith("/api/area/isSyncOnline")
                || requestURI.startsWith("/api/workorder/ispaid")) {
            return true;
        }
        if (requestURI.startsWith("/api/")) {
            String accessKey = request.getHeader("accessKey");
            if (StringUtil.isEmpty(accessKey)) {
                accessKey = request.getParameter("accessKey");
            }
            log.info("out---ApiAuthInterceptor---accessKey=" + accessKey);
            if (ACCESS_KEY.equalsIgnoreCase(accessKey)) {
                return true;
            } else {
                this.returnErrpr(response);
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    private void returnErrpr(HttpServletResponse response) {
        JSONObject json = new JSONObject();
        json.put("code", 401);
        json.put("msg", "您没有操作权限");
        json.put("success", false);
        try {
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(json.toString());
        } catch (Exception e) {
        }
    }

}
