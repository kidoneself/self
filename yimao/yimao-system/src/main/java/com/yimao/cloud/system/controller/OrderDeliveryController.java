package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.OrderFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shnehuiyang
 * @date 2018/12/27.
 */
@RestController
@Slf4j
@Api(tags = "OrderDeliveryController")
public class OrderDeliveryController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserCache userCache;

    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 添加订单收货信息
     *
     * @return
     */
    @PostMapping(value = "/order/delivery")
    @ApiOperation(value = "添加订单收货信息", notes = "添加订单收货信息")
    @ApiImplicitParam(name = "orderDelivery", value = "订单发货单实体类", required = true, dataType = "OrderDeliveryDTO", paramType = "body")
    public Object add(@RequestBody OrderDeliveryDTO orderDelivery) {
        orderFeign.addOrderDelivery(orderDelivery);
        return ResponseEntity.noContent().build();
    }

    /**
     * 导入物流跟踪信息
     *
     * @return
     */
    @RequestMapping(value = "/order/delivery/import", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "导入物流跟踪信息", notes = "导入物流跟踪信息")
    public Object importExcel(@RequestPart("multipartFile") MultipartFile multipartFile) {
        return orderFeign.importExcel(multipartFile);
    }


    /**
     * 查询物流信息
     *
     * @return
     */
    @GetMapping(value = "/order/delivery/query")
    @ApiOperation(value = "查询物流信息", notes = "查询物流信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logisticsNo", value = "物流单号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "子订单号", required = true, dataType = "Long", paramType = "query")
    })
    public Object query(@RequestParam(value = "logisticsNo") String logisticsNo,
                        @RequestParam(value = "orderId") Long orderId) {
        return ResponseEntity.ok(orderFeign.query(logisticsNo, orderId));
    }


    /**
     * 发货列表查询
     *
     * @return
     */
    @PostMapping(value = "/order/delivery/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "发货列表查询", notes = "发货列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", defaultValue = "1", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", defaultValue = "10", required = true, paramType = "path"),
            @ApiImplicitParam(name = "deliveryDTO", value = "查询条件实体类", dataType = "DeliveryDTO", paramType = "body")
    })
    public ResponseEntity<PageVO<DeliveryInfoDTO>> list(@RequestBody DeliveryDTO deliveryDTO,
                                                        @PathVariable(value = "pageNum") Integer pageNum,
                                                        @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<DeliveryInfoDTO> orderList = orderFeign.list(deliveryDTO, pageNum, pageSize);
        return ResponseEntity.ok(orderList);
    }


    /**
     * @Author ycl
     * @Description 发货列表-订单发货
     * @Date 14:29 2019/7/30
     * @Param
     **/
    @PatchMapping(value = "/order/delivery/{id}")
    @ApiOperation(value = "发货列表设置发货", notes = "发货列表设置发货")
    @ApiImplicitParam(name = "id", value = "订单id", required = true, dataType = "String", paramType = "path")
    public Object setDelivery(@PathVariable(value = "id") String id) {
        DeliveryAddressDTO dto = orderFeign.getDeliveryAddress(userCache.getUserId());
        if (null == dto) {
            throw new YimaoException("请先配置发货地址，方可进行发货");
        }
        if(StringUtil.isEmpty(id)){
            throw new YimaoException("传入订单号为空");
        }
        orderFeign.setDelivery(Long.valueOf(id));
        return ResponseEntity.noContent().build();
    }

    /**
     * @Author ycl
     * @Description 发货列表--批量发货
     * @Date 15:04 2019/7/30
     * @Param
     **/
    @PatchMapping(value = "/order/delivery/batch")
    @ApiOperation(value = "发货列表批量发货", notes = "发货列表批量发货")
    @ApiImplicitParam(name = "ids", value = "订单ids", required = true, allowMultiple = true, dataType = "String", paramType = "query")
    public Object batchDelivery(@RequestParam(value = "ids") List<String> ids) {
        DeliveryAddressDTO dto = orderFeign.getDeliveryAddress(userCache.getUserId());
        if (null == dto) {
            throw new YimaoException("请先配置发货地址，方可进行发货");
        }
        List<Long> orderIds = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(ids)){
            orderIds = ids.stream().map(Long::parseLong).collect(Collectors.toList());
            orderFeign.batchDelivery(orderIds);
            return ResponseEntity.noContent().build();
        }

        throw new YimaoException("传入订单号为空");
    }


    /**
     * @Author ycl
     * @Description 发货列表-发货记录
     * @Date 14:37 2019/8/21
     * @Param
     **/
    @GetMapping(value = "/order/delivery/record/{pageNum}/{pageSize}")
    @ApiOperation(value = "发货记录列表查询", notes = "发货记录列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, defaultValue = "1", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, defaultValue = "10", paramType = "path"),
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "logisticsNo", value = "快递单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "发货开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "发货结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "addreessName", value = "收货人姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "terminal", value = "来源端：1-健康e家公众号 2-经销商APP 3-净水设备", dataType = "Long", paramType = "query")
    })
    public ResponseEntity<PageVO<OrderDeliveryRecordDTO>> deliveryRecordList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                             @PathVariable(value = "pageSize") Integer pageSize,
                                                                             @RequestParam(value = "orderId", required = false) String orderId,
                                                                             @RequestParam(value = "logisticsNo", required = false) String logisticsNo,
                                                                             @RequestParam(value = "startTime", required = false) String startTime,
                                                                             @RequestParam(value = "endTime", required = false) String endTime,
                                                                             @RequestParam(value = "userId", required = false) Integer userId,
                                                                             @RequestParam(value = "addreessName", required = false) String addreessName,
                                                                             @RequestParam(value = "terminal", required = false) Integer terminal) {
        PageVO<OrderDeliveryRecordDTO> pageVO = orderFeign.deliveryRecordList(pageNum, pageSize, orderId, logisticsNo, startTime, endTime, userId, addreessName, terminal);
        return ResponseEntity.ok(pageVO);
    }


    /**
     * @Author ycl
     * @Description 发货记录详情
     * @Date 18:11 2019/8/26
     * @Param
     **/
    @GetMapping(value = "/order/delivery/record/{id}")
    @ApiOperation(value = "发货记录详情", notes = "发货记录详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录id", dataType = "Long", required = true, paramType = "path")
    })
    public ResponseEntity<DeliveryDetailInfoDTO> deliveryRecordDetail(@PathVariable(value = "id") Integer id) {
        DeliveryDetailInfoDTO detailInfoDTO = orderFeign.deliveryRecordDetail(id);
        return ResponseEntity.ok(detailInfoDTO);
    }


    /**
     * @Author ycl
     * @Description 发货列表中相关导出
     * @Date 15:08 2019/8/23
     * @Param
     **/
    @PostMapping(value = "/order/delivery/export")
    @ApiOperation(value = "1-发货台账登记导出 2-订单发货信息导出 3-快递导出", notes = "1-发货台账登记导出 2-订单发货信息导出 3-快递导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exportType", value = "导出类别", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dto", value = "查询条件实体", dataType = "DeliveryConditionDTO", paramType = "body")
    })
    public Object deliveryExport(@RequestParam(value = "exportType") Integer exportType,
                               @RequestBody(required = false) DeliveryConditionDTO query) {
        query.setExportType(exportType);
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/order/delivery/export";
        String title = null;
        if (exportType == 1) {
            title = "发货台账登记导出";
        }
        if (exportType == 2) {
            title = "订单发货信息导出";
        }
        if (exportType == 3) {
            title = "快递打印表导出";
        }
        ExportRecordDTO record = exportRecordService.save(url, title);
        query.setExportType(exportType);
        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record != null && record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }

        return CommResult.ok(record.getId());

