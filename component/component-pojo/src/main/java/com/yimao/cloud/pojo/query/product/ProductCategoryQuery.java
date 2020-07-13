package com.yimao.cloud.pojo.query.product;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：产品类目查询条件
 *
 * @Author Zhang Bo
 * @Date 2019/3/18
 */
@Getter
@Setter
public class ProductCategoryQuery {

    private String name;                    //产品类目名称
    private Integer type;                   //前台类目还是后台类目：1-后台类目；2-前台类目；
    private Integer terminal;               //终端：1-健康e家公众号；2-小猫店小程序；3-经销商APP；
    private Integer pid;                    //父类目id，当id=0时说明是根节点，一级类目
    private Integer level;                  //产品类目等级：1-一级；2-二级；3-三级；
    private Integer companyId;              //产品公司ID
    private Date startTime;                 //开始时间
    private Date endTime;                   //结束时间

}
