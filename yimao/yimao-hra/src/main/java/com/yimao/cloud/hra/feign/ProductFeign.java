package com.yimao.cloud.hra.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = Constant.MICROSERVICE_PRODUCT)
public interface ProductFeign {

    @RequestMapping(value = "/product/{id}", method = RequestMethod.GET)
    ProductDTO getProductById(@PathVariable("id") Integer id);

    @RequestMapping(value = "/product/category/{id}", method = RequestMethod.GET)
    ProductCategoryDTO getProductCategory(@PathVariable("id") Integer categoryId);


    /**
     * 获取M卡
     *
     * @return ProductDTO
     */
    @RequestMapping(value = "/product/MCard", method = RequestMethod.GET)
    ProductDTO findMCardProductList();
}
