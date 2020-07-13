package com.yimao.cloud.engineer.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.minlog.Log;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.DistributorRoleLevel;
import com.yimao.cloud.base.enums.PayStatus;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.enums.SignTypeEnum;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WaterDeviceRenewStatus;
import com.yimao.cloud.base.enums.WorkOrderInstallNewStep;
import com.yimao.cloud.base.enums.WorkOrderNewStatusEnum;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.BaiduMapUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.yun.YunOldIdUtil;
import com.yimao.cloud.engineer.enums.ApiStatusCode;
import com.yimao.cloud.engineer.feign.OrderFeign;
import com.yimao.cloud.engineer.feign.ProductFeign;
import com.yimao.cloud.engineer.feign.WaterFeign;
import com.yimao.cloud.engineer.service.CommonService;
import com.yimao.cloud.engineer.service.WorkOrderBusinessService;
import com.yimao.cloud.engineer.utils.ContractUtil;
import com.yimao.cloud.engineer.utils.ResultBean;
import com.yimao.cloud.engineer.utils.ValidateUitls;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderOperationDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderReqDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/***
 * 安装工单业务处理
 * @author zhangbaobao
 *
 */
@Slf4j
@Service
public class WorkOrderBusinessServiceImpl implements WorkOrderBusinessService {

	@Resource
	private UserCache userCache;

	@Resource
	WaterFeign waterFeign;

	@Resource
	private OrderFeign orderFeign;

	@Resource
	private RabbitTemplate rabbitTemplate;

	@Resource
	private ProductFeign productFeign;

	@Resource
	private CommonService commonService;

	@Resource
	private ContractUtil contractUtil;

	/***
	 * 扫sn码、批次码、sim卡
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	@Override
	public void scanCode(WorkOrderReqDTO req) {
		log.info("============安装工扫码开始=========" + JSONObject.toJSONString(req));
		Integer engineerId = userCache.getCurrentEngineerId();
		WorkOrderDTO workOrder = orderFeign.getWorkOrderById(req.getWorkOrderId());

		if (workOrder == null || !Objects.equals(workOrder.getEngineerId(), engineerId)) {
			throw new YimaoException("请求参数错误");
		}

		// 校验
		ValidateUitls.validateSnAndBatchCodeAndSimCard(req.getSn(), req.getLogisticsCode(), req.getSimCard());

		workOrder.setNextStep(WorkOrderInstallNewStep.PAY.value);
		workOrder.setSn(req.getSn());
		workOrder.setOperationTime(new Date());
		workOrder.setStatus(WorkOrderNewStatusEnum.PROCESSING.value);
		workOrder.setStep(WorkOrderInstallNewStep.ACTIVATING.value);
		workOrder.setLogisticsCode(req.getLogisticsCode());
		workOrder.setSimCard(req.getSimCard());
		workOrder.setSimCardTime(new Date());

		// 先创建水机设备
		WaterDeviceDTO device = waterFeign.getWaterDeviceBySnCode(req.getSn());
		if (device != null) {
			Log.error("");
			throw new YimaoException("该sn码已被使用");
		}
		device = this.createWaterDevice(workOrder);

		// 激活sim卡
		waterFeign.activatingSimCard(device.getId(), req.getSimCard());

		// 更新工单信息
		workOrder.setDeviceId(device.getId());
		workOrder.setSnCodeTime(device.getSnEntryTime());
		orderFeign.updateWorkOrder(workOrder);

		//使用异步队列保存工单操作记录
		WorkOrderOperationDTO operation = new WorkOrderOperationDTO();
		operation.setAdmin(workOrder.getEngineerName());
		operation.setWorkOrderId(workOrder.getId());
		operation.setOperation("扫描sn码、批次码、sim卡");
		operation.setSnCode(device.getSn());
		operation.setSimCard(device.getIccid());
		operation.setBatchCode(device.getLogisticsCode());
		operation.setCreateTime(new Date());

		rabbitTemplate.convertAndSend(RabbitConstant.WORK_ORDER_OPERATION, operation);
		log.info("============安装工扫码结束=========");
	}

	/**
	 * 创建水机
	 *
	 * @param workOrder
	 * @return
	 */
	private WaterDeviceDTO createWaterDevice(WorkOrderDTO workOrder) {
		Date now = new Date();
		WaterDeviceDTO device = new WaterDeviceDTO();
		device.setSn(workOrder.getSn());
		device.setSnEntryTime(now);
		device.setLogisticsCode(workOrder.getLogisticsCode());
		device.setCostId(workOrder.getCostId());
		device.setCostType(workOrder.getCostType());
		device.setCostName(workOrder.getCostName());
		device.setCostChanged(false);
		device.setDistributorId(workOrder.getDistributorId());
		device.setDistributorName(workOrder.getDistributorName());
		device.setDistributorPhone(workOrder.getDistributorPhone());
		device.setDistributorAccount(workOrder.getDistributorAccount());
		device.setEngineerId(workOrder.getEngineerId());
		device.setEngineerName(workOrder.getEngineerName());
		device.setEngineerPhone(workOrder.getEngineerPhone());
		device.setDeviceUserId(workOrder.getUserId());
		device.setDeviceUserName(workOrder.getUserName());
		device.setDeviceUserPhone(workOrder.getUserPhone());
		device.setProvince(workOrder.getProvince());
		device.setCity(workOrder.getCity());
		device.setRegion(workOrder.getRegion());
		device.setAddress(workOrder.getAddress());
		device.setDeviceModel(workOrder.getDeviceModel());
		device.setDeviceScope(YunOldIdUtil.getProductScope(workOrder.getDeviceModel()));
		device.setDeviceType(YunOldIdUtil.getProductTypeName());
		device.setInitMoney(workOrder.getFee());
		device.setMoney(workOrder.getFee());
		device.setCreateTime(now);
		device.setWorkOrderId(workOrder.getId());
		device.setCurrentTotalTime(0);
		device.setCurrentTotalFlow(0);
		device.setLastTotalTime(0);
		device.setLastTotalFlow(0);
		device.setUseTime(0);
		device.setUseFlow(0);
		device.setRenewStatus(WaterDeviceRenewStatus.NONEED.value);
		device.setRenewStatusText(WaterDeviceRenewStatus.NONEED.name);
		return waterFeign.createWaterDevice(device);
	}

