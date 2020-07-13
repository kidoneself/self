package com.yimao.cloud.hra.po;

import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/1/22.
 */
@Table(name = "hra_discount_card_record")
@Data
public class DiscountCardRecord{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;//用户ID
    private String giveName;//经销商账号、手机号
    private Integer giveType;//1-经销商配额卡，2-经销商业绩卡，3-用户业绩卡，5-手动发放卡，7-分销用户配额卡 8-兑换码兑换
    private Integer giveCount;//本次发放卡数量
    private Integer totalCount;//历史数量（只针对业绩卡）
    private Boolean received;//是否发放：0-否，1-是
    private Integer ticketStyle;//优惠卡样式：1-金色，2-紫色，3-蓝色
    private String watermark;//优惠卡联名文字水印
    private String remark;//备注
    private Date giveTime;//创建时间
    private Date receivedTime;//领取时间
    private String image;
    private String imageUsed;

}
