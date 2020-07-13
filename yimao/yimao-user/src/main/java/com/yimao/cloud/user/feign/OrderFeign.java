package com.yimao.cloud.user.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.order.ProductIncomeRecordDTO;
import com.yimao.cloud.pojo.dto.order.SalesProductDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = Constant.MICROSERVICE_ORDER)
public interface OrderFeign {


    @RequestMapping(value = "/order/getcompletedworkorder/{mobile}", method = RequestMethod.GET)
    WorkOrderDTO getUserCompletedWorkOrder(@PathVariable(value = "mobile") String mobile);

    @RequestMapping(value = "/order/product/income/{id}", method = RequestMethod.GET)
    List<ProductIncomeRecordDTO> getProductIncomeRecord(@PathVariable(value = "id") Integer id);

    @RequestMapping(value = "/product/sale/income/{id}", method = RequestMethod.GET)
    List<SalesProductDTO> getSaleProductListById(@PathVariable(value = "id") Integer id);

    @RequestMapping(value = "/order/renew/countfordistributor", method = RequestMethod.GET)
    Map<String,Object> getRenewOrderListById(@RequestParam(value = "distributorId") Integer distributorId);

    @GetMapping(value = "/order/existsByUserId")
    boolean existsWithUserId(@RequestParam(value = "userId") Integer userId);

    @GetMapping(value = "/order/productIncome/existsByUserId")
    boolean existsIncomeWithUserId(@RequestParam(value = "userId") Integer userId);

    /**
     * @return
     * @description 招商收益分配
     * @author Liu Yi
     */
    @PostMapping(value = "/order/investemntIncome/investmentAllot", consumes = MediaType.APPLICATION_JSON_VALUE)
    void serviceAllot(@RequestBody DistributorOrderDTO distributorOrderDTO);
    
    /****
     * 安装工转让数据(工单、订单、续费单、收益、水机)
     * @param oldId
     * @param newId
     */
    @PostMapping(value = "/order/transfer/data")
	void transferData(@RequestParam(value = "oldId") Integer oldId,@RequestParam(value = "newId") Integer newId);
}
