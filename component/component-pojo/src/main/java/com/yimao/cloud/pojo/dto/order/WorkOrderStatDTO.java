package com.yimao.cloud.pojo.dto.order;

import java.io.Serializable;

import lombok.Data;

/***
 * 安装工单各状态统计列表对象
 * 
 * @author zhangbaobao
 *
 */
@Data
public class WorkOrderStatDTO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private Integer  status;//状态
	private String  statusName;//状态名称
	private Integer count;//数量
	
	
	
}
