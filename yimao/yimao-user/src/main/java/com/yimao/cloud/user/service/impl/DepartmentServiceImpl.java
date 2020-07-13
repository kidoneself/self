package com.yimao.cloud.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.user.DepartmentDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.mapper.AdminMapper;
import com.yimao.cloud.user.mapper.DepartmentMapper;
import com.yimao.cloud.user.po.Admin;
import com.yimao.cloud.user.po.Department;
import com.yimao.cloud.user.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * 部门业务类
 *
 * @author Zhang Bo
 * @date 2018/11/13.
 */
@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private UserCache userCache;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private AdminMapper adminMapper;

    /**
     * 新增部门
     *
     * @param department 部门信息
     */
    @Override
    public void save(Department department) {
        department.setCreator(userCache.getCurrentAdminRealName());
        department.setCreateTime(new Date());
        department.setUpdater(department.getCreator());
        department.setUpdateTime(department.getCreateTime());
        departmentMapper.insert(department);
    }

    /**
     * 删除某个部门
     *
     * @param id 部门ID
     */
    @Override
    public void delete(Integer id) {
        Department department = departmentMapper.selectByPrimaryKey(id);
        Admin adminQuery = new Admin();
        adminQuery.setDeptId(id);
        if (Objects.isNull(department)) {
            throw new BadRequestException("没有查询到部门信息。");
        } else {
            adminQuery.setSysType(department.getSysType());
        }
        int count = adminMapper.selectCount(adminQuery);
        if (count > 0) {
            throw new YimaoException("该部门下尚有用户存在，不能删除。");
        }
        departmentMapper.deleteByPrimaryKey(id);
    }

    /**
     * 更新某个部门信息
     *
     * @param department 部门信息
     */
    @Override
    public void update(Department department) {
        department.setUpdater(userCache.getCurrentAdminRealName());
        department.setUpdateTime(new Date());
        departmentMapper.updateByPrimaryKey(department);
    }

    /**
     * 查询某个部门信息
     *
     * @param id 部门ID
     * @return
     */
    @Override
    public Department getById(Integer id) {
        Department department = departmentMapper.selectByPrimaryKey(id);
        if (department == null) {
            throw new BadRequestException("没有查询到产品信息。");
        }
        return department;
    }

    /**
     * 分页查询部门信息，可带查询条件
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param name     部门名称
     * @return
     */
    @Override
    public PageVO<DepartmentDTO> page(Integer pageNum, Integer pageSize, String name, Integer sysType) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<DepartmentDTO> page = departmentMapper.page(name, sysType);
        return new PageVO<>(pageNum, page);
    }

}
