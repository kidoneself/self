package com.yimao.cloud.user.service;


import com.yimao.cloud.pojo.dto.user.DepartmentDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.Department;

/**
 * 部门业务类
 *
 * @author Zhang Bo
 * @date 2018/8/15.
 */
public interface DepartmentService {

    /**
     * 新增部门
     *
     * @param department 部门信息
     */
    void save(Department department);

    /**
     * 删除某个部门
     *
     * @param id 部门ID
     */
    void delete(Integer id);

    /**
     * 更新某个部门信息
     *
     * @param department 部门信息
     */
    void update(Department department);

    /**
     * 查询某个部门信息
     *
     * @param id 部门ID
     * @return
     */
    Department getById(Integer id);

    /**
     * 分页查询部门信息，可带查询条件
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param name     部门名称
     * @return
     */
    PageVO<DepartmentDTO> page(Integer pageNum, Integer pageSize, String name, Integer sysType);
}
