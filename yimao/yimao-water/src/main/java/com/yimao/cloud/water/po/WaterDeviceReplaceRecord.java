package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.WaterDeviceReplaceRecordDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：水机设备更换记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/23
 */
@Table(name = "water_device_replace_record")
@Getter
@Setter
public class WaterDeviceReplaceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String oldSn;
    private String newSn;
    private String oldIccid;
    private String newIccid;
    private String oldBatchCode;
    private String newBatchCode;
    //更换前设备的时间
    private Integer time;
    //更换前设备的流量
    private Integer flow;
    private Integer costId;
    private String costName;
    //更换前设备的金额
    private BigDecimal money;
    private String deviceModel;
    private String province;
    private String city;
    private String region;

    private String creator;
    private Date createTime;

    public WaterDeviceReplaceRecord() {
    }

    /**
     * 用业务对象WaterDeviceReplaceRecordDTO初始化数据库对象WaterDeviceReplaceRecord。
     *
     * @param dto 业务对象
     */
    public WaterDeviceReplaceRecord(WaterDeviceReplaceRecordDTO dto) {
        this.id = dto.getId();
        this.oldSn = dto.getOldSn();
        this.newSn = dto.getNewSn();
        this.oldIccid = dto.getOldIccid();
        this.newIccid = dto.getNewIccid();
        this.oldBatchCode = dto.getOldBatchCode();
        this.newBatchCode = dto.getNewBatchCode();
        this.time = dto.getTime();
        this.flow = dto.getFlow();
        this.costId = dto.getCostId();
        this.costName = dto.getCostName();
        this.money = dto.getMoney();
        this.deviceModel = dto.getDeviceModel();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceReplaceRecordDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceReplaceRecordDTO dto) {
        dto.setId(this.id);
        dto.setOldSn(this.oldSn);
        dto.setNewSn(this.newSn);
        dto.setOldIccid(this.oldIccid);
        dto.setNewIccid(this.newIccid);
        dto.setOldBatchCode(this.oldBatchCode);
        dto.setNewBatchCode(this.newBatchCode);
        dto.setTime(this.time);
        dto.setFlow(this.flow);
        dto.setCostId(this.costId);
        dto.setCostName(this.costName);
        dto.setMoney(this.money);
        dto.setDeviceModel(this.deviceModel);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
    }
}
