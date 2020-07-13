package com.yimao.cloud.system.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/11/13
 */
@Table(name = "message_overdue_remind_record")
@Getter
@Setter
public class MessageOverdueRemindRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;
    private String smsParameter;
    private String yimaoAppParameter;
    private String yimaoServiceAppParameter;

}
