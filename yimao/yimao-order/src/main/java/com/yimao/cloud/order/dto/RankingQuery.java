package com.yimao.cloud.order.dto;

import lombok.Data;

import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author Lizhqiang
 * @date 2019/2/26
 */
@Data
public class RankingQuery {
    private Double ranking;
    private Integer id;
    private String name;
    private BigDecimal money;
    private String province;
    private String city;
    private String region;
}
