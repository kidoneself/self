package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.MoveWaterDeviceOrderHangUpLogDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 移机工单挂单记录
 *
 * @author Liu Long Jie
 * @date 2020-6-29 10:41:11
 */
@Data
@Table(name = "move_water_device_order_hang_up_log")
public class MoveWaterDeviceOrderHangUpLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //移机工单id
    private String moveWaterDeviceOrderId;
    //操作安装工id
    private Integer engineerId;
    //操作安装工姓名
    private String engineerName;
    //挂单类型 1-拆机挂单 2-移入挂单
    private Integer type;
    //改约原因
    private String cause;
    //原预约时间（开始）
    private Date origStartTime;
    //原预约时间（结束）
    private Date origEndTime;
    //更改后预约时间（开始）
    private Date destStartTime;
    //更改后预约时间（结束）
    private Date destEndTime;
    //操作时间
    private Date operationTime;


    public MoveWaterDeviceOrderHangUpLog() {
    }

    /**
     * 用业务对象MoveWaterDeviceOrderHangUpLogDTO初始化数据库对象MoveWaterDeviceOrderHangUpLog。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MoveWaterDeviceOrderHangUpLog(MoveWaterDeviceOrderHangUpLogDTO dto) {
        this.id = dto.getId();
        this.moveWaterDeviceOrderId = dto.getMoveWaterDeviceOrderId();
        this.engineerId = dto.getEngineerId();
        this.engineerName = dto.getEngineerName();
        this.type = dto.getType();
        this.origStartTime = dto.getOrigStartTime();
        this.origEndTime = dto.getOrigEndTime();
        this.destStartTime = dto.getDestStartTime();
        this.destEndTime = dto.getDestEndTime();
        this.operationTime = dto.getOperationTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象MoveWaterDeviceOrderHangUpLogDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MoveWaterDeviceOrderHangUpLogDTO dto) {
        dto.setId(this.id);
        dto.setMoveWaterDeviceOrderId(this.moveWaterDeviceOrderId);
        dto.setEngineerId(this.engineerId);
        dto.setEngineerName(this.engineerName);
        dto.setType(this.type);
        dto.setOrigStartTime(this.origStartTime);
        dto.setOrigEndTime(this.origEndTime);
        dto.setDestStartTime(this.destStartTime);
        dto.setDestEndTime(this.destEndTime);
        dto.setOperationTime(this.operationTime);
    }
}
