package com.yimao.cloud.engineer.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.engineer.feign.SystemFeign;
import com.yimao.cloud.engineer.feign.UserFeign;
import com.yimao.cloud.engineer.service.WorkOrderStatisticsService;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @ClassName WorkOrderStatisticsController
 * @Description 服务-工单统计
 * @Author yuchunlei
 * @Date 2020/7/2 13:35
 * @Version 1.0
 */
@RestController
@Slf4j
@Api(tags = "WorkOrderStatisticsController")
public class WorkOrderStatisticsController {

    @Resource
    private WorkOrderStatisticsService workOrderStatisticsService;

    @Resource
    private UserCache userCache;
    
    @Resource
    private SystemFeign systemFeign;
    
    @Resource
    private UserFeign userFeign;

    /**
     * 数据中心-服务统计
     * @param completeTime
     * @param timeType
     * @return
     */
    @GetMapping(value = "/workOrder/service/statistics")
    @ApiOperation(value = "数据中心-服务统计", notes = "数据中心-服务统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "completeTime", value = "时间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "timeType", value = "日期类型:时间类型1 :日,2 :月,3 :年", required = true, dataType = "Long", paramType = "query")
    })
    public Object statisticsWorkOrder(@RequestParam(value = "completeTime") String completeTime, @RequestParam(value = "timeType") Integer timeType){
        Integer engineerId = userCache.getCurrentEngineerId();
        if(Objects.isNull(engineerId)){
            throw new BadRequestException("参数错误");
        }
        if(timeType == 1){
            completeTime = DateUtil.transferDateToString(new Date());
        } else if(timeType == 2){
            completeTime = new SimpleDateFormat("yyyy-MM").format(new Date());
        } else if (timeType == 3){
            completeTime = new SimpleDateFormat("yyyy").format(new Date());
        }
        return workOrderStatisticsService.statisticsCompleteWorkOrder(completeTime,engineerId,timeType);
    }
    
    /**
     * 数据中心-可用库存-水机设备
     * @param completeTime
     * @param timeType
     * @return
     */
    @GetMapping(value = "/dataCenter/availableDeviceStock")
    @ApiOperation(value = "数据中心-可用库存-水机", notes = "数据中心-可用库存-水机")
    public Object availableDeviceStock(){
    	
		  Integer engineerId = userCache.getCurrentEngineerId();
	      if(Objects.isNull(engineerId)){
	          throw new BadRequestException("安装工登录失效");
	      }
	      
	      EngineerDTO engineer = userFeign.getEngineerById(engineerId);
	      
	      if(Objects.isNull(engineer)) {
	    	  throw new YimaoException("安装工不存在");
	      }
	      
	      if(Objects.isNull(engineer.getStationId())) {
	    	  throw new YimaoException("安装所属服务站门店不存在");
	      }
	      
	      return systemFeign.availableStationDeviceStock(engineer.getStationId());
	       
    	
    }
    
    /**
     * 数据中心-可用库存--耗材
     * @param completeTime
     * @param timeType
     * @return
     */
    @PostMapping(value = "/dataCenter/availableMaterialStock")
    @ApiOperation(value = "数据中心-可用库存-耗材", notes = "数据中心-可用库存-耗材")
    @ApiImplicitParam(name = "adaptionModel", value = "适配型号", required = false, dataType = "String", paramType = "body")
    public Object availableMaterialStock(@RequestParam(value = "adaptionModel" ,required=false)String adaptionModel){
    	
		  Integer engineerId = userCache.getCurrentEngineerId();
	      if(Objects.isNull(engineerId)){
	          throw new BadRequestException("安装工登录失效");
	      }
	      
	      EngineerDTO engineer = userFeign.getEngineerById(engineerId);
	      
	      if(Objects.isNull(engineer)) {
	    	  throw new YimaoException("安装工不存在");
	      }
	      
	      if(Objects.isNull(engineer.getStationId())) {
	    	  throw new YimaoException("安装所属服务站门店不存在");
	      }
	      
	      return systemFeign.availableStationMaterialStock(engineer.getStationId(),adaptionModel);
	       
    	
    }

}
