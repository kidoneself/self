package com.yimao.cloud.pojo.vo.out;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


/**
 * 工单耗材信息
 *
 * @author Liu Yi
 * @date 2019-3-20
 */
@Getter
@Setter
@ApiModel(description = "工单耗材信息")
public class WaterDeviceWorkOrderMaterielVO {
    private static final long serialVersionUID = 6234688810659523075L;
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "工单id")
    private String workCode;
    @ApiModelProperty(value = "工单类型：1-维修单，2-维护单")
    private String workOrderIndex;
    @ApiModelProperty(value = "耗材id")
    private String materielId;
    @ApiModelProperty(value = "耗材名称")
    private String materielName;
    @ApiModelProperty(value = "耗材类型id")
    private String materielTypeId;
    @ApiModelProperty(value = "耗材类型名称")
    private String materielTypeName;
    @ApiModelProperty(value = "耗材批次码")
    private String materielBatchCode;
    @ApiModelProperty(value = "耗材类型")
    private String materielIndex;
    @ApiModelProperty(value = "扫描时间")
    private Long scanCodeTime;
    @ApiModelProperty(value = "创建时间")
    private Long createTime;
    @ApiModelProperty(value = "更新时间")
    private Long updateTime;
    @ApiModelProperty(value = "创建用户")
    private String createUser;
    @ApiModelProperty(value = "更新用户")
    private String updateUser;
    @ApiModelProperty(value = "是否已删除：Y-已删除 ，N-未删除")
    private String delStatus;
    @ApiModelProperty(value = "删除时间")
    private Long deleteTime;
    @ApiModelProperty(value = "Y-可用 ，N-不可用")
    private String idStatus;

}
