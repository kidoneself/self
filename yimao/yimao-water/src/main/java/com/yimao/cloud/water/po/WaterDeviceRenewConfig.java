package com.yimao.cloud.water.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 描述：设备续费配置表
 *
 * @Author Zhang Bo
 * @Date 2019/9/24
 */
@Table(name = "water_device_renew_config")
@Getter
@Setter
public class WaterDeviceRenewConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 配置类型
     * {无需续费:-1},{未续费:1},{待续费:2},{已续费:3}
     * 标识不可变.标识名称为自定义
     */
    private Integer type;
    private String typeName;
    /**
     * 延期天数
     * 待续费的判断规则:自定义的余额范围 + (余额为0时的日期 + 延期天数)
     * 未续费的判断规则:超过待续费的范围之后.还未进行续费为未续费
     * 已续费的判断规则:余额大于自定义的余额范围.
     */
    private Integer postponeDay;
    /**
     * 设备余额范围
     */
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    /**
     * 色值
     */
    private String colorValue;
}
