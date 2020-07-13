package com.yimao.cloud.pojo.query.product;

import com.yimao.cloud.pojo.dto.product.ProductPropertyValueDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 产品属性VO对象
 *
 * @auther: Zhang Bo
 * @date: 2019/3/14
 */
@Getter
@Setter
public class ProductPropertyQuery {

    private String name;                //属性名称
    private Integer typeId;             //产品大类：1-实物商品；2-电子卡券；3-租赁商品；
    private Date startUpdateTime;       //开始时间
    private Date endUpdateTime;         //结束时间

}