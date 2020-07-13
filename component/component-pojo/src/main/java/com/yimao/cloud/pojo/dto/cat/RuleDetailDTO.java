package com.yimao.cloud.pojo.dto.cat;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-11-17 09:47:09
 **/
@Data
public class RuleDetailDTO implements Serializable {

    private static final long serialVersionUID = -7025567152914951425L;
    private Integer pinNum;//几个人拼团
    private BigDecimal everyPrice;//单张价格
    private BigDecimal everyCutPrice;//单张让利价格
    private BigDecimal everyIncome;//单张收益
}
