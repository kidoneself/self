package com.yimao.cloud.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.properties.WechatProperties;
import com.yimao.cloud.base.utils.AESUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.WXPayUtil;
import com.yimao.cloud.order.service.AfterSalesOrderService;
import com.yimao.cloud.order.service.OrderAuditLogService;
import com.yimao.cloud.order.utils.HttpUtils;
import com.yimao.cloud.pojo.dto.order.AfterSalesConditionDTO;
import com.yimao.cloud.pojo.dto.order.AfterSalesOrderDTO;
import com.yimao.cloud.pojo.dto.order.AfterSalesOrderQueryDTO;
import com.yimao.cloud.pojo.dto.order.ExamineRecordDTO;
import com.yimao.cloud.pojo.dto.order.OrderAuditLogDTO;
import com.yimao.cloud.pojo.dto.order.RefundDetailDTO;
import com.yimao.cloud.pojo.dto.order.RentalGoodsExportDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.RefundReviewBatchVo;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 退单
 *
 * @author hhf
 * @date 2019/3/12
 */
@RestController
@Slf4j
@Api(tags = "AfterSalesOrderController")
public class AfterSalesOrderController {

    @Resource
    private AfterSalesOrderService afterSalesOrderService;

    @Resource
    private OrderAuditLogService orderAuditLogService;

    @Resource
    private UserCache userCache;

    private Lock lock = new ReentrantLock();

    @Resource
    private WechatProperties wechatProperties;

    /**
     * 线上/线下退款审核列表
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param dto      查询信息
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/12
     */
    @PostMapping(value = "/refund/{pageNum}/{pageSize}")
    @ApiOperation(value = "线上退款审核列表", notes = "线上退款审核列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "待审核提现订单查询条件", dataType = "AfterSalesOrderQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path")
    })
    public ResponseEntity<PageVO<AfterSalesOrderDTO>> refundAudit(@PathVariable(value = "pageNum") Integer pageNum,
                                                                  @PathVariable(value = "pageSize") Integer pageSize,
                                                                  @RequestBody AfterSalesOrderQueryDTO dto) {
        PageVO<AfterSalesOrderDTO> page = afterSalesOrderService.refundAudit(pageNum, pageSize, dto);
        return ResponseEntity.ok(page);
    }

    /**
     * 线上/线下退款复核
     *
     * @param id 售后订单主键
     * @author hhf
     * @date 2019/3/12
     */
    @PatchMapping(value = "/refund/review")
    @ApiOperation(value = "线上退款复核", notes = "线上退款复核")
    @ApiImplicitParam(name = "id", value = "售后订单主键", required = true, dataType = "Long", paramType = "query")
    public void refundReview(@RequestParam(name = "id") Long id) {
       /* String updater = userCache.getCurrentAdminRealName();
        try {
            afterSalesOrderService.refund(id, updater);
        } catch (Exception e) {
            log.error("==========退款失败========id=" + id + ",错误原因=" + e.getMessage());
        }*/
    }

    /**
     * 线上/线下退款批量复核
     *
     * @param refundReviewBatchVo 售后订单
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/12
     */
    @PatchMapping(value = "/refund/review/batch")
    @ApiOperation(value = "线上退款批量复核", notes = "线上退款批量复核")
    @ApiImplicitParam(name = "refundReviewBatchVo", value = "订单售后审核条件", dataType = "RefundReviewBatchVo", paramType = "body", required = true)
    public ResponseEntity refundReviewBatch(@RequestBody RefundReviewBatchVo refundReviewBatchVo) {
        String updater = userCache.getCurrentAdminRealName();
        afterSalesOrderService.refundReviewBatch(refundReviewBatchVo, updater);
        return ResponseEntity.noContent().build();
    }

    /**
     * 线上/线下退款记录列表
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param dto      查询信息
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/12
     */
    @PostMapping(value = "/refund/record/{pageNum}/{pageSize}")
    @ApiOperation(value = "线上/线下退款记录列表", notes = "线上/线下退款记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "待审核提现订单查询条件", dataType = "AfterSalesOrderQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path")
    })
    public ResponseEntity<PageVO<AfterSalesOrderDTO>> refundRecord(@PathVariable(value = "pageNum") Integer pageNum,
                                                                   @PathVariable(value = "pageSize") Integer pageSize,
                                                                   @RequestBody AfterSalesOrderQueryDTO dto) {
        PageVO<AfterSalesOrderDTO> page = afterSalesOrderService.refundRecord(pageNum, pageSize, dto);
        return ResponseEntity.ok(page);
    }

    /**
     * 操作日志
     *
     * @param orderId   查询信息
     * @param pageNum   分页页数
     * @param pageSize  分页大小
     * @param operation 操作
     * @param startTime 操作开始时间
     * @param endTime   操作结束时间
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/12
     */
    @GetMapping(value = "/refund/log/{pageNum}/{pageSize}")
    @ApiOperation(value = "操作日志", notes = "操作日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", defaultValue = "1", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", defaultValue = "10", required = true, paramType = "path"),
            @ApiImplicitParam(name = "orderId", value = "子订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "operation", value = "操作", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "操作开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "操作结束时间", dataType = "String", paramType = "query")
    })
    public ResponseEntity<PageVO<AfterSalesOrderDTO>> refundLog(@PathVariable(value = "pageNum") Integer pageNum,
                                                                @PathVariable(value = "pageSize") Integer pageSize,
                                                                @RequestParam(value = "orderId", required = false) Long orderId,
                                                                @RequestParam(value = "operation", required = false) Integer operation,
                                                                @RequestParam(value = "startTime", required = false) String startTime,
                                                                @RequestParam(value = "endTime", required = false) String endTime) {
        PageVO<AfterSalesOrderDTO> page = afterSalesOrderService.refundLog(pageNum, pageSize, orderId, operation, startTime, endTime);
        return ResponseEntity.ok(page);
    }


    /**
     * @Author ycl
     * @Description 仅退款-租赁商品/实物商品
     * @Date 17:25 2019/8/21
     * @Param
     **/
    @PostMapping(value = "/refund/rental/{pageNum}/{pageSize}")
    public Object rentalGoodsList(@PathVariable(value = "pageNum") Integer pageNum,
                                  @PathVariable(value = "pageSize") Integer pageSize,
                                  @RequestBody AfterSalesConditionDTO dto) {
        PageVO<AfterSalesOrderDTO> page = afterSalesOrderService.rentalGoodsList(pageNum, pageSize, dto);
        return page;
    }


    /**
     * @Author ycl
     * @Description 仅退款-租赁商品/实物商品详情
     * @Date 17:25 2019/8/21
     * @Param
     **/
    @GetMapping(value = "/refund/rental/{id}")
    public AfterSalesOrderDTO getSalesDetailById(@PathVariable(value = "id") Long id) {
        return afterSalesOrderService.getSalesDetailById(id);
    }


    /**
     * @param id
     * @param auditStatus
     * @return java.lang.Object
     * @description
     * @author zhilin.he  我的订单--经销商退单审核
     * @date 2019/8/22 14:15
     */
    @PutMapping(value = "/refund/audit/distributor")
    @ApiOperation(value = "经销商退单审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "售后id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "auditStatus", value = "审核状态：true、同意退单；false、继续服务", required = true, dataType = "Long", paramType = "query")})
    public Object updateAuditStatus(@RequestParam(value = "id") Long id,
                                    @RequestParam(value = "auditStatus") Integer auditStatus) {
        String updater = userCache.getCurrentAdminRealName();
        afterSalesOrderService.updateAuditStatus(id, auditStatus, updater);
        return ResponseEntity.ok().build();
    }


    //售后订单审核记录列表
    @PostMapping(value = "/refund/audit/{pageNum}/{pageSize}")
    public PageVO<OrderAuditLogDTO> orderAuditLogList(@PathVariable(value = "pageNum") Integer pageNum,
                                                      @PathVariable(value = "pageSize") Integer pageSize,
                                                      @RequestBody AfterSalesConditionDTO dto) {
        return orderAuditLogService.orderAuditLogList(pageNum, pageSize, dto);
    }


    //租赁商品导出：全部
