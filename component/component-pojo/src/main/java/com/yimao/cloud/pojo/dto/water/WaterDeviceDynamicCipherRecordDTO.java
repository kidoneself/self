package com.yimao.cloud.pojo.dto.water;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：水机设备动态密码
 *
 * @Author Zhang Bo
 * @Date 2019/3/12
 */
@Getter
@Setter
public class WaterDeviceDynamicCipherRecordDTO {

    private Integer id;
    private String sn;                //SN码
    private String password;          //明文密码
    private String passwordDesStr;       //加密密码
    private Integer engineerId;       //安装工程师ID
    private String engineerName;      //安装工程师姓名
    private String engineerPhone;     //安装工程师手机号
    private String validStatus;       //校验状态：Y-可用；N-不可用
    private Date validTime;           //最后在线时间
    private String terminal;          //创建端
    private Date createTime;          //最后在线时间

}
