package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhilin.he
 * @description 工单操作
 * @date 2019/5/10 16:04
 **/

@Getter
@Setter
@ApiModel(description = "工单操作对象")
public class WorkOrderOperationDTO  implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;//工单操作id
    private String admin;//管理员
    private String workOrderId;//工单id
    private String renewOrderId;//续费工单id
    private String operation;//操作
    private String snCode;//sn码
    private String simCard;//sim码
    private String batchCode;//批次码
    private String remark;//备注
    private String reason;//原因
    private Date createTime;//创建时间


}
