package com.yimao.cloud.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.user.CompanyCustomerDTO;
import com.yimao.cloud.pojo.dto.user.CustomerAddressDTO;
import com.yimao.cloud.pojo.dto.user.PersonCustomerDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.mapper.CompanyCustomerMapper;
import com.yimao.cloud.user.mapper.PersonCustomerMapper;
import com.yimao.cloud.user.mapper.UserAddressMapper;
import com.yimao.cloud.user.po.CompanyCustomer;
import com.yimao.cloud.user.po.PersonCustomer;
import com.yimao.cloud.user.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-09-17 15:59:39
 **/
@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private UserAddressMapper userAddressMapper;

    @Resource
    private PersonCustomerMapper personCustomerMapper;

    @Resource
    private CompanyCustomerMapper companyCustomerMapper;

    @Override
    public PageVO<CustomerAddressDTO> pageQueryCustomerAddress(Integer pageNum, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        Page<CustomerAddressDTO> page = userAddressMapper.pageQueryCustomerAddress(userId);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public void savePersonCustomer(PersonCustomerDTO personCustomerDTO) {
        PersonCustomer personCustomer = new PersonCustomer(personCustomerDTO);
        personCustomer.setCreateTime(new Date());
        personCustomer.setDeleted(false);
        if (StringUtil.isNotEmpty(personCustomer.getStreet())) {
            personCustomer.setStreet(personCustomer.getStreet().trim().replaceAll("\n", ""));
        }
        int count = personCustomerMapper.insert(personCustomer);
        if (count < 1) {
            throw new YimaoException("添加个人客户失败");
        }
    }

    @Override
    public void saveCompanyCustomer(CompanyCustomerDTO companyCustomerDTO) {
        CompanyCustomer companyCustomer = new CompanyCustomer(companyCustomerDTO);
        companyCustomer.setDeleted(false);
        companyCustomer.setCreateTime(new Date());
        if (StringUtil.isNotEmpty(companyCustomer.getStreet())) {
            companyCustomer.setStreet(companyCustomer.getStreet().trim().replaceAll("\n", ""));
        }
        int num = companyCustomerMapper.insert(companyCustomer);
        if (num < 1) {
            throw new YimaoException("添加企业客户失败");
        }
    }

    @Override
    public void updatePersonCustomer(PersonCustomerDTO personCustomerDTO) {
        PersonCustomer personCustomer = new PersonCustomer(personCustomerDTO);
        personCustomer.setUpdateTime(new Date());
        if (StringUtil.isNotEmpty(personCustomer.getStreet())) {
            personCustomer.setStreet(personCustomer.getStreet().trim().replaceAll("\n", ""));
        }
        personCustomerMapper.updateByPrimaryKeySelective(personCustomer);
    }

    @Override
    public void updateCompanyCustomer(CompanyCustomerDTO companyCustomerDTO) {
        CompanyCustomer companyCustomer = new CompanyCustomer(companyCustomerDTO);
        companyCustomer.setUpdateTime(new Date());
        if (StringUtil.isNotEmpty(companyCustomer.getStreet())) {
            companyCustomer.setStreet(companyCustomer.getStreet().trim().replaceAll("\n", ""));
        }
        companyCustomerMapper.updateByPrimaryKeySelective(companyCustomer);
    }


    @Override
    public void deletePersonCustomer(Integer id) {
        PersonCustomer personCustomer = personCustomerMapper.selectByPrimaryKey(id);
        if (null == personCustomer) {
            throw new NotFoundException("未查询到个人客户");
        }
        personCustomer.setDeleted(true);
        personCustomer.setUpdateTime(new Date());
        int i = personCustomerMapper.updateByPrimaryKeySelective(personCustomer);
        if (i < 1) {
            throw new YimaoException("个人客户删除失败");
        }
    }

    @Override
    public void deleteCompanyCustomer(Integer id) {
        CompanyCustomer companyCustomer = companyCustomerMapper.selectByPrimaryKey(id);
        if (null == companyCustomer) {
            throw new NotFoundException("未查询到企业客户");
        }
        companyCustomer.setUpdateTime(new Date());
        companyCustomer.setDeleted(true);
        int num = companyCustomerMapper.updateByPrimaryKeySelective(companyCustomer);
        if (num < 1) {
            throw new YimaoException("企业客户删除失败");
        }

    }

    @Override
    public Object queryCustomerInfo(Integer id, Integer type) {
        Object obj = null;
        if (type == 1) {
            obj = personCustomerMapper.selectByPrimaryKey(id);
        } else if (type == 2) {
            obj = companyCustomerMapper.selectByPrimaryKey(id);
        }
        return obj;
    }

}
