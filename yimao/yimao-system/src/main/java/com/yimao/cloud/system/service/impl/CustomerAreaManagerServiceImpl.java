package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.system.CustomerAreaManagerDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.CustomerAreaManagerMapper;
import com.yimao.cloud.system.po.CustomerAreaManager;
import com.yimao.cloud.system.service.CustomerAreaManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author liuhao@yimaokeji.com
 * 2018052018/5/17
 */
@Service
@Slf4j
public class CustomerAreaManagerServiceImpl implements CustomerAreaManagerService {

    @Resource
    private UserCache userCache;
    @Resource
    private CustomerAreaManagerMapper customerAreaManagerMapper;


    /**
     * 分页查询区域经理列表
     *
     * @param province 省
     * @param name
     * @param pageNum  页码
     * @param pageSize 页数
     * @return
     */
    @Override
    public PageVO<CustomerAreaManagerDTO> listCustomerAreaManager(String province, String name, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<CustomerAreaManagerDTO> areaManagers = customerAreaManagerMapper.listCustomerAreaManager(province, name);
        return new PageVO<>(pageNum, areaManagers);
    }

    /**
     * 新增区域经理
     *
     * @param customerAreaManager
     * @return
     */
    @Override
    public CustomerAreaManager saveCustomerAreaManager(CustomerAreaManager customerAreaManager) {
        if (StringUtil.isEmpty(customerAreaManager.getProvince()) || StringUtil.isEmpty(customerAreaManager.getTechnicalName())) {
            throw new BadRequestException("缺少必填信息");
        }

        CustomerAreaManager query = new CustomerAreaManager();
        query.setProvince(customerAreaManager.getProvince());
        CustomerAreaManager querydb = customerAreaManagerMapper.selectOne(query);
        if (querydb != null) {
            throw new BadRequestException("此省已存在省级经理");
        }

        customerAreaManager.setUpdateTime(new Date());
        customerAreaManager.setCreateTime(new Date());

        String creator = userCache.getCurrentAdminRealName();
        customerAreaManager.setCreator(creator);
        customerAreaManager.setUpdater(creator);
        customerAreaManager.setDeleteFlag(false);
        customerAreaManagerMapper.insertSelective(customerAreaManager);
        return customerAreaManager;
    }

    /**
     * 修改区域经理
     *
     * @param customerAreaManager
     * @return
     */
    @Override
    public CustomerAreaManager updateCustomerAreaManager(CustomerAreaManager customerAreaManager) {
        if (StringUtil.isEmpty(customerAreaManager.getProvince()) || StringUtil.isEmpty(customerAreaManager.getTechnicalName())) {
            throw new BadRequestException("缺少必填信息");
        }

        CustomerAreaManager cam = existsProvinceCustomer(customerAreaManager.getProvince());
        if (cam != null) {
            if (!Objects.equals(customerAreaManager.getId(), cam.getId())) {
                throw new BadRequestException("此省已存在省级经理");
            }
        }
        customerAreaManager.setUpdateTime(new Date());
        String updater = userCache.getCurrentAdminRealName();
        customerAreaManager.setUpdater(updater);
        customerAreaManagerMapper.updateByPrimaryKeySelective(customerAreaManager);
        return customerAreaManager;
    }

    /**
     * 删除区域经理
     *
     * @param id 对象
     */
    @Override
    public void deleteCustomerAreaManager(Integer id) {
        customerAreaManagerMapper.deleteByPrimaryKey(id);
    }


    /**
     * 查看单个区域经理
     *
     * @param id
     * @return
     */
    @Override
    public CustomerAreaManager listCustomerAreaManager(Integer id) {
        CustomerAreaManager customerAreaManager = customerAreaManagerMapper.selectByPrimaryKey(id);
        if (customerAreaManager == null) {
            throw new YimaoException("未找到该区域经理信息");
        }
        return customerAreaManager;
    }

    /**
     * 是否存在省级经理
     */
    private CustomerAreaManager existsProvinceCustomer(String province) {
        Example example = new Example(CustomerAreaManager.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("province", province);
        List<CustomerAreaManager> customerAreaManagers = customerAreaManagerMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(customerAreaManagers)) {
            return customerAreaManagers.get(0);
        }
        return null;
    }
}
