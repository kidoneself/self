package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.PayRecordDTO;

import java.util.Map;
import java.util.SortedMap;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
public interface PayRecordService {
    boolean existsWithOutTradeNo(String outTradeNo);

    PayRecordDTO findPayRecordByOutTradeNo(String outTradeNo);

    void saveWxPayRecord(SortedMap<String, String> map, String queueName);

    void saveAliPayRecord(Map<String, String> map, String queueName);

    PayRecordDTO findPayRecordById(Integer payId);
}
