package com.yimao.cloud.product.service;

import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.product.VirtualProductConfigDTO;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.query.product.ProductQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductStatusStatisticVO;
import com.yimao.cloud.product.po.Product;
import com.yimao.cloud.product.po.VirtualProductConfig;

import java.util.List;
import java.util.Set;

/**
 * 产品业务类
 *
 * @author Zhang Bo
 * @date 2018/11/8.
 */
public interface ProductService {

    /**
     * 创建产品
     *
     * @param product          产品
     * @param frontCategoryIds 前台类目
     * @param incomeRuleIds    收益分配规则
     * @param costIds          水机计费方式
     */
    void save(Product product, Set<Integer> frontCategoryIds, Set<Integer> incomeRuleIds, Set<Integer> costIds, Set<Integer> distributorIds, VirtualProductConfigDTO virtualProductConfigDTO);

    /**
     * 修改产品
     *
     * @param product          产品
     * @param frontCategoryIds 前台类目
     * @param incomeRuleIds    收益分配规则
     * @param costIds          水机计费方式
     */
    void update(Product product, Set<Integer> frontCategoryIds, Set<Integer> incomeRuleIds, Set<Integer> costIds, Set<Integer> distributorIds, VirtualProductConfigDTO virtualProductConfigDTO);

    /**
     * 修改产品状态、排序、库存等信息
     *
     * @param record 产品
     */
    void update(Product record);

    /**
     * 查询产品（单个，基本信息）
     *
     * @param id 产品ID
     */
    ProductDTO getProductById(Integer id);

    /**
     * 查询虚拟产品配置
     *
     * @param id 产品ID
     */
    VirtualProductConfig getVirtualProductConfigByProductId(Integer id);

    /**
     * 查询产品（单个，扩展信息）
     *
     * @param id 产品ID
     * @param terminal 平台
     */
    ProductDTO getFullProductById(Integer id,Integer terminal);

    /**
     * 查询产品（分页）
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    PageVO<ProductDTO> page(Integer pageNum, Integer pageSize, ProductQuery query);

    /**
     * 客户端查询产品（经销商APP、公众号等）
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    PageVO<ProductDTO> listProductForClient(Integer pageNum, Integer pageSize, ProductQuery query);

    /**
     * 描述：APP端获取当前登录用户有权销售的产品销售类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     *
     * @param userName 用户
     */
    List<DictionaryDTO> listProductSupplyCode(String userName);

    /**
     * 统计产品
     */
    ProductStatusStatisticVO findProductStatistics();

    /**
     * @description 查询YM 卡的产品数据
     * @author liulin
     * @date 2019/2/23 9:27
     */
    List<ProductDTO> findMYCardProductList(String categoryName);


    List<ProductDTO> findMCardProductList();

    /**
     * @description 查询F卡的产品数据
     * @author liulin
     * @date 2019/2/27 17:52
     */
    List<ProductDTO> findFCardProductList();

    /**
     * 描述：根据产品分类名称获取产品ID集合
     *
     * @param categoryName  产品分类名称
     * @param categoryLevel 几级产品分类
     */
    List<Integer> listProductIdsByCategoryName(String categoryName, Integer categoryLevel);

    /**
     * @description 查询产品列表
     */
    List<ProductDTO> findProductList();

    /**
     * 产品售出量累加
     *
     * @param id    产品Id
     * @param count 购买数量
     * @author liuhao@yimaokeji.com
     */
    void updateBuyCount(Integer id, Integer count);

    /**
     * 批量上架、下架、删除
     *
     * @param updater
     * @param ids
     * @param status
     */
    void updateBatch(String updater, List<Integer> ids, Integer status);

    /**
     * 后台修改订单产品型号时查询同类产品列表（二级类目一致，价格一致，计费方式一致）
     *
     * @param productId 产品ID
     * @param costId    计费方式ID
     */
    List<ProductDTO> listProductForModifyOrder(Long orderId, Integer productId, Integer costId);

    /**
     * @return java.util.List<com.yimao.cloud.pojo.dto.product.ProductDTO>
     * @Author lizhiqiang
     * @Date 2019-07-24
     * @Param [id]
     */
    List<ProductDTO> getProductByFrontId(Integer id);

    List<DictionaryDTO> getAllProductSupplyCode();

    /**
     * 折机经销商关联关系经销商转让转移
     * @param origDistributorId
     * @param newDistributorId
     */
    void changeProductDistributor(Integer origDistributorId, Integer newDistributorId);

    /**
     * @param
     * @return java.util.List<java.lang.Integer>
     * @description 根据产品供应类型查询产品
     * @author Liu Yi
     * @date 2020/5/6 10:46
     */
    List<Integer> getProductBySupplyCode(String code);

}
