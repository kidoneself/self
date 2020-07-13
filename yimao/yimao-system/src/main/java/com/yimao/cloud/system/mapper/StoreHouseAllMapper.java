package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.StoreHouseAllDTO;
import com.yimao.cloud.pojo.dto.system.StoreHouseAllExportDTO;
import com.yimao.cloud.pojo.query.system.StoreHouseAllQuery;
import com.yimao.cloud.system.po.StoreHouseAll;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StoreHouseAllMapper extends Mapper<StoreHouseAll> {

    Page<StoreHouseAllDTO> pageStoreHouseAll(StoreHouseAllQuery query);

    int pruneStockCountById(@Param("id") Integer id, @Param("count") Integer count);

    List<StoreHouseAllExportDTO> exportHouseAll(StoreHouseAllQuery storeHouseAllQuery);
}