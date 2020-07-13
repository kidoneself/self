package com.yimao.cloud.pojo.dto.order;


import com.yimao.cloud.pojo.vo.out.MaintenanceWorkOrderVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.Date;

/***
 * 功能描述:维护工单实体类
 *
 * @auther: liu yi
 * @date: 2019/4/1 9:58
 */
@Getter
@Setter
@ApiModel(description = "维护工单")
public class MaintenanceWorkOrderDTO {
    @ApiModelProperty(value = "ID")
    private String id;
    @ApiModelProperty(value = "滤芯数量")
    private Integer filterCount;
    @ApiModelProperty(value = "销毁图片url")
    private String destroyFilterImg;
    @ApiModelProperty(value = "更换滤芯状态")
    private String changeFilterStatus;
    @ApiModelProperty(value = "更换滤芯时间")
    private Date changeFilterTime;
    @ApiModelProperty(value = "销毁滤芯图片状态")
    private String destroyFilterImgStatus;
    @ApiModelProperty(value = "毁滤芯时间")
    private Date destroyFilterImgTime;
    @ApiModelProperty(value = "所需耗材名称")
    private String materielDetailName;
    @ApiModelProperty(value = "需要更换的耗材类型id")
    private String materielDetailIds;
    @ApiModelProperty(value = " 维护内容")
    private String maintenanceRemark;
    @ApiModelProperty(value = "详细地址，用于搜索")
    private String addressDetail;
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
    private Date updateDate;
    @ApiModelProperty(value = "最晚完成时间(倒计时)")
    private Date countdownTime;
    @ApiModelProperty(value = "工单code")
    private String workCode;
    @ApiModelProperty(value = "经销商id")
    private Integer distributorId;
    @ApiModelProperty(value = "经销商")
    private String distributorName;
    @ApiModelProperty(value = "经销商电话")
    private String distributorPhone;
    @ApiModelProperty(value = "经销商角色id")
    private Integer distributorRoleId;
    @ApiModelProperty(value = "经销商角色")
    private String distributorRoleName;
    @ApiModelProperty(value = "经销商身份证号")
    private String distributorIdCard;
    @ApiModelProperty(value = "经销商子账号id")
    private Integer distributorChildId;
    @ApiModelProperty(value = "经销商子账号名称")
    private String distributorChildName;
    @ApiModelProperty(value = "经销商子账号电话")
    private String distributorChildPhone;
    @ApiModelProperty(value = "安装工id")
    private Integer engineerId;
    @ApiModelProperty(value = "安装工名称")
    private String engineerName;
    @ApiModelProperty(value = "安装工电话")
    private String engineerPhone;
    @ApiModelProperty(value = "安装工身份证号")
    private String engineerIdCard;
    @ApiModelProperty(value = " 服务站id")
    private Integer stationId;
    @ApiModelProperty(value = " 服务站名称")
    private String stationName;
    @ApiModelProperty(value = "用户id")
    private Integer consumerId;
    @ApiModelProperty(value = "用户名")
    private String consumerName;
    @ApiModelProperty(value = "用户电话")
    private String consumerPhone;
    @ApiModelProperty(value = "用户身份证号")
    private String consumerIdCard;
    @ApiModelProperty(value = "批次码")
    private String deviceBatchCode;
    //    @ApiModelProperty(value = "产品类型Id")
//    private Integer productTypeId;
//    @ApiModelProperty(value = "产品类型")
//    private String productType;
    @ApiModelProperty(value = "产品Id")
    private Integer productId;
    @ApiModelProperty(value = "产品")
    private String productName;
    @ApiModelProperty(value = "产品范围id")
    private String kindId;
    @ApiModelProperty(value = "产品范围")
    private String kindName;
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
    Date bespeakTimeSeconds;
    @ApiModelProperty(value = "用户评价时间")
    Date userAppriseTime;
    @ApiModelProperty(value = "用户评价状态")
    String workorderUserAppraiseStatus;
    @ApiModelProperty(value = "用户评价状态描述")
    String workorderUserAppraiseStatusText;

