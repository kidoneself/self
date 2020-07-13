package com.yimao.cloud.water.mapper;

import com.yimao.cloud.water.po.PretreatmentDevice;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/9/24
 */
public interface PretreatmentDeviceMapper extends Mapper<PretreatmentDevice> {

    PretreatmentDevice selectBySn(@Param("sn") String sn);
}
