package com.yimao.cloud.pojo.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-03-26 15:42:23
 **/
@Getter
@Setter
public class SubOrderDetailDTO {

    private Long subOrderId;//子订单
    private Integer costId;//计费方式id
    private String costName;//计费方式名称
    private Integer dispatchType;//派单类型：0.自动派单，1，手动派单
    private BigDecimal openAccountFee;//开户费
    private Date serviceTime;//服务时间
    private Integer productId;//产品ID
    private String productName;//产品名称
    private String productImg;//产品封面图片
    private Integer productCompanyId;//产品公司id
    private String productCompanyName;//产品公司名称
    private Integer productCategoryId;//三级类目ID
    private String productCategoryName;//三级类目名称
    private Integer productFirstCategoryId;//一级类目ID
    private String productFirstCategoryName;//一级类目名称
    private Integer productTwoCategoryId;  //产品二级类目ID（二级类目）
    private String productTwoCategoryName; //产品二级类目名称（二级类目）
    private String addresseeName;//收件人姓名
    private Integer addresseeSex;//收件人性别 1-男 2-女
    private String addresseePhone;//收件人手机号
    private String addresseeProvince;//收件人省
    private String addresseeCity;//收件人市
    private String addresseeRegion;//收件人区
    private String addresseeStreet;//收件人街道
    private String addresseeCompanyName;//收货公司名称
    private Integer userId;//下单用户ID
    private Integer userType;//下单用户等级：0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商
    private String userTypeName;//下单用户等级名称
    private String userName;//下单用户姓名
    private String userPhone;//下单用户手机号
    private Integer distributorId;//经销商ID
    private String distributorAccount;//经销商ID
    private String distributorName;//经销商姓名
    private Integer distributorType;//经销商身份：1-代理商,2-经销商, 3-经销商+代理商
    private String distributorTypeName;//经销商身份名称
    private String distributorPhone;//经销商手机号
    private String distributorProvince;//经销商省
    private String distributorCity;//经销商市
    private String distributorRegion;//经销商区
    private String mongoEngineerId;//mongo库安装工ID
    private Integer engineerId;//安装工ID
    private String engineerName;//安装工姓名
    private String engineerIdCard;       //安装工身份证
    private String engineerPhone;//安装工手机号
    private String engineerProvince;//安装工所属省
    private String engineerCity;//安装工所属市
    private String engineerRegion;//安装工所属区
    private Integer stationId;//服务站ID
    private String stationName;//服务站名称
    private String stationPhone;//服务站联系电话
    private String stationProvince;//服务站所属省
    private String stationCity;//服务站所属市
    private String stationRegion;//服务站所属区
    private Integer stationCompanyId;//服务站区县公司ID
    private String stationCompanyName;//服务站区县公司名称
    private String stationCompanyProvince;//服务站区县公司所属省
    private String stationCompanyCity;//服务站区县公司所属市
    private String stationCompanyRegion;//服务站区县公司所属区
    private Date createTime;
    private Date updateTime;


    //会员用户详细信息
    private Integer vipUserId;       //会员用户ID
    private Integer vipUserType;     //会员用户等级：0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商
    private String vipUserTypeName;  //会员用户等级名称
    private String vipUserName;      //会员用户姓名
    private String vipUserPhone;     //会员用户手机号
    private Boolean vipUserHasIncome;//会员用户是否享受收益：0-否；1-是；

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

    private String salesSubjectName;       //销售主体名称
    private String salesSubjectCompanyName;//销售主体公司名称
    private String salesSubjectProvince;   //销售主体省
    private String salesSubjectCity;       //销售主体市
    private String salesSubjectRegion;     //销售主体区
    private Integer oldEngineerId;       //更换订单详情的安装工信息用到
}
