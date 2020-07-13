package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yimao.cloud.base.utils.CopyUtil;
import com.yimao.cloud.pojo.dto.system.StockOperationDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.StockOperationMapper;
import com.yimao.cloud.system.po.StockOperation;
import com.yimao.cloud.system.service.StockOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zhilin.he
 * @description 云平台库存操作记录
 * @date 2019/5/6 10:36
 **/
@Service
@Slf4j
public class StockOperationServiceImpl implements StockOperationService {

    @Resource
    private StockOperationMapper stockOperationMapper;

    /**
     * 分页查询库存操作记录列表
     */
    @Override
    public PageVO<StockOperationDTO> getStockOperationList(StockOperation stockOperation, Date startTime, Date endTime, Integer pageNum, Integer pageSize) {
        PageVO<StockOperationDTO> pageVO = new PageVO<>();
        Example example = new Example(StockOperation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("operation", stockOperation.getOperation());
        criteria.andEqualTo("originalProvince", stockOperation.getOriginalProvince());
        criteria.andEqualTo("originalCity", stockOperation.getOriginalCity());
        criteria.andEqualTo("originalRegion", stockOperation.getOriginalRegion());
        criteria.andEqualTo("operateProvince", stockOperation.getOperateProvince());
        criteria.andEqualTo("operateCity", stockOperation.getOperateCity());
        criteria.andEqualTo("operateRegion", stockOperation.getOperateRegion());
        criteria.andEqualTo("special", stockOperation.getSpecial());
        criteria.andGreaterThanOrEqualTo("createTime", startTime);
        criteria.andLessThanOrEqualTo("createTime", endTime);

        example.orderBy("createTime").desc();
        //库存操作记录列表
        PageHelper.startPage(pageNum, pageSize);
        List<StockOperation> list = stockOperationMapper.selectByExample(example);
        List<StockOperationDTO> stockList = CopyUtil.copyList(list, StockOperation.class, StockOperationDTO.class);
        PageInfo<StockOperation> pageResult = new PageInfo<>(list);
        pageVO.setResult(stockList);
        pageVO.setPageNum(pageNum);
        pageVO.setPageSize(pageSize);
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setPages(pageResult.getPages());
        return pageVO;
    }

    /**
     * 描述：云平台导出库存操作日志
     **//*
    @Override
    public void exportStockOperation(StockOperation stockOperation, Date startTime, Date endTime, HttpServletResponse response) {
        Example example = new Example(StockOperation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("operation", stockOperation.getOperation());
        criteria.andEqualTo("originalProvince", stockOperation.getOriginalProvince());
        criteria.andEqualTo("originalCity", stockOperation.getOriginalCity());
        criteria.andEqualTo("originalRegion", stockOperation.getOriginalRegion());
        criteria.andEqualTo("operateProvince", stockOperation.getOperateProvince());
        criteria.andEqualTo("operateCity", stockOperation.getOperateCity());
        criteria.andEqualTo("operateRegion", stockOperation.getOperateRegion());
        criteria.andGreaterThanOrEqualTo("createTime", startTime);
        criteria.andLessThanOrEqualTo("createTime", endTime);
        example.orderBy("createTime").desc();
        //库存操作记录列表
        List<StockOperation> list = stockOperationMapper.selectByExample(example);
        List<StockOperationExportDTO> stockExportList = Lists.newArrayList();
        if (CollectionUtil.isNotEmpty(list)) {
            StockOperationExportDTO dto = null;
            for (StockOperation stockOperation1 : list) {
                if (stockOperation1 != null) {
                    dto = new StockOperationExportDTO();
                    dto.setOriginalProvince(stockOperation1.getOriginalProvince());
                    dto.setOriginalCity(stockOperation1.getOriginalCity());
                    dto.setOriginalRegion(stockOperation1.getOriginalRegion());
                    dto.setOperateProvince(stockOperation1.getOperateProvince());
                    dto.setOperateCity(stockOperation1.getOperateCity());
                    dto.setOperateRegion(stockOperation1.getOperateRegion());
                    dto.setDeviceName(stockOperation1.getDeviceName());
                    dto.setCount(stockOperation1.getCount());
                    dto.setOperation(stockOperation1.getOperation() == StockOperationType.DELIVERY.value ? "下发产品" : (stockOperation1.getOperation() == StockOperationType.ALLOCATION.value ? "调拨产品" : "产品返仓"));
                    dto.setAdmin(stockOperation1.getAdmin());
                    dto.setCreateTime(DateUtil.getDateToString(stockOperation1.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                    stockExportList.add(dto);
                }
            }
        }

        String header = "库存调拨日志";
        String[] titles = new String[]{"原库存省", "原库存市", "原库存区", "操作库存省", "操作库存市", "操作库存区", "产品", "数量", "操作", "操作者", "创建时间"};
        String[] beanPropertys = new String[]{"originalProvince", "originalCity", "originalRegion", "operateProvince", "operateCity", "operateRegion", "deviceName", "count", "operation", "admin", "createTime"};
        boolean boo = ExcelUtil.exportSXSSF(stockExportList, header, titles.length - 1, titles, beanPropertys, response);
        if (!boo) {
            throw new YimaoException("导出失败");
        }
    }*/


}