	/***
	 * 收集水源
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	@Override
	public void collectWater(WorkOrderReqDTO req) {
		//校验
		ValidateUitls.validateTdsAndHydraulic(req.getTds(), req.getHydraulic());

		WorkOrderDTO update = new WorkOrderDTO();
		update.setId(req.getWorkOrderId());
		update.setNextStep(WorkOrderInstallNewStep.ACTIVATING.value);
		update.setMunicipal(req.getIsmunicipal());
		update.setTds(req.getTds());
		update.setHydraulic(req.getHydraulic());
		update.setOtherSource(req.getOtherSource());
		update.setWaterStatus(StatusEnum.YES.value());
		update.setWaterStatusText("水源信息输入成功");
		update.setOperationTime(new Date());
		update.setStatus(WorkOrderNewStatusEnum.PROCESSING.value);
		update.setStep(WorkOrderInstallNewStep.COLLECT_WATER.value);
		orderFeign.updateWorkOrder(update);

		//使用异步队列保存工单操作记录
		WorkOrderDTO workOrder = orderFeign.getWorkOrderById(req.getWorkOrderId());
		WorkOrderOperationDTO operation = new WorkOrderOperationDTO();
		operation.setAdmin(workOrder.getEngineerName());
		operation.setWorkOrderId(workOrder.getId());
		operation.setOperation("收集水源信息");
		operation.setCreateTime(new Date());
		rabbitTemplate.convertAndSend(RabbitConstant.WORK_ORDER_OPERATION, operation);
	}

	/***
	 * 改约挂单
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	@Override
	public void changeAppoint(WorkOrderReqDTO req) {
		//校验
		WorkOrderDTO wo = orderFeign.getWorkOrderById(req.getWorkOrderId());
		if (wo == null) {
			throw new YimaoException("工单信息不存在!");
		}
		if (wo.getStatus() == null || wo.getStatus() != WorkOrderNewStatusEnum.WAIT_INSTALL.value) {
			throw new YimaoException("工单状态不是待安装状态！");
		}

		WorkOrderDTO update = new WorkOrderDTO();
		update.setId(req.getWorkOrderId());
		if (req.getAppointType() == 1) {
			if (StringUtil.isBlank(req.getAppointDate()) || StringUtil.isBlank(req.getAppointTimeLimit())) {
				throw new YimaoException("改约时间必须传值！");
			}

			update.setAppointTime(DateUtil.stringToDate(req.getAppointDate(), "yyyy-MM-dd"));
			update.setAppointRemark(req.getAppointRemark());
			update.setAppointTimeLimit(req.getAppointTimeLimit());
		}
		if (req.getAppointType() == 2) {
			if (StringUtil.isBlank(req.getAddress())) {
				throw new YimaoException("改约服务地址必须传值！");
			}
			update.setAddress(req.getAddress());
			update.setAppointAddress(wo.getProvince() + wo.getCity() + wo.getRegion() + req.getAddress());
			try {
				Map<String, Double> map = BaiduMapUtil.getLngAndLatByAddress(wo.getProvince() + wo.getCity() + wo.getRegion() + req.getAddress());
				update.setAddrLatitude(map.get("lat").toString());
				update.setAddrLongitude(map.get("lng").toString());
			} catch (IOException e) {
				throw new YimaoException("获取地址经纬度失败");
			}
		} else {
			update.setAppointRemark(req.getAppointRemark());
		}
		update.setStatus(WorkOrderNewStatusEnum.PENDING.value);
		update.setOperationTime(new Date());
		orderFeign.updateWorkOrder(update);

		//保存工单操作记录
		WorkOrderOperationDTO operation = new WorkOrderOperationDTO();
		operation.setAdmin(wo.getEngineerName());
		operation.setWorkOrderId(wo.getId());
		operation.setOperation("改约挂单");
		operation.setRemark("");
		operation.setCreateTime(new Date());
		rabbitTemplate.convertAndSend(RabbitConstant.WORK_ORDER_OPERATION, operation);
	}

	/***
	 * 更换机型和计费方式
	 */
	@Override
	public void changeTypeAndModel(WorkOrderReqDTO req) {
		log.info("======更改设备型号计费方式参数=============" + JSONObject.toJSONString(req));
		Integer engineerId = userCache.getCurrentEngineerId();
		//查询选择产品
		ProductDTO product = productFeign.getById(req.getProductId());
		if (Objects.isNull(product)) {
			throw new YimaoException("产品数据错误！");
		}
		//查询当前工单
		WorkOrderDTO workOrder = orderFeign.getWorkOrderById(req.getWorkOrderId());

		if (Objects.isNull(workOrder)) {
			throw new YimaoException("工单不存在！");
		}
		if (!engineerId.equals(workOrder.getEngineerId())) {
			throw new YimaoException("该安装工没有权限操作此工单！");
		}
		//获取变更改的产品型号计费方式
		ProductCostDTO newCost = productFeign.productCostGetById(Integer.parseInt(req.getCostId()));
		if (Objects.isNull(newCost)) {
			throw new YimaoException("计费方式数据错误！");
		}

		List<ProductCostDTO> costList = productFeign.productCostList(product.getId());

		if (costList == null || costList.size() == 0) {
			throw new YimaoException("无法获取产品计费方式！");
		}

		boolean check = costList.stream().anyMatch(costDTO -> Objects.equals(costDTO.getId(), newCost.getId()));
		if (!check) {
			throw new YimaoException("不能更换为该计费方式，如有疑问可咨询客服！");
		}
		try {
			orderFeign.changeWorkOrderProductAndCostByEngineer(req.getWorkOrderId(), product.getId(), newCost.getId(), null, null, null, 1);
		} catch (Exception e) {
			throw new YimaoException("更换机型遇到错误!");
		}
	}

