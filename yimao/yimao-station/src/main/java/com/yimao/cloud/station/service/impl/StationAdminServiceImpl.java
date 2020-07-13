package com.yimao.cloud.station.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.SexType;
import com.yimao.cloud.base.enums.StationAdminOSTypeEnum;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.handler.JWTHandler;
import com.yimao.cloud.base.properties.AuthProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.IpUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.station.*;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyServiceAreaDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;
import com.yimao.cloud.pojo.query.station.StationAdminQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.StationAdminLoginVO;
import com.yimao.cloud.pojo.vo.station.StationCompanyVO;
import com.yimao.cloud.station.constant.StationConstant;
import com.yimao.cloud.station.feign.SystemFeign;
import com.yimao.cloud.station.mapper.StationAdminLogMapper;
import com.yimao.cloud.station.mapper.StationAdminMapper;
import com.yimao.cloud.station.po.StationAdmin;
import com.yimao.cloud.station.po.StationAdminLog;
import com.yimao.cloud.station.po.SystemRole;
import com.yimao.cloud.station.service.StationAdminService;
import com.yimao.cloud.station.service.StationRoleService;
import com.yimao.cloud.station.util.CheckUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Slf4j
public class StationAdminServiceImpl implements StationAdminService {

    @Resource
    private StationAdminMapper stationAdminMapper;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserCache userCache;
    @Resource
    private RedisCache redisCache;
    @Resource
    private StationRoleService stationRoleService;
    @Resource
    private JWTHandler jwtHandler;
    @Resource
    private AuthProperties authProperties;
    @Resource
    private StationAdminLogMapper stationAdminLogMapper;

    /**
     * 员工登录(账号密码)
     */
    @Override
    public StationAdminLoginVO login(String userName, String password, HttpServletRequest request) {

        StationAdmin admin = stationAdminMapper.selectStationAdmin(userName);

        //用户名校验
        if (admin == null) {
            throw new YimaoException("该账号不存在。");
        }
        //禁用校验
        if (admin.getStatus()) {
            throw new YimaoException("该账号已被禁用。");
        }
        //密码校验
        if (!admin.getPassword().equalsIgnoreCase(password)) {
            throw new YimaoException("密码错误。");
        }
        
        Integer serviceType=null;
        if(admin.getStationCompanyId() == StationConstant.SUPERCOMPANYID) {
        	//超管
        	serviceType=PermissionTypeEnum.ALL.value;	
        }else {
        	//设置根据服务站公司的售前售后属性设置该用户售前售后状态
            StationCompanyDTO stationCompany  =systemFeign.getStationCompanyById(admin.getStationCompanyId());
            if(Objects.isNull(stationCompany)) {
            	throw new YimaoException("该账号服务站公司不存在。");
            }else {
            	List<StationCompanyServiceAreaDTO> companyServiceAreas=stationCompany.getServiceAreas();
            	if(CollectionUtil.isEmpty(companyServiceAreas)) {
            		throw new YimaoException("该账号服务站公司无售前售后权限。");
            	}else {
            		for (StationCompanyServiceAreaDTO stationCompanyServiceAreaDTO : companyServiceAreas) {
            			if(Objects.isNull(serviceType)) {
            				serviceType=stationCompanyServiceAreaDTO.getServiceType();
            				continue;
            			}
            			
            			if(serviceType == PermissionTypeEnum.ALL.value) {
            				break;
            			}else if(serviceType == stationCompanyServiceAreaDTO.getServiceType()) {
            				continue;	        				
            			}else{
            				serviceType = PermissionTypeEnum.ALL.value;	        				
            			}
            			
    				}
            	}
            	
            }
        }
     

        log.info("用户登录售前售后serviceType={}",serviceType);
        
        JWTInfo jwtInfo = new JWTInfo();
        jwtInfo.setId(admin.getId());
        jwtInfo.setRealName(admin.getRealName());
        jwtInfo.setType(SystemType.STATION.value);
        jwtInfo.setStationCompanyId(admin.getStationCompanyId());

        String token = jwtHandler.createJWTToken(jwtInfo);

        //查询用户菜单权限
        List<StationMenuDTO> menuList = stationRoleService.getMenuListByRoleId(admin.getRoleId(),serviceType);

        //查询用户的接口权限
        List<StationPermissionCacheDTO> permissionList = stationRoleService.findPermissionsByRoleId(admin.getRoleId());

        //查询用户的区域权限
        StationAdminAreasCacheDTO areasCache = stationRoleService.findStationsAndAreasByAdminId(admin.getId());

        StationAdminCacheDTO adminCache = new StationAdminCacheDTO();
        
        BeanUtils.copyProperties(admin, adminCache);
        //设置该用户售前售后状态
        adminCache.setType(serviceType);
        
        //添加缓存
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("createTime", System.currentTimeMillis());
        map.put("areas", JSON.toJSONString(areasCache));
        map.put("permissionList", JSON.toJSONString(permissionList));
        map.put("userInfo", JSON.toJSONString(adminCache));

        redisCache.hmset(getStationKey(admin), map, authProperties.getExpire2());
        try {
            // 记录登录日志
            StationAdminLog log = new StationAdminLog();
            log.setUserName(admin.getUserName());
            log.setRoleId(admin.getRoleId());
            log.setRealName(admin.getRealName());
            log.setAdminId(admin.getId());
            log.setOsType(StationAdminOSTypeEnum.LOGIN.value);
            log.setTime(new Date());
            log.setIp(IpUtil.getIp(request));
            stationAdminLogMapper.insertSelective(log);
        } catch (Exception e) {
            log.error("站务系统登录日志新增异常");
        }

        return new StationAdminLoginVO(admin.getRealName(), admin.getStationCompanyId(), admin.getStationCompanyName(), token, menuList);
    }

