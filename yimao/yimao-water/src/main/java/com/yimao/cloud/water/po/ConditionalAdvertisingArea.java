package com.yimao.cloud.water.po;

import lombok.Data;

import javax.persistence.Table;

/**
 * 描述：广告条件投放-区域-关联表
 *
 * @Author Zhang Bo
 * @Date 2019/1/29 17:41
 * @Version 1.0
 */
@Table(name = "conditional_advertising__area")
@Data
public class ConditionalAdvertisingArea {

    private Integer advertisingId;    //广告投放ID
    private Integer areaId;           //区域ID
    private Integer areaLevel;        //区域级别
    private String areaName;          //区域名称
    private Integer pid;          //父级id

}
