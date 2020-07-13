package com.yimao.cloud.user.po;

import java.util.Date;

import javax.persistence.Table;

import com.yimao.cloud.pojo.dto.user.EngineerServiceAreaDTO;
import lombok.Data;

/***
 * 安装工服务区域关系信息
 * @author zhangbaobao
 *
 */
@Table(name = "user_engineer_service_area")
@Data
public class EngineerServiceArea {

	private Integer engineerId;//安装工id
	private Integer areaId;//服务区域id
	private String creator;//创建者
	private Date createTime;//创建时间
	private String province;//省
	private String city;//市
	private String region;//区


	public EngineerServiceArea() {
	}

	/**
	 * 用业务对象EngineerServiceAreaDTO初始化数据库对象EngineerServiceArea。
	 * plugin author ylfjm.
	 *
	 * @param dto 业务对象
	 */
	public EngineerServiceArea(EngineerServiceAreaDTO dto) {
		this.engineerId = dto.getEngineerId();
		this.areaId = dto.getAreaId();
		this.creator = dto.getCreator();
		this.createTime = dto.getCreateTime();
		this.province = dto.getProvince();
		this.city = dto.getCity();
		this.region = dto.getRegion();
	}

	/**
	 * 将数据库实体对象信息拷贝到业务对象EngineerServiceAreaDTO上。
	 * plugin author ylfjm.
	 *
	 * @param dto 业务对象
	 */
	public void convert(EngineerServiceAreaDTO dto) {
		dto.setEngineerId(this.engineerId);
		dto.setAreaId(this.areaId);
		dto.setCreator(this.creator);
		dto.setCreateTime(this.createTime);
		dto.setProvince(this.province);
		dto.setCity(this.city);
		dto.setRegion(this.region);
	}
}
