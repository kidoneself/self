package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 区县级公司
 *
 * @author Lizhqiang
 * @date 2019/1/17
 */
@Table(name = "station_company")
@Getter
@Setter
public class StationCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                 //主键
    private String name;                //区县级公司名称
    private String code;                //区县级公司编码
    private Integer type;               //区县级公司类型：1-自营；2-联营；3-入驻；
    private String province;            //省
    private String city;                //市
    private String region;              //区
    private String address;             //地址
    private String contact;             //联系人
    private String contactPhone;        //联系电话
    private String contactIdCard;       //身份证
    private String email;               //邮箱
    private String creditCode;          //统一社会信用代码
    private String legalPerson;          //法人代表姓名
    private String businessLicense;     //营业执照
    private String bank;                //开户行
    private String bankAccount;         //银行账号
    private String bankNumber;          //行号
    private String taxNumber;           //税号
    private Boolean online;             //上线状态：0-未上线；1-上线；
    private Date onlineTime;            //上线时间
    private String creator;             //创建人
    private Date createTime;            //创建时间
    private String updater;             //更新人
    private Date updateTime;            //更新时间
    private String yunSignId ;          // 中国云签 帐号 绑定
    private Date yunSignTime;           // 注册云签的时间
    private Integer signUp;             // 是否注册云签 0-否 ， 1- 是

    public StationCompany() {
    }

    /**
     * 用业务对象StationCompanyDTO初始化数据库对象StationCompany。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StationCompany(StationCompanyDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.code = dto.getCode();
        this.type = dto.getType();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.address = dto.getAddress();
        this.contact = dto.getContact();
        this.contactPhone = dto.getContactPhone();
        this.contactIdCard = dto.getContactIdCard();
        this.email = dto.getEmail();
        this.creditCode = dto.getCreditCode();
        this.legalPerson = dto.getLegalPerson();
        this.businessLicense = dto.getBusinessLicense();
        this.bank = dto.getBank();
        this.bankAccount = dto.getBankAccount();
        this.bankNumber = dto.getBankNumber();
        this.taxNumber = dto.getTaxNumber();
        this.online = dto.getOnline();
        this.onlineTime = dto.getOnlineTime();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.yunSignId = dto.getYunSignId();
        this.yunSignTime = dto.getYunSignTime();
        this.signUp = dto.getSignUp();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StationCompanyDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StationCompanyDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setCode(this.code);
        dto.setType(this.type);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAddress(this.address);
        dto.setContact(this.contact);
        dto.setContactPhone(this.contactPhone);
        dto.setContactIdCard(this.contactIdCard);
        dto.setEmail(this.email);
        dto.setCreditCode(this.creditCode);
        dto.setLegalPerson(this.legalPerson);
        dto.setBusinessLicense(this.businessLicense);
        dto.setBank(this.bank);
        dto.setBankAccount(this.bankAccount);
        dto.setBankNumber(this.bankNumber);
        dto.setTaxNumber(this.taxNumber);
        dto.setOnline(this.online);
        dto.setOnlineTime(this.onlineTime);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setYunSignId(this.yunSignId);
        dto.setYunSignTime(this.yunSignTime);
        dto.setSignUp(this.signUp);
    }
}
