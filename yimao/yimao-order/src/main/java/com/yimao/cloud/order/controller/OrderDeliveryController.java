package com.yimao.cloud.order.controller;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.OrderDelivery;
import com.yimao.cloud.order.service.OrderDeliveryService;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author shnehuiyang
 * @date 2018/12/27.
 */
@RestController
@Slf4j
@Api(tags = "OrderDeliveryController")
public class OrderDeliveryController {

    @Resource
    private OrderDeliveryService orderDeliveryService;

    /**
     * 添加订单发货信息(设置发货)
     * @return
     */
    @PostMapping(value = "/order/delivery")
    @ApiOperation(value = "添加订单发货信息(设置发货)", notes = "添加订单发货信息(设置发货)")
    @ApiImplicitParam(name = "orderDelivery", value = "订单发货单实体类",required = true, dataType = "OrderDelivery", paramType = "body")
    public Object add(@RequestBody OrderDeliveryDTO orderDeliveryDTO) {
        OrderDelivery orderDelivery = new OrderDelivery(orderDeliveryDTO);
        orderDeliveryService.add(orderDelivery);
        return ResponseEntity.noContent().build();
    }

    /**
     * 导入物流跟踪信息
     * @return
     */
    @RequestMapping(value = "/order/delivery/import", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object importExcel(@RequestPart("multipartFile") MultipartFile multipartFile) {
        return orderDeliveryService.importExcel(multipartFile);
    }




    /**
     * 查询物流信息
     * @return
     */
    @GetMapping(value = "/order/delivery/query")
    public Object query(@RequestParam(value = "logisticsNo") String logisticsNo,
                        @RequestParam(value = "orderId") Long orderId) {
        return orderDeliveryService.query(logisticsNo,orderId);
    }



    /**
     * 发货列表查询
     *
     * @return
     */
    @PostMapping(value = "/order/delivery/list/{pageNum}/{pageSize}")
    public PageVO<DeliveryInfoDTO> list(@RequestBody DeliveryDTO deliveryDTO,
                       @PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<DeliveryInfoDTO> orderList =orderDeliveryService.list(deliveryDTO,pageNum,pageSize);
        return orderList;
    }


    /**
     * @Author ycl
     * @Description 发货列表--设置发货
     * @Date 14:16 2019/7/30
     * @Param
     **/
    @PatchMapping(value = "/order/delivery/{id}")
    public void setDelivery(@PathVariable(value = "id") Long id){
        orderDeliveryService.setDelivery(id);
    }


    /**
     * @Author ycl
     * @Description 发货列表--批量发货
     * @Date 14:50 2019/7/30
     * @Param
     **/
    @PatchMapping(value = "/order/delivery/batch")
    public void batchDelivery(@RequestParam(value = "ids") List<Long> ids){
        orderDeliveryService.setBatchDelivery(ids);
    }


    /**
     * @Author ycl
     * @Description 发货记录列表
     * @Date 10:37 2019/9/24
     * @Param
    **/
    @GetMapping(value = "/order/delivery/record/{pageNum}/{pageSize}")
    public Object deliveryRecordList(@PathVariable(value = "pageNum") Integer pageNum,
                                     @PathVariable(value = "pageSize") Integer pageSize,
                                     @RequestParam(value = "orderId",required = false) String orderId,
                                     @RequestParam(value = "logisticsNo",required = false) String logisticsNo,
                                     @RequestParam(value = "startTime",required = false) String startTime,
                                     @RequestParam(value = "endTime",required = false) String endTime,
                                     @RequestParam(value = "userId",required = false) Integer userId,
                                     @RequestParam(value = "addreessName",required = false) String addreessName,
                                     @RequestParam(value = "terminal",required = false) Integer terminal){
        PageVO<OrderDeliveryRecordDTO> recordDTOPageVO =  orderDeliveryService.deliveryRecordList(pageNum,pageSize,orderId,logisticsNo,startTime,endTime,userId,addreessName,terminal);
        return recordDTOPageVO;
    }

    /**
     * @Author ycl
     * @Description 发货记录详情
     * @Date 10:37 2019/9/24
     * @Param
    **/
    @GetMapping(value = "/order/delivery/record/{id}")
    public DeliveryDetailInfoDTO deliveryRecordDetail(@PathVariable(value = "id") Integer id){
        return orderDeliveryService.findDeliveryDetail(id);
    }

    /**
     * @Author ycl
     * @Description 发货列表导出
     * @Date 10:15 2019/8/23
     * @Param
    **/
    //发货列表导出：1-导出发货台账登记表 2-导出订单发货信息表 3-导出快递打印表
    @PostMapping(value = "/order/delivery/export")
    public Page<Object> deliveryExport(@RequestParam(value = "exportType") Integer exportType,
                                       @RequestBody DeliveryConditionDTO dto) {
        return orderDeliveryService.deliveryExport(exportType,dto);
    }


    /**
     * @Author ycl
     * @Description 发货列表-发货记录相关导出
     * 1-导出发货台账登记表 2-导出订单发货信息表
     * @Date 9:40 2019/11/13
     * @Param
    **/
    /*@PostMapping(value = "/order/delivery/record/export")
    public List<DeliveryInfoExportDTO> orderDeliveryRecordExport(@RequestParam(value = "exportType") Integer exportType,
                                                                 @RequestBody DeliveryConditionDTO dto){
        return orderDeliveryService.queryDeliveryRecordList(dto);
    }*/



}
