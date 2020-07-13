package com.yimao.cloud.pojo.dto.cat;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-11-17 09:54:00
 **/
@Data
public class ActivityRuleDetailDTO implements Serializable {

    private static final long serialVersionUID = 2841477699091727870L;
    private Integer id;
    private Integer ruleId;//规则id
    private Integer num;//人数
    private BigDecimal price;//价格
    private Boolean hasPay;//是否可支付
}
