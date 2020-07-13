package com.yimao.cloud.order.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.RemoteCallException;
import com.yimao.cloud.order.po.OrderInvoice;
import com.yimao.cloud.order.service.OrderInvoiceService;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceDTO;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 发票
 *
 * @author hhf
 * @date 2019/1/16
 */
@RestController
@Slf4j
@Api(tags = "OrderInvoiceController")
public class OrderInvoiceController {

    @Resource
    private OrderInvoiceService orderInvoiceService;

    @Resource
    private UserCache userCache;

    /**
     * 查询开票表中是否存在相同订单的数据
     *
     * @param mainOrderId
     * @param orderId
     * @return  true 已存在  false 不存在
     */
    @GetMapping(value = "/order/invoice/checkExist")
    public Object checkExistByOrderId(@RequestParam("mainOrderId") Long mainOrderId,
                                      @RequestParam("orderId") Long orderId
    ) {
        return ResponseEntity.ok(orderInvoiceService.checkExistByOrderId(mainOrderId, orderId));
    }

    /**
     * 分页查询经销商信息
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/16
     */
    @GetMapping(value = "order/invoice/{pageNum}/{pageSize}", consumes = "application/json")
    @ApiOperation(value = "分页查询发票信息", notes = "分页查询发票信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity pageQueryInvoice(@PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize,
                                           @RequestBody OrderInvoiceQueryDTO query) {

        PageVO<OrderInvoiceDTO> page = orderInvoiceService.pageQueryInvoice(query, pageNum, pageSize);
        if (page == null) {
            throw new RemoteCallException();
        }
        return ResponseEntity.ok(page);
    }

    /**
     * 创建开票信息
     *
     * @param dto 开票信息
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/17
     */
    @PostMapping(value = "order/invoice")
    @ApiOperation(value = "创建开票信息", notes = "创建开票信息")
    @ApiImplicitParam(name = "dto", value = "经销商信息", required = true, dataType = "OrderInvoiceDTO", paramType = "body")
    public ResponseEntity save(@RequestBody OrderInvoiceDTO dto) {
        String creator = userCache.getCurrentAdminRealName();
        OrderInvoice orderInvoice = new OrderInvoice(dto);
        orderInvoiceService.save(creator, orderInvoice);
        return ResponseEntity.noContent().build();
    }


    /**
     * 根据主键ID查询发票信息
     *
     * @param id
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/17
     */
    @GetMapping(value = "order/invoice/{id}")
    @ApiOperation(value = "根据主键ID查询发票信息", notes = "根据主键ID查询发票信息")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity getDistributorById(@PathVariable("id") Integer id) {
        OrderInvoiceDTO dto = orderInvoiceService.getInvoiceById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * 续费工单--确认开票
     */
    @PutMapping(value = "/order/invoice/renew/confirm/{id}")
    @ApiOperation(value = "确认开票", notes = "确认开票")
    @ApiImplicitParam(name = "id", value = "续费id", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity confirmInvoice(@PathVariable(value = "id") String id) {
        orderInvoiceService.confirmInvoice(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 续费工单--编辑发票
     */
    @PutMapping(value = "order/invoice/renew")
    @ApiOperation(value = "编辑发票", notes = "编辑发票")
    @ApiImplicitParam(name = "dto", value = "发票信息", required = true, dataType = "OrderInvoiceDTO", paramType = "body")
    public ResponseEntity updateVerify(@RequestBody OrderInvoiceDTO dto) {
        orderInvoiceService.updateVerify(dto);
        return ResponseEntity.noContent().build();
    }
}
