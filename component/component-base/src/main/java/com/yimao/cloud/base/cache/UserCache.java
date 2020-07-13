package com.yimao.cloud.base.cache;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.exception.NoLoginException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.handler.JWTHandler;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.station.StationAdminAreasCacheDTO;
import com.yimao.cloud.pojo.dto.station.StationAdminCacheDTO;
import com.yimao.cloud.pojo.dto.station.StationPermissionCacheDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 用户相关信息缓存类
 *
 * @author Zhang Bo
 * @date 2018/7/10.
 */
@Component
@Slf4j
public class UserCache {

    @Resource
    private RedisCache redisCache;
    @Resource
    private JWTHandler jwtHandler;

    /**
     * 从请求中获取JWTInfo
     */
    public String getJWTInfoStr() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        // 从请求header中获取用户权限认证信息
        String jwtInfoStr = request.getHeader(AuthConstants.JWTINFO);
        if (StringUtil.isBlank(jwtInfoStr)) {
            throw new NoLoginException();
        }
        return jwtInfoStr;
    }

    /**
     * 从请求中获取JWTInfo
     */
    public JWTInfo getJWTInfo() {
        try {
            String jwtInfoStr = URLDecoder.decode(this.getJWTInfoStr(), "UTF-8");
            return JSONObject.parseObject(jwtInfoStr, JWTInfo.class);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("操作失败。");
        }
    }

    /**
     * 获取当前登录的管理员的姓名
     */
    public String getCurrentAdminRealName() {
        JWTInfo jwtInfo = getJWTInfo();
        if (jwtInfo != null) {
            return jwtInfo.getRealName();
        }
        throw new NoLoginException();
    }

    /**
     * 获取当前登录的用户ID
     */
    public Integer getUserId() {
        JWTInfo jwtInfo = getJWTInfo();
        if (jwtInfo != null) {
            return jwtInfo.getId();
        }
        throw new NoLoginException();
    }

    // /**
    //  * 获取当前登录的用户信息【翼猫健康e家】【健康风险评估】【小猫店】
    //  */
    // public UserDTO getCurrentUser() {
    //     UserDTO userDTO = redisCache.get(Constant.USER_CACHE + this.getUserId(), UserDTO.class);
    //     if (Objects.isNull(userDTO)) {
    //         throw new NoLoginException();
    //     }
    //     return userDTO;
    // }

    // /**
    //  * 获取当前登录的经销商信息【经销商管理APP】
    //  */
    // public DistributorDTO getCurrentDistributor() {
    //     log.info("userId===" + this.getUserId());
    //     DistributorDTO distributorDTO = redisCache.get(Constant.DISTRIBUTOR_CACHE + this.getUserId(), DistributorDTO.class);
    //     if (Objects.isNull(distributorDTO)) {
    //         throw new NoLoginException();
    //     }
    //     return distributorDTO;
    // }

    // /**
    //  * 获取当前登录的管理员信息【总部业务管理系统】
    //  */
    // public AdminDTO getCurrentAdmin() {
    //     AdminDTO adminDTO = redisCache.get(Constant.ADMIN_CACHE + this.getUserId(), AdminDTO.class);
    //     if (Objects.isNull(adminDTO)) {
    //         throw new NoLoginException();
    //     }
    //     return adminDTO;
    // }

    /**
     * 获取当前登录的安装工的ID【交互攻坚】
     */
    public Integer getCurrentEngineerId() {
	    JWTInfo jwtInfo = getJWTInfo();
	    if (jwtInfo != null) {
		    return jwtInfo.getId();
	    }
        /*ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        // 从请求header中获取用户权限认证信息
        String token = request.getParameter("token");
        try {
            JWTInfo info = jwtHandler.getJWTInfoFromToken(token);
            if (info != null) {
                return info.getId();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }*/
        return null;
    }
    
    /**
     * 获取服务站员工信息
     */
    public StationAdminCacheDTO getStationUserInfo() {
    	JWTInfo jwt=getJWTInfo();
    	return JSONObject.parseObject((String)redisCache.hget(jwtHandler.getStationKey(jwt), "userInfo"),StationAdminCacheDTO.class);
    }
    
    /**
     * 获取服务站员工菜单权限列表
     */
    public List<StationPermissionCacheDTO> getStationUserPermissionList() {
    	JWTInfo jwt=getJWTInfo();
    	return JSONObject.parseArray((String)redisCache.hget(jwtHandler.getStationKey(jwt), "permissionList"),StationPermissionCacheDTO.class);
    }
    
    /**
     * 获取服务站员工区域权限列表
     * type 0-所属服务站集合   1-服务区域集合
     * serviceType 0-售前+售后；1-售前 ； 2-售后'
     */
    public Set<Integer> getStationUserAreas(Integer type,Integer serviceType) {
    	JWTInfo jwt=getJWTInfo();
    	StationAdminAreasCacheDTO areasCache=JSONObject.parseObject((String)redisCache.hget(jwtHandler.getStationKey(jwt), "areas"),StationAdminAreasCacheDTO.class);
    	//判断售前售后
    	if(type == 0) {
    		return areasCache.getStationIds();
    	}else if(type == 1) {
    		if(Objects.isNull(serviceType)) {
    			throw new YimaoException("获取区域集合未指定售前售后类型");
    		}
    		
    		if(PermissionTypeEnum.ALL.value == serviceType) {
    			Set<Integer> all=new HashSet<>();
    			all.addAll(areasCache.getPreAreaIds());
    			all.addAll(areasCache.getAfterAreaIds());
    			return all;
    		}else if(PermissionTypeEnum.PRE_SALE.value == serviceType) {
    			return areasCache.getPreAreaIds();
    		}else if(PermissionTypeEnum.AFTER_SALE.value == serviceType) {
    			return areasCache.getAfterAreaIds();
    		}

    	}
    	
    	return null;
    
    }

}