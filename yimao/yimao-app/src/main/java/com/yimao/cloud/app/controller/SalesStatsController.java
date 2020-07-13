package com.yimao.cloud.app.controller;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.app.feign.OrderFeign;
import com.yimao.cloud.app.feign.ProductFeign;
import com.yimao.cloud.app.feign.SystemFeign;
import com.yimao.cloud.app.feign.UserFeign;
import com.yimao.cloud.app.feign.WaterFeign;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.AgentLevel;
import com.yimao.cloud.base.enums.OrderSalePerformTypeEnum;
import com.yimao.cloud.base.enums.StationAreaServiceTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.JsonUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.order.AgentSalesOverviewDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsQueryDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsResultDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyServiceAreaDTO;
import com.yimao.cloud.pojo.dto.user.SalePerformRankDTO;
import com.yimao.cloud.pojo.dto.user.SalePerformRankQuery;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/****
 * 经销报表-招商销售统计、产品销售统计、续费销售统计
 * 
 * @author zhangbaobao
 * @date 2020-4-24
 */
@RestController
@Slf4j
@Api(tags = "SalesStatsController")
public class SalesStatsController {

	@Resource
	private UserFeign userFeign;

	@Resource
	private OrderFeign orderFeign;

	@Resource
	private UserCache userCache;

	@Resource
	private SystemFeign systemFeign;
	
	@Resource
	private WaterFeign waterFeign;

	@Resource
	private ProductFeign productFeign;

