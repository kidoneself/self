package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.WorkOrderBackDTO;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description   退机工单
 * @author Liu Yi
 * @date 2020/6/22 10:26
 */
@Table(name = "workorder_back")
@Data
public class WorkOrderBack {
  @Id
  private Integer id;                     //主键id
  private String code;                    //退机工单号
  private String workOrderId;             //工单id
  private Integer status;                 //退机状态:1-待处理，2-挂单，3-处理中，4-已完成
  //private Integer subStatus;            //退机子状态:1-开始，2-拆前拍照，3-拆后确认，4-完成
  private Date acceptTime;                //安装工接单时间
  private String province;                //省
  private String city;                    //市
  private String region;                  //区
  private String address;                 //详细地址
  private String sn;                      //设备sn
  private String iccid;                   //批次码
  private String logisticsCode;           //物流编码
  private Integer userId;                 //用户id
  private String userName;                //用户名
  private String userPhone;               //用户号码
  private Integer distributorId;          //经销商id
  private String distributorAccount;      //经销商账号
  private String distributorName;         //经销商姓名
  private Integer productId;              //产品id
  private String productName;             //产品名称
  private Integer productFirstCategoryId;  //产品一级类目ID（一级类目）
  private String productFirstCategoryName; //产品一级类目名称（一级类目）
  private Integer productTwoCategoryId;  //产品二级类目ID（二级类目）
  private String productTwoCategoryName; //产品二级类目名称（二级类目）
  private Integer productCategoryId;  //产品类目ID（三级类目）
  private String productCategoryName; //产品类目名称（三级类目）
  private Integer costId;                 //计费方式id
  private String costName;                //计费方式
  private BigDecimal money;               //设备剩余金额
  private Date installDate;               //安装日期
  private Integer engineerId;             //安装工id
  private String engineerName;            //安装工姓名
  private String engineerPhone;           //安装工电话
  /*private String engineerProvince;      //安装工省份
  private String engineerCity;            //安装工市
  private String engineerRegion;          //安装工区县*/
  private Integer stationId;              //服务站门店id
  private String stationName;             //服务站门店名称
/*  private String serviceProvince;         //服务省份
  private String serviceCity;             //服务市
  private String serviceRegion;           //服务区县*/
  private String img;                     //上传图片,多个逗号隔开
  private Date imgTime;                   //上传图片时间
  private Integer source;                 //来源：1-总部派单
  private Date completeTime;              //完成时间
  private Date serviceDate;               //服务日期
  private String serviceTimeLimit;        //服务时间段
  private Date appointDate;               // 改约时间
  private String appointTimeLimit;        // 时间点范围
  private Integer appointCauseType;       //改约原因类型：1-时间冲突  2-其它
  private Date countdownTime;             //倒计时时间
  private String remark;                  //备注
  private Date createTime;                //创建时间
  private Date updateTime;                //更新时间
  private String longitude;               // 经度
  private String latitude;                // 纬度


  public WorkOrderBack() {
  }

