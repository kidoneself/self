package com.yimao.cloud.out.controller.api;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.WorkOrderStateEnum;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.service.WorkOrderApi;
import com.yimao.cloud.out.utils.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：原云平台提供给安装工APP调用的接口
 *
 * @Author Zhang Bo
 * @Date 2019/3/13
 */
@RestController
@Slf4j
@Api(tags = "WorkOrderApiController")
public class WorkOrderApiController {

    @Resource
    private WorkOrderApi workOrderApi;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserCache userCache;

    /***
     * 功能描述:安装工调用微服务--工程师工单数(工单、维修单、维护单)
     *
     * @param:
     * @auther: liu yi
     * @date: 2019/4/16 11:43
     * @return: org.springframework.http.ResponseEntity<java.util.Map < java.lang.String, java.lang.Object>>
     */
    @GetMapping(value = "/webapi/engineer/waterdevice/workorder/engineer/orderCount")
    @ApiOperation(value = "工程师工单数")
    public Map<String, Object> orderCount(HttpServletRequest request, @RequestParam(required = false) String orderType) {
        Integer engineerId = userCache.getCurrentEngineerId();
        List<Map<String, Object>> listMap = new ArrayList();
        Map<String, Object> wxCountMap = new HashMap();
        Map<String, Object> whCountMap = new HashMap();
        Map<String, Object> azCountMap = new HashMap();
        if (engineerId == null || engineerId == 0) {
            //安装工单
            azCountMap.put("waitingCount", 0);
            azCountMap.put("handingCount", 0);
            azCountMap.put("completedCount", 0);
            azCountMap.put("orderTypeName", "安装工单");
            azCountMap.put("orderType", 0);
            listMap.add(azCountMap);
            //维修工单
            wxCountMap.put("waitingCount", 0);
            wxCountMap.put("handingCount", 0);
            wxCountMap.put("completedCount", 0);
            wxCountMap.put("orderTypeName", "维修工单");
            wxCountMap.put("orderType", 1);
            listMap.add(wxCountMap);
            //维护工单
            whCountMap.put("waitingCount", 0);
            whCountMap.put("handingCount", 0);
            whCountMap.put("completedCount", 0);
            whCountMap.put("orderTypeName", "维护工单");
            whCountMap.put("orderType", 2);
            listMap.add(whCountMap);

            //换机工单
            /*countMap.put("waitingCount", 0);
            countMap.put("handingCount", 0);
            countMap.put("completedCount", 0);
            countMap.put("orderTypeName", "换机工单");
            countMap.put("orderType", 3);*/
            Map<String, Object> resultMap = ApiResult.result(request, JSONArray.fromObject(listMap));
            return resultMap;
        }
        //安装工单
        azCountMap.put("waitingCount", orderFeign.getEngineerWorkOrderInstallCount(engineerId, WorkOrderStateEnum.WORKORDER_STATE_NOT_ACCEPTED.state));
        azCountMap.put("handingCount", orderFeign.getEngineerWorkOrderInstallCount(engineerId, WorkOrderStateEnum.WORKORDER_STATE_SERVING.state));
        azCountMap.put("completedCount", orderFeign.getEngineerWorkOrderInstallCount(engineerId, WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.state));
        azCountMap.put("orderTypeName", "安装工单");
        azCountMap.put("orderType", 0);
        listMap.add(azCountMap);
        //维修工单
        wxCountMap.put("waitingCount", this.orderFeign.getWorkOrderRepairEngineerCount(engineerId.toString(), WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.state));
        wxCountMap.put("handingCount", this.orderFeign.getWorkOrderRepairEngineerCount(engineerId.toString(), WorkOrderStateEnum.WORKORDER_STATE_SERVING.state));
        wxCountMap.put("completedCount", this.orderFeign.getWorkOrderRepairEngineerCount(engineerId.toString(), WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.state));
        wxCountMap.put("orderTypeName", "维修工单");
        wxCountMap.put("orderType", 1);
        listMap.add(wxCountMap);
        //维护工单
        whCountMap.put("waitingCount", this.orderFeign.getWorkOrderMaintenanceEngineerCount(engineerId.toString(), WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.state));
        whCountMap.put("handingCount", this.orderFeign.getWorkOrderMaintenanceEngineerCount(engineerId.toString(), WorkOrderStateEnum.WORKORDER_STATE_SERVING.state));
        whCountMap.put("completedCount", this.orderFeign.getWorkOrderMaintenanceEngineerCount(engineerId.toString(), WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.state));
        whCountMap.put("orderTypeName", "维护工单");
        whCountMap.put("orderType", 2);
        listMap.add(whCountMap);

        //换机工单
        //TODO 换机工单
       /* countMap.put("waitingCount", this.workOrderExChangeApi.getExchangeWorkOrderCount(engineerId, WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.getState()));
        countMap.put("handingCount", this.workOrderExChangeApi.getExchangeWorkOrderCount(engineerId, WorkOrderStateEnum.WORKORDER_STATE_SERVING.getState()));
        countMap.put("completedCount", this.workOrderExChangeApi.getExchangeWorkOrderCount(engineerId, WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.getState()));
        countMap.put("orderTypeName", "换机工单");
        countMap.put("orderType", 3);*/

        Map<String, Object> resultMap = ApiResult.result(request, JSONArray.fromObject(listMap));
        return resultMap;
    }

}
