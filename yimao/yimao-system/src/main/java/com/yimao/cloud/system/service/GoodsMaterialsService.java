package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;
import com.yimao.cloud.pojo.query.system.GoodsMaterialsQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.GoodsMaterials;

public interface GoodsMaterialsService {

	void saveGoodsMaterials(GoodsMaterialsDTO goodsMaterialsDTO);

	PageVO<GoodsMaterialsDTO> listGoodsMaterials(Integer pageNum, Integer pageSize, Integer type,
			GoodsMaterialsQuery query);

	void updateGoodsMaterials(GoodsMaterialsDTO goodsMaterialsDTO);

}
