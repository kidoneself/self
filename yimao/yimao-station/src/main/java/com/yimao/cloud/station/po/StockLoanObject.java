package com.yimao.cloud.station.po;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Data
@Table(name = "stock_loan_object")
public class StockLoanObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer stockLoanApplyId;

    private Boolean applyType;

    private Integer companyId;

    private String companyName;

    private Integer stationId;
    
    private String stationCode;

    private String stationName;

    private String stationMasterName;

    private String stationMasterPhone;

}