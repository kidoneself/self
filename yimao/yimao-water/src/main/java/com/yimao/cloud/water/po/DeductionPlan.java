package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.DeductionPlanDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：水机设备扣费计划
 *
 * @Author Zhang Bo
 * @Date 2019/8/30
 */
@Table(name = "water_device_deduction_plan")
@Getter
@Setter
public class DeductionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //设备ID
    private Integer deviceId;
    //设备SN码
    private String snCode;
    //提醒阀值
    private Integer threshold;
    //套餐ID
    private Integer costId;
    //单位扣减金额
    private BigDecimal unitMoney;
    //开始第一天剩余金额
    private BigDecimal firstDayMoney;
    //开始第一天剩余流量
    private Integer firstDayFlow = -1;
    //计费方式：1-流量计费；2-时长计费；
    private Integer deductionsType;
    //扣除数量,天/升
    private Integer deductionsNum;
    //是否在更换扣费方式
    private Boolean costChanged;
    //当前扣费计划使用状态：1-待使用；2-使用中；3-已使用；
    private Integer status;
    //开始时间
    private Date startTime;
    //结束时间
    private Date endTime;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //排序字段，由小到大是扣费计划使用的顺序
    private Integer sorts;

    public DeductionPlan() {
    }

    /**
     * 用业务对象DeductionPlanDTO初始化数据库对象DeductionPlan。
     *
     * @param dto 业务对象
     */
    public DeductionPlan(DeductionPlanDTO dto) {
        this.id = dto.getId();
        this.deviceId = dto.getDeviceId();
        this.snCode = dto.getSnCode();
        this.threshold = dto.getThreshold();
        this.costId = dto.getCostId();
        this.unitMoney = dto.getUnitMoney();
        this.firstDayMoney = dto.getFirstDayMoney();
        this.firstDayFlow = dto.getFirstDayFlow();
        this.deductionsType = dto.getDeductionsType();
        this.deductionsNum = dto.getDeductionsNum();
        this.costChanged = dto.getCostChanged();
        this.status = dto.getStatus();
        this.startTime = dto.getStartTime();
        this.endTime = dto.getEndTime();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.sorts = dto.getSorts();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象DeductionPlanDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(DeductionPlanDTO dto) {
        dto.setId(this.id);
        dto.setDeviceId(this.deviceId);
        dto.setSnCode(this.snCode);
        dto.setThreshold(this.threshold);
        dto.setCostId(this.costId);
        dto.setUnitMoney(this.unitMoney);
        dto.setFirstDayMoney(this.firstDayMoney);
        dto.setFirstDayFlow(this.firstDayFlow);
        dto.setDeductionsType(this.deductionsType);
        dto.setDeductionsNum(this.deductionsNum);
        dto.setCostChanged(this.costChanged);
        dto.setStatus(this.status);
        dto.setStartTime(this.startTime);
        dto.setEndTime(this.endTime);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setSorts(this.sorts);
    }
}
