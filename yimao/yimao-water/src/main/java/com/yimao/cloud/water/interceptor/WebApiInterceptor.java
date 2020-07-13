package com.yimao.cloud.water.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * WebApi拦截器，只拦截/api/pad/**
 *
 * @author Zhang Bo
 * @date 2019/3/8.
 */
@Slf4j
public class WebApiInterceptor extends HandlerInterceptorAdapter {

    private static final String ACCESS_KEY = "LH6AB08F8G7324H6GBC42D3OC72GLJ25";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        if (!Constant.PRO_ENVIRONMENT) {
            log.info("water---WebApiInterceptor---requestURI=" + requestURI);
        }
        if (requestURI.startsWith("/api/pad/")) {
            String accessKey = request.getHeader("accessKey");
            if (StringUtil.isEmpty(accessKey)) {
                accessKey = request.getParameter("accessKey");
            }
            if (!Constant.PRO_ENVIRONMENT) {
                log.info("water---WebApiInterceptor---accessKey=" + accessKey);
            }
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
