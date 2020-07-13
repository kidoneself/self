package com.yimao.cloud.pojo.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 描述：水机设备查询条件
 *
 * @Author Zhang Bo
 * @Date 2019/1/31 10:37
 * @Version 1.0
 */
@Data
public class DeviceQuery implements Serializable {

    private static final long serialVersionUID = -8232974765927462455L;

    private List<Map<String,String>> pcr;//省市区集合
    private List<String> models;//型号
    private Date lastOnlineBeginTime;//设备在线开始时间
    private Date lastOnlineEndTime;//设备在线结束时间
    private Date createTime;//设备创建时间
    private Integer connectionType;//网络连接类型
    private String keyWord;   //关键字
    private Boolean online;   //是否在线
    private String location; //位置标签
    private String snCode; //设备编码

}
