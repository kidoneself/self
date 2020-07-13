package com.yimao.cloud.hra.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.hra.po.HraDevice;
import com.yimao.cloud.pojo.dto.hra.HraDeviceExportDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceQuery;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2017/12/6.
 */
public interface HraDeviceMapper extends Mapper<HraDevice> {
    Page<HraDevice> listHraDevice(@Param("province") String province,
                                  @Param("city") String city,
                                  @Param("region") String region,
                                  @Param("recommended") Integer recommended);

    int batchDelete(List<Integer> list);

    Page<HraDevice> findHraDevice(HraDeviceQuery query);

    Page<HraDeviceExportDTO> exportDevice(HraDeviceQuery hraDeviceQuery);

    List<Integer> getDeviceStatus(Integer stationId);

    List<Integer> getHraStationIds();
}
