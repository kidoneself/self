package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.WithdrawSubDTO;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 子提现
 *
 * @author liuhao@yimaokeji.com
 * @date 2018-12-28
 */
@Data
@Table(name = "withdraw_sub")
public class WithdrawSub {

    @Id
    private Long id;                    //子提现订单
    private Long mainPartnerTradeNo;    //主提现单号
    private Integer productCategory;    //产品类型
    private Integer userId;             //用户id
    private String openid;              //提现人的openid
    private String userName;            //用户姓名
    private Integer userType;           //用户身份
    private String mobile;               //手机号
    private BigDecimal withdrawFee;     //提现金额
    private BigDecimal withdrawRealFee; //实际提现金额
    private Date withdrawTime;          //提现时间
    private Integer withdrawType;       //提现方式：1-微信提现；2-支付宝提现；
    private Integer terminal;           //提现端  1-公众号  2-小程序  3-经销商APP 4-ios  默认公众号
    private Integer withdrawFlag;       //提现成功标志 1-成功 2-失败
    private String origCash;            //提现来源
    private String destCash;            //提现去向
    private BigDecimal formalitiesFee;  //手续费
    private String paymentNo;           //提现流水号
    private Date paymentTime;           //提现到账时间
    private Date applyTime;             //申请提现时间
    private Integer status;             //提现审核状态: 1-审核通过 2-审核不通过 3-待审核
    private Date auditTime;             //审核时间(财务)
    private String auditReason;         //原因
    private String remark;              //备注
    private Date updateTime;          //更新时间
    private String updater;             //更新人
    private Integer productCompanyId;    //产品公司Id
    private String productCompanyName;  //产品公司名称


    public WithdrawSub() {
    }

    /**
     * 用业务对象WithdrawSubDTO初始化数据库对象WithdrawSub。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public WithdrawSub(WithdrawSubDTO dto) {
        this.id = Long.valueOf(dto.getId());
        this.mainPartnerTradeNo = Long.valueOf(dto.getMainPartnerTradeNo());
        this.productCategory = dto.getProductCategory();
        this.userId = dto.getUserId();
        this.userType = dto.getUserType();
        this.withdrawFee = dto.getWithdrawFee();
        this.withdrawRealFee = dto.getWithdrawRealFee();
        this.withdrawTime = dto.getWithdrawTime();
        this.withdrawType = dto.getWithdrawType();
        this.terminal = dto.getTerminal();
        this.withdrawFlag = dto.getWithdrawFlag();
        this.origCash = dto.getOrigCash();
        this.destCash = dto.getDestCash();
        this.formalitiesFee = dto.getFormalitiesFee();
        this.paymentNo = dto.getPaymentNo();
        this.paymentTime = dto.getPaymentTime();
        this.applyTime = dto.getApplyTime();
        this.status = dto.getStatus();
        this.auditTime = dto.getAuditTime();
        this.auditReason = dto.getAuditReason();
        this.remark = dto.getRemark();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
        this.productCompanyId = dto.getProductCompanyId();
        this.productCompanyName = dto.getProductCompanyName();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WithdrawSubDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(WithdrawSubDTO dto) {
        dto.setId(String.valueOf(this.id));
        dto.setMainPartnerTradeNo(String.valueOf(this.mainPartnerTradeNo));
        dto.setProductCategory(this.productCategory);
        dto.setUserId(this.userId);
        dto.setUserType(this.userType);
        dto.setWithdrawFee(this.withdrawFee);
        dto.setWithdrawRealFee(this.withdrawRealFee);
        dto.setWithdrawTime(this.withdrawTime);
        dto.setWithdrawType(this.withdrawType);
        dto.setTerminal(this.terminal);
        dto.setWithdrawFlag(this.withdrawFlag);
        dto.setOrigCash(this.origCash);
        dto.setDestCash(this.destCash);
        dto.setFormalitiesFee(this.formalitiesFee);
        dto.setPaymentNo(this.paymentNo);
        dto.setPaymentTime(this.paymentTime);
        dto.setApplyTime(this.applyTime);
        dto.setStatus(this.status);
        dto.setAuditTime(this.auditTime);
        dto.setAuditReason(this.auditReason);
        dto.setRemark(this.remark);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
        dto.setProductCompanyId(this.productCompanyId);
        dto.setProductCompanyName(this.productCompanyName);
    }
}
