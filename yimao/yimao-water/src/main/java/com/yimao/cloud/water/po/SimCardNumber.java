package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.SimCardNumberDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：SIM号码段配置
 *
 * @Author Zhang Bo
 * @Date 2019/4/25
 */
@Table(name = "sim_card_number")
@Getter
@Setter
public class SimCardNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //所属运营商
    private Integer simCardAccountId;
    //ICCID号码段
    private String cardNumber;
    //最小号码
    private String minNumber;
    //最大号码
    private String maxNumber;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    public SimCardNumber() {
    }

    /**
     * 用业务对象SimCardNumberDTO初始化数据库对象SimCardNumber。
     *
     * @param dto 业务对象
     */
    public SimCardNumber(SimCardNumberDTO dto) {
        this.id = dto.getId();
        this.simCardAccountId = dto.getSimCardAccountId();
        this.cardNumber = dto.getCardNumber();
        this.minNumber = dto.getMinNumber();
        this.maxNumber = dto.getMaxNumber();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象SimCardNumberDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(SimCardNumberDTO dto) {
        dto.setId(this.id);
        dto.setSimCardAccountId(this.simCardAccountId);
        dto.setCardNumber(this.cardNumber);
        dto.setMinNumber(this.minNumber);
        dto.setMaxNumber(this.maxNumber);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
