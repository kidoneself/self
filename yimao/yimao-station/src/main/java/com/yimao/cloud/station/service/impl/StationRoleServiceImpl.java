package com.yimao.cloud.station.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.station.*;
import com.yimao.cloud.pojo.query.station.StationAreaTypeQuery;
import com.yimao.cloud.pojo.query.station.StationRoleQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.station.constant.StationConstant;
import com.yimao.cloud.station.feign.SystemFeign;
import com.yimao.cloud.station.mapper.*;
import com.yimao.cloud.station.po.*;
import com.yimao.cloud.station.service.StationAdminService;
import com.yimao.cloud.station.service.StationMenuService;
import com.yimao.cloud.station.service.StationRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


@Service
@Slf4j
public class StationRoleServiceImpl implements StationRoleService {

    @Resource
    private StationRoleMapper stationRoleMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private StationPermissionMapper stationPermissionMapper;

    @Resource
    private StationRolePermissionMapper stationRolePermissionMapper;

    @Resource
    private StationMenuMapper stationMenuMapper;

    @Resource
    private StationRoleMenuMapper stationRoleMenuMapper;

    @Resource
    private UserCache userCache;

    @Resource
    private StationAdminMapper stationAdminMapper;

    @Resource
    private SystemRoleMapper systemRoleMapper;

    @Resource
    private StationMenuService stationMenuService;

    @Resource
    private StationAdminService stationAdminService;
    
    @Resource
    private SystemFeign systemFeign;

