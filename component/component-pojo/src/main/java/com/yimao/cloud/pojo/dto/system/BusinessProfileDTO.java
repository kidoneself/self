package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 经营概况
 *
 * @author hhf
 * @date 2019/3/26
 */
@Data
public class BusinessProfileDTO {

    private Integer id;
    @ApiModelProperty(position = 1, value = "有效销售总额度")
    private BigDecimal saleTotal;
    @ApiModelProperty(position = 2, value = "有效订单总数")
    private Integer orderTotal;
    @ApiModelProperty(position = 3, value = "经销商总数")
    private Integer distributorTotal;
    @ApiModelProperty(position = 4, value = "用户总数")
    private Integer userTotal;
    @ApiModelProperty(position = 5, value = "用户访问次数")
    private Integer visitTotal;
    @ApiModelProperty(position = 6, value = "用户下单次数")
    private Integer buyTotal;
    @ApiModelProperty(position = 7, value = "成交笔数")
    private Integer bargainTotal;
    @ApiModelProperty(position = 8, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 9, value = "昨日有效销售额")
    private BigDecimal yestSaleTotal;
    @ApiModelProperty(position = 10, value = "昨日订单数")
    private Integer yestOrderTotal;
    @ApiModelProperty(position = 11, value = "昨日新增经销商数")
    private Integer yestDistributorTotal;
    @ApiModelProperty(position = 12, value = "昨日用户数")
    private Integer yestUserTotal;
    @ApiModelProperty(position = 13, value = "概况明细")
    private List<BusinessProfileDetailDTO> details;

    @ApiModelProperty(position = 13, value = "概况明细 1-昨日的数据;7-一周的数据；30-一个月的数据")
    private Map<Integer,List<BusinessProfileDetailDTO>> dtoMap;
}
