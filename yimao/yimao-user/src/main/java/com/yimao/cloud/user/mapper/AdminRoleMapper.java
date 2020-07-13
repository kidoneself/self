package com.yimao.cloud.user.mapper;

import com.yimao.cloud.user.po.AdminRole;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @author Zhang Bo
 * @date 2018/11/2.
 */
public interface AdminRoleMapper extends Mapper<AdminRole> {

    /**
     * 批量插入
     *
     * @param list
     */
    void batchInsert(@Param("list") List<AdminRole> list);

    /**
     * 根据管理员ID获取角色ID集合
     *
     * @param adminId 管理员ID
     * @return
     */
    Set<Integer> selectRoleIdsByAdminId(@Param("adminId") Integer adminId);
}
