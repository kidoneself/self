package com.yimao.cloud.pojo.vo.water;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


/***
 * 功能描述:水机设备分布流量分类统计VO对象
 *
 * @auther: liu yi
 * @date: 2019/6/17 15:59
 */
@ApiModel(description = "水机设备分布流量分类统计VO对象")
@Getter
@Setter
public class TrafficStatisticsVO {
    private String sn;
    private BigDecimal total;
    private BigDecimal yimaoTotal;
    private BigDecimal baiduTotal;
    private BigDecimal jingdongTotal;
    private BigDecimal kdxfTotal;
    private BigDecimal jianshiTotal;
    private BigDecimal haidaTotal;
    private BigDecimal systemTotal;
    private BigDecimal otherTotal;
    private BigDecimal yishouTotal;
}
