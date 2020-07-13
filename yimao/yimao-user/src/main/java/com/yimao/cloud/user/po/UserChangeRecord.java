package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.UserChangeRecordDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户变化记录表<br/>
 * 关注/取关、身份变化（绑定经销商，绑定手机号，升级为分销用户等）
 *
 * @author Zhang Bo
 * @date 2018/3/23.
 */
@Table(name = "user_change_record")
@Data
public class UserChangeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 原用户e家号
     */
    private Integer origUserId;

    /**
     * 原经销商Id
     */
    private Integer origDistributorId;

    /**
     * 原经销商账号
     */
    private String origAccount;

    /**
     * 原手机号
     */
    private String origPhone;

    /**
     * 原用户类型
     */
    private Integer origUserType;

    /**
     * 原经销商类型
     */
    private Integer origDistributorType;

    /**
     * 更改后Id
     */
    private Integer destUserId;

    /**
     * 更改后经销商Id
     */
    private Integer destDistributorId;

    /**
     * 原经销商账号
     */
    private String destAccount;

    /**
     * 更改后手机号
     */
    private String destPhone;

    /**
     * 更改后用户类型
     */
    private Integer destUserType;

    /**
     * 原经销商类型
     */
    private Integer destDistributorType;
    
    /**
     * 经销商代理商编辑前数据json
     */
    private String origDistributorData;
    
    /**
     * 经销商代理商编辑后数据json
     */
    private String destDistributorData;
    
    /**
     * 用户编辑前数据json
     */
    private String origUserData;
    
    /**
     * 用户编辑后数据json
     */
    private String destUserData;

    /**
     * 变化类型（事件）1-创建账号 2-升级分享 3-升级分销用户 4-绑定手机 5-绑定经销商 6-升级 7-首次关注公众号 8-首次登陆小程序 9-取消关注公众号 10-转让11-编辑
     */
    private Integer type;

    /**
     * 发生时间
     */
    private Date time;

    /**
     * 变更端 1-H5页面；2-经销商app; 3:翼猫业务系统
     */
    private Integer terminal;

    /**
     * 金额（升级或者续费）
     */
    private BigDecimal amount;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 来源 1-自主关注公众号 2-优惠卡赠送 2-绑定手机号
     */
    private Integer source;

    /**
     * 分享者ID
     */
    private Integer shareId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String creator;


    public UserChangeRecord() {
    }

    /**
     * 用业务对象UserChangeRecordDTO初始化数据库对象UserChangeRecord。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public UserChangeRecord(UserChangeRecordDTO dto) {
        this.id = dto.getId();
        this.origUserId = dto.getOrigUserId();
        this.origDistributorId = dto.getOrigDistributorId();
        this.origAccount = dto.getOrigAccount();
        this.origPhone = dto.getOrigPhone();
        this.origUserType = dto.getOrigUserType();
        this.origDistributorType = dto.getOrigDistributorType();
        this.destUserId = dto.getDestUserId();
        this.destDistributorId = dto.getDestDistributorId();
        this.destAccount = dto.getDestAccount();
        this.destPhone = dto.getDestPhone();
        this.destUserType = dto.getDestUserType();
        this.destDistributorType = dto.getDestDistributorType();
        this.type = dto.getType();
        this.time = dto.getTime();
        this.terminal = dto.getTerminal();
        this.amount = dto.getAmount();
        this.orderId = dto.getOrderId();
        this.source = dto.getSource();
        this.shareId = dto.getShareId();
        this.remark = dto.getRemark();
        this.creator = dto.getCreator();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象UserChangeRecordDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(UserChangeRecordDTO dto) {
        dto.setId(this.id);
        dto.setOrigUserId(this.origUserId);
        dto.setOrigDistributorId(this.origDistributorId);
        dto.setOrigAccount(this.origAccount);
        dto.setOrigPhone(this.origPhone);
        dto.setOrigUserType(this.origUserType);
        dto.setOrigDistributorType(this.origDistributorType);
        dto.setDestUserId(this.destUserId);
        dto.setDestDistributorId(this.destDistributorId);
        dto.setDestAccount(this.destAccount);
        dto.setDestPhone(this.destPhone);
        dto.setDestUserType(this.destUserType);
        dto.setDestDistributorType(this.destDistributorType);
        dto.setType(this.type);
        dto.setTime(this.time);
        dto.setTerminal(this.terminal);
        dto.setAmount(this.amount);
        dto.setOrderId(this.orderId);
        dto.setSource(this.source);
        dto.setShareId(this.shareId);
        dto.setRemark(this.remark);
        dto.setCreator(this.creator);
        dto.setOrigDistributorData(this.origDistributorData);
        dto.setDestDistributorData(this.destDistributorData);
        dto.setOrigUserData(this.origUserData);
        dto.setDestUserData(this.destUserData);
    }
    
}
