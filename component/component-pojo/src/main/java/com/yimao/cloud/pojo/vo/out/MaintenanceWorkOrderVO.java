package com.yimao.cloud.pojo.vo.out;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ApiModel(description = "维护工单展示")
public class MaintenanceWorkOrderVO {
    @ApiModelProperty(value = "ID")
    private String id;
    @ApiModelProperty(value = "滤芯数量")
    private Integer filterCount;
    @ApiModelProperty(value = "销毁图片url")
    private String destroyFilterImg;
    @ApiModelProperty(value = "更换滤芯状态")
    private String changeFilterStatus;
    @ApiModelProperty(value = "更换滤芯时间")
    private Long changeFilterTime;
    @ApiModelProperty(value = "销毁滤芯图片状态")
    private String destroyFilterImgStatus;
    @ApiModelProperty(value = "毁滤芯时间")
    private Long destroyFilterImgTime;
    @ApiModelProperty(value = "所需耗材名称")
    private String materielDetailName;
    @ApiModelProperty(value = "需要更换的耗材类型id")
    private String materielDetailIds;
    @ApiModelProperty(value = " 维护内容")
    private String maintenanceRemark;

    @ApiModelProperty(value = " 完成类型")
    private Integer completeType;
    @ApiModelProperty(value = " 完成类型描述")
    private String completeTypeText;
    @ApiModelProperty(value = " 更新人")
    private String updateBy;
    @ApiModelProperty(value = "更新人")
    private String updateByName;
    @ApiModelProperty(value = "更新人角色")
    private String updateByRole;
    @ApiModelProperty(value = "更新时间")
    private Long updateDate;

    @ApiModelProperty(value = "最晚完成时间(倒计时)")
    private Long countdownTime;
    @ApiModelProperty(value = "工单code")
    private String workCode;
    @ApiModelProperty(value = "经销商id")
    private String distributorId;
    @ApiModelProperty(value = "经销商")
    private String distributorName;
    @ApiModelProperty(value = "经销商电话")
    private String distributorPhone;
    @ApiModelProperty(value = "经销商角色id")
    private String distributorRoleId;
    @ApiModelProperty(value = "经销商角色")
    private String distributorRoleName;
    @ApiModelProperty(value = "经销商身份证号")
    private String distributorIdCard;
    @ApiModelProperty(value = "经销商子账号id")
    private String distributorChildId;
    @ApiModelProperty(value = "经销商子账号名称")
    private String distributorChildName;
    @ApiModelProperty(value = "经销商子账号电话")
    private String distributorChildPhone;
    @ApiModelProperty(value = "服务工程师id")
    private String engineerId;
    @ApiModelProperty(value = "服务工程师")
    private String engineerName;
    @ApiModelProperty(value = "服务工程师电话")
    private String engineerPhone;
    @ApiModelProperty(value = "安装工身份证号")
    private String engineerIdCard;
    @ApiModelProperty(value = " 安装工程师id")
    private String installEngineerId;
    @ApiModelProperty(value = " 安装工程师")
    private String installEngineerName;
    @ApiModelProperty(value = " 安装工程师电话")
    private String installEngineerPhone;
    @ApiModelProperty(value = " 安装工程师身份证号")
    private String installEngineerIdCard;
    @ApiModelProperty(value = " 服务站id")
    private String serviceSiteId;
    @ApiModelProperty(value = " 服务站名称")
    private String serviceSiteName;
    @ApiModelProperty(value = "用户id")
    private String consumerId;
    @ApiModelProperty(value = "用户名")
    private String consumerName;
    @ApiModelProperty(value = "用户电话")
    private String consumerPhone;
    @ApiModelProperty(value = "用户身份证号")
    private String consumerIdCard;
    @ApiModelProperty(value = "批次码")
    private String deviceBatchCode;
    @ApiModelProperty(value = "产品类型Id")
    private String productId;
    @ApiModelProperty(value = "产品类型")
    private String product;
    @ApiModelProperty(value = "产品范围id")
    private String kindId;
    @ApiModelProperty(value = "产品范围")
    private String kindName;
    @ApiModelProperty(value = "设备型号Id")
    private String deviceModel;
    @ApiModelProperty(value = "设备型号")
    private String deviceModelName;
    @ApiModelProperty(value = "设备Id")
    private String deviceId;
    @ApiModelProperty(value = "设备SN码")
    private String deviceSncode;
    @ApiModelProperty(value = "设备ICCID")
    private String deviceSimcard;
    @ApiModelProperty(value = "计费方式id")
    private String chargeId;
    @ApiModelProperty(value = "计费方式名称")
    private String chargeName;
    @ApiModelProperty(value = "工单备注")
    private String workOrderRemark;
    @ApiModelProperty(value = "完成时间天数配置,用于最晚完成时间显示")
    private Integer completeDayNum;
    @ApiModelProperty(value = "预约地址")
    private String bespeakAddress;
    @ApiModelProperty(value = "预约备注")
    private String appointRemark;
    @ApiModelProperty(value = "下一步骤")
    private Integer nextStep;
    @ApiModelProperty(value = "当前步骤")
    private Integer currentStep;
    @ApiModelProperty(value = "修改预约状态")
    String bespeakStatusSeconds;
    @ApiModelProperty(value = "修改预约时间")
    Long bespeakTimeSeconds;
    @ApiModelProperty(value = "用户评价时间")
    Long userAppriseTime;
    @ApiModelProperty(value = "用户评价状态")
    String workorderUserAppraiseStatus;
    @ApiModelProperty(value = "用户评价状态描述")
    String workorderUserAppraiseStatusText;

