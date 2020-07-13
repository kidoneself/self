package com.yimao.cloud.order.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.hra.HraDeviceDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * HRA Feign
 *
 * @date 2018-12-28
 */
@FeignClient(name = Constant.MICROSERVICE_HRA)
public interface HraFeign {


    /**
     * 预约评估
     *
     * @return
     */
//    @RequestMapping(value = "/mall/ticket/create",method = RequestMethod.POST)
//    Object create(@Param("reserveDTO") ReserveDTO reserveDTO);


    /**
     * @param ticketNo 体检卡号
     * @return HraTicketDTO
     * @description: 根据体检卡号查询体检卡
     * @author Liu Yi
     */
    @RequestMapping(value = "/hra/ticket/{ticketNo}", method = RequestMethod.GET)
    HraTicketDTO getTicketByNo(@PathVariable(value = "ticketNo") String ticketNo);

    /**
     * 查询HRA设备信息
     *
     * @param id HRA设备ID
     */
    @RequestMapping(value = "/device/{id}", method = RequestMethod.GET)
    HraDeviceDTO getHraDeviceByDeviceId(@PathVariable(value = "id") String id);

    /**
     * M的支付回调(关联订单号，修改卡状态)
     *
     * @param orderId 订单号
     */
    @RequestMapping(value = "/hra/refer", method = RequestMethod.PATCH)
    void doRefer(@RequestParam("orderId") Long orderId, @RequestParam("ticketNo") String ticketNo, @RequestParam("terminal") Integer terminal);

    /**
     * Y的支付回调
     *
     * @param orderId
     */
    @RequestMapping(value = "/ticket/card", method = RequestMethod.POST)
    void createHraCardAndTicket(@RequestParam("orderId") Long orderId);


    /**
     * 批量支付-查询需要支付的优惠卡
     *
     * @param userId 用户ID
     * @param count  批量支付的数量
     */
    @RequestMapping(value = "/hra/batchpay/tickets", method = RequestMethod.GET)
    List<HraTicketDTO> listTicketForPay(@RequestParam(value = "userId") Integer userId, @RequestParam(value = "count") Integer count);

    //根据订单号查询HraCard
    @RequestMapping(value = "/hraCard/{id}", method = RequestMethod.GET)
    HraTicketDTO getHraTicketByOrderId(@PathVariable(value = "id") Long id);
}
