package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.AreaDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @description: 区域
 * @author: yu chunlei
 * @create: 2019-02-13 09:39:20
 **/
@Table(name = "system_area")
@Data
public class Area implements Serializable {

    private static final long serialVersionUID = -2140607001965027629L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//	主键
    private String name;//名称
    private Integer level;//级别：1-省或直辖市；2-市；3-区县
    private Integer sorts;//排序
    private Integer pid;//父级id
    private Integer capital;//是否省会：0-否；1-是
    private Boolean deleted;//删除标识：0-否；1-是

    public Area() {
    }

    /**
     * 用业务对象SystemAreaDTO初始化数据库对象SystemArea。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Area(AreaDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.level = dto.getLevel();
        this.sorts = dto.getSorts();
        this.pid = dto.getPid();
        this.capital = dto.getCapital();
        this.deleted = dto.getDeleted();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象SystemAreaDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(AreaDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setLevel(this.level);
        dto.setSorts(this.sorts);
        dto.setPid(this.pid);
        dto.setCapital(this.capital);
        dto.setDeleted(this.deleted);
    }
}