    @ApiModelProperty(value = "接单状态")
    private String acceptStatus;
    @ApiModelProperty(value = "接单时间")
    private Long acceptTime;
    @ApiModelProperty(value = "预约状态")
    private String bespeakStatus;
    @ApiModelProperty(value = "预约时间")
    private Long bespeakTime;
    @ApiModelProperty(value = "开始服务状态")
    private String startServerStatus;
    @ApiModelProperty(value = "开始服务时间")
    private Long startServerTime;
    @ApiModelProperty(value = "工单状态：2-已受理 3-处理中 4-已完成")
    private Integer state;
    @ApiModelProperty(value = "工单状态描述")
    private String stateText;
    @ApiModelProperty(value = "完成状态：Y-完成 ，N-未完成")
    private String workOrderCompleteStatus;
    @ApiModelProperty(value = "完成时间")
    private Long workOrderCompleteTime;
    @ApiModelProperty(value = "省份")
    private String addrProvince;
    @ApiModelProperty(value = "城市")
    private String addrCity;
    @ApiModelProperty(value = "县")
    private String addrRegion;
    @ApiModelProperty(value = "地址(不含省市区)")
    private String address;
    @ApiModelProperty(value = "详细地址")
    private String addressDetail;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;
    @ApiModelProperty(value = "更新时间")
    private Long updateTime;
    @ApiModelProperty(value = "创建用户")
    private String createUser;
    @ApiModelProperty(value = "更新用户")
    private String updateUser;
    @ApiModelProperty(value = "是否已删除：Y-已删除 ，N-未删除")
    private String delStatus;
    @ApiModelProperty(value = "删除时间")
    private Long deleteTime;
    @ApiModelProperty(value = "老系统id")
    private String yimaoOldSystemId;
   /* @ApiModelProperty(value = "Y-可用 ，N-不可用")
    private String idStatus;*/

   /* @ApiModelProperty(value = "审核方式：1-自动审核 2-人工审核")
    private Integer auditType;*/
    @ApiModelProperty(value = "来源：1-自动生成 2-总部添加，默认自动生成")
    private Integer source;
    private boolean mustUploadImg;
    @ApiModelProperty(value = "颜色配置")
    private Map configMap;
    @ApiModelProperty(value = "工单耗材信息")
    private List<WaterDeviceWorkOrderMaterielVO> materiels;

    /*private String ServiceSiteName="sdfsf";
    //private String addrCityId;
    //private String addrCountry;
    //private String addrCountryId;
   // private String addrLatitude;
    //private String addrLongitude;
    //private String addrProvinceId;
    //private String addrRegionId;
    private String filterNameStr="asdf";
    private String orderId="234e23";
    private String orderTypeIndex="1";
    private String orderTypeName="1";
    //private int payMoney;
    private String payStatus="1";
    private Long payTime=System.currentTimeMillis();
    private String payType="1";
    private String workorderId="sdf";*/
}
