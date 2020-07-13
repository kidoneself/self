package com.yimao.cloud.order.mapper;

import com.yimao.cloud.order.po.ServiceIncomeRecordPart;
import com.yimao.cloud.pojo.dto.order.IncomeRecordPartResultDTO;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ServiceIncomeRecordPartMapper extends Mapper<ServiceIncomeRecordPart> {
    /**
     * @description 根据收益id获取收益对象信息
     * @author Liu Yi
     * @date 2019/1/29
     */
    List<IncomeRecordPartResultDTO> getPartByServiceIncomeRecordId(Integer serviceIncomeRecordId);

    /**
     * 描述：批量插入
     *
     * @param list
     **/
    void batchInsert(List<ServiceIncomeRecordPart> list);
}