	/***
	 * 退单-接单之后，签约完成之前才可以退单
	 */
	@Override
	public void chargebackWorkOrder(WorkOrderReqDTO req) {
		orderFeign.chargeback(req);
	}

	/***
	 * 其他支付
	 */
	@Override
	public void otherPay(String workOrderId, Integer otherPayType, MultipartFile file1, MultipartFile file2,
	                     MultipartFile file3) {
		log.info("=======其他支付=========workOrderId:" + workOrderId + ",otherPayType=" + otherPayType);
		Integer engineerId = userCache.getCurrentEngineerId();
		WorkOrderDTO workorder = orderFeign.getWorkOrderById(workOrderId);

		//工单数据校验
		if (Objects.isNull(workorder)) {
			throw new YimaoException("工单信息不存在");
		}
		//安装工校验
		if (!Objects.equals(workorder.getEngineerId(), engineerId)) {
			throw new YimaoException("当前安装工无权操作此工单");
		}
		if (PayType.find(otherPayType) == null) {
			throw new YimaoException("未知的其他支付类型");
		}

		try {
			String payCredential = commonService.upload(file1, file2, file3, null, null);
			if (StringUtil.isEmpty(payCredential)) {
				throw new YimaoException("安装工程师上传其他支付图片失败");
			}
			WorkOrderDTO wo = new WorkOrderDTO();
			wo.setMainOrderId(workorder.getMainOrderId());
			wo.setSubOrderId(workorder.getSubOrderId());
			wo.setId(workorder.getId());
			wo.setPayCredential(payCredential);
			wo.setPayCredentialSubmitTime(new Date());
			log.info("===========workOrderOtherPayImages=" + wo.getPayCredential());
			wo.setPayStatus(PayStatus.WAITING_AUDIT.value);
			wo.setPayType(otherPayType);
			wo.setStep(WorkOrderInstallNewStep.PAY.value);
			wo.setNextStep(WorkOrderInstallNewStep.SIGN_CONTRACT.value);
			wo.setEngineerId(engineerId);//赋值安装工id
			//更新工单和订单的支付信息
			orderFeign.otherPay(wo);
		} catch (Exception e) {
			log.error("=============工单[" + workOrderId + "],上传其他支付图片失败,异常信息" + e.getMessage());
			throw new YimaoException("安装工程师上传其他支付图片失败");
		}
	}

