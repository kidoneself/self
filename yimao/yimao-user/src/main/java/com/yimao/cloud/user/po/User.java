package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.UserDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 用户信息表
 *
 * @author Zhang Bo
 * @date 2018/5/9.
 */
@Table(name = "user")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                     //e家号
    private String mobile;                  //手机号
    private String userName;                //用户名
    private String password;                //密码
    private String nickName;                //昵称
    private String realName;                //真实姓名
    private Integer userType;               //用户等级：0-体验版经销商；1-微创版经销商；2-个人版经销商；3-分享用户；4-普通用户；5-企业版经销商（主）；6-企业版经销商（子）；7-分销用户 8-特批经销商；
    private String userTypeName;            //用户身份：体验版经销商；微创版经销商；个人版经销商；企业版经销商；普通用户；会员用户；特批经销商
    private Date bindPhoneTime;             //用户绑定手机号的时间
    private Date bindDistributorTime;       //用户绑定上级经销商的时间
    private Date bindAmbassadorTime;        //用户绑定上级健康大使的时间
    private Integer distributorId;          //经销商ID
    private Integer ambassadorId;           //健康大使ID
    private Integer mid;                    //如果该用户绑定了经销商，该字段保存user_distributor表的主键
    // private String oldMid;                  //老的经销商id
    private String oldDistributorId;        //老的推荐人id

    private Date beDistributorTime;         //变为经销商的时间
    private Date beSalesTime;               //变为分销用户的时间
    private Date beSharerTime;              //变为分享用户的时间

    private String incomePermission;        //产品分销收益权限：例：1,2,3,4,5以逗号分隔 数字为产品类型
    private Boolean subscribe;              //是否关注公众号
    //用户来源方式：1-代言卡分享 2-宣传卡分享 3-资讯分享 4-视频分享 5-商品分享 6-自主关注公众号 7-经销商APP添加 8-水机屏推广二维码；
    // 11-经销商APP手机号注册；12-经销商APP微信授权注册；21-H5分享
    private Integer origin;
    private Integer originTerminal;         //来源端：1-健康e家公众号 2-经销商APP 3-净水设备

    private String qrcode;                  //个人专属二维码（公众号）
    private String wxacode;                 //个人专属二维码（小程序）
    private Boolean online;                 //是否在线：0-否，1-是
    private Boolean available;              //是否可用：0-否，1-是
    private String headImg;                 //头像
    private Integer age;                    //年龄
    private Date birthday;                  //出生年月
    private String email;                   //邮箱
    private Integer sex;                    //性别（1：男，2：女）
    private Integer height;                 //身高（单位cm）
    private Integer weight;                 //体重（单位kg）
    private String idCard;                  //身份证号码
    private Date lastLoginTime;             //最后登录时间
    private String province;                //省
    private String city;                    //市
    private String region;                  //区
    private String address;                 //详细地址

    private Date createTime;                //创建时间
    private Date updateTime;                //更新时间

    private String companyName;             //企业版经销商公司名称

    // private Integer customerType;//客户类型 1-个人客户 2-企业客户

    private Integer authId;                 //非业务字段，在合并账号的时候，被废弃的账号会用此字段记录下user_auths表的ID

    public User() {
    }

    /**
     * 用业务对象UserDTO初始化数据库对象User。
     *
     * @param dto 业务对象
     */
    public User(UserDTO dto) {
        this.id = dto.getId();
        // this.oldMid = dto.getOldMid();
        this.oldDistributorId = dto.getOldDistributorId();
        this.mobile = dto.getMobile();
        this.userName = dto.getUserName();
        this.password = dto.getPassword();
        this.nickName = dto.getNickName();
        this.realName = dto.getRealName();
        this.userType = dto.getUserType();
        this.userTypeName = dto.getUserTypeName();
        this.bindPhoneTime = dto.getBindPhoneTime();
        this.bindDistributorTime = dto.getBindDistributorTime();
        this.bindAmbassadorTime = dto.getBindAmbassadorTime();
        this.distributorId = dto.getDistributorId();
        this.ambassadorId = dto.getAmbassadorId();
        this.mid = dto.getMid();
        this.beDistributorTime = dto.getBeDistributorTime();
        this.beSalesTime = dto.getBeSalesTime();
        this.beSharerTime = dto.getBeSharerTime();
        this.incomePermission = dto.getIncomePermission();
        this.subscribe = dto.getSubscribe();
        this.origin = dto.getOrigin();
        this.originTerminal = dto.getOriginTerminal();
        this.qrcode = dto.getQrcode();
        this.wxacode = dto.getWxacode();
        this.online = dto.getOnline();
        this.available = dto.getAvailable();
        this.headImg = dto.getHeadImg();
        this.age = dto.getAge();
        this.birthday = dto.getBirthday();
        this.email = dto.getEmail();
        this.sex = dto.getSex();
        this.height = dto.getHeight();
        this.weight = dto.getWeight();
        this.idCard = dto.getIdCard();
        this.lastLoginTime = dto.getLastLoginTime();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.address = dto.getAddress();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.companyName = dto.getCompanyName();
        // this.customerType = dto.getCustomerType();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象UserDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(UserDTO dto) {
        dto.setId(this.id);
        // dto.setOldMid(this.oldMid);
        dto.setOldDistributorId(this.oldDistributorId);
        dto.setMobile(this.mobile);
        dto.setUserName(this.userName);
        dto.setPassword(this.password);
        dto.setNickName(this.nickName);
        dto.setRealName(this.realName);
        dto.setUserType(this.userType);
        dto.setUserTypeName(this.userTypeName);
        dto.setBindPhoneTime(this.bindPhoneTime);
        dto.setBindDistributorTime(this.bindDistributorTime);
        dto.setBindAmbassadorTime(this.bindAmbassadorTime);
        dto.setDistributorId(this.distributorId);
        dto.setAmbassadorId(this.ambassadorId);
        dto.setMid(this.mid);
        dto.setBeDistributorTime(this.beDistributorTime);
        dto.setBeSalesTime(this.beSalesTime);
        dto.setBeSharerTime(this.beSharerTime);
        dto.setIncomePermission(this.incomePermission);
        dto.setSubscribe(this.subscribe);
        dto.setOrigin(this.origin);
        dto.setOriginTerminal(this.originTerminal);
        dto.setQrcode(this.qrcode);
        dto.setWxacode(this.wxacode);
        dto.setOnline(this.online);
        dto.setAvailable(this.available);
        dto.setHeadImg(this.headImg);
        dto.setAge(this.age);
        dto.setBirthday(this.birthday);
        dto.setEmail(this.email);
        dto.setSex(this.sex);
        dto.setHeight(this.height);
        dto.setWeight(this.weight);
        dto.setIdCard(this.idCard);
        dto.setLastLoginTime(this.lastLoginTime);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAddress(this.address);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setCompanyName(this.companyName);
        // dto.setCustomerType(this.getCustomerType());
    }
}
