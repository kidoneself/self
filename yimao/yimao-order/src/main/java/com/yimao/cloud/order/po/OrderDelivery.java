package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.OrderDeliveryDTO;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Chen Hui Yang
 * @date 2018/12/26
 * 订单发货单
 */
@Table(name = "order_delivery")
@Data
public class OrderDelivery {
    @Id
    private Long id;                            //发货单ID
    private Long orderId;                       //子订单ID
    private String logisticsNo;                 //物流单号
    private String logisticsCompany;            //物流公司
    private String logisticsCompanyCode;        //物流公司编码
    private Integer logisticsFee;            //物流费用
    private Integer num;                        //发货件数
    private Integer boxNum;                     //发货数量/盒
    private Integer weigh;                      //重量
    private Date deliveryTime;                  //发货时间
    private String payType;                    //寄付方式
    private String deliveryPhone;                      //发货人联系方式
    private String sendingCompany;
    private String sender;
    private String senderProvince;
    private String senderCity;
    private String senderRegion;
    private String senderStreet;
    private String remark;                      //备注
    private Date createTime;                    //创建时间
    private String creator;                     //创建人


    public OrderDelivery() {
    }

    /**
     * 用业务对象OrderDeliveryDTO初始化数据库对象OrderDelivery。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public OrderDelivery(OrderDeliveryDTO dto) {
        this.id = dto.getId();
        this.orderId = dto.getOrderId();
        this.logisticsNo = dto.getLogisticsNo();
        this.logisticsCompany = dto.getLogisticsCompany();
        this.logisticsCompanyCode = dto.getLogisticsCompanyCode();
        this.logisticsFee = dto.getLogisticsFee();
        this.num = dto.getNum();
        this.boxNum = dto.getBoxNum();
        this.weigh = dto.getWeigh();
        this.deliveryTime = dto.getDeliveryTime();
        this.payType = dto.getPayType();
        this.deliveryPhone = dto.getDeliveryPhone();
        this.remark = dto.getRemark();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.sendingCompany = dto.getSendingCompany();
        this.sender = dto.getSender();
        this.senderProvince = dto.getSenderProvince();
        this.senderCity = dto.getSenderCity();
        this.senderRegion = dto.getSenderRegion();
        this.senderStreet = dto.getSenderStreet();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象OrderDeliveryDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(OrderDeliveryDTO dto) {
        dto.setId(this.id);
        dto.setOrderId(this.orderId);
        dto.setLogisticsNo(this.logisticsNo);
        dto.setLogisticsCompany(this.logisticsCompany);
        dto.setLogisticsCompanyCode(this.logisticsCompanyCode);
        dto.setLogisticsFee(this.logisticsFee);
        dto.setNum(this.num);
        dto.setBoxNum(this.boxNum);
        dto.setWeigh(this.weigh);
        dto.setDeliveryTime(this.deliveryTime);
        dto.setPayType(this.payType);
        dto.setDeliveryPhone(this.deliveryPhone);
        dto.setRemark(this.remark);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setSendingCompany(this.sendingCompany);
        dto.setSender(this.sender);
        dto.setSenderProvince(this.senderProvince);
        dto.setSenderCity(this.senderCity);
        dto.setSenderRegion(this.senderRegion);
        dto.setSenderStreet(this.senderStreet);
    }
}
