package com.yimao.cloud.pojo.dto.water;


import com.yimao.cloud.pojo.vo.out.WaterDeviceWorkOrderMaterielVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


/**
 * 工单消耗的耗材(滤芯)
 *
 * @author Liu Yi
 * @date 2019-3-20
 */
@Getter
@Setter
@ApiModel(description = "工单耗材信息")
public class WaterDeviceWorkOrderMaterielDTO {
    private static final long serialVersionUID = 6234688810659523075L;
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "工单id")
    private String workCode;
    @ApiModelProperty(value = "工单类型：1-维修单，2-维护单")
    private String workOrderIndex;
    @ApiModelProperty(value = "滤芯id")
    private String materielId;
    @ApiModelProperty(value = "滤芯名称")
    private String materielName;
    @ApiModelProperty(value = "滤芯类型Id")
    private String materielTypeId;
    @ApiModelProperty(value = "滤芯类型名称--例如:PP棉")
    private String materielTypeName;
    @ApiModelProperty(value = "耗材批次码")
    private String materielBatchCode;
    @ApiModelProperty(value = "物料索引值:1-滤芯,2-电器")
    private String materielIndex;
    @ApiModelProperty(value = "扫描时间")
    private Date scanCodeTime;
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

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceWorkOrderMaterielDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceWorkOrderMaterielVO dto) {
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
       /* dto.setWorkOrderIndexName(this.workOrderIndexName);*/
        dto.setMaterielId(this.materielId);
        dto.setMaterielName(this.materielName);
        dto.setMaterielTypeId(this.materielTypeId);
        dto.setMaterielTypeName(this.materielTypeName);
        dto.setMaterielBatchCode(this.materielBatchCode);
        dto.setMaterielIndex(this.materielIndex);
        if(scanCodeTime != null){
            dto.setScanCodeTime(this.scanCodeTime.getTime());
        }
    }

}
