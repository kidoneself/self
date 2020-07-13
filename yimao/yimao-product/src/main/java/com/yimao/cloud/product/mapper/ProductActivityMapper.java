package com.yimao.cloud.product.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.product.ProductActivityDTO;
import com.yimao.cloud.pojo.query.product.ProductActivityQuery;
import com.yimao.cloud.pojo.vo.product.ProductActivityVO;
import com.yimao.cloud.product.po.ProductActivity;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/***
 * 活动产品mapper
 * @author zhangbaobao
 * @date 2020/3/12
 */
public interface ProductActivityMapper extends Mapper<ProductActivity> {

    /***
     * 查询有效的活动产品列表
     * @return
     */
    Page<ProductActivityVO> listProductActivity(ProductActivityQuery query);

    /***
     * 查询单个活动产品
     * @param query
     * @return
     */
    ProductActivityVO getProductActivity(ProductActivityQuery query);

    /****
     * 业务系统查询活动列表
     * @param query
     * @return
     */
    Page<ProductActivityVO> list(ProductActivityDTO query);

    int subtractStock(@Param("id") Integer id, @Param("count") Integer count);

    int addStock(@Param("id") Integer id, @Param("count") Integer count);

    void stopProductActivity(@Param("productId") Integer productId);
}
