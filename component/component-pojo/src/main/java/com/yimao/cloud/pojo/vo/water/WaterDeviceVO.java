package com.yimao.cloud.pojo.vo.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * 描述：水机设备VO对象
 *
 * @Author Zhang Bo
 * @Date 2019/7/9
 */
@ApiModel(description = "水机设备VO对象，设备详情页使用")
@Getter
@Setter
public class WaterDeviceVO {

    private Integer id;
    //工单号
    private String workOrderId;
    //子订单号
    private String subOrderId;
    private String orderFrom;
    private Date orderCreateTime;

    //安装工程师ID
    private Integer engineerId;

    //SN码
    private String sn;
    //SN码
    private String snCode;
    //生产批次码
    private String batchCode;
    //产品类型
    private String deviceType;
    //产品范围
    private String deviceScope;
    //水机设备型号
    private String deviceModel;
    //省份
    private String province;
    //城市
    private String city;
    //地区
    private String region;
    //地址
    private String address;
    //设备摆放地
    private String place;
    //更换位置
    private String changePlace;
    //是否在线
    private String online;
    //设备网络连接类型：1-WIFI；3-3G；中文描述
    private String connectionType;
    //设备网络连接类型：1-WIFI；3-3G；
    private Integer connType;
    //设备状态
    private String deviceStatus;
    //版本
    private String version;
    //添加时间
    private Date createTime;
    //经销商id
    private Integer distributorId;

    //设备余额
    private BigDecimal money;
    //使用时长(单位：分）
    private Integer currentTotalTime;
    //使用流量（单位：升）
    private Integer currentTotalFlow;

    //设备用户姓名
    private String deviceUserName;
    //设备用户手机号
    private String deviceUserPhone;
    //安装工程师姓名
    private String engineerName;
    //安装工程师手机号
    private String engineerPhone;
    //经销商姓名
    private String distributorName;
    //经销商手机号
    private String distributorPhone;
    //经销商账号
    private String distributorAccount;

    //运营商ICCID：ICCID号码段+号码
    private String iccid;
    // sim卡运营商
    private Integer simAccountId;
    //SIM卡是否激活：0-未激活；1-已激活
    private String simActivating;
    //本月使用流量
    private String monthDataFlowUsed;
    //SIM卡激活时间
    private Date simActivatingTime;
    //SIM卡运营商
    private String simCompany;

    //计费方式名称
    private String costName;

    //最后在线时间
    private Date lastOnlineTime;

    private Set<String> adslotStock;

    private String ontime;
    private String offtime;

    @ApiModelProperty(position = 201, value = "当前计费方式")
    private String currentCostName;


    //版本信息
    @ApiModelProperty(position = 202,value = "水机版本号")
    private String appVersion;
    @ApiModelProperty(position = 203,value = "消耗流量类型：1-WIFI；3-3G；")
    private Integer consumeFlowType;
    @ApiModelProperty(position = 204,value = "消耗流量类型名称：1-WIFI；3-3G；")
    private String consumeFlowTypeName;
    @ApiModelProperty(position = 205,value = "设备更新版本时间")
    private Date updateVersionTime;
    @ApiModelProperty(position = 206,value = "设备更新版本时间")
    private String updateVersionTimeFormat;
    @ApiModelProperty(position = 207, value = "是否最新版本")
    private String isUpdate;
    @ApiModelProperty(position = 208, value = "设备组：1-用户组，2-服务站组")
    private String deviceGroup;

    @ApiModelProperty(value = "地区id")
    private Integer areaId;


}
