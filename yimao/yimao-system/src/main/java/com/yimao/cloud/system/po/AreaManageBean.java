package com.yimao.cloud.system.po;

import lombok.Data;

/**
 * @author Lizhqiang
 * @date 2019-08-20
 */
@Data
public class AreaManageBean {


    private String province;
    private String city;
    private String region;
    private Integer level;
    private Integer population;
    private Integer numerous;
    private Integer premium;
    private Integer directSale;
    private Integer siteCount;
}
