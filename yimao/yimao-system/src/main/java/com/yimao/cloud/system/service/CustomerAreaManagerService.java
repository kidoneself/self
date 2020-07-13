package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.CustomerAreaManagerDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.CustomerAreaManager;

/**
 * @author liuhao@yimaokeji.com
 * 2018052018/5/17
 */
public interface CustomerAreaManagerService {

    /**
     * 管理后台：查询区域经理信息
     *
     * @param province 省
     * @param pageNum  页码
     * @param pageSize 页数
     * @return page
     */
    PageVO<CustomerAreaManagerDTO> listCustomerAreaManager(String province, String name, Integer pageNum, Integer pageSize);

    /**
     * @param customerAreaManager
     * @return
     */
    CustomerAreaManager saveCustomerAreaManager(CustomerAreaManager customerAreaManager);

    /**
     * 修改区域经理
     *
     * @param customerAreaManager
     * @return
     */
    CustomerAreaManager updateCustomerAreaManager(CustomerAreaManager customerAreaManager);

    /**
     * 管理后台：删除区域经理信息
     *
     * @param id 对象
     */
    void deleteCustomerAreaManager(Integer id);

    /**
     * 查看单个区域经理
     *
     * @param id
     * @return
     */
    CustomerAreaManager listCustomerAreaManager(Integer id);
}
