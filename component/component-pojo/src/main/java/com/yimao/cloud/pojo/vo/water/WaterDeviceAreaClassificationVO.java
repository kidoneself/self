package com.yimao.cloud.pojo.vo.water;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：水机设备区域分类统计VO对象
 *
 * @Author Zhang Bo
 * @Date 2019/4/22
 */
@ApiModel(description = "水机设备区域分类统计VO对象")
@Getter
@Setter
public class WaterDeviceAreaClassificationVO {

    private String name;
    private Integer amount;

}
