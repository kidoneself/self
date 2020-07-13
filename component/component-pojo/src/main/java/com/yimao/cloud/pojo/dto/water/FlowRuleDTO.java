package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/***
 * 功能描述:系统流量规则
 *
 * @auther: liu yi
 * @date: 2019/6/3 10:50
 */
@Getter
@Setter
public class FlowRuleDTO implements Serializable {

    private static final long serialVersionUID = -3928510746684693316L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "禁用第三方3G:0-否 ，1-是 默认0")
    private Integer disableThird;
    @ApiModelProperty(value = "禁用自有3G:0-否 ，1-是 默认0")
    private Integer disableOwn;
    @ApiModelProperty(value = "每月3G节点更新时间")
    private String updateNode;
    @ApiModelProperty(value = "最小流量")
    private Integer minFlow;
    @ApiModelProperty(value = "最大流量")
    private Integer maxFlow;
    @ApiModelProperty(value = "流量上传 0-否 1-是 默认1")
    private Integer allTrafficUpload;
    @ApiModelProperty(value = "积分上传 0-否 1-是 默认1")
    private Integer allIntegralUpload;
    @ApiModelProperty(value = "3G流量上传 0-否 1-是 默认1")
    private Integer trafficUpload;
    @ApiModelProperty(value = "第三方效果是否上传：0-否 1-是 默认1")
    private Integer thirdEffectUpload;
    @ApiModelProperty(value = "创建人")
    private String creator;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新人")
    private String updater;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}