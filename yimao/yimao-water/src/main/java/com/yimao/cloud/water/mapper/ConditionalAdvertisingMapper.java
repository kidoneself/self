package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.water.ConditionalAdvertisingDTO;
import com.yimao.cloud.pojo.dto.water.FilterAdvertisingDTO;
import com.yimao.cloud.water.po.ConditionalAdvertising;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ConditionalAdvertisingMapper extends Mapper<ConditionalAdvertising> {

    Page<ConditionalAdvertisingDTO> pageQueryList(@Param("platform") Integer platform);

    Page<ConditionalAdvertisingDTO> pageOwnList(@Param("screenLocation") Integer screenLocation,
                                                @Param("startTime") Date startTime,
                                                @Param("endTime") Date endTime,
                                                @Param("effectiveTime") Date effectiveTime,
                                                @Param("invalidTime") Date invalidTime,
                                                @Param("name") String name);

    List<ConditionalAdvertisingDTO> listByDeviceInfo(@Param("areaIds") List<Integer> areaIds,
                                                     @Param("model") String model,
                                                     @Param("connectionType") Integer connectionType,
                                                     @Param("currentTime") Date currentTime,
                                                     @Param("adSet") Set<String> adSet,
                                                     @Param("locationTab") String locationTab,
                                                     @Param("deviceGroup") Integer deviceGroup);

    Set<FilterAdvertisingDTO> checkAdvertisingExists(@Param("areaIds") Set<Integer> areaIds,
                                                     @Param("models") Set<String> models,
                                                     @Param("platform") Integer platform,
                                                     @Param("ownAdslotId") String ownAdslotId,
                                                     @Param("adslotId") String adslotId,
                                                     @Param("screenLocation") Integer screenLocation,
                                                     @Param("advertisingType") Integer advertisingType,
                                                     @Param("connectionType") Integer connectionType,
                                                     @Param("afterConnectionType") Integer afterConnectionType,
                                                     @Param("effectiveBeginTime") Date effectiveBeginTime,
                                                     @Param("effectiveEndTime") Date effectiveEndTime,
                                                     @Param("id") Integer id,
                                                     @Param("locationTab") String locationTab,
                                                     @Param("deviceGroup") Integer deviceGroup);

    Set<String> selectAdslotId(@Param("areaIds") List<Integer> areaIds,
                               @Param("models") Set<String> models,
                               @Param("connectionType") Integer connectionType,
                               @Param("effectiveBeginTime") Date effectiveBeginTime,
                               @Param("effectiveEndTime") Date effectiveEndTime);

    Boolean checkAdvertisingId(@Param("areaIds") List<Integer> areaIds,
                               @Param("models") Set<String> models,
                               @Param("platform") Integer platform,
                               @Param("ownAdslotId") String ownAdslotId,
                               @Param("adslotId") String adslotId,
                               @Param("screenLocation") Integer screenLocation,
                               @Param("advertisingType") Integer advertisingType,
                               @Param("connectionType") Integer connectionType,
                               @Param("afterConnectionType") Integer afterConnectionType,
                               @Param("effectiveBeginTime") Date effectiveBeginTime,
                               @Param("effectiveEndTime") Date effectiveEndTime,
                               @Param("id") Integer id,
                               @Param("locationTab") String locationTab,
                               @Param("snCode") String snCode,
                               @Param("deviceGroup") Integer deviceGroup);

    List<ConditionalAdvertisingDTO> listPrecisionByDeviceInfo(@Param("snCode") String snCode,
                                                              @Param("connectionType") Integer connectionType,
                                                              @Param("currentTime") Date currentTime,
                                                              @Param("locationTab") String locationTab,
                                                              @Param("deviceGroup") Integer deviceGroup);

    /**
     * 根据条件判断是否有绑定投放策略
     *
     * @param platformId
     * @return
     */
    Boolean selectAdvertsing(@Param("platformId") Integer platformId,
                             @Param("ownAdslotId") String ownAdslotId,
                             @Param("adslotId") String adslotId);

    int update(ConditionalAdvertising advertising);
}
