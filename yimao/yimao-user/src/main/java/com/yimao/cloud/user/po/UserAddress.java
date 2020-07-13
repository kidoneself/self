package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.UserAddressDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author liuhao@yimaokeji.com
 * 2017112017/11/23
 */
@Table(name = "user_address")
@Data
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;//用户e家号
    private String contact;//联系人
    private String mobile;//手机号
    private Integer sex;//性别    1男 2 女
    private String province; //省
    private String city; //市
    private String region; //区
    private String street;//街道
    private Boolean defaultAddress;//默认地址 1:默认 0:非默认
    private Boolean deleted;        //是否删除 1:是 2:否
    private Date createTime;//创建时间
    private Date updateTime;//修改时间


    public UserAddress() {
    }

    /**
     * 用业务对象UserAddressDTO初始化数据库对象UserAddress。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public UserAddress(UserAddressDTO dto) {
        this.id = dto.getId();
        this.userId = dto.getUserId();
        this.contact = dto.getContact();
        this.mobile = dto.getMobile();
        this.sex = dto.getSex();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.street = dto.getStreet();
        this.defaultAddress = dto.getDefaultAddress();
        this.deleted = dto.getDeleted();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象UserAddressDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(UserAddressDTO dto) {
        dto.setId(this.id);
        dto.setUserId(this.userId);
        dto.setContact(this.contact);
        dto.setMobile(this.mobile);
        dto.setSex(this.sex);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setStreet(this.street);
        dto.setDefaultAddress(this.defaultAddress);
        dto.setDeleted(this.deleted);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}
