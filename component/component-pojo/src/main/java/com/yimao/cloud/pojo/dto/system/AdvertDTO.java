package com.yimao.cloud.pojo.dto.system;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author KID
 * @date 2018/11/12
 */
@Data
public class AdvertDTO implements Serializable {


    private static final long serialVersionUID = 116735308423646353L;
    private Integer id;
    private String name;  //名称
    private Integer apId;      //广告位编号
    private String title;   //标题
    private String content; //内容
    private String url;     //url
    private String adImg;   //图片
    private Integer conditions;//状态
    private Integer sorts;  //排序
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;


}
