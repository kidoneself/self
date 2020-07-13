package com.yimao.cloud.pojo.dto.order;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 维修工单编辑，新增类
 * @author yaoweijun
 *
 */
@Data
@ApiModel(description = "维修工单DTO")
public class WorkRepairOrderDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -4520027268687541509L;
	
	//-----新建维修单传参-----	
	@ApiModelProperty("设备id")
    private Integer deviceId;
	
	@ApiModelProperty("来源 0-安装工app创建 1-后台业务系统创建 2-水机故障推送创建")
    private Integer sourceType;
	
	@ApiModelProperty("安装工id")
    private Integer engineerId;
	
	@ApiModelProperty("sn码")
	private String sn;
	
	@ApiModelProperty("发起故障类型id")
	private Integer faultId;
	
	@ApiModelProperty("发起故障类型名称-水机推送使用")
	private String faultName;
	
    @ApiModelProperty("服务设置开始时间")
    private Date serviceStartTime;
    
    @ApiModelProperty("服务设置结束时间")
    private Date serviceEndTime;
    
    @ApiModelProperty("备注")
    private String remark;
    
    //---------通用-----------------
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("工单状态 0-待维修 1-挂单 2-维修中 3-已完成")
    private Integer status;
    @ApiModelProperty("维修工单号")
    private String workOrderNo;
    
    //-------------改约参数-------------
    @ApiModelProperty("挂单改约理由")
    private String hangRemark;
    @ApiModelProperty("改约服务开始时间")
    private Date revisionStartTime;
    @ApiModelProperty("改约服务结束时间")
    private Date revisionEndTime;
    
    //-----------工单处理通用中-----------
    @ApiModelProperty("维修处理中步骤 1-故障确认 2-故障维修 3-确认提交")
    private Integer step;
    
    //------------工单处理中故障确认-------------
    @ApiModelProperty("维修确认故障正面照片")
    private String frontImage;
    @ApiModelProperty("维修确认故障后面照片")
    private String backImage;
    @ApiModelProperty("维修确认故障右面照片")
    private String rightImage;
    @ApiModelProperty("维修确认故障左面照片")
    private String leftImage;
    
    //-------------工单处理中维修故障--------------
    @ApiModelProperty("安装工确认故障类型id")
    private Integer confirmFaultId;
    @ApiModelProperty("安装工确认故障类名称")
    private String confirmFaultName;
    @ApiModelProperty("耗材")
    private List<GoodsMaterialsDTO> materials;
    @ApiModelProperty("是否更换耗材 0-否 1-是")
    private Boolean isChangeMaterial;


/*
		@ApiModelProperty("维修工单号")
	    private String workOrderNo;
	    
	    @ApiModelProperty("维修处理中步骤 1-故障确认 2-故障维修 3-确认提交")
	    private Integer step;
	    @ApiModelProperty("设备id")
	    private Integer deviceId;
	    @ApiModelProperty("设备型号")
	    private String deviceModel;
	    @ApiModelProperty("产品类目id（用于查询耗材）")
	    private Integer productCategoryId;
	    @ApiModelProperty("sn码")
	    private String sn;
	    @ApiModelProperty("发起故障类型id")
	    private Integer faultId;
	    @ApiModelProperty("发起故障类型名称")
	    private String faultName;
	    @ApiModelProperty("安装工确认故障类型id")
	    private Integer confirmFaultId;
	    @ApiModelProperty("安装工确认故障类名称")
	    private String confirmFaultName;
	    @ApiModelProperty("安装工id")
	    private Integer engineerId;
	    @ApiModelProperty("安装工维修完成时所属服务站id（维修完成设置）")
	    private Integer stationId;
	    @ApiModelProperty("水机所在省")
	    private String province;
	    @ApiModelProperty("水机所在市")
	    private String city;
	    @ApiModelProperty("水机所在区")
	    private String region;
	    @ApiModelProperty("水机所在地址")
	    private String address;
	    @ApiModelProperty("水机所在经度")
	    private String longitude;
	    @ApiModelProperty("水机所在纬度")
	    private String latitude;
	    @ApiModelProperty("维修确认故障正面照片")
	    private String frontImage;
	    @ApiModelProperty("维修确认故障后面照片")
	    private String backImage;
	    @ApiModelProperty("维修确认故障右面照片")
	    private String rightImage;
	    @ApiModelProperty("维修确认故障左面照片")
	    private String leftImage;
	    @ApiModelProperty("是否更换耗材 0-否 1-是")
	    private Boolean isChangeMaterial;
	    @ApiModelProperty("挂单改约理由")
	    private String hangRemark;
	    @ApiModelProperty("备注")
	    private String remark;
	    @ApiModelProperty("来源 0-安装工app创建 1-后台业务系统创建 2-水机故障推送创建")
	    private Integer sourceType;
	    @ApiModelProperty("维修工单发起时间")
	    private Date launchTime;
	    @ApiModelProperty("挂单改约发起时间")
	    private Date hangTime;
	    @ApiModelProperty("改约服务开始时间")
	    private Date revisionStartTime;
	    @ApiModelProperty("改约服务结束时间")
	    private Date revisionEndTime;
	    @ApiModelProperty("服务设置开始时间")
	    private Date serviceStartTime;
	    @ApiModelProperty("服务设置结束时间")
	    private Date serviceEndTime;
	    @ApiModelProperty("安装工服务时间")
	    private Date engineerServiceTime;
	    @ApiModelProperty("安装工服务完成时间")
	    private Date engineerFinishTime;
	    */

}
