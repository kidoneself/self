package com.yimao.cloud.order.controller;


import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.enums.RepairOrderSourceType;
import com.yimao.cloud.base.enums.RepairOrderStatus;
import com.yimao.cloud.base.enums.RepairOrderStep;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.mapper.WorkRepairOrderMapper;
import com.yimao.cloud.order.po.RepairWorkOrder;
import com.yimao.cloud.order.po.WorkRepairFault;
import com.yimao.cloud.order.po.WorkRepairOrder;
import com.yimao.cloud.order.service.RepairOrderService;
import com.yimao.cloud.order.service.RepairWorkOrderService;
import com.yimao.cloud.order.service.WorkRepairFaultService;
import com.yimao.cloud.pojo.dto.order.RepairWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkRepairMaterialUseRecordDTO;
import com.yimao.cloud.pojo.dto.order.WorkRepairOrderDTO;
import com.yimao.cloud.pojo.query.order.WorkRepairOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.WorkRepairOrderVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

/**
 * 维修工单
 *
 * @author Liu Yi
 * @date 2019/3/20.
 */
@RestController
@Slf4j
@Api(tags = "WorkOrderRepairController")
public class RepairWorkOrderController {
    @Resource
    private RepairWorkOrderService workOrderRepairService;
    @Resource
    private WorkRepairFaultService workRepairFaultService;
    @Resource
    private RepairOrderService repairOrderService;
	@Resource
	private WorkRepairOrderMapper workRepairOrderMapper;
    

