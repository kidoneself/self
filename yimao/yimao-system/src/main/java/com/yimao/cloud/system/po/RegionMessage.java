package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.RegionMessageDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 区域管理
 *
 * @author Lizhqiang
 * @date 2019/1/16
 */
@Table(name = "t_region_message")
@Data
public class RegionMessage {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;   //主键
    private String areaName;//地域名
    private Integer level;//等级
    private Integer sorts;//排序
    private Integer parentId;//父id
    private Double population;    //人口数
    private Integer crowdFunding; //众筹数


    public RegionMessage() {
    }

    /**
     * 用业务对象RegionMessageDTO初始化数据库对象RegionMessage。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public RegionMessage(RegionMessageDTO dto) {
        this.id = dto.getId();
        this.areaName = dto.getAreaName();
        this.level = dto.getLevel();
        this.sorts = dto.getSorts();
        this.parentId = dto.getParentId();
        this.population = dto.getPopulation();
        this.crowdFunding = dto.getCrowdFunding();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象RegionMessageDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(RegionMessageDTO dto) {
        dto.setId(this.id);
        dto.setAreaName(this.areaName);
        dto.setLevel(this.level);
        dto.setSorts(this.sorts);
        dto.setParentId(this.parentId);
        dto.setPopulation(this.population);
        dto.setCrowdFunding(this.crowdFunding);
    }
}
