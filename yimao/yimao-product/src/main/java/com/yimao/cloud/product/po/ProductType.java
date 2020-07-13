package com.yimao.cloud.product.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 产品二级分类。
 *
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@Table(name = "product_type")
@Data
public class ProductType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;//产品二级分类名称
    private Integer type;//类目：1-后台类目；2-前台类目
    private Integer categoryId;//产品一级分类id
    private Boolean deleted;//是否删除

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

}