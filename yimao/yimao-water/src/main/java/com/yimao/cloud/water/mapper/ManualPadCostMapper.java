package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.export.water.ManualPadCostExport;
import com.yimao.cloud.pojo.query.water.ManualPadCostQuery;
import com.yimao.cloud.pojo.vo.water.ManualPadCostVO;
import com.yimao.cloud.water.po.ManualPadCost;
import tk.mybatis.mapper.common.Mapper;

public interface ManualPadCostMapper extends Mapper<ManualPadCost> {

    /**
     * 查询手动修改水机配额（分页）
     */
    Page<ManualPadCostVO> list(ManualPadCostQuery query);

    Page<ManualPadCostExport> export(ManualPadCostQuery query);
}
