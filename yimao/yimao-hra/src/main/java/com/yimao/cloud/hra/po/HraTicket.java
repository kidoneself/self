package com.yimao.cloud.hra.po;


import com.yimao.cloud.framework.aop.annotation.LogFieldName;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 评估劵
 *
 * @author Zhang Bo
 * @date 2017/11/29.
 */

@Table(name = "hra_ticket")
@Getter
@Setter
public class HraTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String ticketNo;  //卡号
    private BigDecimal ticketPrice;     //每张评估券的价格
    private Integer ticketStatus;//状态：1-未使用(已支付)，2-已使用(已体检)，3-已禁用(Y-禁用 M-未支付)，4-已过期
    private Integer handselStatus;//赠送状态：1-赠送中，2-已被领取
    private String ticketType;    //评估券类型：Y/F/M
    private String deviceId;    //若已用，检测的设备编号
    @LogFieldName("服务站ID")
    private Integer stationId;
    private String stationName;
    private String stationProvince;
    private String stationCity;
    private String stationRegion;
    private Integer reserveStationId;
    private String creator;
    private Date createTime;
    private Date updateTime;
    private Date useTime;         //使用时间
    private Date validBeginTime; //有效期开始时间
    @LogFieldName("有效期")
    private Date validEndTime;    //有效期结束时间
    private String cardId;            //评估卡ID
    private Integer customerId;       //顾客表关联字段，评估卷关联到预约用户或评估用户
    private Integer reserveFrom;       //预约操作来源：1终端用户app，2微信公众号，3评估系统，4经销商APP 5后台管理系统
    private Date reserveTime;      //预约操作的时间
    private Date reserveBeginTime;    //预约评估的开始日期时间
    private Date reserveEndTime;  //预约评估的结束日期时间
    private String remark;  //备注
    private Integer userId;
    private Boolean deleteStatus;   //是否删除 1：已删除  0：未删除
    private Boolean selfStation;  //是否只有此服务站可用
    @LogFieldName("剩余可用次数")
    private Integer useCount;   //可用次数
    @LogFieldName("总可用次数")
    private Integer total;   //总次数
    private Date handselTime; // 赠送时间
    private Date receiveTime; // 接收时间
    private Date bookTime; // 预约时间
    private Integer ticketStyle;    //体检卡样式：1-金色，2-紫色
    private String ticketContent;   //优惠卡显示的内容
    private String image;//背景图
    private String imageUsed;//背景图（已使用）
    private Long handselFlag;       //赠送标志,用来保存前端传递过来多的值(用于区分是属于哪一次赠送)

    public HraTicket() {
    }

    /**
     * 用业务对象HraTicketDTO初始化数据库对象HraTicket。
     *
     * @param dto 业务对象
     */
    public HraTicket(HraTicketDTO dto) {
        this.id = dto.getId();
        this.ticketNo = dto.getTicketNo();
        this.ticketPrice = dto.getTicketPrice();
        this.ticketStatus = dto.getTicketStatus();
        this.handselStatus = dto.getHandselStatus();
        this.ticketType = dto.getTicketType();
        this.deviceId = dto.getDeviceId();
        this.stationId = dto.getStationId();
        this.stationName = dto.getStationName();
        this.stationProvince = dto.getStationProvince();
        this.stationCity = dto.getStationCity();
        this.stationRegion = dto.getStationRegion();
        this.reserveStationId = dto.getReserveStationId();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.useTime = dto.getUseTime();
        this.validBeginTime = dto.getValidBeginTime();
        this.validEndTime = dto.getValidEndTime();
        this.cardId = dto.getCardId();
        this.customerId = dto.getCustomerId();
        this.reserveFrom = dto.getReserveFrom();
        this.reserveTime = dto.getReserveTime();
        this.reserveBeginTime = dto.getReserveBeginTime();
        this.reserveEndTime = dto.getReserveEndTime();
        this.remark = dto.getRemark();
        this.userId = dto.getUserId();
        this.deleteStatus = dto.getDeleteStatus();
        this.selfStation = dto.getSelfStation();
        this.useCount = dto.getUseCount();
        this.total = dto.getTotal();
        this.handselTime = dto.getHandselTime();
        this.receiveTime = dto.getReceiveTime();
        this.bookTime = dto.getBookTime();
        this.ticketStyle = dto.getTicketStyle();
        this.ticketContent = dto.getTicketContent();
        this.image = dto.getImage();
        this.imageUsed = dto.getImageUsed();
        this.handselFlag = dto.getHandselFlag();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HraTicketDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(HraTicketDTO dto) {
        dto.setId(this.id);
        dto.setTicketNo(this.ticketNo);
        dto.setTicketPrice(this.ticketPrice);
        dto.setTicketStatus(this.ticketStatus);
        dto.setHandselStatus(this.handselStatus);
        dto.setTicketType(this.ticketType);
        dto.setDeviceId(this.deviceId);
        dto.setStationId(this.stationId);
        dto.setStationName(this.stationName);
        dto.setStationProvince(this.stationProvince);
        dto.setStationCity(this.stationCity);
        dto.setStationRegion(this.stationRegion);
        dto.setReserveStationId(this.reserveStationId);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setUseTime(this.useTime);
        dto.setValidBeginTime(this.validBeginTime);
        dto.setValidEndTime(this.validEndTime);
        dto.setCardId(this.cardId);
        dto.setCustomerId(this.customerId);
        dto.setReserveFrom(this.reserveFrom);
        dto.setReserveTime(this.reserveTime);
        dto.setReserveBeginTime(this.reserveBeginTime);
        dto.setReserveEndTime(this.reserveEndTime);
        dto.setRemark(this.remark);
        dto.setUserId(this.userId);
        dto.setDeleteStatus(this.deleteStatus);
        dto.setSelfStation(this.selfStation);
        dto.setUseCount(this.useCount);
        dto.setTotal(this.total);
        dto.setHandselTime(this.handselTime);
        dto.setReceiveTime(this.receiveTime);
        dto.setBookTime(this.bookTime);
        dto.setTicketStyle(this.ticketStyle);
        dto.setTicketContent(this.ticketContent);
        dto.setImage(this.image);
        dto.setImageUsed(this.imageUsed);
        dto.setHandselFlag(this.handselFlag);
    }
}
