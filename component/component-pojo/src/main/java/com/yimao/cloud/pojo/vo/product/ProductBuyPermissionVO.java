package com.yimao.cloud.pojo.vo.product;

import lombok.Getter;
import lombok.Setter;

/**
 * 服务内部DTO
 *
 * @author liulin
 * @date 2018/12/19
 */
@Getter
@Setter
public class ProductBuyPermissionVO {

    private String name;  //角色名字
    private String code;  //角色code
    private Integer sorts;//排序
    private Integer group;//隶属组

    public ProductBuyPermissionVO() {
    }

    public ProductBuyPermissionVO(String name, String code, Integer group, Integer sorts) {
        this.name = name;
        this.code = code;
        this.group = group;
        this.sorts = sorts;
    }

}