    @ApiModelProperty(value = "接单状态")
    private String acceptStatus;
    @ApiModelProperty(value = "接单时间")
    private Date acceptTime;
    @ApiModelProperty(value = "预约状态")
    private String bespeakStatus;
    @ApiModelProperty(value = "预约时间")
    private Date bespeakTime;
    @ApiModelProperty(value = "开始服务状态")
    private String startServerStatus;
    @ApiModelProperty(value = "开始服务时间")
    private Date startServerTime;
    @ApiModelProperty(value = "1-未受理 2-已受理,3-处理中,4-已完成  修改为-> 0-改约中 1-待维护 2- 3-处理中 4-已完成")
    private Integer state;
    @ApiModelProperty(value = "工单状态描述")
    private String stateText;
    @ApiModelProperty(value = "完成状态：Y-完成 ，N-未完成")
    private String workOrderCompleteStatus;
    @ApiModelProperty(value = "完成时间")
    private Date workOrderCompleteTime;
    @ApiModelProperty(value = "省份")
    private String addrProvince;
    @ApiModelProperty(value = "城市")
    private String addrCity;
    @ApiModelProperty(value = "县")
    private String addrRegion;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "创建用户")
    private String createUser;
    @ApiModelProperty(value = "更新用户")
    private String updateUser;
    @ApiModelProperty(value = "是否已删除：Y-已删除 ，N-未删除")
    private String delStatus;
    @ApiModelProperty(value = "删除时间")
    private Date deleteTime;
    @ApiModelProperty(value = "老系统id")
    private String yimaoOldSystemId;
    @ApiModelProperty(value = "审核方式：1-自动审核 2-人工审核")
    private Integer auditType;
    @ApiModelProperty(value = "来源：1-自动生成 2-总部添加，默认自动生成")
    private Integer source;
    private boolean mustUploadImg;
    private String workOrderId;//原工单id


    @ApiModelProperty(value = "水机所在经度")
    private Double longitude;
    @ApiModelProperty(value = "水机所在纬度")
    private Double latitude;
    @ApiModelProperty(value = "挂单时间")
    private Date hangTime;
    @ApiModelProperty(value = "改约理由")
    private String hangReason;
    @ApiModelProperty(value = "挂单改约备注")
    private String hangRemark;
    @ApiModelProperty(value = "改约服务开始时间")
    private Date hangStartTime;
    @ApiModelProperty(value = "改约服务结束时间")
    private Date hangEndTime;
    @ApiModelProperty(value = "是否自助更换")
    private Integer hasSelfChange;
    @ApiModelProperty(value = "剩余金额")
    private BigDecimal money;
    @ApiModelProperty(value = "设备安装时间")
    private Date deviceInstallTime;
    @ApiModelProperty(value = "设备完成时间")
    private Date deviceCompleteTime;

    private Integer oldEngineerId;       //更换订单详情的安装工信息用到


    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceWorkOrderMaintenancevo上。
     * plugin author ylfjm.
     *
     * @param vo 业务对象
     */
    public void convert(MaintenanceWorkOrderVO vo) {
        vo.setId(this.id);
        vo.setFilterCount(this.filterCount);
        vo.setDestroyFilterImg(this.destroyFilterImg);
        vo.setChangeFilterStatus(this.changeFilterStatus);
        if (changeFilterTime != null) {
            vo.setChangeFilterTime(this.changeFilterTime.getTime());
        }
        vo.setDestroyFilterImgStatus(this.destroyFilterImgStatus);
        if (destroyFilterImgTime != null) {
            vo.setDestroyFilterImgTime(this.destroyFilterImgTime.getTime());
        }
        vo.setMaterielDetailName(this.materielDetailName);
        vo.setMaterielDetailIds(this.materielDetailIds);
        vo.setMaintenanceRemark(this.maintenanceRemark);
        //vo.setAddressDetail(this.addressDetail);
        vo.setCompleteType(this.completeType);
        vo.setCompleteTypeText(this.completeTypeText);
        vo.setUpdateBy(this.updateBy);
        vo.setUpdateByName(this.updateByName);
        vo.setUpdateByRole(this.updateByRole);
        if (updateDate != null) {
            vo.setUpdateDate(this.updateDate.getTime());
        }
        if (countdownTime != null) {
            vo.setCountdownTime(this.countdownTime.getTime());
        }
        vo.setWorkCode(this.workCode);
        if (this.distributorId != null) {
            vo.setDistributorId(this.distributorId.toString());
        }
        vo.setDistributorName(this.distributorName);
        vo.setDistributorPhone(this.distributorPhone);
        if (this.distributorRoleId != null) {
            vo.setDistributorRoleId(this.distributorRoleId.toString());
        }
        vo.setDistributorRoleName(this.distributorRoleName);
        //vo.setDistributorIdCard(this.distributorIdCard);
        if (this.distributorChildId != null) {
            vo.setDistributorChildId(this.distributorChildId.toString());
        }
        vo.setDistributorChildName(this.distributorChildName);
        vo.setDistributorChildPhone(this.distributorChildPhone);
        if (this.engineerId != null) {
            vo.setEngineerId(this.engineerId.toString());
        }
        vo.setEngineerName(this.engineerName);
        vo.setEngineerPhone(this.engineerPhone);
        //vo.setEngineerIdCard(this.engineerIdCard);
        if (this.stationId != null) {
            vo.setServiceSiteId(this.stationId.toString());
        }
        vo.setServiceSiteName(this.stationName);
        if (this.consumerId != null) {
            vo.setConsumerId(this.consumerId.toString());
        }
        vo.setConsumerName(this.consumerName);
        vo.setConsumerPhone(this.consumerPhone);
        //vo.setConsumerIdCard(this.consumerIdCard);
        vo.setDeviceBatchCode(this.deviceBatchCode);
        if (this.productId != null) {
            vo.setProductId(this.productId.toString());
        }
        vo.setProduct(this.productName);

        if (this.kindId != null) {
            vo.setKindId(this.kindId.toString());
        }
        vo.setKindName(this.kindName);

        if (this.deviceModelId != null) {
            vo.setDeviceModel(this.deviceModelId.toString());
        }
        vo.setDeviceModelName(this.deviceModelName);
        if (this.deviceId != null) {
            vo.setDeviceId(this.deviceId.toString());
        }
        vo.setDeviceSncode(this.deviceSncode);
        vo.setDeviceSimcard(this.deviceSimcard);
        if (this.costId != null) {
            vo.setChargeId(this.costId.toString());
        }
        vo.setChargeName(this.costName);
        vo.setWorkOrderRemark(this.workOrderRemark);
        vo.setCompleteDayNum(this.completeDayNum);
        vo.setBespeakAddress(this.bespeakAddress);
        vo.setAppointRemark(this.appointRemark);
        vo.setNextStep(this.nextStep);
        vo.setCurrentStep(this.currentStep);
        vo.setBespeakStatusSeconds(this.bespeakStatusSeconds);
        if (bespeakTimeSeconds != null) {
            vo.setBespeakTimeSeconds(this.bespeakTimeSeconds.getTime());
        }
        if (userAppriseTime != null) {
            vo.setUserAppriseTime(this.userAppriseTime.getTime());
        }
        vo.setWorkorderUserAppraiseStatus(this.workorderUserAppraiseStatus);
        vo.setWorkorderUserAppraiseStatusText(this.workorderUserAppraiseStatusText);
        vo.setAcceptStatus(this.acceptStatus);
        if (acceptTime != null) {
            vo.setAcceptTime(this.acceptTime.getTime());
        }
        vo.setBespeakStatus(this.bespeakStatus);
        if (bespeakTime != null) {
            vo.setBespeakTime(this.bespeakTime.getTime());
        }
        vo.setStartServerStatus(this.startServerStatus);
        if (startServerTime != null) {
            vo.setStartServerTime(this.startServerTime.getTime());
        }
        vo.setState(this.state);
        vo.setStateText(this.stateText);
        vo.setWorkOrderCompleteStatus(this.workOrderCompleteStatus);
        if (workOrderCompleteTime != null) {
            vo.setWorkOrderCompleteTime(this.workOrderCompleteTime.getTime());
        }
        vo.setAddrProvince(this.addrProvince);
        vo.setAddrCity(this.addrCity);
        vo.setAddrRegion(this.addrRegion);
        vo.setAddress(this.address);
        vo.setAddressDetail(this.getAddrProvince() + this.getAddrCity() + this.getAddrRegion() + this.getAddress());
        if (createTime != null) {
            vo.setCreateTime(this.createTime.getTime());
        }
        if (updateTime != null) {
            vo.setUpdateTime(this.updateTime.getTime());
        }
        vo.setCreateUser(this.createUser);
        vo.setUpdateUser(this.updateUser);
        vo.setDelStatus(this.delStatus);
        if (deleteTime != null) {
            vo.setDeleteTime(this.deleteTime.getTime());
        }
        vo.setYimaoOldSystemId(this.yimaoOldSystemId);
        vo.setSource(this.source);
        //vo.setAuditType(this.auditType);
    }
}
