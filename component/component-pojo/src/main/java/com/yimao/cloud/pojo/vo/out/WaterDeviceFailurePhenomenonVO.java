package com.yimao.cloud.pojo.vo.out;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 故障原因
 *
 * @author Liu Yi
 * @date 2019-3-20
 */
@Getter
@Setter
@ApiModel(description = "工单故障原因记录")
public class WaterDeviceFailurePhenomenonVO implements Serializable {
    private static final long serialVersionUID = 4479277744779155609L;
    @ApiModelProperty(value = "主键ID")
    private String id;
    @ApiModelProperty(value = "部件id")
    private String partsId;
    @ApiModelProperty(value = "部件名称")
    private String partsName;
    @ApiModelProperty(value = "故障类型id")
    private String faultTypeId;
    @ApiModelProperty(value = "故障类型名称")
    private String faultTypeName;
    @ApiModelProperty(value = "工单号")
    private String workCode;
    @ApiModelProperty(value = "工单类型")
    private String workOrderIndex;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;
    @ApiModelProperty(value = "更新时间")
    private Long updateTime;
    @ApiModelProperty(value = "创建用户")
    private String createUser;
    @ApiModelProperty(value = "更新用户")
    private String updateUser;

}
