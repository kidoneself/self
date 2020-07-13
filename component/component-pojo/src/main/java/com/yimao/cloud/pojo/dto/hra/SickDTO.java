package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-05-24 10:56:40
 **/
@Data
public class SickDTO implements Serializable {

    private static final long serialVersionUID = -6622377586297795790L;
    private Integer sickId;
    private String sickName;

}
