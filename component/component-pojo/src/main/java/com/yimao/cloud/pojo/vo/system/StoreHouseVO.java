package com.yimao.cloud.pojo.vo.system;

/**
 * @author zhilin.he
 * @date 2019/5/5 10:14
 **/

import com.yimao.cloud.pojo.dto.system.StockCountDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ApiModel(description = "云平台仓库VO对象")
public class StoreHouseVO {

    @ApiModelProperty(value = "库存id")
    private Integer id;
    @ApiModelProperty(value = "产库位置：1.总仓，0.分仓")
    private String place;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区")
    private String region;
    @ApiModelProperty(value = "水机产品型号库存集合")
    private List<StockCountDTO> counts;
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    public StoreHouseVO() {
    }

    public StoreHouseVO(Integer id, String place, String province, String city, String region, List<StockCountDTO> counts, Date updateTime) {
        this.id = id;
        this.place = place;
        this.province = province;
        this.city = city;
        this.region = region;
        this.counts = counts;
        this.updateTime = updateTime;
    }

}
