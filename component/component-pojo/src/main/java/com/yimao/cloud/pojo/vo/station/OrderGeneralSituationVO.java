package com.yimao.cloud.pojo.vo.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 站务系统-订单-订单概况返回类
 *
 * @author Liu Long Jie
 */
@Data
@ApiModel(description = "订单概况返回类")
public class OrderGeneralSituationVO {

    @ApiModelProperty(value = "有效订单数量")
    private Integer validOrderNum;

    @ApiModelProperty(value = "续费订单数量(售后)")
    private Integer validRenewOrderNum;

    @ApiModelProperty(value = "续费订单数量（售前）")
    private Integer preValidRenewOrderNum;

    @ApiModelProperty(value = "退款退货订单总数")
    private Integer salesReturnOrderNum;

    public OrderGeneralSituationVO() {
    }

    public OrderGeneralSituationVO(Integer validOrderNum, Integer validRenewOrderNum, Integer salesReturnOrderNum) {
        this.validOrderNum = validOrderNum;
        this.validRenewOrderNum = validRenewOrderNum;
        this.salesReturnOrderNum = salesReturnOrderNum;
    }
}
