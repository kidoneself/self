package com.yimao.cloud.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;
import com.yimao.cloud.pojo.query.system.GoodsMaterialsQuery;
import com.yimao.cloud.system.po.GoodsCategory;
import com.yimao.cloud.system.po.GoodsMaterials;

import tk.mybatis.mapper.common.Mapper;

public interface GoodsMaterialsMapper extends Mapper<GoodsMaterials> {

	Page<GoodsMaterialsDTO> listMaterials(GoodsMaterialsQuery query);

	Page<GoodsMaterialsDTO> listGoods(GoodsMaterialsQuery query);

	int checkRename(@Param("type")Integer type,@Param("name")String name, @Param("id")Integer id);

	GoodsMaterialsDTO selectGoodsById(Integer id);

	List<GoodsMaterialsDTO> getMaterialListByCategoryId(Integer productCategoryId);

	List<GoodsMaterialsDTO> getGoodsByCategoryId(Integer goodCategoryId);

}