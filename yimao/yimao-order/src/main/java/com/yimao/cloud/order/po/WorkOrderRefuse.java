package com.yimao.cloud.order.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：拒单记录
 *
 * @Author Zhang Bo
 * @Date 2019/10/17
 */
@Table(name = "workorder_refuse")
@Getter
@Setter
public class WorkOrderRefuse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String workOrderId;  //工单号
    private String province;
    private String city;
    private String region;
    private Integer engineerId;  //安装工ID
    private String engineerName; //安装工姓名
    private String reason;       //拒单原因
    private Date refuseTime;     //拒单时间

}
