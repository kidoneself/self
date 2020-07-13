package com.yimao.cloud.station.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.station.StationPermissionCacheDTO;
import com.yimao.cloud.pojo.dto.station.StationPermissionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.station.constant.StationConstant;
import com.yimao.cloud.station.mapper.StationAdminMapper;
import com.yimao.cloud.station.mapper.StationPermissionMapper;
import com.yimao.cloud.station.mapper.StationRolePermissionMapper;
import com.yimao.cloud.station.po.StationAdmin;
import com.yimao.cloud.station.po.StationPermission;
import com.yimao.cloud.station.po.StationRolePermission;
import com.yimao.cloud.station.service.StationAdminService;
import com.yimao.cloud.station.service.StationMenuService;
import com.yimao.cloud.station.service.StationPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class StationPermissionServiceImpl implements StationPermissionService {

    @Resource
    private StationPermissionMapper stationPermissionMapper;

    @Resource
    private StationMenuService stationMenuService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private UserCache userCache;

    @Resource
    private StationRolePermissionMapper stationRolePermissionMapper;

    @Resource
    private StationAdminService stationAdminService;
    
    @Resource
    private StationAdminMapper stationAdminMapper;

    @Override
    public void save(StationPermission permission) {
        if (StringUtil.isEmpty(permission.getName())) {
            throw new BadRequestException("权限名不能为空。");
        }
        if (StringUtil.isEmpty(permission.getMethod())) {
            throw new BadRequestException("方法类型不能为空。");
        }
        if (StringUtil.isEmpty(permission.getUrl())) {
            throw new BadRequestException("权限url不能为空。");
        }
        if (Objects.isNull(permission.getMenuId())) {
            throw new BadRequestException("菜单ID不能为空。");
        }
        if (Objects.isNull(permission.getType())) {
            throw new BadRequestException("权限类型不能为空。");
        }
        StationPermission query = new StationPermission();
        query.setMethod(permission.getMethod());
        query.setUrl(permission.getUrl());
        query.setMenuId(permission.getMenuId());
        List<StationPermission> queryPermissions = stationPermissionMapper.selectByQuery(query);
        if (CollectionUtil.isNotEmpty(queryPermissions)) {
            throw new BadRequestException("权限已存在。");
        }

        //设置创建人
        permission.setCreator(userCache.getUserId());
        // 设置创建时间
        permission.setCreateTime(new Date());
        int result = stationPermissionMapper.insertSelective(permission);
        if (result < 1) {
            throw new YimaoException("操作失败。");
        }

        //todo 更新缓存
  /*      List<StationPermissionCacheDTO> permissions = stationPermissionMapper.selectAll();
        redisCache.setCacheList(Constant.STATION_PERMISSIONS, permissions, StationPermissionCacheDTO.class);*/
    }

    @Override
    public void delete(Integer id) {
        StationPermission stationPermission = stationPermissionMapper.selectByPrimaryKey(id);
        if (Objects.isNull(stationPermission)) {
            throw new NotFoundException("未找到权限信息。");
        }
        stationPermissionMapper.deleteByPrimaryKey(id);

        List<Integer> roleIds = stationRolePermissionMapper.selectRoleIdByPermissionId(id);
        if (CollectionUtil.isNotEmpty(roleIds)) {
        	//查询所有绑定角色的用户
            List<StationAdmin> adminList = stationAdminMapper.selectByRoleList(roleIds);
            
            if(CollectionUtil.isNotEmpty(adminList)) {            
                //更新绑定了该权限管理员的权限缓存
                stationAdminService.updateStationAdminPermissionCache(adminList);            
            }
            
        }
        //删除角色与权限绑定关系
        StationRolePermission rolePermission = new StationRolePermission();
        rolePermission.setPermissionId(id);
        stationRolePermissionMapper.delete(rolePermission);
    }

    @Override
    public void update(StationPermission permission) {
        StationPermission record = stationPermissionMapper.selectByPrimaryKey(permission.getId());
        if (Objects.isNull(record)) {
            throw new BadRequestException("修改对象不存在。");
        }
        // 校验权限是否已存在
        StationPermission stationPermission = new StationPermission();
        stationPermission.setId(permission.getId());
        stationPermission.setMethod(permission.getMethod());
        stationPermission.setUrl(permission.getUrl());
        stationPermission.setMenuId(permission.getMenuId());
        int count = stationPermissionMapper.checkPermissionExist(stationPermission);
        if (count > 0) {
            throw new BadRequestException("权限已存在。");
        }

        permission.setUpdater(userCache.getUserId());
        // 设置更新时间
        permission.setUpdateTime(new Date());
        stationPermissionMapper.updateByPrimaryKeySelective(permission);

        //更新缓存
        List<Integer> roleIds = stationRolePermissionMapper.selectRoleIdByPermissionId(permission.getId());
        if (CollectionUtil.isNotEmpty(roleIds)) {
        	//查询所有绑定角色的用户
            List<StationAdmin> adminList = stationAdminMapper.selectByRoleList(roleIds);
            
            if(CollectionUtil.isNotEmpty(adminList)) {         
                //更新绑定了该权限管理员的权限缓存
                stationAdminService.updateStationAdminPermissionCache(adminList);            
            }
        }
    }

    @Override
    public PageVO<StationPermissionDTO> page(Integer pageNum, Integer pageSize, Integer menuId) {
        List<Integer> subMenuIdList = new ArrayList<>();
        if (menuId != null) {
            subMenuIdList = stationMenuService.getChildMenuIds(menuId);
        }
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        //通过子菜单的id获取权限信息
        Page<StationPermissionDTO> page = stationPermissionMapper.selectBySubMenuIdList(subMenuIdList);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public void batchSave(List<StationPermissionDTO> dtoList) {
        Date now = new Date();
        for (StationPermissionDTO dto : dtoList) {
            StationPermission permission = new StationPermission(dto);
            List<StationPermission> queryStationPermissions = stationPermissionMapper.selectByQuery(permission);
            if (CollectionUtil.isEmpty(queryStationPermissions) && StringUtil.isNotEmpty(permission.getMethod()) && StringUtil.isNotEmpty(permission.getUrl())) {
                permission.setName("batchInsert");
                // 设置创建人
                permission.setCreator(userCache.getUserId());
                permission.setCreateTime(now);
                stationPermissionMapper.insertSelective(permission);
            }
        }
    }

    @Override
    public boolean existPermission(String method, String url) {
        StationPermission query = new StationPermission();
        query.setMethod(method);
        query.setUrl(url);
        List<StationPermission> queryPermissions = stationPermissionMapper.selectByQuery(query);
        if (CollectionUtil.isNotEmpty(queryPermissions)) {
            return true;
        }
        return false;
    }

    /**
     * 根据角色id获取权限集合
     *
     * @param roleId
     * @return
     */
    @Override
    public Set<Integer> getPermissionIdsByRoleId(Integer roleId) {

        Set<Integer> permissionIds = new HashSet<>();
        //如果是超级管理员，获取全部权限信息
        if (roleId == StationConstant.SUPERROLE) {
            List<StationPermissionCacheDTO> stationPermissionCacheDTOS = stationPermissionMapper.selectPermissionCacheAll();
            if (CollectionUtil.isNotEmpty(stationPermissionCacheDTOS)) {
                for (StationPermissionCacheDTO stationPermissionCacheDTO : stationPermissionCacheDTOS) {
                    permissionIds.add(stationPermissionCacheDTO.getId());
                }
            }
            return permissionIds;
        }
        //如果是普通角色，则根据角色id获取权限信息
        permissionIds = stationRolePermissionMapper.selectPermissionIdsByRoleId(roleId);

        return permissionIds;
    }
}
