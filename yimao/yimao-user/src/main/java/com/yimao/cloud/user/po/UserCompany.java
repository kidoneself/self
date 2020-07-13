package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.UserCompanyDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author hhf
 * @description 企业
 * @date 2018/12/17
 */
@Table(name = "user_company")
@Data
public class UserCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    /**
     * 企业类型
     */
    private Integer companyType;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 信用代码
     */
    private String creditCode;

    /**
     * 税务信息
     */
    private String taxInformation;

    /**
     * 法人代表
     */
    private String corporateRepresentative;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 开户银行
     */
    private String bank;

    /**
     * 开设端口数量
     */
    private Integer portNumber;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 公司邮箱
     */
    private String email;

    /**
     * 公司地址
     */
    private String address;

    /**
     * 营业执照
     */
    private String businessLicense;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 场景标签
     */
    private String sceneTag;

    /**
     * 服务人数
     */
    private String serviceNum;

    /**
     * 备注
     */
    private String remark;

    /**
     * 对应mongo库的id
     */
    private String oldId;


    public UserCompany() {
    }

    /**
     * 用业务对象UserCompanyDTO初始化数据库对象UserCompany。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public UserCompany(UserCompanyDTO dto) {
        this.id = dto.getId();
        this.companyType = dto.getCompanyType();
        this.industry = dto.getIndustry();
        this.companyName = dto.getCompanyName();
        this.creditCode = dto.getCreditCode();
        this.taxInformation = dto.getTaxInformation();
        this.corporateRepresentative = dto.getCorporateRepresentative();
        this.bankAccount = dto.getBankAccount();
        this.bank = dto.getBank();
        this.portNumber = dto.getPortNumber();
        this.phone = dto.getPhone();
        this.email = dto.getEmail();
        this.address = dto.getAddress();
        this.businessLicense = dto.getBusinessLicense();
        this.introduction = dto.getIntroduction();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.sceneTag = dto.getSceneTag();
        this.serviceNum = dto.getServiceNum();
        this.remark = dto.getRemark();
        this.oldId = dto.getOldId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象UserCompanyDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(UserCompanyDTO dto) {
        dto.setId(this.id);
        dto.setCompanyType(this.companyType);
        dto.setIndustry(this.industry);
        dto.setCompanyName(this.companyName);
        dto.setCreditCode(this.creditCode);
        dto.setTaxInformation(this.taxInformation);
        dto.setCorporateRepresentative(this.corporateRepresentative);
        dto.setBankAccount(this.bankAccount);
        dto.setBank(this.bank);
        dto.setPortNumber(this.portNumber);
        dto.setPhone(this.phone);
        dto.setEmail(this.email);
        dto.setAddress(this.address);
        dto.setBusinessLicense(this.businessLicense);
        dto.setIntroduction(this.introduction);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setSceneTag(this.sceneTag);
        dto.setServiceNum(this.serviceNum);
        dto.setRemark(this.remark);
        dto.setOldId(this.oldId);
    }
}
