package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：水机设备
 *
 * @Author Zhang Bo
 * @Date 2019/2/23 15:19
 * @Version 1.0
 */
@Table(name = "water_device")
@Getter
@Setter
public class WaterDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    // sim卡运营商
    private Integer simAccountId;
    //SIM卡运营商
    private String simCompany;
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
    //计费套餐Id
    private Integer costId;
    //首年计费方式：1-流量计费；2-包年计费；
    private Integer costType;
    //计费套餐
    private String costName;
    //当前设备的计费方式（续费之后不能再使用costType）：1-流量计费；2-包年计费；
    private Integer currentCostType;
    //当前设备的计费方式（续费之后不能再使用costName）：1-流量计费；2-包年计费；
    private String currentCostName;
    //是否变更了计费方式
    private Boolean costChanged;
    //修改后的计费套餐Id
    private Integer newCostId;
    //设备变更套餐时间
    private Date lastCostChangeTime;
    //上一次累计使用时长
    private Integer lastTotalTime;
    //上一次累计使用流量
    private Integer lastTotalFlow;
    //当前使用总时长（单位：天）
    private Integer currentTotalTime;
    //当前使用总流量（单位：升）
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
    //续了几次费了
    private Integer renewTimes;
    //设备续费状态：-1-无需续费(首年安装未达到阀值)；1-未续费(到了或者超过阀值未续费，仍有余额)；2-待续费(余额为0 但在10天期限内未续费的)；3-已续费(续费了能正常使用的设备)；
    private Integer renewStatus;
    //设备续费状态中文描述
    private String renewStatusText;
    //最后一次续费时间
    private Date lastRenewTime;
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
    //设备欠费时间,续费之后.此字段需要变更为空
    // private Date deviceArrearsTime;
    //设备网络连接类型：1-WIFI；3-3G；
    private Integer connectionType;
    //是否锁机，Y为锁机,N为未锁机，NONE为后台设置不锁机
    private String lockState;
    //水机TDS值关联ID
    private Integer tdsId;

    //经度
    private String longitude;
    //纬度
    private String latitude;
    //创建时间
    private Date createTime;

    /**
     * 设备欠费时间,续费之后.此字段需要变更为空
     */
    private Date arrearsTime;

    /**
     * 对于时间计费的设备，如果计费开始时间异常，使用该值进行计费计算
     */
    private Date specialCostStartTime;

    //mongo数据库中的额主键
    private String oldId;
    //mongo数据库中的额主键
    private String oldCostId;

    private String oldCustomerId;
    private String oldDistributorId;

    //数据迁移用到
    private String oldTdsId;
    private String oldUserId;
    private String oldSimAccountId;

    public void convert(WaterDeviceVO vo) {
        vo.setId(this.id);
        vo.setSn(this.sn);
        vo.setIccid(this.iccid);
        vo.setSimActivatingTime(this.simActivatingTime);
        vo.setSimAccountId(this.simAccountId);
        vo.setSimCompany(this.simCompany);
        vo.setProvince(this.province);
        vo.setCity(this.city);
        vo.setRegion(this.region);
        vo.setAddress(this.address);
        vo.setPlace(this.place);
        vo.setMoney(this.money);
        vo.setCurrentTotalTime(this.currentTotalTime);
        vo.setCurrentTotalFlow(this.currentTotalFlow);
        vo.setLastOnlineTime(this.lastOnlineTime);
        vo.setVersion(this.version);
        vo.setDeviceType(this.deviceType);
        vo.setDeviceScope(this.deviceScope);
        vo.setDeviceModel(this.deviceModel);
        vo.setCreateTime(this.createTime);
    }

    public WaterDevice() {
    }

    /**
     * 用业务对象WaterDeviceDTO初始化数据库对象WaterDevice。
     *
     * @param dto 业务对象
     */
    public WaterDevice(WaterDeviceDTO dto) {
        this.id = dto.getId();
        this.sn = dto.getSn();
        this.snEntryTime = dto.getSnEntryTime();
        this.workOrderId = dto.getWorkOrderId();
        this.iccid = dto.getIccid();
        this.deviceUserId = dto.getDeviceUserId();
        this.deviceUserName = dto.getDeviceUserName();
        this.deviceUserPhone = dto.getDeviceUserPhone();
        this.engineerId = dto.getEngineerId();
        this.engineerName = dto.getEngineerName();
        this.engineerPhone = dto.getEngineerPhone();
        this.distributorId = dto.getDistributorId();
        this.distributorName = dto.getDistributorName();
        this.distributorPhone = dto.getDistributorPhone();
        this.distributorAccount = dto.getDistributorAccount();
        this.simActivating = dto.getSimActivating();
        this.simActivatingTime = dto.getSimActivatingTime();
        this.simAccountId = dto.getSimAccountId();
        this.simCompany = dto.getSimCompany();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.address = dto.getAddress();
        this.place = dto.getPlace();
        this.costId = dto.getCostId();
        this.costType = dto.getCostType();
        this.costName = dto.getCostName();
        this.currentCostType = dto.getCurrentCostType();
        this.currentCostName = dto.getCurrentCostName();
        this.costChanged = dto.getCostChanged();
        this.newCostId = dto.getNewCostId();
        this.lastCostChangeTime = dto.getLastCostChangeTime();
        this.lastTotalTime = dto.getLastTotalTime();
        this.lastTotalFlow = dto.getLastTotalFlow();
        this.currentTotalTime = dto.getCurrentTotalTime();
        this.currentTotalFlow = dto.getCurrentTotalFlow();
        this.useTime = dto.getUseTime();
        this.useFlow = dto.getUseFlow();
        this.initMoney = dto.getInitMoney();
        this.money = dto.getMoney();
        this.days = dto.getDays();
        this.renewTimes = dto.getRenewTimes();
        this.renewStatus = dto.getRenewStatus();
        this.renewStatusText = dto.getRenewStatusText();
        this.lastRenewTime = dto.getLastRenewTime();
        this.lastPpChangeTime = dto.getLastPpChangeTime();
        this.lastUdfChangeTime = dto.getLastUdfChangeTime();
        this.lastCtoChangeTime = dto.getLastCtoChangeTime();
        this.lastT33ChangeTime = dto.getLastT33ChangeTime();
        this.logisticsCode = dto.getLogisticsCode();
        this.online = dto.getOnline();
        this.lastOnlineTime = dto.getLastOnlineTime();
        this.bind = dto.getBind();
        this.unbundling = dto.getUnbundling();
        this.version = dto.getVersion();
        this.deviceType = dto.getDeviceType();
        this.deviceScope = dto.getDeviceScope();
        this.deviceModel = dto.getDeviceModel();
        this.connectionType = dto.getConnectionType();
        this.lockState = dto.getLockState();
        this.tdsId = dto.getTdsId();
        this.longitude = dto.getLongitude();
        this.latitude = dto.getLatitude();
        this.createTime = dto.getCreateTime();
        this.arrearsTime = dto.getArrearsTime();
        this.oldId = dto.getOldId();
        this.oldCostId = dto.getOldCostId();
        // this.oldTdsId = dto.getOldTdsId();
        // this.oldUserId = dto.getOldUserId();
        this.oldCustomerId = dto.getOldCustomerId();
        this.oldDistributorId = dto.getOldDistributorId();
        // this.oldSimAccountId = dto.getOldSimAccountId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceDTO dto) {
        dto.setId(this.id);
        dto.setSn(this.sn);
        dto.setSnEntryTime(this.snEntryTime);
        dto.setWorkOrderId(this.workOrderId);
        dto.setIccid(this.iccid);
        dto.setDeviceUserId(this.deviceUserId);
        dto.setDeviceUserName(this.deviceUserName);
        dto.setDeviceUserPhone(this.deviceUserPhone);
        dto.setEngineerId(this.engineerId);
        dto.setEngineerName(this.engineerName);
        dto.setEngineerPhone(this.engineerPhone);
        dto.setDistributorId(this.distributorId);
        dto.setDistributorName(this.distributorName);
        dto.setDistributorPhone(this.distributorPhone);
        dto.setDistributorAccount(this.distributorAccount);
        dto.setSimActivating(this.simActivating);
        dto.setSimActivatingTime(this.simActivatingTime);
        dto.setSimAccountId(this.simAccountId);
        dto.setSimCompany(this.simCompany);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAddress(this.address);
        dto.setPlace(this.place);
        dto.setCostId(this.costId);
        dto.setCostType(this.costType);
        dto.setCostName(this.costName);
        dto.setCurrentCostType(this.currentCostType);
        dto.setCurrentCostName(this.currentCostName);
        dto.setCostChanged(this.costChanged);
        dto.setNewCostId(this.newCostId);
        dto.setLastCostChangeTime(this.lastCostChangeTime);
        dto.setLastTotalTime(this.lastTotalTime);
        dto.setLastTotalFlow(this.lastTotalFlow);
        dto.setCurrentTotalTime(this.currentTotalTime);
        dto.setCurrentTotalFlow(this.currentTotalFlow);
        dto.setUseTime(this.useTime);
        dto.setUseFlow(this.useFlow);
        dto.setInitMoney(this.initMoney);
        dto.setMoney(this.money);
        dto.setDays(this.days);
        dto.setRenewTimes(this.renewTimes);
        dto.setRenewStatus(this.renewStatus);
        dto.setRenewStatusText(this.renewStatusText);
        dto.setLastRenewTime(this.lastRenewTime);
        dto.setLastPpChangeTime(this.lastPpChangeTime);
        dto.setLastUdfChangeTime(this.lastUdfChangeTime);
        dto.setLastCtoChangeTime(this.lastCtoChangeTime);
        dto.setLastT33ChangeTime(this.lastT33ChangeTime);
        dto.setLogisticsCode(this.logisticsCode);
        dto.setOnline(this.online);
        dto.setLastOnlineTime(this.lastOnlineTime);
        dto.setBind(this.bind);
        dto.setUnbundling(this.unbundling);
        dto.setVersion(this.version);
        dto.setDeviceType(this.deviceType);
        dto.setDeviceScope(this.deviceScope);
        dto.setDeviceModel(this.deviceModel);
        dto.setConnectionType(this.connectionType);
        dto.setLockState(this.lockState);
        dto.setTdsId(this.tdsId);
        dto.setLongitude(this.longitude);
        dto.setLatitude(this.latitude);
        dto.setCreateTime(this.createTime);
        dto.setArrearsTime(this.arrearsTime);
        dto.setOldId(this.oldId);
        dto.setOldCostId(this.oldCostId);
        // dto.setOldTdsId(this.oldTdsId);
        // dto.setOldUserId(this.oldUserId);
        dto.setOldCustomerId(this.oldCustomerId);
        dto.setOldDistributorId(this.oldDistributorId);
        // dto.setOldSimAccountId(this.oldSimAccountId);
    }
}
