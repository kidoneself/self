package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.DeliveryAddressDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author lizhiqiang
 * @Date 2019/1/28
 */
@Data
@Table(name = "delivery_address")
public class DeliveryAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String contact;        //联系人
    private String mobile;         //联系电话
    private String province;       //省
    private String city;           //市
    private String region;         //区
    private String street;         //联系地址
    private String company;        //公司名称
    private Integer hasDelivery;   //是否默认发货地址
    private Integer hasRefund;     //是否默认退货地址
    private String creator;        //创建人
    private Date createTime;
    private String updater;        //创建人
    private Date updateTime;
    private String remark;         //备注
    private Integer operatorId;//操作人id


    public DeliveryAddress() {
    }

    /**
     * 用业务对象DeliveryAddressDTO初始化数据库对象DeliveryAddress。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public DeliveryAddress(DeliveryAddressDTO dto) {
        this.id = dto.getId();
        this.contact = dto.getContact();
        this.mobile = dto.getMobile();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.street = dto.getStreet();
        this.company = dto.getCompany();
        this.hasDelivery = dto.getHasDelivery();
        this.hasRefund = dto.getHasRefund();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.remark = dto.getRemark();
        this.operatorId = dto.getOperatorId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象DeliveryAddressDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(DeliveryAddressDTO dto) {
        dto.setId(this.id);
        dto.setContact(this.contact);
        dto.setMobile(this.mobile);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setStreet(this.street);
        dto.setCompany(this.company);
        dto.setHasDelivery(this.hasDelivery);
        dto.setHasRefund(this.hasRefund);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setRemark(this.remark);
        dto.setOperatorId(this.operatorId);
    }
}
