package com.yimao.cloud.water.handler;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.water.feign.ProductFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/12/5
 */
@Component
@Slf4j
public class ProductFeignHandler {

    private static final String LIST_PRODUCTCOST_BY_OLDCOSTID = "LIST::PRODUCTCOST_BY_OLDCOSTID::";
    private static final String GET_PRODUCTCOST_BY_ID = "A::GET_PRODUCTCOST_BY_ID::";

    @Resource
    private ProductFeign productFeign;
    @Resource
    private RedisCache redisCache;

    /**
     * water并发较高，改用redis缓存一段时间
     *
     * @param id 地区ID
     */
    public ProductCostDTO getProductCostById(Integer id) {
        ProductCostDTO one = redisCache.get(GET_PRODUCTCOST_BY_ID + id, ProductCostDTO.class);
        if (one != null) {
            return one;
        } else {
            //根据设备的区域信息查询区域ID
            one = productFeign.getProductCostById(id);
            if (one != null) {
                redisCache.set(GET_PRODUCTCOST_BY_ID + id, one, 3600L);
            }
            return one;
        }
    }

    /**
     * water并发较高，改用redis缓存一段时间
     *
     * @param oldCostId 计费方式ID
     */
    public List<ProductCostDTO> listProductCostByOldCostId(Integer oldCostId) {
        List<ProductCostDTO> list = redisCache.getCacheList(LIST_PRODUCTCOST_BY_OLDCOSTID + oldCostId, ProductCostDTO.class);
        if (CollectionUtil.isNotEmpty(list)) {
            return list;
        } else {
            //根据设备的区域信息查询区域ID
            list = productFeign.listProductCostByOldCostId(oldCostId);
            if (CollectionUtil.isNotEmpty(list)) {
                redisCache.setCacheList(LIST_PRODUCTCOST_BY_OLDCOSTID + oldCostId, list, ProductCostDTO.class, 3600L);
            }
            return list;
        }
    }
}