    /**
	 * 员工登录(手机验证码)
	 */
	public StationAdminLoginVO phoneMessageLogin(String phone, Integer code, HttpServletRequest request) {

			//查询当前手机号发送的短信
			Integer sendCode=(Integer) redisCache.hget(StationConstant.STATION_PHONETOKEN+phone, "code");

			 //校验验证码
	        if(Objects.isNull(sendCode)) {
	        	throw new YimaoException("短信未发送或失效。");
	        }

	        //验证码验证
	        if(! code.equals(sendCode)) {
	        	throw new YimaoException("短信码错误。");
	        }

		 	StationAdmin admin = stationAdminMapper.selectStationAdminByPhone(phone);

	        //用户名校验
	        if (admin == null) {
	            throw new YimaoException("该账号不存在。");
	        }
	        //禁用校验
	        if (admin.getStatus()) {
	            throw new YimaoException("该账号已被禁用。");
	        }
	        
	        Integer serviceType=null;

	        if(admin.getStationCompanyId() == StationConstant.SUPERCOMPANYID) {
	        	//超管
	        	serviceType=PermissionTypeEnum.ALL.value;	
	        }else {
	        	//设置根据服务站公司的售前售后属性设置该用户售前售后状态
		        StationCompanyDTO stationCompany  =systemFeign.getStationCompanyById(admin.getStationCompanyId());
		        if(Objects.isNull(stationCompany)) {
		        	throw new YimaoException("该账号服务站公司不存在。");
		        }else {
		        	List<StationCompanyServiceAreaDTO> companyServiceAreas=stationCompany.getServiceAreas();
		        	if(CollectionUtil.isEmpty(companyServiceAreas)) {
		        		throw new YimaoException("该账号服务站公司无售前售后权限。");
		        	}else {
		        		for (StationCompanyServiceAreaDTO stationCompanyServiceAreaDTO : companyServiceAreas) {
		        			if(Objects.isNull(serviceType)) {
		        				serviceType=stationCompanyServiceAreaDTO.getServiceType();
		        				continue;
		        			}
		        			
		        			if(serviceType == PermissionTypeEnum.ALL.value) {
		        				break;
		        			}else if(serviceType == stationCompanyServiceAreaDTO.getServiceType()) {
		        				continue;	        				
		        			}else{
		        				serviceType = PermissionTypeEnum.ALL.value;	        				
		        			}
		        			
						}
		        	}
		        	
		        }
	        }
	        

	        log.info("用户登录售前售后serviceType={}",serviceType);
	        
	        JWTInfo jwtInfo = new JWTInfo();
	        jwtInfo.setId(admin.getId());
	        jwtInfo.setRealName(admin.getRealName());
	        jwtInfo.setType(SystemType.STATION.value);
	        jwtInfo.setStationCompanyId(admin.getStationCompanyId());

	        String token = jwtHandler.createJWTToken(jwtInfo);

	        //查询用户菜单权限
	        List<StationMenuDTO> menuList = stationRoleService.getMenuListByRoleId(admin.getRoleId(),serviceType);

	        //查询用户的接口权限
	        List<StationPermissionCacheDTO> permissionList = stationRoleService.findPermissionsByRoleId(admin.getRoleId());

	        //查询用户的区域权限
	        StationAdminAreasCacheDTO areasCache = stationRoleService.findStationsAndAreasByAdminId(admin.getId());

	        StationAdminCacheDTO adminCache = new StationAdminCacheDTO();

	        BeanUtils.copyProperties(admin, adminCache);
		      
	        adminCache.setType(serviceType);
	        //添加缓存
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("createTime", System.currentTimeMillis());
	        map.put("areas", JSON.toJSONString(areasCache));
	        map.put("permissionList", JSON.toJSONString(permissionList));
	        map.put("userInfo", JSON.toJSONString(adminCache));

	        redisCache.hmset(getStationKey(admin), map, authProperties.getExpire2());
	        try {
	            // 记录登录日志
	            StationAdminLog log = new StationAdminLog();
	            log.setUserName(admin.getUserName());
	            log.setRoleId(admin.getRoleId());
	            log.setRealName(admin.getRealName());
	            log.setAdminId(admin.getId());
	            log.setOsType(StationAdminOSTypeEnum.LOGIN.value);
	            log.setTime(new Date());
	            log.setIp(IpUtil.getIp(request));
	            stationAdminLogMapper.insertSelective(log);
	        } catch (Exception e) {
	            log.error("站务系统登录日志新增异常");
	        }

	        return new StationAdminLoginVO(admin.getRealName(), admin.getStationCompanyId(), admin.getStationCompanyName(), token, menuList);
	}