//    @PostMapping(value = "/refund/rental/goods/all")
//    public List<RentalGoodsExportDTO> exportRentalGoods(@RequestBody AfterSalesConditionDTO dto) {
//        return afterSalesOrderService.exportRentalGoods(dto);
//    }


    //租赁商品导出 待审核导出
//    @PostMapping(value = "/refund/rental/goods/audited")
//    public List<RentalGoodsExportDTO> exportAuditedRentalGoods(@RequestBody AfterSalesConditionDTO dto) {
//        return afterSalesOrderService.exportAuditedRentalGoods(dto);
//    }


    //租赁商品导出 审核记录导出
//    @PostMapping(value = "/refund/rental/audit/record")
//    public List<RentalGoodsExportDTO> exportAuditedRecord(@RequestBody AfterSalesConditionDTO dto) {
//        return afterSalesOrderService.exportAuditedRecord(dto);
//    }

    //审核记录详情
    @GetMapping(value = "/refund/audit/examine/{id}")
    public ExamineRecordDTO getExamineRecordDetailById(@PathVariable Long id) {
        return orderAuditLogService.getExamineRecordDetailById(id);
    }

    /****
     * 微信退款 回调接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/refund/notify", produces = MediaType.APPLICATION_XML_VALUE)
    public String wxRefundNotify(HttpServletRequest request) {
        try {
            lock.lock();
            //从HTTP请求中获取微信返回信息
            String refundCallbackResult = HttpUtils.getResponse(request);
            return refundCallBack(refundCallbackResult);
        } finally {
            lock.unlock();
        }
    }

    /***
     * 微信退款回调处理
     * @param refundCallbackResult
     * @return
     */
    private String refundCallBack(String refundCallbackResult) {
        SortedMap<String, String> map = null;
        boolean flag = false;//默认退款失败
        Long id = 0l;//售后单编号
        try {
            if (StringUtil.isEmpty(refundCallbackResult)) {
                return "微信退款回调返回为空";
            }
            map = WXPayUtil.xmlToMap(refundCallbackResult);
            log.info("===========微信退款异步通知数据:refundCallbackResult=" + JSONObject.toJSONString(map));
            // 处理返回结果
            if (!map.isEmpty()) {
                // 微信退款信息处理
                String return_code = (String) map.get(WechatConstant.RETURN_CODE);// 通信code
                // 只有通信code返回SUCCESS,才处理返回结果
                if (!StringUtil.isEmpty(return_code) && return_code.equals(WechatConstant.SUCCESS)) {
                    // 通信成功解析返回内容
                    // 签名校验
                    if (WXPayUtil.signValid(map, wechatProperties.getJsapi().getKey())) {
                        String req_info = (String) map.get("req_info");// 返回的加密信息，需要解密
                        // 校验返回数据是否和商户售后单信息吻合
                        SortedMap<String, String> resultBody = WXPayUtil.xmlToMap(AESUtil.AESDncode(req_info));
                        if (StringUtil.isEmpty(req_info) && !checkResultSuc(resultBody)) {
                            return "微信退款返回数据校验不通过";
                        }
                        id = Long.parseLong(resultBody.get("out_refund_no"));
                        flag = true;

                    } else {
                        return "签名校验失败";
                    }
                } else {
                    log.error("=========微信退款处理失败======失败原因:" + map.get("return_msg"));
                    return "微信退款处理失败";
                }

            }
        } catch (Exception e) {
            log.error("========退款处理异常:" + e.getMessage());
        } finally {
            //更新订单状态
            if (id > 0) {
                try {
                    afterSalesOrderService.updateAfterSalesOrderAndSubOrder(id, flag);
                } catch (Exception e) {
                    return e.getMessage();
                }
            }
        }


        return "退款处理完成";
    }

    /***
     * 校验微信退款回调数据是否和请求数据吻合
     * @param req_info
     * @return
     */
    private boolean checkResultSuc(SortedMap<String, String> resultBody) {
        try {
            String out_refund_no = resultBody.get("out_refund_no");//商户退款单号
            //String out_trade_no=resultBody.get("out_trade_no");//商户订单号
            String total_fee = resultBody.get("total_fee");//订单总额
            String refund_fee = resultBody.get("refund_fee");//退款金额
            String refund_status = resultBody.get("refund_status");//退款状态
            //String success_time=resultBody.get("success_time");//退款成功时间
            AfterSalesOrderDTO afterSalesOrder = afterSalesOrderService.getSalesDetailById(Long.parseLong(out_refund_no));//根据售后单id查询售后单信息
            if (null == afterSalesOrder) {
                log.error("=========返回退款单号不存在=======" + out_refund_no);
                return false;
            }
            //校验总金额
            if (StringUtil.isNotEmpty(total_fee) && !(new BigDecimal(total_fee).compareTo(afterSalesOrder.getPayFee().multiply(new BigDecimal(100))) == 0)) {
                log.error("==========返回总金额和售后单总金额不一致=======售后单号:" + out_refund_no + ",微信返回总金额:" + total_fee + ",售后单总金额:" + afterSalesOrder.getPayFee());
                return false;
            }

            //校验退款金额
            if (StringUtil.isNotEmpty(refund_fee) && !(new BigDecimal(refund_fee).compareTo(afterSalesOrder.getRealRefundFee().multiply(new BigDecimal(100))) == 0)) {
                log.error("==========返回退款金额和售后单退款金额不一致=======售后单号:" + out_refund_no + ",微信返回退款金额:" + refund_fee + ",售后单退款金额:" + afterSalesOrder.getRealRefundFee());
                return false;
            }
            //退款状态
            if (StringUtil.isEmpty(refund_status) || !refund_status.equals(WechatConstant.SUCCESS)) {
                log.error("=========退款不成功=========售后单号:" + out_refund_no + ",refund_status=" + refund_status);
                return false;
            }
        } catch (Exception e) {
            log.error("======微信退款回调数据校验异常 信息:" + e.getMessage());
            return false;
        }
        return true;
    }


    /***
     * 退款详情
     * @param id
     * @return
     */
    @GetMapping(value = "/order/refund/detail")
    public RefundDetailDTO refunddetail(@RequestParam(value = "id") Long id) {
        return afterSalesOrderService.refundDetai(id);
    }


}
