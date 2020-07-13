package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Lizhqiang
 * @date 2019/11/26
 */
@Data
public class HraDeviceQuery implements Serializable {
    String province;
    String city;
    String region;
    String name;
    Integer deviceType;
    String deviceId;
    Integer online;
    String minTime;
    String maxTime;


}
