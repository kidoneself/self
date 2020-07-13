package com.yimao.cloud.order.po;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "work_repair_fault_operation")
public class WorkRepairFaultOperation {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer faultId;

    private Integer operateType;

    private String beforeFaultName;

    private String afterFaultName;

    private Date operateTime;

    private String operator;

   
}