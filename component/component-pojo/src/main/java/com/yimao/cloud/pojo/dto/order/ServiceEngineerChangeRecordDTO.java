package com.yimao.cloud.pojo.dto.order;

import lombok.Data;

import java.util.Date;

/**
 * 安装工单、维修工单、维护工单、移机工单、退机工单 服务人员变更记录
 */
@Data
public class ServiceEngineerChangeRecordDTO {

    private Integer id;
    //工单号
    private String workOrderNo;
    //工单类型 1-安装工单、2-维修工单、3-维护工单、4-退机工单、5-移机工单（拆机） 6-移机工单（装机）
    private Integer workOrderType;
    //来源端 1-站务系统
    private Integer source;
    //原安装工id
    private Integer origEngineerId;
    //原安装工姓名
    private String origEngineerName;
    //更变后安装工id
    private Integer destEngineerId;
    //更变后安装工姓名
    private String destEngineerName;
    //操作人
    private String operator;
    //操作时间
    private Date time;

}
