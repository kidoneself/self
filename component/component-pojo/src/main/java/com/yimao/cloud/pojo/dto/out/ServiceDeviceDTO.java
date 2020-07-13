package com.yimao.cloud.pojo.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/2/18 11:04
 * @Version 1.0
 */
@Data
public class ServiceDeviceDTO implements Serializable {
    private static final long serialVersionUID = -4829011919890027993L;

    private String id;
    private String sncode;//SN码
    private String simcard;//sim卡
    private Boolean actived;//SIM卡是否激活
    private String longitude;//经度
    private String latitude;//纬度
    private String province;//省份
    private String city;//城市
    private String region;//地区
    private String place;//设备摆放地
    private Date activeTime;//激活时间
    private String online;//是否在线
    private String deviceType;//产品类型
    private String deviceScope;//产品范围
    private String deviceModel;//产品型号
    private Integer internetType;//
    private String ontime;//亮屏时间
    private String offtime;//灭屏时间
    private Date lastonlineTime;


}
