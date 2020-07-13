package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.export.water.ManualPadCostExport;
import com.yimao.cloud.pojo.query.water.ManualPadCostQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.ManualPadCostVO;
import com.yimao.cloud.water.po.ManualPadCost;

import java.util.List;

public interface ManualPadCostService {

    /**
     * 创建手动修改水机配额
     *
     * @param record 手动修改水机配额
     */
    void save(ManualPadCost record);

    /**
     * 修改手动修改水机配额
     *
     * @param record 手动修改水机配额
     */
    void update(ManualPadCost record);

    /**
     * 查询手动修改水机配额（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    PageVO<ManualPadCostVO> page(Integer pageNum, Integer pageSize, ManualPadCostQuery query);

    /**
     * 查询手动修改水机配额
     *
     * @param sncode SN码
     */
    List<ManualPadCost> listBySn(String sncode);

    List<ManualPadCostExport> export(ManualPadCostQuery query);
}
