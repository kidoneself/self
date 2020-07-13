package com.yimao.cloud.pojo.dto.water;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 描述：水机设备
 *
 * @Author Zhang Bo
 * @Date 2019/2/23 15:19
 * @Version 1.0
 */
@Getter
@Setter
public class WaterDeviceDTO implements Serializable {
    private static final long serialVersionUID = 4727835418645950791L;
    private Integer id;
    //SN码
    private String sn;
    //SN码录入时间（设备激活时间）
    private Date snEntryTime;
    //工单号
    private String workOrderId;
    //运营商ICCID：ICCID号码段+号码
    private String iccid;
    //生产批次码
    // private String batchCode;
    //设备用户ID
    private Integer deviceUserId;
    //设备用户姓名
    private String deviceUserName;
    //设备用户手机号
    private String deviceUserPhone;
    //安装工程师ID
    private Integer engineerId;
    //旧的安装工程师ID,安装工转让，更新水机上的安装工信息用到
    private Integer oldEngineerId;
    //安装工程师姓名
    private String engineerName;
    //安装工程师手机号
    private String engineerPhone;
    //经销商ID
    private Integer distributorId;
    //经销商姓名
    private String distributorName;
    //经销商手机号
    private String distributorPhone;
    //经销商账号
    private String distributorAccount;
    //SIM卡是否激活：0-未激活；1-已激活
    private Boolean simActivating;
    //SIM卡激活时间
    private Date simActivatingTime;
    //SIM卡运营商
    private String simCompany;
    //经度
    private String longitude;
    //纬度
    private String latitude;
    //省份
    private String province;
    //城市
    private String city;
    //地区
    private String region;
    //地址
    private String address;
    //计费套餐Id
    private Integer costId;
    //计费方式：1-流量计费；2-包年计费；
    private Integer costType;
    //计费套餐
    private String costName;
    //当前设备的计费方式（续费之后不能再使用costType）：1-流量计费；2-包年计费；
    private Integer currentCostType;
    //当前设备的计费方式（续费之后不能再使用costName）：1-流量计费；2-包年计费；
    private String currentCostName;
    //上一次累计使用时长
    private Integer lastTotalTime;
    //上一次累计使用流量
    private Integer lastTotalFlow;
    //当前使用总时长
    private Integer currentTotalTime;
    //当前使用总流量
    private Integer currentTotalFlow;
    //设备总使用的时长(单位：分）
    private Integer useTime;
    //设备总使用的流量（单位：升）
    private Integer useFlow;
    //设备初始金额
    private BigDecimal initMoney;
    //设备余额
    private BigDecimal money;
    //设备剩余使用天数
    private Integer days;
    //设备摆放地
    private String place;
    //续了几次费了
    private Integer renewTimes;
    //设备续费状态：-1-无需续费；1-未续费；2-待续费；3-已续费；
    private Integer renewStatus;
    //设备续费状态中文描述
    private String renewStatusText;
    //最后一次续费时间
    private Date lastRenewTime;
    //是否变更了计费方式
    private Boolean costChanged;
    //修改后的计费套餐Id
    private Integer newCostId;
    //设备变更套餐时间
    private Date lastCostChangeTime;
    //最后一次PP滤芯更换时间
    private Date lastPpChangeTime;
    //最后一次UDF滤芯更换时间
    private Date lastUdfChangeTime;
    //最后一次CTO滤芯更换时间
    private Date lastCtoChangeTime;
    //最后一次T33滤芯更换时间
    private Date lastT33ChangeTime;
    //生产批次码（物流编码）
    private String logisticsCode;
    //是否在线
    private Boolean online;
    //最后在线时间
    private Date lastOnlineTime;
    //SN码是否绑定水机
    private Boolean bind;
    //解绑标记
    private Boolean unbundling;
    //版本
    private String version;
    //产品类型
    private String deviceType;
    //产品范围
    private String deviceScope;
    //水机设备型号
    private String deviceModel;
    // sim卡运营商
    private Integer simAccountId;
    //设备欠费时间,续费之后.此字段需要变更为空
    // private Date deviceArrearsTime;
    //设备网络连接类型：1-WIFI；3-3G；
    private Integer connectionType;
    //是否锁机，Y为锁机,N为未锁机，NONE为后台设置不锁机
    private String lockState;
    //水机TDS值关联ID
    private Integer tdsId;

    //mongo数据库中的额主键
    private String oldId;
    //mongo数据库中的额主键
    private String oldCostId;

    // private String oldTdsId;
    // private String oldUserId;//老系统用户id
    private String oldCustomerId;//老系统安装工id
    private String oldDistributorId;
    // private String oldSimAccountId;


    private Date createTime;

    private Date arrearsTime;
    //设备故障信息
    private String fault;
    //SIM卡详情
    private SimCardDTO simCard;

    private String supplierName;// 生产商
    private String materiel;
    private String changeTimes;//iccid变更次数

    private List<Integer> ids;//存储设备id,用来批量更新

}
