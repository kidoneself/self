package com.yimao.cloud.out.controller.webapi;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.ProductSupplyCode;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.yun.YunOldIdUtil;
import com.yimao.cloud.out.enums.ApiStatusCode;
import com.yimao.cloud.out.feign.ProductFeign;
import com.yimao.cloud.out.feign.SystemFeign;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.utils.ApiResult;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/3/9
 */
@RestController
@Slf4j
@Api(tags = "BaseWebApiController")
public class BaseWebApiController {

    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private UserCache userCache;
    @Resource
    private ProductFeign productFeign;

    /**
     * 描述：微服务安装工或经销商登录
     *
     * @Creator Zhang Bo
     * @CreateTime 2019/3/9 9:12
     **/
    @PostMapping(value = "/webapi/getToken")
    @ApiOperation(value = "安装工登录获取token")
    public Object login(@RequestParam(required = false) String userId, @RequestParam(required = false) String password,
                        @RequestParam(required = false) String appType, HttpServletRequest request) {
        try {
            if (StringUtil.isBlank(appType)) {
                return ApiResult.error(request, "004", "appType参数必须传递");
            }
            if (!"1".equalsIgnoreCase(appType) && !"2".equalsIgnoreCase(appType)) {
                //错误的appType参数，只允许：1 Android ，2 Ios
                return ResponseEntity.ok(ApiResult.error(request, "004", "错误的appType参数，只允许：1-Android；2-Ios"));
            }
            EngineerDTO engineer = userFeign.engineerLogin(userId, password, Integer.parseInt(appType));
            if (engineer == null) {
                //用户名或密码错误
                return ApiResult.error(request, ApiStatusCode.ACCOUNT_PASSWORD_ERROR);
            }
            OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(engineer.getProvince(), engineer.getCity(), engineer.getRegion());
            if (onlineArea != null && !"Y".equalsIgnoreCase(onlineArea.getSyncState())) {
                return ApiResult.error(request, "您所在服务站正在升级新流程，请在升级成功后进行登录");
            }
            Map<String, String> data = new HashMap<>();
            data.put("token", engineer.getToken());
            return ApiResult.result(request, data);
        } catch (Exception e) {
            log.error("安装工新版登录发生错误，" + e.getMessage(), e);
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    /**
     * 微服务-工程师查询上线地区
     */
    @GetMapping(value = "/webapi/isOnlineArea")
    @ApiOperation(value = "工程师查询上线地区")
    public Object isOnlineArea(HttpServletRequest request,
                               @RequestParam(required = false, defaultValue = "") String province,
                               @RequestParam(required = false, defaultValue = "") String city,
                               @RequestParam(required = false, defaultValue = "") String region) {
        try {
            Integer engineerId = userCache.getCurrentEngineerId();
            EngineerDTO engineer = userFeign.getEngineerById(engineerId);
            if (engineer == null) {
                return ApiResult.error(request, ApiStatusCode.ENGINEER_NOT_EXISTS);
            }
            if (StringUtil.isBlank(province)) {
                province = engineer.getProvince();
            }
            if (StringUtil.isBlank(city)) {
                city = engineer.getCity();
            }
            if (StringUtil.isBlank(region)) {
                region = engineer.getRegion();
            }
            OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(province, city, region);
            if (onlineArea == null) {
                return ApiResult.result(request, false);
            }
            return ApiResult.result(request, true);
        } catch (Exception e) {
            log.error("安装工查询是否是上线地区发生错误，" + e.getMessage(), e);
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    /**
     * 新流程-更换机型，获取产品型号列表
     */
    @GetMapping(value = "/webapi/getDeviceModels")
    @ApiOperation(value = "获取产品型号")
    public Object getDeviceModels(HttpServletRequest request,
                                  @RequestParam(required = false) boolean isPaid,
                                  @RequestParam(required = false) String scopeId) {
        log.info("/webapi/getDeviceModels---获取产品型号参数isPaid=" + isPaid + ",获取产品型号参数scopeId=" + scopeId);

        List<Map<String, String>> mapList = new ArrayList<>();
        //根据scopeId获取对应映射关系（写死）
        List<String> modelList = YunOldIdUtil.DEVICE_MODEL;
        if (isPaid) {
            String[] s = scopeId.split("_");
            if (s.length == 2) {
                ProductDTO productDTO = productFeign.getById(Integer.parseInt(s[1]));
                if (productDTO != null && Objects.equals(productDTO.getSupplyCode(), ProductSupplyCode.PTPSJ.code)) {
                    return ApiResult.error(request, "004", "该产品暂不支持更换机型");
                }
            }
            String productScopeId = s[0];
            modelList = YunOldIdUtil.getDeviceModel(productScopeId);
        }
        for (String deviceMode : modelList) {
            //查询该名字对应产品
            Map<String, String> map = new HashMap<>();
            //翼猫平台业务管理系统中的产品ID
            // map.put("id", YunOldIdUtil.getProductModelId(deviceMode));
            Integer productId = productFeign.getProductIdByCategoryName(deviceMode);
            map.put("id", String.valueOf(productId));
            map.put("name", deviceMode);
            map.put("scopeId", YunOldIdUtil.getProductScopeId(deviceMode));
            map.put("scopeName", YunOldIdUtil.getProductScope(deviceMode));
            mapList.add(map);
        }
        return ApiResult.result(request, mapList);
    }

    /**
     * 新老流程-修改计费方式
     */
    @GetMapping(value = "/webapi/getChargeModelList")
    @ApiOperation(value = "获取计费方式")
    public Object getChargeModelList(HttpServletRequest request,
                                     @RequestParam(required = false, defaultValue = "") String scopeId,//云平台产品范围ID
                                     @RequestParam(required = false, defaultValue = "") String modalId,//云平台产品型号ID
                                     @RequestParam(required = false, defaultValue = "") String distributorId,
                                     String province,
                                     String city,
                                     String region,
                                     @RequestParam(required = false, defaultValue = "-1.0") double price) {
        //TODO 新系统把modalId设置为productId了
        List<Map<String, Object>> mapList = new ArrayList<>();
        log.info("获取产品型号参数scopeId=" + scopeId + ",modalId=" + modalId);
        if (StringUtil.isNotEmpty(scopeId)) {
            scopeId = scopeId.split("_")[0];
        }

        //服务站是否存在校验
        List<StationDTO> stationList = systemFeign.getStationByPCR(province, city, region, null);
        if (CollectionUtil.isEmpty(stationList)) {
            return ApiResult.error(request, ApiStatusCode.SERVICE_SITE_DEVICE_NOT_FOUND);
        }
        ProductDTO product = null;
        if (StringUtil.isNotEmpty(modalId)) {
            Integer productId = Integer.parseInt(modalId);
            //翼猫平台业务管理系统中的产品三级类目ID
            product = productFeign.getById(productId);
        }
        if (product == null) {
            return ApiResult.error(request, "004", "产品不存在");
        }
        if (Objects.equals(product.getSupplyCode(), ProductSupplyCode.PTPSJ.code)) {
            return ApiResult.error(request, "004", "该产品暂不支持更换机型");
        }
        ProductCategoryDTO category = productFeign.getProductCategoryById(product.getCategoryId());

        //根据产品id获取该产品后台绑定的计费方式
        List<ProductCostDTO> costList = productFeign.productCostList(product.getId());
        if (Objects.isNull(costList) || costList.size() < 1) {
            return ApiResult.error(request, ApiStatusCode.SER_NO_DATA);
        }
        for (ProductCostDTO cost : costList) {
            //获取产品二级类目（云平台的产品范围）
            ProductCategoryDTO secondCategory = productFeign.getProductCategoryById(category.getPid());
            //当产品范围不为空的时候，过滤一下
            if (StringUtil.isNotEmpty(scopeId)) {
                if (secondCategory != null && !Objects.equals(cost.getProductCategoryId(), secondCategory.getId())) {
                    continue;
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("id", cost.getId());
            map.put("name", cost.getName());
            map.put("accountFee", cost.getInstallationFee().doubleValue());
            map.put("price", cost.getRentalFee().doubleValue());
            map.put("scopeId", StringUtil.isNotEmpty(scopeId) ? scopeId : secondCategory != null ? secondCategory.getOldId() : "");
            map.put("scopeName", StringUtil.isNotEmpty(scopeId) ? YunOldIdUtil.getProductScopeById(scopeId) : secondCategory != null ? secondCategory.getName() : "");
            mapList.add(map);
        }
        log.info("返回计费方式数据=" + JSON.toJSONString(mapList));
        return ApiResult.result(request, mapList);
    }

    /**
     * 获取所有计费方式（IOS）
     */
    @GetMapping(value = "/webapi/getChargeModels")
    @ApiOperation(value = "获取所有计费方式（IOS）")
    public Object getChargeModels(HttpServletRequest request,
                                  @RequestParam(required = false, defaultValue = "-1") double price,
                                  @RequestParam(required = false, defaultValue = "") String scopeId) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        log.info("获取所有计费方式（IOS）price={},scopeId={}", price, scopeId);

        //根据产品id获取该产品后台绑定的计费方式
        List<ProductCostDTO> costList = productFeign.productCostList(null);
        if (Objects.isNull(costList) || costList.size() < 1) {
            return ApiResult.error(request, ApiStatusCode.SER_NO_DATA);
        }
        //去掉首年1000元和1500元以外的套餐
        // costList.removeIf(dto -> dto.getTotalFee().compareTo(new BigDecimal(1180)) != 0 && dto.getTotalFee().compareTo(new BigDecimal(1680)) != 0);
        for (ProductCostDTO cost : costList) {
            //获取产品二级类目（云平台的产品范围）
            ProductCategoryDTO secondCategory = productFeign.getProductCategoryById(cost.getProductCategoryId());
            Map<String, Object> map = new HashMap<>();
            map.put("id", cost.getId().toString());
            map.put("name", cost.getName());
            map.put("accountFee", cost.getInstallationFee().doubleValue());
            map.put("price", cost.getRentalFee().doubleValue());
            map.put("scopeId", StringUtil.isNotEmpty(scopeId) ? scopeId : secondCategory != null ? secondCategory.getOldId() : "");
            map.put("scopeName", StringUtil.isNotEmpty(scopeId) ? YunOldIdUtil.getProductScopeById(scopeId) : secondCategory != null ? secondCategory.getName() : "");
            mapList.add(map);
        }
        log.info("返回计费方式数据=" + JSON.toJSONString(mapList));
        return ApiResult.result(request, mapList);
    }
}
