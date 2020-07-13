package com.yimao.cloud.system.interceptor;

import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.user.PermissionCacheDTO;
import com.yimao.cloud.system.feign.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 业务管理系统权限拦截器
 *
 * @author Zhang Bo
 * @date 2018/8/8.
 */
@Slf4j
public class PermissionInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private RedisCache redisCache;
    @Resource
    private UserCache userCache;
    @Resource
    private UserFeign userFeign;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1-获取所有权限列表
        List<PermissionCacheDTO> permissions = redisCache.getCacheList(Constant.PERMISSIONS + SystemType.SYSTEM.value, PermissionCacheDTO.class);
        if (CollectionUtil.isEmpty(permissions)) {
            permissions = userFeign.listPermissionBySysType(SystemType.SYSTEM.value);
            if (CollectionUtil.isEmpty(permissions)) {
                return true;
            }
            redisCache.setCacheList(Constant.PERMISSIONS + SystemType.SYSTEM.value, permissions, PermissionCacheDTO.class);
        }
        //2-获取当前接口的信息
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequestMapping annotation = handlerMethod.getMethodAnnotation(RequestMapping.class);
        if (annotation == null) {
            return true;
        }
        String method = annotation.method()[0].name();
        String code = annotation.value()[0];

        //3-校验接口是否添加了权限控制，如果添加了权限说明该接口需要访问权限，否则不需要直接允许访问
        if (!this.permissionRequired(permissions, method, code)) {
            return true;
        }
        //4-如果不是从后台请求的要放行
        try {
            JWTInfo jwtInfo = userCache.getJWTInfo();
            if (jwtInfo != null && !Objects.equals(jwtInfo.getType(), SystemType.SYSTEM.value)) {
                return true;
            }
        } catch (Exception e) {
            log.error("系统权限校验，获取JWTInfo发生异常。");
            return true;
        }
        //5-权限校验
        Integer adminId = userCache.getUserId();
        // 获取用户的权限列表
        Set<PermissionCacheDTO> cachePermissions = userFeign.listPermissionsByAdminId(adminId);
        if (CollectionUtil.isEmpty(cachePermissions)) {
            throw new BadRequestException("您还没有此操作权限，如需开通，请联系管理员。");
        }
        for (PermissionCacheDTO permission : cachePermissions) {
            // 如果用户权限列表存在该接口的权限，则校验通过，允许访问
            if (permission.getMethod().equalsIgnoreCase(method) && permission.getCode().equalsIgnoreCase(code)) {
                return true;
            }
        }
        //校验失败，拒绝访问
        throw new BadRequestException("您还没有此操作权限，如需开通，请联系管理员。");
    }

    private boolean permissionRequired(List<PermissionCacheDTO> permissions, String method, String code) {
        return permissions.stream().anyMatch(dto -> dto.getMethod().equalsIgnoreCase(method) && dto.getCode().equalsIgnoreCase(code));
    }

}
