package com.yimao.cloud.hra.mapper;

import com.yimao.cloud.hra.po.HraExchangeRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Lizhqiang
 * @date 2019/4/11
 */
public interface HraExchangeRecordMapper extends Mapper<HraExchangeRecord> {


    int failureCount(@Param("userId") Integer userId,
                     @Param("channel") String channel);

    int successCount(@Param("userId") Integer userId,
                     @Param("bTime") String bTime,
                     @Param("eTime") String eTime,
                     @Param("channel") String channel);
}