    /**
     * 创建员工
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void save(StationAdminDTO stationAdmin) {
        //查询当前调用改接口用户的服务站id
        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();

        if (stationCompanyId == null) {
            throw new YimaoException("用户登录失效。");
        } else if (stationCompanyId.equals(StationConstant.SUPERCOMPANYID)) {
            //该用户是超级管理员
            if (Objects.isNull(stationAdmin.getStationCompanyId())) {
                throw new BadRequestException("创建用户的服务站公司为空。");
            }

        } else {
            stationAdmin.setStationCompanyId(stationCompanyId);
        }

        //校验用户数据
        checkParams(stationAdmin, 1);
        //用户名和手机号同步
        stationAdmin.setUserName(stationAdmin.getPhone());

        StationAdmin admin = new StationAdmin();

        BeanUtils.copyProperties(stationAdmin, admin);
        //去除空格
        admin.setUserName(admin.getUserName().trim());
        admin.setCreateTime(new Date());
        admin.setCreator(userCache.getUserId());

        log.info("创建员工={}", JSON.toJSONString(admin));

        int res = stationAdminMapper.insertSelective(admin);

        if (res < 1) {
            throw new YimaoException("操作失败。");
        }
        log.info("员工id={}", admin.getId());
        //保存员工可查询区域权限
        saveSystemRoles(admin, stationAdmin.getStations());


    }

    /**
     * 编辑员工
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void update(StationAdminDTO stationAdmin) {
        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();

        if (stationCompanyId == null) {
            throw new BadRequestException("用户登录失效。");
        } else if (stationCompanyId.equals(StationConstant.SUPERCOMPANYID)) {
            //该用户是超级管理员
            if (Objects.isNull(stationAdmin.getStationCompanyId())) {
                throw new BadRequestException("编辑用户的服务站公司为空。");
            }

        } else {
            //非超级管理员默认编辑用户的服务站公司为编辑人所在服务站公司
            stationAdmin.setStationCompanyId(stationCompanyId);
        }

        if (Objects.isNull(stationAdmin.getId())) {
            throw new BadRequestException("员工id为空。");
        }

        //校验用户数据
        checkParams(stationAdmin, 2);
        //用户名与手机号同步
        stationAdmin.setUserName(stationAdmin.getPhone());

        //查询未更新前的员工数据
        StationAdmin origin = stationAdminMapper.selectByPrimaryKey(stationAdmin.getId());

        if (Objects.isNull(origin)) {
            throw new YimaoException("用户不存在。");
        }

        if (!origin.getStationCompanyId().equals(stationAdmin.getStationCompanyId())) {
            throw new BadRequestException("用户不属于该服务站公司。");
        }

        if (origin.getStatus()) {
            throw new YimaoException("用户已禁用。");
        }
        //更新员工基本数据
        StationAdmin updateAdmin = new StationAdmin();
        BeanUtils.copyProperties(stationAdmin, updateAdmin);
        updateAdmin.setUpdateTime(new Date());
        updateAdmin.setUpdater(userCache.getUserId());
        log.info("编辑用户信息={}", JSON.toJSONString(updateAdmin));

        stationAdminMapper.updateByPrimaryKeySelective(updateAdmin);

        //更新员工区域数据
        saveSystemRoles(updateAdmin, stationAdmin.getStations());
        //更新员工区域缓存
        updateStationAdminAreasCache(updateAdmin);

        //更新员工权限列表
        if (!origin.getRoleId().equals(stationAdmin.getRoleId())) {
            updateStationAdminPermissionCache(stationAdmin.getId(), stationAdmin.getStationCompanyId(), stationAdmin.getRoleId());
        }

        //更新员工信息缓存
        updateStationAdminInfoCache(origin, updateAdmin);

    }


    /**
     * 查询员工列表
     */
    @Override
    public PageVO<StationAdminDTO> page(Integer pageNum, Integer pageSize, StationAdminQuery query) {
        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();
        if (stationCompanyId == null) {
            throw new YimaoException("服务站公司id为空");
        } else if (stationCompanyId.equals(StationConstant.SUPERCOMPANYID)) {
            //查询所有用户
            PageHelper.startPage(pageNum, pageSize);
            Page<StationAdminDTO> page = stationAdminMapper.listAdminByQuery(query);
            return new PageVO<>(pageNum, page);

        } else {

            query.setStationCompanyId(stationCompanyId);
            //查询改查询用户所属服务站集合
            Set<Integer> stationList = userCache.getStationUserAreas(0,null);

            if (CollectionUtil.isEmpty(stationList)) {
                return null;
            }

            query.setStationList(stationList);

            // 分页查询
            PageHelper.startPage(pageNum, pageSize);
            Page<StationAdminDTO> page = stationAdminMapper.listAdminByQuery(query);
            return new PageVO<>(pageNum, page);
        }

    }

