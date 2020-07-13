package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

/***
 * 功能描述:积分规则
 *
 * @auther: liu yi
 * @date: 2019/6/3 10:50
 */
@Getter
@Setter
public class IntegralRuleDTO  implements Serializable {
    private static final long serialVersionUID = -1735821538235863185L;
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "规则名称")
    private String name;
    @ApiModelProperty(value = "活动开始时间")
    private Date activityStartTime;
    @ApiModelProperty(value = "活动结束时间")
    private Date activityEndTime;
    @ApiModelProperty(value = "是否清零 0-否，1-是")
    private Integer isClear;
    @ApiModelProperty(value = "清零时间")
    private Date clearTime;
    @ApiModelProperty(value = "网络限制1-wifi网络在线 2- 3G网络在线")
    private Integer networkLimit;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "状态 1-未上架；2-上架；3-下架；4-已删除；")
    private Integer status;
    @ApiModelProperty(value = "状态描述")
    private String statusText;
    @ApiModelProperty(value = "创建人")
    private String creator;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新人")
    private String updater;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    private List<IntegralConfigurationDTO> integralConfigurationList;
}