package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 经销商业绩统计
 * @author: liulin
 * @create: 2019-02-22 16:21
 **/
@Data
public class DealerSalesDTO {


    @ApiModelProperty(value = "主键")
    private Integer id;
    @ApiModelProperty(value = "经销商ID")
    private Integer dealerId;
    @ApiModelProperty(value = "销售额")
    private BigDecimal salesAccount;
    @ApiModelProperty(value = "排行")
    private Integer sort;
    @ApiModelProperty(value = "经销商名称")
    private String dealerName;
    @ApiModelProperty(value = "上一次排名")
    private Integer lastSort;
    @ApiModelProperty(value = "点赞数")
    private Integer linkCount;
    @ApiModelProperty(value = "连续冠军次数")
    private Integer championCount;
    @ApiModelProperty(value = "服务站ID")
    private Integer stationId;
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    @ApiModelProperty(value = "批次")
    private Integer batchId;
    @ApiModelProperty(value = "是否点赞")
    private Integer hasPraised;
    @ApiModelProperty(value = "周期开始时间")
    private Integer batchIdStart;
    @ApiModelProperty(value = "真名")
    private String realName;
    @ApiModelProperty(value = "头像")
    private String headImage;









}
