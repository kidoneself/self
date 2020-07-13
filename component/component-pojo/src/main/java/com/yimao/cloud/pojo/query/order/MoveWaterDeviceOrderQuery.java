package com.yimao.cloud.pojo.query.order;

import com.yimao.cloud.pojo.query.station.BaseQuery;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 移机工单列表筛选条件
 *
 * @author Liu Long Jie
 * @date 2020-7-8 09:13:26
 */
@Data
public class MoveWaterDeviceOrderQuery extends BaseQuery {

    private String id;
    //移机状态 1-待拆机；2-拆机中；3-待移入；4-移入中；5-已完成
    private Integer status;
    //水机设备型号
    private String deviceModel;
    //设备用户姓名
    private String deviceUserName;
    //设备用户联系方式
    private String deviceUserPhone;
    //经销商姓名
    private String distributorName;
    //设备拆机地省
    private String origProvince;
    //设备拆机地市
    private String origCity;
    //设备拆机地区
    private String origRegion;
    //移入地省
    private String destProvince;
    //移入地市
    private String destCity;
    //移入地区
    private String destRegion;
    //拆机挂单状态 0-未挂单；1-挂单
    private Integer dismantleHangUpStatus;
    //装机挂单状态 0-未挂单；1-挂单
    private Integer installHangUpStatus;
    //创建时间（开始）
    private Date createTimeEnd;
    //创建时间（结束）
    private Date createTimeStart;

    private List<Integer> engineerIds;

}
