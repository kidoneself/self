package com.yimao.cloud.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.StationStoreHouseDTO;
import com.yimao.cloud.pojo.query.system.StationStoreHouseQuery;
import com.yimao.cloud.system.po.StationCompanyStoreHouse;
import com.yimao.cloud.system.po.StationStoreHouse;

import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import tk.mybatis.mapper.common.Mapper;

public interface StationStoreHouseMapper extends Mapper<StationStoreHouse> {

    Page<StationStoreHouseDTO> pageStationStoreHouse(StationStoreHouseQuery query);

    /**
     * 扣减良品门店库存
     * @param transferOutStationId 扣减门店id
     * @param goodsMaterialsId 扣减库存物资id
     * @param transferStockCount 扣减数
     * @return
     */
	int reduceStationStock(@Param("transferOutStationId")Integer transferOutStationId, 
						   @Param("goodsMaterialsId")Integer goodsMaterialsId, 
						   @Param("transferStockCount")Integer transferStockCount);

	/**
	 * 增加良品门店库存
	 * @param transferInStationId 增加门店id
	 * @param goodsMaterialsId	增加库存物资id
	 * @param transferStockCount 增加数
	 * @return
	 */
	int addInStationStock(@Param("transferInStationId")Integer transferInStationId, 
					      @Param("goodsMaterialsId")Integer goodsMaterialsId, 
					      @Param("transferStockCount")Integer transferStockCount);
	
	   /**
     * 扣减不良品门店库存
     * @param transferOutStationId 扣减门店id
     * @param goodsMaterialsId 扣减库存物资id
     * @param transferStockCount 扣减数
     * @return
     */
	int reduceDefectiveStationStock(@Param("transferOutStationId")Integer transferOutStationId, 
						   @Param("goodsMaterialsId")Integer goodsMaterialsId, 
						   @Param("transferStockCount")Integer transferStockCount);

	/**
	 * 增加不良品门店库存
	 * @param transferInStationId 增加门店id
	 * @param goodsMaterialsId	增加库存物资id
	 * @param transferStockCount 增加数
	 * @return
	 */
	int addInDefectiveStationStock(@Param("transferInStationId")Integer transferInStationId, 
					      @Param("goodsMaterialsId")Integer goodsMaterialsId, 
					      @Param("transferStockCount")Integer transferStockCount);

	/**
	 * 新增门店物资库存（保证用一门店统一库存物资数据唯一）
	 * @param record
	 * @return
	 */
	int insertStationStoreHouseUnique(StationStoreHouse record);

	/**
	 * 
	 * @param id 门店库存id
	 * @param count 增加良品库存数
	 * @return
	 */
    int addStockCountById(@Param("id") Integer id, @Param("count") Integer count);
    /**
     * 
     * @param id 门店库存id
     * @param count 增加不良品库存数
     * @return
     */
	int addDefectiveStockCountById(@Param("id")Integer id,@Param("count") int count);

	int addOccupyStockCount(@Param("id")Integer id,@Param("count") int count);

	int reduceOccupyStockCount(@Param("id")Integer id,@Param("count") int count);

	List<StationStoreHouseDTO> availableStationDeviceStock(@Param("stationId")Integer stationId);

	List<StationStoreHouseDTO> availableStationMaterialStock(@Param("stationId")Integer stationId,@Param("adaptionModel")String adaptionModel);
}