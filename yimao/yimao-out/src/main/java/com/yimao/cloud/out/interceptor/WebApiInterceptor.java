package com.yimao.cloud.out.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.handler.JWTHandler;
import com.yimao.cloud.out.enums.ApiStatusCode;
import com.yimao.cloud.base.baideApi.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * WebApi拦截器
 *
 * @author Zhang Bo
 * @date 2019/3/8.
 */
@Slf4j
public class WebApiInterceptor extends HandlerInterceptorAdapter {
    @Resource
    private JWTHandler jwtHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        System.out.println("requestURI=" + requestURI);
        if (requestURI.contains("·") || requestURI.contains("/webapi/getToken") || requestURI.contains("/api/customer/change")) {//此接口为安装工登录接口，不拦截
            return true;
        } else {
            String token = request.getParameter("token");
            try {
                JWTInfo info = jwtHandler.getJWTInfoFromToken(token);
                if (info != null) {
                    request.setAttribute("JWTInfo_ID", info.getId());
                    return true;
                }
            } catch (Exception e) {
                log.error("OUT---WebApiInterceptor---" + requestURI);
                log.error(e.getMessage(), e);
                this.returnErrpr(ApiStatusCode.WEBAPI_TOKEN_ERROR.getTextZh(), ApiStatusCode.WEBAPI_TOKEN_ERROR.getCode(), response);
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        String token = request.getParameter("token");
        if (StringUtil.isNotEmpty(token) && modelAndView != null) {
            modelAndView.getModelMap().addAttribute("token", token);
        }
    }

    private void returnErrpr(String info, String code, HttpServletResponse response) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", info);
        json.put("success", false);
        try {
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(json.toString());
        } catch (Exception e) {
        }
    }

}
