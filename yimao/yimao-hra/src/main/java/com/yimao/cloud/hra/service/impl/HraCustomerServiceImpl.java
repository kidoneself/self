package com.yimao.cloud.hra.service.impl;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.hra.mapper.HraCustomerMapper;
import com.yimao.cloud.hra.po.HraCustomer;
import com.yimao.cloud.hra.service.HraCustomerService;
import com.yimao.cloud.pojo.dto.hra.HraCustomerDTO;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * created by liuhao@yimaokeji.com
 * 2017122017/12/13
 */
@Service
public class HraCustomerServiceImpl implements HraCustomerService {

    @Resource
    private HraCustomerMapper hraCustomerMapper;

    @Override
    public HraCustomerDTO findHraCustomer(Integer customerId) {
        HraCustomerDTO hraCustomerDTO = new HraCustomerDTO();
        Example example = new Example(HraCustomer.class);
        example.createCriteria().andEqualTo("id", customerId);
        List<HraCustomer> hraList = hraCustomerMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(hraList)) {
            HraCustomer customer = hraList.get(0);
            customer.convert(hraCustomerDTO);
            return hraCustomerDTO;
        }
        return null;
    }

    @Override
    public HraCustomerDTO update(HraCustomer customer) {
        HraCustomerDTO hraCustomerDTO = new HraCustomerDTO();
        int count = hraCustomerMapper.updateByPrimaryKey(customer);
        customer.convert(hraCustomerDTO);
        return count > 0 ? hraCustomerDTO : null;
    }

    @Override
    public HraCustomerDTO save(HraCustomer customer) {
        HraCustomerDTO hraCustomerDTO = new HraCustomerDTO();
        int count = hraCustomerMapper.insertSelective(customer);
        customer.convert(hraCustomerDTO);
        return count > 0 ? hraCustomerDTO : null;
    }

    @Override
    public Integer deleteHraCustomer(Integer customerId) {
        return hraCustomerMapper.deleteByPrimaryKey(customerId);
    }

}
