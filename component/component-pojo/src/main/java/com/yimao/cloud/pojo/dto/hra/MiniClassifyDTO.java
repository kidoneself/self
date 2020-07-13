package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-04-24 11:48:34
 **/
@Data
public class MiniClassifyDTO implements Serializable {

    private static final long serialVersionUID = 6509291589696977301L;
    private Integer id;
    private String classifyName;//分类名称
    private String classifySign;//标识：1：健康评测 2：症状自查
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;
}
