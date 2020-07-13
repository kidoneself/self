package com.yimao.cloud.hra.controller;

import com.yimao.cloud.base.utils.AESUtil;
import com.yimao.cloud.base.utils.JsonUtil;
import com.yimao.cloud.hra.msg.HraResult;
import com.yimao.cloud.hra.service.HraAesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@Api(tags = "HraAesController")
public class HraAesController {

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
    @ApiOperation(value = "新增或修改翼猫服务站信息", notes = "新增或修改翼猫服务站信息")
    @RequestMapping(value = {"/ServiceStation"}, method = {RequestMethod.POST})
    public Object serviceStation(HttpServletRequest request) {
        try {
            log.debug("=================ServiceStation走进来了=========================");

            String aesContent = request.getParameter("K");
            HraResult result = hraAesService.serviceStation(aesContent);
            log.debug(JsonUtil.objectToJson(result));
            return AESUtil.AESEncode(JsonUtil.objectToJson(result));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //当服务器出现异常时，“code”:“501”， “msg”:“接口服务器工作不正常。”，“data”:null。
            return AESUtil.AESEncode(JsonUtil.objectToJson(HraResult.build("501", "接口服务器工作不正常。")));
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
    @ApiOperation(value = "翼猫HRA服务器测试接口", notes = "翼猫HRA服务器测试接口")
    @RequestMapping(value = {"/DeviceTest"}, method = {RequestMethod.POST})
    public Object deviceTest(HttpServletRequest request) {
        try {

            log.debug("=================DeviceTest走进来了=========================");

            String aesContent = request.getParameter("K");
            HraResult result = hraAesService.deviceTest(aesContent);
            log.debug(JsonUtil.objectToJson(result));
            return AESUtil.AESEncode(JsonUtil.objectToJson(result));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //当服务器出现异常时，“code”:“501”， “msg”:“接口服务器工作不正常。”，“data”:null。
            return AESUtil.AESEncode(JsonUtil.objectToJson(HraResult.build("501", "接口服务器工作不正常。")));
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
    @ApiOperation(value = "通过评估卡号获取预约用户信息", notes = "通过评估卡号获取预约用户信息")
    @RequestMapping(value = {"/GetCustomerByTicketNo"}, method = {RequestMethod.POST})
    public Object getCustomerByTicketNo(HttpServletRequest request) {
        try {

            System.out.println("=================GetCustomerByTicketNo走进来了=========================");

            String aesContent = request.getParameter("K");
            HraResult result = hraAesService.getCustomerByTicketNo(aesContent);
            System.out.println(JsonUtil.objectToJson(result));
            return AESUtil.AESEncode(JsonUtil.objectToJson(result));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //当服务器出现异常时，“code”:“501”， “msg”:“接口服务器工作不正常。”，“data”:null。
            return AESUtil.AESEncode(JsonUtil.objectToJson(HraResult.build("501", "接口服务器工作不正常。")));
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
    @ApiOperation(value = "验证评估卡是否被使用", notes = "验证评估卡是否被使用")
    @RequestMapping(value = {"/TicketValidate"}, method = {RequestMethod.POST})
    public Object ticketValidate(HttpServletRequest request) {
        try {

            log.debug("=================TicketValidate走进来了=========================");

            String aesContent = request.getParameter("K");
            HraResult result = hraAesService.ticketValidate(aesContent);
            log.debug(JsonUtil.objectToJson(result));
            return AESUtil.AESEncode(JsonUtil.objectToJson(result));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //当服务器出现异常时，“code”:“501”， “msg”:“接口服务器工作不正常。”，“data”:null。
            return AESUtil.AESEncode(JsonUtil.objectToJson(HraResult.build("501", "接口服务器工作不正常。")));
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
    @ApiOperation(value = "评估卡号校验并绑定用户", notes = "评估卡号校验并绑定用户")
    @RequestMapping(value = {"/TicketBindCustomer"}, method = {RequestMethod.POST})
    public Object ticketBindCustomer(HttpServletRequest request) {
        try {

            log.debug("=================TicketBindCustomer走进来了=========================");

            String aesContent = request.getParameter("K");
            HraResult result = hraAesService.ticketBindCustomer(aesContent);
            log.debug(JsonUtil.objectToJson(result));
            return AESUtil.AESEncode(JsonUtil.objectToJson(result));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //当服务器出现异常时，“code”:“501”， “msg”:“接口服务器工作不正常。”，“data”:null。
            return AESUtil.AESEncode(JsonUtil.objectToJson(HraResult.build("501", "接口服务器工作不正常。")));
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
    @ApiOperation(value = "评估卡变更为已使用状态", notes = "评估卡变更为已使用状态")
    @RequestMapping(value = {"/TicketMarkupUsed"}, method = {RequestMethod.POST})
    public Object ticketMarkupUsed(HttpServletRequest request) {
        try {

            log.debug("=================TicketMarkupUsed走进来了=========================");

            String aesContent = request.getParameter("K");
            HraResult result = hraAesService.ticketMarkupUsed(aesContent);
            log.debug(JsonUtil.objectToJson(result));
            return AESUtil.AESEncode(JsonUtil.objectToJson(result));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //当服务器出现异常时，“code”:“501”， “msg”:“接口服务器工作不正常。”，“data”:null。
            return AESUtil.AESEncode(JsonUtil.objectToJson(HraResult.build("501", "接口服务器工作不正常。")));
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
    @ApiOperation(value = "评估报告上传", notes = "评估报告上传")
    @RequestMapping(value = {"/ReportUpload"}, method = {RequestMethod.POST})
    public Object reportUpload(HttpServletRequest request) {
        try {

            log.debug("=================ReportUpload走进来了=========================");

            String aesContent = request.getParameter("K");

            Map<String, Part> partMap = new HashMap<>();
            partMap.put("pdf", request.getPart("pdf"));
            partMap.put("pic_erbihout", request.getPart("pic_erbihout"));
            partMap.put("pic_guge", request.getPart("pic_guge"));
            partMap.put("pic_huxi", request.getPart("pic_huxi"));
            partMap.put("pic_miniaoshengzhi", request.getPart("pic_miniaoshengzhi"));
            partMap.put("pic_shenjing", request.getPart("pic_shenjing"));
            partMap.put("pic_xiaohua", request.getPart("pic_xiaohua"));
            partMap.put("pic_xinxueguan", request.getPart("pic_xinxueguan"));

            HraResult result = hraAesService.reportUpload(aesContent, partMap);
            log.debug(JsonUtil.objectToJson(result));
            return AESUtil.AESEncode(JsonUtil.objectToJson(result));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //当服务器出现异常时，“code”:“501”， “msg”:“接口服务器工作不正常。”，“data”:null。
            return AESUtil.AESEncode(JsonUtil.objectToJson(HraResult.build("501", "接口服务器工作不正常。")));
        }
    }

}
