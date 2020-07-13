package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.order.WorkOrderBackDTO;
import com.yimao.cloud.pojo.query.order.WorkOrderBackQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

	/**
	 * 创建退机工单
	 * @param workOrderBackDTO 退机工单
	 */
	@PostMapping(value = "/workorderBack")
	@ApiOperation(value = "创建退机工单", notes = "创建退机工单")
	@ApiImplicitParam(name = "dto", value = "退机工单", required = true, dataType = "WorkOrderBackDTO", paramType = "body")
	public void createWorkOrderBack(@RequestBody WorkOrderBackDTO workOrderBackDTO) {
		orderFeign.createWorkOrderBack(workOrderBackDTO);
	}
}
