package com.yimao.cloud.pojo.dto.order;

/**
 * @author Lizhqiang
 * @date 2020/7/3
 */

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 后台修改子订单操作确定按钮所需携带的参数
 */
@ApiModel(description = "地图工单汇总")
@Getter
@Setter
public class MapOrderDTO {

    @ApiModelProperty(value = "工单号")
    private String id;
    @ApiModelProperty(value = "主订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long mainOrderId;
    @ApiModelProperty(value = "子订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long subOrderId;
    @ApiModelProperty(value = "工单状态：1-未受理；2-已受理；3-处理中；4-已完成；")
    private Integer status;
    @ApiModelProperty(value = "水机用户ID")
    private Integer userId;
    @ApiModelProperty(value = "用户姓名")
    private String userName;
    @ApiModelProperty(value = "用户手机号")
    private String userPhone;
    @ApiModelProperty(value = "设备型号")
    private String deviceModel;
    @ApiModelProperty(value = "计费方式ID")
    private Integer costId;
    @ApiModelProperty(value = "计费方式：1-流量计费；2-包年计费；")
    private Integer costType;
    @ApiModelProperty(value = "计费方式名称")
    private String costName;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区县")
    private String region;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "地址详情")
    private String addressDetail;
    @ApiModelProperty(value = "经度")
    private String addrLongitude;
    @ApiModelProperty(value = "纬度")
    private String addrLatitude;
    @ApiModelProperty(value = "安装工ID")
    private Integer engineerId;
    @ApiModelProperty(value = "用户下单时选择的安装时间")
    private Date serviceTime;
    @ApiModelProperty("来源 0-安装工app创建 1-后台业务系统创建 2-水机故障推送创建")
    private Integer sourceType;
    @ApiModelProperty("发起故障类型名称")
    private String faultName;
    @ApiModelProperty("安装工服务时间")
    private Date engineerServiceTime;
    @ApiModelProperty("维修工单发起时间")
    private Date launchTime;
    @ApiModelProperty(value = "设备SN码")
    private String sn;
    @ApiModelProperty(value = "所需耗材名称")
    private String materielDetailNames;
    @ApiModelProperty(value = "合并时间串")
    private Date createTime;
    @ApiModelProperty(value = "合并时间串")
    private String createTimes;
    @ApiModelProperty(value = "维护工单状态：1-未受理；2-已受理；3-处理中；4-已完成；")
    private Integer state;
    @ApiModelProperty(value = "移机工单状态：1，2-移入；3，4-移出；")
    private Integer moveStatus;
    @ApiModelProperty(value = "经销商名字")
    private String distributorName;
    @ApiModelProperty(value = "移入地省")
    private String destProvince;
    @ApiModelProperty(value = "移入地市")
    private String destCity;
    @ApiModelProperty(value = "移入地区")
    private String destRegion;
    @ApiModelProperty(value = "移入地址")
    private String destAddress;
    @ApiModelProperty(value = "移入地址经度")
    private Double destLongitude;
    @ApiModelProperty(value = "移入地址纬度")
    private Double destLatitude;


}
