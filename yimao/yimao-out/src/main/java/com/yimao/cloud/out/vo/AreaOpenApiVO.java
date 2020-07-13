package com.yimao.cloud.out.vo;

import lombok.Data;

/**
 * 描述：省市区
 *
 * @Author Zhang Bo
 * @Date 2019/2/18 13:45
 * @Version 1.0
 */

@Data
public class AreaOpenApiVO  {

    private String aId;     //地区id
    private String pId;     //父id
    private String name;   //名称
}
