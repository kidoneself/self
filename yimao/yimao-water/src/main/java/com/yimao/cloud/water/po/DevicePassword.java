package com.yimao.cloud.water.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：设备密码
 *
 * @Author Zhang Bo
 * @Date 2019/2/25 15:38
 * @Version 1.0
 */
@Table(name = "water_device_password")
@Data
public class DevicePassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String pwd;
    private String pwdmd5;

    private String creator;//创建人
    private Date createTime;//创建时间
    private String updater;//更新人
    private Date updateTime;//更新时间

}
