package com.yimao.cloud.pojo.vo.station;

import com.yimao.cloud.pojo.dto.order.WorkRepairFaultDTO;
import com.yimao.cloud.pojo.dto.order.WorkRepairMaterialUseRecordDTO;
import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 维修工单展示类
 *
 * @author yaoweijun
 */
@Data
@ApiModel(description = "维修工单VO")
public class WorkRepairOrderVO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -4520027268687541509L;
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("维修工单号")
    private String workOrderNo;
    @ApiModelProperty("工单状态 0-待维修 1-挂单 2-维修中 3-已完成")
    private Integer status;
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
    @ApiModelProperty("水机所属经销商id")
    private Integer distributorId;
    @ApiModelProperty("水机所属经销商姓名")
    private String distributorName;
    @ApiModelProperty("水机所属经销商手机号")
    private String distributorPhone;
    @ApiModelProperty("安装工id")
    private Integer engineerId;
    @ApiModelProperty("安装工姓名")
    private String engineerName;
    @ApiModelProperty("安装工服务站id")
    private Integer stationId;
    @ApiModelProperty("安装工服务站名称")
    private String stationName;
    @ApiModelProperty("水机所在省")
    private String province;
    @ApiModelProperty("水机所在市")
    private String city;
    @ApiModelProperty("水机所在区")
    private String region;
    @ApiModelProperty("水机用户id")
    private Integer deviceUserId;
    @ApiModelProperty("水机用户用户名")
    private String deviceUserName;
    @ApiModelProperty("水机用户手机号")
    private String deviceUserPhone;
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
    @ApiModelProperty(value = "改约服务开始结束时间展示", notes = "yyyy-MM-hh HH-HH")
    private String dislpayRevisionTime;
    @ApiModelProperty("服务设置开始时间")
    private Date serviceStartTime;
    @ApiModelProperty("服务设置结束时间")
    private Date serviceEndTime;
    @ApiModelProperty(value = "服务开始结束时间展示", notes = "yyyy-MM-hh HH-HH")
    private String dislpayServiceTime;
    @ApiModelProperty("安装工服务时间")
    private Date engineerServiceTime;
    @ApiModelProperty("安装工服务完成时间")
    private Date engineerFinishTime;
    @ApiModelProperty("系统手动创建人")
    private String systemCreator;
    @ApiModelProperty("水机信息")
    private WaterDeviceDTO waterDevice;
    @ApiModelProperty("水机故障筛选列表")
    List<WorkRepairFaultDTO> workRepairFaultList;
    @ApiModelProperty("水机可选耗材列表")
    List<GoodsMaterialsDTO> materialList;
    @ApiModelProperty("维修耗材使用纪录")
    List<WorkRepairMaterialUseRecordDTO> materialUseRecordList;
    @ApiModelProperty("安装工App-距离当前距离")
    private Integer distanceNum;

    private Integer oldEngineerId;
}
