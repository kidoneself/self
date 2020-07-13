package com.yimao.cloud.pojo.dto.order;

import com.github.pagehelper.util.StringUtil;
import com.yimao.cloud.pojo.vo.out.RepairWorkOrderVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ApiModel(description = "维修工单")
public class RepairWorkOrderDTO implements Serializable {
    //公用
    @ApiModelProperty(value = "ID")
    private Integer id;
   /* @ApiModelProperty(value = "工单id")
    private String orderId;*/
    @ApiModelProperty(value = "更换耗材状态：Y-是，N-否")
    private String scanBatchCodeStatus;
    @ApiModelProperty(value = "更换的耗材批次码时间")
    private Date scanBatchCodeTime;
    @ApiModelProperty(value = "申请换机状态 申请中:YES 未申请:NO")
    private String applyChangeDeviceStatus;
    @ApiModelProperty(value = "申请换机结果状态,待审核:N,审核完成:Y")
    private String applyChangeDeviceResultStatus;
    @ApiModelProperty(value = "申请换机结果,Y-审核通过,N-审核未通过")
    private String applyChangeDeviceResult;
    @ApiModelProperty(value = "拒绝理由")
    private String refuseApplyReason;
    @ApiModelProperty(value = "提交审核次数")
    private Integer applycount = 0;
    @ApiModelProperty(value = "故障描述")
    private String faultDescise;
    @ApiModelProperty(value = "维修建议")
    private String repairAdvice;
    @ApiModelProperty(value = "更换设备状态：Y-是 N-否")
    private String changeDeviceStatus;
    @ApiModelProperty(value = "更换设备时间")
    private Date changeDeviceTime;
    @ApiModelProperty(value = "更换之后的设备设备SN码")
    private String changeDeviceSncode;
    @ApiModelProperty(value = "更换之后的设备ICCID")
    private String changeDeviceSimcard;
    @ApiModelProperty(value = "更换之后的设备批次码")
    private String changeDeviceBatchCode;
    @ApiModelProperty(value = "故障现象类型id,用来与百得的最晚完成时间做对比")
    private String failurePhenomenonIds;
    @ApiModelProperty(value = "填写故障信息状态：Y-是，N-否")
    private String factFaultStatus;
    @ApiModelProperty(value = "填写故障信息时间")
    private Date factFaultDate;
    @ApiModelProperty(value = "工单来源,android,ios,openapi")
    private String workOrderSource;
    @ApiModelProperty(value = "工单来源,android,ios,openapi")
    private String workOrderSourceText;
    @ApiModelProperty(value = "省份")
    private String addrProvince;
    @ApiModelProperty(value = "城市")
    private String addrCity;
    @ApiModelProperty(value = "县")
    private String addrRegion;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "接单状态：Y-已接单 N-未接单")
    private String acceptStatus;
    @ApiModelProperty(value = "接单时间")
    private Date acceptTime;
    @ApiModelProperty(value = "预约状态：Y-已预约 N-未预约")
    private String bespeakStatus;
    @ApiModelProperty(value = "预约时间")
    private Date bespeakTime;
    @ApiModelProperty(value = "开始服务状态 Y-开始 N-未开始")
    private String startServerStatus;
    @ApiModelProperty(value = "开始服务时间")
    private Date startServerTime;
    @ApiModelProperty(value = "工单状态：2-已受理,3-处理中,4-已完成")
    private Integer state;
    @ApiModelProperty(value = "工单状态描述")
    private String stateText;
    @ApiModelProperty(value = "完成状态：Y-完成 ，N-未完成")
    private String workOrderCompleteStatus;
    @ApiModelProperty(value = "完成时间")
    private Date workOrderCompleteTime;
    @ApiModelProperty(value = "最晚完成时间(倒计时)")
    private Date countdownTime;
    @ApiModelProperty(value = "工单code(售后返回)")
    private String workCode;
    @ApiModelProperty(value = "安装工单id")
    private String workOrderId;
    @ApiModelProperty(value = "经销商id")
    private Integer distributorId;
    @ApiModelProperty(value = "经销商名称")
    private String distributorName;
    @ApiModelProperty(value = "经销商电话")
    private String distributorPhone;
    @ApiModelProperty(value = "经销商角色id")
    private Integer distributorRoleId;
    @ApiModelProperty(value = "经销商角色")
    private String distributorRoleName;
    @ApiModelProperty(value = "经销商身份证")
    private String distributorIdCard;
    @ApiModelProperty(value = "子经销商id")
    private Integer distributorChildId;
    @ApiModelProperty(value = "子经销商")
    private String distributorChildName;
    @ApiModelProperty(value = "子经销商电话")
    private String distributorChildPhone;
    @ApiModelProperty(value = "服务工程师id")
    private Integer engineerId;
    @ApiModelProperty(value = "服务工程师")
    private String engineerName;
    @ApiModelProperty(value = "服务工程师电话")
    private String engineerPhone;
   @ApiModelProperty(value = "服务工程师身份证")
    private String engineerIdCard;
    @ApiModelProperty(value = " 工程师所属服务站id")
    private Integer stationId;
    @ApiModelProperty(value = " 工程师所属服务站名称")
    private String stationName;
    @ApiModelProperty(value = "客户id")
    private Integer consumerId;
    @ApiModelProperty(value = "客户名称(水机用户)")
    private String consumerName;
    @ApiModelProperty(value = "客户电话(水机用户)")
    private String consumerPhone;
    @ApiModelProperty(value = "客户身份证号码(水机用户)")
    private String consumerIdCard;
    @ApiModelProperty(value = "设备批次码")
    private String deviceBatchCode;

  /*  @ApiModelProperty(value = "产品类型Id")
    private Integer productTypeId;
    @ApiModelProperty(value = "产品类型")
    //目前数据库查出只有水机
    private String productType;*/
    /*@ApiModelProperty(value = "产品范围id")
    private String kindId;
    @ApiModelProperty(value = "产品范围：家用、商用")
    private String kindName;*/
    @ApiModelProperty(value = "设备型号Id")
    private Integer deviceModelId;
    @ApiModelProperty(value = "设备型号")
    private String deviceModelName;
    @ApiModelProperty(value = "设备Id")
    private Integer deviceId;
    @ApiModelProperty(value = "设备SN码")
    private String deviceSncode;
    @ApiModelProperty(value = "设备ICCID")
    private String deviceSimcard;
    @ApiModelProperty(value = "计费方式id")
    private Integer costId;
    @ApiModelProperty(value = "计费方式名称")
    private String costName;
    @ApiModelProperty(value = " 支付金额(单位：分)")
    private Integer payMoney;
    @ApiModelProperty(value = "支付类型")
    private String payType;
    @ApiModelProperty(value = "工单备注")
    private String workOrderRemark;
    @ApiModelProperty(value = "完成时间天数配置,用于最晚完成时间显示")
    private Integer completeDayNum = 0;
    @ApiModelProperty(value = "预约地址")
    private String bespeakAddress;
    @ApiModelProperty(value = "预约备注")
    private String appointRemark;
    @ApiModelProperty(value = "下一步骤")
    private Integer nextStep;
    @ApiModelProperty(value = "当前步骤")
    private Integer currentStep;
    @ApiModelProperty(value = "修改预约状态：Y-是 N-否")
    private String bespeakStatusSeconds;
    @ApiModelProperty(value = "修改预约时间")
    private Date bespeakTimeSeconds;
    @ApiModelProperty(value = "用户评价时间")
    private Date userAppriseTime;
    @ApiModelProperty(value = "用户评价状态")
    private String workorderUserAppraiseStatus;
    @ApiModelProperty(value = "用户评价状态描述")
    private String workorderUserAppraiseStatusText;
    @ApiModelProperty(value = "产品ID")
    private Integer productId;
    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "创建人")
    private String createUser;
    @ApiModelProperty(value = "更新人")
    private String updateUser;
    @ApiModelProperty(value = "是否已删除：Y-已删除 ，N-未删除")
    private String delStatus;
    @ApiModelProperty(value = "删除时间")
    private Date deleteTime;



    public RepairWorkOrderDTO() {
    }


    /**
     * 将数据库实体对象信息拷贝到业务对象RepairWorkOrdervo上。
     * plugin author ylfjm.
     *
     * @param vo 业务对象
     */
    public void convert(RepairWorkOrderVO vo) {
        vo.setId(this.id);
        vo.setScanBatchCodeStatus(this.scanBatchCodeStatus);
        if (this.scanBatchCodeTime != null) {
            vo.setScanBatchCodeTime(this.scanBatchCodeTime.getTime());
        }
        vo.setApplyChangeDeviceStatus(this.applyChangeDeviceStatus);
        vo.setApplyChangeDeviceResultStatus(this.applyChangeDeviceResultStatus);
        vo.setApplyChangeDeviceResult(this.applyChangeDeviceResult);
        vo.setRefuseApplyReason(this.refuseApplyReason);
        vo.setApplycount(this.applycount);
        vo.setFaultDescise(this.faultDescise);
        vo.setRepairAdvice(this.repairAdvice);
        vo.setChangeDeviceStatus(this.changeDeviceStatus);
        if (this.changeDeviceTime != null) {
            vo.setChangeDeviceTime(this.changeDeviceTime.getTime());
        }
        vo.setChangeDeviceSncode(this.changeDeviceSncode);
        vo.setChangeDeviceSimcard(this.changeDeviceSimcard);
        vo.setChangeDeviceBatchCode(this.changeDeviceBatchCode);
        vo.setFailurePhenomenonIds(this.failurePhenomenonIds);
        vo.setFactFaultStatus(this.factFaultStatus);
        if (this.factFaultDate != null) {
            vo.setFactFaultDate(this.factFaultDate.getTime());
        }
        vo.setAddrProvince(this.addrProvince);
        vo.setAddrCity(this.addrCity);
        vo.setAddrRegion(this.addrRegion);
        vo.setAddress(this.address);
        vo.setAcceptStatus(this.acceptStatus);
        if (this.acceptTime != null) {
            vo.setAcceptTime(this.acceptTime.getTime());
        }
        vo.setBespeakStatus(this.bespeakStatus);
        if (this.acceptTime != null) {
            vo.setBespeakTime(this.bespeakTime.getTime());
        }
        vo.setStartServerStatus(this.startServerStatus);
        if (this.startServerTime != null) {
            vo.setStartServerTime(this.startServerTime.getTime());
        }
        vo.setState(this.state);
        vo.setStateText(this.stateText);
        vo.setWorkOrderCompleteStatus(this.workOrderCompleteStatus);
        if (this.workOrderCompleteTime != null) {
            vo.setWorkOrderCompleteTime(this.workOrderCompleteTime.getTime());
        }
        if (this.countdownTime != null) {
            vo.setCountdownTime(this.countdownTime.getTime());
        }
        vo.setWorkCode(this.workCode);
        if(this.distributorId != null){
            vo.setDistributorId(this.distributorId.toString());
        }
        vo.setDistributorName(this.distributorName);
        vo.setDistributorPhone(this.distributorPhone);
        if(this.distributorRoleId != null){
            vo.setDistributorRoleId(this.distributorRoleId.toString());
        }
        vo.setDistributorRoleName(this.distributorRoleName);
        //vo.setDistributorIdCard(this.distributorIdCard);
        if(distributorChildId != null){
            vo.setDistributorChildId(this.distributorChildId.toString());
        }
        vo.setDistributorChildName(this.distributorChildName);
        vo.setDistributorChildPhone(this.distributorChildPhone);
        if(this.engineerId != null){
            vo.setEngineerId(this.engineerId.toString());
        }
        vo.setEngineerName(this.engineerName);
        vo.setEngineerPhone(this.engineerPhone);
        if(this.stationId != null){
            vo.setServiceSiteId(this.stationId.toString());
        }
        if(StringUtil.isNotEmpty(this.stationName)){
            vo.setServiceSiteName(this.stationName);
        }
        if(this.consumerId != null){
            vo.setConsumerId(this.consumerId.toString());
        }
        vo.setConsumerName(this.consumerName);
        vo.setConsumerPhone(this.consumerPhone);
        //vo.setConsumerIdCard(this.consumerIdCard);
        vo.setDeviceBatchCode(this.deviceBatchCode);
       /* if(this.productTypeId != null){
            vo.setProductId(this.productTypeId.toString());
        }
        vo.setProduct(this.productType);*/
      /*  if(this.kindId != null){
            vo.setKindId(this.kindId);
        }
        vo.setKindName(this.kindName);*/
        if(this.deviceModelId !=null){
            vo.setDeviceModel(this.deviceModelId.toString());
        }

        vo.setDeviceModelName(this.deviceModelName);
        if(this.deviceId !=null){
            vo.setDeviceId(this.deviceId.toString());
        }
        vo.setDeviceSncode(this.deviceSncode);
        vo.setDeviceSimcard(this.deviceSimcard);
        if(this.costId != null){
            vo.setChargeId(this.costId.toString());
        }
        vo.setChargeName(this.costName);

        /*vo.setPayMoney(this.payMoney);
        vo.setPayType(this.payType);*/
        vo.setWorkOrderRemark(this.workOrderRemark);
        vo.setCompleteDayNum(this.completeDayNum);
        vo.setBespeakAddress(this.bespeakAddress);
        vo.setAppointRemark(this.appointRemark);
        vo.setNextStep(this.nextStep);
        vo.setCurrentStep(this.currentStep);
        vo.setBespeakStatusSeconds(this.bespeakStatusSeconds);
        if (this.bespeakTimeSeconds != null) {
            vo.setBespeakTimeSeconds(this.bespeakTimeSeconds.getTime());
        }
        if (this.userAppriseTime != null) {
            vo.setUserAppriseTime(this.userAppriseTime.getTime());
        }
        vo.setWorkorderUserAppraiseStatus(this.workorderUserAppraiseStatus);
        vo.setWorkorderUserAppraiseStatusText(this.workorderUserAppraiseStatusText);
        if (this.createTime != null) {
            vo.setCreateTime(this.createTime.getTime());
        }
        if (this.updateTime != null) {
            vo.setUpdateTime(this.updateTime.getTime());
        }
        vo.setCreateUser(this.createUser);
        //vo.setUpdateUser(this.updateUser);
        vo.setDelStatus(this.delStatus);
        if (this.deleteTime != null) {
            vo.setDeleteTime(this.deleteTime.getTime());
        }
        /* vo.setYimaoOldSystemId(this.yimaoOldSystemId);*/
    }
}
