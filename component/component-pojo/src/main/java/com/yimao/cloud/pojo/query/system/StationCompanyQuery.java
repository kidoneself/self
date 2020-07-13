package com.yimao.cloud.pojo.query.system;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：区县级公司查询条件
 *
 * @Author Zhang Bo
 * @Date 2019/3/22
 */
@Getter
@Setter
public class StationCompanyQuery  implements Serializable {
    private static final long serialVersionUID = 2430339047489701755L;

    private Integer id;
    private String name;
    private String contact;
    private String contactPhone;
    private Integer areaId;
    private String province;
    private String city;
    private String region;
    private String locationProvince;
    private String locationCity;
    private String locationRegion;
    private Integer type;
    private Integer signUp;
    private Integer online;
    private Date startTime;
    private Date endTime;
    private Integer serviceType; // 服务类型 0-售前+售后；1-售前；2-售后

}
