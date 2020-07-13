package com.yimao.cloud.order.mapper;

import com.yimao.cloud.order.po.PayRecord;
import com.yimao.cloud.pojo.dto.order.PayRecordDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/8.
 */
public interface PayRecordMapper extends Mapper<PayRecord> {

    /***
     * 根据主单号或者子弹号查询订单支付记录
     * @return
     */
    List<PayRecordDTO> queryPayRecordListByPayRecord(PayRecordDTO payRecordDTO);

    boolean existsWithOutTradeNo(@Param("outTradeNo") String outTradeNo);

    PayRecordDTO selectByOutTradeNo(@Param("outTradeNo") String outTradeNo);

    /**
     * 根据支付流水号，获取支付记录
     *
     * @param payTradeNo 支付流水号
     * @return dto
     */
    PayRecordDTO findPayRecordByPayTradeNo(@Param("payTradeNo") String payTradeNo);
}
