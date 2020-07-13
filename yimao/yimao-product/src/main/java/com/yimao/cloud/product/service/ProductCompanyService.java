package com.yimao.cloud.product.service;

import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductCompanyVO;
import com.yimao.cloud.product.po.ProductCompany;

import java.util.Date;

/**
 * 产品公司服务
 *
 * @author Liu Yi
 * @date 2018/11/26.
 */
public interface ProductCompanyService {

    /**
     * 创建产品公司信息
     *
     * @param productCompany 产品公司信息
     */
    void saveProductCompany(ProductCompany productCompany);

    /**
     * 更新产品公司信息
     *
     * @param productCompany 产品公司信息
     */
    void update(ProductCompany productCompany);

    /**
     * 根据ID查询产品公司信息
     *
     * @param id 产品公司ID
     */
    ProductCompany getProductCompanyById(Integer id);

    /**
     * 删除产品公司信息
     *
     * @param id 管理员ID
     */
    void deleteProductCompanyById(Integer id);

    /**
     * 查询产品公司信息列表
     *
     * @param pageNum   第几页
     * @param pageSize  每页大小
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param name      公司名称
     * @param code      公司编码
     */
    PageVO<ProductCompanyVO> listProductCompany(Integer pageNum, Integer pageSize, String code, String name, Date startTime, Date endTime);
}
