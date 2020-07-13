package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.*;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.query.system.StationCompanyStoreHouseQuery;
import com.yimao.cloud.pojo.query.system.StationStoreHouseQuery;
import com.yimao.cloud.pojo.query.system.StoreHouseAllQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.system.StoreHouseVO;
import com.yimao.cloud.system.feign.OrderFeign;
import com.yimao.cloud.system.feign.ProductFeign;
import com.yimao.cloud.system.feign.UserFeign;
import com.yimao.cloud.system.mapper.*;
import com.yimao.cloud.system.po.*;
import com.yimao.cloud.system.service.AreaService;
import com.yimao.cloud.system.service.StoreHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhilin.he
 * @description
 * @date 2019/4/30 16:16
 **/
@Service
@Slf4j
public class StoreHouseServiceImpl implements StoreHouseService {

    @Resource
    private StoreHouseMapper storeHouseMapper;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserCache userCache;
    @Resource
    private StockOperationMapper stockOperationMapper;
    @Resource
    private StationBackStockRecordMapper stationBackStockRecordMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private StationCompanyStoreHouseMapper stationCompanyStoreHouseMapper;
    @Resource
    private StationStoreHouseMapper stationStoreHouseMapper;
    @Resource
    private StoreHouseAllMapper storeHouseAllMapper;
    @Resource
    private GoodsMaterialsMapper goodsMaterialsMapper;
    @Resource
    private StationCompanyStationMapper stationCompanyStationMapper;
    @Resource
    private StationMapper stationMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private AreaService areaService;
    @Resource
    private UserFeign userFeign;

    /**
     * 分页查询库存信息
     */
    @Override
    public PageVO<StoreHouseVO> getStoreHouseList(String province, String city, String region, Integer special, Integer pageNum, Integer pageSize) {
        PageVO<StoreHouseVO> pageVO = new PageVO<>();
        Example example = new Example(StoreHouse.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(province)) {
            criteria.andEqualTo("province", province);
        }
        if (StringUtil.isNotEmpty(city)) {
            criteria.andEqualTo("city", city);
        }
        if (StringUtil.isNotEmpty(region)) {
            criteria.andEqualTo("region", region);
        }
        if (special != null) {
            criteria.andEqualTo("special", special);
        }
        example.orderBy("place").desc().orderBy("updateTime").desc();
        //库存列表
        PageHelper.startPage(pageNum, pageSize);
        List<StoreHouse> list = storeHouseMapper.selectByExample(example);
        PageInfo<StoreHouse> pageResult = new PageInfo<>(list);
        StoreHouse storeHouse = new StoreHouse();
        //总仓位置
        storeHouse.setPlace(StorePlaceEnum.MAIN_STORE.value);
        StoreHouse masterHouse = storeHouseMapper.selectOne(storeHouse);
        //产品列表信息
        List<ProductDTO> productList = productFeign.findProductList();
        if (masterHouse != null) {
            List<StoreHouseVO> storeHouseVOList = getStoreHouse(list, productList);
            pageVO.setResult(storeHouseVOList);
            pageVO.setPageNum(pageNum);
            pageVO.setPageSize(pageSize);
            pageVO.setTotal(pageResult.getTotal());
            pageVO.setPages(pageResult.getPages());
        }
        return pageVO;
    }

    public List<StoreHouseVO> getStoreHouse(List<StoreHouse> list, List<ProductDTO> productList) {
        log.info("================================== productListSize:" + productList.size());
        // productList.removeIf(dto -> (dto.getMode() != null && dto.getMode() != ProductModeEnum.LEASE.value) || (dto.getActivityType() != null && dto.getActivityType() != ProductActivityType.PRODUCT_COMMON.value));
        productList.removeIf(dto -> (dto.getMode() != null && dto.getMode() != ProductModeEnum.LEASE.value) || dto.getActivityType() == null || dto.getActivityType() != ProductActivityType.PRODUCT_COMMON.value);

        List<StoreHouseVO> storeHouseVOList = Lists.newArrayList();
        Gson gson = new Gson();
        for (StoreHouse store : list) {
            List<StockCountDTO> stockCounts = Lists.newArrayList();
            Map<String, Integer> stocks = gson.fromJson(store.getStocks(), new TypeToken<Map<String, Integer>>() {
            }.getType());
            for (ProductDTO dto : productList) {
                // Integer productId = dto.getId();
                String productModel = dto.getCategoryName();
                if (stocks.containsKey(productModel)) {
                    stockCounts.add(new StockCountDTO(dto.getId(), dto.getName(), stocks.get(productModel)));
                    stocks.remove(productModel);
                } else {
                    stockCounts.add(new StockCountDTO(dto.getId(), dto.getName(), 0));
                }
            }
            StoreHouseVO StoreHouseVo = new StoreHouseVO(store.getId(), Integer.toString(store.getPlace()), store.getProvince(), store.getCity(), store.getRegion(), stockCounts, store.getUpdateTime());
            storeHouseVOList.add(StoreHouseVo);
        }
        return storeHouseVOList;
    }

    /**
     * 云平台库存管理--根据库存id获取库存信息
     */
    @Override
    public StoreHouseDTO findStoreHouse(Integer id) {
        StoreHouse storeHouse = storeHouseMapper.selectByPrimaryKey(id);
        StoreHouseDTO dto = new StoreHouseDTO();
        if (storeHouse != null) {
            storeHouse.convert(dto);
        }
        return dto;
    }

