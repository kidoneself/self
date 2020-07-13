package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.ManualPadCostDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：手动修改水机配额
 *
 * @Author Zhang Bo
 * @Date 2019/5/7
 */
@Table(name = "manual_pad_cost")
@Getter
@Setter
public class ManualPadCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //SN码
    private String sn;
    //余额
    private BigDecimal balance;
    //已使用流量
    private Integer discharge;
    //是否开启：0-关闭；1-开启
    private Boolean open;
    // private Boolean check;
    //同步状态：0-未同步；1-同步完成；2-同步失败；
    private Integer syncStatus;
    //同步失败的原因
    private String syncFailReason;
    //同步到水机pad上的时间
    private Date syncTime;
    //创建时间
    private Date createTime;

    public ManualPadCost() {
    }

    /**
     * 用业务对象ManualPadCostDTO初始化数据库对象ManualPadCost。
     *
     * @param dto 业务对象
     */
    public ManualPadCost(ManualPadCostDTO dto) {
        this.id = dto.getId();
        this.sn = dto.getSn();
        this.balance = dto.getBalance();
        this.discharge = dto.getDischarge();
        this.open = dto.getOpen();
        // this.check = dto.getCheck();
        this.syncStatus = dto.getSyncStatus();
        this.syncFailReason = dto.getSyncFailReason();
        this.syncTime = dto.getSyncTime();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ManualPadCostDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(ManualPadCostDTO dto) {
        dto.setId(this.id);
        dto.setSn(this.sn);
        dto.setBalance(this.balance);
        dto.setDischarge(this.discharge);
        dto.setOpen(this.open);
        // dto.setCheck(this.check);
        dto.setSyncStatus(this.syncStatus);
        dto.setSyncFailReason(this.syncFailReason);
        dto.setSyncTime(this.syncTime);
        dto.setCreateTime(this.createTime);
    }
}