package com.yimao.cloud.pojo.dto.cat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-11-17 11:14:11
 **/
@Data
public class ActivityProductDTO implements Serializable {
    private static final long serialVersionUID = -4129156335740287406L;
    private Integer id;
    private Integer productId;         //产品id
    private String productName;     //产品名称
    private Integer activityRuleId;    //活动规则id
    private Integer activityType;    //活动类型 (1：砍价,2：拼团)
    private BigDecimal crowdPrice;  //保底价
    private Boolean onShelf;        //是否上架
    private Boolean onNew;          //是否新品
    private Boolean onHot;          //是否热门
    private Boolean onGive;         //是否赠送
    private Boolean onShare;        //用户享(不懂什么意思)
    private Boolean newProduct;     //是否推荐
    private Date onShelfTime;       //上架时间
    private Date offShelfTime;      //下架时间
}
