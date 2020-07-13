package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.CompanyCustomerDTO;
import com.yimao.cloud.pojo.dto.user.CustomerAddressDTO;
import com.yimao.cloud.pojo.dto.user.PersonCustomerDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;

public interface CustomerService {

    PageVO<CustomerAddressDTO> pageQueryCustomerAddress(Integer pageNum, Integer pageSize, Integer userId);

    void savePersonCustomer(PersonCustomerDTO personCustomerDTO);

    void saveCompanyCustomer(CompanyCustomerDTO companyCustomerDTO);

    void updatePersonCustomer(PersonCustomerDTO personCustomerDTO);

    void updateCompanyCustomer(CompanyCustomerDTO companyCustomerDTO);

    void deletePersonCustomer(Integer id);

    void deleteCompanyCustomer(Integer id);

    Object queryCustomerInfo(Integer id, Integer type);

}
