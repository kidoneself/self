package com.yimao.cloud.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.enums.InvoiceApplyStatus;
import com.yimao.cloud.base.enums.InvoiceHeadEnum;
import com.yimao.cloud.base.enums.InvoiceTypeEnum;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.order.mapper.OrderInvoiceMapper;
import com.yimao.cloud.order.po.OrderInvoice;
import com.yimao.cloud.order.service.OrderInvoiceService;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceDTO;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OrderInvoiceServiceImpl implements OrderInvoiceService {

    @Resource
    private OrderInvoiceMapper orderInvoiceMapper;

    @Override
    public PageVO<OrderInvoiceDTO> pageQueryInvoice(OrderInvoiceQueryDTO query, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<OrderInvoiceDTO> page = orderInvoiceMapper.pageQuery(query);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public void save(String creator, OrderInvoice orderInvoice) {
        int result = orderInvoiceMapper.insert(orderInvoice);
        if (result < 1) {
            throw new YimaoException("操作失败。");
        }
    }

    @Override
    public OrderInvoiceDTO getInvoiceById(Integer id) {
        Example example = new Example(OrderInvoice.class);
        example.createCriteria().andEqualTo("id", id);
        OrderInvoice orderInvoice = orderInvoiceMapper.selectOneByExample(example);
        if (orderInvoice == null) {
            throw new NotFoundException("未获取到开票信息");
        }
        OrderInvoiceDTO dto = new OrderInvoiceDTO();
        orderInvoice.convert(dto);
        return dto;
    }


    @Override
    public OrderInvoice getInvoiceByOrderId(Long orderId) {
        Example example = new Example(OrderInvoice.class);
        example.createCriteria().andEqualTo("orderId", orderId);
        List<OrderInvoice> orderInvoiceList = orderInvoiceMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(orderInvoiceList)) {
            return orderInvoiceList.get(0);
        }
        return null;
    }

    /**
     * 续费工单--确认开票
     */
    @Override
    public void confirmInvoice(String renewId) {
        OrderInvoice orderInvoice = new OrderInvoice();
        orderInvoice.setRenewId(renewId);
        orderInvoice = orderInvoiceMapper.selectByPrimaryKey(orderInvoice.getRenewId());
        if (orderInvoice == null) {
            throw new NotFoundException("该续费单号未开发票！");
        }
        //添加发票信息
        orderInvoice.setApplyStatus(InvoiceApplyStatus.APPLIED.value);
        orderInvoice.setConfirmTime(new Date());
        int result = orderInvoiceMapper.updateByPrimaryKey(orderInvoice);
        if (result < 1) {
            throw new YimaoException("确认开票失败!");
        }
    }

    /**
     * 续费工单--编辑发票
     */
    @Override
    public void updateVerify(OrderInvoiceDTO dto) {
        OrderInvoice orderInvoice = orderInvoiceMapper.selectByPrimaryKey(dto.getRenewId());
        if (orderInvoice == null) {
            throw new NotFoundException("该续费单号未开发票！");
        }
        orderInvoice.setInvoiceType(dto.getInvoiceType());
        orderInvoice.setInvoiceHead(dto.getInvoiceHead());
        if (dto.getInvoiceHead() == InvoiceTypeEnum.RENEW.value) {
            orderInvoice.setBankName(dto.getBankName());
            orderInvoice.setBankAccount(dto.getBankAccount());
        }
        if (dto.getInvoiceHead() == InvoiceHeadEnum.ONE.value) {
            orderInvoice.setApplyUser(dto.getApplyUser());
            orderInvoice.setApplyEmail(dto.getApplyEmail());
            orderInvoice.setApplyAddress(dto.getApplyAddress());
            orderInvoice.setApplyPhone(dto.getApplyPhone());
        }
        orderInvoice.setCompanyName(dto.getCompanyName());
        orderInvoice.setCompanyAddress(dto.getCompanyAddress());
        orderInvoice.setCompanyPhone(dto.getCompanyPhone());
        orderInvoice.setDutyNo(dto.getDutyNo());
        int result = orderInvoiceMapper.updateByPrimaryKey(orderInvoice);
        if (result < 1) {
            throw new YimaoException("编辑发票失败!");
        }
    }

    @Override
    public void updateInvoice(OrderInvoice orderInvoice) {
        Example example = new Example(OrderInvoice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mainOrderId", orderInvoice.getMainOrderId());
        criteria.andEqualTo("orderId", orderInvoice.getOrderId());
        int count = orderInvoiceMapper.updateByExampleSelective(orderInvoice, example);
        if (count != 1) {
            throw new YimaoException("编辑发票失败!");
        }
    }

    @Override
    public Boolean checkExistByOrderId(Long mainOrderId, Long orderId) {
        Example example = new Example(OrderInvoice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mainOrderId", mainOrderId);
        criteria.andEqualTo("orderId", orderId);
        List<OrderInvoice> orderInvoices = orderInvoiceMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(orderInvoices)) {
            //不存在返回false
            return false;
        }
        //存在返回true
        return true;
    }
}
