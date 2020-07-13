package com.yimao.cloud.hra.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 体检步骤记录
 *
 * @author Zhang Bo
 * @date 2018/5/14.
 */
@Table(name = "hra_flow_record")
@Getter
@Setter
public class HraFlowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;
    private String ticketNo;
    private Long customerId;
    private Long stationId;
    private Date stepOneTime;
    private Date stepTwoTime;
    private Date stepThreeTime;
    private Date stepFourTime;
    private Date stepFiveTime;
    private Date stepSixTime;
    private Date stepSevenTime;
    private Boolean hasSendMessage; //是否发送模板消息

}