	/***
	 * 签约
	 */
	@Override
	public void signContract(WorkOrderReqDTO req) {
		log.info("======签约开始=============" + JSONObject.toJSONString(req));
		Integer engineerId = userCache.getCurrentEngineerId();
		WorkOrderDTO wo = orderFeign.getWorkOrderById(req.getWorkOrderId());
		if (wo == null || !Objects.equals(wo.getEngineerId(), engineerId)) {
			throw new YimaoException("数据错误");
		}

		//支付状态为未支付,审核状态也不是审核中,则不可签约
		if ((wo.getPay() == null || !wo.getPay()) && PayStatus.WAITING_AUDIT.value != wo.getPayStatus()) {
			log.error("=========【" + req.getWorkOrderId() + "】该工单不符合签约条件,PayStatus=" + wo.getPayStatus() + ",pay=" + wo.getPay());
			throw new YimaoException("该工单不符合签约条件");
		}

		//判断是否已签约
		if (StatusEnum.isYes(wo.getSignStatus())) {
			throw new YimaoException("该工单已签约");
		}

		//判断签约状态是否是进行中,如果是进行中,则只能签约纸质合同
		if (StatusEnum.isPROCESSING(wo.getSignStatus()) && req.getSignType().intValue() == SignTypeEnum.ELECTRONIC_SIGN.value) {
			throw new YimaoException("工单签约状态【进行中】,只能签署纸质合同");
		}

		//基本参数校验
		checkParams(req);

		//组装数据
		WorkOrderDTO workOrder = new WorkOrderDTO();
		if (req.getSignType().intValue() == SignTypeEnum.PAPER_CONTRACT.value) {
			//纸质合同
			workOrder.setConfirmation(req.getConfirmation());
			workOrder.setProtocol(req.getProtocol());
			workOrder.setSignContractCredential(req.getSignContractCredential());
			workOrder.setStep(WorkOrderInstallNewStep.SIGN_CONTRACT.value);
			workOrder.setNextStep(WorkOrderInstallNewStep.COMPLETE_WORK_ORDER.value);

		} else if (req.getSignType().intValue() == SignTypeEnum.ELECTRONIC_SIGN.value) {
			//电子合同
			workOrder = initWorkOrderData(wo, req);
		}
		Date now = new Date();
		workOrder.setId(req.getWorkOrderId());
		workOrder.setSignTime(now);
		workOrder.setSignClient(req.getSignClient());
		workOrder.setOperationTime(now);

		//更新工单信息
		WorkOrderDTO result = orderFeign.updateWorkOrder(wo);
		if (result == null) {
			throw new YimaoException("用户签署合同失败");
		}
	}

