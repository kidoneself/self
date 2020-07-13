package com.yimao.cloud.order.po;


import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderStateEnum;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/***
 * 功能描述:维护工单实体类
 *
 * @auther: liu yi
 * @date: 2019/4/1 9:58
 */
@Table(name = "workorder_maintenance")
@Data
public class MaintenanceWorkOrder {
    @Id
    private String id;
    private String workCode;
    private String materielDetailName;
    private String materielDetailIds;
    private String maintenanceRemark;
    private Integer distributorId;
    private String distributorName;
    private String distributorPhone;
    private Integer engineerId;
    private String engineerName;
    private String engineerPhone;
    private Integer stationId; //serviceSiteId
    private String stationName;//ServiceSiteName
    private Integer consumerId;
    private String consumerName;        //客户姓名
    private String consumerPhone;
    private String deviceBatchCode;     //批次码
    private String deviceSncode;        //SN
    private String deviceSimcard;
    private String workOrderRemark;
    private Date startServerTime;       //开始服务时间
    private Integer state;//1-未受理 2-处理中,3-改约,4-已完成
    private String stateText;
    private String workOrderCompleteStatus;
    private Date workOrderCompleteTime;
    private String addrProvince;
    private String addrCity;
    private String addrRegion;
    // private String addressDetail;
    private String address;//地址(不含省市区)
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
    private String delStatus;
    private Date deleteTime;
    //private String yimaoOldSystemId;
    private Integer auditType;//审核方式：1-自动审核 2-人工审核
    private Integer source;//来源：1-自动生成 2-总部添加，默认自动生成
    private String kindId;
    private String kindName;//产品范围(家用，商用)

    private Integer deviceId;//设备id
    private Integer deviceModelId;//产品内部型号id
    private String deviceModelName;//1602T-00 产品内部型号
    private String productModel;       //产品型号 categoryName
    private Integer productId;//产品ID
    private String productName;//产品名称
    private Integer costId;//水机计费方式ID
    private String costName;//水机计费方式名称
    private String workOrderId;//原工单id

    private Double longitude;                   //水机所在经度+
    private Double latitude;                    //水机所在纬度+
    private Date hangTime;                      //挂单时间
    private String hangReason;                  //改约理由
    private String hangRemark;                  //挂单改约备注
    private Date hangStartTime;                 //改约服务开始时间
    private Date hangEndTime;                   //改约服务结束时间
    private Integer hasSelfChange;              //是否自助更换 1-是 2-否

    private Integer filterCount;
    private String destroyFilterImg;
    private String changeFilterStatus;
    private Date changeFilterTime;
    private String destroyFilterImgStatus;
    private Date destroyFilterImgTime;
    private Integer completeType;
    private String completeTypeText;
    private String updateBy;
    private String updateByName;
    private String updateByRole;
    private Date updateDate;
    private Date countdownTime;
    private Integer distributorRoleId;
    private String distributorRoleName;
    private String distributorIdCard;
    private Integer distributorChildId;
    private String distributorChildName;
    private String distributorChildPhone;
    private String engineerIdCard;
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
    private String acceptStatus;
    private Date acceptTime;
    private String bespeakStatus;
    private Date bespeakTime;
    private String startServerStatus;


    public MaintenanceWorkOrder() {
        this.changeFilterStatus = StatusEnum.NO.value();
        this.destroyFilterImgStatus = StatusEnum.NO.value();
        //this.haveMasterRedStatus = StatusEnum.NO.value();
        this.bespeakStatusSeconds = StatusEnum.NO.value();
        this.workorderUserAppraiseStatus = StatusEnum.NO.value();

        //this.payStatus = StatusEnum.NO.value();
        this.acceptStatus = StatusEnum.NO.value();
        this.bespeakStatus = StatusEnum.NO.value();
        this.startServerStatus = StatusEnum.NO.value();
        this.state = WorkOrderStateEnum.WORKORDER_STATE_NOT_ACCEPTED.state;
        this.stateText = WorkOrderStateEnum.WORKORDER_STATE_NOT_ACCEPTED.stateText;
        this.workOrderCompleteStatus = StatusEnum.NO.value();
        //this.voteStatus = StatusEnum.NO.value();
        this.delStatus = StatusEnum.NO.value();
    }


