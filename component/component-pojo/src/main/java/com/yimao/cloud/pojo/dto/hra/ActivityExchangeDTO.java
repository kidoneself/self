package com.yimao.cloud.pojo.dto.hra;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/4/11
 */
@Data
public class ActivityExchangeDTO {

    @ApiModelProperty(position = 1, value = "主键")
    private Integer id;
    @ApiModelProperty(position = 2, value = "兑换码 6位")
    private String exchangeCode;
    @ApiModelProperty(position = 3, value = "批次号")
    private String batchNumber;
    @ApiModelProperty(position = 4, value = "兑换来源  1-京东 2-其他")
    private Integer exchangeFrom;
    @ApiModelProperty(position = 5, value = "兑换用户")
    private Integer userId;
    @ApiModelProperty(position = 6, value = "渠道    1-京东(JD)    2-天猫(TM)")
    private Integer channel;
    @ApiModelProperty(position = 7, value = "端     1-公众号  2-小程序")
    private Integer side;
    @ApiModelProperty(position = 8, value = "体检卡号")
    private String ticketNo;
    @ApiModelProperty(position = 9, value = "兑换数量")
    private Integer num;
    @ApiModelProperty(position = 10, value = "兑换状态 1-未兑换 2-兑换成功  3-兑换失败 4-活动过期 5-禁止兑换")
    private Integer exchangeStatus;
    @ApiModelProperty(position = 11, value = "兑换时间")
    private Date exchangeTime;
    @ApiModelProperty(position = 12, value = "活动开始时间")
    private Date beginTime;
    @ApiModelProperty(position = 13, value = "活动截止时间")
    private Date endTime;
    @ApiModelProperty(position = 14, value = "生成人")
    private String creator;
    @ApiModelProperty(position = 15, value = "生成时间")
    private Date createTime;
    @ApiModelProperty(position = 16, value = "兑换卡使用状态")
    private Integer ticketStatus;


}
