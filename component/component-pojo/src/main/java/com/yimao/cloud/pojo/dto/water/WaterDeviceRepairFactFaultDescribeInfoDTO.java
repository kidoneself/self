package com.yimao.cloud.pojo.dto.water;

import com.yimao.cloud.pojo.vo.out.WaterDeviceRepairFactFaultDescribeInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 维修单水机实际故障信息：水机实际故障信息描述,如果涉及一个维修工单多次提交故障原因,
 * 则之前提交的做一个逻辑删除,查询时过滤掉被逻辑删除的数据
 *
 * @author Liu Yi
 * @date 2019-3-20
 */
@Getter
@Setter
@ApiModel(description = "维修单设备故障信息及解决方式")
public class WaterDeviceRepairFactFaultDescribeInfoDTO {
    @ApiModelProperty(value = "主键ID")
    private Integer id;
    @ApiModelProperty(value = "工单号")
    private String workCode;
    @ApiModelProperty(value = "工单类型名：1-维修单，2-维护单,默认维修工单")
    private String workOrderIndex;
    @ApiModelProperty(value = "设备id")
    private Integer deviceId;
    @ApiModelProperty(value = "sn")
    private String deviceSncode;
    @ApiModelProperty(value = "实际故障描述id")
    private String factFaultDescribeId;
    @ApiModelProperty(value = "实际故障描述")
    private String factFaultDescribe;
    @ApiModelProperty(value = " 实际故障原因id")
    private String factFaultReasonId;
    @ApiModelProperty(value = "实际故障原因")
    private String factFaultReason;
    @ApiModelProperty(value = " 解决措施id")
    private String solveMeasureId;
    @ApiModelProperty(value = " 解决措施")
    private String solveMeasure;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "创建用户")
    private String createUser;
    @ApiModelProperty(value = "更新用户")
    private String updateUser;
    @ApiModelProperty(value = "是否已删除：Y-已删除 ，N-未删除")
    private String delStatus;
    @ApiModelProperty(value = "删除时间")
    private Date deleteTime;
    @ApiModelProperty(value = "Y-可用 ，N-不可用")
    private String idStatus;
    @ApiModelProperty(value = "老系统设备id")
    private  String oldDeviceId;

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceRepairFactFaultDescribeInfoDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceRepairFactFaultDescribeInfoVO dto) {
        dto.setId(this.id.toString());
        if(createTime != null){
            dto.setCreateTime(this.createTime.getTime());
        }
        if(updateTime != null){
            dto.setUpdateTime(this.updateTime.getTime());
        }
        dto.setCreateUser(this.createUser);
        dto.setUpdateUser(this.updateUser);
        dto.setDelStatus(this.delStatus);
        if(deleteTime != null){
            dto.setDeleteTime(this.deleteTime.getTime());
        }
        dto.setIdStatus(this.idStatus);
        dto.setWorkCode(this.workCode);
        dto.setWorkOrderIndex(this.workOrderIndex);
        if(this.deviceId != null){
            dto.setDeviceId(this.deviceId.toString());
        }
        dto.setDeviceSncode(this.deviceSncode);
        dto.setFactFaultDescribeId(this.factFaultDescribeId);
        dto.setFactFaultDescribe(this.factFaultDescribe);
        dto.setFactFaultReasonId(this.factFaultReasonId);
        dto.setFactFaultReason(this.factFaultReason);
        dto.setSolveMeasureId(this.solveMeasureId);
        dto.setSolveMeasure(this.solveMeasure);
    }
}
