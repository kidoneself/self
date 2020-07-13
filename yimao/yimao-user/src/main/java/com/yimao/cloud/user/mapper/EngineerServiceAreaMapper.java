package com.yimao.cloud.user.mapper;

import java.util.List;

import com.yimao.cloud.pojo.dto.user.EngineerServiceAreaDTO;
import com.yimao.cloud.user.po.EngineerServiceArea;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface EngineerServiceAreaMapper extends Mapper<EngineerServiceArea> {
	
	/***
	 * 批量保存安装工服务区域关系信息
	 * @param list
	 */
	public void batchInsert(List<EngineerServiceArea> list);
	
	/***
	 * 根据安装工id删除关系
	 * @param engineerId
	 * @return
	 */
	public int deleteByEngineerId(@Param("engineerId") Integer engineerId);

    int deleteByAreaId(@Param("areaId") Integer areaId);

	public List<EngineerServiceAreaDTO> selectAreaListByEngineerId(@Param("engineerId") Integer engineerId);
}
