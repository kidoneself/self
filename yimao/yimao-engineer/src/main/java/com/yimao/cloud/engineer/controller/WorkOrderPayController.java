package com.yimao.cloud.engineer.controller;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.AliConstant;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.enums.OrderType;
import com.yimao.cloud.base.enums.PayPlatform;
import com.yimao.cloud.base.enums.PayReceiveType;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.engineer.enums.ApiStatusCode;
import com.yimao.cloud.engineer.feign.OrderFeign;
import com.yimao.cloud.engineer.feign.UserFeign;
import com.yimao.cloud.engineer.service.WorkOrderBusinessService;
import com.yimao.cloud.engineer.utils.ApiResult;
import com.yimao.cloud.engineer.utils.ResultUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.order.AliPayRequest;
import com.yimao.cloud.pojo.dto.order.PayAccountDetail;
import com.yimao.cloud.pojo.dto.order.WechatPayRequest;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/***
 * 安装工工单支付
 *
 * @author zhangbaobao
 */
@RestController
@Api(tags = "WorkOrderPayController")
@Slf4j
public class WorkOrderPayController {

	@Resource
	private OrderFeign orderFeign;

	@Resource
	private UserFeign userFeign;

	@Resource
	private DomainProperties domainProperties;

	@Resource
	private WorkOrderBusinessService workOrderBusinessService;
	@Resource
	private RedisCache redisCache;