	/***
	 * 组装数据
	 * @param wo
	 * @param req
	 * @return
	 */
	private WorkOrderDTO initWorkOrderData(WorkOrderDTO wo, WorkOrderReqDTO req) {
		WorkOrderDTO workOrder = new WorkOrderDTO();
		//电子合同
		String orderId = "YMAPP" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + (new Random().nextInt(9999) + 10000) + req.getWorkOrderId();
		ProductCostDTO cost = productFeign.productCostGetById(wo.getCostId());
		int homeCostPrice = 0;
		int homeOpenAccount = 0;
		int busineseCostPrice = 0;
		int busineseOpenAccount = 0;
		if ("家用".equals(YunOldIdUtil.getProductScope(wo.getDeviceModel()))) {
			homeCostPrice = cost.getRentalFee().intValue();
			homeOpenAccount = cost.getInstallationFee().intValue();
		}

		if ("商用".equals(YunOldIdUtil.getProductScope(wo.getDeviceModel()))) {
			busineseCostPrice = cost.getRentalFee().intValue();
			busineseOpenAccount = cost.getInstallationFee().intValue();
		}

		boolean isExperience = wo.getDistributorType() != null && DistributorRoleLevel.D_50.value == wo.getDistributorType();

		//签署合同
		Map<String, Object> contractMap = contractUtil.signContract(wo, req, isExperience, homeCostPrice, homeOpenAccount, busineseCostPrice, busineseOpenAccount, orderId);
		if (contractMap == null || !contractMap.containsKey("success") || !(Boolean) contractMap.get("success")) {
			throw new YimaoException(contractMap.get("msg").toString());
		}
		workOrder.setContractValidity(req.getYear());
		workOrder.setSignOrderId(orderId);
		workOrder.setSignUserName(req.getName());
		workOrder.setSignUserPhone(req.getPhone());
		workOrder.setSignUserEmail(req.getMail());
		workOrder.setSignUserAddress(req.getAddress());
		if (StringUtil.isNotEmpty(req.getIdCard())) {
			workOrder.setSignUserIdCard(req.getIdCard());
			workOrder.setUserIdCard(req.getIdCard());
		}
		return workOrder;
	}

	/***
	 * 参数校验
	 * @param req
	 */
	private void checkParams(WorkOrderReqDTO req) {
		if (null == req) {
			throw new YimaoException("参数错误");
		}
		if (StringUtil.isEmpty(req.getWorkOrderId())) {
			throw new YimaoException("工单号不能为空");
		}
		if (!SignTypeEnum.exsistsSignType(req.getSignType())) {
			throw new YimaoException("签约类型不正确");
		}

		if (SignTypeEnum.PAPER_CONTRACT.value == req.getSignType().intValue()) {
			//纸质签约
			if (StringUtil.isEmpty(req.getProtocol())) {
				throw new YimaoException("协议编号不能为空");
			}

			if (StringUtil.isEmpty(req.getConfirmation())) {
				throw new YimaoException("确认单编号不能为空");
			}

			if (StringUtil.isEmpty(req.getSignContractCredential())) {
				throw new YimaoException("合同凭证不能为空");
			}
		} else if (SignTypeEnum.ELECTRONIC_SIGN.value == req.getSignType().intValue()) {
			//TODO 电子签约
		}
	}