    /**
     * 删除员工账号
     */
    @Override
    public void delete(Integer id) {
        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();

        if (stationCompanyId == null) {
            throw new BadRequestException("用户登录失效。");
        }

        if (Objects.isNull(id)) {
            throw new BadRequestException("用户id为空。");
        }

        StationAdmin origin = stationAdminMapper.selectByPrimaryKey(id);

        if (Objects.isNull(origin)) {
            throw new BadRequestException("用户不存在。");
        }

        if (origin.getStationCompanyId().equals(StationConstant.SUPERCOMPANYID)) {
            throw new YimaoException("超级用户无法删除。");
        }

        if (!stationCompanyId.equals(StationConstant.SUPERCOMPANYID) && !stationCompanyId.equals(origin.getStationCompanyId())) {
            throw new YimaoException("用户无法删除。");
        }

        //删除用户
        stationAdminMapper.deleteByPrimaryKey(id);
        //删除区域
        stationRoleService.deleteByAdminId(id);
        //删除该用户缓存
        String token = getStationKey(origin);
        if (redisCache.hasKey(token)) {
            redisCache.delete(token);
        }

    }

    /**
     * 禁用/启用用户账号
     */
    @Override
    public void forbidden(Integer id) {
        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();

        if (stationCompanyId == null) {
            throw new BadRequestException("用户登录失效。");
        }

        if (Objects.isNull(id)) {
            throw new BadRequestException("用户id为空。");
        }

        StationAdmin origin = stationAdminMapper.selectByPrimaryKey(id);

        if (Objects.isNull(origin)) {
            throw new BadRequestException("用户不存在。");
        }

        if (origin.getStationCompanyId().equals(StationConstant.SUPERCOMPANYID)) {
            throw new YimaoException("超级用户无法操作。");
        }

        if (!stationCompanyId.equals(StationConstant.SUPERCOMPANYID) && !stationCompanyId.equals(origin.getStationCompanyId())) {
            throw new YimaoException("用户无法操作。");
        }

        StationAdmin update = new StationAdmin();
        update.setId(id);
        update.setUpdateTime(new Date());
        update.setUpdater(userCache.getUserId());

        if (origin.getStatus()) {
            // 设置禁用状态为false
            update.setStatus(false);
        } else {
            // 设置禁用状态为true
            update.setStatus(true);
        }

        //删除用户
        stationAdminMapper.updateByPrimaryKeySelective(update);

        //删除该用户缓存
        String token = getStationKey(origin);
        if (redisCache.hasKey(token)) {
            redisCache.delete(token);
        }

    }


    private void saveSystemRoles(StationAdmin admin, List<StationAreasDTO> stations) {
        //删除旧的系统访问区域权限
        stationRoleService.deleteByAdminId(admin.getId());

        List<SystemRole> list = new ArrayList<>();

        for (StationAreasDTO stationArea : stations) {
            SystemRole systemRole = new SystemRole();
            systemRole.setAdminId(admin.getId());
            systemRole.setAreaIds(JSON.toJSONString(stationArea.getAreas()));
            systemRole.setStationId(stationArea.getStationId());
            systemRole.setCreator(userCache.getUserId());
            systemRole.setCreateTime(new Date());
            list.add(systemRole);
        }

        stationRoleService.batchInsert(list);

    }

