package com.yimao.cloud.order.po;

import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderStateEnum;
import com.yimao.cloud.pojo.dto.order.RepairWorkOrderDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 维修工单
 *
 * @author Liu Yi
 * @date 2019-3-20
 */
@Data
@Table(name = "workorder_repair")
public class RepairWorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /*private String orderId;*/
    private String scanBatchCodeStatus;
    private Date scanBatchCodeTime;
    private String applyChangeDeviceStatus;
    private String applyChangeDeviceResultStatus;
    private String applyChangeDeviceResult;
    private String refuseApplyReason;
    private Integer applycount = 0;
    private String faultDescise;
    private String repairAdvice;
    private String changeDeviceStatus;
    private Date changeDeviceTime;
    private String changeDeviceSncode;
    private String changeDeviceSimcard;
    private String changeDeviceBatchCode;
    private String failurePhenomenonIds;
    private String factFaultStatus;
    private Date factFaultDate;
    private String addrProvince;
    private String addrCity;
    private String addrRegion;
    private String address;
    private String acceptStatus;
    private Date acceptTime;
    private String bespeakStatus;
    private Date bespeakTime;
    private String startServerStatus;
    private Date startServerTime;
    private Integer state;
    private String stateText;
    private String workOrderCompleteStatus;
    private Date workOrderCompleteTime;
    private Date countdownTime;
    private String workCode;//百得同步维修工单code
    private String workOrderId;//安装工单id
    private Integer distributorId;
    private String distributorName;
    private String distributorPhone;
    private Integer distributorRoleId;
    private String distributorRoleName;
    private String distributorIdCard;
    private Integer distributorChildId;
    private String distributorChildName;
    private String distributorChildPhone;
    private Integer engineerId;
    private String engineerName;
    private String engineerPhone;
    private String engineerIdCard;
    private Integer stationId;
    private String stationName;
    private Integer consumerId;
    private String consumerName;
    private String consumerPhone;
    //private String consumerIdCard;
    private String deviceBatchCode;
    private Integer productId;         //产品ID
    private String productName;        //产品名称
   /* private Integer productTypeId;
    private String productType;*/
   /* private String kindId;
    private String kindName;*/
    private Integer deviceModelId;
    private String deviceModelName;
    private Integer deviceId;
    private String deviceSncode;
    private String deviceSimcard;
    private Integer costId;
    private String costName;
    private String workOrderRemark;
    private Integer completeDayNum;
    private String bespeakAddress;
    private String appointRemark;
    private Integer nextStep;
    private Integer currentStep;
    private String bespeakStatusSeconds;
    private Date bespeakTimeSeconds;
    private Date userAppriseTime;
    private String workorderUserAppraiseStatus;
    private String workorderUserAppraiseStatusText;

    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
    private String delStatus;
    private Date deleteTime;

    public RepairWorkOrder() {
        this.scanBatchCodeStatus = StatusEnum.NO.value();
        this.applyChangeDeviceStatus = StatusEnum.NO.value();
        this.applycount = 0;
        this.changeDeviceStatus = StatusEnum.NO.value();
        this.factFaultStatus = StatusEnum.NO.value();

       /* this.haveMasterRedStatus = StatusEnum.NO.value();*/
        this.bespeakStatusSeconds = StatusEnum.NO.value();
        this.workorderUserAppraiseStatus = StatusEnum.NO.value();
        /*this.haveMasterRedStatus = StatusEnum.NO.value();*/
        this.bespeakStatusSeconds = StatusEnum.NO.value();
        this.workorderUserAppraiseStatus = StatusEnum.NO.value();

        //this.payStatus = StatusEnum.NO.value();
        this.acceptStatus = StatusEnum.NO.value();
        this.bespeakStatus = StatusEnum.NO.value();
        this.startServerStatus = StatusEnum.NO.value();
        this.state = WorkOrderStateEnum.WORKORDER_STATE_NOT_ACCEPTED.state;
        this.stateText = WorkOrderStateEnum.WORKORDER_STATE_NOT_ACCEPTED.stateText;
        this.workOrderCompleteStatus = StatusEnum.NO.value();

        this.delStatus = StatusEnum.FALSE.value();
        /*this.idStatus = StatusEnum.YES.value();*/

       // this.voteStatus = StatusEnum.NO.value();
    }

    /**
     * 用业务对象RepairWorkOrderDTO初始化数据库对象RepairWorkOrder。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public RepairWorkOrder(RepairWorkOrderDTO dto) {
        this.id = dto.getId();
        this.scanBatchCodeStatus = dto.getScanBatchCodeStatus();
        this.scanBatchCodeTime = dto.getScanBatchCodeTime();
        this.applyChangeDeviceStatus = dto.getApplyChangeDeviceStatus();
        this.applyChangeDeviceResultStatus = dto.getApplyChangeDeviceResultStatus();
        this.applyChangeDeviceResult = dto.getApplyChangeDeviceResult();
        this.refuseApplyReason = dto.getRefuseApplyReason();
        this.applycount = dto.getApplycount();
        this.faultDescise = dto.getFaultDescise();
        this.repairAdvice = dto.getRepairAdvice();
        this.changeDeviceStatus = dto.getChangeDeviceStatus();
        this.changeDeviceTime = dto.getChangeDeviceTime();
        this.changeDeviceSncode = dto.getChangeDeviceSncode();
        this.changeDeviceSimcard = dto.getChangeDeviceSimcard();
        this.changeDeviceBatchCode = dto.getChangeDeviceBatchCode();
        this.failurePhenomenonIds = dto.getFailurePhenomenonIds();
        this.factFaultStatus = dto.getFactFaultStatus();
        this.factFaultDate = dto.getFactFaultDate();
        this.addrProvince = dto.getAddrProvince();
        this.addrCity = dto.getAddrCity();
        this.addrRegion = dto.getAddrRegion();
        this.address = dto.getAddress();
        this.acceptStatus = dto.getAcceptStatus();
        this.acceptTime = dto.getAcceptTime();
        this.bespeakStatus = dto.getBespeakStatus();
        this.bespeakTime = dto.getBespeakTime();
        this.startServerStatus = dto.getStartServerStatus();
        this.startServerTime = dto.getStartServerTime();
        this.state = dto.getState();
        this.stateText = dto.getStateText();
        this.workOrderCompleteStatus = dto.getWorkOrderCompleteStatus();
        this.workOrderCompleteTime = dto.getWorkOrderCompleteTime();
        this.countdownTime = dto.getCountdownTime();
        this.workCode = dto.getWorkCode();
        this.workOrderId = dto.getWorkOrderId();
        this.distributorId = dto.getDistributorId();
        this.distributorName = dto.getDistributorName();
        this.distributorPhone = dto.getDistributorPhone();
        this.distributorRoleId = dto.getDistributorRoleId();
        this.distributorRoleName = dto.getDistributorRoleName();
        this.distributorIdCard = dto.getDistributorIdCard();
        this.distributorChildId = dto.getDistributorChildId();
        this.distributorChildName = dto.getDistributorChildName();
        this.distributorChildPhone = dto.getDistributorChildPhone();
        this.engineerId = dto.getEngineerId();
        this.engineerName = dto.getEngineerName();
        this.engineerPhone = dto.getEngineerPhone();
        this.engineerIdCard = dto.getEngineerIdCard();

        this.stationId = dto.getStationId();
        this.stationName = dto.getStationName();
        this.consumerId = dto.getConsumerId();
        this.consumerName = dto.getConsumerName();
        this.consumerPhone = dto.getConsumerPhone();
        this.deviceBatchCode = dto.getDeviceBatchCode();
        this.productId = dto.getProductId();
        this.productName = dto.getProductName();
       /* this.kindId = dto.getKindId();
        this.kindName = dto.getKindName();*/
        this.deviceModelId = dto.getDeviceModelId();
        this.deviceModelName = dto.getDeviceModelName();
        this.deviceId = dto.getDeviceId();
        this.deviceSncode = dto.getDeviceSncode();
        this.deviceSimcard = dto.getDeviceSimcard();
        this.costId = dto.getCostId();
        this.costName = dto.getCostName();
        this.workOrderRemark = dto.getWorkOrderRemark();
        this.completeDayNum = dto.getCompleteDayNum();
        this.bespeakAddress = dto.getBespeakAddress();
        this.appointRemark = dto.getAppointRemark();
        this.nextStep = dto.getNextStep();
        this.currentStep = dto.getCurrentStep();
        this.bespeakStatusSeconds = dto.getBespeakStatusSeconds();
        this.bespeakTimeSeconds = dto.getBespeakTimeSeconds();
        this.userAppriseTime = dto.getUserAppriseTime();
        this.workorderUserAppraiseStatus = dto.getWorkorderUserAppraiseStatus();
        this.workorderUserAppraiseStatusText = dto.getWorkorderUserAppraiseStatusText();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.createUser = dto.getCreateUser();
        this.updateUser = dto.getUpdateUser();
        this.delStatus = dto.getDelStatus();
        this.deleteTime = dto.getDeleteTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象RepairWorkOrderDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(RepairWorkOrderDTO dto) {
        dto.setId(this.id);
        dto.setScanBatchCodeStatus(this.scanBatchCodeStatus);
        dto.setScanBatchCodeTime(this.scanBatchCodeTime);
        dto.setApplyChangeDeviceStatus(this.applyChangeDeviceStatus);
        dto.setApplyChangeDeviceResultStatus(this.applyChangeDeviceResultStatus);
        dto.setApplyChangeDeviceResult(this.applyChangeDeviceResult);
        dto.setRefuseApplyReason(this.refuseApplyReason);
        dto.setApplycount(this.applycount);
        dto.setFaultDescise(this.faultDescise);
        dto.setRepairAdvice(this.repairAdvice);
        dto.setChangeDeviceStatus(this.changeDeviceStatus);
        dto.setChangeDeviceTime(this.changeDeviceTime);
        dto.setChangeDeviceSncode(this.changeDeviceSncode);
        dto.setChangeDeviceSimcard(this.changeDeviceSimcard);
        dto.setChangeDeviceBatchCode(this.changeDeviceBatchCode);
        dto.setFailurePhenomenonIds(this.failurePhenomenonIds);
        dto.setFactFaultStatus(this.factFaultStatus);
        dto.setFactFaultDate(this.factFaultDate);
        dto.setAddrProvince(this.addrProvince);
        dto.setAddrCity(this.addrCity);
        dto.setAddrRegion(this.addrRegion);
        dto.setAddress(this.address);
        dto.setAcceptStatus(this.acceptStatus);
        dto.setAcceptTime(this.acceptTime);
        dto.setBespeakStatus(this.bespeakStatus);
        dto.setBespeakTime(this.bespeakTime);
        dto.setStartServerStatus(this.startServerStatus);
        dto.setStartServerTime(this.startServerTime);
        dto.setState(this.state);
        dto.setStateText(this.stateText);
        dto.setWorkOrderCompleteStatus(this.workOrderCompleteStatus);
        dto.setWorkOrderCompleteTime(this.workOrderCompleteTime);
        dto.setCountdownTime(this.countdownTime);
        dto.setWorkCode(this.workCode);
        dto.setWorkOrderId(this.workOrderId);
        dto.setDistributorId(this.distributorId);
        dto.setDistributorName(this.distributorName);
        dto.setDistributorPhone(this.distributorPhone);
        dto.setDistributorRoleId(this.distributorRoleId);
        dto.setDistributorRoleName(this.distributorRoleName);
        dto.setDistributorIdCard(this.distributorIdCard);
        dto.setDistributorChildId(this.distributorChildId);
        dto.setDistributorChildName(this.distributorChildName);
        dto.setDistributorChildPhone(this.distributorChildPhone);
        dto.setEngineerId(this.engineerId);
        dto.setEngineerName(this.engineerName);
        dto.setEngineerPhone(this.engineerPhone);
        dto.setEngineerIdCard(this.engineerIdCard);
        dto.setStationId(this.stationId);
        dto.setStationName(this.stationName);
        dto.setConsumerId(this.consumerId);
        dto.setConsumerName(this.consumerName);
        dto.setConsumerPhone(this.consumerPhone);
        dto.setDeviceBatchCode(this.deviceBatchCode);
        dto.setProductId(this.productId);
        dto.setProductName(this.productName);
       /* dto.setKindId(this.kindId);
        dto.setKindName(this.kindName);*/
        dto.setDeviceModelId(this.deviceModelId);
        dto.setDeviceModelName(this.deviceModelName);
        dto.setDeviceId(this.deviceId);
        dto.setDeviceSncode(this.deviceSncode);
        dto.setDeviceSimcard(this.deviceSimcard);
        dto.setCostId(this.costId);
        dto.setCostName(this.costName);
        dto.setWorkOrderRemark(this.workOrderRemark);
        dto.setCompleteDayNum(this.completeDayNum);
        dto.setBespeakAddress(this.bespeakAddress);
        dto.setAppointRemark(this.appointRemark);
        dto.setNextStep(this.nextStep);
        dto.setCurrentStep(this.currentStep);
        dto.setBespeakStatusSeconds(this.bespeakStatusSeconds);
        dto.setBespeakTimeSeconds(this.bespeakTimeSeconds);
        dto.setUserAppriseTime(this.userAppriseTime);
        dto.setWorkorderUserAppraiseStatus(this.workorderUserAppraiseStatus);
        dto.setWorkorderUserAppraiseStatusText(this.workorderUserAppraiseStatusText);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setCreateUser(this.createUser);
        dto.setUpdateUser(this.updateUser);
        dto.setDelStatus(this.delStatus);
        dto.setDeleteTime(this.deleteTime);
    }
}
