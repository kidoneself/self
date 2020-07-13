package com.yimao.cloud.hra.service;

import com.yimao.cloud.hra.po.HraCustomer;
import com.yimao.cloud.pojo.dto.hra.HraCustomerDTO;

/**
 * @author Zhang Bo
 * @date 2018/10/31.
 */
public interface HraCustomerService {

    HraCustomerDTO findHraCustomer(Integer customerId);

    HraCustomerDTO update(HraCustomer customer);

    HraCustomerDTO save(HraCustomer customer);

    Integer deleteHraCustomer(Integer customerId);

}
