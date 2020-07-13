package com.yimao.cloud.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.AdminTypeEnum;
import com.yimao.cloud.base.enums.SexType;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.handler.JWTHandler;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.IpUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.aop.OperationType;
import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;
import com.yimao.cloud.framework.aop.dto.OperationLogDTO;
import com.yimao.cloud.framework.aop.parser.DefaultContentParse;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.user.AdminDTO;
import com.yimao.cloud.pojo.dto.user.AdminLogDTO;
import com.yimao.cloud.pojo.dto.user.MenuDTO;
import com.yimao.cloud.pojo.dto.user.PermissionCacheDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.enums.LogErrorCauseEnum;
import com.yimao.cloud.user.mapper.AdminMapper;
import com.yimao.cloud.user.mapper.AdminRoleMapper;
import com.yimao.cloud.user.mapper.DepartmentMapper;
import com.yimao.cloud.user.mapper.MenuMapper;
import com.yimao.cloud.user.mapper.PermissionMapper;
import com.yimao.cloud.user.po.Admin;
import com.yimao.cloud.user.po.AdminRole;
import com.yimao.cloud.user.service.AdminService;
import com.yimao.cloud.user.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 管理员信息业务类
 *
 * @author Zhang Bo
 * @date 2018/10/30.
 */
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Resource
    private UserCache userCache;
    @Resource
    private RedisCache redisCache;
    @Resource
    private JWTHandler jwtHandler;
    @Resource
    private AdminMapper adminMapper;
    @Resource
    private MenuService menuService;
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private AdminRoleMapper adminRoleMapper;
    @Resource
    private AmqpTemplate amqpTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 管理员登录
     *
     * @param userName 用户名
     * @param password 密码
     * @param sysType  所属系统
     * @param request  HTTP请求
     */
    @Override
    public AdminDTO login(String userName, String password, Integer sysType, HttpServletRequest request) {
        Admin query = new Admin();
        query.setUserName(userName);
        query.setSysType(sysType);
        Admin admin = adminMapper.selectOne(query);
        if (admin == null) {
            throw new BadRequestException("该账号不存在。");
        }
        AdminLogDTO adminLog = new AdminLogDTO();
        adminLog.setUserName(admin.getUserName());
        adminLog.setRealName(admin.getRealName());
        adminLog.setType(AdminTypeEnum.SYSTEM.value);
        adminLog.setTime(new Date());
        adminLog.setIp(IpUtil.getIp(request));

        //禁用校验
        if (admin.getForbidden()) {
            adminLog.setIsSuccess(false);
            adminLog.setCause(LogErrorCauseEnum.ACCOUNT_LOCKOUT.msg());
            // 记录登录日志
            amqpTemplate.convertAndSend(RabbitConstant.ADMIN_LOG, adminLog);
            throw new BadRequestException("该账号已被禁用，请联系管理员。");
        }


        if (SystemType.SYSTEM.value == sysType) {
            checkSystemParam(userName, password, admin, adminLog);
        } else {
            //密码校验
            if (!admin.getPassword().equalsIgnoreCase(password)) {
                adminLog.setIsSuccess(false);
                adminLog.setCause(LogErrorCauseEnum.PASSWORD_ERROR.msg());
                // 记录登录日志
                amqpTemplate.convertAndSend(RabbitConstant.ADMIN_LOG, adminLog);
                throw new BadRequestException("密码错误。");
            }
        }

        JWTInfo jwtInfo = new JWTInfo();
        jwtInfo.setId(admin.getId());
        jwtInfo.setRealName(admin.getRealName());
        jwtInfo.setType(sysType);

        adminLog.setIsSuccess(true); //登录成功
        // 记录登录日志
        amqpTemplate.convertAndSend(RabbitConstant.ADMIN_LOG, adminLog);

        // 每次登录的时候删除一次缓存
        // redisCache.delete(Constant.ADMIN_CACHE + admin.getId());

        String token = jwtHandler.createJWTToken(jwtInfo);
        AdminDTO adminDTO = this.getFullAdminDTOById(admin);
        adminDTO.setToken(token);
        if (SystemType.SYSTEM.value == sysType) {
            //清除登录失败信息
            redisCache.delete(Constant.SYSTEM_LOGIN_FAIL + userName);
        }

        return adminDTO;
    }

    public void checkSystemParam(String userName, String password, Admin admin, AdminLogDTO adminLog) {
        //10分钟内连续登录失败5次，账户锁定10分钟
        String failNum = redisCache.get(Constant.SYSTEM_LOGIN_FAIL + userName);
        Integer failNumTemp;
        if (StringUtil.isBlank(failNum)) {
            failNumTemp = 0;
            //第一次登陆添加缓存开始10分钟计时（如果第一次就登录成功则自动删除缓存）
            redisCache.set(Constant.SYSTEM_LOGIN_FAIL + userName, failNumTemp, 600L);
        } else {
            failNumTemp = Integer.parseInt(failNum);
            if (failNumTemp >= 5) {
                throw new BadRequestException("当前登录失败次数已达到5次，账户已锁定，请稍后重试。");
            }
        }
        if (!admin.getPassword().equalsIgnoreCase(password)) {
            int expire = redisCache.getExpire(Constant.SYSTEM_LOGIN_FAIL + userName);
            log.info("登录失败次数缓存剩余时间为{}秒", expire);
            //判断10分钟内登录失败次数是否达到5次数，达到次数返回账户已锁定，账户10分钟内不能再次登录
            if (failNumTemp == 4) {
                redisCache.set(Constant.SYSTEM_LOGIN_FAIL + userName, failNumTemp + 1, 600L);
                adminLog.setIsSuccess(false);
                adminLog.setCause(LogErrorCauseEnum.ACCOUNT_LOCKOUT.msg());
                //登录失败，记录登录日志
                amqpTemplate.convertAndSend(RabbitConstant.ADMIN_LOG, adminLog);
                throw new BadRequestException("当前登录失败次数已达到5次，账户已锁定，请稍后重试。");
            } else {
                //未达到5次
                redisCache.set(Constant.SYSTEM_LOGIN_FAIL + userName, failNumTemp + 1, expire);
                adminLog.setIsSuccess(false);
                adminLog.setCause(LogErrorCauseEnum.PASSWORD_ERROR.msg());
                //登录失败，记录登录日志
                amqpTemplate.convertAndSend(RabbitConstant.ADMIN_LOG, adminLog);
                throw new BadRequestException("密码输入错误，您还剩" + (5 - (failNumTemp + 1)) + "次机会，连续输错后系统将锁定账户10分钟！");
            }
        }
    }

    /**
     * 获取管理员信息，并设置相关额外属性
     *
     * @param adminId 管理员ID
     */
    @Override
    public AdminDTO getFullAdminDTOById(Integer adminId) {
        // 先从缓存中获取管理员信息
        // AdminDTO adminDTO = redisCache.get(Constant.ADMIN_CACHE + adminId, AdminDTO.class);
        // if (Objects.nonNull(adminDTO)) {
        //     return adminDTO;
        // }
        Admin admin = adminMapper.selectByPrimaryKey(adminId);
        return this.getFullAdminDTOById(admin);
    }

    /**
     * 获取管理员信息，并设置相关额外属性
     *
     * @param admin 管理员信息
     */
    @Override
    public AdminDTO getFullAdminDTOById(Admin admin) {
        // 先从缓存中获取用户信息
        // AdminDTO adminDTO = redisCache.get(Constant.ADMIN_CACHE + admin.getId(), AdminDTO.class);
        // if (Objects.nonNull(adminDTO)) {
        //     return adminDTO;
        // }

        AdminDTO adminDTO = new AdminDTO();
        admin.convert(adminDTO);

        // 获取管理员所拥有的菜单列表
        List<MenuDTO> menus = this.listMenusByAdminId(admin.getId());
        if (CollectionUtil.isEmpty(menus)) {
            throw new YimaoException("该管理员没有可用菜单。");
        }
        adminDTO.setMenus(menus);
        // 将管理员信息设置到缓存中
        // amqpTemplate.convertAndSend(RabbitConstant.ADMIN_UPDATE, adminDTO);
        return adminDTO;
    }

    /**
     * 创建管理员
     *
     * @param admin   管理员信息
     * @param roleIds 角色ID集合
     */
    @EnableOperationLog(
            name = "创建管理员",
            type = OperationType.SAVE,
            daoClass = AdminMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"userName", "realName", "phone"},
            index = 0,
            queue = RabbitConstant.SYSTEM_OPERATION_LOG
    )
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void save(Admin admin, Set<Integer> roleIds) {
        //参数校验
        this.checkParams(admin, roleIds);
        // 校验密码
        if (StringUtil.isBlank(admin.getPassword())) {
            throw new BadRequestException("密码不能为空。");
        }
        // 校验用户名是否已被使用
        Admin query = new Admin();
        query.setUserName(admin.getUserName());
        query.setSysType(admin.getSysType());
        int count = adminMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("用户名已存在。");
        }
        // 设置创建人
        admin.setCreator(userCache.getCurrentAdminRealName());
        // 设置创建时间
        admin.setCreateTime(new Date());
        // 设置禁用状态为不禁用（默认）
        admin.setForbidden(false);
        admin.setId(null);
        int result = adminMapper.insert(admin);
        if (result < 1) {
            throw new YimaoException("操作失败。");
        }
        //保存管理员-角色映射关系
        this.saveAdminRole(admin.getId(), roleIds);
    }

    /**
     * 删除管理员
     *
     * @param admin 管理员信息（仅有id）
     * @param request
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void delete(Admin admin, HttpServletRequest request) {
        Admin query = adminMapper.selectByPrimaryKey(admin.getId());
        if (query == null) {
            throw new NotFoundException("未找到管理员信息。");
        }
        adminMapper.deleteByPrimaryKey(admin.getId());
        //添加操作日志
        OperationLogDTO operationLog = new OperationLogDTO();
        operationLog.setOperationPage("删除管理员");
        operationLog.setOperationIp(IpUtil.getIp(request));
        operationLog.setOperationType(OperationType.DELETE.name);
        Map<String, Object> fieldValues = new HashMap<>();
        fieldValues.put("id", query.getId());
        fieldValues.put("userName", query.getUserName());
        fieldValues.put("realName", query.getRealName());
        operationLog.setOperationObject(JSON.toJSONString(fieldValues));
        operationLog.setOperator(userCache.getCurrentAdminRealName());
        operationLog.setOperationDate(new Date());
        rabbitTemplate.convertAndSend(RabbitConstant.SYSTEM_OPERATION_LOG, operationLog);

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminId(admin.getId());
        adminRoleMapper.delete(adminRole);
    }

    /**
     * 禁用/启用管理员账号
     *
     * @param admin 管理员信息（仅有id）
     */
    @EnableOperationLog(
            name = "禁用/启用管理员账号",
            type = OperationType.UPDATE,
            daoClass = AdminMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id"},
            index = 0,
            queue = RabbitConstant.SYSTEM_OPERATION_LOG
    )
    @Override
    public void forbidden(Admin admin) {
        Admin record = adminMapper.selectByPrimaryKey(admin.getId());
        if (record == null) {
            throw new NotFoundException("未找到管理员信息。");
        }
        Admin update = new Admin();
        update.setId(record.getId());
        // 设置更新人
        update.setUpdater(userCache.getCurrentAdminRealName());
        // 设置更新时间
        update.setUpdateTime(new Date());
        if (record.getForbidden()) {
            // 设置禁用状态为false
            update.setForbidden(false);
        } else {
            // 设置禁用状态为true
            update.setForbidden(true);
        }
        adminMapper.updateByPrimaryKeySelective(update);
    }

    /**
     * 更新管理员账号
     *
     * @param admin   管理员信息
     * @param roleIds 角色ID集合
     */
    @EnableOperationLog(
            name = "更新管理员账号",
            type = OperationType.UPDATE,
            daoClass = AdminMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id", "userName", "realName", "phone"},
            index = 0,
            queue = RabbitConstant.SYSTEM_OPERATION_LOG
    )
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void update(Admin admin, Set<Integer> roleIds) {
        Admin record = adminMapper.selectByPrimaryKey(admin.getId());
        if (record == null) {
            throw new BadRequestException("修改对象不存在。");
        }
        // 校验用户名是否已被使用
        Example example = new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("id", admin.getId());
        criteria.andEqualTo("userName", admin.getUserName());
        criteria.andEqualTo("sysType", record.getSysType());
        int count = adminMapper.selectCountByExample(example);
        if (count > 0) {
            throw new BadRequestException("用户名已存在。");
        }
        if (StringUtil.isBlank(admin.getPassword()) || record.getPassword().equalsIgnoreCase(admin.getPassword())) {
            //不更新密码
            admin.setPassword(null);
        }
        // 设置更新人
        admin.setUpdater(userCache.getCurrentAdminRealName());
        // 设置更新时间
        admin.setUpdateTime(new Date());
        //不更新sysType
        admin.setSysType(null);
        int i = adminMapper.updateByPrimaryKeySelective(admin);
        if (i < 1) {
            throw new YimaoException("修改失败。");
        }
        //如果前端传递的参数中角色ID集合不为空，则需要变更管理员的角色信息
        if (CollectionUtil.isNotEmpty(roleIds)) {
            //保存管理员-角色映射关系
            this.saveAdminRole(admin.getId(), roleIds);
        }
    }

    /**
     * 根据ID查询管理员信息
     *
     * @param id 管理员ID
     */
    @Override
    public Admin getById(Integer id) {
        Admin query = new Admin();
        query.setId(id);
        query.setForbidden(false);
        return adminMapper.selectOne(query);
    }

    /**
     * 分页查询管理员信息，可带查询条件
     *
     * @param pageNum   第几页
     * @param pageSize  每页大小
     * @param sysType   所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；
     * @param roleId    角色ID
     * @param deptId    部门ID
     * @param realName  姓名
     * @param forbidden 禁用状态
     */
    @Override
    public PageVO<AdminDTO> page(int pageNum, int pageSize, Integer sysType, Integer roleId, Integer deptId, String realName, Boolean forbidden) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<AdminDTO> page = adminMapper.selectWithRole(sysType, roleId, deptId, realName, forbidden);
        // PO对象转成DTO对象
        return new PageVO<>(pageNum, page);
    }

    /**
     * 为管理员添加角色
     *
     * @param adminId 管理员ID
     * @param roleIds 角色ID集合
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void addRoles(Integer adminId, Set<Integer> roleIds) {
        if (adminId == null) {
            throw new BadRequestException("管理员ID不能为空。");
        }
        //保存管理员-角色映射关系
        this.saveAdminRole(adminId, roleIds);
    }

    /**
     * 获取管理员所拥有的菜单
     *
     * @param id 管理员ID
     */
    @Override
    public List<MenuDTO> listMenusByAdminId(Integer id) {
        //首先获取该管理员的所有菜单集合
        List<MenuDTO> menus = menuMapper.selectMenusByAdminId(id);

        //获取该角色下所有菜单
        List<MenuDTO> firstLevelMenus = new ArrayList<>();
        for (MenuDTO menuDTO : menus) {
            if (menuDTO.getLevel() == 1) {
                firstLevelMenus.add(menuDTO);
            }
        }

        //为一级菜单设置子菜单准备递归
        for (MenuDTO menu : firstLevelMenus) {
            //获取父菜单下所有子菜单调用getChildList
            List<MenuDTO> childList = menuService.getChildList(menu.getId(), menus);
            menu.setSubMenus(childList);
        }
        return firstLevelMenus;
    }

    /**
     * 获取管理员权限列表
     *
     * @param id 管理员ID
     */
    @Override
    public Set<PermissionCacheDTO> listPermissionsByAdminId(Integer id) {
        Set<Integer> roleIds = adminRoleMapper.selectRoleIdsByAdminId(id);
        if (CollectionUtil.isEmpty(roleIds)) {
            return null;
        }
        Set<PermissionCacheDTO> cachePermissions = new HashSet<>();
        for (Integer roleId : roleIds) {
            List<PermissionCacheDTO> tempList = redisCache.getCacheList(Constant.ROLE_PERMISSIONS + roleId, PermissionCacheDTO.class);
            if (CollectionUtil.isEmpty(tempList)) {
                tempList = permissionMapper.selectCodeAndMethodByRoleId(roleId);
                redisCache.setCacheList(Constant.ROLE_PERMISSIONS + roleId, tempList, PermissionCacheDTO.class);
            }
            if (CollectionUtil.isNotEmpty(tempList)) {
                cachePermissions.addAll(tempList);
            }
        }

        return cachePermissions;
    }

    /**
     * 参数校验
     *
     * @param admin   管理员信息
     * @param roleIds 角色ID集合
     */
    private void checkParams(Admin admin, Set<Integer> roleIds) {
        // 校验用户名
        if (StringUtil.isBlank(admin.getUserName())) {
            throw new BadRequestException("用户名不能为空。");
        }
        // 校验姓名
        if (StringUtil.isBlank(admin.getRealName())) {
            throw new BadRequestException("姓名不能为空。");
        }
        // 校验性别
        if (admin.getSex() == null || SexType.find(admin.getSex()) == null) {
            throw new BadRequestException("性别填写错误。");
        }
        // // 校验手机号
        // if (StringUtil.isBlank(admin.getPhone())) {
        //     throw new BadRequestException("手机号不能为空。");
        // }
        // // 校验邮箱
        // if (StringUtil.isBlank(admin.getEmail())) {
        //     throw new BadRequestException("邮箱不能为空。");
        // }
        // 校验部门
        if (admin.getDeptId() == null) {
            throw new BadRequestException("部门不能为空。");
        }
        boolean exists = departmentMapper.existsWithPrimaryKey(admin.getDeptId());
        if (!exists) {
            throw new BadRequestException("部门填写错误。");
        }
        if (CollectionUtil.isEmpty(roleIds)) {
            throw new BadRequestException("角色不能为空。");
        }
    }

    /**
     * 保存管理员-角色映射关系
     *
     * @param adminId 管理员ID
     * @param roleIds 角色ID集合
     */
    private void saveAdminRole(Integer adminId, Set<Integer> roleIds) {
        //1-删除旧的管理员-角色映射
        AdminRole adminRole = new AdminRole();
        adminRole.setAdminId(adminId);
        adminRoleMapper.delete(adminRole);

        //2-保存新的管理员-角色映射关系
        List<AdminRole> list = new ArrayList<>();
        for (Integer roleId : roleIds) {
            adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId);
            list.add(adminRole);
        }
        // 批量插入菜单权限映射
        adminRoleMapper.batchInsert(list);
    }

}
