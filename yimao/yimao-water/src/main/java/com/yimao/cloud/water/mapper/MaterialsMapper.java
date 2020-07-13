package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.water.MaterialsDTO;
import com.yimao.cloud.water.po.Materials;
import tk.mybatis.mapper.common.Mapper;

public interface MaterialsMapper extends Mapper<Materials> {
    Page<MaterialsDTO> pageQueryList(MaterialsDTO materialsDTO);
}
