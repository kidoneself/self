package com.yimao.cloud.order.service.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.HraType;
import com.yimao.cloud.base.enums.IncomeSubjectEnum;
import com.yimao.cloud.base.enums.IncomeType;
import com.yimao.cloud.base.enums.MyOrderOperationType;
import com.yimao.cloud.base.enums.OrderDetailStatus;
import com.yimao.cloud.base.enums.OrderStatusEnum;
import com.yimao.cloud.base.enums.OrderSubStatusEnum;
import com.yimao.cloud.base.enums.PayStatus;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.ProductActivityType;
import com.yimao.cloud.base.enums.ProductModeEnum;
import com.yimao.cloud.base.enums.ProductSupplyCode;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.Terminal;
import com.yimao.cloud.base.enums.UserType;
import com.yimao.cloud.base.enums.WorkOrderCompleteEnum;
import com.yimao.cloud.base.enums.WorkOrderStatusEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.CopyUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.order.feign.HraFeign;
import com.yimao.cloud.order.feign.ProductFeign;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.feign.WechatFeign;
import com.yimao.cloud.order.mapper.AfterSalesOrderMapper;
import com.yimao.cloud.order.mapper.OrderAddressMapper;
import com.yimao.cloud.order.mapper.OrderDeliveryMapper;
import com.yimao.cloud.order.mapper.OrderMainMapper;
import com.yimao.cloud.order.mapper.OrderPayCheckMapper;
import com.yimao.cloud.order.mapper.OrderRenewMapper;
import com.yimao.cloud.order.mapper.OrderStatusRecordMapper;
import com.yimao.cloud.order.mapper.OrderSubMapper;
import com.yimao.cloud.order.mapper.ProductIncomeRecordMapper;
import com.yimao.cloud.order.mapper.ProductIncomeRecordPartMapper;
import com.yimao.cloud.order.mapper.QuotaChangeRecordMapper;
import com.yimao.cloud.order.mapper.SubOrderDetailMapper;
import com.yimao.cloud.order.mapper.WorkOrderMapper;
import com.yimao.cloud.order.mapper.WorkOrderOperationMapper;
import com.yimao.cloud.order.po.AfterSalesOrder;
import com.yimao.cloud.order.po.OrderAddress;
import com.yimao.cloud.order.po.OrderDelivery;
import com.yimao.cloud.order.po.OrderMain;
import com.yimao.cloud.order.po.OrderPayCheck;
import com.yimao.cloud.order.po.OrderStatusRecord;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.order.po.PayRecord;
import com.yimao.cloud.order.po.ProductIncomeRecord;
import com.yimao.cloud.order.po.ProductIncomeRecordPart;
import com.yimao.cloud.order.po.QuotaChangeRecord;
import com.yimao.cloud.order.po.SubOrderDetail;
import com.yimao.cloud.order.po.WorkOrder;
import com.yimao.cloud.order.po.WorkOrderOperation;
import com.yimao.cloud.order.service.OrderMainService;
import com.yimao.cloud.order.service.OrderSubService;
import com.yimao.cloud.order.service.ProductIncomeRecordService;
import com.yimao.cloud.order.service.QuotaChangeRecordService;
import com.yimao.cloud.order.service.SyncWorkOrderService;
import com.yimao.cloud.order.service.WorkOrderService;
import com.yimao.cloud.pojo.dto.order.AgentSalesOverviewDTO;
import com.yimao.cloud.pojo.dto.order.OrderConditionDTO;
import com.yimao.cloud.pojo.dto.order.OrderCountDTO;
import com.yimao.cloud.pojo.dto.order.OrderMainDTO;
import com.yimao.cloud.pojo.dto.order.OrderProductCountDTO;
import com.yimao.cloud.pojo.dto.order.OrderStatusCountDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubListDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsQueryDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsResultDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderReqDTO;
import com.yimao.cloud.pojo.dto.order.WxOrderDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryCascadeDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductCompanyDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.station.FlowStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.ProductSalesStatusDTO;
import com.yimao.cloud.pojo.dto.station.ProductTabulateDataDTO;
import com.yimao.cloud.pojo.dto.station.ProductTwoCategoryPicResDTO;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.export.order.OrderBillExport;
import com.yimao.cloud.pojo.query.order.OrderBillQuery;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.OrderBillVO;
import com.yimao.cloud.pojo.vo.station.OrderGeneralSituationVO;
import com.yimao.cloud.pojo.vo.station.ProductSalesStatusAndTwoCategoryPicResVO;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@Slf4j
@Service
public class OrderSubServiceImpl implements OrderSubService {

	private static final Logger logger = LoggerFactory.getLogger(OrderSubServiceImpl.class);

	@Resource
	private UserCache userCache;
	@Resource
	private RabbitTemplate rabbitTemplate;
	@Resource
	private OrderSubMapper orderSubMapper;
	@Resource
	private ProductFeign productFeign;
	@Resource
	private UserFeign userFeign;
	@Resource
	private HraFeign hraFeign;
	@Resource
	private WechatFeign wechatFeign;
	@Resource
	private SystemFeign systemFeign;
	@Resource
	private OrderDeliveryMapper orderDeliveryMapper;
	@Resource
	private OrderMainMapper orderMainMapper;
	@Resource
	private OrderStatusRecordMapper orderStatusRecordMapper;
	@Resource
	private SubOrderDetailMapper subOrderDetailMapper;
	@Resource
	private OrderAddressMapper orderAddressMapper;
	@Resource
	private WorkOrderMapper workOrderMapper;
	@Resource
	private AfterSalesOrderMapper afterSalesOrderMapper;
	@Resource
	private OrderPayCheckMapper orderPayCheckMapper;
	@Resource
	private ProductIncomeRecordService productIncomeRecordService;
	@Resource
	private ProductIncomeRecordMapper productIncomeRecordMapper;
	@Resource
	private ProductIncomeRecordPartMapper productIncomeRecordPartMapper;
	@Resource
	private OrderSubService orderSubService;
	@Resource
	private QuotaChangeRecordService quotaChangeRecordService;
	@Resource
	private SyncWorkOrderService syncWorkOrderService;
	@Resource
	private QuotaChangeRecordMapper quotaChangeRecordMapper;
	@Resource
	private WorkOrderOperationMapper workOrderOperationMapper;
	@Resource
	private OrderMainService mainOrderService;
	@Resource
	private WorkOrderService workOrderService;
	@Resource
	private OrderRenewMapper orderRenewMapper;

	/**
	 * 获取子订单信息（带订单详情）
	 *
	 * @param id 子订单号
	 */
	@Override
	public OrderSubDTO findFullOrderById(Long id) {
		OrderSub order = orderSubMapper.selectByPrimaryKey(id);
		if (order == null) {
			return null;
		}
		OrderSubDTO dto = new OrderSubDTO();
		order.convert(dto);
		SubOrderDetail subOrderDetail = subOrderDetailMapper.selectByPrimaryKey(id);
		if (subOrderDetail != null) {
			subOrderDetail.convert(dto);
		}

		Integer productType = dto.getProductType();//商品类型
		if (productType == ProductModeEnum.LEASE.value) {
			//租赁商品
			if (dto.getStationId() != null) {
				StationDTO station = systemFeign.getStationById(dto.getStationId());
				if (Objects.nonNull(station)) {
					dto.setStationName(station.getName());
					dto.setStationProvince(station.getProvince());
					dto.setStationCity(station.getCity());
					dto.setStationRegion(station.getRegion());
				}
			}
		}
		//实物商品 显示配送信息
		dto = this.getOrderDelivery(dto);

		ProductDTO product = productFeign.getProductById(dto.getProductId());
		List<ProductCategoryDTO> categoryDTOS;
		if (Objects.nonNull(product)) {
			dto.setProductName(product.getName());
			dto.setTransportType(product.getTransportType());
			//一级类目是产品类型
			ProductCategoryDTO category = productFeign.getOneProductCategory(product.getCategoryId());
			if (Objects.nonNull(category)) {
				dto.setProductOneCategoryId(category.getId());
				dto.setProductOneCategoryName(category.getName());

				categoryDTOS = productFeign.getBottomCatgory(category.getId());
				dto.setProductCategorys(categoryDTOS);
			}
		}
		return dto;
	}

	/**
	 * 后台管理系统查询订单详情
	 *
	 * @param id
	 */
	@Override
	public OrderSubDTO getOrderDetailById(Long id) {
		OrderSub order = orderSubMapper.selectByPrimaryKey(id);
		if (order == null) {
			return null;
		}
		OrderSubDTO dto = new OrderSubDTO();
		order.convert(dto);
		SubOrderDetail subOrderDetail = subOrderDetailMapper.selectByPrimaryKey(id);
		if (subOrderDetail != null) {
			subOrderDetail.convert(dto);
		}

		//设置售后订单
		if (Objects.equals(dto.getStatus(), OrderStatusEnum.AFTER_SALE.value)) {
			AfterSalesOrder salesOrder = new AfterSalesOrder();
			salesOrder.setOrderId(id);
			AfterSalesOrder salesOrders = afterSalesOrderMapper.selectOne(salesOrder);
			if (Objects.nonNull(salesOrders)) {
				dto.setAfterSalesOrderId(salesOrders.getId());
			}
		}
		//设置会员用户是否享受收益
		ProductIncomeRecord query = new ProductIncomeRecord();
		query.setOrderId(String.valueOf(dto.getId()));
		query.setMainOrderId(dto.getMainOrderId());
		query.setIncomeType(IncomeType.PRODUCT_INCOME.value);
		ProductIncomeRecord incomeRecord = productIncomeRecordMapper.selectOne(query);
		if (incomeRecord != null) {
			ProductIncomeRecordPart queryPart = new ProductIncomeRecordPart();
			queryPart.setRecordId(incomeRecord.getId());
			queryPart.setSubjectId(dto.getVipUserId());
			queryPart.setSubjectCode(IncomeSubjectEnum.DISTRIBUTOR_USER.value);
			int count = productIncomeRecordPartMapper.selectCount(queryPart);
			if (count > 0) {
				dto.setVipUserHasIncome(true);
			} else {
				dto.setVipUserHasIncome(false);
			}
		}
		return dto;
	}

