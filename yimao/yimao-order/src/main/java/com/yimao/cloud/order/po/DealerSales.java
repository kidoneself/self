package com.yimao.cloud.order.po;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yimao.cloud.pojo.dto.order.DealerSalesDTO;
import lombok.Data;

/**
 *
 * @description: 经销商业绩统计
 * @author: liulin
 * @create: 2019-02-22 16:21
 **/
@Data
@Table(name = "report_dealer_sales")
public class DealerSales {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //  经销商ID
    private Integer dealerId;
    //销售额
    private BigDecimal salesAccount;
    // 我的全国排行
    private Integer sort;
    //经销商名称
    private String dealerName;
    //上一次排名
    private Integer lastSort;
    // 点赞数
    private Integer linkCount;
    // 连续冠军次数
    private Integer championCount;
    // 服务站ID
    private Integer stationId;
    //批次
    private Integer batchId;
    //创建日期
    private Date createTime;


    public DealerSales() {
    }

    /**
     * 用业务对象DealerSalesDTO初始化数据库对象DealerSales。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public DealerSales(DealerSalesDTO dto) {
        this.id = dto.getId();
        this.dealerId = dto.getDealerId();
        this.salesAccount = dto.getSalesAccount();
        this.sort = dto.getSort();
        this.dealerName = dto.getDealerName();
        this.lastSort = dto.getLastSort();
        this.linkCount = dto.getLinkCount();
        this.championCount = dto.getChampionCount();
        this.stationId = dto.getStationId();
        this.createTime = dto.getCreateTime();
        this.batchId = dto.getBatchId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象DealerSalesDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(DealerSalesDTO dto) {
        dto.setId(this.id);
        dto.setDealerId(this.dealerId);
        dto.setSalesAccount(this.salesAccount);
        dto.setSort(this.sort);
        dto.setDealerName(this.dealerName);
        dto.setLastSort(this.lastSort);
        dto.setLinkCount(this.linkCount);
        dto.setChampionCount(this.championCount);
        dto.setStationId(this.stationId);
        dto.setCreateTime(this.createTime);
        dto.setBatchId(this.batchId);
    }
}
