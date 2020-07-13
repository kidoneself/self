package com.yimao.cloud.wechat.feign;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@FeignClient(name = Constant.MICROSERVICE_ORDER)
public interface OrderFeign {

//    @PostMapping(value = "/order/create/one")
//    JSONObject saveOne(@RequestBody OrderDTO orderDTO);

    /**
     * @param orderDTO
     * @param type     1-单件商品下单；2-购物车下单
     * @Description:
     * @author ycl
     * @Return: java.lang.Integer
     * @Create: 2019/4/9 10:50
     */
    @RequestMapping(value = "/order/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    JSONObject createOrder(@RequestBody(required = false) OrderDTO orderDTO,
                           @PathVariable("type") String type);


    /**
     * @param status    订单状态
     * @param beginTime 下单开始时间
     * @param endTime   下单结束时间
     * @param keys      收件人/手机号
     * @param orderId   订单号
     * @param pageNum
     * @param pageSize
     * @Description: 公众号我的订单
     * @author ycl
     * @Return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.WxOrderDTO>
     * @Create: 2019/4/11 11:13
     */
    @RequestMapping(value = "/order/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<WxOrderDTO> wxOrderList(@RequestParam(value = "userId") Integer userId,
                                   @RequestParam(required = false, value = "status") Integer status,
                                   @RequestParam(required = false, value = "beginTime") String beginTime,
                                   @RequestParam(required = false, value = "endTime") String endTime,
                                   @RequestParam(required = false, value = "keys") String keys,
                                   @RequestParam(required = false, value = "orderId") String orderId,
                                   @PathVariable(value = "pageNum") Integer pageNum,
                                   @PathVariable(value = "pageSize") Integer pageSize);


    /**
     * @param id       订单号
     * @param terminal 订单来源端
     * @return java.lang.Object
     * @description 根据订单号删除订单（逻辑删除）
     * @author zhilin.he
     * @date 2019/1/24 15:21
     */
    @RequestMapping(value = "/order/sub/{id}", method = RequestMethod.DELETE)
    void deleteOrder(@PathVariable(value = "id") Long id, @RequestParam(value = "terminal") Integer terminal);


    /**
     * @param id
     * @Description: 根据订单号查询订单
     * @author ycl
     * @Return: com.yimao.cloud.pojo.dto.order.OrderSubDTO
     * @Create: 2019/4/11 14:03
     */
    @RequestMapping(value = "/order/sub/{id}", method = RequestMethod.GET)
    OrderSubDTO findOrderInfoById(@PathVariable(value = "id") Long id);

    /**
     * @param id
     * @Description: 我的e家：待发货数量、待收货数量、客户订单数量、地址个数
     * @author ycl                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           java.lang.Integer>
     * @Create: 2019/4/12 15:37
     */
    @RequestMapping(value = "/order/count/{id}", method = RequestMethod.GET)
    Map<String, Integer> getOrderCount(@PathVariable(value = "id") Integer id);

