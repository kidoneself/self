package com.yimao.cloud.pojo.query.product;

import lombok.Getter;
import lombok.Setter;

/**
 * 计费方式查询条件
 *
 * @auther: Zhang Bo
 * @date: 2019/3/15
 */
@Getter
@Setter
public class ProductCostQuery {

    private String name;                    //计费方式名称
    private Integer type;                   //计费方式：1-流量计费；2-时长计费；
    private Integer modelType;              //模板类型：1-首年 2-续费
    private Integer firstCategoryId;        //产品一级类目ID（后台类目）
    private Integer secondCategoryId;       //产品二级类目ID（后台类目）

    private Integer productId;              //产品ID
    private Integer deleted;

}
