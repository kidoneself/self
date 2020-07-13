package com.yimao.cloud.water.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：水机设备配置
 *
 * @Author Zhang Bo
 * @Date 2019/9/25
 */
@Table(name = "water_device_config")
@Getter
@Setter
public class WaterDeviceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String deviceModel;
    private String ontime;
    private String offtime;
    //亮灭屏开关
    private Boolean switchAble;
    //流量阀值
    private Integer flowThreshold;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

}
