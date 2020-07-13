package com.yimao.cloud.system.controller;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.enums.GoodsCategoryEnum;
import com.yimao.cloud.base.enums.ProductActivityType;
import com.yimao.cloud.base.enums.ProductModeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.system.GoodsCategoryDTO;
import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;
import com.yimao.cloud.pojo.dto.system.StationBackStockRecordDTO;
import com.yimao.cloud.pojo.dto.system.StationMaterialStockDTO;
import com.yimao.cloud.pojo.dto.system.StationStockTransferDTO;
import com.yimao.cloud.pojo.dto.system.StationStoreHouseDTO;
import com.yimao.cloud.pojo.query.system.StationCompanyStoreHouseQuery;
import com.yimao.cloud.pojo.query.system.StationStoreHouseQuery;
import com.yimao.cloud.pojo.query.system.StoreHouseAllQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.ProductFeign;
import com.yimao.cloud.system.mapper.StationStoreHouseMapper;
import com.yimao.cloud.system.po.StationBackStockRecord;
import com.yimao.cloud.system.po.StockOperation;
import com.yimao.cloud.system.service.ExportRecordService;
import com.yimao.cloud.system.service.StockOperationService;
import com.yimao.cloud.system.service.StoreHouseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import java.util.*;

/**
 * @author zhilin.he
 * @description 云平台库存管理
 * @date 2019/4/30 16:17
 **/
@RestController
@Slf4j
@Api(tags = "StoreHouseController")
public class StoreHouseController {

    @Resource
    private StoreHouseService storeHouseService;
    @Resource
    private StockOperationService stockOperationService;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private StationStoreHouseMapper stationStoreHouseMapper;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 安装工退单回增库存（占用库存-1，可用库存+1）
     */
    @PostMapping(value = "/store/house/station/stock/add")
    public Object addStock(@RequestParam(value = "productId") Integer productId,
                           @RequestParam(value = "stationId") Integer stationId,
                           @RequestParam(value = "count") Integer count) {
        Integer goodsId = productFeign.getGoodsIdByProductId(productId);
        if (goodsId == null) {
            throw new BadRequestException("该产品未绑定库存物资。");
        }
        storeHouseService.addStock(goodsId, stationId, count);
        return ResponseEntity.noContent().build();
    }

    /**
     * 服务站门店预删减某物资库存（可用库存-1，占用库存+1）
     */
    @PostMapping(value = "/store/house/station/stock/prune")
    public Object stockPrune(@RequestParam(value = "productId") Integer productId,
                             @RequestParam(value = "stationId") Integer stationId,
                             @RequestParam(value = "count") Integer count) {
        Integer goodsId = productFeign.getGoodsIdByProductId(productId);
        if (goodsId == null) {
            throw new BadRequestException("该产品未绑定库存物资。");
        }
        storeHouseService.stockPrune(goodsId, stationId, count);
        return ResponseEntity.noContent().build();
    }

    /**
     * 服务站门店删减某物资占用库存（占用库存-1）
     */
    @PostMapping(value = "/store/house/station/occupyStock/prune")
    public Object occupyStockPrune(@RequestParam(value = "productId") Integer productId,
                                   @RequestParam(value = "stationId") Integer stationId,
                                   @RequestParam(value = "count") Integer count) {
        Integer goodsId = productFeign.getGoodsIdByProductId(productId);
        if (goodsId == null) {
            throw new BadRequestException("该产品未绑定库存物资。");
        }
        storeHouseService.occupyStockPrune(goodsId, stationId, count);
        return ResponseEntity.noContent().build();
    }

