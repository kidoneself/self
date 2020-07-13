package com.yimao.cloud.pojo.dto.system;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "门店退机库存记录类")
public class StationBackStockRecordDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7016185359378144079L;

	@ApiModelProperty(value = "id")
	private Integer id;
	
	@ApiModelProperty(value = "退机单号")
    private String workorderBackCode;
	
	@ApiModelProperty(value = "sn")
    private String sn;
	
	@ApiModelProperty(value = "退机归属门店id")
    private Integer stationId;
    
	@ApiModelProperty(value = "退机归属门店名称")
    private String stationName;
    
	@ApiModelProperty(value = "门店所属公司名称")
    private String stationCompanyName;
    
	@ApiModelProperty(value = "退机水机所在省")
    private String province;
    
	@ApiModelProperty(value = "退机水机所在市")
    private String city;
    
	@ApiModelProperty(value = "退机水机所在区")
    private String region;
    
	@ApiModelProperty(value = "退机水机所在地址")
    private String address;
    
	@ApiModelProperty(value = "退机安装工id")
    private Integer engineerId;
    
	@ApiModelProperty(value = "退机安装工名称")
    private String engineerName;

	@ApiModelProperty(value = "产品类目id")
    private Integer productCategoryId;
    
	@ApiModelProperty(value = "产品类目名称")
    private String productCategoryName;
	
	@ApiModelProperty(value = "二级类目名")
    private String productTwoCategoryName;
	
	@ApiModelProperty(value = "一级类目名")
    private String productFirstCategoryName;
	
	@ApiModelProperty(value = "是否转移库存 0-未转移 1-已转移")
    private Boolean isTransferStock;
	
	@ApiModelProperty(value = "退机工单完成时间")
    private Date completeTime;
	
	@ApiModelProperty(value = "转移库存时间")
    private Date transferTime;
	
	@ApiModelProperty(value = "转移库存操作人")
    private Integer transferUserId;
	
}
