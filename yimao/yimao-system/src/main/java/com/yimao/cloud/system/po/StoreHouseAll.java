package com.yimao.cloud.system.po;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yimao.cloud.pojo.dto.system.StoreHouseAllDTO;
import lombok.Data;

@Data
@Table(name = "store_house_all")
public class StoreHouseAll {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer goodsId;

    private Integer stockCount;


    public StoreHouseAll() {
    }

    /**
     * 用业务对象StoreHouseAllDTO初始化数据库对象StoreHouseAll。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StoreHouseAll(StoreHouseAllDTO dto) {
        this.id = dto.getId();
        this.goodsId = dto.getGoodsId();
        this.stockCount = dto.getStockCount();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StoreHouseAllDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StoreHouseAllDTO dto) {
        dto.setId(this.id);
        dto.setGoodsId(this.goodsId);
        dto.setStockCount(this.stockCount);
    }
}