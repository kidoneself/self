package com.yimao.cloud.product.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.pojo.dto.product.ProductRenewDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.product.mapper.ProductRenewMapper;
import com.yimao.cloud.product.po.ProductRenew;
import com.yimao.cloud.product.service.ProductCategoryService;
import com.yimao.cloud.product.service.ProductRenewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Liu Yi
 * @date 2018/12/1.
 */
@Service
@Slf4j
public class ProductRenewServiceImpl implements ProductRenewService {

    @Resource
    private ProductRenewMapper productRenewMapper;
    @Resource
    private ProductCategoryService productCategoryService;
    @Resource
    private UserCache userCache;

    @Override
    public Integer saveProductRenew(List<Integer> categoryIdList, List<Integer> costIdList) {
        if (categoryIdList == null || categoryIdList.size() <= 0) {
            throw new BadRequestException("缺少必填信息!");
        }

        if (costIdList == null || costIdList.size() <= 0) {
            throw new BadRequestException("缺少必填信息!");
        }

        String creator = userCache.getCurrentAdminRealName();
        Date currentDate = new Date();
        List<ProductRenew> rlist = new ArrayList<>();
        ProductRenew productRenew;
        for (int i = 0; i < categoryIdList.size(); i++) {
            for (int j = 0; j < costIdList.size(); j++) {
                productRenew = new ProductRenew();
                productRenew.setCategoryId(categoryIdList.get(i));
                productRenew.setCostId(costIdList.get(j));
                productRenew.setCreateTime(currentDate);
                productRenew.setCreator(creator);

                rlist.add(productRenew);
            }
        }

        return productRenewMapper.saveProductRenews(rlist);
    }

    @Override
    public Integer updateProductRenew(ProductRenewDTO productCosDTO) {
        ProductRenew productRenew = new ProductRenew(productCosDTO);
        return productRenewMapper.updateByPrimaryKeySelective(productRenew);
    }

    @Override
    public ProductRenewDTO getProductRenewById(Integer id) {
        log.debug("*********传入id=" + id + "*********");

        ProductRenewDTO productRenewDTO = productRenewMapper.getProductRenewById(id);
        if (productRenewDTO != null) {
            return productRenewDTO;
        } else {
            throw new NotFoundException("获取商品续费信息为空!");
        }
    }

    @Override
    public void deleteProductRenewById(Integer id) {
        ProductRenew record = new ProductRenew();
        record.setId(id);
        record.setDeleted(true);
        productRenewMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public PageVO<ProductRenewDTO> listProductRenews(Integer pageNum, Integer pageSize, Integer categoryId, Integer secondCategoryId, Integer thirdCategoryId) {
        List<Integer> categoryIdList = new ArrayList<>();

        if (thirdCategoryId != null) {
            categoryIdList.add(thirdCategoryId);
        } else if (categoryId != null || secondCategoryId != null) {
        }

        PageHelper.startPage(pageNum, pageSize);
        Page<ProductRenewDTO> page = productRenewMapper.listProductRenews(categoryIdList);

        return new PageVO<>(pageNum, page);

    }

}
