package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.SubOrderDetailDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 子订单详细信息
 *
 * @author Zhang Bo
 * @date 2019/2/28.
 */
@Table(name = "order_sub_detail")
@Getter
@Setter
public class SubOrderDetail {

    @Id
    private Long subOrderId;           //子订单号

    //水机详细信息
    private Integer costId;            //计费方式ID
    private String costName;           //计费方式名称
    private Integer dispatchType;      //派单方式：1-手动派单；2-自动派单；
    private BigDecimal openAccountFee; //开户费，默认0
    private Date serviceTime;          //水机预约安装服务时间

    //产品详细信息
    private Integer productId;          //产品ID
    private String productName;         //产品名称
    private String productImg;          //产品封面图片
    private Integer productCompanyId;   //产品公司ID
    private String productCompanyName;  //产品公司名称
    private Integer productFirstCategoryId;  //产品一级类目ID（一级类目）
    private String productFirstCategoryName; //产品一级类目名称（一级类目）
    private Integer productTwoCategoryId;  //产品二级类目ID（二级类目）
    private String productTwoCategoryName; //产品二级类目名称（二级类目）
    private Integer productCategoryId;  //产品类目ID（三级类目）
    private String productCategoryName; //产品类目名称（三级类目）

    //收货人详细信息
    private String addresseeName;     //收货人
    private String addresseePhone;    //收货人手机号
    private String addresseeProvince; //收货人省
    private String addresseeCity;     //收货人市
    private String addresseeRegion;   //收货人区
    private String addresseeStreet;   //收货人详细地址
    private Integer addresseeSex;      //收件人性别 1-男 2-女
    private String addresseeCompanyName;//收货公司名称

    //下单用户详细信息
    private Integer userId;       //下单用户ID
    private Integer userType;     //下单用户等级：0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商
    private String userTypeName;  //下单用户等级名称
    private String userName;      //下单用户姓名
    private String userPhone;     //下单用户手机号


    //会员用户详细信息
    private Integer vipUserId;       //会员用户ID
    private Integer vipUserType;     //会员用户等级：0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商
    private String vipUserTypeName;  //会员用户等级名称
    private String vipUserName;      //会员用户姓名
    private String vipUserPhone;     //会员用户手机号
    private Boolean vipUserHasIncome;//会员用户是否享受收益：0-否；1-是；

    //经销商详细信息
    private Integer distributorId;       //经销商ID
    private String distributorAccount;   //经销商账号
    private String distributorName;      //经销商姓名
    /*private Integer distributorType;     //经销商身份：1-代理商,2-经销商, 3-经销商+代理商*/
    private String distributorTypeName;  //经销商身份名称
    private String distributorPhone;     //经销商电话
    private String distributorProvince;  //经销商所属省
    private String distributorCity;      //经销商所属市
    private String distributorRegion;    //经销商所属区
    private Integer distributorAreaId;    //经销商所属地区id

    private Integer subDistributorId;       //经销商子账号ID
    private String subDistributorAccount;   //经销商子账号
    private String subDistributorName;      //经销商子账号姓名
    private String subDistributorPhone;     //经销商子账号手机号

    private Integer recommendId;       //推荐人ID
    private String recommendAccount;   //推荐人账号
    private String recommendName;      //推荐人姓名
    private String recommendPhone;     //推荐人手机号
    private String recommendProvince;  //推荐人所属省
    private String recommendCity;      //推荐人所属市
    private String recommendRegion;    //推荐人所属区

    //安装工详细信息
    private String mongoEngineerId;      //mongo库安装工ID
    private Integer engineerId;          //安装工ID
    private String engineerName;         //安装工姓名
    private String engineerIdCard;       //安装工身份证
    private String engineerPhone;        //安装工电话
    private String engineerProvince;     //安装工所属省
    private String engineerCity;         //安装工所属市
    private String engineerRegion;       //安装工所属区

    //服务站详细信息
    private Integer stationId;             //服务站ID
    private String stationName;            //服务站名称
    private String stationPhone;           //服务站联系电话
    private String stationProvince;        //服务站所属省
    private String stationCity;            //服务站所属市
    private String stationRegion;          //服务站所属区
    private Integer stationCompanyId;      //服务站区县公司ID
    private String stationCompanyName;     //服务站区县公司名称(财务需要）
    private String stationCompanyProvince; //服务站区县公司所属省
    private String stationCompanyCity;     //服务站区县公司所属市
    private String stationCompanyRegion;   //服务站区县公司所属区

    private String salesSubjectName;       //销售主体名称
    private String salesSubjectCompanyName;//销售主体公司名称
    private String salesSubjectProvince;   //销售主体省
    private String salesSubjectCity;       //销售主体市
    private String salesSubjectRegion;     //销售主体区