//
//        List<Object> list = orderFeign.deliveryExport(exportType, dto);
//        if (CollectionUtil.isEmpty(list)) {
//            throw new NotFoundException("未找到数据");
//        }
//
//        String[] titles = null;
//        String[] beanPropertys = null;
//        String header = "";
//        if (exportType == 1) {
//
//            header = "发货台账登记表";
//
//            beanPropertys = new String[]{"deliveryTime", "orderId", "stationCompanyName", "logisticsCompany",
//                    "logisticsNo", "addressee", "addresseeName", "addresseePhone", "num", "boxNum", "weigh", "payType",
//                    "logisticsFee", "remark"};
//
//            titles = new String[]{"发货日期", "子订单号", "服务站公司名称", "快递公司", "快递单号", "收货人地址", "收货人",
//                    "收货人联系方式", "发货件数", "数量/盒", "重量", "寄付方式", "快递费", "备注"};
//
//        } else if (exportType == 2) {
//
//            header = "订单发货信息表";
//
//            beanPropertys = new String[]{"orderId", "productCategoryName", "count", "addresseeProvince",
//                    "addresseeCity", "addresseeRegion", "addresseeStreet", "addresseeName", "addresseePhone", "stationCompanyName", "remark"};
//
//            titles = new String[]{"子订单号", "产品型号", "产品数量", "收货人省", "收货人市", "收货人区", "收货人街道",
//                    "收货人", "联系方式", "服务站公司名称", "备注"};
//
//        } else if (exportType == 3) {
//
//            header = "快递打印表";
//
//            beanPropertys = new String[]{"logisticsNo", "orderId", "productName", "count", "num", "weigh", "payType",
//                    "logisticsFee", "sendCompany", "sender", "senderAddress", "senderPhone", "receiveCompany", "address",
//                    "addressName", "addressPhone", "", "remark"};
//
//            titles = new String[]{"快递单号", "子订单号", "商品名称", "数量", "件数", "重量", "付款方式", "到付款",
//                    "寄件公司", "寄件人", "寄件地址", "邮寄人联系方式", "收货公司", "收货地址", "收货人", "收货人联系方式", "收货固话", "备注"};
//        }
//
//        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanPropertys, response);
//        if (boo) {
//            return ResponseEntity.noContent().build();
//        }
//        throw new YimaoException("导出失败");

    }


    /**
     * @Author ycl
     * @Description 发货列表-发货记录相关导出
     * @Date 11:01 2019/11/13
     * @Param
     **/
    @PostMapping(value = "/order/delivery/record/export")
    @ApiOperation(value = "1-发货台账登记导出 2-订单发货信息导出 ", notes = "1-发货台账登记导出 2-订单发货信息导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exportType", value = "导出类别", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dto", value = "查询条件实体", dataType = "DeliveryConditionDTO", paramType = "body")
    })
    public Object orderDeliveryRecordExport(@RequestParam(value = "exportType") Integer exportType,
                                            @RequestBody DeliveryConditionDTO query) {
        query.setExportType(exportType);
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/order/delivery/record/export";
        String title = null;
        if (exportType == 1) {

            title = "发货台账登记导出";

        } else if (exportType == 2) {

            title = "订单发货信息导出";
        }

        ExportRecordDTO record = exportRecordService.save(url, title);
        query.setExportType(exportType);
        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());
    }

}
