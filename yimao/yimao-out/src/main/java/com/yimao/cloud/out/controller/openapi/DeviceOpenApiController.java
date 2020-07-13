package com.yimao.cloud.out.controller.openapi;

import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.out.enums.ApiStatusCode;
import com.yimao.cloud.out.enums.OpenApiStatusCode;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.feign.WaterFeign;
import com.yimao.cloud.out.utils.ApiResult;
import com.yimao.cloud.out.utils.OpenApiResult;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDevicePlaceChangeRecordDTO;
import com.yimao.cloud.pojo.vo.water.TdsUploadRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @description   设备对外接口(原云百得调用微服务 ，微服务再请求云平台)
 * @author Liu Yi
 * @date 2019/11/14 17:40
 */
@RestController
@Slf4j
@RequestMapping(value = "/openapi/device")
public class DeviceOpenApiController {

    @Resource
    private WaterFeign waterFeign;

    @Resource
    private OrderFeign orderFeign;

    /**
     * 设备信息查询接口
     *
     * @param name
     * @param phone
     * @param province
     * @param city
     * @param region
     * @param address
     * @param batch
     * @param sncode
     * @param iccid
     * @return
     */
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public String find(
            HttpServletRequest request,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String sncode,
            @RequestParam(required = false) String iccid,
            @RequestParam(required = false, defaultValue = "0") Integer pagesize,
            @RequestParam(required = false, defaultValue = "0") Integer index) {
        if (StringUtil.isEmpty(sncode)) {
            return OpenApiResult.error(request, ApiStatusCode.SER_PARAM_ERROR.getCode()).toString();
        }
        WaterDeviceDTO waterDevice = waterFeign.getWaterDeviceBySnCode(sncode);

        Map<String, Object> map;
        if (Objects.nonNull(waterDevice)) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("iccid", iccid);
            data.put("banlance", waterDevice.getMoney().doubleValue());
            data.put("time", System.currentTimeMillis());
            map = OpenApiResult.result(request, data);
        } else {
            map = OpenApiResult.error(request, OpenApiStatusCode.DATA_NOT_FOUND);
        }

        return map.toString();
    }


    /**
     * 设备余额查询
     * @param iccid
     * @return
     */
    @RequestMapping(value = "/banlance", method = RequestMethod.GET)
    public String banlance(
            HttpServletRequest request,
            @RequestParam() String iccid) {
        if (StringUtil.isEmpty(iccid)) {
            return OpenApiResult.error(request, ApiStatusCode.SER_PARAM_ERROR.getCode()).toString();
        }
        WaterDeviceDTO waterDevice = waterFeign.getWaterDeviceByIccid(iccid);
        Map<String, Object> map;
        if (Objects.nonNull(waterDevice)) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("iccid", iccid);
            data.put("banlance", waterDevice.getMoney().doubleValue());
            data.put("time", System.currentTimeMillis());
            map = OpenApiResult.result(request, data);
        } else {
            map = OpenApiResult.error(request, OpenApiStatusCode.DATA_NOT_FOUND);
        }

        return map.toString();
    }

    /**
     * 设备费用充值
     * @param iccid
     * @param orderId
     * @param price
     * @return
     */
    @RequestMapping(value = "/recharge", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> recharge(
            @RequestParam() String iccid,
            @RequestParam() String orderId,
            @RequestParam() double price) {

        return null;
    }


    /**
     * 设备激活SIM卡
     * @param iccid
     * @param type
     * @return
     */
    @RequestMapping(value = "/sim", method = RequestMethod.POST)
    public Map<String, Object> sim(
            HttpServletRequest request,
            @RequestParam() String iccid,
            @RequestParam() Integer type) {

        return null;
    }

    /**
     * 通过SN查询设备数据(原售后请求微服务 微服务再请求云平台)
     *
     * @param sncode
     * @param request
     * @return
     */
    @RequestMapping(value = "/getBySnCode", method = RequestMethod.POST)
    @ResponseBody
    public Map getBySnCode(HttpServletRequest request, @RequestParam(name = "sncode") String sncode) {
        if (StringUtil.isEmpty(sncode)) {
            return ApiResult.error(request, ApiStatusCode.SER_PARAM_ERROR);
        }
        Map resultMap = new HashMap();
        List<Map> filterList = new ArrayList<>();
        String placeChange = "";
        try {
            WaterDevicePlaceChangeRecordDTO waterDevicePlaceChangeRecord = waterFeign.getWaterDevicePlaceChangeRecordBySn(sncode);
            WaterDeviceDTO waterDevice = waterFeign.getWaterDeviceBySnCode(sncode);
            if (Objects.isNull(waterDevice)) {
                return ApiResult.error(request, "错误的sn码");
            }

            if (Objects.nonNull(waterDevicePlaceChangeRecord)) {
                placeChange = "由" + waterDevicePlaceChangeRecord.getOldPlace() + "更换到" + waterDevicePlaceChangeRecord.getNewPlace();
            }

            resultMap.put("isOnline", waterDevice.getOnline());
            resultMap.put("place", placeChange);
            resultMap.put("currenttime", waterDevice.getCurrentTotalTime());
            resultMap.put("currentflow", waterDevice.getCurrentTotalFlow());
            resultMap.put("money", waterDevice.getMoney());
            resultMap.put("version", waterDevice.getVersion());

            if (waterDevice.getTdsId() != null) {
                Integer tdsId = waterDevice.getTdsId();
                if(tdsId !=null){
                    TdsUploadRecordVO tdsUploadRecord=waterFeign.getTdsUploadRecorddetail(tdsId);
                    if (Objects.nonNull(tdsUploadRecord)) {
                        resultMap.put("k", tdsUploadRecord.getK());
                        resultMap.put("t", tdsUploadRecord.getT());
                    }
                }else{
                    resultMap.put("k", 0);
                    resultMap.put("t", 0);
                }
            }

            List<MaintenanceWorkOrderDTO> orderList = orderFeign.getNotCompleteWorkOrderMaintenance(sncode,  null,1);
            for (MaintenanceWorkOrderDTO order : orderList) {
                Map<String, Object> filterMap = new HashMap<>();
                Date createTime = order.getCreateTime();
                String filterName = order.getMaterielDetailName();
                filterMap.put("filter", filterName);
                filterMap.put("filterTime", createTime);
                filterList.add(filterMap);
            }

            resultMap.put("filterInfo", filterList);
            return ApiResult.result(request, resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.error(request, e.getMessage());
        }
    }
}
