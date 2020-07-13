package com.yimao.cloud.order.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.system.AreaInfoDTO;
import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;
import com.yimao.cloud.pojo.dto.system.MessageTemplateDTO;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.system.StationMaterialStockDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * 站务管理Feign
 *
 * @author liuhao@yimaokeji.com
 * @date 2018-12-28
 */
@FeignClient(name = Constant.MICROSERVICE_SYSTEM)
public interface SystemFeign {

	/**
	 * 查询区县级公司（单个）
	 *
	 * @param id 区县级公司ID
	 */
	@GetMapping(value = "/station/company/{id}")
	StationCompanyDTO getStationCompanyById(@PathVariable(value = "id") Integer id);

	/**
	 * 获取服务站信息
	 *
	 * @param id 服务站ID
	 */
	@GetMapping(value = "/station/{id}")
	StationDTO getStationById(@PathVariable("id") Integer id);

	/**
	 * 是否是上线地区校验
	 *
	 * @param province 省
	 * @param city     市
	 * @param region   区
	 */
	@GetMapping(value = "/online/area/is")
	boolean checkIsOnline(@RequestParam(name = "province") String province,
	                      @RequestParam(name = "city") String city,
	                      @RequestParam(name = "region") String region);

	/**
	 * @Author lizhiqiang
	 * @Date 2019/3/1
	 */
	@GetMapping(value = "/station/information")
	StationDTO getStationByPRC(@RequestParam(name = "province") String province,
	                           @RequestParam(name = "city") String city,
	                           @RequestParam(name = "region") String region,
	                           @RequestParam(name = "type") Integer type);


	/**
	 * 根据区域ID查询区域级联信息
	 *
	 * @param id 区域ID
	 * @return
	 */
	@GetMapping(value = "/area/name/{id}")
	AreaInfoDTO getAreaInfoById(@PathVariable("id") Integer id);

	/**
	 * 根据参数查找模版信息
	 */
	@GetMapping(value = "/messageTemplate")
	MessageTemplateDTO findMessageTemplateByParam(@RequestParam(value = "type") String type,
	                                              @RequestParam(value = "mechanism") String mechanism,
	                                              @RequestParam(value = "pushObject") String pushObject,
	                                              @RequestParam(value = "pushMode") String pushMode);

	/****
	 * 根据省市区、服务类型(1.售前，2.售后)查询服务站公司
	 * @param province
	 * @param city
	 * @param region
	 * @param type
	 * @return
	 */
	@GetMapping(value = "/station/company/information")
	StationCompanyDTO getStationCompanyByPCR(@RequestParam(name = "province") String province,
	                                         @RequestParam(name = "city") String city,
	                                         @RequestParam(name = "region") String region,
	                                         @RequestParam(name = "type") Integer type);

	//根据服务站id查询服务站公司名称
	@RequestMapping(value = {"/station/company/name/{stationId}"}, method = {RequestMethod.GET})
	String getStationCompanyNameById(@PathVariable("stationId") Integer stationId);

	@GetMapping(value = "/station/recommendId")
	StationDTO getStationByDistributorId(@RequestParam(name = "recommendId") Integer recommendId);

	/**
	 * 服务站是否升级新流程校验
	 *
	 * @param province 省
	 * @param city     市
	 * @param region   区
	 */
	@GetMapping(value = "/onlinearea")
	OnlineAreaDTO getOnlineAreaByPCR(@RequestParam(name = "province") String province,
	                                 @RequestParam(name = "city") String city,
	                                 @RequestParam(name = "region") String region);

	/**
	 * 根据服务站门店id查询服务站公司信息
	 *
	 * @param stationId
	 * @return
	 */
	@GetMapping("/station/company/findByStationId")
	List<StationCompanyDTO> getStationCompanyByStationId(@RequestParam("stationId") Integer stationId);

	/**
	 * 根据省市区查询system_area区域id
	 *
	 * @param province
	 * @param city
	 * @param region
	 * @return
	 */
	@GetMapping(value = "/area/getRegionIdByPCR")
	Integer getRegionIdByPCR(@RequestParam(value = "province") String province, @RequestParam(value = "city") String city, @RequestParam(value = "region") String region);

	/**
	 * 维修门店耗材扣减
	 */
	@PostMapping(value = "/store/house/stationMaterialStock/reduce", consumes = MediaType.APPLICATION_JSON_VALUE)
	boolean reduceStationMaterialStock(@RequestBody StationMaterialStockDTO dto);

	/**
	 * 服务站门店预删减某物资库存（可用库存-1，占用库存+1）
	 */
	@PostMapping(value = "/store/house/station/stock/prune")
	Void stockPrune(@RequestParam(value = "productId") Integer productId,
	                @RequestParam(value = "stationId") Integer stationId,
	                @RequestParam(value = "count") Integer count);

	/**
	 * 服务站门店删减某物资占用库存（占用库存-1）
	 */
	@PostMapping(value = "/store/house/station/occupyStock/prune")
	Void occupyStockPrune(@RequestParam(value = "productId") Integer productId,
	                      @RequestParam(value = "stationId") Integer stationId,
	                      @RequestParam(value = "count") Integer count);

	/**
	 * 安装工退单回增库存（占用库存-1，可用库存+1）
	 */
	@PostMapping(value = "/store/house/station/stock/add")
	Void addStock(@RequestParam(value = "productId") Integer productId,
	              @RequestParam(value = "stationId") Integer stationId,
	              @RequestParam(value = "count") Integer count);

	/**
	 * 校验某服务站是否有指定物资的库存，有则返回该物资信息，无则返回null
	 */
	@PostMapping(value = "/store/house/check/{stationId}/{goodsId}")
	GoodsMaterialsDTO checkStationGoodsIsHaveStock(@PathVariable(value = "stationId") Integer stationId,
	                                               @PathVariable(value = "goodsId") Integer goodsId);

	/**
	 * 根据产品三级类目id查询匹配耗材列表
	 *
	 * @param productCategoryId
	 * @return
	 */
	@GetMapping(value = "/goods/material/{productCategoryId}")
	List<GoodsMaterialsDTO> getMaterialListByCategoryId(@PathVariable(value = "productCategoryId") Integer productCategoryId);

	/**
	 * 维护-门店耗材扣减
	 */
	@PostMapping(value = "/store/house/maintenance/stationMaterialStock/reduce")
	boolean reduceStationStockByMaintenance(@RequestBody StationMaterialStockDTO dto);
}
