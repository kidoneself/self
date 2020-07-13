package com.yimao.cloud.water.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 描述：设备位置
 *
 * @Author Zhang Bo
 * @Date 2019/1/29 17:41
 * @Version 1.0
 */
@Table(name = "water_device_location")
@Data
public class DeviceLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

}
