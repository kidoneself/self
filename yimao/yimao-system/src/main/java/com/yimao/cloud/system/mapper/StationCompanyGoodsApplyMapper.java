package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.StationCompanyGoodsApplyDTO;
import com.yimao.cloud.system.po.StationCompanyGoodsApply;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;

public interface StationCompanyGoodsApplyMapper extends Mapper<StationCompanyGoodsApply> {

    Page<StationCompanyGoodsApplyDTO> pageGoodsApplyFirstAudit(@Param("province") String province, @Param("city") String city, @Param("region") String region, @Param("categoryId") Integer categoryId);

    Page<StationCompanyGoodsApplyDTO> pageGoodsApplyAfterAudit(@Param("province") String province, @Param("city") String city, @Param("region") String region, @Param("categoryId") Integer categoryId);

    Page<StationCompanyGoodsApplyDTO> pageGoodsApplyTwoAudit(@Param("province") String province, @Param("city") String city, @Param("region") String region, @Param("categoryId") Integer categoryId, @Param("status") Integer status, @Param("isAfterAudit") Integer isAfterAudit);

    Page<StationCompanyGoodsApplyDTO> pageGoodsApplyHistory(@Param("province") String province, @Param("city") String city, @Param("region") String region, @Param("isAfterAudit") Integer isAfterAudit);

    Page<StationCompanyGoodsApplyDTO> pageGoodsApplyStation(@Param("stationCompanyId") Integer stationCompanyId, @Param("categoryId") Integer categoryId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    Page<StationCompanyGoodsApplyDTO> pageGoodsApplyStationHistory(@Param("stationCompanyId") Integer stationCompanyId, @Param("categoryId") Integer categoryId, @Param("status") Integer status, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
