package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhilin.he
 * @description
 * @date 2019/5/5 9:42
 **/
@ApiModel(description = "云平台库存操作")
@Getter
@Setter
public class StockOperationDTO {

    private Integer id;              //库存操作id
    private String admin;            //管理员
    private Integer operation;       //操作类型
    private String originalProvince; //原库存省
    private String originalCity;     //原库存市
    private String originalRegion;   //原库存区
    private String operateProvince;  //操作库存省
    private String operateCity;      //操作库存市
    private String operateRegion;    //操作库存区
    private String deviceName;       //设备名称
    private Integer count;           //数量
    private Date createTime;         //创建时间

}
