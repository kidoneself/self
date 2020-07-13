package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.MaintenanceWorkOrder;
import com.yimao.cloud.pojo.dto.order.MaintenanceDTO;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderExportDTO;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderQueryDTO;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.query.station.StationMaintenanceOrderQuery;

import org.apache.ibatis.annotations.Param;
import com.yimao.cloud.pojo.dto.order.*;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

import java.util.Map;


public interface MaintenanceWorkOrderMapper extends Mapper<MaintenanceWorkOrder> {

    void updateLasteFinishTime(Integer hour);

    Page<MaintenanceWorkOrderExportDTO> maintenanceWorkOrderExport(MaintenanceWorkOrderQueryDTO query);

    List<RenewDTO> getMaintenanceWorkOrderList(@Param("completeTime") String completeTime,
                                               @Param("engineerId") Integer engineerId,
                                               @Param("timeType") Integer timeType);

    Integer getMaintenanceWorkOrderCount(@Param("engineerId") Integer engineerId, @Param("state") Integer state);


//    int getWorkOrderMaintenanceCount(@Param("engineerId") Integer engineerId, @Param("state") Integer state);

    List<MaintenanceDTO> listMaintenanceWorkOrderForClient(@Param("state") Integer state, @Param("engineerId") Integer engineerId, @Param("search") String search);

    List<Map<String, String>> maintenanceFilterChangeList(@Param("deviceSncode") String deviceSncode,
                                                          @Param("handselFlag") Long handselFlag,
                                                          @Param("state") Integer state,
                                                          @Param("source") Integer source,
                                                          @Param("engineerId") Integer engineerId);

    Page<MaintenanceDTO> maintenanceWorkOrderCompleteList(@Param("engineerId") Integer engineerId, @Param("sortType") Integer sortType);

    MaintenanceDTO maintenanceWorkOrderByDeviceSnCode(@Param("engineerId") Integer engineerId,
                                                      @Param("deviceSncode") String deviceSncode,
                                                      @Param("source") Integer source,
                                                      @Param("state") Integer state);

    List<MaintenanceDTO> maintenanceWorkOrderRecordDetail(@Param("engineerId") Integer engineerId, @Param("deviceSncode") String deviceSncode);

    List<Map<String, String>> maintenanceFilterChangeRecordList(@Param("deviceSncode") String deviceSncode,
                                                                @Param("engineerId") Integer engineerId,
                                                                @Param("handselFlag") Long handselFlag);

    Page<MaintenanceWorkOrderDTO> listMaintenanceOrderToStation(StationMaintenanceOrderQuery query);

    int updateStationInfoByPCR(MaintenanceWorkOrderDTO updateMaintenanceWorkOrder);

    int updateEngineerInfoByPCR(MaintenanceWorkOrderDTO updateMaintenanceWorkOrder);

    Integer getMaintenanceModelWorkOrderTotalCount(@Param("engineerId") Integer engineerId);

    int updateEngineerInfo(MaintenanceWorkOrderDTO mwo);

    List<MapOrderDTO> getMaintenanceWorkOrder(Integer engineerId);
}
