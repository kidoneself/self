package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.IncomeExportDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeQueryDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.dto.order.ServiceIncomeRecordPartDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeVO;

import java.util.List;

/**
 * @author Liu Yi
 * @date 2019/1/23.
 */
public interface ServiceIncomeRecordService {

    /**
     * 描述：分配服务收益（HRA）
     *
     * @param ticketNo  评估券号
     * @param stationId 服务站门店ID
     * @param deviceId  HRA设备ID
     */
    void serviceAllot(String ticketNo, Integer stationId, String deviceId);

    /**
     * 查询服务收益
     *
     * @param productIncomeQueryDTO 查询dto
     * @param pageNum               页码
     * @param pageSize              页数
     */
    PageVO<ProductIncomeVO> pageQueryServiceIncome(ProductIncomeQueryDTO productIncomeQueryDTO, Integer pageNum, Integer pageSize);

    /**
     * 根据订单id查询收益记录
     *
     * @param orderId 订单id
     */
    List<ServiceIncomeRecordPartDTO> getServiceIncomeRecordPartList(Long orderId);

    /**
     * 根据id查询收益记录
     *
     * @param id id
     */
    IncomeRecordResultDTO getServiceIncomeById(Integer id);

    /**
     * 产品服务收益导出
     *
     * @author hhf
     * @date 2019/5/14
     */
    List<IncomeExportDTO> serviceIncomeExport(ProductIncomeQueryDTO query);
}
