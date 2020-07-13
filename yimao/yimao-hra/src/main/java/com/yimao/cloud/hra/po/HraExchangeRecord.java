package com.yimao.cloud.hra.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/4/11
 */
@Data
@Table(name = "hra_exchange_record")
public class HraExchangeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String exchangeCode;            //用户输入的兑换码

    private Integer userId;                    //兑换用户id

    private Integer num;                     //数量

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date exchangeTime;                  //兑换时间

    private String ip;

    private Integer exchangeStatus;             //1- 兑换成功  2-兑换失败

    private String channel;                 //渠道
}
