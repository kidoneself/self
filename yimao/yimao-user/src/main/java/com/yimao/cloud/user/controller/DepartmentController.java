package com.yimao.cloud.user.controller;

import com.yimao.cloud.pojo.dto.user.DepartmentDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.Department;
import com.yimao.cloud.user.service.DepartmentService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 部门控制器
 *
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Slf4j
@Api(tags = "DepartmentController")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    /**
     * 新增部门
     *
     * @param dto 部门信息
     * @return
     */
    @RequestMapping(value = "/dept", method = RequestMethod.POST)
    public void save(@RequestBody DepartmentDTO dto) {
        Department department = new Department(dto);
        departmentService.save(department);
    }

    /**
     * 删除某个部门
     *
     * @param id 部门ID
     */
    @RequestMapping(value = "/dept/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable("id") Integer id) {
        departmentService.delete(id);
    }

    /**
     * 更新某个部门信息
     *
     * @param dto 部门信息
     * @return
     */
    @RequestMapping(value = "/dept", method = RequestMethod.PUT)
    public void update(@RequestBody DepartmentDTO dto) {
        Department department = new Department(dto);
        departmentService.update(department);
    }

    /**
     * 查询部门列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param name     角色名
     * @return
     */
    @RequestMapping(value = "/depts/{pageNum}/{pageSize}", method = RequestMethod.GET)
    public PageVO<DepartmentDTO> page(@PathVariable(value = "pageNum") Integer pageNum,
                                      @PathVariable(value = "pageSize") Integer pageSize,
                                      @RequestParam(value = "name", required = false) String name,
                                      @RequestParam(value = "sysType") Integer sysType) {
        return departmentService.page(pageNum, pageSize, name, sysType);
    }

}
