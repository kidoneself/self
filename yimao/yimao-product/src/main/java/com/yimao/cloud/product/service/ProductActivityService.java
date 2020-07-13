package com.yimao.cloud.product.service;

import com.yimao.cloud.pojo.dto.product.ProductActivityDTO;
import com.yimao.cloud.pojo.query.product.ProductActivityQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductActivityVO;

/***
 * 产品活动service
 * @author zhangbaobao
 * @date 2020/3/12
 */
public interface ProductActivityService {

    /***
     * 更新活动
     * @param update
     * @return
     */
    int updateProductActivity(ProductActivityDTO update);

    /***
     * 查询单个活动产品详情
     * @param productActivity
     * @return
     */
    ProductActivityVO getProductActivity(ProductActivityQuery productActivity);

    /***
     * 查询所有已开启的活动产品列表
     * @return
     */
    PageVO<ProductActivityVO> pageProductActivity(Integer pageNum, Integer pageSize, ProductActivityQuery query);

    /***
     * 业务系统查询活动列表
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    PageVO<ProductActivityVO> productActivityList(Integer pageNum, Integer pageSize, ProductActivityDTO query);

    /***
     * 激活活动
     * @param id
     */
    void openProductActivity(Integer id);


    void stopProductActivity(Integer productId);
}
