package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：流量统计
 *
 */
@Getter
@Setter
@ApiModel(description = "流量统计")
public class TrafficStatisticsDTO implements Serializable {

    private static final long serialVersionUID = 1573836755485035727L;

    @ApiModelProperty(position = 1,value = "id")
    private Integer id;
    @ApiModelProperty(position = 2,value = "广告平台：0-翼猫（自有）；1-百度；2-京东；3-科大讯飞；4-简视；5-海大；6-后台交互；7-易售,10-其它")
    private Integer platform;
   /* @ApiModelProperty(position = 3,value = "广告平台名称")
    private Integer platformName;*/
    @ApiModelProperty(position = 4,value = "总流量")
    private BigDecimal totalTraffic;
    @ApiModelProperty(position = 5,value = "请求次数")
    private Integer requestAmount;
    @ApiModelProperty(position = 6,value = "请求流量(byte)")
    private BigDecimal requestTraffic;
    @ApiModelProperty(position = 7,value = "下载次数")
    private Integer downloadsAmount;
    @ApiModelProperty(position = 8,value = "下载流量(byte)")
    private BigDecimal downloadsTraffic;
    @ApiModelProperty(position = 9,value = "回调次数")
    private Integer callbackAmount;
    @ApiModelProperty(position = 10,value = "回调流量(byte)")
    private BigDecimal callbackTraffic;
    @ApiModelProperty(position = 11,value = "sn码")
    private String sn;
    @ApiModelProperty(position = 12, value = "设备组：1-用户组，2-服务站组")
    private Integer deviceGroup;
    @ApiModelProperty(position = 13,value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 14,value = "创建日期（后台展示用）")
    private String createDate;
    @ApiModelProperty(position = 15,value = "来源：1-3G 2-wifi")
    private Integer source;

}