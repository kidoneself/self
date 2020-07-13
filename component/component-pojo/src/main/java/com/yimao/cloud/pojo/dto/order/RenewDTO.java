package com.yimao.cloud.pojo.dto.order;

import lombok.Data;

/**
 * @description: 经营报表-续费订单相关
 * @author: yu chunlei
 * @create: 2019-08-29 13:50:32
 **/
@Data
public class RenewDTO {

    private String costNameType;
    private String categoryName;
    private Integer orderNum;
}
