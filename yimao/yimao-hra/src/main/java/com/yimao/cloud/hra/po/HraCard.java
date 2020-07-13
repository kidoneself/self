package com.yimao.cloud.hra.po;

import com.yimao.cloud.framework.aop.annotation.LogFieldName;
import com.yimao.cloud.pojo.dto.hra.HraCardDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 评估卡
 *
 * @author Zhang Bo
 * @date 2017/11/29.
 */
@Table(name = "hra_card")
@Getter
@Setter
public class HraCard {

    @LogFieldName("体检卡ID")
    @Id
    private String id;
    @LogFieldName("体检卡类型")
    /*
     * 评估卡类型：Y/F/M
     */
    private String cardType;
    private Integer userId;
    private Long orderId;
    private Long mainOrderId;
    private Integer productId;
    /*
     * 订单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；10-管理后台
     */
    private Integer orderFrom;
    private String orderFromName;
    /*
     * 有效期
     */
    private Date validTime;
    /*
     * 评估卡的销售价格
     */
    private BigDecimal cardPrice;
    /*
     * 生成评估券的数量
     */
    private Integer ticketNum;
    private String remark;
    private Date createTime;
    private Date updateTime;
    private String creator;
    /*
     * 是否删除：0-未删除；1-已删除；
     */
    private Boolean deleteStatus;

    public HraCard() {
    }

    /**
     * 用业务对象HraCardDTO初始化数据库对象HraCard。
     *
     * @param dto 业务对象
     */
    public HraCard(HraCardDTO dto) {
        this.id = dto.getId();
        this.cardType = dto.getCardType();
        this.userId = dto.getUserId();
        this.orderId = dto.getOrderId();
        this.mainOrderId = dto.getMainOrderId();
        this.productId = dto.getProductId();
        this.orderFrom = dto.getOrderFrom();
        this.orderFromName = dto.getOrderFromName();
        this.validTime = dto.getValidTime();
        this.cardPrice = dto.getCardPrice();
        this.ticketNum = dto.getTicketNum();
        this.remark = dto.getRemark();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.creator = dto.getCreator();
        this.deleteStatus = dto.getDeleteStatus();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HraCardDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(HraCardDTO dto) {
        dto.setId(this.id);
        dto.setCardType(this.cardType);
        dto.setUserId(this.userId);
        dto.setOrderId(this.orderId);
        dto.setMainOrderId(this.mainOrderId);
        dto.setProductId(this.productId);
        dto.setOrderFrom(this.orderFrom);
        dto.setOrderFromName(this.orderFromName);
        dto.setValidTime(this.validTime);
        dto.setCardPrice(this.cardPrice);
        dto.setTicketNum(this.ticketNum);
        dto.setRemark(this.remark);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setCreator(this.creator);
        dto.setDeleteStatus(this.deleteStatus);
    }
}
