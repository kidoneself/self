package com.yimao.cloud.pojo.dto.system;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 区域DTO
 * @author: yu chunlei
 * @create: 2019-02-13 09:43:45
 **/
@Data
public class SystemAreaDTO implements Serializable {


    private static final long serialVersionUID = 5610708225816172933L;
    private Integer id;//	主键
    private String name;//名称
    private Integer level;//级别：0-中国；1-省或直辖市；2-市；3-区县
    private Integer sorts;//排序
    private Integer pid;//父级id
    private Integer capital;//是否省会：0-否；1-是
    private Integer deleted;//删除标识：0-否；1-是
}
