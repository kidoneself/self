package com.yimao.cloud.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.GoodsCategoryDTO;
import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;
import com.yimao.cloud.pojo.query.system.GoodsCategoryQuery;
import com.yimao.cloud.pojo.query.system.GoodsMaterialsQuery;
import com.yimao.cloud.system.po.AreaManage;
import com.yimao.cloud.system.po.GoodsCategory;

import tk.mybatis.mapper.common.Mapper;

public interface GoodsCategoryMapper extends Mapper<GoodsCategory> {

	int selectMaxSortsValue(@Param("type") Integer type,@Param("level") Integer level);

	Page<GoodsCategoryDTO> listGoodsCategory(GoodsCategoryQuery query);

	List<GoodsCategoryDTO> getGoodsCategoryFilter(@Param("type") Integer type);

}