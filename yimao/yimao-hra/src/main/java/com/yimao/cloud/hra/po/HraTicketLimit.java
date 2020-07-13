package com.yimao.cloud.hra.po;

import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 优惠卡使用次数限制配置类
 *
 * @author Zhang Bo
 * @date 2018/6/27.
 */
@Table(name = "hra_ticket_limit")
@Data
public class HraTicketLimit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer limitType;
    private Integer times;
    private String ticketType;

}
