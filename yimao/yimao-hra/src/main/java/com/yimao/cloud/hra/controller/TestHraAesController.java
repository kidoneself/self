package com.yimao.cloud.hra.controller;

import com.yimao.cloud.base.utils.AESUtil;
import com.yimao.cloud.base.utils.JsonUtil;
import com.yimao.cloud.hra.dto.HraRequest;
import com.yimao.cloud.hra.msg.HraResult;
import com.yimao.cloud.hra.service.HraAesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2017/12/15.
 */
@RestController
@Slf4j
@Api(tags = "TestHraAesController")
public class TestHraAesController {

    @Resource
    private HraAesService hraAesService;

    /**
     * 接口编号：Interface_001
     * 接口名称：新增或修改翼猫服务站信息
     * 接口功能说明：把本地配置的翼猫服务站信息上传到云端
     * 涉及功能编号：func_001
     * URL地址：http://hraapi.yimaokeji.com:10090/ServiceStation
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "新增或修改翼猫服务站信息")
    @PostMapping(value = "/test/ServiceStation")
    public Object serviceStation(@RequestBody HraRequest hraRequest) {
        try {
            log.debug("===ServiceStation请求参数为：" + JsonUtil.objectToJson(hraRequest));
            String aesContent = AESUtil.AESEncode(JsonUtil.objectToJson(hraRequest));
            return hraAesService.serviceStation(aesContent);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HraResult.build("501", "接口服务器工作不正常。");
        }
    }

    /**
     * 接口编号：Interface_002
     * 接口名称：翼猫HRA服务器测试接口
     * 接口功能说明：测试翼猫HRA服务器提供的接口服务工作是否正常
     * 涉及功能编号：func_002、func_003
     * URL地址：http://hraapi.yimaokeji.com:10090/DeviceTest
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "翼猫HRA服务器测试接口")
    @PostMapping(value = "/test/DeviceTest")
    public Object deviceTest(@RequestBody HraRequest hraRequest) {
        try {
            log.debug("===DeviceTest请求参数为：" + JsonUtil.objectToJson(hraRequest));
            String aesContent = AESUtil.AESEncode(JsonUtil.objectToJson(hraRequest));
            return hraAesService.deviceTest(aesContent);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HraResult.build("501", "接口服务器工作不正常。");
        }
    }

    /**
     * 接口编号：Interface_003
     * 接口名称：通过评估卡号获取预约用户信息
     * 接口功能说明：通过评估卡号获取预约用户信息
     * 涉及功能编号：func_005
     * URL地址：http://hraapi.yimaokeji.com:10090/GetCustomerByTicketNo
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "通过评估卡号获取预约用户信息")
    @PostMapping(value = "/test/GetCustomerByTicketNo")
    public Object getCustomerByTicketNo(@RequestBody HraRequest hraRequest) {
        try {
            log.debug("===GetCustomerByTicketNo请求参数为：" + JsonUtil.objectToJson(hraRequest));
            String aesContent = AESUtil.AESEncode(JsonUtil.objectToJson(hraRequest));
            return hraAesService.getCustomerByTicketNo(aesContent);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HraResult.build("501", "接口服务器工作不正常。");
        }
    }

    /**
     * 接口编号：Interface_005
     * 接口名称：验证评估卡是否被使用
     * 接口功能说明：验证评估卡是否被使用
     * 涉及功能编号：func_006
     * URL地址：http://hraapi.yimaokeji.com:10090/TicketValidate
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "验证评估卡是否被使用")
    @PostMapping(value = "/test/TicketValidate")
    public Object ticketValidate(@RequestBody HraRequest hraRequest) {
        try {
            log.debug("===TicketValidate请求参数为：" + JsonUtil.objectToJson(hraRequest));
            String aesContent = AESUtil.AESEncode(JsonUtil.objectToJson(hraRequest));
            return hraAesService.ticketValidate(aesContent);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HraResult.build("501", "接口服务器工作不正常。");
        }
    }

    /**
     * 接口编号：Interface_004
     * 接口名称：评估卡号校验并绑定用户
     * 接口功能说明：评估卡号校验并绑定用户
     * 涉及功能编号：func_007
     * URL地址：http://hraapi.yimaokeji.com:10090/TicketBindCustomer
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "评估卡号校验并绑定用户")
    @PostMapping(value = "/test/TicketBindCustomer")
    public Object ticketBindCustomer(@RequestBody HraRequest hraRequest) {
        try {
            log.debug("===TicketBindCustomer请求参数为：" + JsonUtil.objectToJson(hraRequest));
            String aesContent = AESUtil.AESEncode(JsonUtil.objectToJson(hraRequest));
            return hraAesService.ticketBindCustomer(aesContent);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HraResult.build("501", "接口服务器工作不正常。");
        }
    }

    /**
     * 接口编号：Interface_006
     * 接口名称：评估卡变更为已使用状态
     * 接口功能说明：评估卡变更为已使用状态
     * 涉及功能编号：func_008
     * URL地址：http://hraapi.yimaokeji.com:10090/TicketMarkupUsed
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "评估卡变更为已使用状态")
    @PostMapping(value = "/test/TicketMarkupUsed")
    public Object ticketMarkupUsed(@RequestBody HraRequest hraRequest) {
        try {
            log.debug("===TicketMarkupUsed请求参数为：" + JsonUtil.objectToJson(hraRequest));
            String aesContent = AESUtil.AESEncode(JsonUtil.objectToJson(hraRequest));
            return hraAesService.ticketMarkupUsed(aesContent);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HraResult.build("501", "接口服务器工作不正常。");
        }
    }

    /**
     * 接口编号：Interface_007
     * 接口名称：评估报告上传
     * 接口功能说明：评估报告上传
     * 涉及功能编号：func_008、func_009
     * URL地址：http://hraapi.yimaokeji.com:10090/ReportUpload
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "评估报告上传")
    @PostMapping(value = "/test/ReportUpload")
    public Object reportUpload(HttpServletRequest request, @RequestParam String deviceId, @RequestParam String ticketNo, @RequestParam String detail) {
        try {
            log.debug("===ReportUpload请求参数为：deviceId=" + deviceId + "---ticketNo=" + ticketNo);
            HraRequest hraRequest = new HraRequest();
            hraRequest.setDeviceId(deviceId);
            hraRequest.setTicketNo(ticketNo);
            hraRequest.setDetail(detail);
            String aesContent = AESUtil.AESEncode(JsonUtil.objectToJson(hraRequest));

            Map<String, Part> partMap = new HashMap<>();
            partMap.put("pdf", request.getPart("pdf"));
            partMap.put("pic_erbihout", request.getPart("pic_erbihout"));
            partMap.put("pic_guge", request.getPart("pic_guge"));
            partMap.put("pic_huxi", request.getPart("pic_huxi"));
            partMap.put("pic_miniaoshengzhi", request.getPart("pic_miniaoshengzhi"));
            partMap.put("pic_shenjing", request.getPart("pic_shenjing"));
            partMap.put("pic_xiaohua", request.getPart("pic_xiaohua"));
            partMap.put("pic_xinxueguan", request.getPart("pic_xinxueguan"));

            return hraAesService.reportUpload(aesContent, partMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HraResult.build("501", "接口服务器工作不正常。");
        }
    }

}
