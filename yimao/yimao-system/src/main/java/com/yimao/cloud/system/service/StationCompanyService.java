package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyExportDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyServiceAreaDTO;
import com.yimao.cloud.pojo.query.system.StationCompanyQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Lizhqiang
 * @date 2019/1/17
 */
public interface StationCompanyService {


    /**
     * 新增区县级公司
     *
     * @param dto     区县级公司
     * @param areaIds 区县级公司服务区域ID
     */
    void saveStationCompany(StationCompanyDTO dto, List<Integer> areaIds);

    /**
     * 修改区县级公司
     *
     * @param dto     区县级公司
     * @param areaIds 区县级公司服务区域ID
     */
    void updateStationCompany(StationCompanyDTO dto, List<Integer> areaIds);

    /**
     * 查询区县级公司（分页）
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param query    查询条件
     */
    PageVO<StationCompanyDTO> pageStationCompany(Integer pageNum, Integer pageSize, StationCompanyQuery query);

    /**
     * 查询区县级公司（列表）
     *
     * @param query 查询条件
     */
    List<StationCompanyDTO> listStationCompany(StationCompanyQuery query);

    /**
     * 服务站上下线
     *
     * @param id
     */
    void onlineOffline(Integer id);

    boolean isAreaUsed(String area);

    StationCompanyDTO getStationCompanyByPCR(String province, String city, String region,Integer type);

    /**
     * 服务站公司注册云签账户
     *
     * @param stationCompanyId
     * @return
     */
    String registerCompanyUser(Integer stationCompanyId);

    List<StationCompanyDTO> getStationCompanyByStationId(Integer stationId);

    /**
     * 站务系统修改服务站公司联系方式
     *
     * @param update
     */
    void updateContactInfo(StationCompanyDTO update);

    StationCompanyServiceAreaDTO getStationCompanyServiceAreaByPCR(String province, String city, String region);

    void serviceAreaContractMakeOver(List<StationCompanyServiceAreaDTO> originalStationCompanyServiceAreaList, Integer stationCompanyId, Integer stationId, Integer engineerId, Integer type);

    Set<StationCompanyServiceAreaDTO> getAfterSaleServiceArea(Integer id);

    List<StationCompanyServiceAreaDTO> getStationCompanyServiceAreaAndServiceType(Integer id);

    /**
     * 根据省市区查询售前售后门店集合
     * @param province
     * @param city
     * @param region
     * @return
     */
	List<StationCompanyDTO> getStationCompanyListByPCR(String province, String city, String region);

    List<StationCompanyDTO> getStationCompanyByLocation(String province, String city, String region);

}
