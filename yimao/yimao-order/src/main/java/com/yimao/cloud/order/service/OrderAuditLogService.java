package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.AfterSalesConditionDTO;
import com.yimao.cloud.pojo.dto.order.ExamineRecordDTO;
import com.yimao.cloud.pojo.dto.order.OrderAuditLogDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;

public interface OrderAuditLogService {

    PageVO<OrderAuditLogDTO> orderAuditLogList(Integer pageNum, Integer pageSize, AfterSalesConditionDTO dto);

    ExamineRecordDTO getExamineRecordDetailById(Long id);


    void auditOrder(Long id,String updater);

    void batchAuditAdopt(List<Long> ids);

    void auditNoPassage(Long id,String auditReason,String detailReason, String updater);

    void batchAuditNoPassage(List<Long> ids, String auditReason, String detailReason);
}
