package com.yimao.cloud.pojo.dto.out;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/2/2.
 */
@Getter
@Setter
public class MongoOrderDTO {

    private String id;
    private String orderNo;//工单号
    private Integer state;//1-待付款，3-待发货，7-已完成
    private String province;//省
    private String city;//市
    private String region;//区
    private String address;//地址
    private Integer count;//下单数量
    private Integer totalAmount;//付款金额
    private Integer payState;//0-待付款，1-，2-，3-已支付
    private Integer payType;//支付类型  1:微信  2:支付宝 3:其他
    private Integer otherPayType;//其他支付类型(1-pos机,2-转账)
    private Integer payTerminal;//支付端：经销商APP、安装工APP、其它
    private Date payTime;//支付时间
    private String tradeNo;//交易单号
    private Boolean invoice;//是否开发票
    private Integer invoiceTitleType;//发票抬头类型
    private Integer invoiceType;//发票类型
    private Integer completeType;//完成状态
    private String remark;//备注
    private Date createTime;//订单创建时间
    private Date updateTime;
    private Date completeTime;//完成时间
    private String distributorId;//关联经销商表
    private String childDistributorId;
    private String distributorName;//经销商姓名
    private String distributorProvince;//经销商所在省
    private String distributorCity;//经销商所在城市
    private String distributorRegion;//经销商所在区县
    private String distributorPhone;//经销商电话
    private String distributorIdCard;//经销商身份证
    private String distributorUsername;//经销商账号
    private String userId;//关联用户表
    private String userPhone;//用户电话
    private String userName;//用户姓名
    private String deviceType;//健康产品
    private String deviceScope;//产品范围：养未来/翼猫冷敷贴
    private String deviceModel;//产品型号:翼腰贴...
    private Integer type;//1-经销商APP下单，2-二维码下单
    private Integer scanCodeType;//1-直播二维码，3-个人二维码，4-软文二维码

    private String waterDeviceId;
}