    @RequestMapping(value = "/order/hra/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    OrderMainDTO createHRAOrder(@RequestBody OrderDTO orderDTO, @PathVariable("type") Integer type);

    /**************************************购物车接口-----start-----******************************/
    /**
     * 添加购物车
     *
     * @param dto 购物车信息
     */
    @RequestMapping(value = "/cart", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void savaShopCart(@RequestBody ShopCartDTO dto);


    /**
     * 删除购物车
     *
     * @param cartIds 所选购物车ID
     */
    @RequestMapping(value = "/cart", method = RequestMethod.DELETE)
    void deleteShopCart(@RequestParam(value = "cartIds") List<Integer> cartIds);

    /**
     * 修改购物车
     *
     * @param dto 购物车信息
     */
    @RequestMapping(value = "/cart", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateShopCart(@RequestBody ShopCartDTO dto);

    /**
     * 购物车数量添加
     *
     * @param cartId   购物车ID
     * @param addCount 添加数量
     */
    @RequestMapping(value = "/cart", method = RequestMethod.PATCH)
    void updateCount(@RequestParam(value = "cartId") Integer cartId, @RequestParam(value = "addCount") Integer addCount);

    /**
     * 查询购物车列表
     * <p>
     * //     * @param type              购物车类型：1-普通购物车；2-经销购物车；3-站长购物车；4-特批水机购物车；5-特供产品购物车；
     *
     * @param terminal          1-用户终端APP；2-微信公众号；3-经销商APP；4-小猫店小程序；
     * @param productCategoryId 产品前台一级类目ID
     */
    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    JSONObject listShopCart(@RequestParam(value = "terminal") Integer terminal,
                            @RequestParam(value = "productCategoryId", required = false) Integer productCategoryId);

    /**
     * 查询购物车产品数量
     *
     * @param type     购物车类型：1-普通购物车；2-经销购物车；3-站长购物车；4-特批水机购物车；5-特供产品购物车；
     * @param terminal 1-用户终端APP；2-微信公众号；3-经销商APP；4-小猫店小程序；
     */
    @RequestMapping(value = "/cart/count", method = RequestMethod.GET)
    Integer sumCount(@RequestParam("type") Integer type, @RequestParam("terminal") Integer terminal);

    /**************************************购物车接口-----end-----******************************/


    /**************************************收益接口-----start-----******************************/
    /**
     * 收益管理查询
     *
     * @param userId 用户ID
     */
    @RequestMapping(value = "/order/income/main", method = RequestMethod.GET)
    Map<String, Object> listIncomeByProductType(@RequestParam("userId") Integer userId, @RequestParam("incomeType") Integer incomeType);

    /**
     * 用户第一次进入收益明细,查询收益明细
     *
     * @param userId      用户id
     * @param productType 产品类型id
     * @param pageNum     查询页页码
     * @param pageSize    每页记录数
     * @return map
     */
    @RequestMapping(value = "/order/income/details/first", method = RequestMethod.GET)
    Map<String, Object> incomeDetailListFirst(@RequestParam("userId") Integer userId,
                                              @RequestParam(value = "productType", required = false) Integer productType,
                                              @RequestParam(value = "year", required = false) String year,
                                              @RequestParam(value = "month", required = false) String month,
                                              @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize);

    /**
     * 用户在收益明细中查询子用户的收益明细,查询收益明细
     *
     * @param userId      用户id
     * @param productType 产品类型id
     * @param pageNum     查询页页码
     * @param incomeType  收益类型 1-产品收益 2-续费收益
     * @param pageSize    每页记录数
     * @return map
     */
    @RequestMapping(value = "/order/income/details/second", method = RequestMethod.GET)
    Map<String, Object> incomeDetailListSecond(@RequestParam("distributorId") Integer distributorId,
                                               @RequestParam(value = "productType", required = false) Integer productType,
                                               @RequestParam(value = "year", required = false) java.lang.String year,
                                               @RequestParam(value = "month", required = false) java.lang.String month,
                                               @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize);

    /**************************************收益接口-----end-----******************************/

    /**************************************提现接口-----start-----******************************/

    /**
     * 申请提现接口,检测是否满足提现要求
     *
     * @param userId     用户信息
     * @param incomeType 收益类型 1-产品收益 2-续费收益
     * @author hhf
     * @date 2019/4/9
     */
    @RequestMapping(value = "/wxpay/withdraw/apply", method = RequestMethod.POST)
    Map<String, Object> checkCashCondition(@RequestParam("userId") Integer userId, @RequestParam("incomeType") Integer incomeType);

    /**
     * 微信提现
     *
     * @param partnerTradeNo 提现单号
     * @param amount         提现金额
     * @param userId         用户信息
     * @author hhf
     * @date 2019/4/10
     */
    @RequestMapping(value = "/wxpay/withdraw/verify", method = RequestMethod.POST)
    Boolean insertCashRecord(@RequestParam(value = "partnerTradeNo") String partnerTradeNo,
                             @RequestParam(value = "amount") BigDecimal amount,
                             @RequestParam(value = "userId") Integer userId,
                             @RequestParam(value = "incomeType") Integer incomeType);

    /**
     * 提现记录列表
     *
     * @param pageNum          分页页数
     * @param pageSize         分页大小
     * @param withdrawQueryDTO 查询信息
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/4/24
     */
    @RequestMapping(value = "/withdraw/record/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = "application/json")
    PageVO<WithdrawSubDTO> withdrawRecordList(@PathVariable(value = "pageNum") Integer pageNum,
                                              @PathVariable(value = "pageSize") Integer pageSize,
                                              @RequestBody WithdrawQueryDTO withdrawQueryDTO);


    /**************************************提现接口-----start-----******************************/


    /**
     * @param userId
     * @param orderKeys
     * @param beginTime
     * @param endTime
     * @param keys
     * @param orderId
     * @param pageNum
     * @param pageSize
     * @Description: 客户订单列表
     * @author ycl
     * @create: 2019/4/22 14:45
     */
    @RequestMapping(value = "/order/audit/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<WxOrderDTO> auditList(@RequestParam(value = "userId") Integer userId,
                                 @RequestParam(required = false, value = "orderKeys") String orderKeys,
                                 @RequestParam(required = false, value = "beginTime") String beginTime,
                                 @RequestParam(required = false, value = "endTime") String endTime,
                                 @RequestParam(required = false, value = "keys") String keys,
                                 @RequestParam(required = false, value = "orderId") String orderId,
                                 @PathVariable(value = "pageNum") Integer pageNum,
                                 @PathVariable(value = "pageSize") Integer pageSize);


    //==============================微信支付开始============================================================

    /**
     * 微信统一下单
     *
     * @param unifiedOrder 请求对象
     * @return
     */
    @PostMapping(value = "/wxpay/unified/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    Object unifiedorder(@RequestBody WechatPayRequest payRequest);

    /**
     * 公众号微信支付回调
     *
     * @param wxpayCallbackResult 微信回调的入参信息
     */
    @GetMapping(value = "/wxjsapipay/notify")
    String wxJsapiPayCallback(@RequestParam(value = "wxpayCallbackResult") String wxpayCallbackResult);

    /**
     * 支付订单查询
     *
     * @param orderQuery 查询实体类
     * @return obj
     */
    @RequestMapping(value = "/wxpay/query/order", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object orderQuery(@RequestBody WechatPayRequest payRequest);

    //==============================微信支付结束============================================================

    /**
     * @param orderId
     * @param activityStatus
     * @Description: 客户订单审核是否通过
     * @author ycl
     * @Return: java.lang.String
     * @Create: 2019/4/29 17:00
     */
    @RequestMapping(value = "/order/auditOrder", method = RequestMethod.GET)
    String auditOrder(@RequestParam(value = "orderId") Long orderId, @RequestParam(value = "activityStatus") Integer activityStatus);

    /**
     * 订单配置信息
     *
     * @return config
     * @author liuhao@yimaokeji.com
     */
    @GetMapping(value = "/order/orderConfig")
    OrderConfigDTO getOrderConfig();

    /**
     * @param operationType 操作类型
     * @param pageNum       当前页
     * @param pageSize      每页显示条数
     * @return java.lang.Object
     * @description 查询经销商app-我的订单列表根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/12 13:48
     */
    @RequestMapping(value = "/order/sub/app/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<OrderSubListDTO> myOrderList(@RequestParam(value = "operationType") Integer operationType,
                                        @RequestParam(value = "timeType", required = false) Integer timeType,
                                        @RequestParam(value = "productName", required = false) String productName,
                                        @RequestParam(value = "addresseeName", required = false) String addresseeName,
                                        @RequestParam(value = "addresseePhone", required = false) String addresseePhone,
                                        @PathVariable(value = "pageNum") Integer pageNum,
                                        @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 查询客户订单列表根据产品一级类目和订单状态
     */
    @GetMapping(value = "/order/sub/app/customer/{pageNum}/{pageSize}")
    PageVO<OrderSubListDTO> myCustomerOrderSubList(@RequestParam(value = "userId", required = false) Integer userId,
                                                   @RequestParam(value = "productFirstCategoryId", required = false) Integer productFirstCategoryId,
                                                   @RequestParam(value = "operationType", required = false) Integer operationType,
                                                   @RequestParam(value = "productMode", required = false) Integer productMode,
                                                   @RequestParam(value = "timeType", required = false) Integer timeType,
                                                   @RequestParam(value = "productName", required = false) String productName,
                                                   @RequestParam(value = "addresseeName", required = false) String addresseeName,
                                                   @RequestParam(value = "addresseePhone", required = false) String addresseePhone,
                                                   @RequestParam(value = "source", required = false) Integer source,
                                                   @RequestParam(value = "queryType", required = false) Integer queryType,
                                                   @RequestParam(value = "subDistributorId", required = false) Integer subDistributorId,
                                                   @PathVariable(value = "pageNum") Integer pageNum,
                                                   @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 填写订单--发票须知
     */
    @GetMapping(value = "/order/invoiceNotes")
    JSONObject orderInvoiceNotes();
}
