package com.yimao.cloud.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.user.MenuDTO;
import com.yimao.cloud.pojo.dto.user.PermissionCacheDTO;
import com.yimao.cloud.pojo.dto.user.RoleDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.mapper.AdminRoleMapper;
import com.yimao.cloud.user.mapper.MenuMapper;
import com.yimao.cloud.user.mapper.PermissionMapper;
import com.yimao.cloud.user.mapper.RoleMapper;
import com.yimao.cloud.user.mapper.RoleMenuMapper;
import com.yimao.cloud.user.mapper.RolePermissionMapper;
import com.yimao.cloud.user.po.AdminRole;
import com.yimao.cloud.user.po.Role;
import com.yimao.cloud.user.po.RoleMenu;
import com.yimao.cloud.user.po.RolePermission;
import com.yimao.cloud.user.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Zhang Bo
 * @date 2018/11/22.
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Resource
    private RedisCache redisCache;
    @Resource
    private UserCache userCache;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private AdminRoleMapper adminRoleMapper;
    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;

    /**
     * 创建角色信息
     *
     * @param role          角色信息
     * @param permissionIds 权限ID集合
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void save(Role role, Set<Integer> permissionIds) {
        if (StringUtil.isBlank(role.getName())) {
            throw new BadRequestException("角色名不能为空。");
        }

        Role query = new Role();
        query.setName(role.getName());
        query.setSysType(role.getSysType());
        int count = roleMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("角色已存在。");
        }
        role.setCreator(userCache.getCurrentAdminRealName());
        role.setCreateTime(new Date());
        int result = roleMapper.insert(role);
        if (result < 1) {
            throw new YimaoException("新增角色失败。");
        }

        // // 为角色设置所有菜单权限
        // this.addMenus(role.getId(), role.getSysType());

        // 为角色绑定权限
        this.addPermissions(role.getId(), permissionIds);

        // 缓存角色权限列表
        List<PermissionCacheDTO> permissions = permissionMapper.selectCodeAndMethodByRoleId(role.getId());
        redisCache.setCacheList(Constant.ROLE_PERMISSIONS + role.getId(), permissions, PermissionCacheDTO.class);
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void delete(Integer id) {
        boolean exists = roleMapper.existsWithPrimaryKey(id);
        if (!exists) {
            throw new NotFoundException("未找到角色信息。");
        }

        // 角色有没有关联的用户，如果有不能删除
        AdminRole query = new AdminRole();
        query.setRoleId(id);
        int count = adminRoleMapper.selectCount(query);
        if (count > 0) {
            throw new YimaoException("尚有管理员在使用该角色，不能删除。");
        }

        roleMapper.deleteByPrimaryKey(id);

        // 删除旧的角色-菜单关联映射
        RoleMenu roleMenu = new RoleMenu();
        roleMenu.setRoleId(id);
        roleMenuMapper.delete(roleMenu);

        // 删除旧的角色-权限关联映射
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(id);
        rolePermissionMapper.delete(rolePermission);
    }

    /**
     * 更新角色
     *
     * @param role          角色信息
     * @param permissionIds 权限ID集合
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void update(Role role, Set<Integer> permissionIds) {
        Role record = roleMapper.selectByPrimaryKey(role.getId());
        if (record == null) {
            throw new BadRequestException("修改对象不存在。");
        }
        if (StringUtil.isBlank(role.getName())) {
            throw new BadRequestException("角色名不能为空。");
        }
        Example example = new Example(Role.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("id", role.getId());
        criteria.andEqualTo("name", role.getName());
        criteria.andEqualTo("sysType", record.getSysType());
        int count = roleMapper.selectCountByExample(example);
        if (count > 0) {
            throw new BadRequestException("角色已存在。");
        }
        role.setUpdater(userCache.getCurrentAdminRealName());
        role.setUpdateTime(new Date());
        //不更新sysType
        role.setSysType(null);
        roleMapper.updateByPrimaryKeySelective(role);

        // // 为角色设置所有菜单权限
        // this.addMenus(role.getId(), record.getSysType());

        // 为角色绑定权限
        this.addPermissions(role.getId(), permissionIds);

        // 缓存角色权限列表
        List<PermissionCacheDTO> permissions = permissionMapper.selectCodeAndMethodByRoleId(role.getId());
        redisCache.setCacheList(Constant.ROLE_PERMISSIONS + role.getId(), permissions, PermissionCacheDTO.class);
    }

    /**
     * 分页查询角色信息
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @return
     */
    @Override
    public PageVO<RoleDTO> page(int pageNum, int pageSize, String roleName, Integer sysType) {
        Example example = new Example(Role.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sysType", sysType);
        if (StringUtil.isNotEmpty(roleName)) {
            criteria.andLike("name", roleName + "%");
        }
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<Role> page = (Page<Role>) roleMapper.selectByExample(example);
        // PO对象转成DTO对象
        return new PageVO<>(pageNum, page, Role.class, RoleDTO.class);
    }

    // /**
    //  * 为角色添加菜单
    //  *
    //  * @param roleId 角色ID
    //  */
    // private void addMenus(Integer roleId, Integer sysType) {
    //     if (roleId == null) {
    //         throw new BadRequestException("角色ID不能为空。");
    //     }
    //     // 删除旧的角色-菜单映射
    //     RoleMenu roleMenu = new RoleMenu();
    //     roleMenu.setRoleId(roleId);
    //     roleMenuMapper.delete(roleMenu);
    //     // 获取所有菜单
    //     List<MenuDTO> menus = menuMapper.selectAllMenus(sysType);
    //     if (CollectionUtil.isNotEmpty(menus)) {
    //         // 新增新的角色-菜单映射
    //         List<RoleMenu> roleMenuList = new ArrayList<>();
    //         for (MenuDTO menu : menus) {
    //             roleMenu = new RoleMenu();
    //             roleMenu.setRoleId(roleId);
    //             roleMenu.setMenuId(menu.getId());
    //             roleMenuList.add(roleMenu);
    //         }
    //         // 批量插入角色权限映射
    //         roleMenuMapper.batchInsert(roleMenuList);
    //     }
    // }

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

        Role role = roleMapper.selectByPrimaryKey(roleId);
        if (role == null) {
            throw new BadRequestException("角色不能为空。");
        }
        boolean haveAllMenu = false;
        if (role.getName().contains("系统超级管理员")) {
            haveAllMenu = true;
        }

        // 1-删除旧的角色-权限映射
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        rolePermissionMapper.delete(rolePermission);

        // 2-删除旧的角色-菜单映射
        RoleMenu roleMenu = new RoleMenu();
        roleMenu.setRoleId(roleId);
        roleMenuMapper.delete(roleMenu);

        if (CollectionUtil.isNotEmpty(permissionIds)) {
            // 3-新增新的角色-权限映射
            List<RolePermission> rolePermissionList = new ArrayList<>();
            for (Integer permissionId : permissionIds) {
                rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionList.add(rolePermission);
            }
            // 批量插入角色权限映射
            rolePermissionMapper.batchInsert(rolePermissionList);

            // 4-新增新的角色-菜单映射
            // 根据权限ID集合获取菜单集合
            Set<MenuDTO> menus = menuMapper.selectMenuBypermissionIds(permissionIds);
            if (CollectionUtil.isNotEmpty(menus)) {
                // 定义菜单ID集合
                Set<Integer> menuIds = new HashSet<>();
                for (MenuDTO menuDTO : menus) {
                    menuIds.add(menuDTO.getId());
                    Integer pid = menuDTO.getPid();
                    // 从子菜单一直遍历找到一级菜单，将所有菜单ID存放到menuIds中
                    while (pid != 0 && !menuIds.contains(pid)) {
                        menuIds.add(pid);
                        // 根据菜单ID查询菜单pid
                        pid = menuMapper.selectPidById(pid);
                    }
                }
                //如果是超级管理员，则额外设置上所有菜单的可见权限，实际是否能访问还是要看RolePermission
                if (haveAllMenu) {
                    Set<Integer> allMenuId = menuMapper.selectAllMenuId(role.getSysType());
                    menuIds.addAll(allMenuId);
                }
                List<RoleMenu> roleMenuList = new ArrayList<>();
                for (Integer menuId : menuIds) {
                    roleMenu = new RoleMenu();
                    roleMenu.setRoleId(roleId);
                    roleMenu.setMenuId(menuId);
                    roleMenuList.add(roleMenu);
                }
                // 批量插入角色权限映射
                roleMenuMapper.batchInsert(roleMenuList);
            }
        }
    }

}
