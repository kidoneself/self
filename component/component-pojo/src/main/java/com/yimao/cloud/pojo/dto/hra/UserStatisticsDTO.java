package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户统计dto
 * created by liuhao@yimaokeji.com
 * 2018042018/4/21
 */
@Data
public class UserStatisticsDTO implements Serializable {
    private static final long serialVersionUID = -496041909602963897L;

    //新增绑定经销商总数  日期-总数
    private String disBindTime;             //绑定日期
    private Integer disBindCount;           //日期内总绑定数
    //经销商绑定总数   省-绑定数-经销商占比
    private String disBindProvince;         //省
    private String disBindProvinceCount;    //省绑定数
    private String disBindPencent;          //绑定比例

}
