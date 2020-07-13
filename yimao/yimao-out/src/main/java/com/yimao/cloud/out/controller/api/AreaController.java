package com.yimao.cloud.out.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.feign.SystemFeign;
import com.yimao.cloud.out.utils.ResultUtil;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.vo.out.AreaVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 原云平台提供给安装工APP调用的接口
 */
@RestController
@Slf4j
@Api(tags = "AreaController")
public class AreaController {

    @Resource
    private RedisCache redisCache;

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private SystemFeign systemFeign;

    /**
     * 原云平台-安装工APP获取所有省市区
     */
    @GetMapping(value = "/api/area/region")
    @ApiOperation(value = "原云平台-安装工APP获取所有省市区")
    public Map<String, Object> area() {
        List<AreaDTO> list = redisCache.getCacheList(Constant.AREA_CACHE, AreaDTO.class);
        if (CollectionUtil.isEmpty(list)) {
            list = systemFeign.areaList();
        }

        List<JSONObject> voList = new ArrayList<>();
        JSONObject json;
        for (AreaDTO dto : list) {
            json = new JSONObject();
            json.put("aId", dto.getId());
            json.put("name", dto.getName());
            json.put("pId", dto.getPid());

            voList.add(json);
        }
        Map<String, Object> ru = new HashMap<>();
        ru.put("list", voList);
        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 原云平台-安装工APP支付时判断是否上线中地区
     */
    @GetMapping(value = "/api/area/isSyncOnline")
    @ApiOperation(value = "原云平台-安装工APP支付时判断是否上线中地区")
    public Map<String, Object> isSyncOnline(@RequestParam String workOrderId) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);

        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(workOrderId);
        if (workOrder == null) {
            ResultUtil.error(ru, "59", "工单不存在");
            return ru;
        }
        String province = workOrder.getProvince();
        String city = workOrder.getCity();
        String region = workOrder.getRegion();
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(province, city, region);
        if (onlineArea != null && !StatusEnum.isYes(onlineArea.getSyncState())) {
            ResultUtil.error(ru, "73", "您所在地区正在升级新流程，用户支付不允许支付");
            return ru;
        }
        return ru;
    }

}