	/**
	 * @param id
	 * @param operationType
	 * @return com.yimao.cloud.pojo.dto.order.OrderSubListDTO
	 * @description 我的订单--查询订单详情
	 * @author zhilin.he
	 * @date 2019/8/5 15:58
	 */
	@Override
	public OrderSubListDTO getMyOrderDetailById(Long id, Integer operationType, List<Long> ids, Integer subType) {
		OrderMain orderMain = orderMainMapper.selectByPrimaryKey(id);
		if (orderMain == null) {
			return null;
		}

		Example example = new Example(OrderSub.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("mainOrderId", id);
		criteria.andIn("id", ids);
		List<OrderSubDTO> orderSubDTOList = CopyUtil.copyList(orderSubMapper.selectByExample(example), OrderSub.class, OrderSubDTO.class);
		OrderSubListDTO dto = new OrderSubListDTO();
		OrderConditionDTO orderConditionDTO = new OrderConditionDTO();
		if (CollectionUtil.isEmpty(orderSubDTOList)) {
			return null;
		}
		orderConditionDTO.setOperationType(operationType);
		//设入主订单数据
		dto = this.setOrderMain(orderMain, dto);

		Integer flag = 1;
		OrderSubDTO subDTO;
		Integer engineerId; //安装工
		Date firstsTime;     //服务时间

		for (OrderSubDTO orderSubDTO : orderSubDTOList) {
			//遍历子订单
			SubOrderDetail subOrderDetail = subOrderDetailMapper.selectByPrimaryKey(orderSubDTO.getId());
			if (subOrderDetail != null) {
				subOrderDetail.convert(orderSubDTO);
			}

			if (orderSubDTO.getProductId() != null) {
				ProductDTO productDTO = productFeign.getProductById(orderSubDTO.getProductId());
				if (productDTO != null) {
					orderSubDTO.setSupplyCode(productDTO.getSupplyCode());
				}
			}

			//服务人员和安装时间是否一致：0-否，1-是
			if (orderSubDTO.getProductType() == ProductModeEnum.LEASE.value) {
				subDTO = orderSubDTOList.get(0);
				engineerId = subDTO.getEngineerId();
				firstsTime = subDTO.getServiceTime();
				if (flag == 1) {
					if (orderSubDTO.getCostId() == null || !Objects.equals(orderSubDTO.getEngineerId(), engineerId) || !Objects.equals(orderSubDTO.getServiceTime(), firstsTime)) {
						flag = 0;
					}
				}

				//获取产品价格：根据不同的计费方式
				if (orderSubDTO.getCostId() != null) {
					ProductCostDTO productCost = productFeign.productCostGetById(orderSubDTO.getCostId());
					if (Objects.nonNull(productCost)) {
						orderSubDTO.setProductPrice(productCost.getTotalFee());
					}
				}
			}

			//实物商品 显示配送信息
			if (Objects.equals(orderSubDTO.getProductType(), ProductModeEnum.REALTHING.value)) {
				orderSubDTO = this.getOrderDelivery(orderSubDTO);
				if (orderSubDTO.getProductId() != null) {
					//订单详情设置 货运方式
					ProductDTO product = productFeign.getProductById(orderSubDTO.getProductId());
					if (product != null) {
						orderSubDTO.setTransportType(product.getTransportType());
					}
				}
			}
			//设置订单状态
			this.commonDetailStatusVerify(orderSubDTO);
		}

		dto.setOrderSubList(orderSubDTOList);
		//鉴别服务人员和预约时间是否一致
		dto.setIsDifference(flag);
		return dto;
	}

	private OrderSubListDTO setOrderMain(OrderMain orderMain, OrderSubListDTO dto) {
		dto.setMainOrderId(orderMain.getId());
		dto.setType(orderMain.getType());
		dto.setSubType(orderMain.getSubType());
		dto.setUserId(orderMain.getUserId());
		dto.setProductAmountFee(orderMain.getProductAmountFee());
		dto.setOrderAmountFee(orderMain.getOrderAmountFee());
		dto.setCount(orderMain.getCount());
		dto.setLogisticsFee(orderMain.getLogisticsFee());
		dto.setPayType(orderMain.getPayType());
		dto.setPay(orderMain.getPay());
		dto.setPayTime(orderMain.getPayTime());
		dto.setTradeNo(orderMain.getTradeNo());
		dto.setPayCredential(orderMain.getPayCredential());
		dto.setCreateTime(orderMain.getCreateTime());
		return dto;
	}

	/**
	 * @param orderConditionDTO 订单查询条件
	 * @param pageNum           当前页
	 * @param pageSize          每页显示条数
	 * @description 查询订单列表, 根据订单查询条件
	 * @author zhilin.he
	 * @date 2019/1/11 13:56
	 */
	@Override
	public PageVO<OrderSubDTO> orderList(OrderConditionDTO orderConditionDTO, Integer pageNum, Integer pageSize) {
		if (orderConditionDTO.getDistributorId() != null) {
			List<Integer> ids = userFeign.getDistributorByUserId(orderConditionDTO.getDistributorId());
			if (CollectionUtil.isNotEmpty(ids)) {
				orderConditionDTO.setDistributorId(ids.get(0));
			}
		}

		PageHelper.startPage(pageNum, pageSize);
		Page<OrderSubDTO> ptPage = orderSubMapper.orderList(orderConditionDTO);
		if (CollectionUtil.isNotEmpty(ptPage.getResult())) {
			for (OrderSubDTO subDTO : ptPage.getResult()) {
				if (Objects.nonNull(subDTO.getDistributorId())) {
					log.info("**********subDTO.getDistributorId()**********" + subDTO.getDistributorId());
					DistributorDTO distributor = userFeign.getDistributorById(subDTO.getDistributorId());
					if (distributor != null) {
						subDTO.setDistributorNumber(distributor.getUserId());
					}
				}
				if (Objects.equals(subDTO.getProductType(), ProductModeEnum.REALTHING.value)) {
					this.getOrderDelivery(subDTO);
				}
			}
		}

		return new PageVO<>(pageNum, ptPage);
	}

	private OrderSubDTO getOrderDelivery(OrderSubDTO orderSubDTO) {
		if (orderSubDTO.getProductType() == ProductModeEnum.REALTHING.value) {
			//实物商品 显示配送信息
			Example example = new Example(OrderDelivery.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andEqualTo("orderId", orderSubDTO.getId());
			List<OrderDelivery> deliveries = orderDeliveryMapper.selectByExample(example);
			if (CollectionUtil.isNotEmpty(deliveries)) {
				orderSubDTO.setLogisticsNo(deliveries.get(0).getLogisticsNo());//配送单号
				orderSubDTO.setLogisticsCompany(deliveries.get(0).getLogisticsCompany());//物流公司
			}
		}
		return orderSubDTO;
	}

	private AfterSalesOrder getAfterSalesOrder(OrderSubDTO orderSubDTO) {
		//退款状态非成功及失败只存在唯一一条
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(OrderSubStatusEnum.AFTER_SALE_FAILURE.value);
		statusList.add(OrderSubStatusEnum.AFTER_SALE_SUCCESS.value);

		Example example = new Example(AfterSalesOrder.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("orderId", orderSubDTO.getId());
		criteria.andEqualTo("mainOrderId", orderSubDTO.getMainOrderId());
		criteria.andNotIn("status", statusList);
		AfterSalesOrder afterSalesOrder = afterSalesOrderMapper.selectOneByExample(example);
		if (Objects.nonNull(afterSalesOrder)) {
			return afterSalesOrder;
		} else {
			//TODO 解决售后单数据缺失导致报错影响其他数据无法展示（需优化）
			afterSalesOrder = new AfterSalesOrder();
			afterSalesOrder.setId(000000L);
			return afterSalesOrder;
		}

		//throw new YimaoException("售后订单不存在");
	}


	/**
	 * 状态校验
	 */
	private OrderSubDTO commonDetailStatusVerify(OrderSubDTO orderSubDTO) {
		//待付款
		log.debug("mainStatus===" + orderSubDTO.getStatus());

		// 代付款：正常代付款、支付审核失败
		if (orderSubDTO.getStatus() == 0) {
			if (Objects.isNull(orderSubDTO.getPay()) || Objects.isNull(orderSubDTO.getPayStatus())) {
				throw new YimaoException("查询失败，订单信息有误");
			}
			orderSubDTO.setDetailStatus(OrderDetailStatus.PENDING_PAYMENT.value);
			orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.PENDING_PAYMENT.name);
			Boolean flag = orderSubDTO.getPayType() != null && (orderSubDTO.getPayType() == 3 || orderSubDTO.getPayType() == 4);
			if (flag) {
				//支付审核失败:线下支付,提交凭证
				if (orderSubDTO.getPayStatus() == 4 && orderSubDTO.getPayTerminal() == 1) {
					//支付审核失败
					orderSubDTO.setDetailStatus(OrderDetailStatus.PAY_AUDIT_FAILED.value);
					orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.PAY_AUDIT_FAILED.name);
					//如果是支付审核失败原因
					Example payAuditExample = new Example(OrderPayCheck.class);
					Example.Criteria payAuditCriteria = payAuditExample.createCriteria();
					payAuditCriteria.andEqualTo("orderId", orderSubDTO.getMainOrderId());
					payAuditCriteria.andEqualTo("status", 2);
					List<OrderPayCheck> payAuditRecords = orderPayCheckMapper.selectByExample(payAuditExample);
					if (CollectionUtil.isNotEmpty(payAuditRecords)) {
						orderSubDTO.setPayAuditFailureReason(payAuditRecords.get(0).getReason());
					}
				}
			}
		}

		//待发货-》包含：待发货、订单取消中、 支付审核中、待接单(同)、待出库
		if (orderSubDTO.getStatus() == 1 || orderSubDTO.getStatus() == 2 || orderSubDTO.getStatus() == 3) {
			orderSubDTO.setDetailStatus(OrderDetailStatus.TO_BE_DELIVERED.value);
			orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.TO_BE_DELIVERED.name);

			if (orderSubDTO.getStatus() == 1) {
				//支付审核中
				Boolean flag = orderSubDTO.getStatus() == 1 && orderSubDTO.getPayStatus() == 2 && orderSubDTO.getPayType() != null && (orderSubDTO.getPayType() == 3 || orderSubDTO.getPayType() == 4);
				if (flag) {
					orderSubDTO.setDetailStatus(OrderDetailStatus.PAY_AUDITING.value);
					orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.PAY_AUDITING.name);
				}
			}

			//待接单(租赁)
			if (orderSubDTO.getStatus() == 2 && orderSubDTO.getProductType() == 3) {
				orderSubDTO.setDetailStatus(OrderDetailStatus.WAITING_ORDER.value);
				orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.WAITING_ORDER.name);
			}

