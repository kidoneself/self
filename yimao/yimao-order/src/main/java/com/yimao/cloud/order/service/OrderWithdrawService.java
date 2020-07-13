package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.OrderWithdrawDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawQueryDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawSubDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
public interface OrderWithdrawService {

    /**
     * 应体现订单
     *
     * @param pageNum   分页页数
     * @param pageSize  分页大小
     * @param orderId   订单号
     * @param userId    用户Id
     * @param phone     用户手机号
     * @param startTime 订单完成开始时间
     * @param endTime   订单完成结束时间
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.OrderWithdrawDTO>
     * @author hhf
     * @date 2019/2/23
     */
    PageVO<OrderWithdrawDTO> withdrawList(Integer pageNum, Integer pageSize, String orderId, Integer userId, String phone, String startTime, Integer incomeType, Integer status, String endTime);

    /**
     * 提现审核列表
     *
     * @param pageNum        分页页数
     * @param pageSize       分页大小
     * @param partnerTradeNo 提现订单号
     * @param userId         用户Id
     * @param phone          用户手机号
     * @param startTime      申请提现开始时间
     * @param endTime        申请提现结束时间
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.WithdrawSubDTO>
     * @author hhf
     * @date 2019/3/1
     */
    PageVO<WithdrawSubDTO> withdrawAuditList(Integer pageNum, Integer pageSize, Long partnerTradeNo, Integer userId, Integer incomeType, String phone, String startTime, String endTime);


    /**
     * 提现审核（批量）
     *
     * @param updater     操作人
     * @param ids         体现审核主键
     * @param auditStatus 提现状态
     * @param auditReason 审核不通过原因
     * @author hhf
     * @date 2019/3/1
     */
    String batchAudit(String updater, List<Long> ids, Integer auditStatus, String auditReason, String ip);

    /**
     * 提现记录列表
     *
     * @param pageNum          分页页数
     * @param pageSize         分页大小
     * @param withdrawQueryDTO 查询信息
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.WithdrawSubDTO>
     * @author hhf
     * @date 2019/3/4
     */
    PageVO<WithdrawSubDTO> withdrawRecordList(Integer pageNum, Integer pageSize, WithdrawQueryDTO withdrawQueryDTO);

    /**
     * 提现明细列表
     *
     * @param pageNum          分页页数
     * @param pageSize         分页大小
     * @param withdrawQueryDTO 查询信息
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.WithdrawSubDTO>
     * @author hhf
     * @date 2019/3/4
     */
    PageVO<WithdrawSubDTO> withdrawRecordDetailList(Integer pageNum, Integer pageSize, WithdrawQueryDTO withdrawQueryDTO);

    /**
     * 提现操作日志
     *
     * @param pageNum        分页页数
     * @param pageSize       分页大小
     * @param partnerTradeNo 自提现单号
     * @param withdrawFlag   提现成功与否
     * @param startTime      操作开始时间
     * @param endTime        操作结束时间
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.WithdrawSubDTO>
     * @author hhf
     * @date 2019/3/4
     */
    PageVO<WithdrawSubDTO> withdrawRecordLogList(Integer pageNum, Integer pageSize, Long partnerTradeNo, Integer withdrawFlag, String startTime, String endTime);

    /**
     * 申请提现接口,检测是否满足提现要求
     *
     * @param userId     用户ID
     * @param incomeType 收益类型 1-产品收益 2-续费收益
     * @author hhf
     * @date 2019/4/9
     */
    Map<String, Object> checkCashCondition(Integer userId, Integer incomeType);

    /**
     * 微信提现
     *
     * @param partnerTradeNo 提现单号
     * @param amount         提现金额
     * @param userId         用户ID
     * @author hhf
     * @date 2019/4/10
     */
    Boolean insertCashRecord(String partnerTradeNo, BigDecimal amount, Integer userId, Integer incomeType);

}
