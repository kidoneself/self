package com.yimao.cloud.pojo.dto.station;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "服务站员工")
public class StationAdminDTO {
	@ApiModelProperty(value = "员工ID")
	private Integer id;
	@ApiModelProperty(position = 1, value = "用户名")
    private String userName;
	@ApiModelProperty(position = 2, value = "密码")
    private String password;
	@ApiModelProperty(position = 3, value = "姓名")
    private String realName;
	@ApiModelProperty(position = 4, value = "性别")
    private Integer sex;
	@ApiModelProperty(position = 5, value = "手机")
    private String phone;
	@ApiModelProperty(position = 6, value = "服务站公司id")
    private Integer stationCompanyId;
	@ApiModelProperty(position = 6, value = "服务站公司名称")
    private String stationCompanyName;
	@ApiModelProperty(position = 7, value = "角色id")
    private Integer roleId;
	@ApiModelProperty(position = 8, value = "状态")
    private Boolean status;
	@ApiModelProperty(position = 9, value = "创建人id")
    private Integer creator;
	@ApiModelProperty(position = 10, value = "创建时间")
    private Date createTime;
	@ApiModelProperty(position = 11, value = "更新人id")
    private Integer updater;
	@ApiModelProperty(position = 12, value = "更新时间")
    private Date updateTime;
	@ApiModelProperty(position = 13, value = "角色名称")
	private String roleName;
	@ApiModelProperty(position = 13, value = "用户区域权限")
	private List<StationAreasDTO> stations;
	@ApiModelProperty(position = 14, value = "创建人")
    private String creatorName;
    

}