    private Date createTime; //订单详情创建时间
    private Date updateTime; //订单详情更新时间


    /**
     * 将数据库实体对象信息拷贝到业务对象SubOrderDetailDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(OrderSubDTO dto) {
        dto.setCostId(this.costId);
        dto.setCostName(this.costName);
        dto.setDispatchType(this.dispatchType);
        dto.setOpenAccountFee(this.openAccountFee);
        dto.setServiceTime(this.serviceTime);
        dto.setProductId(this.productId);
        dto.setProductName(this.productName);
        dto.setProductImg(this.productImg);
        dto.setProductCompanyId(this.productCompanyId);
        dto.setProductCompanyName(this.productCompanyName);
        dto.setProductCategoryId(this.productCategoryId);
        dto.setProductCategoryName(this.productCategoryName);
        dto.setProductOneCategoryId(this.getProductFirstCategoryId());
        dto.setProductOneCategoryName(this.productFirstCategoryName);
        dto.setProductTwoCategoryName(this.productTwoCategoryName);
        dto.setAddresseeName(this.addresseeName);
        dto.setAddresseePhone(this.addresseePhone);
        dto.setAddresseeSex(this.addresseeSex);
        dto.setAddresseeProvince(this.addresseeProvince);
        dto.setAddresseeCity(this.addresseeCity);
        dto.setAddresseeRegion(this.addresseeRegion);
        dto.setAddresseeStreet(this.addresseeStreet);
        dto.setAddresseeCompanyName(this.addresseeCompanyName);
        dto.setUserId(this.userId);
        dto.setUserType(this.userType);
        dto.setUserTypeName(this.userTypeName);
        dto.setUserName(this.userName);
        dto.setUserPhone(this.userPhone);
        dto.setDistributorId(this.distributorId);
        dto.setDistributorAccount(this.distributorAccount);
        dto.setDistributorName(this.distributorName);
        dto.setDistributorTypeName(this.distributorTypeName);
        dto.setDistributorPhone(this.distributorPhone);
        dto.setDistributorProvince(this.distributorProvince);
        dto.setDistributorCity(this.distributorCity);
        dto.setDistributorRegion(this.distributorRegion);
        dto.setSubDistributorId(this.subDistributorId);
        dto.setSubDistributorAccount(this.subDistributorAccount);
        dto.setSubDistributorName(this.subDistributorName);
        dto.setSubDistributorPhone(this.subDistributorPhone);
        dto.setRecommendId(this.recommendId);
        dto.setRecommendName(this.recommendName);
        dto.setRecommendAccount(this.recommendAccount);
        dto.setRecommendPhone(this.recommendPhone);
        dto.setRecommendProvince(this.recommendProvince);
        dto.setRecommendCity(this.recommendCity);
        dto.setRecommendRegion(this.recommendRegion);
        dto.setEngineerId(this.engineerId);
        dto.setEngineerIdCard(this.engineerIdCard);
        dto.setEngineerName(this.engineerName);
        dto.setEngineerPhone(this.engineerPhone);
        dto.setEngineerProvince(this.engineerProvince);
        dto.setEngineerCity(this.engineerCity);
        dto.setEngineerRegion(this.engineerRegion);
        dto.setStationId(this.stationId);
        dto.setStationName(this.stationName);
        dto.setStationPhone(this.stationPhone);
        dto.setStationProvince(this.stationProvince);
        dto.setStationCity(this.stationCity);
        dto.setStationRegion(this.stationRegion);
        dto.setStationCompanyId(this.stationCompanyId);
        dto.setStationCompanyName(this.stationCompanyName);
        dto.setStationCompanyProvince(this.stationCompanyProvince);
        dto.setStationCompanyCity(this.stationCompanyCity);
        dto.setStationCompanyRegion(this.stationCompanyRegion);

        dto.setSalesSubjectName(this.salesSubjectName);
        dto.setSalesSubjectCompanyName(this.salesSubjectCompanyName);
        dto.setSalesSubjectProvince(this.salesSubjectProvince);
        dto.setSalesSubjectCity(this.salesSubjectCity);
        dto.setSalesSubjectRegion(this.salesSubjectRegion);

        dto.setVipUserId(this.vipUserId);
        dto.setVipUserName(this.vipUserName);
        dto.setVipUserPhone(this.vipUserPhone);
        dto.setVipUserType(this.vipUserType);
        dto.setVipUserTypeName(this.vipUserTypeName);
        dto.setVipUserHasIncome(this.vipUserHasIncome);
    }

    public SubOrderDetail() {
    }

    /**
     * 用业务对象SubOrderDetailDTO初始化数据库对象SubOrderDetail。
     *
     * @param dto 业务对象
     */
    public SubOrderDetail(SubOrderDetailDTO dto) {
        this.subOrderId = dto.getSubOrderId();
        this.costId = dto.getCostId();
        this.costName = dto.getCostName();
        this.dispatchType = dto.getDispatchType();
        this.openAccountFee = dto.getOpenAccountFee();
        this.serviceTime = dto.getServiceTime();
        this.productId = dto.getProductId();
        this.productName = dto.getProductName();
        this.productImg = dto.getProductImg();
        this.productCompanyId = dto.getProductCompanyId();
        this.productCompanyName = dto.getProductCompanyName();
        this.productFirstCategoryId = dto.getProductFirstCategoryId();
        this.productFirstCategoryName = dto.getProductFirstCategoryName();
        this.productTwoCategoryId = dto.getProductTwoCategoryId();
        this.productTwoCategoryName = dto.getProductTwoCategoryName();
        this.productCategoryId = dto.getProductCategoryId();
        this.productCategoryName = dto.getProductCategoryName();
        this.addresseeName = dto.getAddresseeName();
        this.addresseePhone = dto.getAddresseePhone();
        this.addresseeProvince = dto.getAddresseeProvince();
        this.addresseeCity = dto.getAddresseeCity();
        this.addresseeRegion = dto.getAddresseeRegion();
        this.addresseeStreet = dto.getAddresseeStreet();
        this.addresseeSex = dto.getAddresseeSex();
        this.addresseeCompanyName = dto.getAddresseeCompanyName();
        this.userId = dto.getUserId();
        this.userType = dto.getUserType();
        this.userTypeName = dto.getUserTypeName();
        this.userName = dto.getUserName();
        this.userPhone = dto.getUserPhone();
        this.vipUserId = dto.getVipUserId();
        this.vipUserType = dto.getVipUserType();
        this.vipUserTypeName = dto.getVipUserTypeName();
        this.vipUserName = dto.getVipUserName();
        this.vipUserPhone = dto.getVipUserPhone();
        this.vipUserHasIncome = dto.getVipUserHasIncome();
        this.distributorId = dto.getDistributorId();
        this.distributorAccount = dto.getDistributorAccount();
        this.distributorName = dto.getDistributorName();
        this.distributorTypeName = dto.getDistributorTypeName();
        this.distributorPhone = dto.getDistributorPhone();
        this.distributorProvince = dto.getDistributorProvince();
        this.distributorCity = dto.getDistributorCity();
        this.distributorRegion = dto.getDistributorRegion();
        this.subDistributorId = dto.getSubDistributorId();
        this.subDistributorAccount = dto.getSubDistributorAccount();
        this.subDistributorName = dto.getSubDistributorName();
        this.subDistributorPhone = dto.getSubDistributorPhone();
        this.recommendId = dto.getRecommendId();
        this.recommendAccount = dto.getRecommendAccount();
        this.recommendName = dto.getRecommendName();
        this.recommendPhone = dto.getRecommendPhone();
        this.recommendProvince = dto.getRecommendProvince();
        this.recommendCity = dto.getRecommendCity();
        this.recommendRegion = dto.getRecommendRegion();
        this.mongoEngineerId = dto.getMongoEngineerId();
        this.engineerId = dto.getEngineerId();
        this.engineerName = dto.getEngineerName();
        this.engineerIdCard = dto.getEngineerIdCard();
        this.engineerPhone = dto.getEngineerPhone();
        this.engineerProvince = dto.getEngineerProvince();
        this.engineerCity = dto.getEngineerCity();
        this.engineerRegion = dto.getEngineerRegion();
        this.stationId = dto.getStationId();
        this.stationName = dto.getStationName();
        this.stationPhone = dto.getStationPhone();
        this.stationProvince = dto.getStationProvince();
        this.stationCity = dto.getStationCity();
        this.stationRegion = dto.getStationRegion();
        this.stationCompanyId = dto.getStationCompanyId();
        this.stationCompanyName = dto.getStationCompanyName();
        this.stationCompanyProvince = dto.getStationCompanyProvince();
        this.stationCompanyCity = dto.getStationCompanyCity();
        this.stationCompanyRegion = dto.getStationCompanyRegion();
        this.salesSubjectName = dto.getSalesSubjectName();
        this.salesSubjectCompanyName = dto.getSalesSubjectCompanyName();
        this.salesSubjectProvince = dto.getSalesSubjectProvince();
        this.salesSubjectCity = dto.getSalesSubjectCity();
        this.salesSubjectRegion = dto.getSalesSubjectRegion();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象SubOrderDetailDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(SubOrderDetailDTO dto) {
        dto.setSubOrderId(this.subOrderId);
        dto.setCostId(this.costId);
        dto.setCostName(this.costName);
        dto.setDispatchType(this.dispatchType);
        dto.setOpenAccountFee(this.openAccountFee);
        dto.setServiceTime(this.serviceTime);
        dto.setProductId(this.productId);
        dto.setProductName(this.productName);
        dto.setProductImg(this.productImg);
        dto.setProductCompanyId(this.productCompanyId);
        dto.setProductCompanyName(this.productCompanyName);
        dto.setProductFirstCategoryId(this.productFirstCategoryId);
        dto.setProductFirstCategoryName(this.productFirstCategoryName);
        dto.setProductTwoCategoryId(this.productTwoCategoryId);
        dto.setProductTwoCategoryName(this.productTwoCategoryName);
        dto.setProductCategoryId(this.productCategoryId);
        dto.setProductCategoryName(this.productCategoryName);
        dto.setAddresseeName(this.addresseeName);
        dto.setAddresseePhone(this.addresseePhone);
        dto.setAddresseeProvince(this.addresseeProvince);
        dto.setAddresseeCity(this.addresseeCity);
        dto.setAddresseeRegion(this.addresseeRegion);
        dto.setAddresseeStreet(this.addresseeStreet);
        dto.setAddresseeSex(this.addresseeSex);
        dto.setAddresseeCompanyName(this.addresseeCompanyName);
        dto.setUserId(this.userId);
        dto.setUserType(this.userType);
        dto.setUserTypeName(this.userTypeName);
        dto.setUserName(this.userName);
        dto.setUserPhone(this.userPhone);
        dto.setVipUserId(this.vipUserId);
        dto.setVipUserType(this.vipUserType);
        dto.setVipUserTypeName(this.vipUserTypeName);
        dto.setVipUserName(this.vipUserName);
        dto.setVipUserPhone(this.vipUserPhone);
        dto.setVipUserHasIncome(this.vipUserHasIncome);
        dto.setDistributorId(this.distributorId);
        dto.setDistributorAccount(this.distributorAccount);
        dto.setDistributorName(this.distributorName);
        dto.setDistributorTypeName(this.distributorTypeName);
        dto.setDistributorPhone(this.distributorPhone);
        dto.setDistributorProvince(this.distributorProvince);
        dto.setDistributorCity(this.distributorCity);
        dto.setDistributorRegion(this.distributorRegion);
        dto.setSubDistributorId(this.subDistributorId);
        dto.setSubDistributorAccount(this.subDistributorAccount);
        dto.setSubDistributorName(this.subDistributorName);
        dto.setSubDistributorPhone(this.subDistributorPhone);
        dto.setRecommendId(this.recommendId);
        dto.setRecommendAccount(this.recommendAccount);
        dto.setRecommendName(this.recommendName);
        dto.setRecommendPhone(this.recommendPhone);
        dto.setRecommendProvince(this.recommendProvince);
        dto.setRecommendCity(this.recommendCity);
        dto.setRecommendRegion(this.recommendRegion);
        dto.setMongoEngineerId(this.mongoEngineerId);
        dto.setEngineerId(this.engineerId);
        dto.setEngineerName(this.engineerName);
        dto.setEngineerIdCard(this.engineerIdCard);
        dto.setEngineerPhone(this.engineerPhone);
        dto.setEngineerProvince(this.engineerProvince);
        dto.setEngineerCity(this.engineerCity);
        dto.setEngineerRegion(this.engineerRegion);
        dto.setStationId(this.stationId);
        dto.setStationName(this.stationName);
        dto.setStationPhone(this.stationPhone);
        dto.setStationProvince(this.stationProvince);
        dto.setStationCity(this.stationCity);
        dto.setStationRegion(this.stationRegion);
        dto.setStationCompanyId(this.stationCompanyId);
        dto.setStationCompanyName(this.stationCompanyName);
        dto.setStationCompanyProvince(this.stationCompanyProvince);
        dto.setStationCompanyCity(this.stationCompanyCity);
        dto.setStationCompanyRegion(this.stationCompanyRegion);
        dto.setSalesSubjectName(this.salesSubjectName);
        dto.setSalesSubjectCompanyName(this.salesSubjectCompanyName);
        dto.setSalesSubjectProvince(this.salesSubjectProvince);
        dto.setSalesSubjectCity(this.salesSubjectCity);
        dto.setSalesSubjectRegion(this.salesSubjectRegion);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}