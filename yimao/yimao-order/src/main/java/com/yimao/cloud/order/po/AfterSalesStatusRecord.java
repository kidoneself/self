package com.yimao.cloud.order.po;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/**
 * 售后状态变更记录
 *
 * @author liuhao@yimaokeji.com
 * @date 2018-12-28
 */
@Data
@Table(name = "after_sales_status_record")
public class AfterSalesStatusRecord {
  private Long id;            //主键
  private Long afterSalesOrderId;//售后单号
  private Integer origStatus; //订单状态
  private Integer destStatus; //变更状态
  private Date createTime;    //时间
  private String creator;     //创建人
  private String remark;      //备注
  
  public  AfterSalesStatusRecord(){}
public AfterSalesStatusRecord(Long afterSalesOrderId, Integer origStatus, Integer destStatus, Date createTime,
		String creator, String remark) {
	super();
	this.afterSalesOrderId = afterSalesOrderId;
	this.origStatus = origStatus;
	this.destStatus = destStatus;
	this.createTime = createTime;
	this.creator = creator;
	this.remark = remark;
}
  
  


}