    /**
     * 创建编辑服务站员工数据校验
     *
     * @param stationAdmin
     * @param type         1-新建 2-编辑
     */
    private void checkParams(StationAdminDTO stationAdmin, Integer type) {

//        if (StringUtils.isBlank(stationAdmin.getUserName())) {
//            throw new BadRequestException("用户名不能为空。");
//        }
//
//        if (type == 1) {
//            if (StringUtils.isBlank(stationAdmin.getPassword())) {
//                throw new BadRequestException("密码不能为空。");
//            }
//        }


        if (StringUtils.isBlank(stationAdmin.getRealName())) {
            throw new BadRequestException("姓名不能为空。");
        }

        if (stationAdmin.getSex() == null || SexType.find(stationAdmin.getSex()) == null) {
            throw new BadRequestException("性别填写错误。");
        }

        if (Objects.isNull(stationAdmin.getRoleId())) {
            throw new BadRequestException("用户角色不能为空。");
        }

        if (Objects.isNull(stationAdmin.getPhone())) {
            throw new BadRequestException("手机号不能为空。");
        }

        if(! CheckUtils.isMobile(stationAdmin.getPhone())) {
        	throw new BadRequestException("手机号格式错误。");
        }

        //查询改服务站公司下的用户名是否存在
//        int userNameCount = 0;
        //查询手机号是否存在
        int phoneCount = 0;

        if (type == 1) {
            //userNameCount = stationAdminMapper.selectUserNameCount(stationAdmin.getUserName(), null);
            phoneCount = stationAdminMapper.selectPhoneCount(stationAdmin.getPhone(), null);
        } else if (type == 2) {
            //userNameCount = stationAdminMapper.selectUserNameCount(stationAdmin.getUserName(), stationAdmin.getId());
            phoneCount = stationAdminMapper.selectPhoneCount(stationAdmin.getPhone(), stationAdmin.getId());
        }


//        if (userNameCount > 0) {
//            throw new BadRequestException("用户名已存在。");
//        }

        if (phoneCount > 0) {
            throw new BadRequestException("手机号码已存在。");
        }

        //判断该服务站是否存在该roleId
        boolean exist = stationRoleService.existRoleIdByStationCompanyId(stationAdmin.getRoleId(), stationAdmin.getStationCompanyId());

        if (!exist) {
            throw new BadRequestException("服务站不存在该角色。");
        }

        List<StationAreasDTO> StationAreaList = stationAdmin.getStations();

        if (Objects.isNull(StationAreaList) || StationAreaList.size() < 1) {
            throw new BadRequestException("用户未绑定服务站门店。");
        } else {

            List<Integer> stationIdList = new ArrayList<Integer>();

            for (StationAreasDTO stationAreasDTO : StationAreaList) {
                if (stationIdList.contains(stationAreasDTO.getStationId())) {
                    throw new BadRequestException("服务站门店id重复。");
                } else {
                    stationIdList.add(stationAreasDTO.getStationId());
                }

                //判断服务站是否存在
                StationDTO station = systemFeign.getStationById(stationAreasDTO.getStationId());

                if (Objects.isNull(station)) {
                    throw new BadRequestException("用户未选择服务站门店。");
                }

//				 if(Objects.isNull(stationAreasDTO.getAreas()) || stationAreasDTO.getAreas().size() < 1 ) {
//					 throw new BadRequestException("用户未选择服务站门店服务区域。");
//				 }
                //如果是超级管理员不进行校验
                if (!stationAdmin.getStationCompanyId().equals(StationConstant.SUPERCOMPANYID)) {
                    if (Objects.isNull(station.getStationCompanyIds()) || !station.getStationCompanyIds().contains(stationAdmin.getStationCompanyId())) {
                        throw new BadRequestException("用户所属服务站公司不存在该服务站门店。");
                    }
                }

            }
        }

    }

    /**
     * 获取服务站员工token
     */
    public String getStationKey(StationAdmin admin) {
        return AuthConstants.JWT_STATIONTOKEN_PREFIX + "_" + admin.getStationCompanyId() + "_" + admin.getId();
    }