    /**
     * 用业务对象MaintenanceWorkOrderDTO初始化数据库对象MaintenanceWorkOrder。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MaintenanceWorkOrder(MaintenanceWorkOrderDTO dto) {
        this.id = dto.getId();
        this.filterCount = dto.getFilterCount();
        this.destroyFilterImg = dto.getDestroyFilterImg();
        this.changeFilterStatus = dto.getChangeFilterStatus();
        this.changeFilterTime = dto.getChangeFilterTime();
        this.destroyFilterImgStatus = dto.getDestroyFilterImgStatus();
        this.destroyFilterImgTime = dto.getDestroyFilterImgTime();
        this.materielDetailName = dto.getMaterielDetailName();
        this.materielDetailIds = dto.getMaterielDetailIds();
        this.maintenanceRemark = dto.getMaintenanceRemark();
        this.completeType = dto.getCompleteType();
        this.completeTypeText = dto.getCompleteTypeText();
        this.updateBy = dto.getUpdateBy();
        this.updateByName = dto.getUpdateByName();
        this.updateByRole = dto.getUpdateByRole();
        this.updateDate = dto.getUpdateDate();
        this.countdownTime = dto.getCountdownTime();
        this.workCode = dto.getWorkCode();
        this.distributorId = dto.getDistributorId();
        this.distributorName = dto.getDistributorName();
        this.distributorPhone = dto.getDistributorPhone();
        this.distributorRoleId = dto.getDistributorRoleId();
        this.distributorRoleName = dto.getDistributorRoleName();
        //this.distributorIdCard = dto.getDistributorIdCard();
        this.distributorChildId = dto.getDistributorChildId();
        this.distributorChildName = dto.getDistributorChildName();
        this.distributorChildPhone = dto.getDistributorChildPhone();
        this.engineerId = dto.getEngineerId();
        this.engineerName = dto.getEngineerName();
        this.engineerPhone = dto.getEngineerPhone();
        //this.engineerIdCard = dto.getEngineerIdCard();
        this.stationId = dto.getStationId();
        this.stationName = dto.getStationName();
        this.consumerId = dto.getConsumerId();
        this.consumerName = dto.getConsumerName();
        this.consumerPhone = dto.getConsumerPhone();
        //this.consumerIdCard = dto.getConsumerIdCard();
        this.deviceBatchCode = dto.getDeviceBatchCode();
        this.productId = dto.getProductId();
        this.productName = dto.getProductName();
       /* this.productTypeId = dto.getProductTypeId();
        this.productType = dto.getProductType();*/
        this.kindId = dto.getKindId();
        this.kindName = dto.getKindName();
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
        this.addrProvince = dto.getAddrProvince();
        this.addrCity = dto.getAddrCity();
        this.addrRegion = dto.getAddrRegion();
        //this.addressDetail = dto.getAddressDetail();
        this.address = dto.getAddress();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.createUser = dto.getCreateUser();
        this.updateUser = dto.getUpdateUser();
        this.delStatus = dto.getDelStatus();
        this.deleteTime = dto.getDeleteTime();
        this.auditType = dto.getAuditType();
        this.source = dto.getSource();
        this.workOrderId = dto.getWorkOrderId();
        this.longitude = dto.getLongitude();
        this.latitude = dto.getLatitude();
        this.hangTime = dto.getHangTime();
        this.hangReason = dto.getHangReason();
        this.hangRemark = dto.getHangRemark();
        this.hangStartTime = dto.getHangStartTime();
        this.hangEndTime = dto.getHangEndTime();
        this.hangEndTime = dto.getHangEndTime();
        this.hasSelfChange = dto.getHasSelfChange();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象MaintenanceWorkOrderDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MaintenanceWorkOrderDTO dto) {
        dto.setId(this.id);
        dto.setFilterCount(this.filterCount);
        dto.setDestroyFilterImg(this.destroyFilterImg);
        dto.setChangeFilterStatus(this.changeFilterStatus);
        dto.setChangeFilterTime(this.changeFilterTime);
        dto.setDestroyFilterImgStatus(this.destroyFilterImgStatus);
        dto.setDestroyFilterImgTime(this.destroyFilterImgTime);
        dto.setMaterielDetailName(this.materielDetailName);
        dto.setMaterielDetailIds(this.materielDetailIds);
        dto.setMaintenanceRemark(this.maintenanceRemark);
        dto.setCompleteType(this.completeType);
        dto.setCompleteTypeText(this.completeTypeText);
        dto.setUpdateBy(this.updateBy);
        dto.setUpdateByName(this.updateByName);
        dto.setUpdateByRole(this.updateByRole);
        dto.setUpdateDate(this.updateDate);
        dto.setCountdownTime(this.countdownTime);
        dto.setWorkCode(this.workCode);
        dto.setDistributorId(this.distributorId);
        dto.setDistributorName(this.distributorName);
        dto.setDistributorPhone(this.distributorPhone);
        dto.setDistributorRoleId(this.distributorRoleId);
        dto.setDistributorRoleName(this.distributorRoleName);
        //dto.setDistributorIdCard(this.distributorIdCard);
        dto.setDistributorChildId(this.distributorChildId);
        dto.setDistributorChildName(this.distributorChildName);
        dto.setDistributorChildPhone(this.distributorChildPhone);
        dto.setEngineerId(this.engineerId);
        dto.setEngineerName(this.engineerName);
        dto.setEngineerPhone(this.engineerPhone);
        //dto.setEngineerIdCard(this.engineerIdCard);
        dto.setStationId(this.stationId);
        dto.setStationName(this.stationName);
        dto.setConsumerId(this.consumerId);
        dto.setConsumerName(this.consumerName);
        dto.setConsumerPhone(this.consumerPhone);
        //dto.setConsumerIdCard(this.consumerIdCard);
        dto.setDeviceBatchCode(this.deviceBatchCode);
        dto.setProductId(this.productId);
        dto.setProductName(this.productName);
       /* dto.setProductTypeId(this.productTypeId);
        dto.setProductType(this.productType);*/
        dto.setKindId(this.kindId);
        dto.setKindName(this.kindName);
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
        dto.setAddrProvince(this.addrProvince);
        dto.setAddrCity(this.addrCity);
        dto.setAddrRegion(this.addrRegion);
        //dto.setAddressDetail(this.addressDetail);
        dto.setAddress(this.address);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setCreateUser(this.createUser);
        dto.setUpdateUser(this.updateUser);
        dto.setDelStatus(this.delStatus);
        dto.setDeleteTime(this.deleteTime);
        dto.setAuditType(this.auditType);
        dto.setSource(this.source);
        dto.setWorkOrderId(this.workOrderId);
        dto.setLongitude(this.longitude);
        dto.setLatitude(this.latitude);
        dto.setHangTime(this.hangTime);
        dto.setHangReason(this.hangReason);
        dto.setHangRemark(this.hangRemark);
        dto.setHangStartTime(this.hangStartTime);
        dto.setHangEndTime(this.hangEndTime);
        dto.setHangEndTime(this.hangEndTime);
        dto.setHasSelfChange(this.hasSelfChange);
    }
}
