package com.yimao.cloud.system.po;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yimao.cloud.pojo.dto.system.StationCompanyStoreHouseDTO;
import lombok.Data;

@Data
@Table(name = "station_company_store_house")
public class StationCompanyStoreHouse {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer stationCompanyId;

    private Integer goodsId;

    private Integer stockCount;


    public StationCompanyStoreHouse() {
    }

    /**
     * 用业务对象StationCompanyStoreHouseDTO初始化数据库对象StationCompanyStoreHouse。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StationCompanyStoreHouse(StationCompanyStoreHouseDTO dto) {
        this.id = dto.getId();
        this.stationCompanyId = dto.getStationCompanyId();
        this.goodsId = dto.getGoodsId();
        this.stockCount = dto.getStockCount();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StationCompanyStoreHouseDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StationCompanyStoreHouseDTO dto) {
        dto.setId(this.id);
        dto.setStationCompanyId(this.stationCompanyId);
        dto.setGoodsId(this.goodsId);
        dto.setStockCount(this.stockCount);
    }
}