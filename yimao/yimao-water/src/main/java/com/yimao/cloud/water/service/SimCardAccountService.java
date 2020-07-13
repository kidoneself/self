package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.SimCardAccountDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.SimCardAccount;

import java.util.List;

public interface SimCardAccountService {

    /**
     * 创建SIM运营商分配的权限账号
     *
     * @param simCardAccount SIM运营商分配的权限账号
     */
    void save(SimCardAccount simCardAccount);

    /**
     * 修改SIM运营商分配的权限账号
     *
     * @param simCardAccount SIM运营商分配的权限账号
     */
    void update(SimCardAccount simCardAccount);

    /**
     * 查询SIM运营商分配的权限账号（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    PageVO<SimCardAccountDTO> page(Integer pageNum, Integer pageSize);

    /**
     * 获取所有SIM运营商
     */
    List<SimCardAccountDTO> list();

}
