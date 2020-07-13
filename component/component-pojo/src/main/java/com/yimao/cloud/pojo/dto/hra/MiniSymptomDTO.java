package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 症状数据 封装
 * @author: yu chunlei
 * @create: 2018-05-23 14:53:50
 **/
@Data
public class MiniSymptomDTO implements Serializable {

    private static final long serialVersionUID = 7343453601285461697L;
    private Integer id;
    private Integer parentId;
    private Integer secondId;
    private Integer hotFlag;//0：非热门 1：热门
    private String symptomName;
    private String symptomIntro;//症状介绍
    private String symptomDetail;//症状详情
    private String zhengzhuangIds;//症状集合
    private String jibingIds;//疾病集合
    private String spIcon;//icon路径

    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;

}
