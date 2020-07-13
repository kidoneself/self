package com.yimao.cloud.system.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.system.StationExportDTO;
import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;
import com.yimao.cloud.pojo.query.system.StationQuery;
import com.yimao.cloud.system.po.Station;

import tk.mybatis.mapper.common.Mapper;

/**
 * @author Lizhqiang
 * @date 2019/1/22
 */

public interface StationMapper extends Mapper<Station> {

    /**
     * 查询服务站门店信息（分页）
     *
     * @param query 查询条件
     */
    Page<StationDTO> listStation(StationQuery query);

    /**
     * 根据省市区获取服务站ID和NAME
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    List<StationDTO> selectStationByPCR(@Param("province") String province,
                                        @Param("city") String city,
                                        @Param("region") String region,
                                        @Param("type") Integer type);

    List<Integer> getStationCompanyId(@Param("id") Integer id);

    /**
     * 根据省市区获取服务站ids
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    List<Integer> findStationIdsByPCR(@Param("province") String province,
                                      @Param("city") String city,
                                      @Param("region") String region,
                                      @Param("stationName") String stationName);

    List<StationExportDTO> getStationInfoToExport(StationQuery query);

    /**
     * 根据服务站ids查询服务站名称
     *
     * @param ids
     * @return
     */
    List<StationServiceAreaDTO> getStationNameByIds(@Param("ids") Collection ids);

    String getStationCompanyNameByStationId(Integer stationId);

    List<String> listOldId();

    void deleteByOldId(@Param("oldId") String oldId);

    List<Integer> getStationIds();

    Page<StationDTO> getFinalStations(StationQuery query);
    
    /***
     * 获取服务站公司下的所有有售后权限的服务站
     * @param stationCompanyId
     * @return
     */
    List<StationDTO> getStationByCompanyId(@Param("stationCompanyId") Integer stationCompanyId);

    /**
     * 获取服务站公司所对应的服务站门店信息 （ps  这边暂只获取门店的站长信息，若需要添加其他信息调用者自添）
     * @param stationCompanyId
     * @return
     */
    List<Station> getStationByStationCompanyId(Integer stationCompanyId);

    Boolean getStationStatusByDistributorId(Integer distributorId);

	List<StationDTO> getAllStation();

	List<StationDTO> getStationListByIds(StationQuery stationQuery);

	/**
	 * 站务系统，根据服务站门店查询其售前售后服务区域
	 * @param stationId
	 * @return
	 */
	List<StationServiceAreaDTO> getAreaTypeByStationId(Integer stationId);
	
	/***
	 * 根据服务站公司获取和售后类型获取服务站信息
	 * @param stationCompanyId
	 * @param type
	 * @return
	 */
	List<StationDTO> getAfterServiceStationByStationCompanyId(@Param("stationCompanyId") Integer stationCompanyId,@Param("type") Integer type);
}