			//实物商品：订单取消中
			if (orderSubDTO.getProductType() == 1 && orderSubDTO.getStatus() == 3) {
				//待出库
				orderSubDTO.setDetailStatus(OrderDetailStatus.WAIT_TO_LIBRARY.value);
				orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.WAIT_TO_LIBRARY.name);
			}
		}

		//待收货/待接单
		if (orderSubDTO.getStatus() == 4) {
			orderSubDTO.setDetailStatus(OrderDetailStatus.PENDING_RECEIPT.value);
			orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.PENDING_RECEIPT.name);
			//租赁商品：安装工已经接单
			if (orderSubDTO.getProductType() == 3) {
				//已接单
				List<Integer> query = new ArrayList<>();
				query.add(2);//已接单
				query.add(3);//安装处理中
				Example example = new Example(WorkOrder.class);
				Example.Criteria criteria = example.createCriteria();
				criteria.andEqualTo("subOrderId", orderSubDTO.getId());
				criteria.andIn("status", query);
				WorkOrder workOrder = workOrderMapper.selectOneByExample(example);
				if (workOrder != null) {
					orderSubDTO.setDetailStatus(OrderDetailStatus.ORDER_RECEIVED.value);
					orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.ORDER_RECEIVED.name);
				} else {
					log.error("====================获取工单信息失败==============================");
					log.error("获取工单信息失败,订单号为" + orderSubDTO.getId());
					log.error("======================获取工单信息失败============================");
//                    throw new YimaoException("获取工单信息失败!");
				}
			}
		}

		//已完成
		if (orderSubDTO.getStatus() == 5) {
			orderSubDTO.setDetailStatus(OrderDetailStatus.SUCCESSFUL_TRADE.value);
			orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.SUCCESSFUL_TRADE.name);
		}

		//售后中
		if (orderSubDTO.getStatus() == 6) {
			if (Objects.isNull(orderSubDTO.getSubStatus())) {
				log.error("售后单不能没有售后状态");
				throw new YimaoException("售后状态异常!");
			}

			AfterSalesOrder afterSalesOrder = this.getAfterSalesOrder(orderSubDTO);
			orderSubDTO.setAfterSalesOrderId(afterSalesOrder.getId());
			//租赁商品-待经销商审核
			if (orderSubDTO.getProductType() == 3) {
				if (orderSubDTO.getSubStatus() == 0) {
					//待经销商审核
					orderSubDTO.setDetailStatus(OrderDetailStatus.PENDING_AUDIT_DISTRIBUTOR.value);
					orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.PENDING_AUDIT_DISTRIBUTOR.name);
				}
			}
			if (orderSubDTO.getSubStatus() == 1) {
				//待总部审核
				orderSubDTO.setDetailStatus(OrderDetailStatus.PENDING_AUDIT_HEAD.value);
				orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.PENDING_AUDIT_HEAD.name);
			}
			//实物商品：待退货
			if (orderSubDTO.getSubStatus() == 2 && orderSubDTO.getProductType() == 1) {
				//售后待退货
				orderSubDTO.setDetailStatus(OrderDetailStatus.RETURNING.value);
				orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.RETURNING.name);

			}
			//售后待退款:租赁商品(只能退款),实物商品(退款不退货、退款退货)
			if (orderSubDTO.getSubStatus() == 3) {
				orderSubDTO.setDetailStatus(OrderDetailStatus.PENDING_REFUND.value);
				orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.PENDING_REFUND.name);

			}
			if (orderSubDTO.getSubStatus() == 4) {
				//取消失败
				orderSubDTO.setDetailStatus(OrderDetailStatus.CANCEL_FAILED.value);
				orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.CANCEL_FAILED.name);
			}
		}

		//交易关闭（售后成功的单）
		if (orderSubDTO.getStatus() == 7) {
			//已退款
			orderSubDTO.setDetailStatus(OrderDetailStatus.REFUNDED.value);
			orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.REFUNDED.name);
			if (Objects.nonNull(orderSubDTO.getSubStatus()) && orderSubDTO.getSubStatus() == 5) {
				//售后成功，但无需退款的订单。（目前只针对租赁商品-》货到付款，折机产品）
				if (Objects.nonNull(orderSubDTO.getActivityType()) && Objects.equals(orderSubDTO.getActivityType(), 2) || (Objects.equals(orderSubDTO.getPayTerminal(), 2) && !orderSubDTO.getPay())) {
					orderSubDTO.setDetailStatus(OrderDetailStatus.TRADING_CLOSED.value);
					orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.TRADING_CLOSED.name);
				}
			}
		}
		if (orderSubDTO.getStatus() == 8) {
			//已取消
			orderSubDTO.setDetailStatus(OrderDetailStatus.CANCELLED.value);
			orderSubDTO.setDetailStatusDescribe(OrderDetailStatus.CANCELLED.name);
		}
		return orderSubDTO;
	}


	/**
	 * @param orderSubDTO
	 * @description 我的订单-查询主订单信息和统计水机状态数量
	 * @author zhilin.he
	 * @date 2019/1/16 16:49
	 */
	@Override
	public Map<String, Object> orderNumInfo(OrderSubDTO orderSubDTO) {
		if (orderSubDTO == null) {
			throw new BadRequestException("参数不能为空!");
		}
		if (orderSubDTO.getUserId() == null) {
			throw new BadRequestException("用户id不能为空!");
		}
		if (orderSubDTO.getMainOrderId() == null) {
			throw new BadRequestException("主订单id不能为空!");
		}
		if (orderSubDTO.getStatus() == null) {
			throw new BadRequestException("订单状态不能为空!");
		}
		try {
			Map<String, Object> map = new HashMap<>();
			OrderMain order = new OrderMain();
			OrderMainDTO orderMainDTO = new OrderMainDTO();
			order.setId(orderSubDTO.getMainOrderId());
			order.setUserId(orderSubDTO.getUserId());
			OrderMain orderMain = orderMainMapper.selectOne(order);
			orderMain.convert(orderMainDTO);
			//我的订单-水机订单-子订单主状态总数
			Integer statusNum = orderMain.getCount();
			//我的订单-水机订单-子订单子状态总数
			Integer subStatusNum = orderSubMapper.selectOrderCountBySubStatus(orderSubDTO);
			map.put("orderMainDTO", orderMainDTO);
			map.put("statusNum", statusNum);
			map.put("subStatusNum", subStatusNum);
			return map;
		} catch (Exception e) {
			logger.error("查询主订单信息和统计水机状态数量有误", e);
			throw new YimaoException(e.getMessage(), e);
		}
	}

	@Override
	public OrderSubDTO findOrderById(Long id) {
		OrderSub orders = orderSubMapper.selectByPrimaryKey(id);
		OrderSubDTO dto = new OrderSubDTO();
		orders.convert(dto);
		SubOrderDetail orderDetail = subOrderDetailMapper.selectByPrimaryKey(id);
		if (orderDetail != null) {
			orderDetail.convert(dto);
		}
		return dto;
	}

	/**
	 * @param userName 用户姓名
	 * @param orderId  订单号
	 * @description 根据订单号删除订单（逻辑删除）
	 * @author zhilin.he
	 * @date 2019/1/24 15:21
	 */
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	@Override
	public void deleteOrderSub(String userName, Long orderId) {
		//根据订单号查询订单
		OrderSub query = new OrderSub();
		query.setId(orderId);
		OrderSub orders = orderSubMapper.selectOne(query);
		if (orders == null) {
			log.error("根据订单id查询不到订单,订单ID：" + orderId);
			throw new NotFoundException("订单不存在");
		}
		Integer status = orders.getStatus();
		// 待发货和待收货状态的订单不能删除
		if (status == OrderStatusEnum.TO_BE_DELIVERED.value || status == OrderStatusEnum.PENDING_RECEIPT.value) {
			log.error("待发货和待收货状态的订单不能删除,订单ID：" + orderId);
			throw new NotFoundException("待发货和待收货状态的订单不能删除");
		}
		Date now = new Date();
		orders.setDeleted(true);
		orders.setUpdateTime(now);
		int result = orderSubMapper.updateByPrimaryKeySelective(orders);
		if (result < 1) {
			throw new YimaoException("订单删除失败!");
		}

		query = new OrderSub();
		query.setMainOrderId(orders.getMainOrderId());
		query.setDeleted(false);
		int count = orderSubMapper.selectCount(query);
		if (count == 0) {
			//主订单下的子订单都被删除的话，需要把主订单也标记为删除状态
			OrderMain main = new OrderMain();
			main.setId(orders.getMainOrderId());
			main.setDeleted(true);
			main.setUpdateTime(now);
			result = orderMainMapper.updateByPrimaryKeySelective(main);
			if (result < 1) {
				throw new YimaoException("订单删除失败!");
			}
		}
	}

	/**
	 * 根据主订单ID查询子订单信息
	 *
	 * @param mainOrderId
	 * @return
	 */
	@Override
	public List<OrderSub> findOrdersByMainOrderId(Long mainOrderId) {
		return orderSubMapper.selectByMainOrderIdForAfterPay(mainOrderId);
	}

	/**
	 * @param orderSubDTO
	 * @param ids
	 * @return java.lang.Integer
	 * @description 批量更新订单状态（批量取消订单）
	 * @author zhilin.he
	 * @date 2019/2/22 16:49
	 * @date 2019/10/22 21:07 修改 by zhangbo
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public JSONObject updateOrderStatusBatch(OrderSubDTO orderSubDTO, List<Long> ids) {
		JSONObject object = new JSONObject();
		JSONArray failureArray = new JSONArray();
		JSONArray successArray = new JSONArray();
		//取消失败的次数
		try {
			if (CollectionUtil.isEmpty(ids)) {
				throw new BadRequestException("请选择要取消的订单");
			}

			Date now = new Date();
			List<OrderStatusRecord> list = new ArrayList<>();
			for (Long orderId : ids) {
				JSONObject failureJson = new JSONObject();
				JSONObject successJson = new JSONObject();
				//根据订单号查询订单
				OrderSub orders = orderSubMapper.selectByPrimaryKey(orderId);
				if (orders == null) {
					failureJson.put("id", orderId);
					failureJson.put("reason", "取消失败，订单不存在");
					failureArray.add(failureJson);
					continue;
				}
				Integer status = orders.getStatus();
				Integer productType = orders.getProductType();
				// 处于待付款、待发货、待出库、待收货(安装工取消)的订单才能取消订单
				if (status != OrderStatusEnum.PENDING_PAYMENT.value && status != OrderStatusEnum.TO_BE_DELIVERED.value && status != OrderStatusEnum.TO_BE_OUT_OF_STOCK.value && status != OrderStatusEnum.PENDING_RECEIPT.value) {
					log.error("处于待付款、待发货和待出库状态和待发货的订单才能取消订单,不在范围的订单ID：" + orderId);
					failureJson.put("id", orderId);
					failureJson.put("reason", "取消失败，订单状态不符合取消条件");
					failureArray.add(failureJson);
					continue;
				}

				//虚拟产品只有未付款下，才能取消
				if (ProductModeEnum.VIRTUAL.value == productType && status != OrderStatusEnum.PENDING_PAYMENT.value) {
					failureJson.put("id", orderId);
					failureJson.put("reason", "取消失败，虚拟产品只有待付款状态才能取消");
					failureArray.add(failureJson);
					continue;
				}

				//获取工单信息
				WorkOrder workOrder = null;
				if (productType == ProductModeEnum.LEASE.value) {
					workOrder = workOrderMapper.findWorkOrderByOrderId(orders.getId());
				}

				//待付款-取消订单
				if (status == OrderStatusEnum.PENDING_PAYMENT.value) {
					orders.setStatus(OrderStatusEnum.CANCELLED.value);
					//租赁产品，未付款取消订单。
					if (productType == ProductModeEnum.LEASE.value) {
						//获取订单上面的经销商信息
						if (Objects.nonNull(orders.getDistributorId())) {
							DistributorDTO distributorDTO = userFeign.getDistributorById(orders.getDistributorId());
							if (Objects.nonNull(distributorDTO)) {
								//还配额
								Example example = new Example(QuotaChangeRecord.class);
								Example.Criteria criteria = example.createCriteria();
								criteria.andEqualTo("type", 1);
								criteria.andEqualTo("orderId", orders.getId());
								QuotaChangeRecord quotaChangeRecord = quotaChangeRecordMapper.selectOneByExample(example);
								if (quotaChangeRecord != null) {
									if (quotaChangeRecord.getCount() != 0) {
										quotaChangeRecordService.quotaChange(orders.getId(), orders.getDistributorId(), "未付款取消订单-配额", 2, 1, null);
									} else {
										quotaChangeRecordService.quotaChange(orders.getId(), orders.getDistributorId(), "未付款取消订单-金额", 2, 0, orders.getFee());
									}
								}
							} else {
								log.error("取消订单，修改配额失败。请联系管理员,订单ID：" + orderId);
								failureJson.put("id", orderId);
								failureJson.put("reason", "取消失败，订单状态不符合取消条件");
								failureArray.add(failureJson);
								continue;
							}
						}
					}
					//2020-03-17
					//活动商品还配额
					if (orders.getActivityType() != null && orders.getActivityType() == ProductActivityType.PANIC_BUYING.value) {
						productFeign.addProductActivityStock(orders.getActivityId(), orders.getCount());
					}
				} else {
					orders.setStatus(OrderStatusEnum.AFTER_SALE.value);
					//实物商品取消订单：  待发货取消订单 / 待出库取消订单
					if (productType == ProductModeEnum.REALTHING.value) {
						if (status == OrderStatusEnum.TO_BE_DELIVERED.value) {
							//待发货-取消订单-财务审核
							orders.setSubStatus(OrderSubStatusEnum.PENDING_REFUND.value);
						} else {
							//待出库-取消订单-总部审核(物资)-财务审核
							orders.setSubStatus(OrderSubStatusEnum.PENDING_AUDIT_HEAD.value);
						}
						//租赁商品取消订单：
					} else if (productType == ProductModeEnum.LEASE.value) {
						//待接单取消订单 / 待收货取消订单
						if (status == OrderStatusEnum.TO_BE_DELIVERED.value || status == OrderStatusEnum.PENDING_RECEIPT.value) {
							//获取工单信息
//                            WorkOrder workOrder = workOrderMapper.findWorkOrderByOrderId(orders.getId());
							if (workOrder == null) {
								failureJson.put("id", orderId);
								failureJson.put("reason", "取消失败，未获取到工单信息");
								failureArray.add(failureJson);
								continue;
							}

							//已接单，服务中 取消订单-只能安装工操作取消
							Boolean flag = status == OrderStatusEnum.PENDING_RECEIPT.value
									&& workOrder.getStatus() == WorkOrderStatusEnum.COMPLETED.value
									&& Objects.nonNull(workOrder.getChargeback()) && workOrder.getChargeback();
							if (flag) {
								//处理中-取消订单-经销商审核-总部审核(售后)-财务审核
								orders.setStatus(OrderStatusEnum.AFTER_SALE.value);
								orders.setSubStatus(OrderSubStatusEnum.PENDING_AUDIT_DISTRIBUTOR.value);

							} else if (status == OrderStatusEnum.TO_BE_DELIVERED.value && workOrder.getStatus() == WorkOrderStatusEnum.ASSIGNED.value) {
								//未接单-取消订单-总部审核(售后)-财务审核
								orders.setStatus(OrderStatusEnum.AFTER_SALE.value);
								orders.setSubStatus(OrderSubStatusEnum.PENDING_AUDIT_HEAD.value);

								//经销商app取消租贷产品订单更新工单信息,未接单状态
								updateWorkOrderInfo(workOrder, orderSubDTO.getCancelReason(), orderSubDTO.getRemark());
							} else {
								failureJson.put("id", orderId);
								failureJson.put("reason", "取消失败，安装工已接单");
								failureArray.add(failureJson);
								continue;
							}
						} else {
							log.error("处于待接单和待收货的订单才能取消,不在范围的订单ID：" + orderId);
							failureJson.put("id", orderId);
							failureJson.put("reason", "取消失败，订单状态不符合取消条件");
							failureArray.add(failureJson);
							continue;
						}
					}
				}

				orders.setCancelReason(orderSubDTO.getCancelReason());
				orders.setCancelTime(now);
				// 2020-03-16 修改安装工提交的退单备注覆盖下单备注信息
				// orders.setRemark(orderSubDTO.getRemark());
				if (StringUtil.isNotEmpty(orderSubDTO.getRemark())) {
					orders.setCancelReason(orders.getCancelReason() + "---" + orderSubDTO.getRemark());
				}
				orders.setUpdateTime(now);
				int result = orderSubMapper.updateByPrimaryKey(orders);
				if (result < 1) {
					failureJson.put("id", orderId);
					failureJson.put("reason", "取消失败，系统异常");
					failureArray.add(failureJson);
					continue;
				}

				log.info("变更前的订单状态：=》" + orders.getStatus());
				//添加订单状态变更记录
				OrderStatusRecord orderStatusRecord = new OrderStatusRecord();
				orderStatusRecord.setId(orderId);
				orderStatusRecord.setCreateTime(now);
				orderStatusRecord.setCreator(orderSubDTO.getUserName());
				//变更之后的状态
				orderStatusRecord.setDestStatus(orders.getStatus());
				//变更之前的状态
				orderStatusRecord.setOrigStatus(status);
				orderStatusRecord.setRemark("取消订单");

				if (status != OrderStatusEnum.PENDING_PAYMENT.value) {
					//添加售后订单
					this.addAfterSalesOrder(orders, orderSubDTO);
				}

				list.add(orderStatusRecord);
				successJson.put("id", orderId);
				successArray.add(successJson);
			}
			//有一个以上失败，且不是全部失败的，弹出框提示
			object.put("successArray", successArray);
			object.put("failureArray", failureArray);
			if (CollectionUtil.isNotEmpty(list)) {
				// 批量插入订单状态变更
				orderStatusRecordMapper.insertBatch(list);
			}
			return object;
		} catch (Exception e) {
			logger.error("批量更新订单状态（批量取消订单）失败！", e);
			throw new YimaoException("取消订单失败！");
		}
	}

	/**
	 * 水机订单取消,同步工单取消
	 *
	 * @param workOrder
	 * @param remark
	 * @param reason
	 */
	private void updateWorkOrderInfo(WorkOrder workOrder, String reason, String remark) {
		try {
			log.info("==========经销商取消订单同步工单信息 start=============");
			Date now = new Date();
			WorkOrder update = new WorkOrder();
			update.setId(workOrder.getId());
			update.setOperationTime(now);
			update.setStatus(WorkOrderStatusEnum.COMPLETED.value);
			update.setChargeback(true);
			update.setChargebackType(1);//1-经销商退单；2-客服退单
			if (StringUtil.isNotEmpty(reason)) {
				update.setChargebackReason(reason);
			}
			if (StringUtil.isNotEmpty(remark)) {
				update.setChargebackRemark(remark);
			}
			update.setChargebackStatus(1);//退单状态：0-待退单；1-退单中；2-退单成功；
			update.setChargebackTime(now);
			update.setCompleteType(WorkOrderCompleteEnum.AUTO_COMPLETE.getState());//异常完成
			update.setCompleteTime(now);
			workOrderMapper.updateByPrimaryKeySelective(update);
			//保存工单操作记录
			WorkOrderOperation operation = new WorkOrderOperation();
			operation.setWorkOrderId(workOrder.getId());
			operation.setOperation("经销商退单");
			if (StringUtil.isNotEmpty(remark)) {
				operation.setRemark(remark);
			}
			operation.setCreateTime(now);
			workOrderOperationMapper.insertSelective(operation);

			log.info("==========经销商取消订单同步工单信息 end=============");
		} catch (Exception e) {
			log.error("=========(workOrderId=" + workOrder.getId() + ")取消订单更新工单异常:" + e.getMessage());
			throw new YimaoException("订单取消同步售后失败");

		}

	}

	private void addAfterSalesOrder(OrderSub orderSub, OrderSubDTO orderSubDTO) {
		SubOrderDetail subOrderDetail = subOrderDetailMapper.selectByPrimaryKey(orderSub.getId());
		if (Objects.nonNull(subOrderDetail)) {
			Example example = new Example(AfterSalesOrder.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andEqualTo("orderId", orderSub.getId());
			AfterSalesOrder afterSalesOrder = afterSalesOrderMapper.selectOneByExample(example);
			//先删后增
			if (Objects.nonNull(afterSalesOrder)) {
				afterSalesOrderMapper.deleteByPrimaryKey(afterSalesOrder.getId());
			}

			//添加售后订单
			afterSalesOrder = new AfterSalesOrder();
			afterSalesOrder.setId(UUIDUtil.buildOrderId(orderSub.getProductType()));
			afterSalesOrder.setMainOrderId(orderSub.getMainOrderId());
			afterSalesOrder.setOrderId(orderSub.getId());
			//售后状态:0-待审核(经销商)  1-待审核(总部) 2-待退货入库  3-待退款(财务)  4-售后失败 5-售后成功；
			afterSalesOrder.setStatus(orderSub.getSubStatus());
			afterSalesOrder.setSalesType(1);
			afterSalesOrder.setProductType(orderSub.getProductType());
			afterSalesOrder.setProductId(orderSub.getProductId());
			afterSalesOrder.setProductName(subOrderDetail.getProductName());
			afterSalesOrder.setProductCategoryName(subOrderDetail.getProductCategoryName());
			afterSalesOrder.setProductCompanyName(subOrderDetail.getProductCompanyName());
			afterSalesOrder.setTerminal(orderSub.getTerminal());
			afterSalesOrder.setNum(orderSub.getCount());
			afterSalesOrder.setUserId(orderSub.getUserId());
			afterSalesOrder.setUserType(subOrderDetail.getUserType());
			afterSalesOrder.setPayType(orderSub.getPayType());
			afterSalesOrder.setPayFee(orderSub.getFee());
			afterSalesOrder.setRefundFee(orderSub.getFee());
			afterSalesOrder.setRefundReason(orderSubDTO.getCancelReason());
			//手续费
			BigDecimal formalitiesFee = orderSubDTO.getFormalitiesFee();
			afterSalesOrder.setFormalitiesFee(formalitiesFee == null ? new BigDecimal(0) : formalitiesFee);
			if (afterSalesOrder.getFormalitiesFee() != null) {
				afterSalesOrder.setRealRefundFee(orderSub.getFee().subtract(afterSalesOrder.getFormalitiesFee()));
			}
			afterSalesOrder.setCreateTime(new Date());
			afterSalesOrder.setDeleted(0);
			afterSalesOrderMapper.insert(afterSalesOrder);
		}
	}

	/**
	 * 批量删除订单
	 *
	 * @return
	 */
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	@Override
	public void updateOrderDeleted(List<Long> ids, String userName) {
		if (CollectionUtil.isNotEmpty(ids)) {
			for (Long orderId : ids) {
				this.deleteOrderSub(userName, orderId);
			}
			Example example = new Example(OrderSub.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andEqualTo("id", ids.get(0));
			criteria.andEqualTo("deleted", false);
			List<OrderSub> list = orderSubMapper.selectByExample(example);
			if (CollectionUtil.isEmpty(list)) {
				OrderSub orderSub = orderSubMapper.selectByPrimaryKey(ids.get(0));
				OrderMain orderMain = orderMainMapper.selectByPrimaryKey(orderSub.getMainOrderId());
				if (orderMain == null) {
					throw new NotFoundException("主订单数据已不存在!");
				}
				orderMain.setDeleted(true);
				orderMain.setUpdateTime(new Date());
				int result = orderMainMapper.updateByPrimaryKey(orderMain);
				if (result < 1) {
					throw new BadRequestException("批量更新是否删除订单失败!");
				}
			}
		}
	}

	/**
	 * 查询订单对账列表
	 *
	 * @param query    查询条件
	 * @param pageNum  当前页
	 * @param pageSize 每页显示条数
	 */
	@Override
	public PageVO<OrderBillVO> queryOrderBillList(OrderBillQuery query, Integer pageNum, Integer pageSize) {
		if (query.getProductCompanyId() != null && query.getProductCompanyId() != 0) {
			ProductCompanyDTO company = productFeign.getProductCompanyById(query.getProductCompanyId());
			if (company == null) {
				throw new BadRequestException("产品公司信息有误。");
			}
		}
		PageHelper.startPage(pageNum, pageSize);
		Page<OrderBillVO> page = orderSubMapper.listOrderBill(query);
		return new PageVO<>(pageNum, page);
	}

	/**
	 * 订单对账：汇总导出
	 *
	 * @param query 查询条件
	 */
	@Override
	public List<OrderBillExport> exportOrderBill(OrderBillQuery query) {
		if (query.getProductCompanyId() != null && query.getProductCompanyId() != 0) {
			ProductCompanyDTO company = productFeign.getProductCompanyById(query.getProductCompanyId());
			if (company == null) {
				throw new BadRequestException("产品公司信息有误。");
			}
		}
		return orderSubMapper.exportOrderBill(query);
	}

	/**
	 * 获取经销商及其下属客户订单总数
	 *
	 * @param distributorId 经销商Id
	 * @return Integer
	 * @author hhf
	 * @date 2019/1/26
	 */
	@Override
	public Integer getOrderCountByUserId(Integer distributorId) {
		return orderSubMapper.selectOrderCountByDistributorId(distributorId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateOrderInfo(OrderSubDTO dto) {
		OrderSubDTO order = orderSubService.findOrderById(dto.getId());
		if (order == null) {
			throw new BadRequestException("订单信息不存在。");
		}
		//实物商品【待发货可以修改】 租赁商品【待发货或者待收货可以修改】
		if (order.getStatus() != 2 && (order.getStatus() != 4 || order.getProductType() != ProductModeEnum.LEASE.value)) {
			log.error("实物商品【待发货可以修改】 租赁商品【待发货或者待收货可以修改】,此订单不在范围内");
			throw new YimaoException("修改失败，只能修改待发货或待收货[净水]下的订单");
		}
		//租赁产品，输入了sn码设备号不允许更换
		if (order.getStatus() == 4 && order.getProductType() == ProductModeEnum.LEASE.value) {
			if (StringUtil.isEmpty(order.getRefer())) {
				log.error("修改失败，获取工单信息异常");
				throw new YimaoException("修改失败，获取工单信息异常");
			}
			WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(order.getRefer());
			if (workOrder == null) {
				log.error("修改失败，获取工单信息异常");
				throw new YimaoException("修改失败，获取工单信息异常");
			}

			//判断是新老流程【老流程要在提货前可以修改，新流程输入批次号、SN码】
			if (Objects.nonNull(workOrder.getProvince()) && Objects.nonNull(workOrder.getCity()) && Objects.nonNull(workOrder.getRegion())) {
				OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
				//新流程 - 新流程输入批次号、SN码前
				if (onlineArea != null && Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
					if (StringUtil.isNotEmpty(workOrder.getSn()) || StringUtil.isNotEmpty(workOrder.getLogisticsCode()) || workOrder.getDeviceId() != null) {
						log.error("修改失败，已经输入sn码、批次号的工单不能修改");
						throw new YimaoException("修改失败，此工单安装工正在服务中[已输入sn码、批次号]");
					}
				} else {
					//老流程 - 提货前可以修改
					if (Objects.nonNull(workOrder.getPickTime()) || StringUtil.isNotEmpty(workOrder.getSn()) || StringUtil.isNotEmpty(workOrder.getLogisticsCode()) || workOrder.getDeviceId() != null) {
						log.error("修改失败，已经输入sn码、批次号的工单不能修改");
						throw new YimaoException("修改失败，此工单安装工正在服务中[已提货]");
					}
				}
			} else {
				log.error("修改失败，安装地区有误");
				throw new YimaoException("修改失败，安装地区有误:" + workOrder.getId());
			}
		}

		// 针对净水产品：【如果更换了产品 或者 计费方式】需要同步工单和售后
		boolean flag = (dto.getProductId() != null && !Objects.equals(dto.getProductId(), order.getProductId())) || null != dto.getCostId();
		if (flag && order.getProductType() == ProductModeEnum.LEASE.value) {
			//操作更换型号
			workOrderService.changeProductAndCost(order.getRefer(), dto.getProductId(), dto.getCostId() == null ? order.getCostId() : dto.getCostId(), null, null, null, 1);
		}

		//更新收货地址信息 【待收货下不能修改收货地址】
		if (order.getStatus() == 2 && StringUtil.isNotEmpty(dto.getAddresseeName())) {
			SubOrderDetail detail = new SubOrderDetail();
			OrderAddress address = new OrderAddress();
			if (order.getProductType() != ProductModeEnum.LEASE.value) {
				detail.setAddresseeProvince(dto.getAddresseeProvince());
				detail.setAddresseeCity(dto.getAddresseeCity());
				detail.setAddresseeRegion(dto.getAddresseeRegion());
				address.setProvince(dto.getAddresseeProvince());
				address.setCity(dto.getAddresseeCity());
				address.setRegion(dto.getAddresseeRegion());
			}
			detail.setSubOrderId(dto.getId());
			detail.setUpdateTime(new Date());
			detail.setAddresseeName(dto.getAddresseeName());
			detail.setAddresseePhone(dto.getAddresseePhone());
			detail.setAddresseeSex(dto.getAddresseeSex());
			detail.setAddresseeStreet(dto.getAddresseeStreet());
			subOrderDetailMapper.updateByPrimaryKeySelective(detail);

			address.setId(dto.getId());
			address.setContact(dto.getAddresseeName());
			address.setPhone(dto.getAddresseePhone());
			address.setSex(dto.getAddresseeSex());
			address.setStreet(dto.getAddresseeStreet());
			address.setCreateTime(new Date());
			orderAddressMapper.insertSelective(address);
		}


//        Date now = new Date();
//        SubOrderDetail detail = new SubOrderDetail();
//        detail.setSubOrderId(dto.getId());
//        detail.setUpdateTime(now);
//        ProductDTO product = productFeign.getProductById(dto.getProductId());
		// 如果是更换产品了，要级联修改订单，订单详情，收益分配 [-》同步安装工app(水机)]
		// 产品ID不相同 -需要修改收益分配-
//        if (dto.getProductId() != null && !Objects.equals(dto.getProductId(), order.getProductId())) {
//            if (product != null) {
//                // 更新子订单上的产品信息
//                OrderSub orderSub = new OrderSub();
//                orderSub.setId(dto.getId());
//                orderSub.setProductId(product.getId());
//                orderSub.setProductModel(product.getCategoryName());
//                orderSub.setUpdateTime(now);
//                int result = orderSubMapper.updateByPrimaryKeySelective(orderSub);
//                if (result != 1) {
//                    throw new YimaoException("修改订单[更新子订单产品信息失败]");
//                }
//
//                // 1-是否有收益
//                ProductIncomeRecord record = productIncomeRecordMapper
//                        .selectProductIncomeRecordByOrderId(order.getId(), IncomeType.PRODUCT_INCOME.value);
//                if (record != null) {
//                    // 2-获取收益规则
//                    Set<Integer> incomeRuleIds = productIncomeRuleService
//                            .listIncomeRuleIdByProductId(order.getProductId());
//                    IncomeRuleDTO incomeRule = incomeRuleService.getIncomeRuleByIdAndIncomeType(incomeRuleIds,
//                            IncomeType.PRODUCT_INCOME.value);
//                    if (incomeRule == null) {
//                        log.error("获取产品收益分配规则失败");
//                        throw new YimaoException("获取产品收益分配规则失败");
//                    }
//                    // 3-更新收益记录
//                    record.setIncomeRuleId(incomeRule.getId());
//                    record.setUpdateTime(now);
//                    // [ 产品ID，产品名称，产品类型ID，类型，名称，规则ID，]
//                    record.setProductId(product.getId());
//                    record.setProductName(product.getName());
//                    record.setProductCategoryId(product.getCategoryId());
//                    record.setProductCategoryName(product.getCategoryName());
//                    productIncomeRecordMapper.updateByPrimaryKeySelective(record);
//
//                    try {
//                        // 4-删除收益明细
//                        Example example = new Example(ProductIncomeRecordPart.class);
//                        Example.Criteria criteria = example.createCriteria();
//                        criteria.andEqualTo("recordId", record.getId());
//                        int count = productIncomeRecordPartMapper.deleteByExample(example);
//                        if (count < 1) {
//                            throw new YimaoException("修改订单,删除收益明细失败。");
//                        }
//                        // 5-重新分配收益明细
//                        productIncomeRecordService.allotSellIncomePart(order, incomeRule, record);
//                        // 设置详情信息
//                        this.fillProductInfo(product, detail);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        throw new YimaoException("修改订单，重新分配收益失败");
//                    }
//                } else {
//                    log.info("此订单没有分配收益");
//                    log.error("此订单没有分配收益");
//                    throw new YimaoException("此订单没有分配收益");
//                }
//            } else {
//                log.error("修改失败，产品信息不存在");
//                throw new YimaoException("修改失败，产品信息不存在");
//            }
//        }

//        // 6-更新收货地址
//        if (order.getProductType() == ProductModeEnum.LEASE.value || order.getProductType() == ProductModeEnum.REALTHING.value) {
//            if (order.getProductType() == ProductModeEnum.LEASE.value) {
//                if (dto.getCostId() != null) {
//                    // 存在修改计费方式
//                    detail.setCostId(dto.getCostId());
//                    detail.setCostName(dto.getCostName());
//                    detail.setOpenAccountFee(dto.getOpenAccountFee());
//                    log.info("参数=" + JSON.toJSONString(detail));
//                    subOrderDetailMapper.updateByPrimaryKeySelective(detail);
//                }
//            }
//
//            if (StringUtil.isNotEmpty(dto.getAddresseeName())) {
//                detail.setAddresseeName(dto.getAddresseeName());
//                detail.setAddresseePhone(dto.getAddresseePhone());
//                detail.setAddresseeSex(dto.getAddresseeSex());
//                detail.setAddresseeProvince(dto.getAddresseeProvince());
//                detail.setAddresseeCity(dto.getAddresseeCity());
//                detail.setAddresseeRegion(dto.getAddresseeRegion());
//                detail.setAddresseeStreet(dto.getAddresseeStreet());
//                subOrderDetailMapper.updateByPrimaryKeySelective(detail);
//
//                OrderAddress address = new OrderAddress();
//                address.setId(dto.getId());
//                address.setContact(dto.getAddresseeName());
//                address.setPhone(dto.getAddresseePhone());
//                address.setSex(dto.getAddresseeSex());
//                address.setProvince(dto.getAddresseeProvince());
//                address.setCity(dto.getAddresseeCity());
//                address.setRegion(dto.getAddresseeRegion());
//                address.setStreet(dto.getAddresseeStreet());
//                address.setCreateTime(now);
//                orderAddressMapper.insertSelective(address);
//            }

//             //针对净水产品：【如果更换了产品 或者 计费方式】需要同步工单和售后
//            boolean flag = (dto.getProductId() != null && !Objects.equals(dto.getProductId(), order.getProductId())) || null != dto.getCostId();
//            if (flag && order.getProductType() == ProductModeEnum.LEASE.value) {
//                // 同步到工单和售后
//                afterUpdateOrder(dto, product, order.getRefer());
//            }
//        }
	}

	/***
	 * 更改产品类型同步工单和售后
	 * @param dto
	 * @param product
	 * @param workOrderId
	 */
	private void afterUpdateOrder(OrderSubDTO dto, ProductDTO product, String workOrderId) {
		log.info("=================修改订单同步工单 start============orderSub=" + JSONObject.toJSONString(dto));
		if (StringUtil.isNotEmpty(workOrderId)) {
			WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(workOrderId);
			if (Objects.isNull(workOrder) || Objects.isNull(product)) {
				log.error("修改产品信息失败，工单信息或产品信息不存在");
				throw new YimaoException("修改产品信息失败");
			}

			WorkOrder update = new WorkOrder();
			update.setId(workOrder.getId());
			// 更新工单信息
//            update.setOperationTime(new Date());
			update.setProductId(product.getId());
			update.setProductName(product.getName());
			update.setCompanyName(product.getCompanyName());
			update.setDeviceModel(product.getCategoryName());

			//改计费方式
			if (null != dto.getCostId()) {
				ProductCostDTO charge = productFeign.productCostGetById(dto.getCostId());
				if (charge == null) {
					log.error("==========修改订单信息同步工单售后失败[订单计费类型无效]=====subOrderId=" + dto.getId() + ",costId=" + dto.getCostId());
					throw new YimaoException("订单计费类型无效");
				}
				// 校验更换的产品型号和原工单上的价格是否相同
				if (!checkCostPrice(charge, workOrder)) {
					throw new YimaoException("只能更换同价位的商品，请重新选择。");
				}

				// 查询更换产品类目
				ProductCategoryCascadeDTO categoryDTO = productFeign.getProductCategoryCascadeById(product.getCategoryId());
				if (categoryDTO == null) {
					throw new YimaoException("产品型号为空");
				}
				update.setCostId(charge.getId());
				update.setOldCostId(charge.getOldId());
				update.setChangeTime(new Date());
				update.setCostName(charge.getName());
//                update.setModelPrice(charge.getRentalFee());
				update.setOpenAccountFee(charge.getInstallationFee());
				update.setFee(charge.getTotalFee());
			}

			update.setUpdateTime(new Date());
			log.info("========修改工单商品信息更新工单数据=====data=" + JSONObject.toJSONString(update));
			int result = workOrderMapper.updateByPrimaryKeySelective(update);
			// 同步售后
			try {
				if (result != 1) {
					throw new YimaoException("修改水机订单[更新工单失败]");
				}
				syncWorkOrderService.syncWorkOrder(workOrder.getId());
			} catch (Exception e) {
				log.error("============修改订单[同步售后失败]workOrderId=" + workOrder.getId() + "异常信息:" + e.getMessage());
				throw new YimaoException("修改订单[同步售后失败]");
			}
		}
		log.info("=================修改订单同步工单 end============");
	}

	/***
	 * 校验价格是否一致
	 * @return
	 */
	private boolean checkCostPrice(ProductCostDTO charge, WorkOrder workOrder) {
		//更改产品型号计费的实际金额为安装费+租赁费
		BigDecimal changeMoney = charge.getTotalFee();

		//当前工单的费用(workorder中的fee显示安装费+租赁费后的数据)
		BigDecimal currentMoney = workOrder.getFee();

		//当前的租赁费
		BigDecimal currentInstallationFee = workOrder.getOpenAccountFee();

		//当前工单的可收益分配金额
		BigDecimal currentRentalFee = currentMoney.subtract(currentInstallationFee);

		//已支付，只能选择同等价位的产品类型
		if (!(changeMoney.doubleValue() == currentMoney.doubleValue()) || !(charge.getRentalFee().doubleValue() == currentRentalFee.doubleValue())) {
			log.error("===========更新商品的价格不一致=======");
			return false;
		}
		return true;
	}

	/**
	 * 修改订单时，设置更换的产品信息
	 *
	 * @param product        商品信息
	 * @param subOrderDetail 子订单详细信息
	 */
	private void fillProductInfo(ProductDTO product, SubOrderDetail subOrderDetail) {
		//订单详情-产品信息设置
		subOrderDetail.setProductId(product.getId());
		subOrderDetail.setProductName(product.getName());
		subOrderDetail.setProductImg(product.getCoverImg());
		subOrderDetail.setProductCompanyId(product.getCompanyId());
		subOrderDetail.setProductCompanyName(product.getCompanyName());
		subOrderDetail.setProductCategoryId(product.getCategoryId());
		subOrderDetail.setProductCategoryName(product.getCategoryName());
		ProductCategoryDTO oneCategory = productFeign.getOneProductCategory(product.getCategoryId());
		if (Objects.nonNull(oneCategory)) {
			subOrderDetail.setProductFirstCategoryId(oneCategory.getId());
			subOrderDetail.setProductFirstCategoryName(oneCategory.getName());
		}
		ProductCategoryDTO twoCategory = productFeign.getTwoProductCategory(product.getCategoryId());
		if (Objects.nonNull(twoCategory)) {
			subOrderDetail.setProductTwoCategoryId(twoCategory.getId());
			subOrderDetail.setProductTwoCategoryName(twoCategory.getName());
		}

	}


	@Override
	public PageVO<WxOrderDTO> wxOrderList(Integer userId, Integer status, String beginTime, String endTime, String keys, String orderId, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<WxOrderDTO> page = orderSubMapper.wxOrderList(userId, status, beginTime, endTime, keys, orderId);
		return new PageVO<>(pageNum, page);
	}


	@Override
	public Integer selectOrderCount(Integer userId, Integer status) {
		return orderSubMapper.selectOrderCount(userId, status);
	}


	@Override
	public Integer auditCount(UserDTO userDTO) {
		Integer count = 0;
		List<UserDTO> customerList;
		List<UserDTO> customers = new ArrayList<>();
		int flag = 1;
		if (userDTO.getUserType() == null || userDTO.getUserType() == UserType.USER_3.value || userDTO.getUserType() == UserType.USER_4.value) {
			//普通用户/分享用户的客户订单为空，
			return 0;
		} else if (userDTO.getUserType() == UserType.USER_7.value) {
			//分销商：查询分销商下的客户
//            flag = 3;
//            customers = userFeign.findCustomers(userDTO.getId(), userDTO.getMid(), userDTO.getDistributorId());
			return orderSubMapper.vipUserCount(userDTO.getId());
		} else {
			customerList = userFeign.findCustomers(userDTO.getId(), userDTO.getMid(), userDTO.getDistributorId());
			if (CollectionUtil.isNotEmpty(customers)) {
				customers.addAll(customerList);
				for (int i = 0; i < customers.size(); i++) {
					if (customers.get(i).getUserType() == UserType.DISTRIBUTOR_1000.value) {
						customers.addAll(userFeign.findCustomers(customers.get(i).getId(), customers.get(i).getMid(), customers.get(i).getDistributorId()));
					}
				}
			}
		}

		List<Integer> list = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(customers)) {
			for (UserDTO dto : customers) {
				list.add(dto.getId());
			}
			count = orderSubMapper.auditCount(list, flag);
		}
		return count;
	}


	@Override
	public PageVO<WxOrderDTO> auditList(Integer userId, String orderKeys, String beginTime, String endTime, String keys, String orderId, Integer pageNum, Integer pageSize) {
		Page<WxOrderDTO> ordersPage = new Page<>();
		List<UserDTO> customers = new ArrayList<>();
		List<UserDTO> customerList;
		List<String> incomeList;
		//活动类型 1：所有  2：显示180活动  3:不显示180活动
		int flag = 1;
		//普通用户/分享用户的客户订单为空，
		UserDTO userDTO = userFeign.getUserById(userId);
		log.info("=================用户身份：" + userDTO.getUserType() + "=================");
		if (userDTO.getUserType() == null || userDTO.getUserType() == UserType.USER_3.value || userDTO.getUserType() == UserType.USER_4.value) {
			return new PageVO<>(pageNum, ordersPage);
		} else if (userDTO.getUserType() == UserType.USER_7.value) {
			//分销商：则只显示我享受收益（有分销权的）的产品订单。（180的订单不显示）
			String income = userDTO.getIncomePermission();
			String[] idArray;
			if (StringUtil.isEmpty(income)) {
				return new PageVO<>(pageNum, ordersPage);
			}
			if (income.contains(",")) {
				idArray = income.split(",");
			} else {
				idArray = new String[]{income};
			}
			incomeList = Arrays.asList(idArray);
			flag = 3;
			customers = userFeign.findCustomers(userDTO.getId(), userDTO.getMid(), userDTO.getDistributorId());
		} else {
			log.info("=================用户身份：" + userDTO.getUserType() + "=================");
			incomeList = null;
			customerList = userFeign.findCustomers(userDTO.getId(), userDTO.getMid(), userDTO.getDistributorId());
			if (CollectionUtil.isNotEmpty(customerList)) {
				customers.addAll(customerList);
				for (int i = 0; i < customers.size(); i++) {
					if (customers.get(i).getUserType() == UserType.DISTRIBUTOR_1000.value) {
						customers.addAll(userFeign.findCustomers(customers.get(i).getId(), customers.get(i).getMid(), customers.get(i).getDistributorId()));

					}
				}
			} else {
				customers = null;
			}
		}

		if (CollectionUtil.isNotEmpty(customers)) {
			List<Integer> list = new ArrayList<>();
			for (UserDTO user : customers) {
				list.add(user.getId());
			}
			PageHelper.startPage(pageNum, pageSize);
			ordersPage = orderSubMapper.auditList(list, incomeList, flag, beginTime, endTime, keys, orderKeys, orderId);
			log.info("***************客户订单列表长度：" + ordersPage.getResult().size() + "***************");
		}

		return new PageVO<>(pageNum, ordersPage);
	}

	/**
	 * 将订单设置为已完成
	 *
	 * @param id 子订单号
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public void completeOrder(Long id) {
		OrderSub order = orderSubMapper.selectForComplete(id);
		if (order != null) {
			//1-将订单设置为已完成
			Date date = new Date();
			order.setId(id);
			//订单状态设置为【5-交易成功】
			order.setStatus(OrderStatusEnum.SUCCESSFUL_TRADE.value);
			//订单完成日期
			order.setCompleteTime(date);
			order.setUpdateTime(date);
			orderSubMapper.updateByPrimaryKeySelective(order);

			//2-将收益设置为已完成（可结算）
			productIncomeRecordService.completeIncome(order);

			//3-升级会员用户逻辑
			if (order.getProductType() == ProductModeEnum.LEASE.value) {
				rabbitTemplate.convertAndSend(RabbitConstant.USER_UPGRADEVIP, order.getUserId());
			}
		}
	}

	@Override
	public void fillHraCardSalesObject(Integer stationId, Long orderId) {
		StationDTO station = systemFeign.getStationById(stationId);
		if (station != null) {
			SubOrderDetail orderDetail = new SubOrderDetail();
			orderDetail.setSubOrderId(orderId);
			orderDetail.setSalesSubjectProvince(station.getProvince());
			orderDetail.setSalesSubjectCity(station.getCity());
			orderDetail.setSalesSubjectRegion(station.getRegion());
			orderDetail.setSalesSubjectCompanyName(station.getStationCompanyName());//销售主体公司名称
			subOrderDetailMapper.updateByPrimaryKeySelective(orderDetail);
		}
	}

	@Override
	public boolean checkOrderByMobile(String mobile) {
		Example example = new Example(SubOrderDetail.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("addresseePhone", mobile);
		List<SubOrderDetail> orderDetails = subOrderDetailMapper.selectByExample(example);
		if (CollectionUtil.isNotEmpty(orderDetails)) {
			return true;
		}
		return false;
	}

	@Override
	public Integer orderExportListCount(OrderConditionDTO query) {
		return orderSubMapper.orderExportListCount(query);
	}

	/**
	 * @param userId
	 * @return java.util.List<com.yimao.cloud.pojo.dto.order.OrderCountDTO>
	 * @description 当前用户的不同订单状态下的订单数量(用于小红点)
	 * @author zhilin.he
	 * @date 2019/8/7 17:07
	 */
	@Override
	public List<OrderCountDTO> checkUserOrderByStatus(Integer userId) {
		List<OrderCountDTO> result = new ArrayList<>();
		List<OrderCountDTO> list = orderSubMapper.selectUserOrderCountByStatus(userId);
		OrderCountDTO dto = new OrderCountDTO();
		Integer count = 0;
		for (OrderCountDTO countDTO : list) {
			//待付款
			if (countDTO.getStatus() == OrderStatusEnum.PENDING_PAYMENT.value) {
				countDTO.setOperationType(MyOrderOperationType.PENDING_PAYMENT.value);
				result.add(countDTO);
			}
			//待发货
			if (countDTO.getStatus() == OrderStatusEnum.TO_BE_DELIVERED.value || countDTO.getStatus() == OrderStatusEnum.WAITING_AUDIT.value
					|| countDTO.getStatus() == OrderStatusEnum.TO_BE_OUT_OF_STOCK.value) {
				count += countDTO.getCount();
			}
			//待收货
			if (countDTO.getStatus() == OrderStatusEnum.PENDING_RECEIPT.value) {
				countDTO.setOperationType(MyOrderOperationType.PENDING_RECEIPT.value);
				result.add(countDTO);
			}
		}
		dto.setOperationType(MyOrderOperationType.TO_BE_DELIVERED.value);
		dto.setCount(count);
		result.add(dto);
		return result;
	}

	/**
	 * 当前用户的客户订单数量
	 */
	@Override
	public Integer selectCustomerOrderCount(Integer userId) {
		UserDTO userDTO = userFeign.getUserById(userId);
		Integer userIdentity = 2;
		Integer mId = null;
		//不同的类型的，展示的数据不一样
		//获取用户身份 1-经销商身份 2-用户身份【默认是用户身份】
		if (UserType.isDistributor(userDTO.getUserType())) {
			userIdentity = 1;
			mId = userDTO.getMid();
		}
		return orderSubMapper.myCustomerOrderCount(userIdentity, userId, mId);

	}

	/**
	 * 根据累计的水机、健康产品已完成的工单数，发放HRA优惠卡
	 *
	 * @param distributorId 经销商e家号
	 * @param phone         手机号
	 * @param userType      用户类型
	 */
	@Override
	public Integer countCompletedOrderFromDate(Integer distributorId, String phone, Integer userType) {
		//政策制定日期
		Date date = DateUtil.transferStringToDate("2018-01-22 00:00:00", "yyyy-MM-dd HH:mm:ss");
		if (UserType.isDistributor(userType)) {
			return orderSubMapper.countDistributorCompletedOrder(distributorId, date);
		} else {
			return orderSubMapper.countUserCompletedOrder(phone, date);
		}
	}

	/**
	 * 将超时未支付的订单状态设置为【已取消】
	 *
	 * @param date 创建时间比较值
	 */
	@Override
	public void timeout(Date date) {
		//超时和未支付的状态为->已取消
		orderSubMapper.timeout(date, OrderStatusEnum.CANCELLED.value);
	}


	@Override
	public List<OrderProductCountDTO> customerOrderCountByType(Integer userId, Integer queryType, Integer subDistributorId) {
		if (queryType == null) {
			queryType = 0;
		}

		//用户信息
		Integer userIdentity = 1;  //是否是经销商
		UserDTO userDTO = userFeign.getBasicUserById(userCache.getUserId());
		if (Objects.isNull(userDTO)) {
			log.error("用户信息不存在");
			throw new YimaoException("用户信息不存在，E家号为：" + userId);
		}
		if (UserType.isDistributor(userDTO.getUserType())) {
			if (userDTO.getMid() == null) {
				log.error("数据错误");
				throw new YimaoException("数据错误，经销商信息不存在，E家号为：" + userId);
			}
		} else { //不是经销商
			userIdentity = 2;
		}
		if ((queryType == 1 || queryType == 2) && UserType.DISTRIBUTOR_950.value != userDTO.getUserType()) {
			throw new YimaoException("查询失败，当前经销商不是企业版主账号");
		}
		if (queryType == 2 && subDistributorId == null) {
			throw new YimaoException("查询失败，请选择正确的子账号");
		}

		List<OrderProductCountDTO> opList = new LinkedList<>();
		List<OrderStatusCountDTO> osList;
		OrderProductCountDTO opDTO;
		OrderStatusCountDTO osDTO;
		//0-待付款；2-待发货；；4-待收货；5-交易成功 //6退货/退款（对应售后中(6)+交易关闭(7)）
		int[] orderStatus = {OrderStatusEnum.PENDING_PAYMENT.value, OrderStatusEnum.TO_BE_DELIVERED.value, OrderStatusEnum.PENDING_RECEIPT.value, OrderStatusEnum.SUCCESSFUL_TRADE.value, OrderStatusEnum.AFTER_SALE.value};
		List<ProductCategoryDTO> firstProductCategory = productFeign.getFirstProductCategory();
		for (ProductCategoryDTO dto : firstProductCategory) {
			opDTO = new OrderProductCountDTO();
			opDTO.setProductCategoryId(dto.getId());
			opDTO.setProductCategoryName(dto.getName());
			//根据产品类型、用户ID查询
			osList = new LinkedList<>();
			for (int os : orderStatus) {    //遍历需要显示的订单状态
				osDTO = new OrderStatusCountDTO();
				osDTO.setStatus(os);
				osDTO.setCount(orderSubMapper.customerOrderCountByDistributor(userId, userDTO.getMid(), dto.getId(), os, userIdentity, queryType, subDistributorId));
				osList.add(osDTO);
			}
			opDTO.setOrderList(osList);
			opList.add(opDTO);
		}
		return opList;
	}

	@Override
	public int updateSubStatus(OrderSub orderSub) {
		return orderSubMapper.updateSubStatus(orderSub);
	}


	@Override
	public PageVO<OrderSubListDTO> myOrderSubList(OrderConditionDTO orderConditionDTO, Integer pageNum, Integer pageSize) {
		//如果带时间查询。先转换
		if (orderConditionDTO.getTimeType() != null) {
			this.convertSearchTime(orderConditionDTO);
		}
		PageHelper.startPage(pageNum, pageSize);
		// todo 查询复杂,下版本再改
		Page<OrderSubListDTO> orderSubList = orderSubMapper.myOrderSubList(orderConditionDTO);
		this.findOrderSubList(orderSubList.getResult());
		return new PageVO<>(pageNum, orderSubList);
	}


	@Override
	public PageVO<OrderSubListDTO> myCustomerOrderList(OrderConditionDTO orderConditionDTO, Integer pageNum, Integer pageSize) {
		//如果带时间查询。先转换
		if (orderConditionDTO.getTimeType() != null) {
			this.convertSearchTime(orderConditionDTO);
		}

		UserDTO userDTO = userFeign.getUserById(orderConditionDTO.getUserId());
		if (Objects.isNull(userDTO)) {
			log.error("我的客户订单，获取不到当前用户，E家号" + orderConditionDTO.getUserId());
			throw new NotFoundException("我的客户订单,获取不到当前用户!，E家号:" + orderConditionDTO.getUserId());
		}
		//不同的类型的，展示的数据不一样
//        if (orderConditionDTO.getProductType() == null) {
//            //默认租赁
//            orderConditionDTO.setProductType(1);
//        }
		//获取用户身份 1-经销商身份 2-用户身份【默认是用户身份】
		orderConditionDTO.setUserIdentity(2);
		List<Integer> distributorList = new ArrayList<>();
		if (UserType.isDistributor(userDTO.getUserType())) {
			orderConditionDTO.setUserIdentity(1);
			// distributorList.add(userDTO.getMid());

			//如果是企业版
			if (userDTO.getUserType() == UserType.DISTRIBUTOR_950.value) {
				if (orderConditionDTO.getQueryType() == 0) {
					distributorList.add(userDTO.getMid());
					//公众号要查询全部客户订单  || app是根据传的用户ID，去查询
					if (Objects.nonNull(orderConditionDTO.getSource()) && Objects.equals(orderConditionDTO.getSource(), 1)) {
						List<DistributorDTO> sonDistributorList = userFeign.getSonDistributorByMid(userDTO.getMid());
						if (CollectionUtil.isNotEmpty(sonDistributorList)) {
							for (DistributorDTO dto : sonDistributorList) {
								distributorList.add(dto.getId());
							}
						}
					}
				} else if (orderConditionDTO.getQueryType() == 2) {
					distributorList.add(orderConditionDTO.getSubDistributorId());
				} else {
					distributorList.add(userDTO.getMid());
				}
			} else {
				distributorList.add(userDTO.getMid());
			}
		}
		orderConditionDTO.setDistributorIds(distributorList);

		PageHelper.startPage(pageNum, pageSize);
		Page<OrderSubListDTO> orderSubList = orderSubMapper.myCustomerOrderSubList(orderConditionDTO);
		this.findOrderSubList(orderSubList.getResult());
		return new PageVO<>(pageNum, orderSubList);
	}

	//设置，其他信息
	private void findOrderSubList(List<OrderSubListDTO> orderSubList) {
		if (CollectionUtil.isNotEmpty(orderSubList)) {
			for (OrderSubListDTO orderSubListDTO : orderSubList) {
				if (CollectionUtil.isNotEmpty(orderSubListDTO.getOrderSubList())) {
					for (OrderSubDTO orderSubDTO : orderSubListDTO.getOrderSubList()) {
						SubOrderDetail subOrderDetail = subOrderDetailMapper.selectByPrimaryKey(orderSubDTO.getId());
						if (Objects.nonNull(subOrderDetail)) {
							subOrderDetail.convert(orderSubDTO);
						}
						if (orderSubDTO.getProductId() != null) {
							ProductDTO productDTO = productFeign.getProductById(orderSubDTO.getProductId());
							if (productDTO != null) {
								orderSubDTO.setSupplyCode(productDTO.getSupplyCode());
							}
						}
						//获取产品价格：根据不同的计费方式
						if (Objects.equals(orderSubDTO.getProductType(), ProductModeEnum.LEASE.value) && orderSubDTO.getCostId() != null) {
							ProductCostDTO productCost = productFeign.productCostGetById(orderSubDTO.getCostId());
							if (Objects.nonNull(productCost)) {
								orderSubDTO.setProductPrice(productCost.getTotalFee());
							}
						}
						//显示配送信息
						if (Objects.equals(orderSubDTO.getProductType(), ProductModeEnum.REALTHING.value)) {
							this.getOrderDelivery(orderSubDTO);
						}
						//状态转换【有些状态，前端不好判断】
						this.commonDetailStatusVerify(orderSubDTO);
					}
				}
			}
		}
	}

	/**
	 * 查询时间转换
	 */
	private void convertSearchTime(OrderConditionDTO orderConditionDTO) {
		Date currentBeginTime = DateUtil.getCurrentDayBeginTime();    //今天的开始时间
		Date currentEndTime = DateUtil.getDayEndTime(new Date()); //今天的结束时间
		//下单时间类型：1、昨日；2、上周；3、30天内；4、3个月内；5、今年；6、上一年
		switch (orderConditionDTO.getTimeType()) {
			case 1:
				orderConditionDTO.setBeginTime(DateUtil.dayAfter(currentBeginTime, -1));
				orderConditionDTO.setEndTime(DateUtil.dayAfter(currentEndTime, -1));
				break;
			case 2:
				orderConditionDTO.setBeginTime(DateUtil.dayAfter(currentBeginTime, -7));
				orderConditionDTO.setEndTime(currentEndTime);
				break;
			case 3:
				orderConditionDTO.setBeginTime(DateUtil.dayAfter(currentBeginTime, -30));
				orderConditionDTO.setEndTime(currentEndTime);
				break;
			case 4:
				orderConditionDTO.setBeginTime(DateUtil.monthAfter(currentEndTime, -3));
				orderConditionDTO.setEndTime(currentEndTime);
				break;
			case 5:
				orderConditionDTO.setBeginTime(DateUtil.getCurrentYearBeginTime());
				orderConditionDTO.setEndTime(currentEndTime);
				break;
			case 6:
				orderConditionDTO.setBeginTime(DateUtil.yearAfter(DateUtil.getCurrentYearBeginTime(), -1));
				orderConditionDTO.setEndTime(DateUtil.getCurrentYearBeginTime());
				break;
			default:
				break;
		}
	}

	@Override
	public List<OrderSub> timeoutOrder(Date date) {
		return orderSubMapper.timeoutOrder(date);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public void payCallback(PayRecord record) {
		log.info("支付成功之后的后续操作");
		String tradeNo = record.getTradeNo();
		int payType = record.getPayType();
		Date payTime = record.getPayTime();
		Long mainOrderId = Long.parseLong(record.getMainOrderId());
		//更新主订单相关信息
		OrderMain mainOrder = mainOrderService.findById(mainOrderId);
		if (mainOrder == null) {
			log.error("支付回调更新主订单信息失败。mainOrderId={}", mainOrderId);
			return;
		}
		mainOrder.setPay(true);
		mainOrder.setPayType(payType);
		mainOrder.setPayTime(payTime);
		mainOrder.setTradeNo(tradeNo);
		mainOrder.setUpdateTime(new Date());
		mainOrder.setDeleted(false);
		mainOrderService.update(mainOrder);

		//获取子订单列表
		List<OrderSub> orderList = orderSubService.findOrdersByMainOrderId(mainOrderId);
		if (CollectionUtil.isNotEmpty(orderList)) {
			//下单时如果是普通用户，需要设置健康大使，如果不是直接返回用户信息
			UserDTO user = userFeign.changeUserTypeIfMeetTheConditions(mainOrder.getUserId());
			if (user == null) {
				log.error("支付回调获取下单用户信息失败，用户e家号为{}", mainOrder.getUserId());
				throw new YimaoException("获取下单用户信息失败，用户e家号为{}" + mainOrder.getUserId());
			}
			for (OrderSub order : orderList) {
				//1-实物商品；2-电子卡券；3-租赁商品；
				int productMode = order.getProductType();
				Long subOrderId = order.getId();
				if (order.getPay()) {
					continue;
				}
				//支付完成后把订单的可见性改为可见
				order.setDeleted(false);
				//支付方式：1-微信；2-支付宝；3-POS机；4-转账；
				order.setPayType(payType);
				order.setPay(true);
				//支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败
				order.setPayStatus(PayStatus.PAY.value);
				order.setPayTime(payTime);
				order.setTradeNo(tradeNo);
				//设置订单状态
				if (Objects.equals(productMode, ProductModeEnum.VIRTUAL.value)) {
					//虚拟商品支付完成后订单状态即可设置为已完成
					order.setStatus(OrderStatusEnum.SUCCESSFUL_TRADE.value);
					order.setCompleteTime(new Date());
				} else {
					order.setStatus(OrderStatusEnum.TO_BE_DELIVERED.value);
				}
				int r = orderSubMapper.updateByPrimaryKeySelective(order);
				if (r <= 0) {
					log.error("更新订单支付状态失败。");
					throw new YimaoException("更新订单支付状态失败。");
				}
				//评估-电子卡券
				if (Objects.equals(productMode, ProductModeEnum.VIRTUAL.value)) {
					//虚拟商品订单状态设置及后续处理
					virtualOrderProcessor(order);
				} else {
					//实物或租赁商品订单处理
					nonVirtualOrderProcessor(order, user.getOpenid());
				}

				//累加产品购买次数
				productFeign.updateBuyCount(order.getProductId(), order.getCount());

				//收益分配
				log.info("支付完成进行收益分配，订单号：{}", subOrderId);
				productIncomeRecordService.allotSellIncome(subOrderId);
			}
		} else {
			log.error("支付完成获取订单信息失败。");
		}
	}

	/**
	 * 虚拟商品订单状态设置及后续处理
	 *
	 * @param order 订单
	 */
	private void virtualOrderProcessor(OrderSub order) {
		log.info("支付回调{}卡", order.getProductModel());
		if (Objects.equals(order.getProductModel(), HraType.Y.name())) {
			//生成体检卡
			hraFeign.createHraCardAndTicket(order.getId());
		} else if (Objects.equals(order.getProductModel(), HraType.M.name())) {
			hraFeign.doRefer(order.getId(), order.getRefer().trim(), order.getTerminal());
		}
	}

	/**
	 * 实物或租赁商品订单处理
	 *
	 * @param order  订单
	 * @param openid openid
	 */
	private void nonVirtualOrderProcessor(OrderSub order, String openid) {
		//1-实物商品；2-电子卡券；3-租赁商品；
		int productMode = order.getProductType();
		//租赁商品，生成安装工单
		if (Objects.equals(productMode, ProductModeEnum.LEASE.value)) {
			createWorkOrderBySubOrder(order.getId());
		}
		//如果是租赁、实物商品 需要发送短信
		if (Objects.equals(productMode, ProductModeEnum.LEASE.value) || Objects.equals(productMode, ProductModeEnum.REALTHING.value)) {
			// 公众号下单的话，推送微信模板消息
			if (order.getTerminal() == Terminal.WECHAT.value) {
				try {
					//消息推送
					wechatFeign.orderPaySuccessMessage(openid, order.getId());
				} catch (Exception e) {
					log.error("订单下单成功,但向经销商发送消息失败---subOrderId={}", order.getId());
				}
			}
		}
	}

	/**
	 * 支付完成后根据子订单创建工单
	 *
	 * @param subOrderId 子订单号
	 */
	public void createWorkOrderBySubOrder(Long subOrderId) {
		OrderSub subOrder = orderSubMapper.selectByPrimaryKey(subOrderId);
		SubOrderDetail subOrderDetail = subOrderDetailMapper.selectByPrimaryKey(subOrderId);
		if (subOrder == null || subOrderDetail == null) {
			log.error("订单下单成功，创建工单失败，订单或订单详情为空---subOrderId={}", subOrderId);
			return;
		}
		//创建工单
		log.info("创建工单subOrderId={},pay={},payStatus={},payTerminal={}", subOrder.getId(), subOrder.getPay(), subOrder.getPayStatus(), subOrder.getPayTerminal());
		Map<String, Object> map = workOrderService.generateWorkOrderAndSyncaAfterSale(subOrder, subOrderDetail);
		if (map == null || map.size() < 1 || !(boolean) map.get("success")) {
			log.error("支付完成创建工单失败。subOrderId={}", subOrderId);
			throw new YimaoException("支付完成创建工单失败。");
		}

		//创建工单成功回写工单号到子订单
		if ((boolean) map.get("success") && Objects.nonNull(map.get("workorderId"))) {
			//将工单号回写订单中
			String workorderId = (String) map.get("workorderId");
			OrderSub order = new OrderSub();
			order.setId(subOrder.getId());
			order.setRefer(workorderId);
			orderSubMapper.updateByPrimaryKeySelective(order);
		}
	}

	/**
	 * 站务系统-订单-概况
	 *
	 * @param areas
	 * @param engineerIds
	 * @return
	 */
	@Override
	public OrderGeneralSituationVO getOrderGeneralSituation(Set<Integer> areas, List<Integer> engineerIds, List<Integer> distributorIds, Integer type) {
		OrderGeneralSituationVO vo = new OrderGeneralSituationVO();
		if (type == PermissionTypeEnum.ALL.value) {
			//拥有售前+售后权限
			vo = orderSubMapper.getOrderGeneralSituation(areas);
			Integer salesReturnOrderNum = afterSalesOrderMapper.getSalesReturnOrderNum(areas);
			Integer validRenewOrderNum = orderRenewMapper.getSalesReturnOrderNum(engineerIds);
			Integer preValidRenewOrderNum = orderRenewMapper.getSalesReturnOrderNumByDistributorIds(distributorIds);
			vo.setSalesReturnOrderNum(salesReturnOrderNum);
			vo.setValidRenewOrderNum(validRenewOrderNum);
			vo.setPreValidRenewOrderNum(preValidRenewOrderNum);
		} else if (type == PermissionTypeEnum.PRE_SALE.value) {
			//仅售前
			vo = orderSubMapper.getOrderGeneralSituation(areas);
			Integer preValidRenewOrderNum = orderRenewMapper.getSalesReturnOrderNumByDistributorIds(distributorIds);
			Integer salesReturnOrderNum = afterSalesOrderMapper.getSalesReturnOrderNum(areas);
			vo.setPreValidRenewOrderNum(preValidRenewOrderNum);
			vo.setSalesReturnOrderNum(salesReturnOrderNum);
		} else if (type == PermissionTypeEnum.AFTER_SALE.value) {
			//仅售后
			Integer validRenewOrderNum = orderRenewMapper.getSalesReturnOrderNum(engineerIds);
			vo.setValidRenewOrderNum(validRenewOrderNum);
		}
		return vo;
	}

	/**
	 * 下架商品时需要将所有【待付款】的订单设置为【已取消】
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public void cancelOrderWhenOffshelf(Integer productId) {
		//获取待付款订单
		List<OrderSub> list = orderSubMapper.listForCancelWhenOffshelf(productId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (OrderSub order : list) {
				Integer productType = order.getProductType();
				//租赁产品，未付款取消订单，归还经销商水机配额
				if (productType == ProductModeEnum.LEASE.value) {
					//获取订单上面的经销商信息
					if (Objects.nonNull(order.getDistributorId())) {
						DistributorDTO distributor = userFeign.getDistributorById(order.getDistributorId());
						if (Objects.nonNull(distributor)) {
							//还配额
							Example example = new Example(QuotaChangeRecord.class);
							Example.Criteria criteria = example.createCriteria();
							criteria.andEqualTo("type", 1);
							criteria.andEqualTo("orderId", order.getId());
							QuotaChangeRecord quotaChangeRecord = quotaChangeRecordMapper.selectOneByExample(example);
							if (quotaChangeRecord != null) {
								if (quotaChangeRecord.getCount() != 0) {
									quotaChangeRecordService.quotaChange(order.getId(), order.getDistributorId(), "未付款取消订单-配额", 2, 1, null);
								} else {
									quotaChangeRecordService.quotaChange(order.getId(), order.getDistributorId(), "未付款取消订单-金额", 2, 0, order.getFee());
								}
							}
						} else {
							log.error("下架商品时需要将所有【待付款】的订单设置为【已取消】失败，修改配额失败。请联系管理员,订单ID：" + order.getId());
						}
					}
				}
				orderSubMapper.cancelWhenOffshelf(order.getId());

				//活动商品还配额
				if (order.getActivityType() != null && order.getActivityType() == ProductActivityType.PANIC_BUYING.value) {
					productFeign.addProductActivityStock(order.getActivityId(), order.getCount());
				}
			}
		}
	}

	@Override
	public ProductSalesStatusAndTwoCategoryPicResVO getProductSalesStatusAndTwoCategoryPicRes(StatisticsQuery query, String categoryName) {
		query.setCategoryName(categoryName);
		//商品销售情况
		List<ProductSalesStatusDTO> productSalesStatus = orderSubMapper.getProductSalesStatus(query);
		List<ProductTwoCategoryPicResDTO> productTwoCategoryPicRes = orderSubMapper.getProductTwoCategoryPicRes(query);
		ProductSalesStatusAndTwoCategoryPicResVO vo = new ProductSalesStatusAndTwoCategoryPicResVO();
		vo.setProductSalesStatus(productSalesStatus);
		vo.setProductTwoCategoryPicRes(productTwoCategoryPicRes);
		return vo;
	}

	@Override
	public List<ProductTabulateDataDTO> getProductTabulateData(StatisticsQuery query) {
		//汇总数据
		List<ProductTabulateDataDTO> productTabulateData = orderSubMapper.getProductTabulateData(query);
		if (CollectionUtil.isEmpty(productTabulateData)) {
			return null;
		}
		//总销售额（初始化）
		BigDecimal totalSale = new BigDecimal(0);
		//总销售量 （初始化）
		Integer totalSalesQuantity = 0;
		for (ProductTabulateDataDTO data : productTabulateData) {
			totalSalesQuantity += data.getSalesQuantity();
			totalSale = data.getSale().add(totalSale);
		}
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(2); //保留到小数点后2位
		for (ProductTabulateDataDTO data : productTabulateData) {
			if (totalSalesQuantity == 0) {
				data.setSalesProportion("总数为0，无法统计");
			} else {
				data.setSalesProportion(nf.format((data.getSalesQuantity() * 1.0) / totalSalesQuantity));
			}
			if (totalSale.longValue() == 0) {
				data.setSaleProportion("总销售额为0，无法统计");
			} else {
				data.setSaleProportion(nf.format((data.getSale().intValue() * 1.0) / (totalSale.longValue())));
			}
		}
		return productTabulateData;
	}

	@Override
	public FlowStatisticsDTO getProductAndHraSaleData(StatisticsQuery query) {

		if (CollectionUtil.isEmpty(query.getAreas())) {
			return null;
		}
		//生物科技销量-上海翼猫生物科技有限公司30000 , 养未来销量-上海养未来健康食品有限公司20000,净水服务销量-翼猫科技发展（上海）有限公司10000，按区域
		List<FlowStatisticsDTO> productRes = orderSubMapper.getProductSaleData(query);

		//hra销量查询（1.hra优惠卡  2.hra体检卡），按区域
		List<FlowStatisticsDTO> hraRes = orderSubMapper.getHraSaleData(query);

		//产品统计图数据(按日期)
		List<FlowStatisticsDTO> productPicRes = orderSubMapper.getProductSalePicData(query);
		//hra统计图数据(按日期)
		List<FlowStatisticsDTO> hraPicRes = orderSubMapper.getHraSalePicData(query);
		//全部汇总产品总数与hra总数统计图数据(按日期);
		List<FlowStatisticsDTO> totalProductAndHraPicRes = orderSubMapper.getTotalProductAndHraPicData(query);

		FlowStatisticsDTO res = new FlowStatisticsDTO();
		res.setProductRes(productRes);
		res.setHraRes(hraRes);
		res.setProductPicRes(productPicRes);
		res.setHraPicRes(hraPicRes);
		res.setTotalProductAndHraPicRes(totalProductAndHraPicRes);

		return res;
	}

	@Override
	public SalesStatsResultDTO getProductSaleStats(SalesStatsQueryDTO query) {
		SalesStatsResultDTO rs = new SalesStatsResultDTO();

		//需要统计的产品
		List<Integer> productIds = productFeign.getProductIdsBySupplyCode(ProductSupplyCode.PJXCP.code);
		query.setProductIds(productIds);

		//产品销售额数据,根据日期统计经销商发展的经销商产品销售额
		query.setDay(6);
		List<SalesStatsDTO> prodSalesList = orderSubMapper.getProdSalesData(query);

		//补全数据
		completeData(prodSalesList, query, 7, 12);

		//按日期统计交易成功订单数
		List<SalesStatsDTO> tradeOrderList = orderSubMapper.getTradeOrderData(query);

		//组装各分类的日期
		completeTradeData(tradeOrderList, query, 6);

		//水机型号占比数据1601T、1602T、1603T、1601L
		List<SalesStatsDTO> waterModelPropList = orderSubMapper.getwaterModelPropData(query);

		rs.setProdSalesList(prodSalesList);
		rs.setTradeOrderList(tradeOrderList);
		rs.setWaterModelPropList(completeModelPropData(waterModelPropList));
		return rs;
	}

	/***
	 * 补全数据
	 * @param tradeOrderList
	 * @param query
	 * @param i
	 */
	private void completeTradeData(List<SalesStatsDTO> tradeOrderList, SalesStatsQueryDTO query, int mon) {
		List<SalesStatsDTO> initData = new ArrayList<SalesStatsDTO>();
		if (query.getTimeType() == 1) {
			//按日统计
			for (int i = 0; i < 7; i++) {
				String completeTime = DateUtil.getChangeDayByDate(DateUtil.transferStringToDate(query.getCompleteTime()), -i);
				initSalesStatsDate(completeTime, initData);
			}
		} else if (query.getTimeType() == 2) {
			//按月统计
			String time = query.getCompleteTime() + "-01";
			for (int i = 0; i < mon; i++) {
				String completeTime = DateUtil.getChangeMonthByDate(DateUtil.transferStringToDate(time), -i);
				initSalesStatsDate(completeTime, initData);
			}
		}

		if (query.getTimeType() == 1 || query.getTimeType() == 2) {
			if (!tradeOrderList.isEmpty()) {
				for (SalesStatsDTO ssd : initData) {
					for (SalesStatsDTO rs : tradeOrderList) {
						if (rs.getCompleteTime().equals(ssd.getCompleteTime()) && rs.getFirstCategoryName().equals(ssd.getFirstCategoryName())) {
							//检测到需要赋值数据
							ssd.setIncreaseNum(rs.getIncreaseNum());
						}
					}

				}
			}
			tradeOrderList.clear();
			tradeOrderList.addAll(initData);
			//数据排序
			Collections.sort(tradeOrderList, new Comparator<SalesStatsDTO>() {
				@Override
				public int compare(SalesStatsDTO u1, SalesStatsDTO u2) {
					long diff = DateUtil.transferStringToDate(query.getTimeType() == 2 ? u1.getCompleteTime() + "-01" : u1.getCompleteTime()).getTime() - DateUtil.transferStringToDate(query.getTimeType() == 2 ? u2.getCompleteTime() + "-01" : u2.getCompleteTime()).getTime();
					if (diff > 0) {
						return 1;
					} else if (diff < 0) {
						return -1;
					} else {
						return 0;
					}
				}
			});

		}


	}

	/***
	 * 初始化数据
	 * @param completeTime
	 * @param initData
	 */
	private void initSalesStatsDate(String completeTime, List<SalesStatsDTO> initData) {
		SalesStatsDTO ssd = new SalesStatsDTO();
		ssd.setCompleteTime(completeTime);
		ssd.setIncreaseNum(0);
		ssd.setFirstCategoryName("净水服务");
		initData.add(ssd);

		ssd = new SalesStatsDTO();
		ssd.setCompleteTime(completeTime);
		ssd.setIncreaseNum(0);
		ssd.setFirstCategoryName("健康食品");
		initData.add(ssd);

		ssd = new SalesStatsDTO();
		ssd.setCompleteTime(completeTime);
		ssd.setIncreaseNum(0);
		ssd.setFirstCategoryName("生物科技");
		initData.add(ssd);

		ssd = new SalesStatsDTO();
		ssd.setCompleteTime(completeTime);
		ssd.setIncreaseNum(0);
		ssd.setFirstCategoryName("健康评估");
		initData.add(ssd);

	}

	/**
	 * 补全数据
	 * @param prodSalesList
	 * @param query
	 */
	private void completeData(List<SalesStatsDTO> prodSalesList, SalesStatsQueryDTO query, int day, int mon) {
		calculateDate(query, day, mon);
		if (query.getTimeType() == 1 || query.getTimeType() == 2) {
			for (String date : query.getDates()) {
				if (notExists(date, prodSalesList, query.getType())) {
					SalesStatsDTO statsRs = new SalesStatsDTO();
					statsRs.setCompleteTime(date);
					statsRs.setSaleAmount(BigDecimal.ZERO.setScale(2));
					prodSalesList.add(statsRs);
				}
			}

			//数据排序
			Collections.sort(prodSalesList, new Comparator<SalesStatsDTO>() {
				@Override
				public int compare(SalesStatsDTO u1, SalesStatsDTO u2) {
					long diff = DateUtil.transferStringToDate(query.getTimeType() == 2 ? u1.getCompleteTime() + "-01" : u1.getCompleteTime()).getTime() - DateUtil.transferStringToDate(query.getTimeType() == 2 ? u2.getCompleteTime() + "-01" : u2.getCompleteTime()).getTime();
					if (diff > 0) {
						return 1;
					} else if (diff < 0) {
						return -1;
					} else {
						return 0;
					}
				}
			});

		}

	}

	/***
	 * 校验数据是否存在
	 * @param date
	 * @param data
	 * @return
	 */
	private boolean notExists(String date, List<SalesStatsDTO> data, Integer type) {
		if (data.isEmpty()) {
			return true;
		}
		boolean flag = true;
		for (SalesStatsDTO stats : data) {
			if (!StringUtil.isEmpty(stats.getCompleteTime()) && stats.getCompleteTime().equals(date)) {
				return false;
			} else {
				flag = true;
			}
		}
		return flag;
	}

	/***
	 * 计算日期集合
	 * @param query
	 */
	private void calculateDate(SalesStatsQueryDTO query, int day, int mon) {
		List<String> dates = new ArrayList<String>();
		dates.add(query.getCompleteTime());
		if (query.getTimeType() == 1) {
			//按日统计
			for (int i = 0; i < day; i++) {
				dates.add(DateUtil.getChangeDayByDate(DateUtil.transferStringToDate(query.getCompleteTime()), -i));
			}
		} else if (query.getTimeType() == 2) {
			//按月统计
			String time = query.getCompleteTime() + "-01";
			for (int i = 0; i < mon; i++) {
				dates.add(DateUtil.getChangeMonthByDate(DateUtil.transferStringToDate(time), -i));
			}
		}
		query.setDates(dates);

	}


	@Override
	public List<SalesStatsDTO> getProductSaleAmountStats(SalesStatsQueryDTO query) {
		//需要统计的产品
		List<Integer> productIds = productFeign.getProductIdsBySupplyCode(ProductSupplyCode.PJXCP.code);
		query.setProductIds(productIds);
		query.setDay(6);
		List<SalesStatsDTO> result = orderSubMapper.getProdSalesData(query);
		completeData(result, query, 7, 12);
		return result;
	}

	@Override
	public List<SalesStatsDTO> getTradeSucOrderStats(SalesStatsQueryDTO query) {
		//需要统计的产品
		List<Integer> productIds = productFeign.getProductIdsBySupplyCode(ProductSupplyCode.PJXCP.code);
		query.setProductIds(productIds);
		query.setDay(6);
		List<SalesStatsDTO> result = orderSubMapper.getTradeOrderData(query);
		completeTradeData(result, query, 6);
		return result;
	}

	@Override
	public AgentSalesOverviewDTO getOrderSalesHomeReport(SalesStatsQueryDTO query) {
		//需要统计的产品
		List<Integer> productIds = productFeign.getProductIdsBySupplyCode(ProductSupplyCode.PJXCP.code);
		query.setProductIds(productIds);

		//销售报表-上海翼猫生物科技有限公司30000 , 养未来销量-上海养未来健康食品有限公司20000,净水服务销量-翼猫科技发展（上海）有限公司10000，按区域
		List<FlowStatisticsDTO> saleData = orderSubMapper.getOrderSalesHomeReport(query);
		AgentSalesOverviewDTO fsTemp = new AgentSalesOverviewDTO();
		//翼猫科技销售额
		BigDecimal jsfwSaleFee = new BigDecimal(0);
		//养未来销售额
		BigDecimal ywlSaleFee = new BigDecimal(0);
		//生物科技销售额
		BigDecimal swkjSaleFee = new BigDecimal(0);
		//产品总销售额
		BigDecimal hraTotalSaleFee = new BigDecimal(0);
		BigDecimal totalSaleFee = new BigDecimal(0);
		//汇总
		for (FlowStatisticsDTO fs : saleData) {
			jsfwSaleFee = jsfwSaleFee.add(fs.getJsfwSaleFee());
			ywlSaleFee = ywlSaleFee.add(fs.getYwlSaleFee());
			swkjSaleFee = swkjSaleFee.add(fs.getSwkjSaleFee());
			hraTotalSaleFee = hraTotalSaleFee.add(fs.getHraTotalSaleFee());
			totalSaleFee = totalSaleFee.add(fs.getJsfwSaleFee()).add(fs.getYwlSaleFee()).add(fs.getSwkjSaleFee()).add(fs.getHraTotalSaleFee());
		}


		fsTemp.setJsfwSaleFee(jsfwSaleFee);
		fsTemp.setJkpgSaleFee(hraTotalSaleFee);
		fsTemp.setSwkjSaleFee(swkjSaleFee);
		fsTemp.setJkspSaleFee(ywlSaleFee);
		fsTemp.setProductTotalSaleFee(totalSaleFee);
		return fsTemp;
	}

	@Override
	public List<SalesStatsDTO> getOrderSalesTotalReport(SalesStatsQueryDTO query) {
		//需要统计的产品
		List<Integer> productIds = productFeign.getProductIdsBySupplyCode(ProductSupplyCode.PJXCP.code);
		query.setProductIds(productIds);
		query.setDay(29);
		List<SalesStatsDTO> result = orderSubMapper.getProdSalesData(query);
		completeData(result, query, 30, 12);
		return result;
	}


	/***
	 * 补全水机设备型号各类型占比(1601T、1602T、1603T、1601L)
	 * @param waterModelPropList
	 * @return
	 */
	private List<SalesStatsDTO> completeModelPropData(List<SalesStatsDTO> waterModelPropList) {
		List<SalesStatsDTO> initData = new ArrayList<SalesStatsDTO>();
		SalesStatsDTO ssd = new SalesStatsDTO();
		ssd.setCategoryName("1601T");
		ssd.setCategoryNum(0);
		initData.add(ssd);

		ssd = new SalesStatsDTO();
		ssd.setCategoryName("1602T");
		ssd.setCategoryNum(0);
		initData.add(ssd);

		ssd = new SalesStatsDTO();
		ssd.setCategoryName("1603T");
		ssd.setCategoryNum(0);
		initData.add(ssd);

		ssd = new SalesStatsDTO();
		ssd.setCategoryName("1601L");
		ssd.setCategoryNum(0);
		initData.add(ssd);

		if (!waterModelPropList.isEmpty()) {
			for (SalesStatsDTO ss : initData) {
				for (SalesStatsDTO rs : waterModelPropList) {
					if (ss.getCategoryName().equals(rs.getCategoryName())) {
						ss.setCategoryNum(rs.getCategoryNum());
						break;
					}
				}
			}
		}
		return initData;
	}

	@Override
	public void updateOrderStatusForEngineer(WorkOrderReqDTO req, Long subOrderId) {
		try {
			if (null == subOrderId) {
				throw new BadRequestException("请选择要取消的订单");
			}

			Date now = new Date();
			// 根据订单号查询订单
			OrderSub orders = orderSubMapper.selectByPrimaryKey(subOrderId);
			if (orders == null) {
				log.error("==========取消失败:订单信息不存在,子订单号:" + subOrderId);
				throw new YimaoException("取消失败:订单信息不存在");
			}
			Integer status = orders.getStatus();
			// 处于待收货状态才可以取消
			if (status == null || status != OrderStatusEnum.PENDING_RECEIPT.value) {
				log.error("======退单失败,该订单不是待收货状态,子订单号:" + subOrderId + ",订单状态:" + status);
				throw new YimaoException("取消失败，订单状态不符合取消条件,请检查");
			}

			// 获取工单信息
			WorkOrder workOrder = workOrderMapper.findWorkOrderByOrderId(orders.getId());

			orders.setStatus(OrderStatusEnum.AFTER_SALE.value);
			orders.setSubStatus(OrderSubStatusEnum.PENDING_AUDIT_DISTRIBUTOR.value);

			// 未支付
			/*
			 * if (status == OrderStatusEnum.PENDING_PAYMENT.value) { if (null ==
			 * orders.getDistributorId()) { log.error("取消订单，修改配额失败。请联系管理员,订单ID：" +
			 * subOrderId); throw new YimaoException("取消失败，订单状态不符合取消条件"); }
			 *
			 * // 获取订单上面的经销商信息 DistributorDTO distributorDTO =
			 * userFeign.getDistributorById(orders.getDistributorId()); if
			 * (Objects.nonNull(distributorDTO)) { // 还配额 Example example = new
			 * Example(QuotaChangeRecord.class); Example.Criteria criteria =
			 * example.createCriteria(); criteria.andEqualTo("type", 1);
			 * criteria.andEqualTo("orderId", orders.getId()); QuotaChangeRecord
			 * quotaChangeRecord = quotaChangeRecordMapper.selectOneByExample(example); if
			 * (quotaChangeRecord != null) { if (quotaChangeRecord.getCount() != 0) {
			 * quotaChangeRecordService.quotaChange(orders.getId(),
			 * orders.getDistributorId(),"未付款取消订单-配额", 2, 1, null); } else {
			 * quotaChangeRecordService.quotaChange(orders.getId(),
			 * orders.getDistributorId(),"未付款取消订单-金额", 2, 0, orders.getFee()); } } }
			 *
			 * }
			 */

			orders.setCancelReason(req.getChargeBcakReason());
			orders.setCancelTime(now);

			orders.setUpdateTime(now);
			int result = orderSubMapper.updateByPrimaryKey(orders);
			if (result < 1) {
				throw new YimaoException("取消失败,系统异常");
			}

			log.info("变更前的订单状态：=》" + orders.getStatus());
			// 添加订单状态变更记录
			OrderStatusRecord orderStatusRecord = new OrderStatusRecord();
			orderStatusRecord.setId(subOrderId);
			orderStatusRecord.setCreateTime(now);
			orderStatusRecord.setCreator(workOrder.getEngineerName());// 安装工退单,取安装工的姓名
			// 变更之后的状态
			orderStatusRecord.setDestStatus(orders.getStatus());
			// 变更之前的状态
			orderStatusRecord.setOrigStatus(status);
			orderStatusRecord.setRemark("取消订单");

			// 保存订单状态变更记录
			orderStatusRecordMapper.insertSelective(orderStatusRecord);

			if (status != OrderStatusEnum.PENDING_PAYMENT.value) {
				// 添加售后订单
				OrderSubDTO dto = new OrderSubDTO();
				dto.setCancelReason(req.getChargeBcakReason());
				this.addAfterSalesOrder(orders, dto);
			}

		} catch (Exception e) {
			logger.error("取下订单失败[更新订单失败,子订单号:" + subOrderId + "]", e);
			throw new YimaoException("取消订单失败！");
		}

	}


}
