package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.water.EffectStatisticsDTO;
import com.yimao.cloud.water.po.EffectStatistics;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;


public interface EffectStatisticsMapper extends Mapper<EffectStatistics> {

    Page<EffectStatisticsDTO> queryEffectDetail(@Param("materielId") Integer materielId,
                                                @Param("advertisingId") Integer advertisingId,
                                                @Param("adslotId") String adslotId,
                                                @Param("time") Date time,
                                                @Param("snCode") String snCode);

    Page<EffectStatisticsDTO> queryDetail(@Param("materielId") Integer materielId,
                                          @Param("advertisingId") Integer advertisingId,
                                          @Param("adslotId") String adslotId,
                                          @Param("beginTime") Date beginTime,
                                          @Param("endTime") Date endTime);

    Page<EffectStatisticsDTO> queryEffectListByCondition(@Param("platform") Integer platform,
                                                         @Param("screenLocation") Integer screenLocation,
                                                         @Param("materielName") String materielName);

    EffectStatisticsDTO queryEffectDetailByAdvertising( @Param("advertisingId") Integer advertisingId);

    Page<EffectStatisticsDTO> queryEffectListBySn(@Param("snCode") String snCode,
                                                         @Param("beginTime") Date beginTime,
                                                         @Param("endTime") Date endTime
                                                         );

    EffectStatisticsDTO queryEffectCountBySn(@Param("snCode") String snCode,
                                                  @Param("beginTime") Date beginTime,
                                                  @Param("endTime") Date endTime);

    /**
     * 批量插入
     *
     * @param list
     */
    void insertBatch(List<EffectStatistics> list);

    /**
     *
     * @param type 类型 1:自有，2:第三方
     * @return
     */
    EffectStatisticsDTO queryYesterdayCount(@Param("type") int type);
}
