package com.yimao.cloud.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.order.mapper.ReimburseManageMapper;
import com.yimao.cloud.order.service.ReimburseManageService;
import com.yimao.cloud.pojo.dto.hra.HraExportPhysicalDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.refundManageExportDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019-09-18
 */
@Service
public class ReimburseManageServiceImpl implements ReimburseManageService {

    @Resource
    private ReimburseManageMapper reimburseManageMapper;


    @Override
    public List<refundManageExportDTO> exportReimburse(OrderSubDTO dto) {
        List<refundManageExportDTO> list = new ArrayList<>();
        if (dto.getQueryType() == 1) {
            dto.setTypePay(1);
            list = reimburseManageMapper.exportOnline(dto);
        } else if (dto.getQueryType() == 2) {
            dto.setTypePay(2);
            list = reimburseManageMapper.exportOnline(dto);
        }
        return list;
    }


    @Override
    public PageVO<OrderSubDTO> onlineReimburseManagePage(Integer pageNum, Integer pageSize, OrderSubDTO dto) {
        Page<OrderSubDTO> pages;
        if (dto.getQueryType() == 1) {
            PageHelper.startPage(pageNum, pageSize);
            pages = reimburseManageMapper.onlineReimburseManagePage(dto);
        } else {
            PageHelper.startPage(pageNum, pageSize);
            pages = reimburseManageMapper.refundRecord(dto);
        }
        return new PageVO<>(pageNum, pages);
    }

    @Override
    public List<refundManageExportDTO> exportRefund(OrderSubDTO dto) {
        List<refundManageExportDTO> list = reimburseManageMapper.exportRefund(dto);
        return list;
    }
}