    @Override
    public List<StationRoleDTO> getRoleList(Integer stationCompanyId) {

        return stationRoleMapper.selectRolesByStationCompanyId(stationCompanyId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void save(StationRoleDTO dto) {

        if (Objects.isNull(dto.getStationCompanyId())) {
            throw new BadRequestException("创建角色的服务站公司为空。");
        }

        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();

        if (stationCompanyId == null) {
            throw new BadRequestException("用户登录失效。");
        } else if (stationCompanyId.equals(StationConstant.SUPERCOMPANYID)) {
            //该用户是超级管理员
            stationCompanyId = dto.getStationCompanyId();

        } else {
            if (!stationCompanyId.equals(dto.getStationCompanyId())) {
                throw new BadRequestException("创建角色选择的服务站公司非用户所在服务站公司");
            }
        }

        StationRole role = new StationRole(dto);
        if (StringUtil.isBlank(role.getRoleName())) {
            throw new BadRequestException("角色名不能为空。");
        }

        int count = stationRoleMapper.selectCountByRoleName(role.getRoleName(), stationCompanyId);
        if (count > 0) {
            throw new BadRequestException("角色已存在。");
        }

        //角色绑定服务站公司id
        role.setStationCompanyId(stationCompanyId);

        // 设置创建人
        role.setId(null);
        role.setCreator(userCache.getUserId());
        role.setCreateTime(new Date());
        int result = stationRoleMapper.insertSelective(role);
        if (result < 1) {
            throw new YimaoException("新增角色失败。");
        }

        // 为角色绑定权限
        this.addPermissions(role.getId(), dto.getStationPermissionIds());

    }

    /**
     * 根据角色id删除角色
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Integer id) {

        StationRole role = stationRoleMapper.selectByPrimaryKey(id);
        if (Objects.isNull(role)) {
            throw new NotFoundException("角色信息不存在");
        }

        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();

        if (stationCompanyId == null) {
            throw new BadRequestException("用户登录失效。");
        } else if (!stationCompanyId.equals(StationConstant.SUPERCOMPANYID)) {

            if (!role.getStationCompanyId().equals(stationCompanyId)) {
                throw new YimaoException("角色删除错误。");
            }

        }

        // 角色有没有关联的用户，如果有不能删除
        StationAdmin query = new StationAdmin();
        query.setRoleId(id);

        int count = stationAdminMapper.selectAdminCount(query);

        if (count > 0) {
            throw new YimaoException("尚有管理员在使用该角色，不能删除。");
        }

        stationRoleMapper.deleteByPrimaryKey(id);

        // 删除旧的角色-菜单关联映射
        StationRoleMenu roleMenu = new StationRoleMenu();
        roleMenu.setRoleId(id);
        stationRoleMenuMapper.delete(roleMenu);

        // 删除旧的角色-权限关联映射
        StationRolePermission rolePermission = new StationRolePermission();
        rolePermission.setRoleId(id);
        stationRolePermissionMapper.delete(rolePermission);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void update(StationRoleDTO dto) {

        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();

        if (stationCompanyId == null) {
            throw new BadRequestException("用户登录失效。");
        } else if (stationCompanyId.equals(StationConstant.SUPERCOMPANYID)) {
            //该用户是超级管理员
            if (Objects.isNull(dto.getStationCompanyId())) {
                throw new BadRequestException("创建角色的服务站公司为空。");
            }
            stationCompanyId = dto.getStationCompanyId();

        } else {
            dto.setStationCompanyId(stationCompanyId);
        }

        StationRole role = new StationRole(dto);
        StationRole record = stationRoleMapper.selectByPrimaryKey(role.getId());
        if (Objects.isNull(record)) {
            throw new BadRequestException("修改对象不存在。");
        }

        if (!record.getStationCompanyId().equals(stationCompanyId)) {
            throw new YimaoException("角色编辑错误。");
        }

        if (StringUtil.isBlank(role.getRoleName())) {
            throw new BadRequestException("角色名不能为空。");
        }

        int count = stationRoleMapper.selectCountByRoleNameAndId(role.getRoleName(), role.getId(), stationCompanyId);
        if (count > 0) {
            throw new BadRequestException("角色已存在。");
        }
        // 设置修改人
        role.setUpdater(userCache.getUserId());
        role.setUpdateTime(new Date());
        stationRoleMapper.updateByPrimaryKeySelective(role);

        // 为角色绑定权限
        this.addPermissions(role.getId(), dto.getStationPermissionIds());

        //缓存角色权限列表
        stationAdminService.updateStationAdminPermissionCache(role.getId());
    }

    @Override
    public PageVO<StationRoleDTO> page(Integer pageNum, Integer pageSize, StationRoleQuery query) {
        //从缓存中获取服务站公司id
        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();
        if (stationCompanyId == null) {
            throw new YimaoException("用户登录异常。");
        } else if (stationCompanyId.equals(StationConstant.SUPERCOMPANYID)) {
            query.setStationCompanyId(null);
        } else {
            query.setStationCompanyId(stationCompanyId);
        }

        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<StationRoleDTO> page = stationRoleMapper.selectByRoleNameAndStationCompanyId(query);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 为角色添加权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID集合
     */
    private void addPermissions(Integer roleId, Set<Integer> permissionIds) {

        if (roleId == null) {
            throw new BadRequestException("角色ID不能为空。");
        }

        StationRole role = stationRoleMapper.selectByPrimaryKey(roleId);
        if (Objects.isNull(role)) {
            throw new BadRequestException("角色不能为空。");
        }
        boolean haveAllMenu = false;
        if (role.getRoleName().contains("高级管理员")) {
            haveAllMenu = true;
        }

        // 1-删除旧的角色-权限映射
        StationRolePermission rolePermission = new StationRolePermission();
        rolePermission.setRoleId(roleId);
        stationRolePermissionMapper.delete(rolePermission);

        // 2-删除旧的角色-菜单映射
        StationRoleMenu roleMenu = new StationRoleMenu();
        roleMenu.setRoleId(roleId);
        stationRoleMenuMapper.delete(roleMenu);

        if (CollectionUtil.isNotEmpty(permissionIds)) {
            // 3-新增新的角色-权限映射
            List<StationRolePermission> rolePermissionList = new ArrayList<>();
            for (Integer permissionId : permissionIds) {
                rolePermission = new StationRolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionList.add(rolePermission);
            }
            // 批量插入角色权限映射
            stationRolePermissionMapper.batchInsert(rolePermissionList);

            // 4-新增新的角色-菜单映射
            // 根据权限ID集合获取菜单集合
            Set<StationMenuDTO> menus = stationMenuMapper.selectMenuByPermissionIds(permissionIds);
            if (CollectionUtil.isNotEmpty(menus)) {
                // 定义菜单ID集合
                Set<Integer> menuIds = new HashSet<>();
                for (StationMenuDTO menuDTO : menus) {
                    menuIds.add(menuDTO.getId());
                    Integer pid = menuDTO.getPid();
                    // 从子菜单一直遍历找到一级菜单，将所有菜单ID存放到menuIds中
                    while (pid != 0 && !menuIds.contains(pid)) {
                        menuIds.add(pid);
                        // 根据菜单ID查询菜单pid
                        pid = stationMenuMapper.selectPidById(pid);
                    }
                }
                //如果是高级管理员，则额外设置上所有菜单的可见权限，实际是否能访问还是要看RolePermission
                if (haveAllMenu) {
                    Set<Integer> allMenuId = stationMenuMapper.selectAllMenuId();
                    menuIds.addAll(allMenuId);
                }
                List<StationRoleMenu> roleMenuList = new ArrayList<>();
                for (Integer menuId : menuIds) {
                    roleMenu = new StationRoleMenu();
                    roleMenu.setRoleId(roleId);
                    roleMenu.setMenuId(menuId);
                    roleMenuList.add(roleMenu);
                }
                // 批量插入角色权限映射
                stationRoleMenuMapper.batchInsert(roleMenuList);
            }
        }
    }

    /**
     * 根据服务站员工id删除所有绑定区域
     */
    @Override
    public void deleteByAdminId(Integer adminId) {
        if (Objects.nonNull(adminId)) {
            systemRoleMapper.deleteByAdminId(adminId);
        }

    }

    /**
     * 根据员工id获取服务站与区域集合
     */
    @Override
    public StationAdminAreasCacheDTO findStationsAndAreasByAdminId(Integer adminId) {

        StationAdminAreasCacheDTO areasCache = new StationAdminAreasCacheDTO();

        if (Objects.isNull(adminId)) {
            throw new BadRequestException("员工id为空。");
        } else {
            Set<Integer> preAreasList = new HashSet<>();
                      
            Set<Integer> afterAreasList = new HashSet<>();
           
            Set<Integer> stationsList = new HashSet<>();
            
            List<SystemRole> list = systemRoleMapper.selectAreasByAdminId(adminId);

            if (list != null && list.size() > 0) {           	                       
            	
                for (SystemRole systemRole : list) {
                	
                	stationsList.add(systemRole.getStationId());
                	
                    String areas = systemRole.getAreaIds();
                    log.info("areas={}", areas);
                    List<Integer> areaList=JSON.parseArray(areas, Integer.class);

                    //根据绑定门店查询绑定区域的售前售后属性
                    if(CollectionUtil.isNotEmpty(areaList)) {
                    	StationAreaTypeQuery query =new StationAreaTypeQuery();
                    	query.setStationId(systemRole.getStationId());
                    	query.setAreaIds(new HashSet<Integer>(areaList));
                    	
                    	List<SystemAreaTypeDto> result = systemFeign.getAdminStationAreaType(query);
                    	
                    	//将售前售后区域分别归属不同集合
                    	if(CollectionUtil.isNotEmpty(result)) {
                    		for (SystemAreaTypeDto systemAreaTypeDto : result) {
                    			
								if(PermissionTypeEnum.ALL.value == systemAreaTypeDto.getServiceType()) {//售前+售后
									preAreasList.add(systemAreaTypeDto.getAresId());
									afterAreasList.add(systemAreaTypeDto.getAresId());
								}else if(PermissionTypeEnum.PRE_SALE.value == systemAreaTypeDto.getServiceType()) {//售前
									preAreasList.add(systemAreaTypeDto.getAresId());
								}else if(PermissionTypeEnum.AFTER_SALE.value == systemAreaTypeDto.getServiceType()) {//售后
									afterAreasList.add(systemAreaTypeDto.getAresId());
								}
								
							}
                    		
                    	}
                    	
                    }

                    
                }

                areasCache.setPreAreaIds(preAreasList);
                areasCache.setStationIds(stationsList);
                areasCache.setAfterAreaIds(afterAreasList);

                return areasCache;
            }

            return areasCache;
        }

    }


    /**
     * 批量新增员工绑定区域
     */
    @Override
    public void batchInsert(List<SystemRole> list) {
        if (list != null && list.size() > 0) {
            systemRoleMapper.batchInsert(list);
        }
    }


    /**
     * 根据角色获取菜单列表
     *
     * @param roleId 角色id
     * @return 菜单列表
     */
    @Override
    public List<StationMenuDTO> getMenuListByRoleId(Integer roleId,Integer serviceType) {
        if (roleId == null) {
            throw new YimaoException("角色id不能为空");
        }
        return stationMenuService.getMenuListByRoleId(roleId,serviceType);
    }

    /**
     * 根据角色id获取菜单权限列表（查询用户权限放入使用）
     */
    @Override
    public List<StationPermissionCacheDTO> findPermissionsByRoleId(Integer roleId) {
        if (roleId == null) {
            throw new BadRequestException("角色id不能为空");
        }
        //如果是高级管理员查询所有
        StationRole role = stationRoleMapper.selectByPrimaryKey(roleId);
        if (Objects.isNull(role)) {
            throw new BadRequestException("角色不存在。");
        }
        // 如果是超级管理员查询所有
        if (roleId == StationConstant.SUPERROLE) {
            return stationPermissionMapper.selectPermissionCacheAll();
        }
        return stationRolePermissionMapper.selectPermissionsByRoleId(roleId);
    }

    /**
     * 判断该服务站公司是否有角色
     */
    @Override
    public boolean existRoleIdByStationCompanyId(Integer roleId, Integer stationCompanyId) {

        StationRole role = new StationRole();
        role.setId(roleId);
        role.setStationCompanyId(stationCompanyId);
        StationRole res = stationRoleMapper.selectByQuery(role);
        if (Objects.nonNull(res)) {
            return true;
        }
        return false;
    }

    /**
     * 根据服务站id获取所有绑定该服务站的用户区域集合
     */
    @Override
    public List<SystemRoleDTO> findSystemRoleByStationId(Integer stationId) {
        return systemRoleMapper.selectSystemRoleByStationId(stationId);

    }

    @Override
    public void updateSystemRole(SystemRole systemRole) {
        systemRoleMapper.updateByPrimaryKeySelective(systemRole);

    }


}
