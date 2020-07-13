package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.IntegralRuleDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/***
 * 功能描述:积分规则
 *
 * @auther: liu yi
 * @date: 2019/6/3 10:50
 */
@Table(name = "integral_rule")
@Getter
@Setter
public class IntegralRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //规则名称
    private String name;
    //活动开始时间
    private Date activityStartTime;
    //活动结束时间
    private Date activityEndTime;
    //是否清零 0-否，1-是
    private Integer isClear;
    //清零时间
    private Date clearTime;
    //网络限制1-wifi网络在线 2- 3G网络在线
    private Integer networkLimit;
    //描述
    private String description;
    //状态 0-否 1-是   1-未上架；2-已上架；3-已下架；4-已删除；
    private Integer status;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    public IntegralRule() {
    }

    /**
     * 用业务对象IntegralRuleDTO初始化数据库对象IntegralRule。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public IntegralRule(IntegralRuleDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.activityStartTime = dto.getActivityStartTime();
        this.activityEndTime = dto.getActivityEndTime();
        this.isClear = dto.getIsClear();
        this.clearTime = dto.getClearTime();
        this.networkLimit = dto.getNetworkLimit();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象IntegralRuleDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(IntegralRuleDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setActivityStartTime(this.activityStartTime);
        dto.setActivityEndTime(this.activityEndTime);
        dto.setIsClear(this.isClear);
        dto.setClearTime(this.clearTime);
        dto.setNetworkLimit(this.networkLimit);
        dto.setDescription(this.description);
        dto.setStatus(this.status);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}