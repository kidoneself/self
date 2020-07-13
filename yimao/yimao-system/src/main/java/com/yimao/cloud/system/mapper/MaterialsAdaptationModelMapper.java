package com.yimao.cloud.system.mapper;

import java.util.List;

import com.yimao.cloud.system.po.MaterialsAdaptationModel;

import tk.mybatis.mapper.common.Mapper;

public interface MaterialsAdaptationModelMapper extends Mapper<MaterialsAdaptationModel>{

	int batchInsert(List<MaterialsAdaptationModel> adaptationModelList);

}