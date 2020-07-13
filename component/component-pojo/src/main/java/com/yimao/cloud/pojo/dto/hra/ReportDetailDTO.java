package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 体检报告详情 数据封装
 * @author: yu chunlei
 * @create: 2018-05-10 11:12:51
 **/
@Data
public class ReportDetailDTO implements Serializable {

    private static final long serialVersionUID = -5052510418849777623L;
    private String username;//名字
    private String sex;//性别
    private String height;//身高
    private String weight;//体重
    private String birthdate;//出生日期
    private Date examDate;//体检日期
    private String reportPdf;

    private String pic2;//耳鼻喉
    private String pic3;//骨骼
    private String pic4;//呼吸
    private String pic5;//泌尿生殖
    private String pic6;//神经
    private String pic7;//消化
    private String pic8;//心血管

    private HraReportJsonDTO reportJsonDTO;

}
