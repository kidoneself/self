package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.MaterialsDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.Materials;

import java.util.Map;

public interface MaterialsService {

    /**
     * 创建物料信息
     */
    void save(Materials materials);

    PageVO<MaterialsDTO> list(MaterialsDTO materialsDTO, Integer pageNum, Integer pageSize);

    void deleteMaterials(Materials materials);

    void auditMaterials( Materials materials);

    Map<String, Object> queryCount();
}
