package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.OrderAddressDTO;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 订单收货地址
 *
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@Table(name = "order_address")
@Getter
@Setter
public class OrderAddress {

    @Id
    private Long id;
    private String contact;//联系人
    private Integer sex;//性别：1-男；2-女
    private String phone;//收货人手机号
    private String province;//省
    private String city;//市
    private String region;//区
    private String street;//街道
    private String postCode;//邮编
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private String companyName;//公司名称


    public OrderAddress() {
    }

    /**
     * 用业务对象OrderAddressDTO初始化数据库对象OrderAddress。
     *
     * @param dto 业务对象
     */
    public OrderAddress(OrderAddressDTO dto) {
        this.id = dto.getId();
        this.contact = dto.getContact();
        this.sex = dto.getSex();
        this.phone = dto.getPhone();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.street = dto.getStreet();
        this.postCode = dto.getPostCode();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.companyName = dto.getCompanyName();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象OrderAddressDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(OrderAddressDTO dto) {
        dto.setId(this.id);
        dto.setContact(this.contact);
        dto.setSex(this.sex);
        dto.setPhone(this.phone);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setStreet(this.street);
        dto.setPostCode(this.postCode);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setCompanyName(this.companyName);
    }
}