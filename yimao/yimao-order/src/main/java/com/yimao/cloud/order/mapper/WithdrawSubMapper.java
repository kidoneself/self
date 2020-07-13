package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.WithdrawSub;
import com.yimao.cloud.pojo.dto.order.WithdrawSubDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Chen Hui Yang
 * @date 2018/12/27
 */
public interface WithdrawSubMapper extends Mapper<WithdrawSub> {


    int getWithdrawnCount(Integer userId);

    BigDecimal getWithdrawnAmountToday(Integer userId);

    int updateCashRecordFlagAndStatus(@Param("partnerTradeNo") List<Long> partnerTradeNo,
                                      @Param("flag") Integer flag,
                                      @Param("status") Integer status,
                                      @Param("withdrawTime") Date withdrawTime,
                                      @Param("auditTime") Date auditTime,
                                      @Param("oldStatus") Integer oldStatus,
                                      @Param("reason") String reason,
                                      @Param("updater") String updater);

    Page<WithdrawSubDTO> listReviewCashRecord(WithdrawSubDTO withdrawSubDTO);

    BigDecimal getWithdrawnAmount(@Param("userId") Integer userId, @Param("incomeType") Integer incomeType);
}
