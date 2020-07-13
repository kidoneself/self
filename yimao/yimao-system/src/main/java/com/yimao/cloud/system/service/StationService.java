package com.yimao.cloud.system.service;


import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.system.StationExportDTO;
import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;
import com.yimao.cloud.pojo.query.system.StationQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.Station;

import java.util.List;
import java.util.Set;

/**
 * @author Lizhqiang
 * @date 2019/1/22
 */
public interface StationService {

    /**
     * 新增服务站门店
     *  @param station           服务站门店
     * @param areaIds            服务区域id集合
     * @param serviceAreaList   服务站门店服务区域dto
     * @param stationCompanyIds 区县级公司ID
     */
    void saveStation(Station station, List<Integer> areaIds, List<StationServiceAreaDTO> serviceAreaList, List<Integer> stationCompanyIds);

    /**
     * 修改服务站门店
     *
     * @param station           服务站门店
     * @param serviceAreaList   服务站门店服务区域dto
     */
    void updateStation(Station station,List<Integer> areaIds, List<StationServiceAreaDTO> serviceAreaList);

    /**
     * 修改服务站门店承包信息
     *
     * @param station 服务站门店
     */
    void updateStationContractInfo(Station station);

    /**
     * 修改服务站门店经营信息
     *
     * @param station 服务站门店
     */
    void updateStationManagementInfo(Station station);

    /**
     * 服务站门店上下线
     *
     * @param id 服务站门店ID
     */
    void onlineOffline(Integer id);

    /**
     * 服务站推荐
     *
     * @param id 服务站门店ID
     */
    void recommend(Integer id);

    /**
     * 查询服务站门店信息（分页）
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param query    查询条件
     */
    PageVO<StationDTO> pageStation(Integer pageNum, Integer pageSize, StationQuery query);

    /**
     * 查询服务站门店信息（列表）
     *
     * @param query 查询条件
     */
    List<StationDTO> listStation(StationQuery query);

    List<StationDTO> findStationByLngAndLat(Double lng, Double lat, Integer online, Boolean hraIsOnline);

    /**
     * 根据省市区获取服务站ID和NAME
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */StationDTO getStationByPCR(String province, String city, String region, Integer type);

    /**
     * 根据省市区获取服务站ids
     *
     * @param province    省
     * @param city        市
     * @param region      区
     * @param stationName 名称
     * @return ids
     * @author liuhao@yimaokeji.com
     */
    List<Integer> findStationIdsByPCR(String province, String city, String region, String stationName);

    StationDTO referrerStation();


    /**
     * 服务站导出
     *
     * @param province
     * @param city
     * @param region
     * @param stationName
     * @param realName
     * @param contract
     * @param stationCompanyName
     * @param areaId
     * @param online
     * @param recommend
     * @param startTime
     * @param endTime
     * @return
     */
    List<StationExportDTO> getStationInfoToExport(StationQuery query);


    List<StationServiceAreaDTO> getStationNameByIds(Set<Integer> stationIds);

    String getStationCompanyNameById(Integer stationId);

    StationDTO getStationByDistributorId(Integer recommendId);

    /**
     * 根据服务站公司id查询所对应服务站门店
     *
     * @param stationCompanyId 服务站门店id
     * @return 服务站公司对对应的服务站门店
     */
    List<Station> getStationByStationCompanyId(Integer stationCompanyId);

    /**
     * 根据站长经销商id查询服务站是否上线
     *
     * @param distributorId
     * @return
     */
    Boolean getStationStatusByDistributorId(Integer distributorId);

    /**
     * 根据服务站id获取服务站服务区域
     *
     * @param stationId 服务站id
     * @return
     */
    List<StationServiceAreaDTO> getServiceAreaByStationId(Integer stationId);

    List<StationDTO> getAllStation();

    void updateContactInfo(StationDTO update);

    List<StationDTO> getStationListByIds(StationQuery stationQuery);

    void showStation(Integer id);
}
