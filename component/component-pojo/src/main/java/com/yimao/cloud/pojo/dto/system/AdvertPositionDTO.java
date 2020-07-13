package com.yimao.cloud.pojo.dto.system;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author KID
 * @date 2018/11/13
 */
@Data
public class AdvertPositionDTO implements Serializable {

    private static final long serialVersionUID = -1722572359910872579L;
    private Integer id;
    private String title;       //标题
    private String description; //描述
    private String typeCode;    //标识广告位置   首页轮播图：slideshow
    private Integer conditions; //状态
    private Integer sorts;      //排序
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;
}
