package com.yimao.cloud.hra.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 评估劵
 *
 * @author Zhang Bo
 * @date 2017/11/29.
 */

@Table(name = "hra_ticket_life_cycle")
@Data
public class HraTicketLifeCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String cardId;       //体检卡号
    private String ticketNo;   //体检劵
    private Integer origUserId;  //赠送者id
    private Integer destUserId;  //接收者id
    private Date handselTime;  //赠送时间
    private Date receiveTime;  //接收时间
    private Long handselFlag;  //  赠送标志位 - 赠送时间的毫秒值
    private Integer expiredFlag;//过期标记 0-未过期 1-在规定时间内领取 2-已过期 3-赠送过程中体检卡劵过期

}
