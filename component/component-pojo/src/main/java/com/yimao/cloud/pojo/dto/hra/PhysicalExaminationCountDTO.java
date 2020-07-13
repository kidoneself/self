package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;

@Data
public class PhysicalExaminationCountDTO implements Serializable{

    private static final long serialVersionUID = 8028728905664993154L;
    private String date;
    private String province;        //省
    private Integer stationId;         //服务站id
    private String stationArea;     //服务站地区
    private String stationName;     //服务站名称
    private Integer usedCount;      //体检人数(已使用)
    private Integer reverseCount;   //预约体检人数
    private Double ratio;
    private Double usedRatio;       //体检比例
    private Double reverseRatio;    //预约比例

}
