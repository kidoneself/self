package com.yimao.cloud.system.po;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Data
@Table(name = "goods_category")
public class GoodsCategory {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Integer type;

    private Integer level;

    private Integer pid;

    private Integer sorts;

    private String creator;

    private Date createTime;

    private String updater;

    private Date updateTime;


}