    /**
     * 创建服务站员工缓存
     */
    public void createStationAdminCache(Integer adminId) {
        StationAdmin admin = stationAdminMapper.selectByPrimaryKey(adminId);

        //查询用户的接口权限
        List<StationPermissionCacheDTO> permissionList = stationRoleService.findPermissionsByRoleId(admin.getRoleId());

        //查询用户的区域权限
        StationAdminAreasCacheDTO areasCache = stationRoleService.findStationsAndAreasByAdminId(adminId);
        
        Integer serviceType=null;

        if(admin.getStationCompanyId() == StationConstant.SUPERCOMPANYID) {
        	//超管
        	serviceType=PermissionTypeEnum.ALL.value;	
        }else {
        	//设置根据服务站公司的售前售后属性设置该用户售前售后状态
	        StationCompanyDTO stationCompany  =systemFeign.getStationCompanyById(admin.getStationCompanyId());
	        if(Objects.isNull(stationCompany)) {
	        	throw new YimaoException("该账号服务站公司不存在。");
	        }else {
	        	List<StationCompanyServiceAreaDTO> companyServiceAreas=stationCompany.getServiceAreas();
	        	if(CollectionUtil.isEmpty(companyServiceAreas)) {
	        		throw new YimaoException("该账号服务站公司无售前售后权限。");
	        	}else {
	        		for (StationCompanyServiceAreaDTO stationCompanyServiceAreaDTO : companyServiceAreas) {
	        			if(Objects.isNull(serviceType)) {
	        				serviceType=stationCompanyServiceAreaDTO.getServiceType();
	        				continue;
	        			}
	        			
	        			if(serviceType == PermissionTypeEnum.ALL.value) {
	        				break;
	        			}else if(serviceType == stationCompanyServiceAreaDTO.getServiceType()) {
	        				continue;	        				
	        			}else{
	        				serviceType = PermissionTypeEnum.ALL.value;	        				
	        			}
	        			
					}
	        	}
	        	
	        }
        }
        

        StationAdminCacheDTO adminCache = new StationAdminCacheDTO();
        
        adminCache.setType(serviceType);

        BeanUtils.copyProperties(admin, adminCache);
        //添加缓存
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("areas", JSON.toJSONString(areasCache));
        map.put("permissionList", JSON.toJSONString(permissionList));
        map.put("userInfo", JSON.toJSONString(adminCache));

        redisCache.hmset(getStationKey(admin), map, authProperties.getExpire2());

    }

    /**
     * 员工角色权限变更，批修该改员工缓存
     *
     * @param adminId
     * @param stationCompanyId
     * @param roleId
     */
    public void updateStationAdminPermissionCache(Integer adminId, Integer stationCompanyId, Integer roleId) {
        if (Objects.isNull(adminId)) {
            throw new YimaoException("员工id为空");
        }

        if (Objects.isNull(roleId)) {
            throw new YimaoException("角色id为空");
        }


        if (Objects.isNull(stationCompanyId)) {
            throw new YimaoException("服务站公司id为空");
        }

        //查询当前角色对应的权限列表
        List<StationPermissionCacheDTO> permissionList = stationRoleService.findPermissionsByRoleId(roleId);

        StationAdmin stationAdmin = new StationAdmin();
        stationAdmin.setStationCompanyId(stationCompanyId);
        stationAdmin.setId(adminId);

        if (redisCache.hasKey(getStationKey(stationAdmin))) {
            //更新权限
            redisCache.hset(getStationKey(stationAdmin), "permissionList", JSON.toJSONString(permissionList));
        }

    }
    
    /**
     * 防止循环调用查询数据库，根据涉及角色的用户集合更新权限缓存
     * @param adminList
     */
    public void updateStationAdminPermissionCache(List<StationAdmin> adminList) {
    	  	
    	 if (CollectionUtil.isEmpty(adminList)) {
             return;
         }
    	 
         for (StationAdmin stationAdmin : adminList) {
             //判断是否存在缓存
             if (redisCache.hasKey(getStationKey(stationAdmin))) {            	
            	//查询当前角色对应的权限列表
             	List<StationPermissionCacheDTO> permissionList = stationRoleService.findPermissionsByRoleId(stationAdmin.getRoleId());             	
                //更新权限
                redisCache.hset(getStationKey(stationAdmin), "permissionList", JSON.toJSONString(permissionList));
             }
         }
    }

    /**
     * 角色权限变更，批量修改员工缓存
     */
    public void updateStationAdminPermissionCache(Integer roleId) {

        if (Objects.isNull(roleId)) {
            throw new YimaoException("角色id为空");
        }

        //查询当前角色对应的权限列表
        List<StationPermissionCacheDTO> permissionList = null;
        //查询当前角色对应的员工列表
        StationAdmin admin = new StationAdmin();
        admin.setRoleId(roleId);
        List<StationAdmin> adminList = stationAdminMapper.selectByQuery(admin);

        if (CollectionUtil.isEmpty(adminList)) {
            return;
        }

        for (StationAdmin stationAdmin : adminList) {
            //判断是否存在缓存
            if (redisCache.hasKey(getStationKey(stationAdmin))) {
            	
            	if(CollectionUtil.isEmpty(permissionList)) {
            		permissionList = stationRoleService.findPermissionsByRoleId(roleId);
            	}
                //更新权限
                redisCache.hset(getStationKey(stationAdmin), "permissionList", JSON.toJSONString(permissionList));
            }
        }
    }