	/****
	 * 获取招商销售额主状数据、各类经销商增长趋势数据、各类型经销商占比
	 * 
	 * @return
	 */
	@PostMapping(value = "/distributor/investment/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "经营报表-招商累计销售额-查看更多", notes = "经营报表-招商累计销售额-查看更多")
	@ApiImplicitParam(name = "query", value = "经营报表-招商累计销售额-查看更多", required = true, dataType = "SalesStatsQueryDTO", paramType = "body")
	public Object getInvestmentStatsInfo(@RequestBody SalesStatsQueryDTO query) {
		log.info("===============distributorSalesStatsInfoReq=======" + JSON.toJSONString(query));
		// 判断当前用户是否是区代经销商
		UserDTO user =getUserInfo();
		if (!checkUser(user)) {
			throw new BadRequestException("请求参数错误");
		}
		
		//初始化请求对象
		initQueryData(user,query);
		
		// 招商销售额柱状图,各类经销商增长趋势图,各类经销商占比图
		SalesStatsResultDTO result = userFeign.getInvestmentStats(query);
		
		//如果type是公司,则要获取排行榜数据
		if(query.getType()==2) {
			SalePerformRankQuery sprQ=initRankQueryParam(query, OrderSalePerformTypeEnum.INVEST_SALEPERFORM.value);
			List<SalePerformRankDTO> salePerformRankList=orderFeign.getSalePerformRankData(sprQ);
			result.setSalePerformRankList(salePerformRankList);
		}
		log.info("===============distributorSalesStatsInforesp=======" + JSON.toJSONString(result));
		return ResponseEntity.ok(result);

	}
	
	/***
	 * 获取当前登录用户
	 * @param query 
	 * @return
	 */
	private UserDTO getUserInfo() {
		Integer userId = userCache.getUserId();
		UserDTO user=userFeign.getUserDistributorInfoById(userId);
		return user;
	}

	/***
	 *初始化请求对象
	 * @param user
	 * @param query
	 */
	private void initQueryData(UserDTO user, SalesStatsQueryDTO query) {
		query.setDistributorId(user.getMid());
		if (StringUtil.isEmpty(query.getCompleteTime()) && query.getTimeType() != null) {
			if (query.getTimeType() == 1) {
				query.setCompleteTime(DateUtil.transferDateToString(new Date()));
			} else if (query.getTimeType() == 2) {
				query.setCompleteTime(new SimpleDateFormat("yyyy-MM").format(new Date()));
			}else if(query.getTimeType() == 3) {
				query.setCompleteTime(new SimpleDateFormat("yyyy").format(new Date()));
			}

		}
		
		//设置公司id和发展经销商信息数据
		setQueryParams(query,user);
		
	}
	
	/***
	 * 填充数据
	 * @param query
	 * @param user 
	 */
	private void setQueryParams(SalesStatsQueryDTO query, UserDTO user) {
		//判断是查询个人还是公司销售统计
		List<Integer> distributorIds=null;
		if(query.getType()==1) {
			//个人
			distributorIds=userFeign.getDistributorList(query.getDistributorId());
			if(distributorIds.isEmpty()) {
				log.error("===========发展经销商未找到==========DistributorId:"+query.getDistributorId());
				throw new YimaoException("发展经销商未找到");
			}
			query.setIds(distributorIds);
		}else if(query.getType()==2) {
			StationCompanyDTO company=systemFeign.getStationCompanyByPCR(user.getProvince(), user.getCity(), user.getRegion(),StationAreaServiceTypeEnum.PRE_SALE.value);
			if(Objects.isNull(company)){
                log.error("===========经销商公司信息未找到==========DistributorId:"+query.getDistributorId());
                throw new YimaoException("经销商公司信息未找到");
            }
			query.setStationCompanyId(company.getId());

			StationCompanyDTO stationCompanyAreas=systemFeign.getStationCompanyById(company.getId());
			if(Objects.isNull(stationCompanyAreas)||CollectionUtil.isEmpty(stationCompanyAreas.getServiceAreas())) {
				log.error("===========服务站门店和服务站服务区域未找到==========");
				throw new YimaoException("服务站门店和服务站服务区域未找到");
			}
			query.setAreaIds(getAreaIds(stationCompanyAreas.getServiceAreas()));
		}
		
	}
	
	/***
	 * 转换
	 * @param serviceAreas
	 * @return
	 */
	private List<Integer> getAreaIds(List<StationCompanyServiceAreaDTO> serviceAreas) {
		List<Integer> areaIdsList=new ArrayList<Integer>();
		for(StationCompanyServiceAreaDTO scsa:serviceAreas) {
			areaIdsList.add(scsa.getAreaId());
		}
		return areaIdsList;
	}

	/****
	 * 根据日期(年、月、日)统计招商销售额数据
	 * 
	 * @return
	 */
	@PostMapping(value = "/distributor/investment/sales/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "经营报表-招商销售额-查看更多-销售额柱状图", notes = "经营报表-招商销售额-查看更多-销售额柱状图")
	@ApiImplicitParam(name = "query", value = "经营报表-招商销售额-查看更多-销售额柱状图", required = true, dataType = "SalesStatsQueryDTO", paramType = "body")
	public Object getInvestmentSalesStatsInfo(@RequestBody SalesStatsQueryDTO query) {
		log.info("===============InvestmentSalesStatsInfoReq=======" + JSON.toJSONString(query));
		// 判断当前用户是否是区代经销商
		UserDTO user =getUserInfo();
		if (!checkUser(user)) {
			throw new BadRequestException("请求参数错误");
		}
		
		//初始化请求对象
		initQueryData(user, query);
		
		List<SalesStatsDTO> result = userFeign.getInvestmentSalesStats(query);
		log.info("===============InvestmentSalesStatsInforesp=======" + JSON.toJSONString(result));
		return ResponseEntity.ok(result);

	}

	/****
	 * 根据日期(年、月)统计销商增长趋势图接口
	 * 
	 * @return
	 */
	@PostMapping(value = "/distributor/increase/trend/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "经营报表-招商销售额-查看更多-经销商增长折线图", notes = "经营报表-招商销售额-查看更多-经销商增长折线图")
	@ApiImplicitParam(name = "query", value = "经营报表-招商销售额-查看更多-经销商增长折线图", required = true, dataType = "SalesStatsQueryDTO", paramType = "body")
	public Object getIncreaseTrendStatsInfo(@RequestBody SalesStatsQueryDTO query) {
		log.info("===============IncreaseTrendStatsInfoReq=======" + JSON.toJSONString(query));
		// 判断当前用户是否是区代经销商
		UserDTO user =getUserInfo(); 
		if (!checkUser(user)) { 
			throw new BadRequestException("请求参数错误");
		}
		//初始化请求对象
		initQueryData(user, query);
		
		List<SalesStatsDTO> result = userFeign.getInvestmentIncreaseTrendStats(query);
		log.info("===============IncreaseTrendStatsInforesp=======" + JSON.toJSONString(result));
		return ResponseEntity.ok(result);

	}

	/****
	 * 校验用户是否为区代
	 * 
	 * @param user
	 * @return
	 */
	private Boolean checkUser(UserDTO user) {
		if (null != user && user.getUserType() != null && (user.getAgentLevel() != null && (user.getAgentLevel() == AgentLevel.AGENT_R.value
				||user.getAgentLevel() == AgentLevel.AGENT_PR.value
				||user.getAgentLevel() == AgentLevel.AGENT_CR.value
				||user.getAgentLevel() == AgentLevel.AGENT_PCR.value))) {
			return true;
		}
		log.error("===============当前用户没有权限查看经营报表权限!,user=" + JSON.toJSONString(user));
		return false;
	}
	
	/****************************产品销售统计开始*****************************/
	/****
	 * 经营报表-产品累计销售额-查看更多(默认)
	 * 
	 * @return
	 */
	@PostMapping(value = "/distributor/product/sale/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "经营报表-产品累计销售额-查看更多(默认)", notes = "经营报表-产品累计销售额-查看更多(默认)")
	@ApiImplicitParam(name = "query", value = "经营报表-产品累计销售额-查看更多(默认)", required = true, dataType = "SalesStatsQueryDTO", paramType = "body")
	public Object getProductStatsInfo(@RequestBody SalesStatsQueryDTO query) {
		log.info("===============getRenewStatsInfoReq=======" + JSON.toJSONString(query));
		// 判断当前用户是否是区代经销商
		UserDTO user =getUserInfo();
		if (!checkUser(user)) {
			throw new BadRequestException("请求参数错误");
		}
		
		//初始化请求对象
		initQueryData(user,query);
		
		//产品销售额柱状数据、交易成功订单增长趋势、各型号净水占比
		SalesStatsResultDTO result= orderFeign.getProductSaleStats(query);
		
		//要获取排行榜数据
		SalePerformRankQuery sprQ=initRankQueryParam(query,OrderSalePerformTypeEnum.PRODUCT_SALEPERFORM.value);
		List<SalePerformRankDTO> salePerformRankList=orderFeign.getSalePerformRankData(sprQ);
		result.setSalePerformRankList(salePerformRankList);
		log.info("===============getRenewStatsInforesp=======" + JSON.toJSONString(result));
		return ResponseEntity.ok(result);

	}
	
	/****
	 *经营报表-产品累计销售额-查看更多-产品销售额柱状图
	 * @param query
	 * @return
	 */
	@PostMapping(value = "/distributor/prodSale/amount/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "经营报表-产品累计销售额-查看更多-产品销售额柱状图", notes = "经营报表-产品累计销售额-查看更多-产品销售额柱状图")
	@ApiImplicitParam(name = "query", value = "经营报表-产品累计销售额-查看更多-产品销售额柱状图", required = true, dataType = "SalesStatsQueryDTO", paramType = "body")
   	public Object getProductSaleAmountStats(@RequestBody SalesStatsQueryDTO query){
		// 判断当前用户是否是区代经销商
		UserDTO user =getUserInfo();
		if (!checkUser(user)) {
			throw new BadRequestException("请求参数错误");
		}
		
		//初始化请求对象
		initQueryData(user,query);
		
		return ResponseEntity.ok(orderFeign.getProductSaleAmountStats(query));
    	
    }
    
    /***
     * 经营报表-产品累计销售额-查看更多-交易成功订单增长趋势折线图`
     * @param query
     * @return
     */
    @PostMapping(value = "/distributor/order/trade/suc/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "经营报表-产品累计销售额-查看更多-交易成功订单增长趋势折线图", notes = "经营报表-产品累计销售额-查看更多-交易成功订单增长趋势折线图")
	@ApiImplicitParam(name = "query", value = "经营报表-产品累计销售额-查看更多-交易成功订单增长趋势折线图", required = true, dataType = "SalesStatsQueryDTO", paramType = "body")
   	public Object getTradeSucOrderStats(@RequestBody SalesStatsQueryDTO query){
    	
    	// 判断当前用户是否是区代经销商
		UserDTO user =getUserInfo();
		if (!checkUser(user)) {
			throw new BadRequestException("请求参数错误");
		}
		
		//初始化请求对象
		initQueryData(user,query);
		
		return ResponseEntity.ok(orderFeign.getTradeSucOrderStats(query));
    	
    }
	/****************************产品销售统计结束*****************************/
	
	/****************************续费销售统计开始*****************************/
	
	
	/****
	 * 经营报表(公司)-净水设续费累计-查看更多(获取净水设备金额统计)
	 * 
	 * @return
	 */
	@PostMapping(value = "/distributor/renew/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "经营报表(公司)-净水设续费累计-查看更多(获取净水设备金额统计)", notes = "经营报表(公司)-净水设续费累计-查看更多(获取净水设备金额统计)")
	@ApiImplicitParam(name = "query", value = "经营报表(公司)-净水设续费累计-查看更多(获取净水设备金额统计)请求参数", required = true, dataType = "SalesStatsQueryDTO", paramType = "body")
	public Object getRenewStatsInfo(@RequestBody SalesStatsQueryDTO query) {
		log.info("===============getRenewStatsInfoReq=======" + JSON.toJSONString(query));
		// 判断当前用户是否是区代经销商
		UserDTO user =getUserInfo();
		if (!checkUser(user)||query.getType()==null||query.getType()!=2) {
			throw new BadRequestException("请求参数错误");
		}
		
		//初始化请求对象
		initQueryData(user,query);
		SalesStatsResultDTO result=new SalesStatsResultDTO();
		// 续费金额柱状图和续费率数据统计
		List<SalesStatsDTO> renewSaleList= orderFeign.getRenewSaleStats(query);
		result.setRenewSaleList(renewSaleList);
		
		//根据areaid获取经销商ids,然后根据ids获取净水设备
		if(CollectionUtil.isEmpty(query.getAreaIds())) {
			log.error("=========该经销商所属服务站公司没有绑定areaId========companyid="+query.getStationCompanyId());
			throw new BadRequestException("该经销商所属服务站公司没有绑定areaId");
		}
		
		//获取该公司下的所有经销商信息(只获取个人版、企业版、微创版、体验版)
		List<Integer> distributorIds=userFeign.getDistributorIdsByAreaIdsForApp(query.getAreaIds());
		if(!distributorIds.isEmpty()) {
			//获取设备续费率
			List<SalesStatsDTO> deviceRenewPropList=waterFeign.getDeviceRenewPropList(distributorIds);
			result.setDeviceRenewPropList(deviceRenewPropList);
		}
		
		log.info("===============getRenewStatsInforesp=======" + JSON.toJSONString(result));
		return ResponseEntity.ok(result);
	}

	/****
	 * 经营报表(公司)-净水设续费累计-查看更多-净水设备续费金额柱状图
	 * 
	 * @return
	 */
	@PostMapping(value = "/distributor/renew/amount/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "经营报表(公司)-净水设续费累计-查看更多-净水设备续费金额柱状图", notes = "经营报表(公司)-净水设续费累计-查看更多-净水设备续费金额柱状图")
	@ApiImplicitParam(name = "query", value = "经营报表(公司)-净水设续费累计-查看更多-净水设备续费金额柱状图", required = true, dataType = "SalesStatsQueryDTO", paramType = "body")
	public Object getRenewAmountStatsInfo(@RequestBody SalesStatsQueryDTO query) {
		log.info("===============getRenewAmountStatsInfo=======" + JSON.toJSONString(query));
		// 判断当前用户是否是区代经销商
		UserDTO user =getUserInfo();
		if (!checkUser(user)||query.getType()==null||query.getType()!=2) {
			return ResponseEntity.noContent().build();
		}
		
		//初始化请求对象
		initQueryData(user,query);
		
		// 续费金额柱状图和续费率数据统计
		List<SalesStatsDTO> renewSaleList= orderFeign.getRenewSaleStats(query);
		log.info("===============getRenewAmountStatsInfo=======" + JSON.toJSONString(renewSaleList));
		return ResponseEntity.ok(renewSaleList);

	}
	/****************************续费销售统计结束*****************************/


	/**
	 * @Description 我的代理-我的经销商(区代下的经销商)
	 * @author Liu Yi
	 * @Date 11:59 2020/4/26
	 * @Param
	 **/
	@GetMapping(value = "/distributors/app/agent/homeReport")
	@ApiOperation(value = "区代首页报表", notes = "区代首页报表", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiImplicitParam(name = "type", value = "类型:1 个人,2 公司", required = true, dataType = "Long")
	public Object agentHomeReport(@RequestParam(value = "type", defaultValue = "1") Integer type) {
		// 判断当前用户是否是区代经销商
		UserDTO user = getUserInfo();
		if (!checkUser(user)) {
			return ResponseEntity.noContent().build();
		}

		SalesStatsQueryDTO query = new SalesStatsQueryDTO();
		query.setType(type);
		//初始化请求对象
		initQueryData(user,query);
		//产品累计销售额
		AgentSalesOverviewDTO saleReport = orderFeign.getOrderSalesHomeReport(query);
		//招商累计销售额
		AgentSalesOverviewDTO distributorOrderSale = userFeign.getOrderSalesHomeReport(query);
		BigDecimal investmentTotalSaleFee = distributorOrderSale.getInvestmentTotalSaleFee();
		//招商相关
		saleReport.setInvestmentRegistSaleFee(distributorOrderSale.getInvestmentRegistSaleFee());    //招商注册销售金额
		saleReport.setInvestmentUpgradeSaleFee(distributorOrderSale.getInvestmentUpgradeSaleFee());   //招商升级销售金额
		saleReport.setInvestmentRenewSaleFee(distributorOrderSale.getInvestmentRenewSaleFee());       //招商续费销售金额

 		//总金额
		saleReport.setInvestmentTotalSaleFee(investmentTotalSaleFee);
		//招商累计销售额-分类名称-经销商订单的订单类型
		//List<String> distributorOrderTypeNames = userFeign.queryDistributorOrderTypeNames();
		BigDecimal totalFee = new BigDecimal(0);
		totalFee = saleReport.getProductTotalSaleFee().add(investmentTotalSaleFee);
		AgentSalesOverviewDTO renewOrderSale;
		//List<String> costTypeNames = null;
		if (query.getType() == 2) {
			// 累计销售额
			renewOrderSale = orderFeign.getRenewOrderSalesHomeReport(query);
			//costTypeNames = orderFeign.getRenewOrderTypeNames();
			// 设备续费总金额
			BigDecimal renewTotal =
					renewOrderSale
							.getSybnSaleFee()
							.add(renewOrderSale.getSyllSaleFee())
							.add(renewOrderSale.getJybnSaleFee())
							.add(renewOrderSale.getJyllSaleFee());
			saleReport.setRenewTotalSaleFee(renewTotal);
			//累加续费金额
			totalFee = totalFee.add(renewTotal);
			saleReport.setSyllSaleFee(renewOrderSale.getSyllSaleFee());
			saleReport.setSybnSaleFee(renewOrderSale.getSybnSaleFee());
			saleReport.setJyllSaleFee(renewOrderSale.getJyllSaleFee());
			saleReport.setJybnSaleFee(renewOrderSale.getJybnSaleFee());
		} else {
			saleReport.setRenewTotalSaleFee(new BigDecimal(0));
		}

		saleReport.setSaleTotalFee(totalFee);
		//saleReport.setOrderTypeNames(distributorOrderTypeNames);
		//saleReport.setCostTypeNames(costTypeNames);

		return ResponseEntity.ok(saleReport);
	}

	/**
	 * @Description 我的代理-经营报表-汇总统计
	 * @author Liu Yi
	 * @Date 11:59 2020/5/7
	 * @Param
	 **/
	@PostMapping(value = "/distributor/app/agent/totalStatsInfo")
	@ApiOperation(value = "经营报表-汇总统计", notes = "经营报表-汇总统计")
	@ApiImplicitParam(name = "query", value = "经营报表-汇总统计", required = true, dataType = "SalesStatsQueryDTO", paramType = "body")
	public Object getAgentSalesStats(@RequestBody SalesStatsQueryDTO query) {
		log.info("=======传入参数:" + JsonUtil.objectToJson(query) + "=======");
		// 判断当前用户是否是区代经销商
		UserDTO user =getUserInfo();
		if (!checkUser(user)) {
			throw new BadRequestException("请求参数错误");
		}
		//初始化请求对象
		initQueryData(user, query);
		// 产品销售统计
		List<SalesStatsDTO> productList = orderFeign.getOrderSalesTotalReport(query);
		//招商销售统计
		List<SalesStatsDTO> distributorOrderList = userFeign.getOrderSalesTotalReport(query);
		// 续费销售统计
		List<SalesStatsDTO> renewOrderList = orderFeign.getRenewOrderSalesTotalReport(query);

		List<SalesStatsDTO> list = new ArrayList<>();
		SalesStatsDTO salesStatsDTO;
		if(query.getType() == 1){
			if(CollectionUtil.isNotEmpty(productList) && CollectionUtil.isNotEmpty(distributorOrderList)){
				for (SalesStatsDTO productOrder : productList) {
					for (SalesStatsDTO distributorOrder : distributorOrderList) {
						if (productOrder.getCompleteTime().equals(distributorOrder.getCompleteTime()) ) {
							salesStatsDTO = new SalesStatsDTO();
							salesStatsDTO.setCompleteTime(productOrder.getCompleteTime());
							salesStatsDTO.setSaleAmount(productOrder.getSaleAmount().add(distributorOrder.getSaleAmount()));
							list.add(salesStatsDTO);
						}
					}
				}
			} else {
				if(CollectionUtil.isNotEmpty(productList)){
					for (SalesStatsDTO productOrder : productList) {
						salesStatsDTO = new SalesStatsDTO();
						salesStatsDTO.setCompleteTime(productOrder.getCompleteTime());
						salesStatsDTO.setSaleAmount(productOrder.getSaleAmount());
						list.add(salesStatsDTO);
					}
				}

				if(CollectionUtil.isNotEmpty(distributorOrderList)){
					for (SalesStatsDTO distributorOrder : distributorOrderList) {
						salesStatsDTO = new SalesStatsDTO();
						salesStatsDTO.setCompleteTime(distributorOrder.getCompleteTime());
						salesStatsDTO.setSaleAmount(distributorOrder.getSaleAmount());
						list.add(salesStatsDTO);
					}
				}
			}


		} else if(query.getType() == 2){
			if(CollectionUtil.isNotEmpty(productList) && CollectionUtil.isNotEmpty(distributorOrderList) && CollectionUtil.isNotEmpty(renewOrderList)){
				for (SalesStatsDTO productOrder : productList) {
					for (SalesStatsDTO distributorOrder : distributorOrderList) {
						for (SalesStatsDTO renewOrder : renewOrderList) {
							if (productOrder.getCompleteTime().equals(distributorOrder.getCompleteTime()) && productOrder.getCompleteTime().equals(renewOrder.getCompleteTime())) {
								salesStatsDTO = new SalesStatsDTO();
								salesStatsDTO.setCompleteTime(productOrder.getCompleteTime());
								salesStatsDTO.setSaleAmount(productOrder.getSaleAmount().add(distributorOrder.getSaleAmount()).add(renewOrder.getSaleAmount()));
								list.add(salesStatsDTO);
							}
						}
					}
				}
			} else {

				determine(productList, distributorOrderList, renewOrderList, list);

				determine(distributorOrderList, productList, renewOrderList, list);

				determine(renewOrderList, productList, distributorOrderList, list);

				judge(productList, distributorOrderList, renewOrderList, list);

				judge(productList, renewOrderList, distributorOrderList, list);

				judge(renewOrderList, productList, distributorOrderList, list);

			}



		}
		return ResponseEntity.ok(list);
	}

	private void judge(List<SalesStatsDTO> productList, List<SalesStatsDTO> distributorOrderList, List<SalesStatsDTO> renewOrderList, List<SalesStatsDTO> list) {
		SalesStatsDTO salesStatsDTO;
		if(CollectionUtil.isNotEmpty(renewOrderList) && CollectionUtil.isNotEmpty(productList) && CollectionUtil.isEmpty(distributorOrderList)){
			salesStatsDTO = new SalesStatsDTO();
			salesStatsDTO.setCompleteTime(renewOrderList.get(0).getCompleteTime());
			salesStatsDTO.setSaleAmount(renewOrderList.get(0).getSaleAmount().add(productList.get(0).getSaleAmount()));
			list.add(salesStatsDTO);
		}
	}

	private void determine(List<SalesStatsDTO> productList, List<SalesStatsDTO> distributorOrderList, List<SalesStatsDTO> renewOrderList, List<SalesStatsDTO> list) {
		SalesStatsDTO salesStatsDTO;
		if(CollectionUtil.isNotEmpty(productList) && CollectionUtil.isEmpty(distributorOrderList) && CollectionUtil.isEmpty(renewOrderList)){
			salesStatsDTO = new SalesStatsDTO();
			salesStatsDTO.setCompleteTime(productList.get(0).getCompleteTime());
			salesStatsDTO.setSaleAmount(productList.get(0).getSaleAmount());
			list.add(salesStatsDTO);
		}
	}

	/***
	 * 初始化参数
	 * @param query
	 * @param value
	 * @return
	 */
	private SalePerformRankQuery initRankQueryParam(SalesStatsQueryDTO query, int type) {
		SalePerformRankQuery sprQ=new SalePerformRankQuery();
		sprQ.setType(type);
		sprQ.setQueryType(query.getType());
		String statMonth=DateUtil.getChangeMonthByDate(DateUtil.transferStringToDate(DateUtil.addMonths(new Date(), -1, "yyyy-MM-dd")), 0); 
		sprQ.setStatMonth(statMonth);
		if (query.getType()==1) {
			sprQ.setIds(query.getIds());
		}else if (query.getType()==2) {
			//公司
			sprQ.setStationCompanyId(query.getStationCompanyId());
		}
		return sprQ;
	}

}
