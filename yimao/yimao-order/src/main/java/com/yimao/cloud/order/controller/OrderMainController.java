package com.yimao.cloud.order.controller;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.DialogException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.order.mapper.OrderMainMapper;
import com.yimao.cloud.order.po.OrderMain;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.order.service.OrderMainService;
import com.yimao.cloud.pojo.dto.order.OrderDTO;
import com.yimao.cloud.pojo.dto.order.OrderMainDTO;
import com.yimao.cloud.pojo.dto.order.OrderPayCheckDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Slf4j
public class OrderMainController {

    @Resource
    private OrderMainService orderMainService;
    @Resource
    private OrderMainMapper orderMainMapper;
    @Resource
    private UserCache userCache;


    /**
     * 下单接口
     *
     * @param orderDTO 订单对象
     * @param type     1-单件商品下单；2-购物车下单
     * @date 2018/12/28 17:28
     */
    @PostMapping(value = "/order/{type}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object createOrder(@RequestBody OrderDTO orderDTO, @PathVariable Integer type) throws Exception {
        try {
            OrderMain main = orderMainService.createOrder(orderDTO, type);
            if (main == null) {
                throw new BadRequestException("下单失败。");
            }
            OrderMainDTO dto = new OrderMainDTO();
            dto.setId(main.getId());
            dto.setOrderAmountFee(main.getOrderAmountFee());
            return CommResult.ok(dto);
        } catch (Exception e) {
            if (e instanceof DialogException) {
                //对话框消息提示
                return CommResult.dialogError(e.getMessage());
            } else {
                throw e;
            }
        }
    }

    /**
     * 50元优惠券下单
     *
     * @param orderDTO 订单对象
     * @param type     1-单张下单；2-批量下单
     */
    @RequestMapping(value = "/order/hra/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderMainDTO createHRAOrder(@RequestBody OrderDTO orderDTO, @PathVariable Integer type) {
        Integer userId = userCache.getUserId();
        OrderMainDTO mainDTO;
        if (type == 2) {
            mainDTO = orderMainService.batchCreateHRAOrder(orderDTO, userId);
        } else {
            mainDTO = orderMainService.createHRAOrder(orderDTO, userId);
        }
        OrderMainDTO dto = new OrderMainDTO();
        dto.setId(mainDTO.getId());
        dto.setOrderAmountFee(mainDTO.getOrderAmountFee());
        return dto;
    }

    /**
     * 根据id查询主订单
     *
     * @param id 主订单id
     */
    @GetMapping(value = "/order/{id}")
    public OrderMainDTO findById(@PathVariable("id") Long id) {
        OrderMain orderMain = orderMainService.findById(id);
        OrderMainDTO orderMainDTO = new OrderMainDTO();
        orderMain.convert(orderMainDTO);
        return orderMainDTO;
    }

    /**
     * 线下支付审核列表（立即支付）
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @GetMapping(value = "/order/main/paycheckList/{pageNum}/{pageSize}")
    public PageVO<OrderMainDTO> orderMainPayCheckList(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody OrderMainDTO query) {
        log.info("查询 参数=" + JSON.toJSONString(query));

        return orderMainService.orderMainPayCheckList(pageNum, pageSize, query);


    }

    /**
     * 线下支付审核列表（货到付款）
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @GetMapping(value = "/order/main/deliveryPaycheckList/{pageNum}/{pageSize}")
    public PageVO<WorkOrderDTO> orderDeliveryPayCheckList(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody WorkOrderQueryDTO query) {
        log.info("查询 参数=" + JSON.toJSONString(query));

        return orderMainService.orderDeliveryPayCheckList(pageNum, pageSize, query);

    }

    /**
     * 线下财务审核记录
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping(value = "/order/main/paycheckRecord/{pageNum}/{pageSize}")
    public PageVO<OrderPayCheckDTO> paycheckRecord(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody OrderPayCheckDTO orderPayCheckDTO) {

        return orderMainService.orderPaycheckRecordList(pageNum, pageSize, orderPayCheckDTO);

    }


    /**
     * 线下商品支付审核
     *
     * @param ids
     * @param pass
     * @param reason
     */
    @PatchMapping(value = "/order/main/paycheck/single")
    public void orderMainPayCheckSingle(@RequestParam String id, @RequestParam Boolean pass, @RequestParam(required = false) String reason, @RequestParam Integer payTerminal, @RequestParam(required = false) String userPhone) {
        String adminName = userCache.getCurrentAdminRealName();
       
		List<OrderSub> hraSubOrderList=orderMainService.orderMainPayCheckSingle(id, pass, reason, adminName, payTerminal, userPhone);
		
		if(Objects.nonNull(hraSubOrderList) && hraSubOrderList.size()>0) {
			//生成体检卡
			orderMainService.createHraCard(hraSubOrderList);
		}
			
    }

    /**
     * 线下支付审核列表导出（立即支付）
     *
     * @param query
     * @param response
     * @return
     */
    @PostMapping("/order/main/paycheckList/export")
    public List<OrderMainDTO> orderMainPayCheckExport(@RequestBody OrderMainDTO query) {
        return orderMainService.orderMainPayCheckExport(query);
    }

    /**
     * 线下支付审核列表导出（货到付款）
     *
     * @param query
     * @param response
     * @return
     */
    @PostMapping("/order/main/deliveryPaycheckList/export")
    public List<WorkOrderDTO> orderDeliveryPayCheckExport(@RequestBody WorkOrderQueryDTO query) {
        return orderMainService.orderDeliveryPayCheckListExport(query);
    }

    /**
     * 查询用户是否下过订单
     */
    @GetMapping(value = "/order/existsByUserId")
    public Object existsWithUserId(@RequestParam Integer userId) {
        return orderMainMapper.existsWithUserId(userId);
    }
    
    /**
     * 线下支付审核记录详情
     * @param orderId 单号
     * @param orderType 订单类型：1-普通订单(立即支付) 2-水机续费订单 3-工单(货到付款)
     * @return
     */
    @GetMapping(value = "/order/main/paycheckRecord/{id}/info")
   	OrderPayCheckDTO paycheckRecordInfo(@PathVariable("id") Integer id,@RequestParam(value="orderType",required=true) Integer orderType) {
		return orderMainService.findPayCheckRecordInfo(id,orderType);
    	
    }

}
