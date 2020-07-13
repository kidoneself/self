package com.yimao.cloud.system.po;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yimao.cloud.pojo.dto.system.StationBackStockRecordDTO;

import lombok.Data;
@Data
@Table(name = "station_back_stock_record")
public class StationBackStockRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //退机单号
    private String workorderBackCode;
    
    private String sn;
    //退机归属门店id
    private Integer stationId;
    //退机归属门店名称
    private String stationName;
    //退机水机所在省
    private String province;
    //退机水机所在市
    private String city;
    //退机水机所在区
    private String region;
    //退机水机所在地址
    private String address;
    //退机安装工id
    private Integer engineerId;
    //退机安装工名称
    private String engineerName;

    private Integer productCategoryId;
    //产品类目
    private String productCategoryName;
    //二级类目名
    private String productTwoCategoryName;
    //一级类目名
    private String productFirstCategoryName;
    //是否转移库存 0-未转移 1-已转移
    private Boolean isTransferStock;
    //退机工单完成时间
    private Date completeTime;
    //转移库存时间
    private Date transferTime;
    //转移库存操作人
    private Integer transferUserId;
    
    public StationBackStockRecord() {    	
    }
    
    public StationBackStockRecord(StationBackStockRecordDTO dto) {
    	this.id=dto.getId();
    	this.workorderBackCode=dto.getWorkorderBackCode();
    	this.sn=dto.getSn();
    	this.stationId=dto.getStationId();
    	this.stationName=dto.getStationName();
    	this.province=dto.getProvince();
    	this.city=dto.getCity();
    	this.region=dto.getRegion();
    	this.address=dto.getAddress();
    	this.engineerId=dto.getEngineerId();
    	this.engineerName=dto.getEngineerName();
    	this.productCategoryId=dto.getProductCategoryId();
    	this.productCategoryName=dto.getProductCategoryName();
    	this.productTwoCategoryName=dto.getProductTwoCategoryName();
    	this.productFirstCategoryName=dto.getProductFirstCategoryName();
    	this.isTransferStock=dto.getIsTransferStock();
    	this.completeTime=dto.getCompleteTime();
    	this.transferTime=dto.getTransferTime();
    	this.transferUserId=dto.getTransferUserId();
    }

}