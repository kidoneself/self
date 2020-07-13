package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.OrderFrom;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.hra.HraCardDTO;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.wechat.feign.HraFeign;
import com.yimao.cloud.wechat.feign.OrderFeign;
import com.yimao.cloud.wechat.feign.SystemFeign;
import com.yimao.cloud.wechat.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Slf4j
@Api(tags = "OrderController")
public class OrderController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserCache userCache;
    @Resource
    private UserFeign userFeign;
    @Resource
    private HraFeign hraFeign;
    
    @Resource
    private SystemFeign systemFeign;


    /**
     * @param type 1-单件商品下单；2-购物车下单
     * @Description:
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/4/9 10:48
     */
    @PostMapping(value = "/order/{type}")
    @ApiOperation(value = "下单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderDTO", value = "订单信息", required = true, dataType = "OrderDTO", paramType = "body"),
            @ApiImplicitParam(name = "type", value = "下单类型", required = true, dataType = "String", paramType = "path")
    })
    public ResponseEntity createOrder(@RequestBody OrderDTO orderDTO, @PathVariable("type") String type) {
        if (orderDTO == null) {
            throw new BadRequestException("订单不能为空！");
        }

        //区分公众号下单，还是H5下单
        if (Objects.nonNull(orderDTO.getTerminal())) {
            orderDTO.setTerminal(OrderFrom.H5.value);
        } else {
            orderDTO.setTerminal(OrderFrom.WECHAT.value);
        }
        orderDTO.setPayTerminal(1);
        return ResponseEntity.ok(orderFeign.createOrder(orderDTO, type));
    }


    /**
     * @param operationType 操作类型
     * @param pageNum       当前页
     * @param pageSize      每页显示条数
     * @return java.lang.Object
     * @description 经销商app-我的订单列表根据订单状态
     * @author zhilin.he
     * @date 2019/1/12 13:48
     */
    @GetMapping(value = "/order/sub/app/{pageNum}/{pageSize}")
    @ApiOperation(value = "我的订单", notes = "查询订单列表根据订单状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "operationType", defaultValue = "-1", value = "操作类型: -1-全部 0-待付款 1-待发货 2-待收货 3-已完成 4-退款/退货/待审核 5-待退货 6-待退款 7-取消记录", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "timeType", value = "下单时间类型：1、昨日；2、上周；3、30天内；4、3个月内；5、今年；6、上一年", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productName", value = "产品名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addresseeName", value = "收货人姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addresseePhone", value = "手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")})
    public ResponseEntity<PageVO<OrderSubListDTO>> myOrderList(@RequestParam(value = "operationType") Integer operationType,
                                                               @RequestParam(value = "timeType", required = false) Integer timeType,
                                                               @RequestParam(value = "productName", required = false) String productName,
                                                               @RequestParam(value = "addresseeName", required = false) String addresseeName,
                                                               @RequestParam(value = "addresseePhone", required = false) String addresseePhone,
                                                               @PathVariable(value = "pageNum") Integer pageNum,
                                                               @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<OrderSubListDTO> map = orderFeign.myOrderList(operationType, timeType, productName, addresseeName, addresseePhone, pageNum, pageSize);
        return ResponseEntity.ok(map);
    }


    /**
     * 查询客户订单列表:根据产品一级类目和订单状态
     */
    @GetMapping(value = "/order/sub/app/customer/{pageNum}/{pageSize}")
    @ApiOperation(value = "我的客户订单", notes = "查询客户订单列表根据产品一级类目和订单状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productFirstCategoryId", value = "产品一级类目id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "operationType", defaultValue = "-1", value = "操作类型:  -1-全部 0-待付款 1-待发货/待接单 2-待收货 3-已完成", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productMode", value = "产品模式：1-实物；2-虚拟；3-租赁；", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "timeType", value = "下单时间类型：1、昨日；2、上周；3、30天内；4、3个月内；5、今年；6、上一年", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productName", value = "产品名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addresseeName", value = "收货人姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addresseePhone", value = "手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryType", value = "查询类型 0-全部（默认） 1-主账户 2-子账户", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "subDistributorId", value = "经销商子账号ID，当queryType=2时，此为必传", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")
    })
    public ResponseEntity<PageVO<OrderSubListDTO>> myCustomerOrderSubList(@RequestParam(value = "userId", required = false) Integer userId,
                                                                          @RequestParam(value = "productFirstCategoryId", required = false) Integer productFirstCategoryId,
                                                                          @RequestParam(value = "operationType") Integer operationType,
                                                                          @RequestParam(value = "productMode", required = false) Integer productMode,
                                                                          @RequestParam(value = "timeType", required = false) Integer timeType,
                                                                          @RequestParam(value = "productName", required = false) String productName,
                                                                          @RequestParam(value = "addresseeName", required = false) String addresseeName,
                                                                          @RequestParam(value = "addresseePhone", required = false) String addresseePhone,
                                                                          @RequestParam(value = "queryType", required = false) Integer queryType,
                                                                          @RequestParam(value = "subDistributorId", required = false) Integer subDistributorId,
                                                                          @PathVariable(value = "pageNum") Integer pageNum,
                                                                          @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<OrderSubListDTO> map = orderFeign.myCustomerOrderSubList(userId, productFirstCategoryId, operationType, productMode, timeType, productName, addresseeName, addresseePhone, 1, queryType, subDistributorId, pageNum, pageSize);
        return ResponseEntity.ok(map);
    }


    /**
     * @param id       订单号
     * @param terminal 订单来源端
     * @Description: 根据订单号删除订单（逻辑删除）
     * @author ycl
     */
    @DeleteMapping(value = {"/order/sub/{id}"})
    @ApiOperation(value = "根据订单号删除订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "terminal", value = "订单来源端", dataType = "Long", required = true, paramType = "query")
    })
    public ResponseEntity deleteOrder(@PathVariable("id") Long id, @RequestParam Integer terminal) {
        orderFeign.deleteOrder(id, terminal);
        return ResponseEntity.noContent().build();
    }


    /**
     * @param id 主键
     * @Description: 根据订单号查询订单
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/4/11 14:04
     */
    @GetMapping(value = {"/order/sub/{id}"})
    @ApiOperation(value = "根据订单号查询订单")
    @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public ResponseEntity<OrderSubDTO> findOrderInfoById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderFeign.findOrderInfoById(id));
    }


    /**
     * @Description: 我的订单：待发货数量、待收货数、收货地址数、客户订单总数
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/4/11 17:35
     */
    @GetMapping(value = "/order/count")
    @ApiOperation(value = "我的订单：待发货数量、待收货数、收货地址数、客户订单总数")
    public ResponseEntity getOrderCount() {
        Integer userId = userCache.getUserId();
        Map<String, Integer> map = orderFeign.getOrderCount(userId);
        return ResponseEntity.ok(map);
    }

    /**
     * 50元优惠券下单
     *
     * @param orderDTO 订单对象
     * @param type     1-单张下单；2-批量下单
     */
    @RequestMapping(value = "/order/hra/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "50元优惠券下单")
    public OrderMainDTO createMCardOrder(@RequestBody OrderDTO orderDTO, @PathVariable Integer type) {
        userCache.getUserId();
        return orderFeign.createHRAOrder(orderDTO, type);
    }

    /**
     * @param orderId  订单ID
     * @param pageNum  页码
     * @param pageSize 页数
     * @Description: 根据订单号查询评估卡列表
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/4/19 9:48
     */
    @RequestMapping(value = "/order/hraCard/{pageNum}/{pageSize}", method = RequestMethod.GET)
    @ApiOperation(value = "根据订单号查询评估卡列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单ID", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path")
    })
    public ResponseEntity hraCardByOrderId(@RequestParam Long orderId,
                                           @RequestParam Integer userId,
                                           @PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize) {

        PageVO<HraCardDTO> pageVO = hraFeign.listCardByUserId(orderId, userId, pageNum, pageSize);
        return ResponseEntity.ok(pageVO);
    }

    /**
     * 根据省市区查询安装工列表（MySQL）
     *
     * @param province
     * @param city
     * @param region
     */
    @GetMapping(value = "/engineers")
    @ApiOperation(value = "根据省市区查询安装工列表（MySQL）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", required = true, dataType = "String", paramType = "query")
    })
    public Object listEngineerByPCR(@RequestParam String province,
                                    @RequestParam String city,
                                    @RequestParam String region) {
    	
    	Integer areaId=systemFeign.getRegionIdByPCR(province, city, region);
    	if(null==areaId) {
    		return null;
    	}
        List<EngineerDTO> engineers = userFeign.listEngineerByRegion(areaId);
        if (CollectionUtil.isEmpty(engineers)) {
            return null;
        }
        List<EngineerDTO> voList = new ArrayList<>();
        for (EngineerDTO dto : engineers) {
            EngineerDTO engineer = new EngineerDTO();
            engineer.setId(dto.getId());
            engineer.setRealName(dto.getRealName());
            engineer.setPhone(dto.getPhone());
            voList.add(engineer);
        }
        return voList;
    }


    /**
     * 订单配置
     *
     * @return orderConfigDTO
     */
    @GetMapping(value = "/order/orderConfig")
    @ApiOperation(value = "订单配置信息")
    public OrderConfigDTO getOrderConfig() {
        return orderFeign.getOrderConfig();
    }


    /**
     * 废弃[暂时保留]
     *
     * @param status    0-待付款 2-待发货 4-待收货 5-交易成功
     * @param beginTime 下单开始时间
     * @param endTime   下单结束时间
     * @param keys      收货人姓名/手机号
     * @param orderId   订单号
     * @Description: 根据订单状态查询订单(查询所有)
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/4/10 11:23
     */
    @GetMapping("/order/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询订单列表根据订单查询条件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "订单状态:0-待付款 2-待发货 4-待收货 5-交易成功", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "下单开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "下单结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "keys", value = "收货人姓名/手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")})
    public ResponseEntity orderList(@RequestParam(required = false) Integer status,
                                    @RequestParam(required = false) String beginTime,
                                    @RequestParam(required = false) String endTime,
                                    @RequestParam(required = false) String keys,
                                    @RequestParam(required = false) String orderId,
                                    @PathVariable(value = "pageNum") Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize) {
        UserDTO userDTO = userFeign.getBasicUserById(userCache.getUserId());
        if (Objects.isNull(userDTO)) {
            throw new YimaoException("未登陆,请登陆");
        }
        PageVO<WxOrderDTO> pageVO = orderFeign.wxOrderList(userDTO.getId(), status, beginTime, endTime, keys, orderId, pageNum, pageSize);
        return ResponseEntity.ok(pageVO);
    }


    /**
     * @Description: 客户订单列表 （废弃[暂时保留]）
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/4/22 14:40
     */
    @GetMapping("/order/audit/{pageNum}/{pageSize}")
    @ApiOperation(value = "客户订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "下单开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "下单结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "keys", value = "收货人姓名/手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderKeys", value = "下单人(e家号/手机号/昵称)", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")})
    public ResponseEntity auditList(@RequestParam(required = false) String beginTime,
                                    @RequestParam(required = false) String endTime,
                                    @RequestParam(required = false) String keys,
                                    @RequestParam(required = false) String orderKeys,
                                    @RequestParam(required = false) String orderId,
                                    @PathVariable(value = "pageNum") Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize) {
        UserDTO userDTO = userFeign.getBasicUserById(userCache.getUserId());
        if (Objects.isNull(userDTO)) {
            throw new YimaoException("未登陆,请登陆");
        }

        PageVO<WxOrderDTO> pageVO = orderFeign.auditList(userDTO.getId(), orderKeys, beginTime, endTime, keys, orderId, pageNum, pageSize);
        return ResponseEntity.ok(pageVO);
    }
}
