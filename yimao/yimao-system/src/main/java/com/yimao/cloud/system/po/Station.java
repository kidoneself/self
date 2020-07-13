package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.StationDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 服务站门店（原服务站）
 *
 * @author Lizhqiang
 * @date 2019/1/17
 */
@Table(name = "station")
@Getter
@Setter
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                     //主键
    private String name;                    //名称
    private String code;                    //门店编号
    private Integer type;                   //门店类型：1-加盟店，2-连锁店，3-旗舰店；
    private String province;                //服务站所在省
    private String city;                    //服务站所在市
    private String region;                  //服务站所在区
    private String address;                 //地址
    private Double longitude;               //经度
    private Double latitude;                //纬度
    private Integer online;                 //上线状态：0-未上线；1-上线；
    private Date onlineTime;                //上线时间
    private String contact;                 //联系人
    private String contactPhone;            //联系人手机号
    private Integer masterDistributorId;                //经销商ID
    private String masterName;              //站长姓名
    private String masterPhone;             //站长电话
    private String masterIdCard;            //站长身份证

    private String companyName;             //服务站公司名称

    private Boolean contract;               //是否承包：0-未承保；1-已承包；
    private String contractor;              //承包人姓名
    private String contractorPhone;         //承包人电话
    private String contractorIdCard;        //承包人身份证号码
    private Date contractStartTime;         //承包开始时间
    private Date contractEndTime;           //承包结束时间

    private Date establishedTime;           //成立时间
    private Double satisfaction;            //满意度
    private Integer employeeNum;            //员工数量
    private String businessHoursStart;        //营业开始时间
    private String businessHoursEnd;          //营业结束时间
    private Boolean recommend;              //是否推荐：0-否；1-是；
    private String imgs;                    //服务站图片,多张图片用逗号隔开
    private String coverImage;              //服务站封面图片
    private Double stationArea;             //门店规模 单位:平方米
    private String purpose;                 //服务理念
    private String aptitude;                //资质授权
    private String introduction;            //服务站介绍
    private Integer sorts;                  //排序字段
    private Integer display;                //是否展示

    private String creator;                 //创建人
    private Date createTime;                //创建时间
    private String updater;                 //更新人
    private Date updateTime;                //更新时间


    //删除标识：0-未删除；1-已删除；
    private Boolean deleted;

    //mongdb数据库中的ID
    private String oldId;


    public Station() {
    }

    /**
     * 用业务对象StationDTO初始化数据库对象Station。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Station(StationDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.code = dto.getCode();
        this.type = dto.getType();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.address = dto.getAddress();
        this.longitude = dto.getLongitude();
        this.latitude = dto.getLatitude();
        this.online = dto.getOnline();
        this.onlineTime = dto.getOnlineTime();
        this.contact = dto.getContact();
        this.contactPhone = dto.getContactPhone();
        this.masterDistributorId = dto.getMasterDistributorId();
        this.masterName = dto.getMasterName();
        this.masterPhone = dto.getMasterPhone();
        this.masterIdCard = dto.getMasterIdCard();
        this.companyName = dto.getCompanyName();
        this.contract = dto.getContract();
        this.contractor = dto.getContractor();
        this.contractorPhone = dto.getContractorPhone();
        this.contractorIdCard = dto.getContractorIdCard();
        this.contractStartTime = dto.getContractStartTime();
        this.contractEndTime = dto.getContractEndTime();
        this.establishedTime = dto.getEstablishedTime();
        this.satisfaction = dto.getSatisfaction();
        this.employeeNum = dto.getEmployeeNum();
        this.businessHoursStart = dto.getBusinessHoursStart();
        this.businessHoursEnd = dto.getBusinessHoursEnd();
        this.recommend = dto.getRecommend();
        this.imgs = dto.getImgs();
        this.coverImage = dto.getCoverImage();
        this.stationArea = dto.getStationArea();
        this.purpose = dto.getPurpose();
        this.aptitude = dto.getAptitude();
        this.introduction = dto.getIntroduction();
        this.sorts = dto.getSorts();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.deleted = dto.getDeleted();
        this.oldId = dto.getOldId();
        this.display = dto.getDisplay();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StationDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StationDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setCode(this.code);
        dto.setType(this.type);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAddress(this.address);
        dto.setLongitude(this.longitude);
        dto.setLatitude(this.latitude);
        dto.setOnline(this.online);
        dto.setOnlineTime(this.onlineTime);
        dto.setContact(this.contact);
        dto.setContactPhone(this.contactPhone);
        dto.setMasterDistributorId(this.masterDistributorId);
        dto.setMasterName(this.masterName);
        dto.setMasterPhone(this.masterPhone);
        dto.setMasterIdCard(this.masterIdCard);
        dto.setCompanyName(this.companyName);
        dto.setContract(this.contract);
        dto.setContractor(this.contractor);
        dto.setContractorPhone(this.contractorPhone);
        dto.setContractorIdCard(this.contractorIdCard);
        dto.setContractStartTime(this.contractStartTime);
        dto.setContractEndTime(this.contractEndTime);
        dto.setEstablishedTime(this.establishedTime);
        dto.setSatisfaction(this.satisfaction);
        dto.setEmployeeNum(this.employeeNum);
        dto.setBusinessHoursStart(this.businessHoursStart);
        dto.setBusinessHoursEnd(this.businessHoursEnd);
        dto.setRecommend(this.recommend);
        dto.setImgs(this.imgs);
        dto.setCoverImage(this.coverImage);
        dto.setStationArea(this.stationArea);
        dto.setPurpose(this.purpose);
        dto.setAptitude(this.aptitude);
        dto.setIntroduction(this.introduction);
        dto.setSorts(this.sorts);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setDeleted(this.deleted);
        dto.setOldId(this.oldId);
        dto.setDisplay(this.display);
    }
}
