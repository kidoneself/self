package com.yimao.cloud.user.service.impl;

import com.yimao.cloud.pojo.dto.user.AdminLogDTO;
import com.yimao.cloud.user.mapper.AdminLogMapper;
import com.yimao.cloud.user.po.AdminLog;
import com.yimao.cloud.user.service.AdminLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 管理员登录日志业务类
 *
 * @author Zhang Bo
 * @date 2018/11/13.
 */
@Service
public class AdminLogServiceImpl implements AdminLogService {

    @Resource
    private AdminLogMapper adminLogMapper;

    /**
     * 创建管理员登录日志
     *
     * @param dto 管理员登录日志
     * @return
     */
    @Override
    public AdminLogDTO save(AdminLogDTO dto) {
        AdminLog adminLog = new AdminLog(dto);
        adminLogMapper.insert(adminLog);
        dto.setId(adminLog.getId());
        return dto;
    }

    /**
     * 查询管理员登录日志
     *
     * @param query 查询条件
     * @return
     */
    @Override
    public List<AdminLogDTO> list(AdminLogDTO query) {
        return null;
    }

}
