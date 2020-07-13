package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.WithdrawMain;
import com.yimao.cloud.pojo.dto.order.OrderWithdrawDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawExportDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawQueryDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawSubDTO;
import com.yimao.cloud.pojo.query.order.WithdrawQuery;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/8.
 */
public interface OrderWithdrawMapper extends Mapper<WithdrawMain> {

    Page<OrderWithdrawDTO> withdrawList(@Param(value = "orderId") String orderId, @Param(value = "userId") Integer userId,
                                        @Param(value = "phone") String phone, @Param(value = "startTime") String startTime,
                                        @Param(value = "incomeType") Integer incomeType, @Param(value = "status") Integer status, @Param(value = "endTime") String endTime);

    Page<WithdrawSubDTO> withdrawAuditList(@Param(value = "partnerTradeNo") Long partnerTradeNo, @Param(value = "userId") Integer userId,
                                           @Param(value = "phone") String phone, @Param(value = "startTime") String startTime,
                                           @Param(value = "incomeType") Integer incomeType, @Param(value = "endTime") String endTime);

    Page<WithdrawSubDTO> withdrawRecordList(WithdrawQueryDTO withdrawQueryDTO);

    Page<WithdrawSubDTO> withdrawRecordDetailList(WithdrawQueryDTO withdrawQueryDTO);

    Page<WithdrawSubDTO> withdrawRecordLogList(@Param(value = "partnerTradeNo") Long partnerTradeNo, @Param(value = "withdrawFlag") Integer withdrawFlag,
                                               @Param(value = "startTime") String startTime, @Param(value = "endTime") String endTime);

    Integer getWithdrawnCount(@Param(value = "userId") Integer userId);

    Page<WithdrawExportDTO> withdrawAuditListExport(WithdrawQuery query);

    List<WithdrawExportDTO> withdrawRecordListExport(WithdrawQueryDTO withdrawQueryDTO);

    Page<WithdrawExportDTO> withdrawRecordDetailListExport(WithdrawQueryDTO withdrawQueryDTO);

    Page<WithdrawExportDTO> withdrawListExport(WithdrawQuery query);
    Page<WithdrawExportDTO> withdrawRecordListExportPage(WithdrawQueryDTO withdrawQueryDTO);
}
