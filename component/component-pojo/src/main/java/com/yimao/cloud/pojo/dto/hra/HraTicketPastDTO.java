package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;

/**
 * created by liuhao@yimaokeji.com
 * 2018/9/5
 */
@Data
public class HraTicketPastDTO implements Serializable {
    private static final long serialVersionUID = 2186267572118602906L;

    private Integer userId;    //用户id
    private Integer counts; //过期数量
    private String openId;  //用户的openId
    private String stationName; //服务站名称
    private Integer stationId;  //服务站Id

}
