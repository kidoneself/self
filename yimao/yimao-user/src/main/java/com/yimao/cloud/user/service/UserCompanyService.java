package com.yimao.cloud.user.service;

import com.yimao.cloud.user.po.UserCompany;


public interface UserCompanyService {
    /**
     * @description   新增公司信息
     * @author Liu Yi
     * @date 2019/8/21 17:42
     * @param
     * @return void
     */
    void insert(UserCompany userCompany);

    /**
     * @description   根据ID查询经企业详情
     * @author Liu Yi
     * @date 2019/8/23 14:26
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserCompany
     */
    UserCompany getCompanyById(Integer id);

}
