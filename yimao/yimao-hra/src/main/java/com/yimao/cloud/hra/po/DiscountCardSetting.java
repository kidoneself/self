package com.yimao.cloud.hra.po;

import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/1/24.
 */
@Table(name = "hra_discount_card_setting")
@Data
public class DiscountCardSetting{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userType;
    private Integer giveCount;
    private String company;
    private Integer ticketStyle;
    private String watermark;
    private String image;
    private String imageUsed;

    protected Date createTime;
    protected String creator;
    protected Date updateTime;
    protected String updater;

}
