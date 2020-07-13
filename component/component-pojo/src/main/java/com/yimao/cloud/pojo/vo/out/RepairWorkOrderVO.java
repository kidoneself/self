package com.yimao.cloud.pojo.vo.out;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ApiModel(description = "维修工单展示")
public class RepairWorkOrderVO {
    @Id
    private Integer id;
    private String oldId;
    private String scanBatchCodeStatus;
    private Long scanBatchCodeTime;
    private String applyChangeDeviceStatus;
    private String applyChangeDeviceResultStatus;
    private String applyChangeDeviceResult;
    private String refuseApplyReason;
    private Integer applycount = 0;
    private String faultDescise;
    private String repairAdvice;
    private String changeDeviceStatus;
    private Long changeDeviceTime;
    private String changeDeviceSncode;
    private String changeDeviceSimcard;
    private String changeDeviceBatchCode;
    private String failurePhenomenonIds;
    private String factFaultStatus;
    private Long factFaultLong;
    private String addrProvince;
    private String addrCity;
    private String addrRegion;
    private String address;
    private String acceptStatus;
    private Long acceptTime;
    private String bespeakStatus;
    private Long bespeakTime;
    private String startServerStatus;
    private Long startServerTime;
    private Integer state;
    private String stateText;
    private String workOrderCompleteStatus;
    private Long workOrderCompleteTime;
    private Long countdownTime;
    private String workCode;
    private String distributorId;
    private String distributorName;
    private String distributorPhone;
    private String distributorRoleId;
    private String distributorRoleName;
    private String distributorIdCard;
    private String distributorChildId;
    private String distributorChildName;
    private String distributorChildPhone;
    private String engineerId;
    private String engineerName;
    private String engineerPhone;
    private String engineerIdCard;
    private String serviceSiteId;
    private String ServiceSiteName;
    private String consumerId;
    private String consumerName;
    private String consumerPhone;
    private String consumerIdCard;
    private String deviceBatchCode;
    private String productId;
    private String product;
    private String kindId;
    private String kindName;
    private String deviceModel;
    private String deviceModelName;
    private String deviceId;
    private String deviceSncode;
    private String deviceSimcard;
    private String chargeId;
    private String chargeName;
    private String workOrderRemark;
    private Integer completeDayNum;
    private String bespeakAddress;
    private String appointRemark;
    private Integer nextStep;
    private Integer currentStep;
    private String bespeakStatusSeconds;
    private Long bespeakTimeSeconds;
    private Long userAppriseTime;
    private String workorderUserAppraiseStatus;
    private String workorderUserAppraiseStatusText;
    private Long factFaultDate;

    private Long createTime;
    private Long updateTime;
    private String createUser;
    private String upDateUser;
    private String delStatus;
    private Long deleteTime;
    
    @ApiModelProperty(value = "颜色配置")
    private Map configMap;
    @ApiModelProperty(value = "工单耗材信息")
    private List<WaterDeviceWorkOrderMaterielVO> materiels;
    @ApiModelProperty(value = "设备故障")
    private List<WaterDeviceRepairFactFaultDescribeInfoVO> factFaults;
    @ApiModelProperty(value = "工单故障原因记录")
    private List<WaterDeviceFailurePhenomenonVO> failurePhenomenons;
}
