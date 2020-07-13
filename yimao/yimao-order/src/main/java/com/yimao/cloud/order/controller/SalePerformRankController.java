package com.yimao.cloud.order.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.mapper.OrderSubMapper;
import com.yimao.cloud.order.mapper.SalePerformRankMapper;
import com.yimao.cloud.pojo.dto.user.SalePerformRankDTO;
import com.yimao.cloud.pojo.dto.user.SalePerformRankQuery;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/****
 * 销售业绩统计服务
 * 
 * @author zhangbaobao
 * @date 2020-4-27
 */
@RestController
@Api(tags = "SalePerformRankController")
public class SalePerformRankController {

	@Resource
	private SalePerformRankMapper salePerformRankMapper;
	@Resource
	UserFeign userFeign;

	@Resource
	private OrderSubMapper orderSubMapper;

	/***
	 * 根据服务站公司id获取上个月的销售排行-取5条
	 * 
	 * @param query
	 * @return
	 */
	@PostMapping(value = "/order/sale/perform/rank", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "根据服务站公司id获取上个月的销售排行", notes = "根据服务站公司id获取上个月的销售排行")
	@ApiImplicitParam(name = "query", value = "排行榜查询对象", required = true, dataType = "SalePerformRankQuery", paramType = "body")
	List<SalePerformRankDTO> getSalePerformRankData(@RequestBody SalePerformRankQuery query){
		return salePerformRankMapper.getSalePerformRankData(query);

	}

}
