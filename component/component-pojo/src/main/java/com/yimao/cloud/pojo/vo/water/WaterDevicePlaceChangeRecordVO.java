package com.yimao.cloud.pojo.vo.water;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：水机摆放位置更换记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/23
 */
@ApiModel(description = "水机摆放位置更换记录VO")
@Getter
@Setter
public class WaterDevicePlaceChangeRecordVO {

    private Integer id;
    //设备sn码
    private String sn;
    //旧地址
    private String oldPlace;
    //新地址
    private String newPlace;
    //创建时间
    private String createTime;
    //是否锁机，Y为锁机,N为未锁机
    private String lockStateText;
    //来源方式 1-水机设备自动检测 2-移机
    private String typeText;

}
