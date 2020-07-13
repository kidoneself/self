package com.yimao.cloud.water.po;

import lombok.Data;

import javax.persistence.Table;

/**
 * 描述：广告条件投放-水机型号-关联表
 *
 * @Author Zhang Bo
 * @Date 2019/1/29 17:41
 * @Version 1.0
 */
@Table(name = "conditional_advertising__model")
@Data
public class ConditionalAdvertisingModel {

    private Integer advertisingId;    //广告投放ID
    private String model;             //水机型号

}
