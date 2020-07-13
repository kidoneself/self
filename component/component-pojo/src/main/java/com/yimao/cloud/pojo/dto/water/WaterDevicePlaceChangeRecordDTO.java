package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;

/**
 * 描述：水机摆放位置更换记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/23
 */
@ApiModel(description = "水机摆放位置更换记录DTO")
@Getter
@Setter
public class WaterDevicePlaceChangeRecordDTO {

    private Integer id;
    //设备sn码
    private String sn;
    //旧地址
    private String oldPlace;
    //新地址
    private String newPlace;
    //创建时间
    private Date createTime;
    //是否锁机，Y为锁机,N为未锁机
    private String lockState;
    //来源方式 1-水机设备自动检测 2-移机
    @Column(name = "`type`")
    private Integer type;
    private String detailAddress;

}
