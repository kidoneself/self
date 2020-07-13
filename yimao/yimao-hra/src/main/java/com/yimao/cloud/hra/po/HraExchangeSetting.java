package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.HraExchangeSettingDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Lizhqiang
 * @date 2019/4/22
 */
@Table(name = "hra_exchange_setting")
@Data
public class HraExchangeSetting {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer terminal;               //端  1-终端app；2-微信公众号；3-经销商APP；4-小程序
    private Integer channel;                //渠道
    private Integer limitType;              //限制类型: 1-每天，2-每周，3-每月，4-季度，5-每年
    private Integer times;                  //次数  空为不限制


    public HraExchangeSetting() {
    }

    /**
     * 用业务对象HraExchangeSettingDTO初始化数据库对象HraExchangeSetting。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public HraExchangeSetting(HraExchangeSettingDTO dto) {
        this.id = dto.getId();
        this.terminal = dto.getTerminal();
        this.channel = dto.getChannel();
        this.limitType = dto.getLimitType();
        this.times = dto.getTimes();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HraExchangeSettingDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(HraExchangeSettingDTO dto) {
        dto.setId(this.id);
        dto.setTerminal(this.terminal);
        dto.setChannel(this.channel);
        dto.setLimitType(this.limitType);
        dto.setTimes(this.times);
    }
}
