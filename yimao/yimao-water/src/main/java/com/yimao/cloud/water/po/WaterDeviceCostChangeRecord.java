package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.WaterDeviceCostChangeRecordDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：水机计费方式修改记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/23
 */
@Table(name = "water_device_cost_change_record")
@Getter
@Setter
public class WaterDeviceCostChangeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String sn;
    private Integer oldCostId;
    private String oldCostName;
    private Integer newCostId;
    private String newCostName;
    //修改计费方式时使用了的时长
    private Integer time;
    //修改计费方式时使用了的流量
    private Integer flow;
    //修改计费方式时的余额
    private BigDecimal money;
    private String creator;
    private Date createTime;

    public WaterDeviceCostChangeRecord() {
    }

    /**
     * 用业务对象WaterDeviceCostChangeRecordDTO初始化数据库对象WaterDeviceCostChangeRecord。
     *
     * @param dto 业务对象
     */
    public WaterDeviceCostChangeRecord(WaterDeviceCostChangeRecordDTO dto) {
        this.id = dto.getId();
        this.sn = dto.getSn();
        this.oldCostId = dto.getOldCostId();
        this.oldCostName = dto.getOldCostName();
        this.newCostId = dto.getNewCostId();
        this.newCostName = dto.getNewCostName();
        this.time = dto.getTime();
        this.flow = dto.getFlow();
        this.money = dto.getMoney();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceCostChangeRecordDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceCostChangeRecordDTO dto) {
        dto.setId(this.id);
        dto.setSn(this.sn);
        dto.setOldCostId(this.oldCostId);
        dto.setOldCostName(this.oldCostName);
        dto.setNewCostId(this.newCostId);
        dto.setNewCostName(this.newCostName);
        dto.setTime(this.time);
        dto.setFlow(this.flow);
        dto.setMoney(this.money);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
    }
}
