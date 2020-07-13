package com.yimao.cloud.pojo.dto.out;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zhilin.he
 * @description 安装工信息
 * @date 2019/1/22 13:32
 **/
@Data
public class CustomerDTO implements Serializable {
    private static final long serialVersionUID = 4851504586695077992L;

    private String id;
    private String name;//13271139812//登录名
    private String realName;//李健//真实姓名
    private String password;//07B8AFD6805BE3E0D16DC5175345272D
    private String sex;//1
    private String phone;//13271139812
    private String province;//省
    private String city;//市
    private String region;//区
    private String address;//详细地址
    private String company;//公司
    private String job;//部门职位
    private String workId;////工号
    private String mail;
    private Date createTime;//ISODate("2017-01-03T03:44:44.667Z")
    private Date updateTime;
    private Integer appType;//1-android，2-ios
    private Integer state;//客服人员状态   1代表 忙碌   0 代表 空闲
    private Integer count;////在服务工单数
    private Integer loginCount;//登录次数
    private Integer used;//账号开启、关闭(0 开启， 1 关闭)
    private String iccid;//e18fd8d1-9d74-49cf-aeee-f26cf762fc27
    private String version;//4.0.1
    private String longitude;//114.064530//经度
    private String latitude;//32.131291//纬度
    private String servicesiteName;//浉河区翼猫体验服务中心

    private String oldSiteId;

}
