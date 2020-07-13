package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Chen Hui Yang
 * @date 2018/12/26
 * 主提现实体类
 */
@Data
@ApiModel(description = "主提现实体类")
public class WithdrawMainDTO {

    @ApiModelProperty( value = "主提现单")
    private Long mainPartnerTradeNo;
    @ApiModelProperty( value = "提现用户")
    private Integer userId;             
    @ApiModelProperty( value = "总金额")
    private BigDecimal amountFee;       
    @ApiModelProperty( value = "手续费")
    private BigDecimal formalitiesFee;  
    @ApiModelProperty( value = "提现时间")
    private Date withdrawTime;          
    @ApiModelProperty( value = "提现方式")
    private Integer withdrawType;       
    @ApiModelProperty( value = "审核时间")
    private Date applyTime;             
    @ApiModelProperty( value = "提现入口  1-公众号  2-小程序  3-经销商APP 4-ios  默认公众号")
    private Integer terminal;           


}