package com.yimao.cloud.engineer.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.WorkOrderBackStatusEnum;
import com.yimao.cloud.base.enums.WorkOrderStatusEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.engineer.feign.OrderFeign;
import com.yimao.cloud.engineer.feign.SystemFeign;
import com.yimao.cloud.engineer.feign.WaterFeign;
import com.yimao.cloud.pojo.dto.order.WorkOrderBackDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.query.order.WorkOrderBackQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description 水机退机控制层
 * @author Liu Yi
 * @date 2020/6/23 9:14
 */
@RestController
@Slf4j
@Api(tags = "WorkOrderBackController")
public class WorkOrderBackController {

	@Resource
	private OrderFeign orderFeign;

	@Resource
	private SystemFeign systemFeign;

	@Resource
	private UserCache userCache;

	/**
	 * 根据安装工查询退机工单数量
	 */
	@GetMapping(value = "/workorderBack/engineer/count")
	@ApiOperation(value = "根据安装工查询退机工单数量")
	public List<Map<String, Object>> getWorkOrderBackCountByEngineerId() {
		List<Map<String, Object>> list = orderFeign.getWorkOrderBackCountByEngineerId();

		return list;
	}

	/**
	 * 根据条件查询退机工单信息
	 */
	@GetMapping(value = "/workorderBack/{pageNum}/{pageSize}")
	@ApiOperation(value = "根据条件查询安装工单信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
			@ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")
	})
	public ResponseEntity<PageVO<WorkOrderBackDTO>> getWorkOrderBackList(
			@PathVariable(value = "pageNum") Integer pageNum,
			@PathVariable(value = "pageSize") Integer pageSize,
			WorkOrderBackQueryDTO query) {
		Integer engineerId = userCache.getCurrentEngineerId();

		if (engineerId == null) {
			throw new YimaoException("用户信息过期或不存在！");
		}

		query.setEngineerId(engineerId);
		PageVO<WorkOrderBackDTO> orderList = orderFeign.getWorkOrderBackList(query, pageNum, pageSize);

		return ResponseEntity.ok(orderList);
	}

	/**
	 * 描述：根据工单id获取退机工单信息
	 * @param id 退机工单ID
	 **/
	@GetMapping(value = "/workorderBack/{id}")
	@ApiOperation(value = "根据工单id获取退机工单信息", notes = "根据工单id获取退机工单信息")
	@ApiImplicitParam(name = "id", value = "工单ID", dataType = "Long", required = true, paramType = "path")
	public WorkOrderBackDTO getWorkOrderById(@PathVariable(value = "id") Integer id) {
		WorkOrderBackDTO dto = orderFeign.getWorkOrderBackById(id);

		return dto;
	}

	@PatchMapping(value = "/workorderBack/{id}/start")
	@ApiOperation(value = "开始退机工单服务", notes = "开始退机工单服务")
	@ApiImplicitParam(name = "id", value = "工单ID", dataType = "Long", required = true, paramType = "path")
	public void workOrderBackStart(@PathVariable(value = "id") Integer id) {
		WorkOrderBackDTO dto = orderFeign.getWorkOrderBackById(id);
		dto.setStatus(WorkOrderBackStatusEnum.INSTALLING.value);
		dto.setAcceptTime(new Date());

		orderFeign.updateWorkOrderBack(dto);
	}

	/**
	 * 描述：校验sn是否为拆机对应的设备
	 **/
	@GetMapping(value = "/workorderBack/{id}/checkSn")
	@ApiOperation(value = "校验sn是否为拆机对应的设备", notes = "校验sn是否为拆机对应的设备")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "工单ID", dataType = "Long", required = true, paramType = "path"),
			@ApiImplicitParam(name = "snCode", value = "snCode", dataType = "Long", required = true, paramType = "query")
	})
	public boolean checkSn(@PathVariable(value = "id") Integer id, @RequestParam(value = "snCode") String snCode) {
		if (StringUtils.isBlank(snCode)) {
			throw new BadRequestException("sn必填!");
		}
		if (id == null) {
			throw new BadRequestException("id不能为空!");
		}
		WorkOrderBackDTO dto = orderFeign.getWorkOrderBackById(id);
		if (Objects.isNull(dto)) {
			throw new YimaoException("退机工单不存在!");
		}
		if (!snCode.equalsIgnoreCase(dto.getSn())) {
			throw new YimaoException("sn不匹配!");
		}

		return true;
	}

	/**
	 * 描述：挂单
	 **/
	@PatchMapping(value = "/workorderBack/{id}/pendingOrder")
	@ApiOperation(value = "挂单", notes = "挂单")
	@ApiImplicitParam(name = "id", value = "工单ID", dataType = "Long", required = true, paramType = "path")
	@ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query"),
			@ApiImplicitParam(name = "appointCauseType", value = "改约原因类型：1-时间冲突  2-其它", dataType = "Long", paramType = "query"),
			@ApiImplicitParam(name = "appointDate", value = "改约日期", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "appointTimeLimit", value = "改约时间段", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "remark", value = "备注", dataType = "Long", paramType = "query")
	})
	public void pendingOrder(@PathVariable(value = "id") Integer id,
	                         @RequestParam(value = "appointCauseType") Integer appointCauseType,
	                         @RequestParam(value = "appointDate") String appointDate,
	                         @RequestParam(value = "appointTimeLimit") String appointTimeLimit,
	                         @RequestParam(value = "remark") String remark) {
		if (id == null) {
			throw new BadRequestException("id不能为空!");
		}
		if (appointCauseType == null) {
			throw new BadRequestException("改约类型不能为空!");
		}
		if (StringUtil.isBlank(appointDate)) {
			throw new BadRequestException("改约日期不能为空!");
		}
		if (StringUtil.isBlank(appointTimeLimit)) {
			throw new BadRequestException("改约时间不能为空!");
		}
		WorkOrderBackDTO dto = orderFeign.getWorkOrderBackById(id);
		if (Objects.isNull(dto)) {
			throw new YimaoException("退机工单不存在!");
		}
		dto.setAppointDate(DateUtil.transferStringToDate(appointDate));
		dto.setAppointTimeLimit(appointTimeLimit);
		dto.setAppointCauseType(appointCauseType);
		dto.setCountdownTime(DateUtil.transferStringToDate(appointDate));

		dto.setRemark(remark);
		dto.setStatus(WorkOrderBackStatusEnum.SUSPEND.value);
		dto.setUpdateTime(new Date());

		orderFeign.updateWorkOrderBack(dto);
	}

	/**
	 * 描述：拆机图片提交
	 **/
	@PatchMapping(value = "/workorderBack/{id}/uploadImage")
	@ApiOperation(value = "校验sn是否为拆机对应的设备", notes = "校验sn是否为拆机对应的设备")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "工单ID", dataType = "Long", required = true, paramType = "path"),
			@ApiImplicitParam(name = "snCode", value = "snCode", dataType = "Long", required = true, paramType = "query")
	})
	public void uploadImage(@PathVariable(value = "id") Integer id, @RequestParam(value = "snCode") String snCode, MultipartFile file1, MultipartFile file2, MultipartFile file3) {
		if (StringUtils.isBlank(snCode)) {
			throw new BadRequestException("sn必填!");
		}
		if (id == null) {
			throw new BadRequestException("id不能为空!");
		}
		WorkOrderBackDTO dto = orderFeign.getWorkOrderBackById(id);
		if (Objects.isNull(dto)) {
			throw new YimaoException("退机工单不存在!");
		}
		if (!snCode.trim().equalsIgnoreCase(dto.getSn().trim())) {
			throw new YimaoException("sn不匹配!");
		}

		dto.setImg(this.upload(file1, file2, file3, null, null));
		dto.setImgTime(new Date());
		dto.setUpdateTime(new Date());

		orderFeign.updateWorkOrderBack(dto);
	}


	/**
	 * 描述：确认提交
	 **/
	@PatchMapping(value = "/workorderBack/{id}/finish")
	@ApiOperation(value = "校验sn是否为拆机对应的设备", notes = "校验sn是否为拆机对应的设备")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "工单ID", dataType = "Long", required = true, paramType = "path"),
			@ApiImplicitParam(name = "snCode", value = "snCode", dataType = "Long", required = true, paramType = "query")
	})
	public void finishWorkOrderBack(@PathVariable(value = "id") Integer id, @RequestParam(value = "snCode") String snCode) {
		orderFeign.finishWorkOrderBack(id,snCode);
	}

	public String upload(MultipartFile file1, MultipartFile file2, MultipartFile file3, String folder, String remark) {
		String path = "";
		try {
			if (file1 != null) {
				String path1 = systemFeign.upload(file1, folder, remark);
				path += StringUtil.isNotBlank(path1) ? path1 : "";
			}
			if (file2 != null) {
				String path2 = systemFeign.upload(file2, folder, remark);
				path += StringUtil.isNotBlank(path2) ? "," + path2 : "";
			}
			if (file3 != null) {
				String path3 = systemFeign.upload(file3, folder, remark);
				path += StringUtil.isNotBlank(path3) ? "," + path3 : "";
			}
		} catch (Exception e) {
			log.error("==========上传失败=========" + e.getMessage());
		}

		return path;
	}
}
