package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：水机计费方式修改记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/23
 */
@ApiModel(description = "水机计费方式修改记录DTO")
@Getter
@Setter
public class WaterDeviceCostChangeRecordDTO {

    private Integer id;
    private String sn;
    private Integer oldCostId;
    private String oldCostName;
    private Integer newCostId;
    private String newCostName;
    //修改计费方式时使用了的时长
    private Integer time;
    //修改计费方式时使用了的流量
    private Integer flow;
    //修改计费方式时的余额
    private BigDecimal money;
    private String creator;
    private Date createTime;

}
