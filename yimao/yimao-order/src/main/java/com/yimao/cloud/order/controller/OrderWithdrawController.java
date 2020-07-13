package com.yimao.cloud.order.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.utils.IpUtil;
import com.yimao.cloud.order.service.OrderWithdrawService;
import com.yimao.cloud.pojo.dto.order.OrderWithdrawDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawQueryDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawSubDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Api(tags = "OrderWithdrawController")
public class OrderWithdrawController {

    @Resource
    private OrderWithdrawService orderWithdrawService;

    @Resource
    private UserCache userCache;

    /**
     * 应提现订单
     *
     * @param pageNum   分页页数
     * @param pageSize  分页大小
     * @param orderId   订单号
     * @param userId    用户Id
     * @param phone     用户手机号
     * @param startTime 订单完成开始时间
     * @param endTime   订单完成结束时间
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/2/23
     */
    @GetMapping(value = "/withdraw/{pageNum}/{pageSize}")
    @ApiOperation(value = "应提现订单", notes = "应提现订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "用户手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "订单完成开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "订单完成结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "提现状态", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型", dataType = "Long", paramType = "query")
    })
    public ResponseEntity<PageVO<OrderWithdrawDTO>> withdrawList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                 @PathVariable(value = "pageSize") Integer pageSize,
                                                                 @RequestParam(value = "orderId", required = false) String orderId,
                                                                 @RequestParam(value = "userId", required = false) Integer userId,
                                                                 @RequestParam(value = "phone", required = false) String phone,
                                                                 @RequestParam(value = "startTime", required = false) String startTime,
                                                                 @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                                                 @RequestParam(value = "status", required = false) Integer status,
                                                                 @RequestParam(value = "endTime", required = false) String endTime) {
        PageVO<OrderWithdrawDTO> page = orderWithdrawService.withdrawList(pageNum, pageSize, orderId, userId, phone, startTime, incomeType, status, endTime);
        return ResponseEntity.ok(page);
    }

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
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/2/23
     */
    @GetMapping(value = "/withdraw/audit/{pageNum}/{pageSize}")
    @ApiOperation(value = "提现审核列表", notes = "提现审核列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "partnerTradeNo", value = "提现订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "用户手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "申请提现开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "申请提现结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型", dataType = "Long", paramType = "query")
    })
    public ResponseEntity<PageVO<WithdrawSubDTO>> withdrawAuditList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                    @PathVariable(value = "pageSize") Integer pageSize,
                                                                    @RequestParam(value = "partnerTradeNo", required = false) Long partnerTradeNo,
                                                                    @RequestParam(value = "userId", required = false) Integer userId,
                                                                    @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                                                    @RequestParam(value = "phone", required = false) String phone,
                                                                    @RequestParam(value = "startTime", required = false) String startTime,
                                                                    @RequestParam(value = "endTime", required = false) String endTime) {
        PageVO<WithdrawSubDTO> page = orderWithdrawService.withdrawAuditList(pageNum, pageSize, partnerTradeNo, userId, incomeType, phone, startTime, endTime);
        return ResponseEntity.ok(page);
    }


    /**
     * 提现审核（单/批量）
     *
     * @param ids         提现审核主键
     * @param auditStatus 1-通过 ; 2-不通过
     * @param auditReason 审核不通过原因
     * @return java.lang.Object
     * @author hhf
     * @date 2018/12/18
     */
    @PatchMapping(value = "/withdraw/audit/batch")
    @ApiOperation(value = "提现审核（单/批量）", notes = "提现审核（单/批量）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "企业审核ID", required = true, dataType = "Long", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "auditStatus", value = "审核状态 1-通过 ; 2-不通过", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "auditReason", value = "原因", dataType = "String", paramType = "query")
    })
    public ResponseEntity batchAudit(@RequestParam(name = "ids") List<Long> ids,
                                     @RequestParam(name = "auditStatus") Integer auditStatus,
                                     @RequestParam(name = "auditReason", required = false) String auditReason, HttpServletRequest request) {
        String updater = userCache.getCurrentAdminRealName();
        String ip = IpUtil.getIp(request);
        String message = orderWithdrawService.batchAudit(updater, ids, auditStatus, auditReason, ip);
        return ResponseEntity.ok(message);
    }

    /**
     * 提现记录列表
     *
     * @param pageNum          分页页数
     * @param pageSize         分页大小
     * @param withdrawQueryDTO 查询信息
     * @author hhf
     * @date 2019/3/4
     */
    @PostMapping(value = "/withdraw/record/{pageNum}/{pageSize}")
    @ApiOperation(value = "提现记录列表", notes = "提现记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "withdrawQueryDTO", value = "提现查询信息", required = true, dataType = "WithdrawQueryDTO", paramType = "body")
    })
    public ResponseEntity<PageVO<WithdrawSubDTO>> withdrawRecordList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                     @PathVariable(value = "pageSize") Integer pageSize,
                                                                     @RequestBody WithdrawQueryDTO withdrawQueryDTO) {

        PageVO<WithdrawSubDTO> page = orderWithdrawService.withdrawRecordList(pageNum, pageSize, withdrawQueryDTO);
        return ResponseEntity.ok(page);
    }

    /**
     * 提现明细列表
     *
     * @param pageNum          分页页数
     * @param pageSize         分页大小
     * @param withdrawQueryDTO 查询信息
     * @author hhf
     * @date 2019/3/4
     */
    @PostMapping(value = "/withdraw/record/detail/{pageNum}/{pageSize}", consumes = "application/json")
    @ApiOperation(value = "提现明细列表", notes = "提现明细列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "withdrawQueryDTO", value = "提现查询信息", required = true, dataType = "WithdrawQueryDTO", paramType = "body")
    })
    public ResponseEntity<PageVO<WithdrawSubDTO>> withdrawRecordDetailList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                           @PathVariable(value = "pageSize") Integer pageSize,
                                                                           @RequestBody WithdrawQueryDTO withdrawQueryDTO) {

        PageVO<WithdrawSubDTO> page = orderWithdrawService.withdrawRecordDetailList(pageNum, pageSize, withdrawQueryDTO);
        return ResponseEntity.ok(page);
    }


    /**
     * 提现操作日志
     *
     * @param pageNum        分页页数
     * @param pageSize       分页大小
     * @param partnerTradeNo 自提现单号
     * @param withdrawFlag   提现成功与否
     * @param startTime      操作开始时间
     * @param endTime        操作结束时间
     * @return org.springframework.http.ResponseEntity<com.yimao.cloud.pojo.vo.PageVO < com.yimao.cloud.pojo.dto.order.WithdrawSubDTO>>
     * @author hhf
     * @date 2019/3/4
     */
    @GetMapping(value = "/withdraw/record/log/{pageNum}/{pageSize}")
    @ApiOperation(value = "提现操作日志", notes = "提现操作日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "partnerTradeNo", value = "子提现订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "withdrawFlag", value = "提现成功标志 1-成功 2-失败", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "申请提现开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "申请提现结束时间", dataType = "String", paramType = "query")
    })
    public ResponseEntity<PageVO<WithdrawSubDTO>> withdrawRecordLogList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                        @PathVariable(value = "pageSize") Integer pageSize,
                                                                        @RequestParam(value = "partnerTradeNo", required = false) Long partnerTradeNo,
                                                                        @RequestParam(value = "withdrawFlag", required = false) Integer withdrawFlag,
                                                                        @RequestParam(value = "startTime", required = false) String startTime,
                                                                        @RequestParam(value = "endTime", required = false) String endTime) {

        PageVO<WithdrawSubDTO> page = orderWithdrawService.withdrawRecordLogList(pageNum, pageSize, partnerTradeNo, withdrawFlag, startTime, endTime);
        return ResponseEntity.ok(page);
    }

    /**
     * 申请提现接口,检测是否满足提现要求
     *
     * @param userId 用户信息
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/4/9
     */
    @PostMapping(value = "/wxpay/withdraw/apply")
    @ApiOperation(value = "申请提现接口,检测是否满足提现要求", notes = "申请提现接口,检测是否满足提现要求")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户信息", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型 1-产品收益 2-续费收益", required = true, dataType = "Long", paramType = "query")
    })
    public ResponseEntity checkCashCondition(@RequestParam("userId") Integer userId,@RequestParam("incomeType") Integer incomeType) {
        Map<String, Object> map = orderWithdrawService.checkCashCondition(userId, incomeType);
        return ResponseEntity.ok(map);
    }

    /**
     * 微信提现
     *
     * @param partnerTradeNo 提现单号
     * @param amount         提现金额
     * @param userId         用户ID
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/4/10
     */
    @PostMapping(value = "/wxpay/withdraw/verify")
    @ApiOperation(value = "微信提现", notes = "微信提现")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "partnerTradeNo", value = "提现单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "amount", value = "提现金额", dataType = "BigDecimal", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型 1-产品收益 2-续费收益", required = true, dataType = "Long", paramType = "query")
    })
    public ResponseEntity insertCashRecord(@RequestParam(value = "partnerTradeNo") String partnerTradeNo,
                                           @RequestParam(value = "amount") BigDecimal amount,
                                           @RequestParam(value = "userId") Integer userId,
                                           @RequestParam(value = "incomeType") Integer incomeType) {
        Boolean flag = orderWithdrawService.insertCashRecord(partnerTradeNo, amount, userId, incomeType);
        return ResponseEntity.ok(flag);
    }

}