	/***
	 * 电子合同签署成功需要设置工单step 和next
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultBean<WorkOrderDTO> signSuccess(String code, String orderId, String signClient) {
		WorkOrderDTO workOrder = orderFeign.getWorkOrderById(code);
		if (workOrder != null && !StatusEnum.isYes(workOrder.getSignStatus())) {
			Date now = new Date();
			workOrder.setSignTime(now);
			workOrder.setSignOrderId(orderId);
			workOrder.setSignClient(signClient);
			workOrder.setSignStatus(StatusEnum.YES.value());
			workOrder.setStep(WorkOrderInstallNewStep.SIGN_CONTRACT.value);
			workOrder.setNextStep(WorkOrderInstallNewStep.COMPLETE_WORK_ORDER.value);
			workOrder.setOperationTime(now);
			try {
				workOrder = orderFeign.updateWorkOrder(workOrder);
				if (workOrder != null) {
					return ResultBean.success(workOrder);
				}
			} catch (Exception var6) {
				return ResultBean.fastFailServerException();
			}
		}

		return ResultBean.fastFail(ApiStatusCode.SER_EXCEPTION.getTextZh());
	}

	/**
	 * 更换设备
	 */
	@Override
	public void changeWaterDevice(String workOrderId, String logisticsCode, Integer productId, String snCode, String simCard, Integer costId) {
		log.info("=======调用更换设备=========workOrderId:" + workOrderId + ",logisticsCode=" + logisticsCode + ",productId=" + productId + ",snCode=" + snCode + ",simCard=" + simCard + ",costId=" + costId);
		// 校验
		ValidateUitls.validateSnAndBatchCodeAndSimCard(snCode, logisticsCode, simCard);
		Integer engineerId = userCache.getCurrentEngineerId();
		WorkOrderDTO workOrder = orderFeign.getWorkOrderById(workOrderId);
		if (workOrder == null || !Objects.equals(workOrder.getEngineerId(), engineerId)) {
			throw new YimaoException("工单不存在或无权限操作此工单！");
		}

		if (WorkOrderNewStatusEnum.COMPLETED.value == workOrder.getStatus()) {
			throw new YimaoException("工单已完成不可更换新设备！");
		}
		//只能是采集水源之后并且是签署合同之前才能更换设备
		if (WorkOrderNewStatusEnum.PROCESSING.value != workOrder.getStatus()
				|| WorkOrderInstallNewStep.COLLECT_WATER.value >= workOrder.getStep()
				|| WorkOrderInstallNewStep.SIGN_CONTRACT.value <= workOrder.getStep()) {
			throw new YimaoException("该工单当前步骤不可更换新设备！");
		}

		//更换产品
		ProductDTO product = productFeign.getById(productId);
		if (product == null) {
			throw new YimaoException("产品数据错误!");
		}
		//获取变更改的产品型号计费方式
		ProductCostDTO newCost = productFeign.productCostGetById(costId);
		if (newCost == null) {
			throw new YimaoException("计费方式数据错误!");
		}

		List<ProductCostDTO> costList = productFeign.productCostList(product.getId());
		boolean check = costList.stream().anyMatch(costDTO -> Objects.equals(costDTO.getId(), newCost.getId()));
		if (!check) {
			throw new YimaoException("不能更换为该计费方式，如有疑问可咨询客服！");
		}
		try {
			//变更计费方式和产品
			orderFeign.changeWorkOrderProductAndCostByEngineer(workOrderId, product.getId(), newCost.getId(), logisticsCode, snCode, simCard, 2);
		} catch (Exception e) {
			log.error("更换设备异常：" + e.getMessage());
			throw new YimaoException("更换设备异常!");
		}
	}
}
