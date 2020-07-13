package com.yimao.cloud.system.po;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yimao.cloud.pojo.dto.system.StationStoreHouseDTO;
import lombok.Data;

@Data
@Table(name = "station_store_house")
public class StationStoreHouse {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer stationId;

    private Integer goodsId;

    private Integer stockCount;

    private Integer occupyStockCount;

    private Integer defectiveStockCount;


    public StationStoreHouse() {
    }

    /**
     * 用业务对象StationStoreHouseDTO初始化数据库对象StationStoreHouse。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StationStoreHouse(StationStoreHouseDTO dto) {
        this.id = dto.getId();
        this.stationId = dto.getStationId();
        this.goodsId = dto.getGoodsId();
        this.stockCount = dto.getStockCount();
        this.defectiveStockCount = dto.getDefectiveStockCount();
        this.occupyStockCount = dto.getOccupyStockCount();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StationStoreHouseDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StationStoreHouseDTO dto) {
        dto.setId(this.id);
        dto.setStationId(this.stationId);
        dto.setGoodsId(this.goodsId);
        dto.setStockCount(this.stockCount);
        dto.setDefectiveStockCount(this.defectiveStockCount);
        dto.setOccupyStockCount(this.occupyStockCount);
    }
}