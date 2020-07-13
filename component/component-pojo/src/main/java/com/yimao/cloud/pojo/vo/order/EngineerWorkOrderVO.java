package com.yimao.cloud.pojo.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：水机安装工单
 *
 * @Author Zhang Bo
 * @Date 2019/2/26 15:19
 * @Version 1.0
 */
@ApiModel(description = "工单对象")
@Getter
@Setter
public class EngineerWorkOrderVO {

    @ApiModelProperty(value = "工单号")
    private String id;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区县")
    private String region;
    @ApiModelProperty(value = "下单时间")
    private Date createTime;
    @ApiModelProperty(value = "支付方式：1-微信；2-支付宝；3-POS机；4-转账；")
    private String payType;
    @ApiModelProperty(value = "工单状态：-2、审核未通过；-1、审核中；0-已支付；1-未受理；2-已受理；3-处理中；4-已完成；5-待付款；6-客服拒绝；7-分配客服；")
    private String status;
    @ApiModelProperty(value = "地区id")
    private Integer areaId;

}
