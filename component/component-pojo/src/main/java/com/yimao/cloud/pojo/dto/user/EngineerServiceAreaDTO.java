package com.yimao.cloud.pojo.dto.user;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/***
 * 安装工服务区域关系信息
 * @author zhangbaobao
 *
 */
@SuppressWarnings("serial")
@Data
public class EngineerServiceAreaDTO implements Serializable {
	
	private Integer engineerId;//安装工id
	private Integer areaId;//服务区域id
	private String creator;//创建者
	private Date createTime;//创建时间
	private String province;//省
	private String city;//市
	private String region;//区

}
