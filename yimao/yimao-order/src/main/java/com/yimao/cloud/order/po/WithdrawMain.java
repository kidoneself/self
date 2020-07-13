package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.WithdrawMainDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 主提现单
 *
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@Table(name = "withdraw_main")
@Data
public class WithdrawMain {

    @Id
    private Long mainPartnerTradeNo; //主提现单
    private Integer userId;             //提现用户
    private BigDecimal amountFee;       //总金额
    private BigDecimal formalitiesFee;  //手续费
    private Date withdrawTime;          //提现时间
    private Integer withdrawType;       //提现方式
    private Date applyTime;             //审核时间
    private Integer terminal;           //提现入口  1-公众号  2-小程序  3-经销商APP 4-ios  默认公众号


    public WithdrawMain() {
    }

    /**
     * 用业务对象WithdrawMainDTO初始化数据库对象WithdrawMain。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public WithdrawMain(WithdrawMainDTO dto) {
        this.mainPartnerTradeNo = dto.getMainPartnerTradeNo();
        this.userId = dto.getUserId();
        this.amountFee = dto.getAmountFee();
        this.formalitiesFee = dto.getFormalitiesFee();
        this.withdrawTime = dto.getWithdrawTime();
        this.withdrawType = dto.getWithdrawType();
        this.applyTime = dto.getApplyTime();
        this.terminal = dto.getTerminal();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WithdrawMainDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(WithdrawMainDTO dto) {
        dto.setMainPartnerTradeNo(this.mainPartnerTradeNo);
        dto.setUserId(this.userId);
        dto.setAmountFee(this.amountFee);
        dto.setFormalitiesFee(this.formalitiesFee);
        dto.setWithdrawTime(this.withdrawTime);
        dto.setWithdrawType(this.withdrawType);
        dto.setApplyTime(this.applyTime);
        dto.setTerminal(this.terminal);
    }
}