    @PostMapping(value = "/order/repairWorkOrder")
    @ApiOperation(value = "新增维修工单", notes = "新增维修工单")
    @ApiImplicitParam(name = "dto", value = "设备信息", required = true, dataType = "RepairWorkOrderDTO", paramType = "body")
    public Object create(@RequestBody RepairWorkOrderDTO dto) {
        workOrderRepairService.create(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param isFather
     * @param distributorId
     * @param engineerId
     * @param state
     * @param orderStatus
     * @param search
     * @param pageNum
     * @param pageSize
     * @return
     * @description 查询维修工单列表
     * @author Liu Yi
     */
    @GetMapping(value = "/order/repairWorkOrder/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询维修工单列表", notes = "查询维修工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isFather", required = true, value = "是否是企业帐号Y-是，N-否", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "distributorId", value = "经销商ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "维修工单状态：2-已受理,3-处理中,4-已完成", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderStatus", value = "订单类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "搜索条件", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object page(@RequestParam(value = "isFather") String isFather,
                       @RequestParam(value = "distributorId", required = false) String distributorId,
                       @RequestParam(value = "engineerId", required = false) Integer engineerId,
                       @RequestParam(value = "state", required = false) Integer state,
                       @RequestParam(value = "orderStatus", required = false) String orderStatus,
                       @RequestParam(value = "search", required = false) String search,
                       @PathVariable("pageNum") Integer pageNum,
                       @PathVariable("pageSize") Integer pageSize) {
        PageVO<RepairWorkOrderDTO> page = workOrderRepairService.page(pageNum, pageSize, isFather, distributorId, engineerId, state, orderStatus, search);

        return ResponseEntity.ok(page);
    }

    /**
     * @param id
     * @return
     * @description 根据id查询维修工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/repairWorkOrder/{id}")
    @ApiOperation(value = "根据id查询维修工单", notes = "根据id查询维修工单")
    @ApiImplicitParam(name = "id", required = true, value = "工单ID", dataType = "Long", paramType = "path")
    public Object getWorkOrderRepairById(@PathVariable("id") Integer id) {
        RepairWorkOrderDTO dto = workOrderRepairService.getRepairWorkOrderById(id);

        return ResponseEntity.ok(dto);
    }

    /**
     * @param workCode
     * @return
     * @description 根据workCode查询维修工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/repairWorkOrder")
    @ApiOperation(value = "根据workCode查询维修工单", notes = "根据workCode查询维修工单")
    @ApiImplicitParam(name = "workCode", required = true, value = "工单ID", dataType = "String", paramType = "query")
    public Object getWorkOrderRepairByWorkCode(@RequestParam("workCode") String workCode) {
        RepairWorkOrderDTO dto = workOrderRepairService.getRepairWorkOrderByWorkCode(workCode);

        return ResponseEntity.ok(dto);
    }

    @RequestMapping(value = {"/order/repairWorkOrder"}, method = {RequestMethod.PUT})
    @ApiOperation("编辑维修工单")
    @ApiImplicitParam(name = "repairWorkOrderDTO", required = true, value = "维修工单", dataType = "RepairWorkOrderDTO", paramType = "body")
    public Object update(@RequestBody RepairWorkOrderDTO repairWorkOrderDTO) {
        if (repairWorkOrderDTO == null) {
            throw new BadRequestException("维修工单不能为空。");
        }
        workOrderRepairService.update(new RepairWorkOrder(repairWorkOrderDTO), null);
        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:维修工单完成后评价
     *
     * @param: [workcode, appraiseContent]
     * @auther: liu yi
     * @date: 2019/3/28 9:57
     * @return: void
     */
    @RequestMapping(value = {"/order/repairWorkOrder/userAppraise"}, method = {RequestMethod.PATCH})
    @ApiOperation("维修工单评价")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workCode", required = true, value = "维修工单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "appraiseContent", required = true, value = "评价内容", dataType = "String", paramType = "query")
    })
    public Object userAppraise(@RequestParam(value = "workCode") String workCode,
                               @RequestParam(value = "appraiseContent") String appraiseContent) {
        workOrderRepairService.userAppraise(workCode, appraiseContent);
        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:维修工单评价次数更新
     *
     * @param: [workcode, appraiseContent]
     * @auther: liu yi
     * @date: 2019/3/28 9:57
     * @return: void
     */
   /* @RequestMapping(value = {"/order/workOrderRepair/pushVoteIno"}, method = {RequestMethod.PATCH})
    @ResponseBody
    @ApiOperation("维修工单评价次数更新")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workCode",required = true, value = "维修工单号",dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "voteCount",required = true, defaultValue = "0", value = "总评论数",dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "goodVoteCount",required = true, defaultValue = "0",value = "好评数",dataType = "String", paramType = "query")
    })
    public Object  updateVoteWorkOrderRepair(@RequestParam(value = "workCode")String workCode,
                                @RequestParam(value = "voteCount")Integer voteCount,
                               @RequestParam(value = "goodVoteCount")Integer goodVoteCount){
        workOrderRepairService.pushVoteIno(workCode,voteCount,goodVoteCount);
        return ResponseEntity.noContent();
    }*/

    /**
     * 描述：获取安装工某个状态的维修工单数量
     *
     * @param engineerId 安装工ID
     * @param status     工单状态
     * @Creator Liu Yi
     * @CreateTime 2019/3/9 12:03
     **/
    @RequestMapping(value = "/order/repairWorkOrder/count/status", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取安装工某个状态的维修工单数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "engineerId", required = true, value = "安装工id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", required = true, value = "维修工单状态：2-已受理,3-处理中,4-已完成", dataType = "String", paramType = "query")
    })
    public Object getWorkOrderRepairEngineerCount(@RequestParam(value = "engineerId") String engineerId,
                                                  @RequestParam(value = "status", required = false) Integer status) {
        Integer count = workOrderRepairService.getWorkOrderRepairEngineerCount(engineerId, status);

        return ResponseEntity.ok(count);
    }
    
    
    /*
     * ----------------------3.0新维修工单接口-----------------------
     */
    @PostMapping(value = "/repair/fault")
    @ApiOperation(value = "新增故障类型-3.0", notes = "新增维修新增故障类型-3.0")
    @ApiImplicitParam(name = "fault", value = "故障类型信息", required = true, dataType = "WorkRepairFault", paramType = "body")
    public Object createRepairFault(@RequestBody WorkRepairFault fault) {
    	workRepairFaultService.create(fault);
        return ResponseEntity.noContent().build();
    }
    
    
    @PostMapping(value = "/repair/fault/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询维修故障类型列表-3.0", notes = "查询维修故障类型列表-3.0")
    @ApiImplicitParams({
    	  	@ApiImplicitParam(name = "type", value = "类型 0-水机推送故障 1-后台自定义故障", dataType = "Long", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object pageRepairFault(@PathVariable("pageNum") Integer pageNum,@PathVariable("pageSize") Integer pageSize,@RequestParam(value = "type",required=false) Integer type) {
    	
        PageVO<WorkRepairFault> page = workRepairFaultService.page(pageNum, pageSize, type);

        return page;
    }
    
    
    @PutMapping(value = {"/repair/fault"})
    @ApiOperation("编辑维修新增故障类型-3.0")
    @ApiImplicitParam(name = "fault", required = true, value = "故障类型", dataType = "WorkRepairFault", paramType = "body")
    public Object editRepairFault(@RequestBody WorkRepairFault fault) {
    	workRepairFaultService.update(fault);
        return ResponseEntity.noContent().build();
    }
    
    
    
    @DeleteMapping(value = {"/repair/fault/{id}"})
    @ApiOperation("删除维修新增故障类型-3.0")
    @ApiImplicitParam(name = "id", required = true, value = "故障类型id", dataType = "Long", paramType = "path")
    public Object editRepairFault(@PathVariable("id") Integer id) {
    	workRepairFaultService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    
    /**
     * 新增维修工单(唯一入口)
     * @param dto
     * @return
     */
    @PostMapping(value = "/repair/workorder")
    @ApiOperation(value = "新增维修工单-3.0", notes = "新增维修工单-3.0")
    @ApiImplicitParam(name = "dto", value = "维修工单信息", required = true, dataType = "WorkRepairOrderDTO", paramType = "body")
    public Object createRepairOrder(@RequestBody WorkRepairOrderDTO dto) {
    	
    	log.info("创建维修工单参数={}",JSON.toJSONString(dto));
    	
    	if(Objects.isNull(dto.getSourceType())) {
    		throw new YimaoException("维修工单来源为空");
    	}
    	
    	if(Objects.isNull(RepairOrderSourceType.find(dto.getSourceType()))) {
    		throw new YimaoException("维修工单未知来源");
    	}
    	
    	if(RepairOrderSourceType.ENGINEER_APP.value == dto.getSourceType()) {
    		//安装工app
    		try {
    			repairOrderService.createAppRepairOrder(dto);
    			
    			return ResponseEntity.ok().build();
    			
			} catch (Exception e) {
				log.error("维修工单创建失败,message={}",e.getMessage());
				if(e instanceof YimaoException) {
					YimaoException yimaoException = (YimaoException)e;
					return ResponseEntity.badRequest().body(yimaoException.getMessage());
				}else if(e instanceof BadRequestException) {
					BadRequestException badRequestException = (BadRequestException)e;
					return ResponseEntity.badRequest().body(badRequestException.getMessage());
				}else {
					e.printStackTrace();
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}			
			}
    		
    	}else if(RepairOrderSourceType.SYSTEM.value == dto.getSourceType()) {
    		//业务系统创建    		
    		repairOrderService.createSystemRepairOrder(dto);
    		return ResponseEntity.noContent().build();
    		
    	}else {
    		return ResponseEntity.noContent().build();
    	}
		        
    }
    
    
    @PostMapping(value = "/repair/workorder/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询维修工单列表-3.0", notes = "查询维修工单列表-3,0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "search", value = "搜索条件", dataType = "WorkRepairOrderQuery", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public PageVO<WorkRepairOrderVO> pageRepairOrders(@RequestBody WorkRepairOrderQuery search,
                       @PathVariable("pageNum") Integer pageNum,
                       @PathVariable("pageSize") Integer pageSize) {
    	
    	return repairOrderService.page(pageNum,pageSize,search);
    }
    
    @GetMapping(value = "/repair/workorder/info/{workOrderNo}")
    @ApiOperation(value = "维修工单详情-3.0", notes = "维修工单详情-3.0")
    @ApiImplicitParam(name = "workOrderNo", value = "维修单号", dataType = "String", paramType = "path")            
    public Object repairOrderInfo(@PathVariable("workOrderNo") String workOrderNo) {
    	if(StringUtil.isEmpty(workOrderNo)) {
    		throw new BadRequestException("维修单号为空");
    	}
    	
    	WorkRepairOrder query=new WorkRepairOrder();
    	query.setWorkOrderNo(workOrderNo);
    	WorkRepairOrder repairOrder = workRepairOrderMapper.selectOne(query);
    	
    	if(Objects.isNull(repairOrder)) {
    		throw new YimaoException("维修工单不存在");
    	}
    	WorkRepairOrderVO vo=new WorkRepairOrderVO();
    	BeanUtils.copyProperties(repairOrder, vo);
    	
    	List<WorkRepairMaterialUseRecordDTO> recordList= workRepairOrderMapper.selectMaterialUseRecordById(repairOrder.getId());
    	vo.setMaterialUseRecordList(recordList);
    	
    	return vo;
    }
    

	@GetMapping(value = "/repair/workorder/getRepairOrderByWorkOrderNo")
	@ApiOperation(value = "根据维修单号查询维修工单详情-3.0", notes = "根据维修单号查询维修工单详情-3.0")
	@ApiImplicitParam(name = "workOrderNo", value = "维修单号", dataType = "String", paramType = "query")
	public WorkRepairOrderVO getRepairOrderByWorkOrderNo(@RequestParam("workOrderNo")String workOrderNo) {
		if(StringUtil.isEmpty(workOrderNo)) {
			throw new BadRequestException("维修单号");
		}
		
		WorkRepairOrder query = new WorkRepairOrder();
		query.setWorkOrderNo(workOrderNo);
		WorkRepairOrder res =workRepairOrderMapper.selectOne(query);
		
		List<WorkRepairMaterialUseRecordDTO> record = workRepairOrderMapper.selectMaterialUseRecordById(res.getId());
		
		if(Objects.isNull(res)) {
			return null;
		}else {
			WorkRepairOrderVO vo = new WorkRepairOrderVO();
			
			BeanUtils.copyProperties(res,vo);
			
			vo.setMaterialUseRecordList(record);
			
			return vo;
		}
	}
    
	/**
	 * 改约维修工单修改
	 * @param dto
	 */
	@PutMapping("/repair/workorder/renegotiation")
	@ApiOperation(value = "改约维修工单修改-3.0", notes = "改约维修工单修改-3.0")
	@ApiImplicitParam(name = "dto", dataType = "WorkRepairOrderDTO", paramType = "body")
	public void renegotiationRepairOrderUpdate(@RequestBody WorkRepairOrderDTO dto) {
		if(Objects.isNull(dto.getId())) {
			throw new BadRequestException("id为空");
		}
		
		if(StringUtil.isEmpty(dto.getWorkOrderNo())) {
			throw new BadRequestException("维修单号为空");
		}
		WorkRepairOrder record =new WorkRepairOrder();
		record.setStatus(RepairOrderStatus.HANG_UP.value);
		record.setHangRemark(dto.getHangRemark());
		record.setHangTime(new Date());
		record.setRevisionStartTime(dto.getRevisionStartTime());
		record.setRevisionEndTime(dto.getRevisionEndTime());
		Example example = new Example(WorkRepairOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", dto.getId());
        criteria.andEqualTo("workOrderNo", dto.getWorkOrderNo());
        criteria.andEqualTo("status", RepairOrderStatus.PENDING.value);
		int res = workRepairOrderMapper.updateByExampleSelective(record, example);
		
		if(res < 1) {
			throw new YimaoException("改约更新失败");
		}
	}
    
    
    
	/**
	 * 维修工单处理中步骤流转
	 * @param dto
	 * @return
	 */
	@PutMapping("/repair/workorder/processStepChange")
	@ApiOperation(value = "维修工单处理中步骤流转-3.0", notes = "维修工单处理中步骤流转-3.0")
	@ApiImplicitParam(name = "dto", dataType = "WorkRepairOrderDTO", paramType = "body")
	public WorkRepairOrderVO processRepairOrderChange(@RequestBody WorkRepairOrderDTO dto) {
		log.info("维修工单处理中变更步骤传参={}",JSON.toJSONString(dto));
		return repairOrderService.processRepairOrderChange(dto);
	}
    
	/**
	 * 维修工单变更完成状态
	 * @param id
	 */
	@PutMapping(value = "/repair/workorder/submit/{id}")
	@ApiOperation(value = "维修工单变更完成状态-3.0", notes = "维修工单变更完成状态-3.0")
	@ApiImplicitParam(name = "id", dataType = "Long", paramType = "path")
	public void submitRepairOrder(@PathVariable("id")Integer id) {
		
		if(Objects.isNull(id)) {
			throw new BadRequestException("id为空");
		}
		
		WorkRepairOrder record =new WorkRepairOrder();
		record.setStatus(RepairOrderStatus.SOLVED.value);
		record.setEngineerFinishTime(new Date());
		Example example = new Example(WorkRepairOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", id);
        criteria.andEqualTo("status", RepairOrderStatus.PROCESSIONG.value);
        criteria.andEqualTo("step", RepairOrderStep.SUBMIT.value);
		int res = workRepairOrderMapper.updateByExampleSelective(record, example);
		
		if(res < 1) {
			throw new YimaoException("维修工单提交更新失败");
		}
		
	}
	
	/**
	 * 维修工单继续服务返回展示信息
	 * @param workOrderNo
	 * @return
	 */
	@GetMapping(value = "/repair/workorder/continueRepairServiveInfo/{id}/{step}")
	public WorkRepairOrderVO continueRepairServiveInfo(@PathVariable("id")Integer id,@PathVariable("step")Integer step) {
		if(Objects.isNull(id)) {
			throw new BadRequestException("id为空");
		}
		
		if(Objects.isNull(step)) {
			throw new BadRequestException("维修步骤为空");
		}
		
		return repairOrderService.continueRepairServiveInfo(id,step);
		 
	}
	
    /**
     * 维修更换安装工
     * @param workOrderNo
     * @param engineerId
     */
    @PutMapping("/repair/workorder/replace")
	public void replaceRepairEngineer(@RequestParam("workOrderNo")String workOrderNo,
									  @RequestParam("engineerId")Integer engineerId,
									  @RequestParam("sourceType")Integer sourceType,
									  @RequestParam("operator")String operator) {
    	repairOrderService.replaceRepairEngineer(workOrderNo,engineerId,sourceType,operator);
    }


	/**
	 * 查询每个状态的维修工单数量
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/repair/workorder/count/census")
	public Map<String,Integer> getRepairOrderCount(@RequestParam(value = "engineerId") Integer engineerId){
		return repairOrderService.getRepairOrderCount(engineerId);
	}


	/**
	 * 维修模块总工单数:待维修、处理中、挂单
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/repair/model/workorder/count/total")
	public Integer getRepairModelTotalCount(@RequestParam(value = "engineerId") Integer engineerId){
		return repairOrderService.getRepairModelTotalCount(engineerId);
	}
    
}