    /**
     * 云平台库存管理--下发产品（新建库存）
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void saveStoreHouse(String province, String city, String region, Integer productId, Integer count, Integer special) {
        String adminName = userCache.getCurrentAdminRealName();
        // ProductDTO product = productFeign.getProductById(productId);
        // if (product == null) {
        //     throw new BadRequestException("该产品无数据！");
        // }
        ProductDTO product = getProduct(productId);
        //产品型号，产品要求以产品型号记录库存，因为折机水机或180水机分别都是不同的产品
        String productModel = product.getCategoryName();
        StoreHouse storeHouse = new StoreHouse();
        storeHouse.setProvince(province);
        storeHouse.setCity(city);
        storeHouse.setRegion(region);
        storeHouse.setSpecial(special);
        StoreHouse store = storeHouseMapper.selectOne(storeHouse);
        Gson gson = new Gson();
        Map<String, Integer> stock = new HashMap<>();
        if (store == null) {
            store = new StoreHouse();
            store.setPlace(StorePlaceEnum.SEPARATE_STORE.value);
            store.setProvince(province);
            store.setCity(city);
            store.setRegion(region);
            stock.put(productModel, count);
            store.setStocks(stock.toString());
            store.setCreateTime(new Date());
            store.setSpecial(special);

            storeHouseMapper.insert(store);
        } else {
            stock = gson.fromJson(store.getStocks(), new TypeToken<Map<String, Integer>>() {
            }.getType());
            if (stock.get(productModel) != null) {
                stock.put(productModel, stock.get(productModel) + count);
            } else {
                stock.put(productModel, count);
            }
            store.setStocks(stock.toString());
            store.setUpdateTime(new Date());
            store.setSpecial(special);
            storeHouseMapper.updateByPrimaryKey(store);
        }
        storeHouse = new StoreHouse();
        storeHouse.setPlace(StorePlaceEnum.MAIN_STORE.value);
        StoreHouse total = storeHouseMapper.selectOne(storeHouse);
        if (total == null) {
            total = new StoreHouse();
            total.setPlace(StorePlaceEnum.MAIN_STORE.value);
            total.setProvince(province);
            total.setCity(city);
            total.setRegion(region);
            stock = new HashMap<>();
            stock.put(productModel, count);
            total.setStocks(stock.toString());
            total.setCreateTime(new Date());
            total.setSpecial(special);
            storeHouseMapper.insert(total);
        } else {
            stock = gson.fromJson(total.getStocks(), new TypeToken<Map<String, Integer>>() {
            }.getType());
            if (stock.get(productModel) != null) {
                stock.put(productModel, stock.get(productModel) - count);
            } else {
                stock.put(productModel, 100000 - count);
            }
            total.setStocks(stock.toString());
            total.setUpdateTime(new Date());
            total.setSpecial(special);
            storeHouseMapper.updateByPrimaryKey(total);
        }
        //添加库存操作记录
        StockOperation operation = new StockOperation();
        operation.setAdmin(adminName);
        operation.setOperation(StockOperationType.DELIVERY.value);
        operation.setDeviceName(product.getName());
        operation.setOperateProvince(store.getProvince());
        operation.setOperateCity(store.getCity());
        operation.setOperateRegion(store.getRegion());
        operation.setCount(count);
        operation.setCreateTime(new Date());
        operation.setSpecial(special);

        stockOperationMapper.insert(operation);

        //修改工单安装工步骤
        Map<String, Object> map = new HashMap<>();
        map.put("province", province);
        map.put("city", city);
        map.put("region", region);
        map.put("productId", productId);
        rabbitTemplate.convertAndSend(RabbitConstant.RESET_WORK_ORDER_STEP, map);
    }

    /**
     * 云平台库存管理--修改库存
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void updateStoreHouse(Integer id, Integer productId, Integer count, Integer special) {
        StoreHouse house = storeHouseMapper.selectByPrimaryKey(id);
        if (house == null) {
            throw new BadRequestException("库存不存在");
        }
        ProductDTO product = getProduct(productId);
        //产品型号，产品要求以产品型号记录库存，因为折机水机或180水机分别都是不同的产品
        String productModel = product.getCategoryName();
        Gson gson = new Gson();
        Map<String, Integer> stocks = gson.fromJson(house.getStocks(), new TypeToken<Map<String, Integer>>() {
        }.getType());

        Integer oldCount = stocks.get(productModel);
        if (oldCount == null) {
            oldCount = 0;
        }

        stocks.put(productModel, count);
        house.setStocks(stocks.toString());
        house.setUpdateTime(new Date());
        storeHouseMapper.updateByPrimaryKey(house);

        //总仓设置
        StoreHouse query = new StoreHouse();
        query.setPlace(StorePlaceEnum.MAIN_STORE.value);
        query.setSpecial(special);
        StoreHouse total = storeHouseMapper.selectOne(query);
        Map<String, Integer> stock = gson.fromJson(total.getStocks(), new TypeToken<Map<String, Integer>>() {
        }.getType());
        if (stock.get(productModel) != null) {
            stock.put(productModel, stock.get(productModel) + oldCount - count);
        } else {
            stock.put(productModel, 100000 - count);
        }
        total.setStocks(stock.toString());
        total.setUpdateTime(new Date());
        //保存总仓
        storeHouseMapper.updateByPrimaryKeySelective(total);

        //TODO 修改工单安装工步骤
        Map<String, Object> map = new HashMap<>();
        map.put("province", house.getProvince());
        map.put("city", house.getCity());
        map.put("region", house.getRegion());
        map.put("productId", productId);
        rabbitTemplate.convertAndSend(RabbitConstant.RESET_WORK_ORDER_STEP, map);
    }

    /**
     * 云平台库存管理--设置阀值
     */
    @Override
    public void updateStoreHouseMaxValues(Integer id, Integer productId, Integer count) {
        StoreHouse house = storeHouseMapper.selectByPrimaryKey(id);
        if (house == null) {
            throw new NotFoundException("该库存无数据！");
        }
        ProductDTO product = getProduct(productId);
        //产品型号，产品要求以产品型号记录库存，因为折机水机或180水机分别都是不同的产品
        String productModel = product.getCategoryName();
        Gson gson = new Gson();
        Map<String, Integer> maxValues = gson.fromJson(house.getMaxValues(), new TypeToken<Map<String, Integer>>() {
        }.getType());
        maxValues.put(productModel, count);
        house.setMaxValues(maxValues.toString());
        storeHouseMapper.updateByPrimaryKey(house);
    }

    /**
     * 云平台库存管理--删除库存
     */
    @Override
    public void deleteStoreHouse(Integer id) {
        StoreHouse house = storeHouseMapper.selectByPrimaryKey(id);
        if (house == null) {
            throw new BadRequestException("该库存id不存在！");
        }
        storeHouseMapper.deleteByPrimaryKey(id);
    }

