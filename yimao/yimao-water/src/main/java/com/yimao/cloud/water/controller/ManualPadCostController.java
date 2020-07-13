package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.water.ManualPadCostDTO;
import com.yimao.cloud.pojo.export.water.ManualPadCostExport;
import com.yimao.cloud.pojo.query.water.ManualPadCostQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.ManualPadCostVO;
import com.yimao.cloud.water.po.ManualPadCost;
import com.yimao.cloud.water.service.ManualPadCostService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述：手动修改水机配额
 *
 * @Author Zhang Bo
 * @Date 2019/5/7
 */
@RestController
public class ManualPadCostController {

    @Resource
    private ManualPadCostService manualPadCostService;

    /**
     * 创建手动修改水机配额
     *
     * @param dto dto
     */
    @PostMapping(value = "/padcost")
    public void save(@RequestBody ManualPadCostDTO dto) {
        ManualPadCost manualPadCost = new ManualPadCost(dto);
        manualPadCostService.save(manualPadCost);
    }

    /**
     * 修改手动修改水机配额
     *
     * @param dto dto
     */
    @PutMapping(value = "/padcost")
    public void update(@RequestBody ManualPadCostDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("操作对象ID不能为空。");
        }
        ManualPadCost manualPadCost = new ManualPadCost(dto);
        manualPadCostService.update(manualPadCost);
    }

    /**
     * 查询手动修改水机配额（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/padcost/{pageNum}/{pageSize}")
    public PageVO<ManualPadCostVO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                        @RequestBody ManualPadCostQuery query) {
        return manualPadCostService.page(pageNum, pageSize, query);
    }

    /**
     * 水机物联-设备金额管理-导出
     */
    @PostMapping(value = "/padcost/export")
    public List<ManualPadCostExport> export(@RequestBody ManualPadCostQuery query) {
        return manualPadCostService.export(query);
    }

}
