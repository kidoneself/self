package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-11-21 10:23:56
 **/
@Data
public class HraBodyElementDTO implements Serializable {

    private static final long serialVersionUID = 2998204316852081853L;
    private Integer id;
    private String elementName;
    private String elementContext;
    private String elementImg;
    private Date createTime;
    private String creator;
}
