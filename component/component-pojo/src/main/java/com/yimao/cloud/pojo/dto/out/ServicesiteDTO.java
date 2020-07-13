package com.yimao.cloud.pojo.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhilin.he
 * @description 服务站信息
 * @date 2019/1/22 13:35
 **/
@Data
public class ServicesiteDTO implements Serializable {
    private static final long serialVersionUID = -7739307144437351137L;

    private String id;
    private String province;//省
    private String city;//市
    private String region;//区
    private String name;
    private Date createTime;
    private String person;//联系人
    private String phone;//联系电话
    private String address;//服务站详细地址
    private Date updateTime;
    private String mail;
    private String legalPerson;//法定代表人姓名
    private String companyName;//区县级公司名称
    private String creditCode;//统一社会信用代码
    private String masterName;//站长姓名
    private String masterPhone;//站长电话
    private String identityNumber;//站长身份证号
    private String yunSignId;//云签编号
    private Date synchronousTime;//同步时间
    private Boolean synchronousState;//同步状态
    private Boolean signUp;//是否云签
    private Date yunSignTime;//云签时间
    private String[] costRoles;//水机计费方式权限

}
