package com.yimao.cloud.out.controller.openapi;

import com.yimao.cloud.out.feign.WaterFeign;
import com.yimao.cloud.out.utils.OpenApiResult;
import com.yimao.cloud.out.vo.ProductOpenApiVO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 描述：同步产品对内型号管理（售后系统调用）
 *
 * @Author Liu long jie
 * @Date 2019/10/18
 * @Version 1.0
 */
@RestController
@Api(tags = "ProductOpenApiController")
@Slf4j
public class ProductOpenApiController {
    private static final String FILTER_ADD = "0"; //滤芯新增
    private static final String FILTER_UPDATE = "2"; // 滤芯修改
    private WaterFeign waterFeign;
    /**
     * 产品体系基础数据
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/openapi/product/getData")
    @ApiOperation(value = "同步产品体系基础数据(原云平台)")
    public Map<String, Object> getData(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        //TODO 产品体系基础数据
        //List<WaterDevice> list = productOpenApiService.getProductData();
        List<ProductOpenApiVO> volist = new ArrayList<>();
        /*for(WaterDevice wd : list){
            ProductOpenApiVO vo = new ProductOpenApiVO();
            vo.setId(wd.getId());
            vo.setName(wd.getName());
            vo.setProductType(wd.getDeviceType());
            vo.setProductTypeId(wd.getDeviceTypeId());
            vo.setProductScope(wd.getDeviceScope());
            vo.setProductScopeId(wd.getDeviceScopeId());
            vo.setProductModel(wd.getDeviceModel());
            volist.add(vo);
        }*/
        Map<String, Object> data = new HashMap<>();
        data.put("data", volist);
        map = OpenApiResult.result(request, data);
        return map;
    }

    /**
     * 滤芯数据(百得滤芯数据同步原云平台)
     *
     * @param request
     * @return
     */
    @PostMapping(value = "openapi/product/filterInfo/data")
    public Map<String, Object> filterInfo(
            HttpServletRequest request,
            @RequestParam() String type,
            @RequestParam(required = false, defaultValue = "") String id,
            @RequestParam() String name,
            @RequestParam() String productType,
            @RequestParam() String productRange,
            @RequestParam() String productModel) {
        if (FILTER_ADD.equals(type)) {
            WaterDeviceConsumableDTO consumable = new WaterDeviceConsumableDTO();
            consumable.setName(name.trim());
            consumable.setCreateTime(new Date());
            consumable.setCreator("售后同步");
            consumable.setUpdateTime(new Date());
            consumable.setType(1);
            consumable.setDeviceModel(productModel);
            consumable.setOldId(id);
            waterFeign.saveWaterDeviceConsumable(consumable);
        } else if (FILTER_UPDATE.equals(type)) {
            WaterDeviceConsumableDTO consumable = waterFeign.getConsumableByOldId(id);
            if (consumable != null) {
                consumable.setName(name.trim());
                consumable.setUpdateTime(new Date());
                consumable.setUpdater("售后同步");
                consumable.setType(1);
                consumable.setDeviceModel(productModel);
                waterFeign.updateWaterDeviceConsumable(consumable);
            } else {
                log.error("售后同步更新业务系统，未找到百得耗材id对应的耗材，耗材id为：" + id);
            }

        }
        return OpenApiResult.success(request);
    }
}
