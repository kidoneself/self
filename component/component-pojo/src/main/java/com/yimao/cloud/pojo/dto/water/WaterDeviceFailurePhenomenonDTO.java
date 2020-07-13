package com.yimao.cloud.pojo.dto.water;


import com.yimao.cloud.pojo.vo.out.WaterDeviceFailurePhenomenonVO;
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
@ApiModel(description = "工单水机故障类型原因记录")
public class WaterDeviceFailurePhenomenonDTO  implements Serializable {
    private static final long serialVersionUID = 4479277744779155609L;
    @ApiModelProperty(value = "主键ID")
    private Integer id;
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
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "创建用户")
    private String createUser;
    @ApiModelProperty(value = "更新用户")
    private String updateUser;

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceFailurePhenomenonDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceFailurePhenomenonVO dto) {
        dto.setId(this.id.toString());
        dto.setPartsId(this.partsId);
        dto.setPartsName(this.partsName);
        dto.setFaultTypeId(this.faultTypeId);
        dto.setFaultTypeName(this.faultTypeName);
        dto.setWorkCode(this.workCode);
        dto.setWorkOrderIndex(this.workOrderIndex);
        if(createTime != null){
            dto.setCreateTime(this.createTime.getTime());
        }
        if(updateTime != null){
            dto.setUpdateTime(this.updateTime.getTime());
        }
        dto.setCreateUser(this.createUser);
        dto.setUpdateUser(this.updateUser);
    }
}
