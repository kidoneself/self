package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.CompanyCustomerDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @description: 企业客户
 * @author: yu chunlei
 * @create: 2019-09-17 15:28:15
 **/
@Table(name = "company_customer")
@Data
public class CompanyCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String contact;
    private String mobile;
    private String companyName;
    private String industry;
    private String label;
    private String number;
    private String province;
    private String city;
    private String region;
    private String street;
    private Integer pid;
    private Integer sex;
    private Integer age;
    private Boolean deleted;
    private Date createTime;
    private Date updateTime;


    public CompanyCustomer() {
    }

    /**
     * 用业务对象CompanyCustomerDTO初始化数据库对象CompanyCustomer。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public CompanyCustomer(CompanyCustomerDTO dto) {
        this.id = dto.getId();
        this.contact = dto.getContact();
        this.mobile = dto.getMobile();
        this.companyName = dto.getCompanyName();
        this.industry = dto.getIndustry();
        this.label = dto.getLabel();
        this.number = dto.getNumber();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.street = dto.getStreet();
        this.pid = dto.getPid();
        this.sex = dto.getSex();
        this.age = dto.getAge();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象CompanyCustomerDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(CompanyCustomerDTO dto) {
        dto.setId(this.id);
        dto.setContact(this.contact);
        dto.setMobile(this.mobile);
        dto.setCompanyName(this.companyName);
        dto.setIndustry(this.industry);
        dto.setLabel(this.label);
        dto.setNumber(this.number);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setStreet(this.street);
        dto.setPid(this.pid);
        dto.setSex(this.sex);
        dto.setAge(this.age);
    }
}
