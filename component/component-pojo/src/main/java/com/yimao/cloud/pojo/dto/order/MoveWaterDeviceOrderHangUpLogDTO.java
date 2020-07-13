package com.yimao.cloud.pojo.dto.order;

import lombok.Data;

import java.util.Date;

/**
 * 移机工单挂单记录
 *
 * @author Liu Long Jie
 * @date 2020-6-29 10:41:11
 */
@Data
public class MoveWaterDeviceOrderHangUpLogDTO {

    private Integer id;
    //移机工单id
    private String moveWaterDeviceOrderId;
    //操作安装工id
    private Integer engineerId;
    //操作安装工姓名
    private String engineerName;
    //挂单类型 1-拆机挂单 2-移入挂单
    private Integer type;
    //原预约时间（开始）
    private Date origStartTime;
    //原预约时间（结束）
    private Date origEndTime;
    //更改后预约时间（开始）
    private Date destStartTime;
    //更改后预约时间（结束）
    private Date destEndTime;
    //操作时间
    private Date operationTime;

}
