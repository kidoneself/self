package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：安装工程师
 *
 * @Author Zhang Bo
 * @Date 2019/2/25 15:56
 * @Version 1.0
 */
@Table(name = "user_engineer")
@Getter
@Setter
public class Engineer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;         //安装工ID
    private String userName;    //用户名
    private String password;    //密码
    private String realName;    //真实姓名
    private String phone;       //手机号
    private String headImg;     //头像
    private String province;    //省
    private String city;        //市
    private String region;      //区县
    private Integer areaId;     //地区ID
    private Integer sex;        //性别（1：男，2：女）
    private String email;       //邮箱
    private String address;     //地址
    private String idCard;      //身份证号码
    private Integer state;      //客服人员状态：1-忙碌，0-空闲
    private Integer appType;    //系统：1-Android；2-ios
    private Integer count;      //正在进行的工单数
    private Boolean forbidden;  //是否禁用：0-否，1-是
    private Integer loginCount; //累计登录次数
    private Date lastLoginTime; //最后登录时间
    private String version;     //app版本
    private String workId;      //工号
    private Integer stationId;  //服务站ID
    private String stationName; //服务站名称
    private Integer stationCompanyId;  //服务站公司ID
    private String stationCompanyName; //服务站公司名称
    private String iccid;       //SIM卡卡号

    private String oldId;       //云平台mongo数据库安装工表ID
    private String oldSiteId;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    public Engineer() {
    }

    /**
     * 用业务对象EngineerDTO初始化数据库对象Engineer。
     *
     * @param dto 业务对象
     */
    public Engineer(EngineerDTO dto) {
        this.id = dto.getId();
        this.userName = dto.getUserName();
        this.password = dto.getPassword();
        this.realName = dto.getRealName();
        this.phone = dto.getPhone();
        this.headImg = dto.getHeadImg();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.sex = dto.getSex();
        this.email = dto.getEmail();
        this.address = dto.getAddress();
        this.idCard = dto.getIdCard();
        this.state = dto.getState();
        this.appType = dto.getAppType();
        this.count = dto.getCount();
        this.forbidden = dto.getForbidden();
        this.loginCount = dto.getLoginCount();
        this.lastLoginTime = dto.getLastLoginTime();
        this.version = dto.getVersion();
        this.workId = dto.getWorkId();
        this.stationId = dto.getStationId();
        this.areaId = dto.getAreaId();
        this.stationName = dto.getStationName();
        this.stationCompanyId = dto.getStationCompanyId();
        this.stationCompanyName = dto.getStationCompanyName();
        this.iccid = dto.getIccid();
        this.oldId = dto.getOldId();
        this.oldSiteId = dto.getOldSiteId();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象EngineerDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(EngineerDTO dto) {
        dto.setId(this.id);
        dto.setUserName(this.userName);
        dto.setPassword(this.password);
        dto.setRealName(this.realName);
        dto.setPhone(this.phone);
        dto.setHeadImg(this.headImg);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setSex(this.sex);
        dto.setEmail(this.email);
        dto.setAddress(this.address);
        dto.setAreaId(this.areaId);
        dto.setIdCard(this.idCard);
        dto.setState(this.state);
        dto.setAppType(this.appType);
        dto.setCount(this.count);
        dto.setForbidden(this.forbidden);
        dto.setLoginCount(this.loginCount);
        dto.setLastLoginTime(this.lastLoginTime);
        dto.setVersion(this.version);
        dto.setWorkId(this.workId);
        dto.setStationId(this.stationId);
        dto.setStationName(this.stationName);
        dto.setStationCompanyId(this.stationCompanyId);
        dto.setStationCompanyName(this.stationCompanyName);
        dto.setIccid(this.iccid);
        dto.setOldId(this.oldId);
        dto.setOldSiteId(this.oldSiteId);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
