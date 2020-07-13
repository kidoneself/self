package com.yimao.cloud.pojo.dto.order;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/***
 * 功能描述:维护工单记录实体类
 *
 * @auther: liu yi
 * @date: 2019/4/1 9:58
 */
@Getter
@Setter
@ApiModel(description = "维护工单操作日志")
public class MaintenanceWorkOrderOperateLogDTO implements Serializable {
    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(value = "维护工单id")
    private String maintenanceWorkOrderId;
    @ApiModelProperty(value = "操作描述")
    private String operateDescription;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "创建用户")
    private String creator;
}
