package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 描述：水机设备列表查询条件（站务系统）
 *
 * @Author Liu Long Jie
 * @Date 2020-1-14
 */
@ApiModel(description = "水机设备列表查询条件（站务系统）")
@Getter
@Setter
@ToString
public class StationWaterDeviceQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = 6647650171932452087L;

    @ApiModelProperty(value = "SN码")
    private String sn;

    @ApiModelProperty(value = "工单号")
    private String workOrderId;

    @ApiModelProperty(value = "水机型号")
    private String deviceModel;

    @ApiModelProperty(value = "设备在线状态：0-不在线；1-在线；")
    private Boolean online;

    @ApiModelProperty(value = "安装工姓名")
    private String engineerName;

    @ApiModelProperty(value = "客户姓名")
    private String deviceUserName;

    @ApiModelProperty(value = "客户手机号")
    private String deviceUserPhone;

    @ApiModelProperty(value = "经销商姓名")
    private String distributorName;

    @ApiModelProperty(value = "经销商手机号")
    private String distributorPhone;

    @ApiModelProperty(value = "续费状态：0-未续费；1-已续费；")
    private Integer renewStatus;

    @ApiModelProperty(value = "计费方式：1-流量计费；2-包年计费")
    private Integer costType;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "剩余金额 （最小值）")
    private BigDecimal minResidueMoney;

    @ApiModelProperty(value = "剩余金额 （最大值）")
    private BigDecimal maxResidueMoney;

    @ApiModelProperty(value = "查询方式 1-根据安装工 2-根据经销商",required = true)
    private Integer queryType;

    @ApiModelProperty(value = "经销商id")
    private Integer distributorId; //用做 经销商详情--销售水机数量 做页面跳转

    private List<Integer> engineerIds; //安装工id 集合
    private List<Integer> distributorIds; //经销商id 集合
}