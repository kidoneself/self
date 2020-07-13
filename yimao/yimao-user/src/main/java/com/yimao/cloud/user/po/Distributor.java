package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/11/13.
 */
@Table(name = "user_distributor")
@Getter
@Setter
public class Distributor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;                 //e家号
    private String userName;                //经销商账号
    private String password;                //密码
    private String nickName;                //经销商昵称
    private String realName;                //经销商姓名
    private Integer sex;                    //性别：1-男；2-女；
    private String phone;                   //手机号
    private String province;                //所在省
    private String city;                    //所在市
    private String region;                  //所在区
    private Integer areaId;                 //地区ID
    private String address;                 //地址
    private String idCard;                  //身份证
    private String email;                   //邮箱
    private BigDecimal money;               //支付金额

    private Integer type;                   //经销代理身份：1-代理商；2-经销商；3-经销商+代理商；
    private Boolean founder;                //是否为五大创始人：0-否；1-是；
    private Boolean stationMaster;          //是否为站长：0-否；1-是；
    private Integer recommendId;            //推荐人ID
    private String recommendName;           //推荐人姓名

    private Integer terminal;               //经销商创建端：1-翼猫业务系统；2-经销商app；
    private Integer sourceType;             //来源方式：1-翼猫企业二维码;2-翼猫后台上线; 3:经销商app创建账号; 4-资讯分享 ; 5-视频分享; 6-权益卡分享 ; 7-发展经销商二维码；
    private Boolean sponsor;                //是否为发起人：0-不是；1-是
    private Integer sponsorLevel;           //发起人级别：1-省级；2-市级；4-区级；3-省级+市级；5-省级+区级；6-市级+区级；7-省级+市级+区级；
    private Integer agentLevel;             //代理商级别：1-省代；2-市代；4-区代；3-省代+市代；5-省代+区代；6-市代+区代；7-省代+市代+区代；
    private Integer roleId;                 //经销商角色ID
    private Integer roleLevel;              //经销商角色等级：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子）
    private String roleName;                //经销商角色名称

    private Integer companyId;              //经销商企业ID
    private String companyName;             //经销商企业名称
    private Integer pid;                    //企业版主账号id
    private String attachment;              //附件
    private String remark;                  //备注
    private Integer quota;                  //配额
    private Integer remainingQuota;         //剩余配额
    private Integer provinceRanking;        //省代排名
    private Integer cityRanking;            //市代排名
    private Integer regionRanking;          //区代排名
    private Boolean deleted;                //是否删除：0-否 1-是
    private Boolean forbidden;              //是否禁用：0-否，1-是
    private Boolean forbiddenOrder;         //是否禁止下单：0-否，1-是
    private BigDecimal replacementAmount;   //置换金额
    private BigDecimal remainingReplacementAmount;//剩余置换金额

    private Boolean fuhuishun;              //是否是福慧顺：0-否，1-是
    private Date completeTime;              //成为该经销商身份时间

    private String oldId;                   //mongo库中的ID
    private String oldRecommendId;          //mongo库中的推荐人ID主键
    private String oldPid;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    private Integer appType;                //终端类型：1-Android；2-ios
    private String version;                 //app版本
    
    private Date provincialTime; //成为省代时间
    private Date cityTime;	//成为市代时间
    private Date districtTime; //成为区代时间

    public Distributor() {
    }

    /**
     * 用业务对象DistributorDTO初始化数据库对象Distributor。
     *
     * @param dto 业务对象
     */
    public Distributor(DistributorDTO dto) {
        this.id = dto.getId();
        this.userId = dto.getUserId();
        this.userName = dto.getUserName();
        this.password = dto.getPassword();
        this.nickName = dto.getNickName();
        this.realName = dto.getRealName();
        this.sex = dto.getSex();
        this.phone = dto.getPhone();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.address = dto.getAddress();
        this.idCard = dto.getIdCard();
        this.email = dto.getEmail();
        this.money = dto.getMoney();
        this.type = dto.getType();
        this.founder = dto.getFounder();
        this.stationMaster = dto.getStationMaster();
        this.recommendId = dto.getRecommendId();
        this.recommendName = dto.getRecommendName();
        this.terminal = dto.getTerminal();
        this.sourceType = dto.getSourceType();
        this.sponsor = dto.getSponsor();
        this.sponsorLevel = dto.getSponsorLevel();
        this.agentLevel = dto.getAgentLevel();
        this.roleId = dto.getRoleId();
        this.roleLevel = dto.getRoleLevel();
        this.roleName = dto.getRoleName();
        this.companyId = dto.getCompanyId();
        this.companyName = dto.getCompanyName();
        this.pid = dto.getPid();
        this.attachment = dto.getAttachment();
        this.remark = dto.getRemark();
        this.quota = dto.getQuota();
        this.remainingQuota = dto.getRemainingQuota();
        this.provinceRanking = dto.getProvinceRanking();
        this.cityRanking = dto.getCityRanking();
        this.regionRanking = dto.getRegionRanking();
        this.deleted = dto.getDeleted();
        this.forbidden = dto.getForbidden();
        this.forbiddenOrder = dto.getForbiddenOrder();
        this.replacementAmount = dto.getReplacementAmount();
        this.remainingReplacementAmount = dto.getRemainingReplacementAmount();
        this.fuhuishun = dto.getFuhuishun();
        this.completeTime = dto.getCompleteTime();
        this.oldId = dto.getOldId();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.appType = dto.getAppType();
        this.version = dto.getVersion();
        this.provincialTime = dto.getProvincialTime();
        this.cityTime = dto.getCityTime();
        this.districtTime = dto.getDistrictTime();
        this.oldRecommendId = dto.getOldRecommendId();
        this.areaId = dto.getAreaId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象DistributorDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(DistributorDTO dto) {
        dto.setId(this.id);
        dto.setUserId(this.userId);
        dto.setUserName(this.userName);
        dto.setPassword(this.password);
        dto.setNickName(this.nickName);
        dto.setRealName(this.realName);
        dto.setSex(this.sex);
        dto.setPhone(this.phone);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAddress(this.address);
        dto.setIdCard(this.idCard);
        dto.setEmail(this.email);
        dto.setMoney(this.money);
        dto.setType(this.type);
        dto.setFounder(this.founder);
        dto.setStationMaster(this.stationMaster);
        dto.setRecommendId(this.recommendId);
        dto.setRecommendName(this.recommendName);
        dto.setTerminal(this.terminal);
        dto.setSourceType(this.sourceType);
        dto.setSponsor(this.sponsor);
        dto.setSponsorLevel(this.sponsorLevel);
        dto.setAgentLevel(this.agentLevel);
        dto.setRoleId(this.roleId);
        dto.setRoleLevel(this.roleLevel);
        dto.setRoleName(this.roleName);
        dto.setCompanyId(this.companyId);
        dto.setCompanyName(this.companyName);
        dto.setPid(this.pid);
        dto.setAttachment(this.attachment);
        dto.setRemark(this.remark);
        dto.setQuota(this.quota);
        dto.setRemainingQuota(this.remainingQuota);
        dto.setProvinceRanking(this.provinceRanking);
        dto.setCityRanking(this.cityRanking);
        dto.setRegionRanking(this.regionRanking);
        dto.setDeleted(this.deleted);
        dto.setForbidden(this.forbidden);
        dto.setForbiddenOrder(this.forbiddenOrder);
        dto.setReplacementAmount(this.replacementAmount);
        dto.setRemainingReplacementAmount(this.remainingReplacementAmount);
        dto.setFuhuishun(this.fuhuishun);
        dto.setCompleteTime(this.completeTime);
        dto.setOldId(this.oldId);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setAppType(this.appType);
        dto.setVersion(this.version);
        dto.setProvincialTime(this.provincialTime);
        dto.setCityTime(this.cityTime);
        dto.setDistrictTime(this.districtTime);
        dto.setOldRecommendId(this.oldRecommendId);
        dto.setAreaId(this.areaId);
    }
}