    /**
     * 云平台库存管理--返仓
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void updateStoreHouseBack(Integer id, Integer productId, Integer count, Integer special) {
        String adminName = userCache.getCurrentAdminRealName();
        StoreHouse store = storeHouseMapper.selectByPrimaryKey(id);
        StoreHouse house = new StoreHouse();
        //总仓位置
        house.setPlace(StorePlaceEnum.MAIN_STORE.value);
        house.setSpecial(special);
        StoreHouse total = storeHouseMapper.selectOne(house);
        Gson gson = new Gson();
        Map<String, Integer> stocks = gson.fromJson(store.getStocks(), new TypeToken<Map<String, Integer>>() {
        }.getType());
        Map<String, Integer> totals = gson.fromJson(total.getStocks(), new TypeToken<Map<String, Integer>>() {
        }.getType());
        ProductDTO product = getProduct(productId);
        //产品型号，产品要求以产品型号记录库存，因为折机水机或180水机分别都是不同的产品
        String productModel = product.getCategoryName();
        if (stocks.get(productModel) != null) {
            if (stocks.get(productModel) >= count) {
                stocks.put(productModel, stocks.get(productModel) - count);
                store.setStocks(stocks.toString());
                store.setUpdateTime(new Date());
                storeHouseMapper.updateByPrimaryKey(store);
                totals.put(productModel, totals.get(productModel) + count);
                total.setStocks(totals.toString());
                total.setUpdateTime(new Date());
                storeHouseMapper.updateByPrimaryKey(total);
                //添加库存操作记录
                StockOperation operation = new StockOperation();
                operation.setAdmin(adminName);
                operation.setOperation(StockOperationType.RETURNING.value);
                operation.setDeviceName(product.getName());
                operation.setOperateProvince(store.getProvince());
                operation.setOperateCity(store.getCity());
                operation.setOperateRegion(store.getRegion());
                operation.setCount(count);
                operation.setCreateTime(new Date());
                operation.setSpecial(special);

                stockOperationMapper.insert(operation);
            } else {
                throw new BadRequestException("该产品的返仓数量不能大于库存数量！");
            }
        } else {
            throw new BadRequestException("该产品的库存数量为空！");
        }
    }

    /**
     * 云平台库存管理--调拨
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void updateStoreHouseTransfer(Integer id, String province, String city, String region, Integer productId, Integer count, Integer special) {
        String adminName = userCache.getCurrentAdminRealName();
        ProductDTO waterDevice = productFeign.getProductById(productId);
        if (waterDevice == null) {
            throw new BadRequestException("该产品无数据！");
        }
        StoreHouse originalStore = storeHouseMapper.selectByPrimaryKey(id);
        Gson gson = new Gson();
        Map<String, Integer> stocks = gson.fromJson(originalStore.getStocks(), new TypeToken<Map<String, Integer>>() {
        }.getType());
        ProductDTO product = getProduct(productId);
        //产品型号，产品要求以产品型号记录库存，因为折机水机或180水机分别都是不同的产品
        String productModel = product.getCategoryName();
        if (stocks.get(productModel) >= count) {
            StoreHouse storeHouse = new StoreHouse();
            storeHouse.setProvince(province);
            storeHouse.setCity(city);
            storeHouse.setRegion(region);
            storeHouse.setSpecial(special);
            StoreHouse store = storeHouseMapper.selectOne(storeHouse);
            if (store != null) {
                stocks.put(productModel, stocks.get(productModel) - count);
                originalStore.setStocks(stocks.toString());
                originalStore.setUpdateTime(new Date());
                storeHouseMapper.updateByPrimaryKey(originalStore);
                Map<String, Integer> stocks1 = gson.fromJson(store.getStocks(), new TypeToken<Map<String, Integer>>() {
                }.getType());
                if (stocks1.get(productModel) == null) {
                    stocks1.put(productModel, count);
                } else {
                    stocks1.put(productModel, stocks1.get(productModel) + count);
                }
                store.setStocks(stocks1.toString());
                store.setUpdateTime(new Date());
                storeHouseMapper.updateByPrimaryKey(store);
                storeHouse = new StoreHouse();
                //总仓位置
                storeHouse.setPlace(StorePlaceEnum.MAIN_STORE.value);
                storeHouse.setSpecial(special);
                StoreHouse place = storeHouseMapper.selectOne(storeHouse);
                place.setUpdateTime(new Date());
                storeHouseMapper.updateByPrimaryKey(place);

                //修改工单安装工步骤
                Map<String, Object> map = new HashMap<>();
                map.put("province", province);
                map.put("city", city);
                map.put("region", region);
                map.put("productId", productId);
                rabbitTemplate.convertAndSend(RabbitConstant.RESET_WORK_ORDER_STEP, map);

                //添加库存操作记录
                StockOperation operation = new StockOperation();
                operation.setAdmin(adminName);
                operation.setOperation(StockOperationType.ALLOCATION.value);
                operation.setDeviceName(product.getName());
                operation.setOriginalProvince(originalStore.getProvince());
                operation.setOriginalCity(originalStore.getCity());
                operation.setOriginalRegion(originalStore.getRegion());
                operation.setOperateProvince(store.getProvince());
                operation.setOperateCity(store.getCity());
                operation.setOperateRegion(store.getRegion());
                operation.setCount(count);
                operation.setCreateTime(new Date());
                operation.setSpecial(special);
                stockOperationMapper.insert(operation);
            } else {
                throw new BadRequestException("该地区还没有建库存！");
            }
        } else {
            throw new BadRequestException("该产品的返仓数量不能大于库存数量！");
        }
    }

    /**
     * 描述：根据省市区和产品ID查询库存数量
     **/
    @Override
    public Integer getStoreHouseCount(String province, String city, String region, Integer productId, Integer special) {
        StoreHouse query = new StoreHouse();
        query.setProvince(province);
        query.setCity(city);
        query.setRegion(region);
        query.setSpecial(special);
        StoreHouse store = storeHouseMapper.selectOne(query);
        if (store == null) {
            return null;
        }
        Gson gson = new Gson();
        Map<String, Integer> stockMap = gson.fromJson(store.getStocks(), new TypeToken<Map<String, Integer>>() {
        }.getType());
        ProductDTO product = productFeign.getProductById(productId);
        if (product == null) {
            throw new BadRequestException("没有查询到产品信息");
        }
        //产品型号，产品要求以产品型号记录库存，因为折机水机或180水机分别都是不同的产品
        String productModel = product.getCategoryName();
        return stockMap.get(productModel) == null ? 0 : stockMap.get(productModel);
    }

    private ProductDTO getProduct(Integer productId) {
        ProductDTO product = productFeign.getProductById(productId);
      /*  if (product == null || product.getMode() != ProductModeEnum.LEASE.value || product.getActivityType() != ProductActivityType.PRODUCT_COMMON.value) {
            throw new BadRequestException("该商品无需添加库存配置");
        }*/

        if (product == null || product.getMode() != ProductModeEnum.LEASE.value || (product.getActivityType() != null && product.getActivityType() != ProductActivityType.PRODUCT_COMMON.value)) {
            throw new BadRequestException("该商品无需添加库存配置");
        }
        return product;
    }

