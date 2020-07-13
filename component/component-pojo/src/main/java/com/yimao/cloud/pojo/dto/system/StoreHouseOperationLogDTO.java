package com.yimao.cloud.pojo.dto.system;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 库存操作记录
 *
 * @author Liu Long Jie
 * @Date 2020-6-22 09:09:03
 */
@Data
public class StoreHouseOperationLogDTO implements Serializable {

    private static final long serialVersionUID = -1023570402223483089L;

    private Integer id;
    private Integer origObjectId;//原对象id
    private String origObjectName;//原对象名称
    private String origProvince;//原对象所在地省
    private String origCity;//原对象所在地市
    private String origRegion;//原对象所在地区
    private Integer origAreaId;//原对象所在区域id
    private Integer objectId;//目标对象id
    private String objectName;//目标对象名称
    private String province;//所在地省
    private String city;//所在地市
    private String region;//所在地区
    private Integer areaId;//所在区域id
    private Integer goodsId;//物品id
    private String goodsName;//物品名称
    private Integer origCount;//修改前库存数量
    private Integer destCount;//修改后库存数量
    private Integer count;//增减数量
    private Integer objectType;//主体类型 1-总仓 2-服务站门店 3-服务站公司
    private Integer operationType;//操作类型:1-后台修改库存 2-服务站公司库存分配至服务站门店
    private String description;//描述信息
    private String operator;//操作人
    private Date createTime;//创建时间
}