package com.yimao.cloud.water.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：水机设备摆放位置
 *
 * @Author Zhang Bo
 * @Date 2019/8/19
 */
@Table(name = "water_device_location")
@Getter
@Setter
public class WaterDeviceLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

}
