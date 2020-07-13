package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.FlowRuleDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/***
 * 功能描述:系统流量规则
 *
 * @auther: liu yi
 * @date: 2019/6/3 10:50
 */
@Table(name = "flow_rule")
@Getter
@Setter
public class FlowRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //禁用第三方3G:0-否 ，1-是 默认0
    private Integer disableThird;
    //禁用自有3G:0-否 ，1-是 默认0
    private Integer disableOwn;
    //每月3g节点更新时间
    private String updateNode;
    //最小流量
    private Integer minFlow;
    //最大流量
    private Integer maxFlow;
    //流量上传 0-否 1-是 默认1
    private Integer allTrafficUpload;
    //3G流量上传 0-否 1-是 默认1
    private Integer trafficUpload;
    //积分上传 0-否 1-是 默认1
    private Integer allIntegralUpload;
   //第三方效果是否上传：0-否 1-是
    private Integer thirdEffectUpload;
    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    public FlowRule() {
    }

    /**
     * 用业务对象FlowRuleDTO初始化数据库对象FlowRule。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public FlowRule(FlowRuleDTO dto) {
        this.id = dto.getId();
        this.disableThird = dto.getDisableThird();
        this.disableOwn = dto.getDisableOwn();
        this.updateNode = dto.getUpdateNode();
        this.minFlow = dto.getMinFlow();
        this.maxFlow = dto.getMaxFlow();
        this.allTrafficUpload = dto.getAllTrafficUpload();
        this.allIntegralUpload = dto.getAllIntegralUpload();
        this.trafficUpload = dto.getTrafficUpload();
        this.thirdEffectUpload = dto.getThirdEffectUpload();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象FlowRuleDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(FlowRuleDTO dto) {
        dto.setId(this.id);
        dto.setDisableThird(this.disableThird);
        dto.setDisableOwn(this.disableOwn);
        dto.setUpdateNode(this.updateNode);
        dto.setMinFlow(this.minFlow);
        dto.setMaxFlow(this.maxFlow);
        dto.setAllTrafficUpload(this.allTrafficUpload);
        dto.setAllIntegralUpload(this.allIntegralUpload);
        dto.setTrafficUpload(this.trafficUpload);
        dto.setThirdEffectUpload(this.thirdEffectUpload);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}