    /**
     * 服务站公司物资分配至服务站门店
     */
    @PostMapping(value = "/store/house/stationCompany/goods/distribution")
    public Object goodsDistribution(@RequestParam(value = "goodsId") Integer goodsId,
                                    @RequestParam(value = "stationId") Integer stationId,
                                    @RequestParam(value = "count") Integer count,
                                    @RequestParam(value = "stationCompanyId") Integer stationCompanyId) {
        storeHouseService.goodsDistribution(goodsId, stationId, count, stationCompanyId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 校验某服务站是否有指定物资的库存，有则返回该物资信息，无则返回null
     */
    @PostMapping(value = "/store/house/check/{stationId}/{goodsId}")
    public GoodsMaterialsDTO checkStationGoodsIsHaveStock(@PathVariable(value = "stationId") Integer stationId,
                                                          @PathVariable(value = "goodsId") Integer goodsId) {
        return storeHouseService.checkStationGoodsIsHaveStock(stationId, goodsId);
    }

    /**
     * 分页查询服务站公司库存
     */
    @PostMapping(value = "/store/house/stationCompany/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询服务站公司库存", notes = "分页查询服务站公司库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StationCompanyStoreHouseQuery", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object pageStationCompanyStoreHouse(@RequestBody StationCompanyStoreHouseQuery query,
                                               @PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable(value = "pageSize") Integer pageSize) {
        return ResponseEntity.ok(storeHouseService.pageStationCompanyStoreHouse(query, pageNum, pageSize));
    }

    /**
     * 总仓库存导出
     */
    @PostMapping(value = "/store/house/stationCompany/export")
    @ApiOperation(value = "服务站公司库存导出", notes = "服务站公司库存导出")
    @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StationCompanyStoreHouseQuery", paramType = "body")
    public Object exportStationCompanyStoreHouse(@RequestBody StationCompanyStoreHouseQuery query) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/store/house/stationCompany/export";
        String title = "";
        if (query.getType() == 1) {
            title = "服务站公司净水设备库存列表";
        } else if (query.getType() == 2) {
            title = "服务站公司物资配件库存列表";
        } else if (query.getType() == 3) {
            title = "服务站公司展示机库存列表";
        } else {
            throw new BadRequestException("请指明导出物资的类型。");
        }
        ExportRecordDTO record = exportRecordService.save(url, title);

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_USER, map);
        }
        return CommResult.ok(record.getId());
    }

