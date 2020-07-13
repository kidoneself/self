package com.yimao.cloud.hra.po;

import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 评估预约
 *
 * @author Zhang Bo
 * @date 2017/11/29.
 */
@Table(name = "hra_reserve")
@Data
public class Reserve{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;        //预约人
    private String mobile;      //手机号
    private Integer stationId;     //服务站
    private String stationName;     //服务站名称
    private Date reserveTime;   //预约时间
    private Date arriveTime;    //评估时间
    private String idCard;      //身份证
    private  Boolean sex;   //性别
    private String code;    //唯一code
    private Integer height;
    private Double weight;
    private Integer userSource;  //用户来源 1：APP 2：公众号 3：后台添加
    private Integer conditions;      //状态 0：已预约  1：已完成  -1：已失败  -2: 已过期
    //private Integer userId;

    protected Date createTime;
    protected String creator;
    protected Date updateTime;
    protected String updater;

}
