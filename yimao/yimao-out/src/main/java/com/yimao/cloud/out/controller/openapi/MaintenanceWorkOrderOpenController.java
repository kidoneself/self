package com.yimao.cloud.out.controller.openapi;

import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderStateEnum;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.out.enums.ApiStatusCode;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.utils.ApiResult;
import com.yimao.cloud.base.baideApi.utils.StringUtil;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/***
 * 功能描述: 维修工单同步（售后平台调用）
 *
 * @auther: liu yi
 * @date: 2019/3/27 17:06
 */
@RestController
@Api("RepairWorkOrderOpenController")
@Slf4j
@RequestMapping({"/openapi/waterdevice/workorder/maint"})
public class MaintenanceWorkOrderOpenController {
    @Resource
    private OrderFeign orderFeign;

    /***
     * 功能描述:关闭维护工单
     *
     * @param: [code, updateBy, updateByName, updateByRole, updateDate, autoComplete, request]
     * @auther: liu yi
     * @date: 2019/5/31 14:42
     * @return: java.util.Map
     */
    @PostMapping(value = {"/complete"})
    @ApiOperation("关闭维护工单")
    public Map close(@RequestParam(name = "code") String workCode,
                     @RequestParam(name = "updateBy") String updateBy,
                     @RequestParam(name = "updateByName") String updateByName,
                     @RequestParam(name = "updateByRole") String updateByRole,
                     @RequestParam(name = "updateDate") String updateDate,
                     @RequestParam(name = "autoComplete") int autoComplete,
                     HttpServletRequest request) {
        if (StringUtil.isEmpty(workCode) || StringUtil.isEmpty(updateBy) || StringUtil.isEmpty(updateByName) || StringUtil.isEmpty(updateByRole) || StringUtil.isEmpty(updateDate)) {
            return ApiResult.error(request, ApiStatusCode.SER_PARAM_ERROR);
        }

        try {
            MaintenanceWorkOrderDTO order = orderFeign.getWorkOrderMaintenanceByWorkCode(workCode);
            if (order == null) {
                return ApiResult.error(request, ApiStatusCode.SER_NO_DATA);
            }
            Date updateTime = DateUtil.stringToDate(updateDate, "yyyy-MM-dd HH:mm:ss");

            order.setWorkOrderCompleteStatus(StatusEnum.YES.value());
            order.setWorkOrderCompleteTime(new Date());
            order.setState(WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.state);
            order.setStateText(WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.stateText);
            order.setUpdateBy(updateBy);
            order.setUpdateByName(updateByName);
            order.setUpdateByRole(updateByRole);
            order.setUpdateDate(updateTime);
            order.setCompleteType(autoComplete);
            if (autoComplete == 1) {
                order.setCompleteTypeText("随维修工单自动完成");
            } else if (autoComplete == 2) {
                order.setCompleteTypeText("售后手动关闭");
            } else {
                return ApiResult.error(request, "无法辨别的操作!");
            }

            orderFeign.updateWorkOrderMaintenance(order);
        } catch (YimaoException e) {
            return ApiResult.error(request, e.getMessage());
        }

        return ApiResult.success(request);
    }
}
