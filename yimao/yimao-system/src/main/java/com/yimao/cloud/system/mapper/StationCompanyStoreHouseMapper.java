package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.StationCompanyStoreHouseDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyStoreHouseExportDTO;
import com.yimao.cloud.pojo.dto.system.StationStoreHouseDTO;
import com.yimao.cloud.pojo.query.system.StationCompanyStoreHouseQuery;
import com.yimao.cloud.pojo.query.system.StationStoreHouseQuery;
import com.yimao.cloud.system.po.GoodsCategory;
import com.yimao.cloud.system.po.StationCompanyStoreHouse;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StationCompanyStoreHouseMapper extends Mapper<StationCompanyStoreHouse> {

    Page<StationCompanyStoreHouseDTO> pageStationCompanyStoreHouse(StationCompanyStoreHouseQuery query);

    int pruneStockCountById(@Param("id") Integer id, @Param("count") Integer count);

    int addStockCountById(@Param("id") Integer id, @Param("count") Integer count);

    int save(@Param("stationCompanyId")Integer stationCompanyId, @Param("goodsId")Integer goodsId, @Param("count")Integer count);

    List<StationCompanyStoreHouseExportDTO> exportStationCompanyStoreHouse(StationCompanyStoreHouseQuery stationCompanyStoreHouseQuery);
}