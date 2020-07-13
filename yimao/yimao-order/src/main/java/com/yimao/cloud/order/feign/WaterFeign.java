package com.yimao.cloud.order.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDevicePlaceChangeRecordDTO;

import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 描述：水机微服务的接口列表
 *
 * @author liu yi
 * @date 2019/3/23.
 */
@FeignClient(name = Constant.MICROSERVICE_WATER)
public interface WaterFeign {

	@RequestMapping(value = "/waterdevice/{id}", method = RequestMethod.GET)
	WaterDeviceDTO getWaterDeviceById(@PathVariable(value = "id") Integer id);

	/**
	 * 根据水机设备SN获取设备信息
	 *
	 * @param sn 设备sn编码
	 */
	@GetMapping(value = "/waterdevice")
	WaterDeviceDTO getBySnCode(@RequestParam("sn") String sn);

	/**
	 * 更新设备信息
	 *
	 * @param waterDeviceDTO
	 */
	@PatchMapping(value = "/waterdevice", consumes = MediaType.APPLICATION_JSON_VALUE)
	void updateDevice(@RequestBody WaterDeviceDTO waterDeviceDTO);

	/**
	 * 根据水机设备ID获取设备信息（详情）
	 *
	 * @param id 设备ID
	 */
	@GetMapping(value = "/waterdevice/{id}/detail")
	@ApiOperation(value = "根据水机设备ID获取设备信息（详情）")
	WaterDeviceDTO getWaterDeviceDetailById(@PathVariable("id") Integer id);

	/**
	 * 根据水机设备ID删除设备
	 *
	 * @param id 设备ID
	 */
	@DeleteMapping(value = "/waterdevice/{id}")
	void deleteWaterDevice(@PathVariable(value = "id") Integer id);


	/***
	 * 功能描述:批量记录生效状态改变
	 *
	 * @param: [ids, effective]
	 * @auther: liu yi
	 * @date: 2019/5/16 16:53
	 * @return: java.lang.Object
	 */
	@PatchMapping(value = "/waterdevice/filterChangeRecord")
	@ApiOperation(value = "批量记录生效状态改变")
	void forbiddenChangeWaterDeviceFilterChangeRecord(@RequestParam(value = "ids") Integer[] ids, @RequestParam(value = "effective", defaultValue = "1") Integer effective);

	/***
	 * 功能描述:新增水机滤芯更换记录
	 *
	 * @auther: liu yi
	 * @date: 2019/5/16 16:53
	 * @return: java.lang.Object
	 */
	@PostMapping(value = "/waterdevice/filterChangeRecord", consumes = MediaType.APPLICATION_JSON_VALUE)
	void createWaterDeviceFilterChangeRecord(@RequestBody WaterDeviceFilterChangeRecordDTO dto);

	/**
	 * 更新水机设备续费信息
	 */
	@PatchMapping(value = "/waterdevice/renew", consumes = MediaType.APPLICATION_JSON_VALUE)
	void renewProcessor(@RequestBody WaterDeviceDTO device);

	@PatchMapping(value = "/waterdevice/{id}/deactivatedsimcard")
	void deactivatedSimCard(@PathVariable("id") Integer id);

	/***
	 * 安装工转让更新水机上的安装工信息
	 * @param wdds
	 */
	@PostMapping(value = "/waterdevice/engineer", consumes = MediaType.APPLICATION_JSON_VALUE)
	void updateWaterDeviceForEngineer(@RequestBody List<WaterDeviceDTO> wdds);

	@GetMapping(value = "/placechangerecord/sn")
	public WaterDevicePlaceChangeRecordDTO getWaterDevicePlaceChangeRecordBySn(@RequestParam(value = "sn") String sn);

	/****
	 * 根据省市区获取水机设备
	 * @param wdd
	 */
	@PostMapping(value = "/waterdevice/prc", consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<Integer> getWaterDeviceListByPrc(@RequestBody WaterDeviceDTO wdd);

	/**
	 * 保存水机摆放位置更换记录
	 */
	@PostMapping(value = "/placechangerecord/save", consumes = MediaType.APPLICATION_JSON_VALUE)
	Void saveWaterDevicePlaceChangeRecord(@RequestBody WaterDevicePlaceChangeRecordDTO waterDevicePlaceChangeRecordDTO);

	@PatchMapping(value = "/waterdevice/{id}/activatingsimcard")
	void activatingSimCard(@PathVariable("id") Integer id, @RequestParam(value = "iccid", required = false) String iccid);

	/**
	 * 检查sn是否存在
	 *
	 * @param id 设备ID
	 */
	@GetMapping(value = "/waterdevice/checkSnExists")
	Boolean checkSnExists(@RequestParam("id") Integer id, @RequestParam("sncode") String sncode);

	/**
	 * 检查sim是否存在
	 *
	 * @param id 设备ID
	 */
	@GetMapping(value = "/waterdevice/checkIccidExists")
	Boolean checkIccidExists(@RequestParam("id") Integer id, @RequestParam("iccid") String iccid);

	/**
	 * 创建水机设备信息
	 *
	 * @param dto 水机设备信息
	 */
	@PostMapping(value = "/waterdevice", consumes = MediaType.APPLICATION_JSON_VALUE)
	WaterDeviceDTO createWaterDevice(@RequestBody WaterDeviceDTO dto);
}
