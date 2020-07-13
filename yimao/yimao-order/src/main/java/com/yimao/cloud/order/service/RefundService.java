package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.RefundRequest;

public interface RefundService {

    void refund(RefundRequest refundRequest);

}
