package com.yimao.cloud.water.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 描述：PRODUCT微服务远程调用类。
 *
 * @Author Zhang Bo
 * @Date 2019/2/18 13:44
 */
@FeignClient(name = Constant.MICROSERVICE_PRODUCT)
public interface ProductFeign {

    /**
     * 获取变更计费方式时可选择的计费方式列表
     */
    @GetMapping(value = "/product/cost")
    List<ProductCostDTO> listProductCostByOldCostId(@RequestParam("oldCostId") Integer oldCostId);

    /**
     * 获取续费计费方式列表
     */
    @GetMapping(value = "/product/renewcost")
    List<ProductCostDTO> listProductRenewCostByOldCostId(@RequestParam("oldCostId") Integer oldCostId);

    @GetMapping(value = "/product/cost/{id}")
    ProductCostDTO getProductCostById(@PathVariable("id") Integer id);

}
