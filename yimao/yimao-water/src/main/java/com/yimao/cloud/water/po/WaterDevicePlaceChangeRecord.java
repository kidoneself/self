package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.WaterDevicePlaceChangeRecordDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：水机摆放位置更换记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/23
 */
@Table(name = "water_device_place_change_record")
@Getter
@Setter
public class WaterDevicePlaceChangeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //设备sn码
    private String sn;
    //旧地址
    private String oldPlace;
    //新地址
    private String newPlace;
    //创建时间
    private Date createTime;
    //是否锁机，Y为锁机,N为未锁机
    private String lockState;
    //来源方式 1-水机设备自动检测 2-移机
    private Integer type;
    //新地址详情地址
    private String detailAddress;

    public WaterDevicePlaceChangeRecord() {
    }

    /**
     * 用业务对象WaterDevicePlaceChangeRecordDTO初始化数据库对象WaterDevicePlaceChangeRecord。
     *
     * @param dto 业务对象
     */
    public WaterDevicePlaceChangeRecord(WaterDevicePlaceChangeRecordDTO dto) {
        this.id = dto.getId();
        this.sn = dto.getSn();
        this.oldPlace = dto.getOldPlace();
        this.newPlace = dto.getNewPlace();
        this.createTime = dto.getCreateTime();
        this.lockState = dto.getLockState();
        this.detailAddress = dto.getDetailAddress();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDevicePlaceChangeRecordDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(WaterDevicePlaceChangeRecordDTO dto) {
        dto.setId(this.id);
        dto.setSn(this.sn);
        dto.setOldPlace(this.oldPlace);
        dto.setNewPlace(this.newPlace);
        dto.setCreateTime(this.createTime);
        dto.setLockState(this.lockState);
        dto.setDetailAddress(this.detailAddress);
    }
}
