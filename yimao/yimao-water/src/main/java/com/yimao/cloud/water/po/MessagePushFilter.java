package com.yimao.cloud.water.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：消息推送过滤配置
 *
 * @Author Zhang Bo
 * @Date 2019/9/24
 */
@Table(name = "message_push_filter")
@Getter
@Setter
public class MessagePushFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String province;
    private String city;
    private String region;
    private String deviceModel;
    //过滤时间
    private int hours;
    //故障类型：1-余额不足；2-制水故障；3-TDS异常；4-滤芯报警；5-阀值提醒；6-续费超期；
    private int type;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

}
