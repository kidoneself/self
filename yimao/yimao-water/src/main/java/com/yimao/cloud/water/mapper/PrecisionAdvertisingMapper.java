package com.yimao.cloud.water.mapper;

import com.yimao.cloud.water.po.PrecisionAdvertising;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Set;

public interface PrecisionAdvertisingMapper extends Mapper<PrecisionAdvertising> {

    /**
     * 根据水机SN码查询
     *
     * @param snCode 水机SN码
     * @return
     */
    PrecisionAdvertising selectBySnCode(@Param("snCode") String snCode);

    void batchInsert(@Param("list") Set<String> list, @Param("advertisingId") Integer advertisingId);

    /**
     * 校验当前设备在精准投放表是否有匹配的规则
     *
     * @param list
     * @param snCode
     * @return
     */
    Boolean checkPrecisionAdvertising(@Param("list") Set<Integer> list, @Param("snCode") String snCode);
}
