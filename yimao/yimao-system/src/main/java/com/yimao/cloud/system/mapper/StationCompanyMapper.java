package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyExportDTO;
import com.yimao.cloud.pojo.query.system.StationCompanyQuery;
import com.yimao.cloud.system.po.StationCompany;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/1/17
 */
public interface StationCompanyMapper extends Mapper<StationCompany> {

    /**
     * 查询区县级公司（分页）
     *
     * @param query 查询条件
     */
    Page<StationCompanyDTO> listStationCompany(StationCompanyQuery query);

    Integer isAreaUsed(@Param("area") String area);

    Page<StationCompanyExportDTO> getStationCompanyInfoToExport(@Param("province") String province,
                                                                @Param("city") String city,
                                                                @Param("region") String region,
                                                                @Param("name") String name,
                                                                @Param("contact") String contact,
                                                                @Param("contactPhone") String contactPhone,
                                                                @Param("areaId") Integer areaId,
                                                                @Param("online") Integer online,
                                                                @Param("signUp") Integer signUp,
                                                                @Param("startTime") Date startTime,
                                                                @Param("endTime") Date endTime,
                                                                @Param("locationProvince") String locationProvince,
                                                                @Param("locationCity") String locationCity,
                                                                @Param("locationRegion") String locationRegion,
                                                                @Param("serviceType") Integer serviceType,
                                                                @Param("type") Integer type);

    StationCompanyDTO getStationCompanyByPCR(@Param("province") String province,
                                             @Param("city") String city,
                                             @Param("region") String region,
                                             @Param("type") Integer type);

    List<StationCompanyDTO> getCompanyByPCR(@Param("province") String province,
                                            @Param("city") String city,
                                            @Param("region") String region);

    // 根据服务站门店id查询服务站公司 （ps 这边这查询了 法人，email，公司统一信用代码，如需添加调用者自增）
    List<StationCompanyDTO> getStationCompanyByStationId(@Param("stationId") Integer stationId);

    /**
     * 根据省市区查询售前售后门店集合
     *
     * @param province
     * @param city
     * @param region
     * @return
     */
    List<StationCompanyDTO> getStationCompanyListByPCR(@Param("province") String province,
                                                       @Param("city") String city,
                                                       @Param("region") String region);

    List<StationCompanyDTO> getStationCompanyByLocation(@Param("province") String province,
                                                        @Param("city") String city,
                                                        @Param("region") String region);

}
