package com.yimao.cloud.hra.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.RemoteCallException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.hra.service.HealthyLoginService;
import com.yimao.cloud.hra.service.HraBodyElementService;
import com.yimao.cloud.hra.service.HraReportOtherService;
import com.yimao.cloud.hra.service.HraReportService;
import com.yimao.cloud.pojo.dto.hra.HraBodyElementDTO;
import com.yimao.cloud.pojo.dto.hra.ReportDTO;
import com.yimao.cloud.pojo.dto.hra.ReportDetailDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 评估报告 控制类
 * @author: yu chunlei
 * @create: 2018-05-02 16:44:40
 **/
@RestController
@Slf4j
@Api(tags = "EvaluateReportController")
public class EvaluateReportController {

    @Resource
    private HealthyLoginService healthyLoginService;
    @Resource
    private HraBodyElementService hraBodyElementService;
    @Resource
    private HraReportService hraReportService;
    @Resource
    private HraReportOtherService hraReportOtherService;

    /**
     * 小程序登录
     *
     * @param code
     * @param encryptedData
     * @param iv
     * @return
     */
    @GetMapping(value = {"/miniprogram/evaluate/miniLogin"})
    @ApiOperation(value = "健康自测小程序登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "登录凭证", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "encryptedData", value = "加密数据", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "iv", value = "向量", required = true, dataType = "String", paramType = "query")
    })
    public Object miniLogin(@RequestParam(name = "code") String code,
                            @RequestParam(name = "encryptedData") String encryptedData,
                            @RequestParam(name = "iv") String iv,
                            @RequestParam(value = "sharerId", required = false) Integer sharerId) {
        log.debug("=====进入小程序登录方法,miniLogin()=====");
        if (StringUtil.isEmpty(code)) {
            throw new BadRequestException("登录凭证不能为空 ！");
        }

        if (StringUtil.isEmpty(encryptedData)) {
            throw new BadRequestException("传入加密串不能为空 ！");
        }

        if (StringUtil.isEmpty(iv)) {
            throw new BadRequestException("传入向量iv不能为空 ！");
        }
        log.debug("传入参数:code= " + code + ",encryptedData=" + encryptedData + ",iv=" + iv);
        UserDTO userDTO = healthyLoginService.getUserInfoByCode(code, encryptedData, iv, sharerId);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        }
        throw new NotFoundException("获取用户数据信息为空！");
    }


    /**
     * 我的评估报告或他人的评估报告
     *
     * @return
     */
    @GetMapping(value = {"/report"})
    @ApiOperation(value = "查询我的评估报告或他人的评估报告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "标识", required = true, dataType = "Long", paramType = "query")
    })
    public Object getListReport(@RequestParam(name = "uid") Integer uid, @RequestParam(name = "flag") Integer flag) {
        log.info("=====进入获取我的报告方法getMyReport()=====用户id：" + uid);
        List<ReportDTO> reportList = hraReportService.getMyReportList(uid, flag);
        if (CollectionUtil.isNotEmpty(reportList)) {
            return ResponseEntity.ok(reportList);
        }
        throw new NotFoundException("我的评估报告为空！");
    }


    /**
     * 体检报告详情
     *
     * @param ticketNo 体检卡号
     * @return
     */
    @GetMapping(value = {"/miniprogram/evaluate/{ticketNo}"})
    @ApiOperation(value = "查询体检报告详情")
    @ApiImplicitParam(name = "ticketNo", value = "体检卡号", required = true, dataType = "String", paramType = "path")
    public Object getReportDeatil(@PathVariable(name = "ticketNo") String ticketNo) {
        log.info("=====进入获取体检报告详情getReportDeatil()方法,体检卡号为" + ticketNo + "======");
        ReportDetailDTO detailDto = hraReportService.getReportDetail(ticketNo);
        if (detailDto != null) {
            return ResponseEntity.ok(detailDto);
        }
        log.debug("#####获取体检报告详情为空!#####");
        throw new NotFoundException("获取体检报告详情为空");
    }


    /**
     * 添加体检报告
     *
     * @param userId
     * @param ticketNo
     * @param mobile
     * @return
     */
    @PostMapping(value = {"/miniprogram/evaluate"})
    @ApiOperation(value = "添加体检报告", notes = "添加体检报告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ticketNo", value = "体检卡号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "query")
    })
    public Object addOtherReport(@RequestParam(name = "userId") Integer userId,
                                 @RequestParam(name = "ticketNo") String ticketNo,
                                 @RequestParam(name = "mobile") String mobile) {
        log.info("==============添加体检报告=================");
        if (userId == null) {
            throw new BadRequestException("userId为空!");
        }
        if (StringUtil.isEmpty(ticketNo)) {
            throw new BadRequestException("ticketNo为空!");
        }
        if (StringUtil.isEmpty(mobile)) {
            throw new BadRequestException("mobile为空!");
        }
        return ResponseEntity.ok(hraReportOtherService.addOtherReport(userId, ticketNo, mobile));
    }


    /**
     * 删除他人体检报告
     *
     * @param ticketId
     * @return
     */
    @DeleteMapping(value = {"/miniprogram/evaluate/{ticketId}"})
    @ApiOperation(value = "删除他人体检报告")
    @ApiImplicitParam(name = "ticketId", value = "卡号ID", required = true, dataType = "Long", paramType = "path")
    public void deleteReport(@PathVariable Integer ticketId) {
        Integer i = hraReportOtherService.deleteReport(ticketId);
        if (i != null && i == -1) {
            throw new RemoteCallException();
        }
        log.debug("*****删除他人报告操作记录数,i=" + i + "*******");
    }


    /**
     * 获取当前用户下所有的评估报告
     *
     * @param userId
     * @return
     */
    @GetMapping(value = {"/evaluate/{userId}"})
    @ApiOperation(value = "获取当前用户下所有的评估报告")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    public Object getAllReport(@PathVariable(name = "userId") Integer userId) {
        log.debug("==============getAllReport()=================");
        List<ReportDTO> reportDTOS = hraReportService.getAllReport(userId);
        if (CollectionUtil.isNotEmpty(reportDTOS)) {
            return ResponseEntity.ok(reportDTOS);
        }
        throw new NotFoundException("当前用户下所有的评估报告为空!");
    }


    /**
     * 对比评估报告
     *
     * @param ticketNos
     * @return
     */
    @GetMapping(value = {"/miniprogram/evaluate/getReportContrast"})
    @ApiOperation(value = "对比评估报告")
    @ApiImplicitParam(name = "ticketNos", value = "评估号集合", required = true, paramType = "query", allowMultiple = true, dataType = "String")
    public Object getReportContrast(@RequestParam String[] ticketNos) {
        log.info("=====进入报告对比方法getReportContrast()=======");
        if (ticketNos == null || ticketNos.length == 0) {
            throw new BadRequestException("传入参数ticketNos为空!");
        }
        List<ReportDetailDTO> detailDtoList = hraReportService.getReportDetailList(ticketNos);
        if (CollectionUtil.isNotEmpty(detailDtoList)) {
            return ResponseEntity.ok(detailDtoList);
        }
        throw new NotFoundException("获取评估报告详情列表为空!");
    }


    /**
     * 分享评估报告给好友
     *
     * @param fromUserId
     * @param ticketNo
     * @return
     */
    @PostMapping(value = {"/miniprogram/evaluate/shareReport"})
    @ApiOperation(value = "分享评估报告给好友", notes = "分享评估报告给好友")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromUserId", value = "分享人ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ticketNo", value = "评估卡号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sharedUserId", value = "被分享人ID", required = true, dataType = "Long", paramType = "query")
    })
    public Object shareReport(@RequestParam(name = "fromUserId") Integer fromUserId,
                              @RequestParam(name = "ticketNo") String ticketNo,
                              @RequestParam(name = "sharedUserId") Integer sharedUserId) {
        if (fromUserId == null) {
            throw new BadRequestException("传入参数fromUserId为空");
        }

        if (StringUtil.isEmpty(ticketNo)) {
            throw new BadRequestException("传入参数ticketNo为空");
        }

        if (sharedUserId == null) {
            throw new BadRequestException("传入参数sharedUserId为空!");
        }

        log.debug("*****用户ID=" + fromUserId + ",评估卡号=" + ticketNo + ",sharedUserId = " + sharedUserId + "******");
        Object reportDtoList = hraReportService.shareReport(fromUserId, ticketNo, sharedUserId);
        if (reportDtoList != null) {
            return ResponseEntity.ok(reportDtoList);
        }
        throw new NotFoundException("他人评估报告列表为空!");
    }


    /**
     * 获取身体元素信息
     *
     * @return
     */
    @GetMapping(value = {"/miniprogram/evaluate"})
    @ApiOperation(value = "获取身体元素信息")
    public Object getBodyElements() {
        List<HraBodyElementDTO> elementList = hraBodyElementService.getBodyElements();
        return ResponseEntity.ok(elementList);
    }


}
