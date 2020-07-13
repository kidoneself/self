package com.yimao.cloud.station.po;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
@Table(name = "stock_loan_apply")
public class StockLoanApply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer status;

	private String firstCategoryLevelName;
	
	private String secondCategoryLevelName;	

    private Integer goodsMaterialsId;

    private String goodsMaterialsName;

    private Integer applyCount;

    private String remark;

    private Date applyTime;

    private Integer applyerId;

    private Date auditTime;

    private Integer auditorId;


}