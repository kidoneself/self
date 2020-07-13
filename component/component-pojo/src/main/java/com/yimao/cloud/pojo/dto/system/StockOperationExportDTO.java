package com.yimao.cloud.pojo.dto.system;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhilin.he
 * @description 库存操作日志导出数据
 * @date 2019/5/6 13:52
 **/
@Getter
@Setter
public class StockOperationExportDTO {

    private String admin;            //管理员
    private String operation;        //操作类型
    private String originalProvince; //原库存省
    private String originalCity;     //原库存市
    private String originalRegion;   //原库存区
    private String operateProvince;  //操作库存省
    private String operateCity;      //操作库存市
    private String operateRegion;    //操作库存区
    private String deviceName;       //设备名称
    private Integer count;           //数量
    private String createTime;       //创建时间
}
