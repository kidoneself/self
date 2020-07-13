package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.StoreHouseOperationLogDTO;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 库存操作记录
 *
 * @author Liu Long Jie
 * @Date 2020-6-22 09:09:03
 */
@Data
@Table(name = "store_house_operation_log")
public class StoreHouseOperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "`count`")
    private Integer count;//增减数量
    private Integer objectType;//主体类型 1-总仓 2-服务站门店 3-服务站公司
    private Integer operationType;//操作类型:1-后台修改库存 2-服务站公司库存分配至服务站门店
    private String description;//描述信息
    private String operator;//操作人
    private Date createTime;//创建时间


    public StoreHouseOperationLog() {
    }

    /**
     * 用业务对象StoreHouseOperationLogDTO初始化数据库对象StoreHouseOperationLog。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StoreHouseOperationLog(StoreHouseOperationLogDTO dto) {
        this.id = dto.getId();
        this.origObjectId = dto.getOrigObjectId();
        this.origObjectName = dto.getOrigObjectName();
        this.origProvince = dto.getOrigProvince();
        this.origCity = dto.getOrigCity();
        this.origRegion = dto.getOrigRegion();
        this.origAreaId = dto.getOrigAreaId();
        this.objectId = dto.getObjectId();
        this.objectName = dto.getObjectName();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.areaId = dto.getAreaId();
        this.goodsId = dto.getGoodsId();
        this.goodsName = dto.getGoodsName();
        this.origCount = dto.getOrigCount();
        this.destCount = dto.getDestCount();
        this.count = dto.getCount();
        this.objectType = dto.getObjectType();
        this.operationType = dto.getOperationType();
        this.description = dto.getDescription();
        this.operator = dto.getOperator();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StoreHouseOperationLogDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StoreHouseOperationLogDTO dto) {
        dto.setId(this.id);
        dto.setOrigObjectId(this.origObjectId);
        dto.setOrigObjectName(this.origObjectName);
        dto.setOrigProvince(this.origProvince);
        dto.setOrigCity(this.origCity);
        dto.setOrigRegion(this.origRegion);
        dto.setOrigAreaId(this.origAreaId);
        dto.setObjectId(this.objectId);
        dto.setObjectName(this.objectName);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAreaId(this.areaId);
        dto.setGoodsId(this.goodsId);
        dto.setGoodsName(this.goodsName);
        dto.setOrigCount(this.origCount);
        dto.setDestCount(this.destCount);
        dto.setCount(this.count);
        dto.setObjectType(this.objectType);
        dto.setOperationType(this.operationType);
        dto.setDescription(this.description);
        dto.setOperator(this.operator);
        dto.setCreateTime(this.createTime);
    }
}