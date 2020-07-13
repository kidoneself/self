package com.yimao.cloud.water.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：预处理设备
 *
 * @Author Zhang Bo
 * @Date 2019/9/24
 */
@Table(name = "pretreatment_device")
@Getter
@Setter
public class PretreatmentDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //SN码
    private String sn;
    //创建时间
    private Date createTime;
}
