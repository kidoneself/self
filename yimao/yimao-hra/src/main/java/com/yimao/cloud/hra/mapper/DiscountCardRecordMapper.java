package com.yimao.cloud.hra.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.hra.po.DiscountCardRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Zhang Bo
 * @date 2018/1/22.
 */
public interface DiscountCardRecordMapper extends Mapper<DiscountCardRecord> {
    Integer listNotReceivedCardCount(@Param(value = "userId") Long userId);

    Page<DiscountCardRecord> cardGiveRecordList(@Param("userId") Long userId,
                                                @Param("beginTime") String beginTime,
                                                @Param("endTime") String endTime,
                                                @Param("received") Integer received,
                                                @Param("giveType") Integer giveType);
}
