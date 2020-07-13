package com.yimao.cloud.order.po;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/**
 * 订单状态变更记录
 *
 * @author liuhao@yimaokeji.com
 * @date 2018-12-28
 */
@Data
@Table(name = "order_status_record")
public class OrderStatusRecord {

  private Long id;               //订单编号
  private Integer origStatus;    //源
  private Integer destStatus;    //目标
  private Date createTime;       //时间
  private String creator;        //创建人
  private String remark;         //备注

}