  /**
   * 用业务对象WorkOrderBackDTO初始化数据库对象WorkOrderBack。
   * plugin author ylfjm.
   *
   * @param dto 业务对象
   */
  public WorkOrderBack(WorkOrderBackDTO dto) {
    this.id = dto.getId();
    this.code = dto.getCode();
    this.workOrderId = dto.getWorkOrderId();
    this.status = dto.getStatus();
    this.acceptTime = dto.getAcceptTime();
    this.province = dto.getProvince();
    this.city = dto.getCity();
    this.region = dto.getRegion();
    this.address = dto.getAddress();
    this.sn = dto.getSn();
    this.iccid = dto.getIccid();
    this.logisticsCode = dto.getLogisticsCode();
    this.userId = dto.getUserId();
    this.userName = dto.getUserName();
    this.userPhone = dto.getUserPhone();
    this.distributorId = dto.getDistributorId();
    this.distributorAccount = dto.getDistributorAccount();
    this.distributorName = dto.getDistributorName();
    this.productId = dto.getProductId();
    this.productName = dto.getProductName();
    this.productFirstCategoryId = dto.getProductFirstCategoryId();
    this.productFirstCategoryName = dto.getProductFirstCategoryName();
    this.productTwoCategoryId = dto.getProductTwoCategoryId();
    this.productTwoCategoryName = dto.getProductTwoCategoryName();
    this.productCategoryId = dto.getProductCategoryId();
    this.productCategoryName = dto.getProductCategoryName();
    this.costId = dto.getCostId();
    this.costName = dto.getCostName();
    this.money = dto.getMoney();
    this.installDate = dto.getInstallDate();
    this.engineerId = dto.getEngineerId();
    this.engineerName = dto.getEngineerName();
    this.engineerPhone = dto.getEngineerPhone();
    this.stationId = dto.getStationId();
    this.stationName = dto.getStationName();
    this.img = dto.getImg();
    this.imgTime = dto.getImgTime();
    this.source = dto.getSource();
    this.completeTime = dto.getCompleteTime();
    this.serviceDate = dto.getServiceDate();
    this.serviceTimeLimit = dto.getServiceTimeLimit();
    this.appointDate = dto.getAppointDate();
    this.appointTimeLimit = dto.getAppointTimeLimit();
    this.appointCauseType = dto.getAppointCauseType();
    this.countdownTime = dto.getCountdownTime();
    this.remark = dto.getRemark();
    this.createTime = dto.getCreateTime();
    this.updateTime = dto.getUpdateTime();
    this.longitude = dto.getLongitude();
    this.latitude = dto.getLatitude();
  }

  /**
   * 将数据库实体对象信息拷贝到业务对象WorkOrderBackDTO上。
   * plugin author ylfjm.
   *
   * @param dto 业务对象
   */
  public void convert(WorkOrderBackDTO dto) {
    dto.setId(this.id);
    dto.setCode(this.code);
    dto.setWorkOrderId(this.workOrderId);
    dto.setStatus(this.status);
    dto.setAcceptTime(this.acceptTime);
    dto.setProvince(this.province);
    dto.setCity(this.city);
    dto.setRegion(this.region);
    dto.setAddress(this.address);
    dto.setSn(this.sn);
    dto.setIccid(this.iccid);
    dto.setLogisticsCode(this.logisticsCode);
    dto.setUserId(this.userId);
    dto.setUserName(this.userName);
    dto.setUserPhone(this.userPhone);
    dto.setDistributorId(this.distributorId);
    dto.setDistributorAccount(this.distributorAccount);
    dto.setDistributorName(this.distributorName);
    dto.setProductId(this.productId);
    dto.setProductName(this.productName);
    dto.setProductFirstCategoryId(this.productFirstCategoryId);
    dto.setProductFirstCategoryName(this.productFirstCategoryName);
    dto.setProductTwoCategoryId(this.productTwoCategoryId);
    dto.setProductTwoCategoryName(this.productTwoCategoryName);
    dto.setProductCategoryId(this.productCategoryId);
    dto.setProductCategoryName(this.productCategoryName);
    dto.setCostId(this.costId);
    dto.setCostName(this.costName);
    dto.setMoney(this.money);
    dto.setInstallDate(this.installDate);
    dto.setEngineerId(this.engineerId);
    dto.setEngineerName(this.engineerName);
    dto.setEngineerPhone(this.engineerPhone);
    dto.setStationId(this.stationId);
    dto.setStationName(this.stationName);
    dto.setImg(this.img);
    dto.setImgTime(this.imgTime);
    dto.setSource(this.source);
    dto.setCompleteTime(this.completeTime);
    dto.setServiceDate(this.serviceDate);
    dto.setServiceTimeLimit(this.serviceTimeLimit);
    dto.setAppointDate(this.appointDate);
    dto.setAppointTimeLimit(this.appointTimeLimit);
    dto.setAppointCauseType(this.appointCauseType);
    dto.setCountdownTime(this.countdownTime);
    dto.setRemark(this.remark);
    dto.setCreateTime(this.createTime);
    dto.setUpdateTime(this.updateTime);
    dto.setLongitude(this.longitude);
    dto.setLatitude(this.latitude);
  }
}