    /**
     * 分页查询服务站门店库存
     */
    @PostMapping(value = "/store/house/station/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询服务站门店库存", notes = "分页查询服务站门店库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StationStoreHouseQuery", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object pageStationStoreHouse(@RequestBody StationStoreHouseQuery query,
                                        @PathVariable(value = "pageNum") Integer pageNum,
                                        @PathVariable(value = "pageSize") Integer pageSize) {
        return ResponseEntity.ok(storeHouseService.pageStationStoreHouse(query, pageNum, pageSize));
    }

    /**
     * 安装工app获取门店的可用库存(水机设备)
     *
     * @param stationId
     * @return
     */
    @GetMapping(value = "/store/house/station/availableDeviceStock/{stationId}")
    public List<StationStoreHouseDTO> availableStationDeviceStock(@PathVariable(value = "stationId") Integer stationId) {

        if (Objects.isNull(stationId)) {
            throw new YimaoException("服务站门店id不存在");
        }

        return stationStoreHouseMapper.availableStationDeviceStock(stationId);

    }

    /**
     * 安装工app获取门店的可用库存(耗材)
     *
     * @param stationId
     * @param adaptionModel
     * @return
     */
    @GetMapping(value = "/store/house/station/availableMaterialStock/{stationId}")
    public List<GoodsCategoryDTO> availableStationMaterialStock(@PathVariable(value = "stationId") Integer stationId, @RequestParam(value = "adaptionModel", required = false) String adaptionModel) {

        if (Objects.isNull(stationId)) {
            throw new YimaoException("服务站门店id不存在");
        }

        List<StationStoreHouseDTO> stationMaterialStock = stationStoreHouseMapper.availableStationMaterialStock(stationId, adaptionModel);

        if (CollectionUtil.isEmpty(stationMaterialStock)) {
            return null;
        }

        List<GoodsCategoryDTO> res = new ArrayList<>();

        Map<Integer, List<StationStoreHouseDTO>> map = new HashMap();
        Map<Integer, GoodsCategoryDTO> goodsCategoryMap = new HashMap();
        //封装每个分类id对应下的耗材集合
        for (StationStoreHouseDTO stationStoreHouseDTO : stationMaterialStock) {
            if (map.containsKey(stationStoreHouseDTO.getGoodsCategoryId())) {
                map.get(stationStoreHouseDTO.getGoodsCategoryId()).add(stationStoreHouseDTO);
            } else {
                List<StationStoreHouseDTO> list = new ArrayList();
                map.put(stationStoreHouseDTO.getGoodsCategoryId(), list);
            }

            //根据分类id创建对应分类对象
            if (!goodsCategoryMap.containsKey(stationStoreHouseDTO.getGoodsCategoryId())) {
                GoodsCategoryDTO category = new GoodsCategoryDTO();
                category.setId(stationStoreHouseDTO.getGoodsCategoryId());
                category.setName(stationStoreHouseDTO.getTwoCategoryName());
                category.setSorts(stationStoreHouseDTO.getGoodsCategorySorts());
                goodsCategoryMap.put(stationStoreHouseDTO.getGoodsCategoryId(), category);
            }

        }

        for (Map.Entry<Integer, GoodsCategoryDTO> entry : goodsCategoryMap.entrySet()) {
            Integer categoryId = entry.getKey();
            GoodsCategoryDTO dto = entry.getValue();

            if (map.containsKey(categoryId)) {
                dto.setStationGoodsList(map.get(categoryId));
            }

            res.add(dto);
        }

        //排序
        Collections.sort(res, new Comparator<GoodsCategoryDTO>() {

            public int compare(GoodsCategoryDTO o1, GoodsCategoryDTO o2) {

                return o1.getSorts() - o2.getSorts();
            }
        });

        return res;

    }

    /**
     * 站务系统-服务站门店库存借调
     *
     * @param transfer
     * @return
     */
    @PostMapping(value = "/store/house/station/transfer")
    public boolean transferStationStock(@RequestBody StationStockTransferDTO transfer) {
        try {
            if (Objects.isNull(transfer.getType())) {
                //设置为站务系统门店借调类型
                transfer.setType(0);
            }

            return storeHouseService.transferStationStock(transfer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 后台-门店库存调拨
     *
     * @param transfer
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/store/house/station/allot")
    @ApiImplicitParam(name = "transfer", value = "调拨类", required = true, dataType = "StationStockTransferDTO", paramType = "body")
    public Object allotStationStock(@RequestBody StationStockTransferDTO transfer) throws Exception {
        //设置为后台调拨类型
        transfer.setType(1);

        storeHouseService.transferStationStock(transfer);

        return ResponseEntity.noContent().build();
    }

    /**
     * 后台-门店库存修改
     *
     * @param transfer
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/store/house/stationStock/{stationId}/{goodsId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stationId", value = "门店id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "goodsId", value = "物资id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "stockCount", value = "修改库存数", required = true, dataType = "Long", paramType = "body"),
            @ApiImplicitParam(name = "isDefective", value = "修改物资类型 true-不良品 false-良品 ", required = true, dataType = "Boolean", paramType = "body")
    })
    public Object editStationStock(@PathVariable("stationId") Integer stationId,
                                   @PathVariable("goodsId") Integer goodsId,
                                   Integer stockCount, boolean isDefective) {

        storeHouseService.updateStationStockCount(stationId, goodsId, stockCount, isDefective);

        return ResponseEntity.noContent().build();
    }

    /**
     * 后台-门店退机库存-分页查询门店退机库存纪录
     */
    @PostMapping(value = "/store/house/stationReturnStock/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询门店退机库存纪录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StationStoreHouseQuery", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<StationBackStockRecordDTO> pageStationReturnStockRecord(@RequestBody StationStoreHouseQuery query,
                                                                          @PathVariable(value = "pageNum") Integer pageNum,
                                                                          @PathVariable(value = "pageSize") Integer pageSize) {
        return storeHouseService.pageStationReturnStockRecord(query, pageNum, pageSize);
    }


    /**
     * 后台-门店退机转良品不良品良品
     *
     * @param transfer
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/store/house/stationReturnStock/transfer/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "退机库存纪录ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "isDefective", value = "0-转移不良品库存 1-转移良品库存", required = true, dataType = "Boolean", paramType = "body")
    })
    public Object stationReturnStockTransfer(@PathVariable("id") Integer id, boolean isDefective) {

        storeHouseService.stationReturnStockTransfer(id, isDefective);

        return ResponseEntity.noContent().build();

    }

    /**
     * 维修门店耗材扣减
     */
    @PostMapping(value = "/store/house/stationMaterialStock/reduce")
    public boolean reduceStationMaterialStock(@RequestBody StationMaterialStockDTO dto) {
        try {
            log.info("门店耗材扣减参数={}", JSON.toJSONString(dto));

            if (Objects.isNull(dto.getStationId())) {
                log.error("门店id为空");
                return false;
            }

            if (Objects.isNull(dto.getProductCategoryId())) {
                log.error("产品三级类目为空");
                return false;
            }

            storeHouseService.reduceStationMaterialStock(dto);

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 维护-门店耗材扣减
     */
    @PostMapping(value = "/store/house/maintenance/stationMaterialStock/reduce")
    public boolean reduceStationStockByMaintenance(@RequestBody StationMaterialStockDTO dto) {
        try {
            log.info("门店耗材扣减参数={}", JSON.toJSONString(dto));

            if (Objects.isNull(dto.getEngineerId())) {
                log.error("安装工id为空");
                return false;
            }

            if (CollectionUtil.isEmpty(dto.getFilterNames())) {
                log.error("更换滤芯名称为空");
                return false;
            }

            storeHouseService.reduceStationStockByMaintenance(dto);

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 总仓库存导出
     */
    @PostMapping(value = "/store/house/all/export")
    @ApiOperation(value = "总仓库存导出", notes = "总仓库存导出")
    @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StoreHouseAllQuery", paramType = "body")
    public Object exportHouseAll(@RequestBody StoreHouseAllQuery query) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/store/house/all/export";
        String title = "";
        if (query.getType() == 1) {
            title = "总仓净水设备库存列表";
        } else if (query.getType() == 2) {
            title = "总仓物资配件库存列表";
        } else if (query.getType() == 3) {
            title = "总仓展示机库存列表";
        } else {
            throw new BadRequestException("请指明导出物资的类型。");
        }
        ExportRecordDTO record = exportRecordService.save(url, title);

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_SYSTEM, map);
        }
        return CommResult.ok(record.getId());
    }

    /**
     * 分页查询总仓库存
     */
    @PostMapping(value = "/store/house/all/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询总仓库存", notes = "分页查询总仓库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StoreHouseAllQuery", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object pageStoreHouseAll(@RequestBody StoreHouseAllQuery query,
                                    @PathVariable(value = "pageNum") Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize) {
        return ResponseEntity.ok(storeHouseService.pageStoreHouseAll(query, pageNum, pageSize));
    }

    /**
     * 修改总仓库存
     */
    @PutMapping(value = "/store/house/all/updateStockCount/{id}")
    @ApiOperation(value = "修改总仓库存", notes = "修改总仓库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "总仓库存id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "stockCount", value = "修改的库存数量", required = true, dataType = "Long", paramType = "query")
    })
    public Object updateStockCount(@PathVariable(value = "id") Integer id,
                                   @RequestParam(value = "stockCount") Integer stockCount) {
        storeHouseService.updateStockCount(id, stockCount);
        return ResponseEntity.noContent().build();
    }

    /**
     * 云平台库存管理--分页查询库存信息
     */
    @GetMapping(value = "/store/house/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询库存信息", notes = "分页查询库存信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "special", value = "是否特殊库存：0-否，1-是", defaultValue = "0", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object query(@RequestParam(required = false) String province,
                        @RequestParam(required = false) String city,
                        @RequestParam(required = false) String region,
                        @RequestParam(required = false, defaultValue = "0") Integer special,
                        @PathVariable(value = "pageNum") Integer pageNum,
                        @PathVariable(value = "pageSize") Integer pageSize) {
        return ResponseEntity.ok(storeHouseService.getStoreHouseList(province, city, region, special, pageNum, pageSize));
    }

    /**
     * 云平台库存管理--查询可以下发库存的产品信息
     */
    @GetMapping(value = "/store/house/product")
    @ApiOperation(value = "查询可以下发库存的产品信息")
    public Object listProduct() {
        List<ProductDTO> productList = productFeign.findProductList();
        productList.removeIf(dto -> dto.getMode() != ProductModeEnum.LEASE.value || dto.getActivityType() == null || dto.getActivityType() != ProductActivityType.PRODUCT_COMMON.value);
        List<ProductDTO> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(productList)) {
            for (ProductDTO dto : productList) {
                ProductDTO product = new ProductDTO();
                product.setId(dto.getId());
                product.setName(dto.getCategoryName());
                list.add(product);
            }
        }
        return list;
    }

    /**
     * 云平台库存管理--根据库存id获取库存信息
     */
    @GetMapping(value = "/store/house/{id}")
    @ApiOperation(value = "根据库存id获取库存信息", notes = "根据库存id获取库存信息")
    @ApiImplicitParam(name = "id", value = "库存id", required = true, dataType = "Long", paramType = "path")
    public Object findStoreHouse(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok(storeHouseService.findStoreHouse(id));
    }

    /**
     * 云平台库存管理--下发产品（新建库存）
     */
    @PostMapping(value = "/store/house")
    @ApiOperation(value = "下发产品（新建库存）", notes = "下发产品（新建库存）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "productId", value = "产品id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "库存数", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "special", value = "是否特殊库存：0-否，1-是", defaultValue = "0", dataType = "Long", paramType = "query")
    })
    public Object add(@RequestParam String province,
                      @RequestParam String city,
                      @RequestParam String region,
                      @RequestParam Integer productId,
                      @RequestParam Integer count,
                      @RequestParam(required = false, defaultValue = "0") Integer special) {
        storeHouseService.saveStoreHouse(province, city, region, productId, count, special);
        return ResponseEntity.noContent().build();
    }

    /**
     * 云平台库存管理--修改库存
     */
    @PutMapping(value = "/store/house/stock")
    @ApiOperation(value = "修改库存", notes = "修改库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "库存id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productId", value = "产品id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "库存数", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "special", value = "是否特殊库存：0-否，1-是", defaultValue = "0", dataType = "Long", paramType = "query")
    })
    public Object update(@RequestParam Integer id,
                         @RequestParam Integer productId,
                         @RequestParam Integer count,
                         @RequestParam(required = false, defaultValue = "0") Integer special) {
        storeHouseService.updateStoreHouse(id, productId, count, special);
        return ResponseEntity.noContent().build();
    }

