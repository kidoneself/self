package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFaultDTO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceFaultVO;
import com.yimao.cloud.water.po.WaterDeviceFault;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface WaterDeviceFaultMapper extends Mapper<WaterDeviceFault> {

    boolean existsWith(@Param("deviceId") Integer deviceId, @Param("sn") String sn,
                       @Param("type") Integer type, @Param("state") Integer state, @Param("filterType") String filterType);

    Page<WaterDeviceFaultVO> selectPage(@Param("sn") String sn);

    WaterDeviceFaultDTO selectByDeviceIdAndSn(@Param("deviceId") Integer deviceId, @Param("sn") String sn, @Param("state") Integer state);
}
