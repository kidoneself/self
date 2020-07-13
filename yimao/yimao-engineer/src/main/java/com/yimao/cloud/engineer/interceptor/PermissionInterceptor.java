//package com.yimao.cloud.engineer.interceptor;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import com.alibaba.fastjson.JSONObject;
//import com.yimao.cloud.base.auth.JWTInfo;
//import com.yimao.cloud.base.handler.JWTHandler;
//import com.yimao.cloud.engineer.enums.ApiStatusCode;
//import lombok.extern.slf4j.Slf4j;
//
///***
// * 安装工app拦截器
// *
// * @author zhangbaobao
// *
// */
//@Slf4j
//public class PermissionInterceptor extends HandlerInterceptorAdapter {
//
//	@Resource
//	private JWTHandler jwtHandler;
//
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
//		String requestURI = request.getRequestURI();
//		log.info("===================requestURI=" + requestURI);
//
//		//登录接口放行
//		if (requestURI.contains("/login")) {
//			return true;
//		}
//
//		//发送验证码
//		if (requestURI.contains("/smscode")) {
//			return true;
//		}
//
//		//校验验证码
//		if (requestURI.contains("/validate/code")) {
//			return true;
//		}
//
//		//校验手机号是否绑定
//		if (requestURI.contains("/engineer/phone/bind")) {
//			return true;
//		}
//
//		//找回密码
//		if (requestURI.contains("/engineer/find/pwd")) {
//			return true;
//		}
//
//		//云签
//		if (requestURI.startsWith("/api/yunsign")) {
//            return true;
//        }
//
//		//经销商合同云签回调
//		if (requestURI.contains("/api/distributor/quickOnline/backCall")) {
//			return true;
//		}
//
//		String token = request.getParameter("token");
//		try {
//			JWTInfo info = jwtHandler.getJWTInfoFromToken(token);
//			if (info != null) {
//				request.setAttribute("JWTInfo_ID", info.getId());
//				return true;
//			}
//		} catch (Exception e) {
//			log.error("engineer---PermissionInterceptor---" + requestURI);
//			log.error(e.getMessage(), e);
//			this.returnErrpr(ApiStatusCode.WEBAPI_TOKEN_ERROR.getTextZh(),ApiStatusCode.WEBAPI_TOKEN_ERROR.getCode(), response);
//			return false;
//		}
//
//		return true;
//
//	}
//
//	private void returnErrpr(String info, String code, HttpServletResponse response) {
//		JSONObject json = new JSONObject();
//		json.put("code", code);
//		json.put("msg", info);
//		json.put("success", false);
//		try {
//			response.setContentType("application/json; charset=UTF-8");
//			response.setCharacterEncoding("UTF-8");
//			response.getWriter().print(json.toString());
//		} catch (Exception e) {
//		}
//	}
//
//}
