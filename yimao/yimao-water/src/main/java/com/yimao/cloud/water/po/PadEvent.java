package com.yimao.cloud.water.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：pad事件上报
 *
 * @Author Zhang Bo
 * @Date 2019/5/10
 */
@Table(name = "pad_event")
@Getter
@Setter
public class PadEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String sn;
    //1代表人为造成  2代表设备自发造成
    private Integer type;
    //设备事件信息描述
    private String description;
    //app版本号
    private String version;
    private Date createTime;

}
