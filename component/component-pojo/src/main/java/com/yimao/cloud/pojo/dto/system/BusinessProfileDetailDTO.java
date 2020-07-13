package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 经营概况明细
 *
 * @author hhf
 * @date 2019/3/26
 */
@Data
public class BusinessProfileDetailDTO {

    private Integer id;
    @ApiModelProperty(value = "经营概况id")
    private Integer businessProfileId;
    @ApiModelProperty(value = "服务id")
    private Integer serviceId;
    @ApiModelProperty(value = "服务名称")
    private String serviceName;
    @ApiModelProperty(value = "服务销售额")
    private BigDecimal salesTotal;
    @ApiModelProperty(value = "服务销量")
    private Integer serviceCount;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
