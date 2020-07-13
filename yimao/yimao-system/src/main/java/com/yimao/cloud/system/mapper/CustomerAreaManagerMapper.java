package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.CustomerAreaManagerDTO;
import com.yimao.cloud.system.po.CustomerAreaManager;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author liuhao@yimaokeji.com
 * 2018052018/5/17
 */
public interface CustomerAreaManagerMapper extends Mapper<CustomerAreaManager> {

    /**
     * 查询区域经理
     *
     * @param province 省
     * @return page
     */
    Page<CustomerAreaManagerDTO> listCustomerAreaManager(@Param("province") String province, @Param("name") String name);
}
