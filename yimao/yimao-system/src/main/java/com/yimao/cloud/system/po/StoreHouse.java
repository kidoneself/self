package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.StoreHouseDTO;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zhilin.he
 * @description 云平台仓库
 * @date 2019/4/30 15:33
 **/
@Table(name = "store_house")
@Getter
@Setter
public class StoreHouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer place;   //位置
    private String province; //省
    private String city;     //市
    private String region;   //区
    private String stocks;   //库存数组
    private Integer special; //是否特殊库存：0-否，1-是
    private String maxValues;//最大值数组
    private Date createTime; //创建时间
    private Date updateTime; //修改时间


    public StoreHouse() {
    }

    /**
     * 用业务对象StoreHouseDTO初始化数据库对象StoreHouse。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StoreHouse(StoreHouseDTO dto) {
        this.id = dto.getId();
        this.place = dto.getPlace();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.stocks = dto.getStocks();
        this.special = dto.getSpecial();
        this.maxValues = dto.getMaxValues();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StoreHouseDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StoreHouseDTO dto) {
        dto.setId(this.id);
        dto.setPlace(this.place);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setStocks(this.stocks);
        dto.setSpecial(this.special);
        dto.setMaxValues(this.maxValues);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}
