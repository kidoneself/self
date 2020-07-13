package com.yimao.cloud.order.po;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "work_repair_fault")
public class WorkRepairFault {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String faultName;

    private Integer sorts;

    private Integer type;

    private Date createTime;

    private String creator;

  
}