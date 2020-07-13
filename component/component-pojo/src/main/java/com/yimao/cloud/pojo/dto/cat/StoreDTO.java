package com.yimao.cloud.pojo.dto.cat;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-11-15 13:39:06
 **/
@Data
public class StoreDTO implements Serializable {

    private static final long serialVersionUID = -7419948985319896341L;
    private Integer id;
    private String storeName;       //店铺名称
    private String storeBackcolor;  //背景颜色
    private String storeIcon;
    private String storeSecretary;  //文案
    private String storeBadge;      //徽章
    private Date createTime;
    private Date updateTime;
    private Integer userId;        //用户id
    private Integer themeId;       //主题id
    private String qrCode;      //二维码
    private Boolean onLine;     //是否启用  1:是  0 ：否
    private Boolean hot_store;  //是否热门
    private Integer clickRate;  //点击数

}
