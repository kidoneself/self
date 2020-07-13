package com.yimao.cloud.pojo.dto.order;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 服务站业绩统计
 * @author: liulin
 * @create: 2019-02-22 16:21
 **/
@Data
public class StationSalesDTO {
    @ApiModelProperty(value = "主键")
    private Integer id;
    @ApiModelProperty(value = "服务站门店id")
    private Integer stationId;
    @ApiModelProperty(value = "省份")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区")
    private String region;
    @ApiModelProperty(value = "销售额")
    private BigDecimal salesAccount;
    @ApiModelProperty(value = "排行")
    private Integer sort;
    @ApiModelProperty(value = "批次")
    private Integer batchId;
    @ApiModelProperty(value = "服务站门店名称")
    private String stationName;
    @ApiModelProperty(value = "上一次排名")
    private Integer lastSort;
    @ApiModelProperty(value = "点赞数")
    private Integer linkCount;
    @ApiModelProperty(value = "连续冠军次数")
    private Integer championCount;
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    @ApiModelProperty(value = "是否点赞")
    private Integer hasPraised;
    @ApiModelProperty(value = "周期开始时间")
    private Integer batchIdStart;


}
