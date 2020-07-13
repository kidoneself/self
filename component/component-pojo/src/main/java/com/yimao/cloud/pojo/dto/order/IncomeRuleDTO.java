package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 收益规则
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@ApiModel(description = "收益分配规则DTO")
@Getter
@Setter
public class IncomeRuleDTO {

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(position = 1, value = "名称")
    private String name;

    @ApiModelProperty(position = 2, value = "规则code")
    private String code;

    @ApiModelProperty(position = 3, value = "收益类型：1-产品收益；2-续费收益；3-服务收益；4-招商收益；")
    private Integer incomeType;

    @ApiModelProperty(position = 4, value = "分配类型：1-按比例分配；2-按金额分配；")
    private Integer allotType;

    @ApiModelProperty(position = 5, value = "收益分配规则所含收益主体")
    private List<IncomeRulePartDTO> incomeRuleParts;

    @ApiModelProperty(position = 100, value = "创建人")
    private String creator;

    @ApiModelProperty(position = 101, value = "创建时间", example = "2018-12-28 11:00:00")
    private Date createTime;

    @ApiModelProperty(position = 102, value = "更新人")
    private String updater;

    @ApiModelProperty(position = 103, value = "更新时间", example = "2018-12-28 11:00:00")
    private Date updateTime;

    @ApiModelProperty(position = 104, value = "是否删除 1-是;0-否 默认否")
    private Boolean deleted;

}