package com.yimao.cloud.pojo.dto.order;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhilin.he
 * @description  订单状态、数量
 * @date 2019/8/7 16:37
 **/
@Getter
@Setter
public class OrderCountDTO {

    private Integer operationType;     //操作类型: 0-待付款 1-待发货 2-待收货
    private Integer count;             //数量
    private Integer status;            //状态：0-待付款；1-待审核；2-待发货；3-待出库；4-待收货；5-交易成功；6-售后中；7-交易关闭；8-已取消；
}
