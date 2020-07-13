package com.yimao.cloud.pojo.dto.system;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：地区信息
 *
 */
@Getter
@Setter
public class AreaInfoDTO {
    private Integer provinceId;     //省id
    private Integer cityId;         //市id
    private Integer regionId;       //区id
    private String provinceName;        //省
    private String cityName;            //市
    private String regionName;          //区
}
