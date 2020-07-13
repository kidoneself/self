package com.yimao.cloud.user.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.user.DepartmentDTO;
import com.yimao.cloud.user.po.Department;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Zhang Bo
 * @date 2018/10/30.
 */
public interface DepartmentMapper extends Mapper<Department> {

    Page<DepartmentDTO> page(@Param("name") String name,
                             @Param("sysType") Integer sysType);

}