    /**
     * 员工区域变更，修改员工区域缓存
     */
    public void updateStationAdminAreasCache(StationAdmin stationAdmin) {
        if (Objects.isNull(stationAdmin.getId())) {
            throw new YimaoException("员工id为空");
        }

        //根据员工id查询区域列表
        StationAdminAreasCacheDTO areasCache = stationRoleService.findStationsAndAreasByAdminId(stationAdmin.getId());

        if (redisCache.hasKey(getStationKey(stationAdmin))) {
            redisCache.hset(getStationKey(stationAdmin), "areas", JSON.toJSONString(areasCache));
        }

    }

    /**
     * 服务门店区域变更，批量修改员工区域缓存
     */
    public void updateStationAdminAreasCache(Integer stationId, List<StationServiceAreaDTO> areas) {

        if (Objects.isNull(stationId)) {
            throw new YimaoException("服务站id为空");
        }
        
        List<Integer> nowArea=new ArrayList<Integer>();
        
        if(CollectionUtil.isNotEmpty(areas)) {
        	for (StationServiceAreaDTO dto : areas) {
            	nowArea.add(dto.getAreaId());
    		}
        }
        
        
        List<SystemRoleDTO> systemRolelist = stationRoleService.findSystemRoleByStationId(stationId);

        if (CollectionUtil.isNotEmpty(systemRolelist)) {
            for (SystemRoleDTO systemRoleDTO : systemRolelist) {
                try {

                    String areaIds = systemRoleDTO.getAreaIds();
                    List<Integer> userAreas = JSON.parseArray(areaIds, Integer.class);
                    //用于全部转让区域缓存删除
                    List<Integer> userCacheAreas = JSON.parseArray(areaIds, Integer.class);

                    //判断区域对否删除
                    Iterator<Integer> iterator = userAreas.iterator();
                    while (iterator.hasNext()) {
                        Integer userArea = iterator.next();
                        if (!nowArea.contains(userArea)) {
                            iterator.remove();
                        }

                    }

                    //更新数据
                    SystemRole systemRole = new SystemRole();
                    systemRole.setId(systemRoleDTO.getSystemRoleId());
                    systemRole.setAreaIds(JSON.toJSONString(userAreas));
                    stationRoleService.updateSystemRole(systemRole);

                    //更新缓存
                    StationAdmin stationAdmin = new StationAdmin();
                    stationAdmin.setId(systemRoleDTO.getAdminId());
                    stationAdmin.setStationCompanyId(systemRoleDTO.getStationCompanyId());

                    if (redisCache.hasKey(getStationKey(stationAdmin))) {
                        //StationAdminAreasCacheDTO areasCache = stationRoleService.findStationsAndAreasByAdminId(stationAdmin.getId());
                        //编辑售前售后区域缓存       
                        StationAdminAreasCacheDTO areasCache =new StationAdminAreasCacheDTO();
                        
                        String areaJson = (String) redisCache.hget(getStationKey(stationAdmin), "areas");
                        
                        if(Objects.isNull(areaJson)) {
                        	continue;
                        }
                        
                        areasCache=JSONObject.parseObject(areaJson,StationAdminAreasCacheDTO.class);
                        Set<Integer> preAreas=areasCache.getPreAreaIds();
                        Set<Integer> afterAreas=areasCache.getAfterAreaIds();
                        
                        if(CollectionUtil.isEmpty(areas)) {
                        	afterAreas.removeAll(userCacheAreas);
							preAreas.removeAll(userCacheAreas);
                        }else {
                        	for (StationServiceAreaDTO stationServiceArea : areas) {
    							int areaId=stationServiceArea.getAreaId();
                            	int serviceType=stationServiceArea.getServiceType();
                            	log.info("areaId={},serviceType={}",areaId,serviceType);
                            	
    							if(serviceType == PermissionTypeEnum.PRE_SALE.value) {
    								preAreas.add(areaId);
    								afterAreas.remove(areaId);
    							}else if(serviceType == PermissionTypeEnum.AFTER_SALE.value) {
    								afterAreas.add(areaId);
    								preAreas.remove(areaId);
    							}
    						}
                        }
                        
                                            
                        Set<Integer> stations=areasCache.getStationIds();
                        areasCache.setPreAreaIds(preAreas);
                        areasCache.setAfterAreaIds(afterAreas);
                        areasCache.setStationIds(stations);
                        redisCache.hset(getStationKey(stationAdmin), "areas", JSON.toJSONString(areasCache));
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }


    }

    /**
     * stationAdmin为未更新员工对象数据
     * 员工信息变更修改员工信息缓存
     */
    public void updateStationAdminInfoCache(StationAdmin origStationAdmin, StationAdmin updateStationAdmin) {

        //查询原用户信息
        StationAdmin query = new StationAdmin();
        query.setId(updateStationAdmin.getId());
        List<StationAdmin> adminList = stationAdminMapper.selectByQuery(query);

        if (CollectionUtil.isEmpty(adminList)) {
            return;
        }

        //查询当前更新后的数据
        StationAdmin admin = adminList.get(0);

        boolean flag = false;
        //判断是否修改角色用户名姓名
        if (Objects.nonNull(origStationAdmin.getRoleId()) && !origStationAdmin.getRoleId().equals(admin.getRoleId())) {
            flag = true;
        }
        if (StringUtils.isNotBlank(origStationAdmin.getUserName()) && !origStationAdmin.getUserName().equals(admin.getUserName())) {

            flag = true;
        }
        if (StringUtils.isNotBlank(origStationAdmin.getRealName()) && !origStationAdmin.getRealName().equals(admin.getRealName())) {
            flag = true;
        }

        if (flag) {
            if (redisCache.hasKey(getStationKey(admin))) {
                //更新权限
                StationAdminCacheDTO adminCache = new StationAdminCacheDTO();

                BeanUtils.copyProperties(admin, adminCache);
                
                Integer serviceType=null;

                if(admin.getStationCompanyId() == StationConstant.SUPERCOMPANYID) {
                	//超管
                	serviceType=PermissionTypeEnum.ALL.value;	
                }else {
                	//设置根据服务站公司的售前售后属性设置该用户售前售后状态
        	        StationCompanyDTO stationCompany  =systemFeign.getStationCompanyById(admin.getStationCompanyId());
        	        if(Objects.isNull(stationCompany)) {
        	        	return;
        	        }else {
        	        	List<StationCompanyServiceAreaDTO> companyServiceAreas=stationCompany.getServiceAreas();
        	        	if(CollectionUtil.isEmpty(companyServiceAreas)) {
        	        		return;
        	        	}else {
        	        		for (StationCompanyServiceAreaDTO stationCompanyServiceAreaDTO : companyServiceAreas) {
        	        			if(Objects.isNull(serviceType)) {
        	        				serviceType=stationCompanyServiceAreaDTO.getServiceType();
        	        				continue;
        	        			}
        	        			
        	        			if(serviceType == PermissionTypeEnum.ALL.value) {
        	        				break;
        	        			}else if(serviceType == stationCompanyServiceAreaDTO.getServiceType()) {
        	        				continue;	        				
        	        			}else{
        	        				serviceType = PermissionTypeEnum.ALL.value;	        				
        	        			}
        	        			
        					}
        	        	}
        	        	
        	        }
                }
                
                adminCache.setType(serviceType);

                redisCache.hset(getStationKey(admin), "userInfo", JSON.toJSONString(adminCache));
            }
        }

    }

    @Override
    public List<StationCompanyVO> getAllStationCompany() {
        return stationAdminMapper.getAllStationCompany();

    }

    /**
     * 修改密码
     */
    @Override
    public void changePassword(String originPassword, String changePassword) {
        Integer adminId = userCache.getUserId();

        if (Objects.isNull(adminId)) {
            throw new YimaoException("用户登录错误，重新登录");
        }

        StationAdmin admin = stationAdminMapper.selectByPrimaryKey(adminId);

        if (Objects.isNull(admin)) {
            throw new YimaoException("用户不存在");
        }

        if (!admin.getPassword().equals(originPassword.trim())) {
            throw new YimaoException("原密码不正确");
        }

        StationAdmin update = new StationAdmin();
        update.setId(adminId);
        update.setPassword(changePassword);
        stationAdminMapper.updateByPrimaryKeySelective(update);

    }

	/**
	 * 短信登录发送验证码校验用户账号有效性
	 */
	public boolean checkStationAdminByPhone(String phone) {

		StationAdmin admin = stationAdminMapper.selectStationAdminByPhone(phone);

		if(Objects.isNull(admin)) {
			return false;
		}

		 //禁用校验
        if (admin.getStatus()) {
        	return false;
        }else {
        	return true;
        }


	}
}
