package com.yimao.cloud.order.po;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yimao.cloud.pojo.dto.order.StationSalesDTO;
import lombok.Data;

/**
 * @description: 服务站业绩统计
 * @author: liulin
 * @create: 2019-02-22 16:21
 **/
@Data
@Table(name = "report_station_sales")
public class StationSales {
    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //服务站门店id
    private Integer stationId;
    //省份
    private String province;
    //市
    private String city;
    //区
    private String region;
    //销售额
    private BigDecimal salesAccount;
    //排行
    private Integer sort;
    //服务站门店名称
    private String stationName;
    //上一次排名
    private Integer lastSort;
    //点赞数
    private Integer linkCount;
    //连续冠军次数
    private Integer championCount;
    //创建日期
    private Date createTime;
    //批次
    private Integer batchId;


    public StationSales() {
    }

    /**
     * 用业务对象StationSalesDTO初始化数据库对象StationSales。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StationSales(StationSalesDTO dto) {
        this.id = dto.getId();
        this.stationId = dto.getStationId();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.salesAccount = dto.getSalesAccount();
        this.sort = dto.getSort();
        this.stationName = dto.getStationName();
        this.lastSort = dto.getLastSort();
        this.linkCount = dto.getLinkCount();
        this.championCount = dto.getChampionCount();
        this.createTime = dto.getCreateTime();
        this.batchId = dto.getBatchId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StationSalesDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StationSalesDTO dto) {
        dto.setId(this.id);
        dto.setStationId(this.stationId);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setSalesAccount(this.salesAccount);
        dto.setSort(this.sort);
        dto.setStationName(this.stationName);
        dto.setLastSort(this.lastSort);
        dto.setLinkCount(this.linkCount);
        dto.setChampionCount(this.championCount);
        dto.setCreateTime(this.createTime);
        dto.setBatchId(this.batchId);
    }
}
