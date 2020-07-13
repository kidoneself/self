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
import com.yimao.cloud.pojo.dto.station.StationMenuDTO;
import com.yimao.cloud.pojo.dto.station.StationPermissionCacheDTO;
import com.yimao.cloud.pojo.dto.station.StationPermissionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.station.constant.StationConstant;
import com.yimao.cloud.station.mapper.StationMenuMapper;
import com.yimao.cloud.station.mapper.StationRoleMenuMapper;
import com.yimao.cloud.station.po.StationMenu;
import com.yimao.cloud.station.po.StationRoleMenu;
import com.yimao.cloud.station.service.StationMenuService;
import com.yimao.cloud.station.service.StationPermissionService;
import com.yimao.cloud.station.service.StationRoleService;
import com.yimao.cloud.station.util.MenuFilterConfig;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class StationMenuServiceImpl implements StationMenuService {

    @Resource
    private StationMenuMapper stationMenuMapper;

    @Resource
    private StationRoleMenuMapper stationRoleMenuMapper;

    @Resource
    private UserCache userCache;

    @Resource
    private StationPermissionService stationPermissionService;

    @Resource
    private StationRoleService stationRoleService;
    
    @Autowired
    private MenuFilterConfig menuFilterConfig;

    /**
     * 添加菜单
     *
     * @param stationMenu 菜单信息
     */
    @Override
    public void save(StationMenu stationMenu) {
        if (StringUtil.isEmpty(stationMenu.getName())) {
            throw new BadRequestException("菜单名不能为空。");
        }
        if (StringUtil.isEmpty(stationMenu.getUrl())) {
            throw new BadRequestException("菜单地址不能为空。");
        }
        if (stationMenu.getPid() == null || stationMenu.getPid() == 0) {
            stationMenu.setPid(0);
            stationMenu.setLevel(1);//一级菜单
        } else {
            StationMenu parent = stationMenuMapper.selectByPrimaryKey(stationMenu.getPid());
            if (Objects.isNull(parent)) {
                throw new YimaoException("添加菜单失败，未找到父级菜单。");
            }
            // 设置菜单层级
            stationMenu.setLevel(parent.getLevel() + 1);
        }
        StationMenu query = new StationMenu();
        query.setName(stationMenu.getName());
        int count = stationMenuMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("菜单已存在。");
        }
        query.setName(null);
        query.setUrl(stationMenu.getUrl());
        count = stationMenuMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("菜单URL已存在。");
        }
        // 设置创建人
        stationMenu.setCreator(userCache.getUserId());
        stationMenu.setCreateTime(new Date());
        int result = stationMenuMapper.insertSelective(stationMenu);
        if (result < 1) {
            throw new YimaoException("添加菜单失败。");
        }
    }

    @Override
    public void delete(Integer id) {
        StationMenu stationMenu = stationMenuMapper.selectByPrimaryKey(id);
        if (Objects.isNull(stationMenu)) {
            throw new NotFoundException("未找到菜单信息。");
        }

        int result = stationMenuMapper.deleteByPrimaryKey(id);
        if (result < 1) {
            throw new YimaoException("删除菜单失败。");
        }

        // 删除角色菜单关联表数据
        StationRoleMenu roleMenu = new StationRoleMenu();
        roleMenu.setMenuId(id);
        stationRoleMenuMapper.delete(roleMenu);
    }

    @Override
    public void update(StationMenu menu) {
        StationMenu record = stationMenuMapper.selectByPrimaryKey(menu.getId());
        if (Objects.isNull(record)) {
            throw new BadRequestException("修改菜单不存在。");
        }
        if (StringUtil.isEmpty(menu.getName())) {
            throw new BadRequestException("菜单名不能为空。");
        }
        if (StringUtil.isEmpty(menu.getUrl())) {
            throw new BadRequestException("菜单地址不能为空。");
        }
        if (menu.getPid() == null || menu.getPid() == 0) {
            menu.setPid(0);
            menu.setLevel(1);//一级菜单
        } else {
            StationMenu parent = stationMenuMapper.selectByPrimaryKey(menu.getPid());
            if (Objects.isNull(parent)) {
                throw new YimaoException("修改菜单失败，未找到父级菜单。");
            }
            // 设置菜单层级
            menu.setLevel(parent.getLevel() + 1);
        }
        //校验菜单名称是否已经存在
        int count = stationMenuMapper.checkMenuNameAndUrlExist(menu.getId(), menu.getName(), null);
        if (count > 0) {
            throw new BadRequestException("该菜单名称已存在。");
        }
        //校验菜单URL是否已经存在
        count = stationMenuMapper.checkMenuNameAndUrlExist(menu.getId(), null, menu.getUrl());
        if (count > 0) {
            throw new BadRequestException("该菜单URL已存在。");
        }
        // 设置修改人
        menu.setUpdater(userCache.getUserId());
        menu.setUpdateTime(new Date());
        stationMenuMapper.updateByPrimaryKeySelective(menu);
    }

    @Override
    public PageVO<StationMenuDTO> page(Integer pageNum, Integer pageSize, Integer id) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<StationMenuDTO> page = stationMenuMapper.selectSubMenus(id);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 获取级联菜单结构
     */
    @Override
    public List<StationMenuDTO> listMenuTree() {
        List<StationMenuDTO> allMenus = stationMenuMapper.selectAllMenus();
        //获取所有一级菜单
        List<StationMenuDTO> firstLevelMenus = stationMenuMapper.selectFirstLevelMenus();

        //为一级菜单设置子菜单准备递归
        for (StationMenuDTO menu : firstLevelMenus) {
            //获取父菜单下所有子菜单调用getChildList
            List<StationMenuDTO> childList = this.getChildList(menu.getId(), allMenus);
            menu.setSubMenus(childList);
        }

        return firstLevelMenus;
    }

    @Override
    public List<Integer> getChildMenuIds(Integer menuId) {
        List<StationMenuDTO> allMenus = stationMenuMapper.selectAllMenus();
        //构建子菜单
        List<Integer> subMenus = new ArrayList<>();
        subMenus.add(menuId);
        this.getChildList(menuId, allMenus, subMenus);
        return subMenus;
    }

    /**
     * 递归获取子菜单逻辑
     *
     * @param pid      父菜单ID
     * @param allMenus 所有菜单
     * @param subMenus 子菜单集合
     */
    private void getChildList(Integer pid, List<StationMenuDTO> allMenus, List<Integer> subMenus) {
        List<Integer> tempList = new ArrayList<>();
        //遍历传入的list
        ListIterator<StationMenuDTO> listIterator = allMenus.listIterator();
        while (listIterator.hasNext()) {
            StationMenuDTO menu = listIterator.next();
            //所有菜单的pid与传入的节点id比较，若相等则为该级菜单的子菜单
            if (Objects.equals(menu.getPid(), pid)) {
                tempList.add(menu.getId());
                listIterator.remove();
            }
        }
        //递归
        for (Integer id : tempList) {
            this.getChildList(id, allMenus, subMenus);
        }
        subMenus.addAll(tempList);
    }

    /**
     * 递归组合子菜单逻辑
     *
     * @param pid      父级菜单ID
     * @param allMenus 所有菜单集合
     * @return
     */
    @Override
    public List<StationMenuDTO> getChildList(Integer pid, List<StationMenuDTO> allMenus) {
    	
        //构建子菜单
        List<StationMenuDTO> subMenus = new ArrayList<>();
        //遍历传入的list
        ListIterator<StationMenuDTO> listIterator = allMenus.listIterator();
        while (listIterator.hasNext()) {
            StationMenuDTO menuDTO = listIterator.next();
            //所有菜单的pid与传入的节点id比较，若相等则为该级菜单的子菜单
            if (Objects.equals(menuDTO.getPid(), pid)) {
                subMenus.add(menuDTO);
                listIterator.remove();
            }
        }
        //递归
        for (StationMenuDTO menuDTO : subMenus) {
            menuDTO.setSubMenus(this.getChildList(menuDTO.getId(), allMenus));
        }
        if (subMenus.size() == 0) {
            return null;
        }
        return subMenus;
    }

    /**
     *  根据角色 获取想对应菜单（若角色无当前菜单权限，则不返回该菜单）
     *
     * @param roleId 角色id
     * @param permissionsType  权限类型（0-售前+售后，1-售前， 2-售后）
     * @return
     */
    @Override
    public List<StationMenuDTO> getMenuListByRoleId(Integer roleId, Integer permissionsType) {

        List<StationMenuDTO> menus = new ArrayList<>();
        if (roleId == StationConstant.SUPERROLE) {
            //查询所有
            menus = stationMenuMapper.selectAllMenus();
        } else {
            //获取该角色的权限
            List<StationPermissionCacheDTO> permission = stationRoleService.findPermissionsByRoleId(roleId);
            Iterator<StationPermissionCacheDTO> listIterator = permission.iterator();
            while (listIterator.hasNext()) {
                StationPermissionCacheDTO permissionCacheDTO = listIterator.next();
                //根据用户所拥有的权限类型，仅保留管理员所拥有的售前或售后权限
                if (permissionsType == PermissionTypeEnum.PRE_SALE.value) {
                    //仅售前，删除售后的权限
                    if (permissionCacheDTO.getType() == PermissionTypeEnum.AFTER_SALE.value) {
                        listIterator.remove();
                    }
                }else if (permissionsType == PermissionTypeEnum.AFTER_SALE.value) {
                    //仅售后，删除售前的权限
                    if (permissionCacheDTO.getType() == PermissionTypeEnum.PRE_SALE.value) {
                        listIterator.remove();
                    }
                }
            }

            for (StationPermissionCacheDTO permissionCacheDTO : permission) {
                if (menus.size() == 0) {
                    menus.add(stationMenuMapper.selectMenuById(permissionCacheDTO.getMenuId()));
                } else {
                    Boolean flag = true;
                    for (StationMenuDTO menu : menus) {
                        if (menu.getId().intValue() == permissionCacheDTO.getMenuId()) {
                            //已存在该菜单，无需再次添加
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        menus.add(stationMenuMapper.selectMenuById(permissionCacheDTO.getMenuId()));
                    }
                }
            }

            if (CollectionUtil.isEmpty(menus)) {

                return null;
            }
            // 定义父级菜单ID集合
            Set<Integer> menuIds = new HashSet<>();
            for (StationMenuDTO menuDTO : menus) {
                Integer pid = menuDTO.getPid();
                // 从子菜单一直遍历找到一级菜单，将所有菜单ID存放到menuIds中
                while (pid != 0 && !menuIds.contains(pid)) {
                    menuIds.add(pid);
                    // 根据菜单ID查询菜单pid
                    pid = stationMenuMapper.selectPidById(pid);
                }
            }
            //将父级菜单存入菜单集合
            for (Integer menuId : menuIds) {
                menus.add(stationMenuMapper.selectMenuById(menuId));
            }

            //排序
            Collections.sort(menus, new Comparator<StationMenuDTO>() {
                @Override
                public int compare(StationMenuDTO o1, StationMenuDTO o2) {
                    return o1.getSorts() - o2.getSorts();
                }
            });
        }

        //获取该角色下所有菜单
        List<StationMenuDTO> firstLevelMenus = new ArrayList<>();
        
        //过滤不需要展示的一级菜单
    	List<String> menuFilterList = menuFilterConfig.getList();
    	log.info("过滤菜单集合={}",JSON.toJSONString(menuFilterList));
    	
    	Iterator<StationMenuDTO> iterator=menus.iterator();
    	
        while (iterator.hasNext()) {
        	StationMenuDTO menuDTO=iterator.next();
        	
        	String menuUrl = menuDTO.getUrl();
        	
        	if(CollectionUtil.isNotEmpty(menuFilterList) && menuFilterList.contains(menuUrl)) {
        		iterator.remove();
        		continue;
        	}
        	
            if (menuDTO.getLevel() == 1) {           	           	
                firstLevelMenus.add(menuDTO);
            }
        }

        //为一级菜单设置子菜单准备递归
        for (StationMenuDTO menu : firstLevelMenus) {
            //获取父菜单下所有子菜单调用getChildList
            List<StationMenuDTO> childList = this.getChildList(menu.getId(), menus);
            menu.setSubMenus(childList);
        }
        return firstLevelMenus;
    }

    /**
     * 根据角色ID获取所有菜单及权限集合
     *
     * @return
     */
    @Override
    public List<StationMenuDTO> listMenuAndPermission(Integer roleId) {

        // 获取所有菜单列表（每个菜单中携带对应的权限列表）
        List<StationMenuDTO> menus = stationMenuMapper.selectMenuWithPermission();
        //剔除菜单管理和权限管理菜单
        for (int i = menus.size() - 1; i >= 0; i--) {
            if (menus.get(i).getName().equals("菜单管理") || menus.get(i).getName().equals("权限管理")) {
                menus.remove(i);
            }
        }

        // 获取一共有几级菜单
        Integer maxLevel = stationMenuMapper.selectMaxMenuLevel();

        Set<Integer> permissionIds = new HashSet<Integer>();
        // 获取该角色对应的所有权限
        if (Objects.nonNull(roleId)) {
            permissionIds = stationPermissionService.getPermissionIdsByRoleId(roleId);
        }

        if (CollectionUtil.isNotEmpty(menus)) {
            Map<Integer, List<StationMenuDTO>> map = new HashMap<>();
            // 1-将菜单分级存放
            for (int i = 1; i <= maxLevel; i++) {
                map.put(i, new ArrayList<>());
                for (StationMenuDTO menuDTO : menus) {
                    if (menuDTO.getLevel() == i) {
                        map.get(i).add(menuDTO);
                    }
                    // 菜单的权限列表不为空 && 角色拥有的权限列表不为空
                    if (CollectionUtil.isNotEmpty(menuDTO.getPermissions()) && CollectionUtil.isNotEmpty(permissionIds)) {
                        for (StationPermissionDTO permissionDTO : menuDTO.getPermissions()) {
                            // 判断菜单的权限是否被包含在角色拥有的权限列表中，如果被包含，则设置个标识（前端展示用到）
                            if (permissionIds.contains(permissionDTO.getId())) {
                                // 表示该角色拥有该权限
                                permissionDTO.setHave(true);
                                // 表示该角色拥有该菜单
                                menuDTO.setHave(true);
                            }
                        }
                    }
                }
            }
            // 2-将子菜单设置到对应的上级菜单下
            for (int i = maxLevel; i > 1; i--) {
                List<StationMenuDTO> outer = map.get(i - 1);
                List<StationMenuDTO> inner = map.get(i);
                for (StationMenuDTO outerDto : outer) {
                    List<StationMenuDTO> subMenus = new ArrayList<>();
                    for (StationMenuDTO innerDto : inner) {
                        if (Objects.equals(innerDto.getPid(), outerDto.getId())) {
                            subMenus.add(innerDto);
                        }
                        // 如果某个子菜单被该角色拥有，则上级菜单也应该被角色拥有
                        if (innerDto.getHave() != null && innerDto.getHave()) {
                            outerDto.setHave(innerDto.getHave());
                        }
                    }
                    outerDto.setSubMenus(subMenus);
                }
            }
            return map.get(1);
        }
        return null;

    }
}
