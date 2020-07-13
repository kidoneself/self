package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：水机设备用户
 *
 * @Author Zhang Bo
 * @Date 2019/5/17
 */
@Table(name = "water_device_user")
@Getter
@Setter
public class WaterDeviceUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String realName;
    private String phone;
    private String province;
    private String city;
    private String region;
    private String address;
    private String job;
    private Integer sex;
    private Integer age;
    private Integer type;//类别 1-个人 2-公司
    private String idCard;
    private String degree;//文化程度
    private String email;
    private Date createTime;
    private Date updateTime;

    //mongo数据库中的额主键
    private String oldId;

    //经销商ID
    private Integer distributorId;
    //mongo库中经销商ID
    private String oldDistributorId;

    //子经销商ID
    private Integer childDistributorId;
    private String oldChildDistributorId;
    //用户来源方式
    private Integer origin;
    private Integer originTerminal;

    private String companyName;
    private String companyIndustry;
    private String sceneTag;
    private String serviceNum;

    private String distributorAccount;
    private String distributorName;
    private Integer roleLevel;
    private String roleName;
    private String distributorPhone;
    private String distributorIdCard;
    private String distributorProvince;
    private String distributorCity;
    private String distributorRegion;
    private String distributorAddress;
    private String childDistributorName;
    private String childDistributorAccount;
    private String childDistributorPhone;


    public WaterDeviceUser() {
    }

    /**
     * 用业务对象WaterDeviceUserDTO初始化数据库对象WaterDeviceUser。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public WaterDeviceUser(WaterDeviceUserDTO dto) {
        this.id = dto.getId();
        this.realName = dto.getRealName();
        this.phone = dto.getPhone();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.address = dto.getAddress();
        this.job = dto.getJob();
        this.sex = dto.getSex();
        this.age = dto.getAge();
        this.type = dto.getType();
        this.idCard = dto.getIdCard();
        this.degree = dto.getDegree();
        this.email = dto.getEmail();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.oldId = dto.getOldId();
        this.distributorId = dto.getDistributorId();
        this.oldDistributorId = dto.getOldDistributorId();
        this.childDistributorId = dto.getChildDistributorId();
        this.oldChildDistributorId = dto.getOldChildDistributorId();
        this.origin = dto.getOrigin();
        this.originTerminal = dto.getOriginTerminal();
        this.companyName = dto.getCompanyName();
        this.companyIndustry = dto.getCompanyIndustry();
        this.sceneTag = dto.getSceneTag();
        this.serviceNum = dto.getServiceNum();
        this.distributorAccount = dto.getDistributorAccount();
        this.distributorName = dto.getDistributorName();
        this.roleLevel = dto.getRoleLevel();
        this.roleName = dto.getRoleName();
        this.distributorPhone = dto.getDistributorPhone();
        this.distributorIdCard = dto.getDistributorIdCard();
        this.distributorProvince = dto.getDistributorProvince();
        this.distributorCity = dto.getDistributorCity();
        this.distributorRegion = dto.getDistributorRegion();
        this.distributorAddress = dto.getDistributorAddress();
        this.childDistributorName = dto.getChildDistributorName();
        this.childDistributorAccount = dto.getChildDistributorAccount();
        this.childDistributorPhone = dto.getChildDistributorPhone();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceUserDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceUserDTO dto) {
        dto.setId(this.id);
        dto.setRealName(this.realName);
        dto.setPhone(this.phone);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAddress(this.address);
        dto.setJob(this.job);
        dto.setSex(this.sex);
        dto.setAge(this.age);
        dto.setType(this.type);
        dto.setIdCard(this.idCard);
        dto.setDegree(this.degree);
        dto.setEmail(this.email);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setOldId(this.oldId);
        dto.setDistributorId(this.distributorId);
        dto.setOldDistributorId(this.oldDistributorId);
        dto.setChildDistributorId(this.childDistributorId);
        dto.setOldChildDistributorId(this.oldChildDistributorId);
        dto.setOrigin(this.origin);
        dto.setOriginTerminal(this.originTerminal);
        dto.setCompanyName(this.companyName);
        dto.setCompanyIndustry(this.companyIndustry);
        dto.setSceneTag(this.sceneTag);
        dto.setServiceNum(this.serviceNum);
        dto.setDistributorAccount(this.distributorAccount);
        dto.setDistributorName(this.distributorName);
        dto.setRoleLevel(this.roleLevel);
        dto.setRoleName(this.roleName);
        dto.setDistributorPhone(this.distributorPhone);
        dto.setDistributorIdCard(this.distributorIdCard);
        dto.setDistributorProvince(this.distributorProvince);
        dto.setDistributorCity(this.distributorCity);
        dto.setDistributorRegion(this.distributorRegion);
        dto.setDistributorAddress(this.distributorAddress);
        dto.setChildDistributorName(this.childDistributorName);
        dto.setChildDistributorAccount(this.childDistributorAccount);
        dto.setChildDistributorPhone(this.childDistributorPhone);
    }
}
