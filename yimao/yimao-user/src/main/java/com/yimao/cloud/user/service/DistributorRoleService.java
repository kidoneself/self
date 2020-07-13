package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.DistributorRoleDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.DistributorRole;

import java.util.List;

public interface DistributorRoleService {

    /**
     * 新增经销商角色配置
     *
     * @param dto 经销商角色配置
     */
    void save(DistributorRoleDTO dto);

    /**
     * 禁用/启用经销商角色配置
     *
     * @param id 经销商角色ID
     */
    void forbidden(Integer id);

    /**
     * 修改经销商角色配置
     *
     * @param dto 经销商角色配置
     */
    void update(DistributorRoleDTO dto);

    /**
     * 分页查询经销商角色配置信息
     *
     * @param pageNum  页数
     * @param pageSize 每页大小
     */
    PageVO<DistributorRoleDTO> page(Integer pageNum, Integer pageSize);

    /**
     * 查询经销商角色配置信息（所有）
     */
    List<DistributorRoleDTO> listAll();

    /**
     * 查询经销商角色配置信息（根据level）
     *
     * @param level 经销商角色等级：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子）
     */
    DistributorRoleDTO getByLevel(Integer level);
}
