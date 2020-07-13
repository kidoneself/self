package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.AreaManageDTO;
import lombok.Data;

import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Lizhqiang
 * @date 2019-08-19
 */
@Data
@Table(name = "t_area_manage")
public class AreaManage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer level;
    private Integer numerous;
    private Integer population;
    private Integer premium;
    private Integer directSale;
    private Integer siteCount;
    private Integer pid;

    public AreaManage() {
    }

    /**
     * 用业务对象AreaManageDTO初始化数据库对象AreaManage。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public AreaManage(AreaManageDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.level = dto.getLevel();
        this.numerous = dto.getNumerous();
        this.population = dto.getPopulation();
        this.premium = dto.getPremium();
        this.directSale = dto.getDirectSale();
        this.siteCount = dto.getSiteCount();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象AreaManageDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(AreaManageDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setLevel(this.level);
        dto.setNumerous(this.numerous);
        dto.setPopulation(this.population);
        dto.setPremium(this.premium);
        dto.setDirectSale(this.directSale);
        dto.setSiteCount(this.siteCount);
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AreaManage a=(AreaManage) obj;
		
		if(Objects.isNull(this.getId()) || Objects.isNull(a.getId()) ) {
			return false;
		}
		
		if(this.getId()==a.getId()) {
			return true;
		}else{
			return false;
		}
		
	}


    
}
