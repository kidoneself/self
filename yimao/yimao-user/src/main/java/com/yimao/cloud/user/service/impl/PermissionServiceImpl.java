package com.yimao.cloud.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.user.PermissionCacheDTO;
import com.yimao.cloud.pojo.dto.user.PermissionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.mapper.PermissionMapper;
import com.yimao.cloud.user.po.Permission;
import com.yimao.cloud.user.service.MenuService;
import com.yimao.cloud.user.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Zhang Bo
 * @date 2018/12/20.
 */
@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private RedisCache redisCache;
    @Resource
    private UserCache userCache;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private MenuService menuService;

    /**
     * 新增权限
     *
     * @param permission 权限信息
     */
    @Override
    public void save(Permission permission) {
        if (StringUtil.isBlank(permission.getName())) {
            throw new BadRequestException("权限名不能为空。");
        }
        if (StringUtil.isBlank(permission.getMethod())) {
            throw new BadRequestException("方法类型不能为空。");
        }
        if (StringUtil.isBlank(permission.getCode())) {
            throw new BadRequestException("权限url不能为空。");
        }
        if (Objects.isNull(permission.getMenuId())) {
            throw new BadRequestException("菜单ID不能为空。");
        }
        Permission query = new Permission();
        query.setMethod(permission.getMethod());
        query.setCode(permission.getCode());
        query.setMenuId(permission.getMenuId());
        query.setSysType(permission.getSysType());
        Permission one = permissionMapper.selectOne(query);
        if (one != null) {
            throw new BadRequestException("权限已存在。");
        }
        // 设置创建人
        permission.setCreator(userCache.getCurrentAdminRealName());
        // 设置创建时间
        permission.setCreateTime(new Date());
        int result = permissionMapper.insert(permission);
        if (result < 1) {
            throw new YimaoException("操作失败。");
        }

        //更新缓存
        List<PermissionCacheDTO> permissions = permissionMapper.selectBySysType(permission.getSysType());
        redisCache.setCacheList(Constant.PERMISSIONS + permission.getSysType(), permissions, PermissionCacheDTO.class);
    }

    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    @Override
    public void delete(Integer id) {
        Permission record = permissionMapper.selectByPrimaryKey(id);
        if (record == null) {
            throw new NotFoundException("未找到权限信息。");
        }
        permissionMapper.deleteByPrimaryKey(id);

        //更新缓存
        List<PermissionCacheDTO> permissions = permissionMapper.selectBySysType(record.getSysType());
        redisCache.setCacheList(Constant.PERMISSIONS + record.getSysType(), permissions, PermissionCacheDTO.class);
    }

    /**
     * 更新权限
     *
     * @param permission 权限信息
     */
    @Override
    public void update(Permission permission) {
        Permission record = permissionMapper.selectByPrimaryKey(permission.getId());
        if (record == null) {
            throw new BadRequestException("修改对象不存在。");
        }
        // 校验权限是否已存在
        Example example = new Example(Permission.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("id", permission.getId());
        criteria.andEqualTo("method", permission.getMethod());
        criteria.andEqualTo("code", permission.getCode());
        criteria.andEqualTo("menuId", permission.getMenuId());
        criteria.andEqualTo("sysType", record.getSysType());
        int count = permissionMapper.selectCountByExample(example);
        if (count > 0) {
            throw new BadRequestException("权限已存在。");
        }

        // 设置更新人
        permission.setUpdater(userCache.getCurrentAdminRealName());
        // 设置更新时间
        permission.setUpdateTime(new Date());
        //不更新sysType
        permission.setSysType(null);
        permissionMapper.updateByPrimaryKeySelective(permission);

        //更新缓存
        List<PermissionCacheDTO> permissions = permissionMapper.selectBySysType(record.getSysType());
        redisCache.setCacheList(Constant.PERMISSIONS + record.getSysType(), permissions, PermissionCacheDTO.class);
    }

    /**
     * 分页查询权限信息，可带查询条件
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param menuId   菜单ID
     * @return
     */
    @Override
    public PageVO<PermissionDTO> page(Integer pageNum, Integer pageSize, Integer sysType, Integer menuId) {
        Example example = new Example(Permission.class);
        Example.Criteria criteria = example.createCriteria();
        if (menuId != null) {
            List<Integer> subMenuIdList = menuService.getChildMenuIds(sysType, menuId);
            criteria.andIn("menuId", subMenuIdList);
        }
        criteria.andEqualTo("sysType", sysType);
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<Permission> page = (Page<Permission>) permissionMapper.selectByExample(example);
        // PO对象转成DTO对象
        return new PageVO<>(pageNum, page, Permission.class, PermissionDTO.class);
    }

    /**
     * 根据菜单ID获取权限列表
     *
     * @param menuId 菜单ID
     * @return
     */
    @Override
    public List<PermissionDTO> listByMenuId(Integer menuId) {
        return permissionMapper.selectPermissionByMenuId(menuId);
    }

}
