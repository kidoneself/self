package com.yimao.cloud.system.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：APP版本更新
 *
 * @Author Zhang Bo
 * @Date 2019/11/8
 */
@Table(name = "new_app_version")
@Getter
@Setter
public class NewAppVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //版本号
    private Integer version;
    //版本名称
    private String versionName;
    //版本说明
    private String versionDesc;
    //系统类型：0-Android；1-IOS；
    private Integer systemType;
    //系统类型：0-Android；1-IOS；
    private String systemTypeName;
    //app类型：0-翼猫服务；经销商管理；
    private Integer appType;
    //app类型：0-翼猫服务；经销商管理；
    private String appTypeName;
    //是否弹框(默认:不弹框)
    private Boolean popout = false;
    //是否强制升级(默认:不强制)
    private Boolean forceUpdate = false;
    //外部链接.例如:ios App指向appStore的地址
    private String outLink;

    private Date createTime;
    private Date updateTime;

}
