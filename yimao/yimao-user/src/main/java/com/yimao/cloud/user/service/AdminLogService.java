package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.AdminLogDTO;

import java.util.List;

/**
 * 管理员登录日志业务类
 *
 * @author Zhang Bo
 * @date 2018/10/30.
 */
public interface AdminLogService {

    /**
     * 创建管理员登录日志
     *
     * @param dto 管理员登录日志
     * @return
     */
    AdminLogDTO save(AdminLogDTO dto);

    /**
     * 查询管理员登录日志
     *
     * @param query 查询条件
     * @return
     */
    List<AdminLogDTO> list(AdminLogDTO query);

}
