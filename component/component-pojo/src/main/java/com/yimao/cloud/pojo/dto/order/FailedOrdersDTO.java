package com.yimao.cloud.pojo.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 导入失败订单
 * @author: yu chunlei
 * @create: 2019-09-23 18:01:24
 **/
@Data
public class FailedOrdersDTO implements Serializable {

    private Long orderId;
    private String status;
    private String failReason;

}
