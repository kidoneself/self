package com.yimao.cloud.order.po;

import javax.persistence.Table;
import lombok.Data;

/**
 * @description: 经销商用户关系表，这个表主要用于统计报表，和user db 冗余
 * @author: liulin
 * @create: 2019-02-22 16:21
 **/
@Data
@Table(name = "station_user_distributor")
public class StationUserDistributor {

    //主键
    private Integer id;
    //用户id
    private Integer userId;
    //经销商id
    private Integer dealerId;
    //经销商名称
    private String dealerName;
    //服务站ID
    private Integer stationId;
    //服务站名称
    private String stationName;
    //企业版子账号的父经销商ID
    private Integer pid;


}