    /**
     * 云平台库存管理--设置阀值
     */
    @PutMapping(value = "/store/house/set")
    @ApiOperation(value = "设置阀值", notes = "设置阀值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "库存id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productId", value = "产品id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "阀值数", required = true, dataType = "Long", paramType = "query")
    })
    public Object updateStoreHouseMaxValues(@RequestParam Integer id,
                                            @RequestParam Integer productId,
                                            @RequestParam Integer count) {
        storeHouseService.updateStoreHouseMaxValues(id, productId, count);
        return ResponseEntity.noContent().build();
    }

    /**
     * 云平台库存管理--返仓
     */
    @PutMapping(value = "/store/house/back")
    @ApiOperation(value = "返仓", notes = "返仓")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "库存id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productId", value = "产品id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "调拨数量", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "special", value = "是否特殊库存：0-否，1-是", defaultValue = "0", dataType = "Long", paramType = "query")
    })
    public Object updateStoreHouseBack(@RequestParam Integer id,
                                       @RequestParam Integer productId,
                                       @RequestParam Integer count,
                                       @RequestParam(required = false, defaultValue = "0") Integer special) {
        storeHouseService.updateStoreHouseBack(id, productId, count, special);
        return ResponseEntity.noContent().build();
    }

    /**
     * 云平台库存管理--调拨
     */
    @PostMapping(value = "/store/house/transfer")
    @ApiOperation(value = "调拨", notes = "调拨")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "库存id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "productId", value = "产品id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "调拨数量", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "special", value = "是否特殊库存：0-否，1-是", defaultValue = "0", dataType = "Long", paramType = "query")
    })
    public Object updateStoreHouseTransfer(@RequestParam Integer id,
                                           @RequestParam String province,
                                           @RequestParam String city,
                                           @RequestParam String region,
                                           @RequestParam Integer productId,
                                           @RequestParam Integer count,
                                           @RequestParam(required = false, defaultValue = "0") Integer special) {
        storeHouseService.updateStoreHouseTransfer(id, province, city, region, productId, count, special);
        return ResponseEntity.noContent().build();
    }

    /**
     * 云平台库存管理--删除库存
     */
    @DeleteMapping(value = "/store/house/{id}")
    @ApiOperation(value = "根据库存id删除库存", notes = "根据库存id删除库存")
    @ApiImplicitParam(name = "id", value = "库存id", required = true, dataType = "Long", paramType = "path")
    public Object delete(@PathVariable(value = "id") Integer id) {
        storeHouseService.deleteStoreHouse(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 描述：云平台导出库存管理
     **/
    @PostMapping("/store/house/export")
    @ApiOperation(value = "导出库存管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "special", value = "是否特殊库存：0-否，1-是", defaultValue = "0", dataType = "Long", paramType = "query")
    })
    public Object exportStoreHouse(@RequestParam(required = false) String province,
                                   @RequestParam(required = false) String city,
                                   @RequestParam(required = false) String region,
                                   @RequestParam(required = false, defaultValue = "0") Integer special) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/store/house/export";
        ExportRecordDTO record = exportRecordService.save(url, "库存管理");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("province", province);
            map.put("city", city);
            map.put("region", region);
            map.put("special", special);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_SYSTEM, map);
        }
        return CommResult.ok(record.getId());
    }

    /**
     * 云平台库存管理--分页查询库存操作记录列表
     */
    @GetMapping(value = "/store/operation/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询库存操作记录列表", notes = "分页查询库存操作记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "operation", value = "操作类型", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "originalProvince", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "originalCity", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "originalRegion", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "operateProvince", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "operateCity", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "operateRegion", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "special", value = "是否特殊库存：0-否，1-是", defaultValue = "0", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object getStockOperationList(@RequestParam(required = false) Integer operation,
                                        @RequestParam(required = false) String originalProvince,
                                        @RequestParam(required = false) String originalCity,
                                        @RequestParam(required = false) String originalRegion,
                                        @RequestParam(required = false) String operateProvince,
                                        @RequestParam(required = false) String operateCity,
                                        @RequestParam(required = false) String operateRegion,
                                        @RequestParam(required = false) Date startTime,
                                        @RequestParam(required = false) Date endTime,
                                        @RequestParam(required = false, defaultValue = "0") Integer special,
                                        @PathVariable(value = "pageNum") Integer pageNum,
                                        @PathVariable(value = "pageSize") Integer pageSize) {
        StockOperation stockOperation = new StockOperation();
        stockOperation.setOperation(operation);
        stockOperation.setOriginalProvince(originalProvince);
        stockOperation.setOriginalCity(originalCity);
        stockOperation.setOriginalRegion(originalRegion);
        stockOperation.setOperateProvince(operateProvince);
        stockOperation.setOperateCity(operateCity);
        stockOperation.setOperateRegion(operateRegion);
        stockOperation.setSpecial(special);
        return ResponseEntity.ok(stockOperationService.getStockOperationList(stockOperation, startTime, endTime, pageNum, pageSize));
    }

    /**
     * 云平台库存管理--云平台导出库存操作日志
     */
    @PostMapping(value = "/store/operation/export")
    @ApiOperation(value = "云平台导出库存操作日志", notes = "云平台导出库存操作日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "operation", value = "操作类型", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "originalProvince", value = "原库存省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "originalCity", value = "原库存市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "originalRegion", value = "原库存区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "operateProvince", value = "操作库存省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "operateCity", value = "操作库存市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "operateRegion", value = "操作库存区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "special", value = "是否特殊库存：0-否，1-是", defaultValue = "0", dataType = "Long", paramType = "query")
    })
    public Object exportStockOperation(@RequestParam(required = false) Integer operation,
                                       @RequestParam(required = false) String originalProvince,
                                       @RequestParam(required = false) String originalCity,
                                       @RequestParam(required = false) String originalRegion,
                                       @RequestParam(required = false) String operateProvince,
                                       @RequestParam(required = false) String operateCity,
                                       @RequestParam(required = false) String operateRegion,
                                       @RequestParam(required = false) Date startTime,
                                       @RequestParam(required = false) Date endTime,
                                       @RequestParam(required = false, defaultValue = "0") Integer special) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/store/operation/export";
        ExportRecordDTO record = exportRecordService.save(url, "库存操作日志");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("operation", operation);
            map.put("originalProvince", originalProvince);
            map.put("originalCity", originalCity);
            map.put("originalRegion", originalRegion);
            map.put("operateProvince", operateProvince);
            map.put("operateCity", operateCity);
            map.put("operateRegion", operateRegion);
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            map.put("special", special);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_SYSTEM, map);
        }
        return CommResult.ok(record.getId());
        /*StockOperation stockOperation = new StockOperation();
        stockOperation.setOperation(operation);
        stockOperation.setOriginalProvince(originalProvince);
        stockOperation.setOriginalCity(originalCity);
        stockOperation.setOriginalRegion(originalRegion);
        stockOperation.setOperateProvince(operateProvince);
        stockOperation.setOperateCity(operateCity);
        stockOperation.setOperateRegion(operateRegion);
        stockOperationService.exportStockOperation(stockOperation, startTime, endTime, response);
        return ResponseEntity.noContent().build();*/
    }

    /**
     * 描述：根据省市区和产品ID查询库存数量
     */
    @GetMapping(value = "/store/house/count")
    @ApiOperation(value = "根据省市区和产品ID查询库存数量")
    public Object getStoreHouseCount(@RequestParam String province, @RequestParam String city, @RequestParam String region,
                                     @RequestParam Integer productId,
                                     @RequestParam(required = false, defaultValue = "0") Integer special) {
        return storeHouseService.getStoreHouseCount(province, city, region, productId, special);
    }
}