    @Override
    public PageVO<StationCompanyStoreHouseDTO> pageStationCompanyStoreHouse(StationCompanyStoreHouseQuery query, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<StationCompanyStoreHouseDTO> page = stationCompanyStoreHouseMapper.pageStationCompanyStoreHouse(query);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public PageVO<StationStoreHouseDTO> pageStationStoreHouse(StationStoreHouseQuery query, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<StationStoreHouseDTO> page = stationStoreHouseMapper.pageStationStoreHouse(query);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public PageVO<StoreHouseAllDTO> pageStoreHouseAll(StoreHouseAllQuery query, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<StoreHouseAllDTO> page = storeHouseAllMapper.pageStoreHouseAll(query);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public void updateStockCount(Integer id, Integer stockCount) {
        if (stockCount < 0) {
            throw new BadRequestException("库存数量不能低于0 !");
        }
        StoreHouseAll storeHouseAll = storeHouseAllMapper.selectByPrimaryKey(id);
        if (storeHouseAll == null) {
            throw new BadRequestException("修改的库存不存在！");
        }
        Date now = new Date();
        StoreHouseAll update = new StoreHouseAll();
        update.setId(id);
        update.setStockCount(stockCount);
        int count = storeHouseAllMapper.updateByPrimaryKeySelective(update);
        if (count != 1) {
            throw new YimaoException("库存修改失败！");
        }
        //记录操作日志
        //查询物资信息
        GoodsMaterials goodsMaterials = goodsMaterialsMapper.selectByPrimaryKey(storeHouseAll.getGoodsId());
        if (goodsMaterials == null) {
            throw new YimaoException("物资信息不存在！");
        }
        StoreHouseOperationLogDTO storeHouseOperationLog = new StoreHouseOperationLogDTO();
        storeHouseOperationLog.setGoodsId(storeHouseAll.getGoodsId());
        storeHouseOperationLog.setGoodsName(goodsMaterials.getName());
        storeHouseOperationLog.setOrigCount(storeHouseAll.getStockCount());
        storeHouseOperationLog.setDestCount(stockCount);
        storeHouseOperationLog.setObjectType(StoreHouseOperationSubObjectEnum.STORE_HOUSE_ALL.value);
        storeHouseOperationLog.setOperationType(StoreHouseOperationTypeEnum.STORE_HOUSE_UPDATE_COUNT.value); //操作类型 -后台修改库存
        storeHouseOperationLog.setCreateTime(now);
        rabbitTemplate.convertAndSend(RabbitConstant.STORE_HOUSE_OPERATION_LOG, storeHouseOperationLog);
    }

    @Override
    public GoodsMaterialsDTO checkStationGoodsIsHaveStock(Integer stationId, Integer goodsId) {
        //查询该服务站是否拥有该物资库存
        StationStoreHouse query = new StationStoreHouse();
        query.setStationId(stationId);
        query.setGoodsId(goodsId);
        StationStoreHouse stationStoreHouse = stationStoreHouseMapper.selectOne(query);

        if (stationStoreHouse == null) {
            //库存不存在，返回null
            return null;
        }
        //返回物资信息
        GoodsMaterialsDTO dto = goodsMaterialsMapper.selectGoodsById(goodsId);
        dto.setStockCount(stationStoreHouse.getStockCount());
        return dto;
    }

    /**
     * 门店库存借调/调拨
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public boolean transferStationStock(StationStockTransferDTO transfer) throws Exception {

        if (Objects.isNull(transfer.getGoodsMaterialsId())) {
            throw new BadRequestException("调拨物资id为空");
        }

        if (Objects.isNull(transfer.getTransferInStationId())) {
            throw new BadRequestException("调入门店id为空");
        }

        if (Objects.isNull(transfer.getTransferOutStationId())) {
            throw new BadRequestException("调出门店id为空");
        }

        if (Objects.isNull(transfer.getTransferStockCount()) || transfer.getTransferStockCount() <= 0) {
            throw new BadRequestException("调拨数量错误");
        }

        //查询服务站门店信息
        Station stationIn = stationMapper.selectByPrimaryKey(transfer.getTransferInStationId());
        if (stationIn == null) {
            throw new YimaoException("调入服务站门店不存在！");
        }

        //查询服务站门店信息
        Station stationOut = stationMapper.selectByPrimaryKey(transfer.getTransferOutStationId());
        if (stationOut == null) {
            throw new YimaoException("调出服务站门店不存在！");
        }

        GoodsMaterialsDTO stationOutGoods = checkStationGoodsIsHaveStock(transfer.getTransferOutStationId(), transfer.getGoodsMaterialsId());

        GoodsMaterialsDTO stationInGoods = checkStationGoodsIsHaveStock(transfer.getTransferInStationId(), transfer.getGoodsMaterialsId());

        if (Objects.isNull(stationOutGoods)) {
            throw new YimaoException("调出门店无该物资库存");
        }

        if (stationOutGoods.getStockCount().intValue() < transfer.getTransferStockCount()) {
            throw new YimaoException("调出门店该物资库存不足");
        }

        //先减后加
        int reduceRes = stationStoreHouseMapper.reduceStationStock(transfer.getTransferOutStationId(), transfer.getGoodsMaterialsId(), transfer.getTransferStockCount());

        if (reduceRes != 1) {
            log.info("减库存失败,stationId={},goodId={}", transfer.getTransferOutStationId(), stationOutGoods.getId());
            throw new YimaoException("调出门店减库存失败");
        }

        int addRes = 0;

        if (Objects.isNull(stationInGoods)) {
            //新增库存
            StationStoreHouse record = new StationStoreHouse();
            record.setStationId(transfer.getTransferInStationId());
            record.setGoodsId(transfer.getGoodsMaterialsId());
            record.setStockCount(transfer.getTransferStockCount());
            addRes = stationStoreHouseMapper.insertStationStoreHouseUnique(record);

        } else {
            //更新库存
            addRes = stationStoreHouseMapper.addInStationStock(transfer.getTransferInStationId(), transfer.getGoodsMaterialsId(), transfer.getTransferStockCount());
        }


        if (addRes != 1) {//只存在新增或者更新一条数据，否则可能存在更新失败或者重复数据
            log.info("加库存失败,stationId={},goodId={}", transfer.getTransferInStationId(), stationInGoods.getId());
            throw new YimaoException("调入门店加库存失败");
        }


        if (transfer.getType() == 1) {
            //纪录调拨操作日志
            StoreHouseOperationLogDTO storeHouseOperationLog = new StoreHouseOperationLogDTO();
            storeHouseOperationLog.setObjectId(stationIn.getId());
            storeHouseOperationLog.setObjectName(stationIn.getName());
            storeHouseOperationLog.setOrigObjectId(stationOut.getId());
            storeHouseOperationLog.setOrigObjectName(stationOut.getName());
            storeHouseOperationLog.setCount(transfer.getTransferStockCount());
            storeHouseOperationLog.setProvince(stationIn.getProvince());
            storeHouseOperationLog.setCity(stationIn.getCity());
            storeHouseOperationLog.setRegion(stationIn.getRegion());
            storeHouseOperationLog.setOrigProvince(stationOut.getProvince());
            storeHouseOperationLog.setOrigCity(stationOut.getCity());
            storeHouseOperationLog.setOrigRegion(stationOut.getRegion());
            //区域id
            Integer areaId = redisCache.get(Constant.AREA_CACHE + stationIn.getProvince() + "_" + stationIn.getCity() + "_" + stationIn.getRegion(), Integer.class);
            if (areaId == null) {
                areaId = areaService.getRegionIdByPCR(stationIn.getProvince(), stationIn.getCity(), stationIn.getRegion());
            }

            Integer origAreaId = redisCache.get(Constant.AREA_CACHE + stationOut.getProvince() + "_" + stationOut.getCity() + "_" + stationOut.getRegion(), Integer.class);
            if (origAreaId == null) {
                origAreaId = areaService.getRegionIdByPCR(stationOut.getProvince(), stationOut.getCity(), stationOut.getRegion());
            }

            storeHouseOperationLog.setAreaId(areaId);
            storeHouseOperationLog.setOrigAreaId(origAreaId);
            storeHouseOperationLog.setGoodsId(transfer.getGoodsMaterialsId());
            storeHouseOperationLog.setGoodsName(stationOutGoods.getName());
            storeHouseOperationLog.setObjectType(StoreHouseOperationSubObjectEnum.STATION.value);
            storeHouseOperationLog.setOperationType(StoreHouseOperationTypeEnum.GOODS_STOCK_TRANSFER.value);
            storeHouseOperationLog.setOperator(userCache.getCurrentAdminRealName());
            storeHouseOperationLog.setCreateTime(new Date());
            rabbitTemplate.convertAndSend(RabbitConstant.STORE_HOUSE_OPERATION_LOG, storeHouseOperationLog);
        }

        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void goodsDistribution(Integer goodsId, Integer stationId, Integer count, Integer stationCompanyId) {
        if (count < 1) {
            throw new BadRequestException("分配物资数量不能小于1！");
        }
        StationCompanyStation queryStationCompanyStation = new StationCompanyStation();
        queryStationCompanyStation.setStationCompanyId(stationCompanyId);
        queryStationCompanyStation.setStationId(stationId);
        StationCompanyStation stationCompanyStation = stationCompanyStationMapper.selectOne(queryStationCompanyStation);
        if (stationCompanyStation == null) {
            throw new BadRequestException("所选服务站门店不属于当前服务站公司！");
        }

        //查询服务站公司对于该物资的库存数量
        StationCompanyStoreHouse queryStationCompanyStoreHouse = new StationCompanyStoreHouse();
        queryStationCompanyStoreHouse.setStationCompanyId(stationCompanyId);
        queryStationCompanyStoreHouse.setGoodsId(goodsId);
        StationCompanyStoreHouse stationCompanyStoreHouse = stationCompanyStoreHouseMapper.selectOne(queryStationCompanyStoreHouse);
        if (stationCompanyStoreHouse == null) {
            throw new YimaoException("服务站公司暂无该物资库存！");
        }
        if ((stationCompanyStoreHouse.getStockCount() - count) < 0) {
            throw new YimaoException("服务站公司对该物资库存数量不足，请减少分配数量重试！");
        }
        //查询物资信息
        GoodsMaterials goodsMaterials = goodsMaterialsMapper.selectByPrimaryKey(goodsId);
        if (goodsMaterials == null) {
            throw new YimaoException("物资信息不存在！");
        }

        StationStoreHouse queryStationStoreHouse = new StationStoreHouse();
        queryStationStoreHouse.setStationId(stationId);
        queryStationStoreHouse.setGoodsId(goodsId);
        StationStoreHouse stationStoreHouse = stationStoreHouseMapper.selectOne(queryStationStoreHouse);
        if (stationStoreHouse == null) {
            //服务站门店没有对于该物资的库存，新增
            StationStoreHouse insert = new StationStoreHouse();
            insert.setStationId(stationId);
            insert.setGoodsId(goodsId);
            insert.setStockCount(count);
            int res = stationStoreHouseMapper.insertStationStoreHouseUnique(insert);

            if (res != 1) {//只存在新增或者更新一条数据，否则可能存在更新失败或者重复数据
                log.info("加库存失败,stationId={},goodId={}", goodsId, stationId);
                throw new YimaoException("新增门店加库存失败");
            }
        } else {
            //存在则直接在原数量上增加库存数量
            int i = stationStoreHouseMapper.addStockCountById(stationStoreHouse.getId(), count);
            if (i < 1) {
                throw new YimaoException("门店增加库存失败！");
            }
        }

        //删减服务站公司对该商品的库存数量
        int i = stationCompanyStoreHouseMapper.pruneStockCountById(stationCompanyStoreHouse.getId(), count);
        if (i < 1) {
            throw new YimaoException("服务站公司删减库存失败！");
        }

        //======================================= 操作日志 ===========================================
        //查询服务站门店信息
        Station station = stationMapper.selectByPrimaryKey(stationId);
        if (station == null) {
            throw new YimaoException("服务站门店不存在！");
        }

        //查询数据更新后的最新数据
        stationStoreHouse = stationStoreHouseMapper.selectOne(queryStationStoreHouse);
        StoreHouseOperationLogDTO storeHouseOperationLog = new StoreHouseOperationLogDTO();
        storeHouseOperationLog.setObjectId(stationId);
        storeHouseOperationLog.setObjectName(station.getName());
        storeHouseOperationLog.setDestCount(stationStoreHouse.getStockCount());
        storeHouseOperationLog.setOrigCount(stationStoreHouse.getStockCount() - count);
        storeHouseOperationLog.setCount(count);
        storeHouseOperationLog.setGoodsId(goodsId);
        storeHouseOperationLog.setGoodsName(goodsMaterials.getName());
        storeHouseOperationLog.setProvince(station.getProvince());
        storeHouseOperationLog.setCity(station.getCity());
        storeHouseOperationLog.setRegion(station.getRegion());
        //区域id
        Integer areaId = redisCache.get(Constant.AREA_CACHE + station.getProvince() + "_" + station.getCity() + "_" + station.getRegion(), Integer.class);
        if (areaId == null) {
            areaId = areaService.getRegionIdByPCR(station.getProvince(), station.getCity(), station.getRegion());
        }
        storeHouseOperationLog.setAreaId(areaId);
        storeHouseOperationLog.setObjectType(StoreHouseOperationSubObjectEnum.STATION.value);
        storeHouseOperationLog.setOperationType(StoreHouseOperationTypeEnum.GOODS_STOCK_DISTRIBUTION.value);
        storeHouseOperationLog.setOperator(userCache.getCurrentAdminRealName());
        storeHouseOperationLog.setCreateTime(new Date());
        rabbitTemplate.convertAndSend(RabbitConstant.STORE_HOUSE_OPERATION_LOG, storeHouseOperationLog);
    }

    /**
     * 服务站门店库存编辑库存数
     *
     * @param stationId   门店id
     * @param goodsId     物资id
     * @param stockCount  修改库存数
     * @param isDefective true-修改不良品库存 false-修改良品库存
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void updateStationStockCount(Integer stationId, Integer goodsId, Integer stockCount, boolean isDefective) {

        if (Objects.isNull(goodsId)) {
            throw new BadRequestException("物资id为空");
        }

        if (Objects.isNull(stationId)) {
            throw new BadRequestException("门店id为空");
        }

        if (Objects.isNull(stockCount) || stockCount <= 0) {
            throw new BadRequestException("编辑库存数量错误");
        }

        //查询服务站门店信息
        Station station = stationMapper.selectByPrimaryKey(stationId);
        if (station == null) {
            throw new YimaoException("服务站门店不存在！");
        }

        GoodsMaterialsDTO stationGoods = checkStationGoodsIsHaveStock(stationId, goodsId);


        int res = 0;

        if (Objects.isNull(stationGoods)) {
            //新增服务站门店库存物资
            StationStoreHouse record = new StationStoreHouse();
            record.setStationId(stationId);
            record.setGoodsId(goodsId);
            if (isDefective) {
                record.setDefectiveStockCount(stockCount);
            } else {
                record.setStockCount(stockCount);
            }

            res = stationStoreHouseMapper.insertStationStoreHouseUnique(record);

        } else {
            //更新服务站门店库存物资
            Example example = new Example(StationStoreHouse.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("stationId", stationId);
            criteria.andEqualTo("goodsId", goodsId);
            StationStoreHouse update = new StationStoreHouse();

            if (isDefective) {
                update.setDefectiveStockCount(stockCount);
            } else {
                update.setStockCount(stockCount);
            }
            res = stationStoreHouseMapper.updateByExampleSelective(update, example);
        }

        if (res != 1) {//只存在新增或者更新一条数据，否则可能存在更新失败或者重复数据
            log.info("编辑库存失败,stationId={},goodId={}", stationId, goodsId);
            throw new YimaoException("编辑门店库存失败");
        }

        //操作日志
        StoreHouseOperationLogDTO storeHouseOperationLog = new StoreHouseOperationLogDTO();
        storeHouseOperationLog.setObjectId(stationId);
        storeHouseOperationLog.setObjectName(station.getName());
        storeHouseOperationLog.setDestCount(stockCount);
        storeHouseOperationLog.setOrigCount(stationGoods.getStockCount());
        storeHouseOperationLog.setGoodsId(goodsId);
        storeHouseOperationLog.setGoodsName(stationGoods.getName());
        storeHouseOperationLog.setProvince(station.getProvince());
        storeHouseOperationLog.setCity(station.getCity());
        storeHouseOperationLog.setRegion(station.getRegion());
        //区域id
        Integer areaId = redisCache.get(Constant.AREA_CACHE + station.getProvince() + "_" + station.getCity() + "_" + station.getRegion(), Integer.class);
        if (areaId == null) {
            areaId = areaService.getRegionIdByPCR(station.getProvince(), station.getCity(), station.getRegion());
        }
        storeHouseOperationLog.setAreaId(areaId);
        storeHouseOperationLog.setObjectType(StoreHouseOperationSubObjectEnum.STATION.value);
        storeHouseOperationLog.setOperationType(StoreHouseOperationTypeEnum.STORE_HOUSE_UPDATE_COUNT.value);
        storeHouseOperationLog.setOperator(userCache.getCurrentAdminRealName());
        storeHouseOperationLog.setCreateTime(new Date());
        rabbitTemplate.convertAndSend(RabbitConstant.STORE_HOUSE_OPERATION_LOG, storeHouseOperationLog);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void occupyStockPrune(Integer goodsId, Integer stationId, Integer count) {
        if (count <= 1) {
            throw new BadRequestException("删减的库存不能小于1 ！");
        }
        StationStoreHouse query = new StationStoreHouse();
        query.setGoodsId(goodsId);
        query.setStationId(stationId);
        StationStoreHouse stationStoreHouse = stationStoreHouseMapper.selectOne(query);
        if (stationStoreHouse == null) {
            throw new BadRequestException("服务站门店暂无该物资库存！");
        }
        if (stationStoreHouse.getOccupyStockCount() - count < 0) {
            throw new YimaoException("服务站门店对该物资占用库存数量不足！");
        }
        //删减占用库存
        int i = stationStoreHouseMapper.reduceOccupyStockCount(stationStoreHouse.getId(), count);
        if (i != 1) {
            throw new YimaoException("删减服务站占用库存失败！");
        }

        Station station = stationMapper.selectByPrimaryKey(stationId);
        if (station == null) {
            throw new YimaoException("服务站门店不存在！");
        }
        GoodsMaterials goodsMaterials = goodsMaterialsMapper.selectByPrimaryKey(goodsId);
        if (goodsMaterials == null) {
            throw new YimaoException("物资信息不存在！");
        }
        //获取库存最新数据
        stationStoreHouse = stationStoreHouseMapper.selectOne(query);
        //操作日志
        StoreHouseOperationLogDTO storeHouseOperationLog = new StoreHouseOperationLogDTO();
        storeHouseOperationLog.setObjectId(stationId);
        storeHouseOperationLog.setObjectName(station.getName());
        storeHouseOperationLog.setDestCount(stationStoreHouse.getOccupyStockCount());
        storeHouseOperationLog.setOrigCount(stationStoreHouse.getOccupyStockCount() + count);
        storeHouseOperationLog.setCount(count);
        storeHouseOperationLog.setGoodsId(goodsId);
        storeHouseOperationLog.setGoodsName(goodsMaterials.getName());
        storeHouseOperationLog.setProvince(station.getProvince());
        storeHouseOperationLog.setCity(station.getCity());
        storeHouseOperationLog.setRegion(station.getRegion());
        //区域id
        Integer areaId = redisCache.get(Constant.AREA_CACHE + station.getProvince() + "_" + station.getCity() + "_" + station.getRegion(), Integer.class);
        if (areaId == null) {
            areaId = areaService.getRegionIdByPCR(station.getProvince(), station.getCity(), station.getRegion());
        }
        storeHouseOperationLog.setAreaId(areaId);
        storeHouseOperationLog.setObjectType(StoreHouseOperationSubObjectEnum.STATION.value);
        storeHouseOperationLog.setOperationType(StoreHouseOperationTypeEnum.GOODS_PRUNE_STATION_OCCUPY_STOCK.value);
        storeHouseOperationLog.setOperator(userCache.getCurrentAdminRealName());
        storeHouseOperationLog.setCreateTime(new Date());
        rabbitTemplate.convertAndSend(RabbitConstant.STORE_HOUSE_OPERATION_LOG, storeHouseOperationLog);

    }

    @Override
    public void addStock(Integer goodsId, Integer stationId, Integer count) {
        if (count <= 1) {
            throw new BadRequestException("增加的库存数量不能小于1 ！");
        }
        StationStoreHouse query = new StationStoreHouse();
        query.setGoodsId(goodsId);
        query.setStationId(stationId);
        StationStoreHouse stationStoreHouse = stationStoreHouseMapper.selectOne(query);
        if (stationStoreHouse == null) {
            throw new BadRequestException("服务站门店暂无该物资库存！");
        }
        if (stationStoreHouse.getOccupyStockCount() - count < 0) {
            throw new YimaoException("服务站门店对该物资占用库存数量不足！");
        }
        //扣减占用库存
        int i = stationStoreHouseMapper.reduceOccupyStockCount(stationStoreHouse.getId(), count);
        if (i != 1) {
            throw new YimaoException("服务站门店扣减占用库存失败！");
        }
        //增加可用库存
        i = stationStoreHouseMapper.addStockCountById(stationStoreHouse.getId(), count);
        if (i != 1) {
            throw new YimaoException("服务站门店增加可用库存失败！");
        }
        Station station = stationMapper.selectByPrimaryKey(stationId);
        if (station == null) {
            throw new YimaoException("服务站门店不存在！");
        }
        GoodsMaterials goodsMaterials = goodsMaterialsMapper.selectByPrimaryKey(goodsId);
        if (goodsMaterials == null) {
            throw new YimaoException("物资信息不存在！");
        }
        //获取库存最新数据
        stationStoreHouse = stationStoreHouseMapper.selectOne(query);
        //操作日志
        StoreHouseOperationLogDTO storeHouseOperationLog = new StoreHouseOperationLogDTO();
        storeHouseOperationLog.setObjectId(stationId);
        storeHouseOperationLog.setObjectName(station.getName());
        storeHouseOperationLog.setDestCount(stationStoreHouse.getStockCount());
        storeHouseOperationLog.setOrigCount(stationStoreHouse.getStockCount() - count);
        storeHouseOperationLog.setCount(count);
        storeHouseOperationLog.setGoodsId(goodsId);
        storeHouseOperationLog.setGoodsName(goodsMaterials.getName());
        storeHouseOperationLog.setProvince(station.getProvince());
        storeHouseOperationLog.setCity(station.getCity());
        storeHouseOperationLog.setRegion(station.getRegion());
        //区域id
        Integer areaId = redisCache.get(Constant.AREA_CACHE + station.getProvince() + "_" + station.getCity() + "_" + station.getRegion(), Integer.class);
        if (areaId == null) {
            areaId = areaService.getRegionIdByPCR(station.getProvince(), station.getCity(), station.getRegion());
        }
        storeHouseOperationLog.setAreaId(areaId);
        storeHouseOperationLog.setObjectType(StoreHouseOperationSubObjectEnum.STATION.value);
        storeHouseOperationLog.setOperationType(StoreHouseOperationTypeEnum.GOODS_ADD_STATION_STOCK.value);
        storeHouseOperationLog.setOperator(userCache.getCurrentAdminRealName());
        storeHouseOperationLog.setCreateTime(new Date());
        rabbitTemplate.convertAndSend(RabbitConstant.STORE_HOUSE_OPERATION_LOG, storeHouseOperationLog);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void stockPrune(Integer goodsId, Integer stationId, Integer count) {
        if (count <= 1) {
            throw new BadRequestException("删减的库存不能小于1 ！");
        }
        StationStoreHouse query = new StationStoreHouse();
        query.setGoodsId(goodsId);
        query.setStationId(stationId);
        StationStoreHouse stationStoreHouse = stationStoreHouseMapper.selectOne(query);
        if (stationStoreHouse == null) {
            throw new BadRequestException("服务站门店暂无该物资库存！");
        }
        if (stationStoreHouse.getStockCount() - count < 0) {
            throw new YimaoException("服务站门店对该物资库存数量不足！");
        }
        //扣减可用库存
        int i = stationStoreHouseMapper.reduceStationStock(stationId, goodsId, count);
        if (i != 1) {
            throw new YimaoException("服务站门店扣减库存失败！");
        }
        //增加占用库存
        i = stationStoreHouseMapper.addOccupyStockCount(stationStoreHouse.getId(), count);
        if (i != 1) {
            throw new YimaoException("服务站门店增加占用库存失败！");
        }
        Station station = stationMapper.selectByPrimaryKey(stationId);
        if (station == null) {
            throw new YimaoException("服务站门店不存在！");
        }
        GoodsMaterials goodsMaterials = goodsMaterialsMapper.selectByPrimaryKey(goodsId);
        if (goodsMaterials == null) {
            throw new YimaoException("物资信息不存在！");
        }
        //获取库存最新数据
        stationStoreHouse = stationStoreHouseMapper.selectOne(query);
        //操作日志
        StoreHouseOperationLogDTO storeHouseOperationLog = new StoreHouseOperationLogDTO();
        storeHouseOperationLog.setObjectId(stationId);
        storeHouseOperationLog.setObjectName(station.getName());
        storeHouseOperationLog.setDestCount(stationStoreHouse.getStockCount());
        storeHouseOperationLog.setOrigCount(stationStoreHouse.getStockCount() + count);
        storeHouseOperationLog.setCount(count);
        storeHouseOperationLog.setGoodsId(goodsId);
        storeHouseOperationLog.setGoodsName(goodsMaterials.getName());
        storeHouseOperationLog.setProvince(station.getProvince());
        storeHouseOperationLog.setCity(station.getCity());
        storeHouseOperationLog.setRegion(station.getRegion());
        //区域id
        Integer areaId = redisCache.get(Constant.AREA_CACHE + station.getProvince() + "_" + station.getCity() + "_" + station.getRegion(), Integer.class);
        if (areaId == null) {
            areaId = areaService.getRegionIdByPCR(station.getProvince(), station.getCity(), station.getRegion());
        }
        storeHouseOperationLog.setAreaId(areaId);
        storeHouseOperationLog.setObjectType(StoreHouseOperationSubObjectEnum.STATION.value);
        storeHouseOperationLog.setOperationType(StoreHouseOperationTypeEnum.GOODS_PRUNE_STATION_STOCK.value);
        storeHouseOperationLog.setOperator(userCache.getCurrentAdminRealName());
        storeHouseOperationLog.setCreateTime(new Date());
        rabbitTemplate.convertAndSend(RabbitConstant.STORE_HOUSE_OPERATION_LOG, storeHouseOperationLog);
    }

    /**
     * 分页查询服务站门店退机库存纪录
     *
     * @param stationId   门店id
     * @param goodsId     物资id
     * @param isDefective true-修改不良品库存 false-修改良品库存
     */
    public PageVO<StationBackStockRecordDTO> pageStationReturnStockRecord(StationStoreHouseQuery query, Integer pageNum,
                                                                          Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<StationBackStockRecordDTO> page = stationBackStockRecordMapper.pageStationCompanyStoreHouse(query);
        return new PageVO<>(pageNum, page);

    }

    /**
     * 服务站门店退机转良品不良品
     *
     * @param stationId   门店id
     * @param goodsId     物资id
     * @param isDefective true-修改不良品库存 false-修改良品库存
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void stationReturnStockTransfer(Integer id, boolean isDefective) {

        if (Objects.isNull(id)) {
            throw new BadRequestException("退机库存纪录id为空");
        }

        StationBackStockRecord record = stationBackStockRecordMapper.selectByPrimaryKey(id);

        if (Objects.isNull(record)) {
            throw new YimaoException("退机库存纪录不存在");
        }

        if (record.getIsTransferStock()) {
            throw new YimaoException("退机库存已转移");
        }

        if (Objects.isNull(record.getStationId())) {
            throw new YimaoException("门店id为空");
        }

        //查询服务站门店信息
        Station station = stationMapper.selectByPrimaryKey(record.getStationId());
        if (station == null) {
            throw new YimaoException("服务站门店不存在！");
        }

        //获取产品水机类目
        ProductCategoryDTO categoryDto = productFeign.getProductCategoryById(record.getProductCategoryId());

        if (Objects.isNull(categoryDto)) {
            throw new YimaoException("水机类目不存在");
        }

        //获取产品水机类目上的库存物资id
        Integer goodsId = categoryDto.getStoreGoodsId();

        if (Objects.isNull(goodsId)) {
            throw new YimaoException("产品类目未绑定对应库存id");
        }
        //物资是否存在
        GoodsMaterialsDTO goods = goodsMaterialsMapper.selectGoodsById(goodsId);
        if (Objects.isNull(goods)) {
            throw new YimaoException("物资不存在");
        }

        if (GoodsCategoryEnum.DEVICE.value != goods.getType()) {
            throw new YimaoException("物资非水机设备类型");
        }

        //查询门店是否存在该物资
        StationStoreHouse queryStationStoreHouse = new StationStoreHouse();
        queryStationStoreHouse.setStationId(record.getStationId());
        queryStationStoreHouse.setGoodsId(goodsId);
        StationStoreHouse stationStoreHouse = stationStoreHouseMapper.selectOne(queryStationStoreHouse);

        //TODO 根据退机单号校验退机操作是否已完成

        int res = 0;

        if (Objects.isNull(stationStoreHouse)) {
            //新增服务站门店库存物资
            StationStoreHouse newStationStoreHouse = new StationStoreHouse();
            newStationStoreHouse.setStationId(record.getStationId());
            newStationStoreHouse.setGoodsId(goodsId);
            if (isDefective) {
                newStationStoreHouse.setDefectiveStockCount(1);
            } else {
                newStationStoreHouse.setStockCount(1);
            }

            res = stationStoreHouseMapper.insertStationStoreHouseUnique(newStationStoreHouse);

        } else {
            //更新服务站门店库存物资
            if (isDefective) {
                res = stationStoreHouseMapper.addDefectiveStockCountById(stationStoreHouse.getId(), 1);
            } else {
                res = stationStoreHouseMapper.addStockCountById(stationStoreHouse.getId(), 1);
            }


        }

        if (res != 1) {//只存在新增或者更新一条数据，否则可能存在更新失败或者重复数据
            log.info("退机库存转移失败,stationId={},goodId={}", record.getStationId(), goodsId);
            throw new YimaoException("退机库存转移失败");
        }

        //将退机工单上的库存退机返仓状态变为已转移
        StationBackStockRecord transfer = new StationBackStockRecord();
        transfer.setId(id);
        transfer.setIsTransferStock(true);
        transfer.setTransferTime(new Date());
        transfer.setTransferUserId(userCache.getUserId());
        int transferRes = stationBackStockRecordMapper.changeTransferInfo(transfer);

        if (transferRes < 1) {
            throw new YimaoException("退机库存纪录修改状态失败");
        }

        StationStoreHouse afterUpdate = stationStoreHouseMapper.selectByPrimaryKey(stationStoreHouse.getId());
        //纪录操作日志
        StoreHouseOperationLogDTO storeHouseOperationLog = new StoreHouseOperationLogDTO();
        storeHouseOperationLog.setObjectId(record.getStationId());
        storeHouseOperationLog.setObjectName(station.getName());
        storeHouseOperationLog.setCount(1);

        if (isDefective) {
            storeHouseOperationLog.setDestCount(afterUpdate.getDefectiveStockCount());
            storeHouseOperationLog.setOrigCount(afterUpdate.getDefectiveStockCount() - 1);
        } else {
            storeHouseOperationLog.setDestCount(afterUpdate.getStockCount());
            storeHouseOperationLog.setOrigCount(afterUpdate.getStockCount() - 1);
        }

        storeHouseOperationLog.setGoodsId(goodsId);
        storeHouseOperationLog.setGoodsName(goods.getName());
        storeHouseOperationLog.setProvince(station.getProvince());
        storeHouseOperationLog.setCity(station.getCity());
        storeHouseOperationLog.setRegion(station.getRegion());
        //区域id
        Integer areaId = redisCache.get(Constant.AREA_CACHE + station.getProvince() + "_" + station.getCity() + "_" + station.getRegion(), Integer.class);
        if (areaId == null) {
            areaId = areaService.getRegionIdByPCR(station.getProvince(), station.getCity(), station.getRegion());
        }
        storeHouseOperationLog.setAreaId(areaId);
        storeHouseOperationLog.setObjectType(StoreHouseOperationSubObjectEnum.STATION.value);

        if (isDefective) {
            storeHouseOperationLog.setOperationType(StoreHouseOperationTypeEnum.GOODS_STOCK_DEFECTIVE_RETURN.value);
        } else {
            storeHouseOperationLog.setOperationType(StoreHouseOperationTypeEnum.GOODS_STOCK_RETURN.value);
        }

        storeHouseOperationLog.setOperator(userCache.getCurrentAdminRealName());
        storeHouseOperationLog.setCreateTime(new Date());
        rabbitTemplate.convertAndSend(RabbitConstant.STORE_HOUSE_OPERATION_LOG, storeHouseOperationLog);
    }

    /**
     * 维修扣减门店库存
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void reduceStationMaterialStock(StationMaterialStockDTO dto) {

        for (GoodsMaterialsDTO material : dto.getMaterials()) {

            GoodsMaterialsDTO stationGoods = checkStationGoodsIsHaveStock(dto.getStationId(), material.getId());

            if (Objects.isNull(stationGoods) || stationGoods.getType() != GoodsCategoryEnum.MATERIAL.value) {
                log.error("门店没有该耗材,goodId={},stationId={}", material.getId(), dto.getStationId());
                throw new YimaoException("门店没有该耗材");
            }

            int res = stationStoreHouseMapper.reduceStationStock(dto.getStationId(), material.getId(), material.getMaterialCount());

            if (res < 1) {
                log.error("门店耗材不足,goodId={},stationId={}", material.getId(), dto.getStationId());
                throw new YimaoException("门店耗材不足");
            }

        }

    }

    /**
     * 维护扣减滤芯耗材
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void reduceStationStockByMaintenance(StationMaterialStockDTO dto) {
        //根据安装工id查询其绑定门店
        EngineerDTO engineer = userFeign.getEngineerDetailById(dto.getEngineerId());

        if (Objects.isNull(engineer)) {
            throw new YimaoException("安装工不存在");
        }

        if (Objects.isNull(engineer.getStationId())) {
            throw new YimaoException("安装工所属门店id为空");
        }

        //更新名称查询滤芯对应物料的id
        for (String filterName : dto.getFilterNames()) {
            GoodsMaterials query = new GoodsMaterials();
            query.setType(GoodsCategoryEnum.MATERIAL.value);
            query.setName(filterName);
            GoodsMaterials goods = goodsMaterialsMapper.selectOne(query);

            if (Objects.isNull(goods)) {
                log.error("耗材物资不存在该滤芯,filterName={}", filterName);
                throw new YimaoException("耗材物资不存在该滤芯");
            }

            int res = stationStoreHouseMapper.reduceStationStock(engineer.getStationId(), goods.getId(), 1);

            if (res < 1) {
                log.error("门店耗材不足,filterName={},goodId={},stationId={}", filterName, goods.getId(), engineer.getStationId());
                throw new YimaoException("门店耗材不足");
            }
        }


    }


}
