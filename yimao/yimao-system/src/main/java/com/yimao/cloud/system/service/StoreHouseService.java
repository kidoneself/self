package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.*;
import com.yimao.cloud.pojo.query.system.StationCompanyStoreHouseQuery;
import com.yimao.cloud.pojo.query.system.StationStoreHouseQuery;
import com.yimao.cloud.pojo.query.system.StoreHouseAllQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.system.StoreHouseVO;
import com.yimao.cloud.system.po.StationBackStockRecord;

/**
 * @author zhilin.he
 * @description
 * @date 2019/4/30 16:16
 **/
public interface StoreHouseService {

    /**
     * 分页查询库存信息
     */
    PageVO<StoreHouseVO> getStoreHouseList(String province, String city, String region, Integer special, Integer pageNum, Integer pageSize);

    /**
     * 云平台库存管理--根据库存id获取库存信息
     */
    StoreHouseDTO findStoreHouse(Integer id);

    /**
     * 云平台库存管理--下发产品（新建库存）
     */
    void saveStoreHouse(String province, String city, String region, Integer productId, Integer count, Integer special);

    /**
     * 云平台库存管理--修改库存
     */
    void updateStoreHouse(Integer id, Integer productId, Integer count, Integer special);

    /**
     * 云平台库存管理--设置阀值
     */
    void updateStoreHouseMaxValues(Integer id, Integer productId, Integer count);

    /**
     * 云平台库存管理--返仓
     */
    void updateStoreHouseBack(Integer id, Integer productId, Integer count, Integer special);

    /**
     * 云平台库存管理--调拨
     */
    void updateStoreHouseTransfer(Integer id, String province, String city, String region, Integer productId, Integer count, Integer special);

    /**
     * 云平台库存管理--删除库存
     */
    void deleteStoreHouse(Integer id);

    /**
     * 描述：根据省市区和产品ID查询库存数量
     **/
    Integer getStoreHouseCount(String province, String city, String region, Integer productId, Integer special);

    PageVO<StationCompanyStoreHouseDTO> pageStationCompanyStoreHouse(StationCompanyStoreHouseQuery query, Integer pageNum, Integer pageSize);

    PageVO<StationStoreHouseDTO> pageStationStoreHouse(StationStoreHouseQuery query, Integer pageNum, Integer pageSize);

    PageVO<StoreHouseAllDTO> pageStoreHouseAll(StoreHouseAllQuery query, Integer pageNum, Integer pageSize);

    void updateStockCount(Integer id, Integer stockCount);

    GoodsMaterialsDTO checkStationGoodsIsHaveStock(Integer stationId, Integer goodsId);

	boolean transferStationStock(StationStockTransferDTO transfer) throws Exception;

    void goodsDistribution(Integer goodsId, Integer stationId, Integer count, Integer stationCompanyId);

	void updateStationStockCount(Integer stationId, Integer goodsId, Integer stockCount, boolean isDefective);

	void stationReturnStockTransfer(Integer id, boolean isDefective);

	PageVO<StationBackStockRecordDTO> pageStationReturnStockRecord(StationStoreHouseQuery query, Integer pageNum, Integer pageSize);

	void reduceStationMaterialStock(StationMaterialStockDTO dto);

    void stockPrune(Integer goodsId, Integer stationId, Integer count);

    void occupyStockPrune(Integer goodsId, Integer stationId, Integer count);

	void reduceStationStockByMaintenance(StationMaterialStockDTO dto);

    void addStock(Integer goodsId, Integer stationId, Integer count);
}
