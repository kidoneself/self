package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.OrderDeliveryRecordDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 发货记录
 * @author: yu chunlei
 * @create: 2019-08-21 09:34:54
 **/
@Table(name = "order_delivery_record")
@Data
public class OrderDeliveryRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String logisticsNo;
    private String logisticsCompany;
    private Long orderId;
    private Long mainOrderId;
    private Integer productOneCategoryId;
    private String productOneCategoryName;
    private Integer productTwoCategoryId;
    private String productTwoCategoryName;
    private Integer productCategoryId;
    private String productCategoryName;
    private String productImg;
    private Integer num;
    private Integer boxNum;
    private Date deliveryTime;
    private Integer terminal;
    private Integer userId;
    private String addressName;
    private String addressPhone;
    private String addressProvince;
    private String addressCity;
    private String addressRegion;
    private String addressStreet;
    private Integer stationId;
    private String payType;
    private String stationName;
    private String sendingCompany;
    private String sender;
    private String senderProvince;
    private String senderCity;
    private String senderRegion;
    private String senderStreet;
    private String senderPhone;
    private String setShipper;
    private Date setShipTime;
    private String deliveryPeople;
    private Integer weigh;
    private Integer logisticsFee;
    private String remark;
    private Date createTime;
    private Date updateTime;


    public OrderDeliveryRecord() {
    }

    /**
     * 用业务对象OrderDeliveryRecordDTO初始化数据库对象OrderDeliveryRecord。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public OrderDeliveryRecord(OrderDeliveryRecordDTO dto) {
        this.id = dto.getId();
        this.logisticsNo = dto.getLogisticsNo();
        this.orderId = dto.getOrderId();
        this.mainOrderId = dto.getMainOrderId();
        this.productOneCategoryId = dto.getProductOneCategoryId();
        this.productOneCategoryName = dto.getProductOneCategoryName();
        this.productTwoCategoryId = dto.getProductTwoCategoryId();
        this.productTwoCategoryName = dto.getProductTwoCategoryName();
        this.productCategoryId = dto.getProductCategoryId();
        this.productCategoryName = dto.getProductCategoryName();
        this.productImg = dto.getProductImg();
        this.num = dto.getNum();
        this.boxNum = dto.getBoxNum();
        this.deliveryTime = dto.getDeliveryTime();
        this.terminal = dto.getTerminal();
        this.userId = dto.getUserId();
        this.addressName = dto.getAddressName();
        this.addressPhone = dto.getAddressPhone();
        this.addressProvince = dto.getAddressProvince();
        this.addressCity = dto.getAddressCity();
        this.addressRegion = dto.getAddressRegion();
        this.addressStreet = dto.getAddressStreet();
        this.stationId = dto.getStationId();
        this.payType = dto.getPayType();
        this.stationName = dto.getStationName();
        this.sendingCompany = dto.getSendingCompany();
        this.sender = dto.getSender();
        this.senderProvince = dto.getSenderProvince();
        this.senderCity = dto.getSenderCity();
        this.senderRegion = dto.getSenderRegion();
        this.senderStreet = dto.getSenderStreet();
        this.senderPhone = dto.getSenderPhone();
        this.setShipper = dto.getSetShipper();
        this.setShipTime = dto.getSetShipTime();
        this.deliveryPeople = dto.getDeliveryPeople();
        this.weigh = dto.getWeigh();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.logisticsCompany = dto.getLogisticsCompany();
        this.logisticsFee = dto.getLogisticsFee();
        this.remark = dto.getRemark();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象OrderDeliveryRecordDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(OrderDeliveryRecordDTO dto) {
        dto.setId(this.id);
        dto.setLogisticsNo(this.logisticsNo);
        dto.setOrderId(this.orderId);
        dto.setMainOrderId(this.mainOrderId);
        dto.setProductOneCategoryId(this.productOneCategoryId);
        dto.setProductOneCategoryName(this.productOneCategoryName);
        dto.setProductTwoCategoryId(this.productTwoCategoryId);
        dto.setProductTwoCategoryName(this.productTwoCategoryName);
        dto.setProductCategoryId(this.productCategoryId);
        dto.setProductCategoryName(this.productCategoryName);
        dto.setProductImg(this.productImg);
        dto.setNum(this.num);
        dto.setBoxNum(this.boxNum);
        dto.setDeliveryTime(this.deliveryTime);
        dto.setTerminal(this.terminal);
        dto.setUserId(this.userId);
        dto.setAddressName(this.addressName);
        dto.setAddressPhone(this.addressPhone);
        dto.setAddressProvince(this.addressProvince);
        dto.setAddressCity(this.addressCity);
        dto.setAddressRegion(this.addressRegion);
        dto.setAddressStreet(this.addressStreet);
        dto.setStationId(this.stationId);
        dto.setPayType(this.payType);
        dto.setStationName(this.stationName);
        dto.setSendingCompany(this.sendingCompany);
        dto.setSender(this.sender);
        dto.setSenderProvince(this.senderProvince);
        dto.setSenderCity(this.senderCity);
        dto.setSenderRegion(this.senderRegion);
        dto.setSenderStreet(this.senderStreet);
        dto.setSenderPhone(this.senderPhone);
        dto.setSetShipper(this.setShipper);
        dto.setSetShipTime(this.setShipTime);
        dto.setDeliveryPeople(this.deliveryPeople);
        dto.setWeigh(this.weigh);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setLogisticsCompany(this.getLogisticsCompany());
        dto.setLogisticsFee(this.logisticsFee);
        dto.setRemark(this.remark);
    }
}
