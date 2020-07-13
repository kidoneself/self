package com.yimao.cloud.station.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * @author yaoweijun
 * @date 2019/12/23.
 */
@FeignClient(name = Constant.MICROSERVICE_PRODUCT)
public interface ProductFeign {
    /**
     * 查询产品类目（分页）
     *
     * @param name      类目名称
     * @param type      前台类目还是后台类目：1-后台类目；2-前台类目；
     * @param terminal  终端：1-健康e家公众号；2-小猫店小程序；3-经销商APP；
     * @param pid       父类目ID
     * @param level     产品类目等级：1-一级；2-二级；3-三级；
     * @param companyId 产品公司ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageNum   页码
     * @param pageSize  每页大小
     */
    @GetMapping(value = "/product/category/{pageNum}/{pageSize}")
    PageVO<ProductCategoryDTO> pageProductCategory(@RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "type", required = false) Integer type,
                                                   @RequestParam(value = "terminal", required = false) Integer terminal,
                                                   @RequestParam(value = "pid", required = false) Integer pid,
                                                   @RequestParam(value = "level", required = false) Integer level,
                                                   @RequestParam(value = "companyId", required = false) Integer companyId,
                                                   @RequestParam(value = "startTime", required = false) Date startTime,
                                                   @RequestParam(value = "endTime", required = false) Date endTime,
                                                   @PathVariable(value = "pageNum") Integer pageNum,
                                                   @PathVariable(value = "pageSize") Integer pageSize);

}