	/**
	 * 扫码支付获取二维码链接 (支付宝、微信统一)
	 *
	 * @param paymentId 工单号
	 */
	@GetMapping(value = "/pay/getCodeUrl")
	@ApiOperation(value = "扫码支付获取二维码链接 (支付宝、微信统一)")
	public Map<String, Object> getCodeUrl(HttpServletRequest request, @RequestParam(required = false) String body,
			@RequestParam(required = false) String spbill_create_ip, @RequestParam String paymentId) {
		try {
			log.info("============安装工获取支付二维码参数====paymentId" + paymentId + ",body=" + body + ",spbill_create_ip="
					+ spbill_create_ip);
			WorkOrderDTO workOrder = orderFeign.getWorkOrderById(paymentId);
			if (workOrder == null) {
				return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
			}
			if (workOrder.getPay() != null && workOrder.getPay()) {
				return ApiResult.error(request, "184", "工单已支付");
			}
			String randomNum = getRandomNum();
			redisCache.set(paymentId, randomNum);
			// 微信获取支付二维码
			WechatPayRequest payRequest = new WechatPayRequest();
			payRequest.setOut_trade_no(paymentId + randomNum);
			payRequest.setTotal_fee(workOrder.getFee().doubleValue());
			payRequest.setBody(Constant.PAY_BODY);
			payRequest.setSpbillCreateIp(spbill_create_ip);
			payRequest.setTradeType(WechatConstant.NATIVE);
			payRequest.setNotifyUrl(domainProperties.getApi() + WechatConstant.WXPAY_NOTIFY_URL_FOUR);
			payRequest.setOrderType(OrderType.WORKORDER.value);
			payRequest.setPlatform(PayPlatform.WECHAT.value);
			payRequest.setClientType(SystemType.ENGINEER.value);
			payRequest.setReceiveType(PayReceiveType.ONE.value);
			String code_url = orderFeign.wechatScanCodePay(payRequest);
			Map<String, Object> data = new HashMap<>();
			if (StringUtils.isNotEmpty(code_url)) {
				data.put("wx_codeUrl", code_url);
			}
			// 支付宝获取二维码
			AliPayRequest aliPayRequest = new AliPayRequest();
			aliPayRequest.setOutTradeNo(paymentId + randomNum);
			aliPayRequest.setTotalAmount(workOrder.getFee().doubleValue());
			aliPayRequest.setSubject(Constant.PAY_BODY);
			aliPayRequest.setNotifyUrl(domainProperties.getApi() + AliConstant.ALIPAY_NOTIFY_URL_FOUR);
			aliPayRequest.setOrderType(OrderType.WORKORDER.value);
			aliPayRequest.setPlatform(PayPlatform.ALI.value);
			aliPayRequest.setClientType(SystemType.ENGINEER.value);
			aliPayRequest.setReceiveType(PayReceiveType.ONE.value);
			String qrCode = orderFeign.aliScanCodePay(aliPayRequest);
			if (StringUtil.isNotEmpty(qrCode)) {
				data.put("alipay_codeUrl", qrCode);
			}
			return ApiResult.result(request, data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
		}
	}

	/**
	 * 查询支付状态
	 *
	 * @param paymentId 工单号
	 */
	@GetMapping(value = "/pay/payStatus")
	@ApiOperation(value = "查询支付状态")
	public Map<String, Object> payStatus(HttpServletRequest request, @RequestParam String paymentId) {
		try {
			Thread.sleep(1000);
			WorkOrderDTO workorder = orderFeign.getWorkOrderById(paymentId);
			if (null != workorder) {
				return ApiResult.result(request, workorder.getPay());
			}
			return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ApiResult.result(request, false);
		}
	}

	/**
	 * 微信APP支付
	 */
	@PostMapping(value = "/wxpay/unified/order", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "微信APP支付")
	public Object wxpayapp(@RequestBody WechatPayRequest payRequest) {
		log.info("=======工单支付微信app支付开始====" + JSONObject.toJSONString(payRequest));
		WorkOrderDTO workOrder = orderFeign.getWorkOrderById(payRequest.getOut_trade_no());
		if (workOrder == null) {
			throw new YimaoException("工单信息不存在");
		}

		if (workOrder.getPay() != null && workOrder.getPay()) {
			throw new YimaoException("工单已支付");
		}

		// 校验
		if (StringUtil.isBlank(payRequest.getOut_trade_no())) {
			throw new YimaoException("订单号不能为空！");
		}
		if (payRequest.getTotal_fee() == null) {
			throw new YimaoException("订单支付金额不能为空！");
		}
		payRequest.setOut_trade_no(payRequest.getOut_trade_no() + "" + getRandomNum());
		payRequest.setTradeType(WechatConstant.APP);
		payRequest.setNotifyUrl(domainProperties.getApi() + WechatConstant.WXPAY_NOTIFY_URL_FOUR);
		payRequest.setOrderType(OrderType.WORKORDER.value);

		payRequest.setPlatform(PayPlatform.WECHAT.value);
		payRequest.setClientType(SystemType.ENGINEER.value);
		payRequest.setReceiveType(PayReceiveType.ONE.value);
		if (SpringContextHolder.getEnvironment().equalsIgnoreCase(EnvironmentEnum.PRO.code)) {
			payRequest.setBody("翼猫APP商品");
		} else {
			payRequest.setBody("翼猫APP商品测试");
		}
		return orderFeign.unifiedorder(payRequest);
	}

	/**
	 * 微信订单查询
	 */
	@PostMapping(value = "/wxpay/query/order", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "微信订单查询")
	@ApiImplicitParam(name = "payRequest", value = "支付订单查询实体类", required = true, dataType = "WechatPayRequest", paramType = "body")
	public Object orderQuery(@RequestBody WechatPayRequest payRequest) {
		payRequest.setTradeType(WechatConstant.APP);
		return orderFeign.orderQuery(payRequest);
	}

	/**
	 * 支付宝APP支付
	 */
	@PostMapping(value = "/alipay/tradeapp", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "支付宝APP支付")
	@ApiImplicitParam(name = "payRequest", value = "支付宝支付下单请求参数", required = true, dataType = "AliPayRequest", paramType = "body")
	public Object alipayapp(@RequestBody AliPayRequest payRequest) {

		// 校验
		if (StringUtil.isBlank(payRequest.getOutTradeNo())) {
			throw new YimaoException("订单号不能为空！");
		}
		if (payRequest.getTotalAmount() == null) {
			throw new YimaoException("订单支付金额不能为空！");
		}
		if (SpringContextHolder.getEnvironment().equalsIgnoreCase(EnvironmentEnum.PRO.code)) {
			payRequest.setSubject("APP端商品下单");
		} else {
			payRequest.setSubject("APP支付测试");
		}

		WorkOrderDTO workOrder = orderFeign.getWorkOrderById(payRequest.getOutTradeNo());
		if (workOrder == null) {
			throw new YimaoException("工单不存在");
		}

		if (workOrder.getPay() != null && workOrder.getPay()) {
			throw new YimaoException("工单已支付");
		}
		payRequest.setOutTradeNo(payRequest.getOutTradeNo() + "" + getRandomNum());
		payRequest.setNotifyUrl(domainProperties.getApi() + AliConstant.ALIPAY_NOTIFY_URL_ONE);
		payRequest.setOrderType(OrderType.WORKORDER.value);

		payRequest.setPlatform(PayPlatform.ALI.value);
		payRequest.setClientType(SystemType.ENGINEER.value);
		payRequest.setReceiveType(PayReceiveType.ONE.value);
		return orderFeign.tradeapp(payRequest);
	}

	/**
	 * 支付宝订单查询
	 */
	@PostMapping(value = "/alipay/query/order", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "支付宝订单查询")
	@ApiImplicitParam(name = "payRequest", value = "支付宝支付下单请求参数", required = true, dataType = "AliPayRequest", paramType = "body")
	public Object tradequeryOrder(@RequestBody AliPayRequest payRequest) {
		// 校验
		if (StringUtil.isBlank(payRequest.getOutTradeNo())) {
			throw new YimaoException("订单号不能为空！");
		}
		return orderFeign.tradequery(payRequest);
	}

	/***
	 * 支付上传
	 * 
	 * @param workOrderId
	 * @param otherPayType
	 * @param file1
	 * @param file2
	 * @param file3
	 * @return
	 */
	@PostMapping(value = "/pay/other")
	@ApiOperation(value = "其他支付提交")
	public void otherPay(@RequestParam("workOrderId") String workOrderId,
			@RequestParam("otherPayType") Integer otherPayType, @RequestPart("file1") MultipartFile file1,
			@RequestPart(required = false, value = "file2") MultipartFile file2,
			@RequestPart(required = false, value = "file3") MultipartFile file3) {
		workOrderBusinessService.otherPay(workOrderId, otherPayType, file1, file2, file3);

	}


	/**
	 * 获取线下支付账号
	 */
	@GetMapping(value = "/pay/order/otherPayAccountInfo")
	@ResponseBody
	@ApiOperation(value = "获取线下支付账号")
	public Map<String, Object> otherPayAccountInfo(@RequestParam("workOrderId") String workOrderId) {
		Map<String, Object> ru = new HashMap<>();
		ResultUtil.success(ru);

		// 校验工单是否已支付
		WorkOrderDTO workOrder = orderFeign.getWorkOrderById(workOrderId);
		if (workOrder == null) {
			ResultUtil.error(ru, "59", "工单不存在");
			return ru;
		}
		if (workOrder.getPay() != null && workOrder.getPay()) {
			ResultUtil.error(ru, "59", "工单已支付");
			return ru;
		}

		Map<String, Object> data = new HashMap<>();
		PayAccountDetail payAccountDetail = orderFeign.getPayAccount(10000, PayType.OTHER.value,
				SystemType.ENGINEER.value, PayReceiveType.ONE.value);// 获取线下支付账号信息
		data.put("account", payAccountDetail.getBankAccount());// 收款账号
		data.put("userName", payAccountDetail.getCompanyName());// 收款人
		data.put("bankName", payAccountDetail.getBankName());// 开户行
		ru.put("data", data);
		return ru;
	}

	public String getRandomNum() {
		int randomNum = (int) (Math.random() * 900 + 100);
		System.out.println(randomNum);
		return Integer.toString(randomNum);
	}
}
