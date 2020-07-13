package com.yimao.cloud.pojo.query.hra;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @description: 评估列表查询条件
 * @author: yu chunlei
 * @create: 2019-11-26 17:26:49
 **/
@Getter
@Setter
public class HraEvaluationQuery {

    @ApiModelProperty(value = "体检开始时间")
    private String beginTime;

    @ApiModelProperty(value = "体检结束时间")
    private String endTime;

    @ApiModelProperty(value = "用户来源")
    private Integer userSource;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "体检卡号")
    private String ticketNo;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String region;

    @ApiModelProperty(value = "体检人姓名")
    private String name;

    @ApiModelProperty(value = "预约状态  1-预约中 2-预约到期")
    private Integer reserveStatus;

    @ApiModelProperty(value = "用户e家号")
    private Integer userId;

    @ApiModelProperty(value = "体检卡型号")
    private String ticketType;

    @ApiModelProperty(value = "服务站id集合")
    private List<Integer> stationIds;

}
