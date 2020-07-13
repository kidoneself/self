package com.yimao.cloud.user.service.impl;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.user.mapper.*;
import com.yimao.cloud.user.po.*;
import com.yimao.cloud.user.service.UserCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserCompanyServiceImpl implements UserCompanyService {

    @Resource
    private UserCompanyMapper userCompanyMapper;

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserCompany
     * @description 根据ID查询经企业详情
     * @author Liu Yi
     * @date 2019/8/23 14:26
     */
    public UserCompany getCompanyById(Integer id) {
        if (id == null) {
            throw new BadRequestException("id必须传参!");
        }
        UserCompany userCompany = userCompanyMapper.selectByPrimaryKey(id);
        return userCompany;
    }

    /**
     * @param
     * @return void
     * @description 新增公司信息
     * @author Liu Yi
     * @date 2019/8/21 17:42
     */
    @Override
    public void insert(UserCompany userCompany) {
        if (userCompany.getCompanyType() == null) {
            throw new BadRequestException("公司类型不能为空！");
        }
        if (userCompany.getIndustry() == null) {
            throw new BadRequestException("请选择行业类型！");
        }
        if (StringUtil.isBlank(userCompany.getCompanyName())) {
            throw new BadRequestException("请填写公司名称！");
        }
        if (StringUtil.isBlank(userCompany.getCreditCode())) {
            throw new BadRequestException("请填写信用代码！");
        }
        if (StringUtil.isBlank(userCompany.getTaxInformation())) {
            throw new BadRequestException("请填写税务信息！");
        }
        if (StringUtil.isBlank(userCompany.getCorporateRepresentative())) {
            throw new BadRequestException("请填写法人代表！");
        }
        if (StringUtil.isBlank(userCompany.getBankAccount())) {
            throw new BadRequestException("请填写银行账号！");
        }
        if (StringUtil.isBlank(userCompany.getBank())) {
            throw new BadRequestException("请填写开户银行！");
        }
        if (StringUtil.isBlank(userCompany.getPhone())) {
            throw new BadRequestException("请填写联系电话！");
        }
        if (StringUtil.isBlank(userCompany.getEmail())) {
            throw new BadRequestException("请填写公司邮箱！");
        }
        if (StringUtil.isBlank(userCompany.getAddress())) {
            throw new BadRequestException("请填写公司地址！");
        }
        if (StringUtil.isBlank(userCompany.getBusinessLicense())) {
            throw new BadRequestException("请填写营业执照！");
        }

        userCompanyMapper.insert(userCompany);
    }

}
