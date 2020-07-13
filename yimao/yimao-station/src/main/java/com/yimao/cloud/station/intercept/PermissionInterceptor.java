package com.yimao.cloud.station.intercept;


import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.handler.JWTHandler;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.station.StationAdminCacheDTO;
import com.yimao.cloud.pojo.dto.station.StationPermissionCacheDTO;
import com.yimao.cloud.station.service.StationAdminService;
import com.yimao.cloud.station.service.StationPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * 站务系统权限拦截器
 *
 * @author yaoweijun
 * @date 2019/12/23.
 */
@Slf4j
public class PermissionInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private RedisCache redisCache;
    @Resource
    private UserCache userCache;
    @Resource
    private JWTHandler jwtHandler;
    @Resource
    private StationAdminService stationAdminService;
    @Resource
    private StationPermissionService stationPermissionService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        //1-获取当前访问接口的信息
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequestMapping annotation = handlerMethod.getMethodAnnotation(RequestMapping.class);
        if (annotation == null) {
            return true;
        }
        String method = annotation.method()[0].name();
        String code = annotation.value()[0];
        log.info("method={}", method);
        log.info("code={}", code);
        //2-登录放行,通用接口放行
        if (method.equalsIgnoreCase("get") && code.equals("login")) {
            return true;
        }
        //2-短信登录放行
        if (method.equalsIgnoreCase("get") && code.startsWith("phoneMessageLogin")) {
            return true;
        }

        //2-短信发送放行
        if (method.equalsIgnoreCase("get") && code.startsWith("/common/sendCode")) {
            return true;
        }

        try {
            JWTInfo jwtInfo = null;
            try {
                jwtInfo = userCache.getJWTInfo();
                log.info("jwtInfo={}", JSON.toJSONString(jwtInfo));
            } catch (Exception e) {
                log.error("系统权限校验，获取JWTInfo发生异常。");
                throw new YimaoException("操作异常");
            }

            //3-如果是从后台请求的要放行
            if (Objects.equals(jwtInfo.getType(), SystemType.SYSTEM.value)) {
                return true;
            }
            //4-如果是服务站请求，校验员工token是否过期
            if (Objects.equals(jwtInfo.getType(), SystemType.STATION.value)) {
                String stationAdmintoken = jwtHandler.getStationKey(jwtInfo);

                if (redisCache.hasKey(stationAdmintoken)) {

                    long millis = (long) redisCache.hget(stationAdmintoken, "createTime");
                    log.info("用户缓存创建时间={}", millis);
                    long expire = redisCache.getExpire(stationAdmintoken);
                    log.info("expire={}秒", expire);
                    log.info("用户缓存到期时间={}", millis + expire * 1000);
                    long restMillis = (millis + expire * 1000) - System.currentTimeMillis();
                    //小于五分钟更新缓存
                    if (restMillis <= 5 * 60 * 1000) {
                        log.info("更新缓存");
                        stationAdminService.createStationAdminCache(jwtInfo.getId());
                    }

                } else {
                    //创建员工缓存
                    stationAdminService.createStationAdminCache(jwtInfo.getId());

                }

                //5-校验接口是否添加了权限控制，如果添加了权限说明该接口需要访问权限，否则不需要直接允许访问
                boolean exist = stationPermissionService.existPermission(method, code);
                if (!exist) {
                    return true;
                }
                //6-获取用户角色上的权限列表
                List<StationPermissionCacheDTO> permissionList = userCache.getStationUserPermissionList();

                //获取用户拥有售前售后属性
            	StationAdminCacheDTO admin = userCache.getStationUserInfo();
            	log.info("adminCache={}",JSON.toJSONString(admin));
                
                if (permissionList.isEmpty()) {
                    log.error("员工操作权限为空");

                } else {
                    log.info("员工操作权限={}", JSON.toJSONString(permissionList));
                    for (StationPermissionCacheDTO permission : permissionList) {
                        // 如果用户权限列表存在该接口的权限，则校验通过，允许访问
                        if (permission.getMethod().equalsIgnoreCase(method) && permission.getUrl().equalsIgnoreCase(code)) {
                        	
                        	if(PermissionTypeEnum.ALL.value == permission.getType()) {
                        		return true;
                        	}
                        	
                        	
                        	if(PermissionTypeEnum.ALL.value == admin.getType()) {
                        		return true;
                        	}
                        	
                        	if(permission.getType().equals(admin.getType())) {
                        		return true;
                        	}
                        	                       	
                     
                        	log.error("访问失败,用户售前售后属性为{},当前接口售前售后属性为{}",admin.getType(),permission.getType());                      	
                        
                        }

                    }
                    log.error("员工没有操作权限");
                }

            }

        } catch (Exception e) {
            if(e instanceof YimaoException) {
            	YimaoException yimaoException=(YimaoException) e;
            	throw yimaoException;
            }else { 
            	log.error("拦截器异常={}",e);
            	throw new YimaoException("操作异常");
            }
            

        }


        //校验失败，拒绝访问
        throw new YimaoException("您还没有此操作权限，如需开通，请联系管理员。");
    }


}
