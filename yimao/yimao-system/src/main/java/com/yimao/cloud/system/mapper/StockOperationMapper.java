package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.StockOperationExportDTO;
import com.yimao.cloud.system.po.StockOperation;
import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

/**
 * @author zhilin.he
 * @description
 * @date 2019/4/30 16:06
 **/
public interface StockOperationMapper  extends Mapper<StockOperation> {

    Page<StockOperationExportDTO> stockOperationExport(Map<String,Object> map);
}
