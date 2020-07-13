package com.yimao.cloud.pojo.dto.station;

import java.util.Date;
import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class StationRoleDTO {

    @ApiModelProperty(value = "角色ID")
    private Integer id;
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "所属服务站公司id")
    private Integer stationCompanyId;
    @ApiModelProperty(value = "描述")
    private String discription;
    @ApiModelProperty(value = "创建者")
    private Integer creator;
    private String creatorName; //创建人姓名
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新者")
    private Integer updater;
    private String updaterName;//更新者姓名
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "该类管理员数量")
    private Integer adminNum;

    @ApiModelProperty(value = "权限ID集合")
    private Set<Integer> StationPermissionIds;

}
