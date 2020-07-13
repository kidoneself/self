package com.yimao.cloud.product.mapper;


import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.product.ProductCompanyDTO;
import com.yimao.cloud.product.po.ProductCompany;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;

/**
 * 产品公司
 *
 * @author Liu Yi
 * @date 2018/11/26.
 */
public interface ProductCompanyMapper extends Mapper<ProductCompany> {

    /**
     * 分页查询
     *
     * @param code
     * @param name
     * @param createTime
     * @param endTime
     * @return
     */
    Page<ProductCompanyDTO> listProductCompany(@Param("code") String code,
                                               @Param("name") String name,
                                               @Param("startTime") Date createTime,
                                               @Param("endTime") Date endTime);

    /**
     * 删除产品公司
     * @param id
     * @return
     */
    Integer deleteProductCompany(@Param("id") Integer id);
}
