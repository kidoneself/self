package com.yimao.cloud.pojo.query.product;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 产品查询条件
 *
 * @auther: Zhang Bo
 * @date: 2019/3/15
 */
@Getter
@Setter
public class ProductQuery {

    private String name;
    private List<Integer> ids;    //后端产品级别
    private Integer categoryId;
    private List<Integer> status;
    private Integer hot;
    private Integer frontCategoryId;
    private String supplyCode;          //产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
    private Integer terminal;            //终端：1-健康e家公众号；2-小猫店小程序；3-翼猫APP 4小程序
    private Integer companyId;
    private Integer need;
    private Date startTime;
    private Date endTime;
    private Date onShelfStartTime;
    private Date onShelfEndTime;
    private Integer distributorId;

}
